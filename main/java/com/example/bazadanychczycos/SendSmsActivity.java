package com.example.bazadanychczycos;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class SendSmsActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 100;
    private EditText phoneNumberInput;
    private DataBase.DatabaseHelper dbHelper;
    private OrderItem selectedOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms);

        // Konfiguracja toolbara
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Wyślij SMS");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        phoneNumberInput = findViewById(R.id.edit_text_phone);
        Button sendButton = findViewById(R.id.button_send_sms);
        dbHelper = new DataBase.DatabaseHelper(this);

        sendButton.setOnClickListener(v -> showOrderSelectionDialog());
    }

    private void showOrderSelectionDialog() {
        List<OrderItem> orders = dbHelper.getAllOrdersWithIds();
        if (orders.isEmpty()) {
            Toast.makeText(this, "Brak zamówień do wysłania", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] orderItems = new String[orders.size()];
        for (int i = 0; i < orders.size(); i++) {
            OrderItem order = orders.get(i);
            orderItems[i] = String.format("Zamówienie z %s - %s", 
                order.getDate(), 
                order.getCustomerName());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Wybierz zamówienie do wysłania SMS")
               .setItems(orderItems, (dialog, which) -> {
                   OrderItem selectedOrder = orders.get(which);
                   checkPermissionAndSendSMS(selectedOrder);
               })
               .setNegativeButton("Anuluj", null)
               .show();
    }

    private void checkPermissionAndSendSMS(OrderItem order) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            selectedOrder = order;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    SMS_PERMISSION_CODE);
        } else {
            sendSMS(order);
        }
    }

    private void sendSMS(OrderItem order) {
        String phoneNumber = phoneNumberInput.getText().toString().trim();
        if (phoneNumber.isEmpty()) {
            phoneNumberInput.setError("Wprowadź numer telefonu");
            return;
        }

        if (!isValidPhoneNumber(phoneNumber)) {
            phoneNumberInput.setError("Wprowadź poprawny 9-cyfrowy numer telefonu");
            return;
        }

        String message = String.format(
            "Zamówienie:\nZamawiający: %s\n%s\nSuma: %.2f zł",
            order.getCustomerName(),
            order.getDetails(),
            order.getTotalPrice()
        );

        try {
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> parts = smsManager.divideMessage(message);
            smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null);
            Toast.makeText(this, "SMS został wysłany", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(this, 
                "Błąd podczas wysyłania SMS: " + e.getMessage(), 
                Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (selectedOrder != null) {
                    sendSMS(selectedOrder);
                }
            } else {
                Toast.makeText(this, 
                    "Aplikacja potrzebuje uprawnień do wysyłania SMS aby kontynuować", 
                    Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("[0-9]{9}");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}