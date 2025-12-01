package com.example.papiro3.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.papiro3.R;
import com.example.papiro3.model.Book;
import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private Context context;
    private List<Book> bookList;
    private OnBookClickListener listener;

    public interface OnBookClickListener {
        void onBookClick(Book book);
    }

    public BookAdapter(Context context, OnBookClickListener listener) {
        this.context = context;
        this.bookList = new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        Book book = bookList.get(position);

        // Establecer título
        holder.tvTitle.setText(book.getTitle());

        // Establecer autor
        if (book.getAuthor() != null && !book.getAuthor().isEmpty()) {
            holder.tvAuthor.setText(book.getAuthor());
            holder.tvAuthor.setVisibility(View.VISIBLE);
        } else {
            holder.tvAuthor.setVisibility(View.GONE);
        }

        // Cargar imagen con Glide
        if (book.getCoverUrl() != null && !book.getCoverUrl().isEmpty()) {
            Glide.with(context)
                    .load(book.getCoverUrl())
                    .placeholder(R.drawable.placeholder_book)
                    .error(R.drawable.error_book)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .into(holder.ivCover);
        } else {
            holder.ivCover.setImageResource(R.drawable.placeholder_book);
        }

        // Click listener
        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBookClick(book);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public void setBooks(List<Book> books) {
        this.bookList = books;
        notifyDataSetChanged();
    }

    public void addBooks(List<Book> books) {
        int startPosition = this.bookList.size();
        this.bookList.addAll(books);
        notifyItemRangeInserted(startPosition, books.size());
    }

    public void clearBooks() {
        this.bookList.clear();
        notifyDataSetChanged();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView ivCover;
        TextView tvTitle;
        TextView tvAuthor;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardViewBook);
            ivCover = itemView.findViewById(R.id.ivBookCover);
            tvTitle = itemView.findViewById(R.id.tvBookTitle);
            tvAuthor = itemView.findViewById(R.id.tvBookAuthor);
        }
    }
}