package com.example.bazadanychczycos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ShortcutAdapter extends RecyclerView.Adapter<ShortcutAdapter.ViewHolder> {
    private List<Shortcut> shortcuts;
    private Context context;

    public ShortcutAdapter(Context context, List<Shortcut> shortcuts) {
        this.context = context;
        this.shortcuts = shortcuts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.shortcut_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Shortcut shortcut = shortcuts.get(position);
        holder.nameTextView.setText(shortcut.getName());
        holder.iconImageView.setImageResource(shortcut.getIcon());
        
        holder.itemView.setOnClickListener(v -> {
            // Tutaj dodaj logikę, co się stanie po kliknięciu skrótu
        });
    }

    @Override
    public int getItemCount() {
        return shortcuts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public ImageView iconImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.shortcut_name);
            iconImageView = itemView.findViewById(R.id.shortcut_icon);
        }
    }
}