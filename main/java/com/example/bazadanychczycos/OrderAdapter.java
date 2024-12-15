package com.example.bazadanychczycos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

public class OrderAdapter extends ArrayAdapter<OrderItem> {
    private final Context context;
    private final List<OrderItem> orders;
    private final DataBase.DatabaseHelper dbHelper;

    public OrderAdapter(Context context, List<OrderItem> orders, DataBase.DatabaseHelper dbHelper) {
        super(context, R.layout.order_list_item, orders);
        this.context = context;
        this.orders = orders;
        this.dbHelper = dbHelper;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.order_list_item, parent, false);
        }

        OrderItem order = orders.get(position);

        TextView orderDetailsView = convertView.findViewById(R.id.text_order_details);
        TextView orderTotalView = convertView.findViewById(R.id.text_order_total);
        Button deleteButton = convertView.findViewById(R.id.button_delete);

        String orderText = String.format("Data: %s\nZamawiający: %s\n%s", 
            order.getDate(), order.getCustomerName(), order.getDetails());
        orderDetailsView.setText(orderText);
        
        orderTotalView.setText(String.format("Suma zamówienia: %.2f zł", order.getTotalPrice()));

        deleteButton.setOnClickListener(v -> {
            dbHelper.deleteOrder(order.getId());
            orders.remove(position);
            notifyDataSetChanged();
            Toast.makeText(context, "Zamówienie zostało usunięte", Toast.LENGTH_SHORT).show();
        });

        return convertView;
    }
}
