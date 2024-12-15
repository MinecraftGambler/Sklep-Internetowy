package com.example.bazadanychczycos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private List<Item> itemList;

    public ItemAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(position);

        holder.nameTextView.setText(item.getName());
        holder.priceTextView.setText(String.valueOf(item.getPrice()));
        holder.descriptionTextView.setText(item.getDescription());

        // Pobieranie obrazu z zasobów
        int imageResource = holder.itemView.getContext().getResources().getIdentifier(
                item.getPicture(), "drawable", holder.itemView.getContext().getPackageName()
        );

        if (imageResource != 0) {
            holder.pictureImageView.setImageResource(imageResource);
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public TextView priceTextView;
        public TextView descriptionTextView;
        public ImageView pictureImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.item_name);
            priceTextView = itemView.findViewById(R.id.item_price);
            descriptionTextView = itemView.findViewById(R.id.item_description);
            pictureImageView = itemView.findViewById(R.id.item_picture);
        }
    }
}
