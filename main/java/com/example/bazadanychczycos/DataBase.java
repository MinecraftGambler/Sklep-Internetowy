package com.example.bazadanychczycos;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.bazadanychczycos.Item;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Locale;

public class DataBase extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);

        long newRowId = dbHelper.addItem("Example Item", 25, "Example description", "example_image.png");

        List<Item> items = dbHelper.getAllItems();
    }

    public static class DatabaseHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "itemsDatabase";
        private static final int DATABASE_VERSION = 1;

        private static final String TABLE_ITEMS = "items_table";
        private static final String COLUMN_ID = "id";
        private static final String COLUMN_NAME = "name";
        private static final String COLUMN_PRICE = "price";
        private static final String COLUMN_DESCRIPTION = "description";
        private static final String COLUMN_PICTURE = "picture";
        private static final String COLUMN_CATEGORY = "category";

        private static final String TABLE_ORDERS = "orders";
        private static final String COLUMN_ORDER_ID = "order_id";
        private static final String COLUMN_ORDER_DATE = "order_date";
        private static final String COLUMN_CUSTOMER_NAME = "customer_name";
        private static final String COLUMN_ORDER_DETAILS = "order_details";
        private static final String COLUMN_TOTAL_PRICE = "total_price";

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
                String CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLE_ITEMS + "("
                        + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + COLUMN_NAME + " TEXT,"
                        + COLUMN_PRICE + " REAL,"
                        + COLUMN_DESCRIPTION + " TEXT,"
                        + COLUMN_PICTURE + " TEXT,"
                        + COLUMN_CATEGORY + " TEXT" + ")";
                db.execSQL(CREATE_ITEMS_TABLE);

                addDefaultItems(db);

                // Dodaj tabelę zamówień
                String CREATE_ORDERS_TABLE = "CREATE TABLE " + TABLE_ORDERS + "("
                        + COLUMN_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + COLUMN_ORDER_DATE + " TEXT,"
                        + COLUMN_CUSTOMER_NAME + " TEXT,"
                        + COLUMN_ORDER_DETAILS + " TEXT,"
                        + COLUMN_TOTAL_PRICE + " REAL" + ")";
                db.execSQL(CREATE_ORDERS_TABLE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
            onCreate(db);
        }

        public long addItem(String name, float price, String description, String picture) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, name);
            values.put(COLUMN_PRICE, price);
            values.put(COLUMN_DESCRIPTION, description);
            values.put(COLUMN_PICTURE, picture);
            long id = db.insert(TABLE_ITEMS, null, values);
            db.close();
            return id;
        }

        public List<Item> getAllItems() {
            List<Item> itemList = new ArrayList<>();
            SQLiteDatabase db = this.getReadableDatabase();
            String selectQuery = "SELECT * FROM " + TABLE_ITEMS;
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                    @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                    @SuppressLint("Range") float price = cursor.getFloat(cursor.getColumnIndex(COLUMN_PRICE));
                    @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
                    @SuppressLint("Range") String picture = cursor.getString(cursor.getColumnIndex(COLUMN_PICTURE));

                    Item item = new Item(id, name, price, description, picture);
                    itemList.add(item);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
            return itemList;
        }

        // Generate a list of default items and add to the database
        private void addDefaultItems(SQLiteDatabase db) {
            // Klawiatury
            addItemDirectly(db, "Klawiatura Gamingowa", 199.99f, "Klawiatura mechaniczna RGB idealna dla graczy.", "klawiatura1.png", "klawiatury");
            addItemDirectly(db, "Klawiatura Biurowa", 89.99f, "Ergonomiczna klawiatura do biura.", "klawiatura2.png", "klawiatury");
            addItemDirectly(db, "Klawiatura Multimedialna", 129.99f, "Komfortowa klawiatura z dodatkowymi przyciskami multimedialnymi.", "klawiatura3.png", "klawiatury");
            addItemDirectly(db, "Klawiatura Bezprzewodowa", 159.99f, "Lekka klawiatura bezprzewodowa z baterią na 12 miesięcy.", "klawiatura4.png", "klawiatury");

            // Komputery
            addItemDirectly(db, "Komputer Gamingowy", 4999.99f, "Potężny komputer stacjonarny z RTX 3070.", "komputer1.png", "komputery");
            addItemDirectly(db, "Komputer Biurkowy", 2299.99f, "Idealny komputer do biura z procesorem i5.", "komputer2.png", "komputery");
            addItemDirectly(db, "Komputer Mini-PC", 1499.99f, "Kompaktowy mini-PC z SSD 512GB.", "komputer3.png", "komputery");
            addItemDirectly(db, "Komputer All-in-One", 3599.99f, "Ekran + komputer w jednym, procesor Ryzen 5.", "komputer4.png", "komputery");

            // Monitory
            addItemDirectly(db, "Monitor UHD", 1199.99f, "4K 32-calowy monitor do pracy i rozrywki.", "monitor1.png", "monitory");
            addItemDirectly(db, "Monitor Full HD", 799.99f, "Lekki, 24-calowy monitor do codziennego użytku.", "monitor2.png", "monitory");
            addItemDirectly(db, "Monitor Gamingowy", 1599.99f, "165Hz, 1ms, 27 cali, świetny do gier.", "monitor3.png", "monitory");
            addItemDirectly(db, "Monitor Ultra-Wide", 2299.99f, "34-calowy ekran panoramiczny do profesjonalistów.", "monitor4.png", "monitory");

            // Myszki
            addItemDirectly(db, "Mysz Gamingowa", 249.99f, "Myszka z sensorem 16000 DPI, dla zaawansowanych graczy.", "myszka1.png", "myszki");
            addItemDirectly(db, "Mysz Biurkowa", 59.99f, "Prosta, ergonomiczna myszka przewodowa.", "myszka2.png", "myszki");
            addItemDirectly(db, "Mysz Multimedialna", 129.99f, "Bezprzewodowa mysz z przyciskami funkcyjnymi.", "myszka3.png", "myszki");
            addItemDirectly(db, "Mysz Profesjonalna", 199.99f, "Dla grafików i projektantów, precyzyjny sensor.", "myszka4.png", "myszki");
        }

        private void addItemDirectly(SQLiteDatabase db, String name, float price, String description, String picture, String category) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, name);
            values.put(COLUMN_PRICE, price);
            values.put(COLUMN_DESCRIPTION, description);
            values.put(COLUMN_PICTURE, picture);
            values.put(COLUMN_CATEGORY, category);
            db.insert(TABLE_ITEMS, null, values);
        }

        // Generate a default set of items

        public List<Item> getItemsByCategory(String category) {
            List<Item> itemList = new ArrayList<>();
            SQLiteDatabase db = this.getReadableDatabase();
            
            String selectQuery = "SELECT * FROM " + TABLE_ITEMS + " WHERE " + COLUMN_CATEGORY + " = ?";
            Cursor cursor = db.rawQuery(selectQuery, new String[]{category});

            if (cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                    @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                    @SuppressLint("Range") float price = cursor.getFloat(cursor.getColumnIndex(COLUMN_PRICE));
                    @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
                    @SuppressLint("Range") String picture = cursor.getString(cursor.getColumnIndex(COLUMN_PICTURE));
                    @SuppressLint("Range") String itemCategory = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY));

                    Item item = new Item(id, name, price, description, picture, itemCategory);
                    itemList.add(item);
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
            return itemList;
        }

        // Metoda do dodawania nowego sprzętu do bazy danych
        public long addEquipment(String name, float price, String description, String picture) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            
            values.put(COLUMN_NAME, name);
            values.put(COLUMN_PRICE, price); 
            values.put(COLUMN_DESCRIPTION, description);
            values.put(COLUMN_PICTURE, picture);

            long newRowId = db.insert(TABLE_ITEMS, null, values);
            db.close();
            return newRowId;
        }

        // Metoda do zapisywania zamówienia
        public long saveOrder(String customerName, String orderDetails, float totalPrice) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            
            values.put(COLUMN_ORDER_DATE, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()).format(new Date()));
            values.put(COLUMN_CUSTOMER_NAME, customerName);
            values.put(COLUMN_ORDER_DETAILS, orderDetails);
            values.put(COLUMN_TOTAL_PRICE, totalPrice);

            long id = db.insert(TABLE_ORDERS, null, values);
            db.close();
            return id;
        }

        public List<String> getAllOrders() {
            List<String> ordersList = new ArrayList<>();
            SQLiteDatabase db = this.getReadableDatabase();
            
            Cursor cursor = db.query(TABLE_ORDERS, null, null, null, null, null, 
                COLUMN_ORDER_DATE + " DESC");

            if (cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_DATE));
                    @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(COLUMN_CUSTOMER_NAME));
                    @SuppressLint("Range") String details = cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_DETAILS));
                    @SuppressLint("Range") float total = cursor.getFloat(cursor.getColumnIndex(COLUMN_TOTAL_PRICE));
                    
                    ordersList.add(String.format("Data: %s\nZamawiający: %s\n%s\nSuma: %.2f zł\n", 
                        date, name, details, total));
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
            
            return ordersList;
        }

        public List<OrderItem> getAllOrdersWithIds() {
            List<OrderItem> ordersList = new ArrayList<>();
            SQLiteDatabase db = this.getReadableDatabase();
            
            Cursor cursor = db.query(TABLE_ORDERS, null, null, null, null, null, 
                COLUMN_ORDER_DATE + " DESC");

            if (cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") long id = cursor.getLong(cursor.getColumnIndex(COLUMN_ORDER_ID));
                    @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_DATE));
                    @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(COLUMN_CUSTOMER_NAME));
                    @SuppressLint("Range") String details = cursor.getString(cursor.getColumnIndex(COLUMN_ORDER_DETAILS));
                    @SuppressLint("Range") float total = cursor.getFloat(cursor.getColumnIndex(COLUMN_TOTAL_PRICE));
                    
                    ordersList.add(new OrderItem(id, date, name, details, total));
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
            
            return ordersList;
        }

        public void deleteOrder(long orderId) {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_ORDERS, COLUMN_ORDER_ID + " = ?", new String[]{String.valueOf(orderId)});
            db.close();
        }
    }
}
