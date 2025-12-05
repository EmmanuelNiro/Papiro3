package com.example.papiro3.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.papiro3.R;
import com.example.papiro3.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context context;
    private List<Category> categoryList;
    private OnCategoryClickListener listener;
    private int selectedPosition = 0; // POPULAR seleccionado por defecto

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category, int position);
    }

    public CategoryAdapter(Context context, OnCategoryClickListener listener) {
        this.context = context;
        this.categoryList = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Category category = categoryList.get(position);
        holder.tvCategoryName.setText(category.getName());

        // Cambiar estilo según si está seleccionado o no
        if ( position == selectedPosition ) {
            // Categoría seleccionada (verde)
            holder.cardView.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.category_selected)
            );
            holder.tvCategoryName.setTextColor(
                    ContextCompat.getColor(context, android.R.color.white)
            );
        } else {
            // Categoría no seleccionada (beige)
            holder.cardView.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.category_unselected)
            );
            holder.tvCategoryName.setTextColor(
                    ContextCompat.getColor(context, R.color.category_text)
            );
        }

        // Click listener
        holder.cardView.setOnClickListener(v -> {
            if ( listener != null ) {
                int oldPosition = selectedPosition;
                selectedPosition = position;

                // Actualizar ambas posiciones
                notifyItemChanged(oldPosition);
                notifyItemChanged(selectedPosition);

                listener.onCategoryClick(category, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public void setCategories(List<Category> categories) {
        this.categoryList = categories;
        notifyDataSetChanged();
    }

    public void setSelectedPosition(int position) {
        int oldPosition = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(oldPosition);
        notifyItemChanged(selectedPosition);
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvCategoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewCategory);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
        }
    }
}

