package com.example.bazadanychczycos;

import android.os.Bundle;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.util.List;

public class OrderListActivity extends AppCompatActivity {

    private DataBase.DatabaseHelper dbHelper;
    private ListView orderListView;
    private List<OrderItem> orders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        // Konfiguracja toolbara
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Lista zamówień");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Inicjalizacja bazy danych
        dbHelper = new DataBase.DatabaseHelper(this);
        
        // Inicjalizacja ListView
        orderListView = findViewById(R.id.order_list_view);
        
        // Pobierz i wyświetl zamówienia
        displayOrders();
    }

    private void displayOrders() {
        orders = dbHelper.getAllOrdersWithIds();
        if (!orders.isEmpty()) {
            OrderAdapter adapter = new OrderAdapter(this, orders, dbHelper);
            orderListView.setAdapter(adapter);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}