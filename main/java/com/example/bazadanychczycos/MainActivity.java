package com.example.bazadanychczycos;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.google.android.material.textfield.TextInputEditText;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.inputmethod.EditorInfo;
import android.view.KeyEvent;

public class MainActivity extends AppCompatActivity {

    private Spinner spinnerMouse;
    private Spinner spinnerKeyboard;
    private Spinner spinnerMonitor;
    private Spinner spinnerComputer;
    private DataBase.DatabaseHelper dbHelper;
    private Toolbar toolbar;
    private SeekBar quantitySeekBar;
    private Button decreaseButton;
    private Button increaseButton;
    private CheckBox checkBoxMouse;
    private CheckBox checkBoxKeyboard;
    private CheckBox checkBoxMonitor;
    private SeekBar quantitySeekBarMouse;
    private SeekBar quantitySeekBarKeyboard;
    private SeekBar quantitySeekBarMonitor;
    private Button decreaseButtonMouse;
    private Button increaseButtonMouse;
    private Button decreaseButtonKeyboard;
    private Button increaseButtonKeyboard;
    private Button decreaseButtonMonitor;
    private Button increaseButtonMonitor;
    private TextView quantityTextMouse;
    private TextView quantityTextKeyboard;
    private TextView quantityTextMonitor;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Konfiguracja toolbara
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Sklep komputerowy");
        }

        // Inicjalizacja bazy danych
        dbHelper = new DataBase.DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.onCreate(db);

        // Inicjalizacja spinnerów
        spinnerMouse = findViewById(R.id.spinner_mouse);
        spinnerKeyboard = findViewById(R.id.spinner_keyboard);
        spinnerMonitor = findViewById(R.id.spinner_monitor);
        spinnerComputer = findViewById(R.id.spinner_computer);

        // Konfiguracja spinnerów i ich listenerów
        initializeSpinners();
        setupSpinnerListeners();

        // Konfiguracja przycisku zamówienia
        Button orderButton = findViewById(R.id.button_order);
        orderButton.setOnClickListener(v -> handleOrder());

        // Inicjalizacja kontrolek ilości
        setupQuantityControls();

        // Inicjalizacja CheckBoxów
        checkBoxMouse = findViewById(R.id.checkbox_mouse);
        checkBoxKeyboard = findViewById(R.id.checkbox_keyboard);
        checkBoxMonitor = findViewById(R.id.checkbox_monitor);

        checkBoxMouse.setOnCheckedChangeListener((buttonView, isChecked) -> updateTotalPrice());
        checkBoxKeyboard.setOnCheckedChangeListener((buttonView, isChecked) -> updateTotalPrice());
        checkBoxMonitor.setOnCheckedChangeListener((buttonView, isChecked) -> updateTotalPrice());

        // Inicjalizacja kontrolek ilości dla każdego produktu
        quantitySeekBarMouse = findViewById(R.id.seekbar_quantity_mouse);
        quantitySeekBarKeyboard = findViewById(R.id.seekbar_quantity_keyboard);
        quantitySeekBarMonitor = findViewById(R.id.seekbar_quantity_monitor);
        
        decreaseButtonMouse = findViewById(R.id.button_decrease_mouse);
        increaseButtonMouse = findViewById(R.id.button_increase_mouse);
        decreaseButtonKeyboard = findViewById(R.id.button_decrease_keyboard);
        increaseButtonKeyboard = findViewById(R.id.button_increase_keyboard);
        decreaseButtonMonitor = findViewById(R.id.button_decrease_monitor);
        increaseButtonMonitor = findViewById(R.id.button_increase_monitor);
        
        quantityTextMouse = findViewById(R.id.text_quantity_mouse);
        quantityTextKeyboard = findViewById(R.id.text_quantity_keyboard);
        quantityTextMonitor = findViewById(R.id.text_quantity_monitor);

        setupProductQuantityControls(quantitySeekBarMouse, decreaseButtonMouse, 
            increaseButtonMouse, quantityTextMouse, "mouse");
        setupProductQuantityControls(quantitySeekBarKeyboard, decreaseButtonKeyboard, 
            increaseButtonKeyboard, quantityTextKeyboard, "keyboard");
        setupProductQuantityControls(quantitySeekBarMonitor, decreaseButtonMonitor, 
            increaseButtonMonitor, quantityTextMonitor, "monitor");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    private void initializeSpinners() {
        setupSpinner(spinnerComputer, "komputery");
        setupSpinner(spinnerMouse, "myszki");
        setupSpinner(spinnerKeyboard, "klawiatury");
        setupSpinner(spinnerMonitor, "monitory");
    }

    private void setupSpinner(Spinner spinner, String category) {
        List<Item> items = dbHelper.getItemsByCategory(category);
        if (items.isEmpty()) {
            // Jeśli lista jest pusta, wyświetl komunikat
            System.out.println("Brak produktów w kategorii: " + category);
        }
        ArrayAdapter<Item> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void setupSpinnerListeners() {
        spinnerMouse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Item selectedItem = (Item) parent.getItemAtPosition(position);
                updateItemDetails(selectedItem, R.id.image_mouse, R.id.text_mouse_name, 
                    R.id.text_mouse_description, R.id.text_mouse_price);
                quantitySeekBarMouse.setProgress(0);
                quantityTextMouse.setText("0 szt.");
                updateTotalPrice();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerKeyboard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Item selectedItem = (Item) parent.getItemAtPosition(position);
                updateItemDetails(selectedItem, R.id.image_keyboard, R.id.text_keyboard_name, 
                    R.id.text_keyboard_description, R.id.text_keyboard_price);
                quantitySeekBarKeyboard.setProgress(0);
                quantityTextKeyboard.setText("0 szt.");
                updateTotalPrice();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerMonitor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Item selectedItem = (Item) parent.getItemAtPosition(position);
                updateItemDetails(selectedItem, R.id.image_monitor, R.id.text_monitor_name, 
                    R.id.text_monitor_description, R.id.text_monitor_price);
                quantitySeekBarMonitor.setProgress(0);
                quantityTextMonitor.setText("0 szt.");
                updateTotalPrice();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerComputer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Item selectedItem = (Item) parent.getItemAtPosition(position);
                updateItemDetails(selectedItem, R.id.image_computer, R.id.text_computer_name, 
                    R.id.text_computer_description, R.id.text_computer_price);
                
                // Zmiana wartości początkowej z 1 na 0
                quantitySeekBar.setProgress(0);
                TextView quantityText = findViewById(R.id.text_quantity);
                quantityText.setText("0 szt.");
                
                updateTotalPrice();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void updateItemDetails(Item item, int imageViewId, int nameViewId, int descriptionViewId, int priceViewId) {
        if (item == null) {
            System.out.println("Item jest null!");
            return;
        }
        
        try {
            ImageView imageView = findViewById(imageViewId);
            TextView nameView = findViewById(nameViewId);
            TextView descriptionView = findViewById(descriptionViewId);
            TextView priceView = findViewById(priceViewId);

            System.out.println("Aktualizacja szczegółów produktu:");
            System.out.println("Nazwa: " + item.getName());
            System.out.println("Opis: " + item.getDescription());
            System.out.println("Cena: " + item.getPrice());

            nameView.setText(item.getName());
            descriptionView.setText(item.getDescription());
            priceView.setText(String.format("Cena: %.2f zł", item.getPrice()));

            // Ustawianie obrazka
            String imageName = item.getPicture().replace(".png", "");
            int imageResource = getResources().getIdentifier(imageName, "drawable", getPackageName());
            
            if (imageResource != 0) {
                imageView.setImageResource(imageResource);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Menu opcji
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.item1) {
            zapiszKoszyk(); // Zapisz koszyk
            return true;
        } else if (id == R.id.item2) {
            wczytajKoszyk(); // Wczytaj koszyk
            return true;
        } else if (id == R.id.item3) {
            wyslijSMS();
            return true;
        } else if (id == R.id.item4) {
            pokazListe();
            return true;
        } else if (id == R.id.item5) {
            udostepnij();
            return true;
        } else if (id == R.id.item6) {
            oProgramie();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void stronaGlowna() {
        // Logika głównej strony
    }

    private void wyslijSMS() {
        Intent intent = new Intent(this, SendSmsActivity.class);
        startActivity(intent);
    }

    private void pokazListe() {
        Intent intent = new Intent(this, OrderListActivity.class);
        startActivity(intent);
    }

    private void udostepnij() {
        showOrderSelectionDialog();
    }

    private void showOrderSelectionDialog() {
        List<OrderItem> orders = dbHelper.getAllOrdersWithIds();
        if (orders.isEmpty()) {
            Toast.makeText(this, "Brak zamówień do udostępnienia", Toast.LENGTH_SHORT).show();
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
        builder.setTitle("Wybierz zamówienie do udostępnienia")
               .setItems(orderItems, (dialog, which) -> {
                   OrderItem selectedOrder = orders.get(which);
                   shareOrder(selectedOrder);
               })
               .setNegativeButton("Anuluj", null)
               .show();
    }

    private void shareOrder(OrderItem order) {
        String subject = "Zamówienie ze sklepu komputerowego";
        String message = String.format(
            "Szczegóły zamówienia:\n\n" +
            "Data: %s\n" +
            "Zamawiający: %s\n\n" +
            "Zamówione produkty:\n%s\n" +
            "Suma zamówienia: %.2f zł",
            order.getDate(),
            order.getCustomerName(),
            order.getDetails(),
            order.getTotalPrice()
        );

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);

        try {
            startActivity(Intent.createChooser(emailIntent, "Wyślij email..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, 
                "Brak zainstalowanej aplikacji do wysyłania maili.", 
                Toast.LENGTH_SHORT).show();
        }
    }

    private void oProgramie() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Informacje o autorze")
                .setMessage("Aplikacja stworzona przez \nKonrad Szczepaniak").show();
    }

    private List<Item> getItemsByCategory(String category) {
        List<Item> allItems = dbHelper.getAllItems();
        List<Item> filteredItems = new ArrayList<>();
        
        for (Item item : allItems) {
            if (item.getCategory() != null && item.getCategory().equals(category)) {
                filteredItems.add(item);
            }
        }
        
        return filteredItems;
    }

    private void handleOrder() {
        EditText customerNameInput = findViewById(R.id.edit_text_notes);
        String customerName = customerNameInput.getText().toString();

        if (customerName.trim().isEmpty()) {
            customerNameInput.setError("Proszę podać imię i nazwisko");
            return;
        }

        if (quantitySeekBar.getProgress() == 0 && 
            (!checkBoxMouse.isChecked() || quantitySeekBarMouse.getProgress() == 0) &&
            (!checkBoxKeyboard.isChecked() || quantitySeekBarKeyboard.getProgress() == 0) &&
            (!checkBoxMonitor.isChecked() || quantitySeekBarMonitor.getProgress() == 0)) {
            
            Toast.makeText(this, "Proszę wybrać przynajmniej jeden produkt", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder orderDetails = new StringBuilder();
        float totalPrice = 0;

        Item selectedComputer = (Item) spinnerComputer.getSelectedItem();
        int computerQuantity = quantitySeekBar.getProgress();
        float computerPrice = selectedComputer.getPrice() * computerQuantity;
        totalPrice += computerPrice;
        
        orderDetails.append(String.format("Komputer: %s x%d (%.2f zł)\n", 
            selectedComputer.getName(), computerQuantity, computerPrice));

        if (checkBoxMouse.isChecked()) {
            Item selectedMouse = (Item) spinnerMouse.getSelectedItem();
            int mouseQuantity = quantitySeekBarMouse.getProgress();
            float mousePrice = selectedMouse.getPrice() * mouseQuantity;
            totalPrice += mousePrice;
            orderDetails.append(String.format("Myszka: %s x%d (%.2f zł)\n", 
                selectedMouse.getName(), mouseQuantity, mousePrice));
        }

        if (checkBoxKeyboard.isChecked()) {
            Item selectedKeyboard = (Item) spinnerKeyboard.getSelectedItem();
            int keyboardQuantity = quantitySeekBarKeyboard.getProgress();
            float keyboardPrice = selectedKeyboard.getPrice() * keyboardQuantity;
            totalPrice += keyboardPrice;
            orderDetails.append(String.format("Klawiatura: %s x%d (%.2f zł)\n", 
                selectedKeyboard.getName(), keyboardQuantity, keyboardPrice));
        }

        if (checkBoxMonitor.isChecked()) {
            Item selectedMonitor = (Item) spinnerMonitor.getSelectedItem();
            int monitorQuantity = quantitySeekBarMonitor.getProgress();
            float monitorPrice = selectedMonitor.getPrice() * monitorQuantity;
            totalPrice += monitorPrice;
            orderDetails.append(String.format("Monitor: %s x%d (%.2f zł)\n", 
                selectedMonitor.getName(), monitorQuantity, monitorPrice));
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        float finalTotalPrice = totalPrice;
        builder.setTitle("Potwierdzenie zamówienia")
                .setMessage("Zamawiający: " + customerName + "\n\n" + 
                    orderDetails.toString() + "\nSuma: " + String.format("%.2f zł", totalPrice))
                .setPositiveButton("Potwierdź", (dialog, which) -> {
                    long orderId = dbHelper.saveOrder(customerName, orderDetails.toString(), finalTotalPrice);
                    if (orderId != -1) {
                        Toast.makeText(this, "Zamówienie zostało zapisane", Toast.LENGTH_SHORT).show();
                        resetForm();
                    } else {
                        Toast.makeText(this, "Błąd podczas zapisywania zamówienia", 
                            Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Anuluj", null)
                .show();
    }

    private void resetForm() {
        quantitySeekBar.setProgress(0);

        checkBoxMouse.setChecked(false);
        checkBoxKeyboard.setChecked(false);
        checkBoxMonitor.setChecked(false);
        
        quantitySeekBarMouse.setProgress(0);
        quantitySeekBarKeyboard.setProgress(0);
        quantitySeekBarMonitor.setProgress(0);

        EditText customerNameInput = findViewById(R.id.edit_text_notes);
        customerNameInput.setText("");

        updateTotalPrice();
    }

    private void setupQuantityControls() {
        quantitySeekBar = findViewById(R.id.seekbar_quantity);
        decreaseButton = findViewById(R.id.button_decrease);
        increaseButton = findViewById(R.id.button_increase);
        TextView quantityText = findViewById(R.id.text_quantity);

        quantitySeekBar.setProgress(0);
        quantityText.setText("0 szt.");

        decreaseButton.setOnClickListener(v -> {
            int currentProgress = quantitySeekBar.getProgress();
            if (currentProgress > 0) {
                quantitySeekBar.setProgress(currentProgress - 1);
                quantityText.setText(currentProgress - 1 + " szt.");
                updateTotalPrice();
            }
        });

        increaseButton.setOnClickListener(v -> {
            int currentProgress = quantitySeekBar.getProgress();
            if (currentProgress < quantitySeekBar.getMax()) {
                quantitySeekBar.setProgress(currentProgress + 1);
                quantityText.setText(currentProgress + 1 + " szt.");
                updateTotalPrice();
            }
        });

        quantitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                quantityText.setText(progress + " szt.");
                updateTotalPrice();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void updateTotalPrice() {
        float basePrice = 0;
        float totalPrice = 0;
        int mainQuantity = quantitySeekBar.getProgress();
        
        // Cena komputera (cena podstawowa)
        Item selectedComputer = (Item) spinnerComputer.getSelectedItem();
        if (selectedComputer != null) {
            basePrice = selectedComputer.getPrice() * mainQuantity;
            totalPrice += basePrice;
            
            // Aktualizacja wyświetlanej ceny podstawowej
            TextView basePriceView = findViewById(R.id.text_base_price);
            basePriceView.setText(String.format("Cena podstawowa: %.2f zł", basePrice));
        }

        if (checkBoxMouse.isChecked()) {
            Item selectedMouse = (Item) spinnerMouse.getSelectedItem();
            if (selectedMouse != null) {
                int mouseQuantity = quantitySeekBarMouse.getProgress();
                float mousePrice = selectedMouse.getPrice() * mouseQuantity;
                totalPrice += mousePrice;
                TextView mouseAddonPrice = findViewById(R.id.text_mouse_addon_price);
                mouseAddonPrice.setText(String.format("+ %.2f zł", mousePrice));
            }
        } else {
            TextView mouseAddonPrice = findViewById(R.id.text_mouse_addon_price);
            mouseAddonPrice.setText("+ 0.00 zł");
        }

        if (checkBoxKeyboard.isChecked()) {
            Item selectedKeyboard = (Item) spinnerKeyboard.getSelectedItem();
            if (selectedKeyboard != null) {
                int keyboardQuantity = quantitySeekBarKeyboard.getProgress();
                float keyboardPrice = selectedKeyboard.getPrice() * keyboardQuantity;
                totalPrice += keyboardPrice;
                TextView keyboardAddonPrice = findViewById(R.id.text_keyboard_addon_price);
                keyboardAddonPrice.setText(String.format("+ %.2f zł", keyboardPrice));
            }
        } else {
            TextView keyboardAddonPrice = findViewById(R.id.text_keyboard_addon_price);
            keyboardAddonPrice.setText("+ 0.00 zł");
        }

        if (checkBoxMonitor.isChecked()) {
            Item selectedMonitor = (Item) spinnerMonitor.getSelectedItem();
            if (selectedMonitor != null) {
                int monitorQuantity = quantitySeekBarMonitor.getProgress();
                float monitorPrice = selectedMonitor.getPrice() * monitorQuantity;
                totalPrice += monitorPrice;
                TextView monitorAddonPrice = findViewById(R.id.text_monitor_addon_price);
                monitorAddonPrice.setText(String.format("+ %.2f zł", monitorPrice));
            }
        } else {
            TextView monitorAddonPrice = findViewById(R.id.text_monitor_addon_price);
            monitorAddonPrice.setText("+ 0.00 zł");
        }

        TextView totalPriceView = findViewById(R.id.text_total_price);
        totalPriceView.setText(String.format("Suma zamówienia: %.2f zł", totalPrice));
    }

    private void setupProductQuantityControls(SeekBar seekBar, Button decreaseButton, 
        Button increaseButton, TextView quantityText, String productType) {
        
        seekBar.setProgress(0);
        quantityText.setText("0 szt.");

        decreaseButton.setOnClickListener(v -> {
            int currentProgress = seekBar.getProgress();
            if (currentProgress > 0) {
                seekBar.setProgress(currentProgress - 1);
                quantityText.setText(currentProgress - 1 + " szt.");
                updateTotalPrice();
            }
        });

        increaseButton.setOnClickListener(v -> {
            int currentProgress = seekBar.getProgress();
            if (currentProgress < seekBar.getMax()) {
                seekBar.setProgress(currentProgress + 1);
                quantityText.setText(currentProgress + 1 + " szt.");
                updateTotalPrice();
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                quantityText.setText(progress + " szt.");
                updateTotalPrice();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void zapiszKoszyk() {
        SharedPreferences sharedPreferences = getSharedPreferences("Koszyk", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("monitorQuantity", quantitySeekBarMonitor.getProgress());
        editor.putInt("mouseQuantity", quantitySeekBarMouse.getProgress());
        editor.putInt("keyboardQuantity", quantitySeekBarKeyboard.getProgress());
        editor.putInt("computerQuantity", quantitySeekBar.getProgress());

        EditText customerNameInput = findViewById(R.id.edit_text_notes);
        String customerName = customerNameInput.getText().toString();
        editor.putString("customerName", customerName);
        editor.apply();
        
        Toast.makeText(this, "Koszyk został zapisany", Toast.LENGTH_SHORT).show();
    }

    private void wczytajKoszyk() {
        SharedPreferences sharedPreferences = getSharedPreferences("Koszyk", MODE_PRIVATE);

        int monitorQuantity = sharedPreferences.getInt("monitorQuantity", 0);
        int mouseQuantity = sharedPreferences.getInt("mouseQuantity", 0);
        int keyboardQuantity = sharedPreferences.getInt("keyboardQuantity", 0);
        int computerQuantity = sharedPreferences.getInt("computerQuantity", 0);
        
        quantitySeekBarMonitor.setProgress(monitorQuantity);
        quantitySeekBarMouse.setProgress(mouseQuantity);
        quantitySeekBarKeyboard.setProgress(keyboardQuantity);
        quantitySeekBar.setProgress(computerQuantity);

        String customerName = sharedPreferences.getString("customerName", "");
        EditText customerNameInput = findViewById(R.id.edit_text_notes);
        customerNameInput.setText(customerName);
        
        updateTotalPrice();
        
        Toast.makeText(this, "Koszyk został wczytany", Toast.LENGTH_SHORT).show();
    }
}
