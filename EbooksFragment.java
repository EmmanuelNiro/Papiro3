package com.example.papiro3;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.papiro3.adapter.BookAdapter;
import com.example.papiro3.api.RetrofitClient;
import com.example.papiro3.model.Book;
import com.example.papiro3.model.Doc;
import com.example.papiro3.model.SearchResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EbooksFragment extends Fragment implements BookAdapter.OnBookClickListener {

    private static final String TAG = "EbooksFragment";

    private RecyclerView recyclerView;
    private BookAdapter bookAdapter;
    private ProgressBar progressBar;

    public EbooksFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ebooks, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializar vistas
        initViews(view);

        // Configurar RecyclerView
        setupRecyclerView();

        // Cargar libros populares
        loadPopularBooks();
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewBooks);
        progressBar = view.findViewById(R.id.progressBar);
    }

    private void setupRecyclerView() {
        // Crear adaptador
        bookAdapter = new BookAdapter(getContext(), this);

        // Configurar GridLayoutManager con 2 columnas
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(bookAdapter);

        // Agregar espacio entre items
        int spacing = getResources().getDimensionPixelSize(R.dimen.grid_spacing);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, spacing, true));
    }

    private void loadPopularBooks() {
        showLoading(true);

        // Realizar llamada a la API
        Call<SearchResponse> call = RetrofitClient.getInstance()
                .getAPI()
                .searchBooks("popular", 30, 1);

        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                showLoading(false);

                if ( response.isSuccessful() && response.body() != null ) {
                    SearchResponse searchResponse = response.body();
                    List<Doc> docs = searchResponse.getDocs();

                    if ( docs != null && !docs.isEmpty() ) {
                        List<Book> books = convertDocsToBooks(docs);
                        bookAdapter.setBooks(books);
                    } else {
                        showError("No se encontraron libros");
                    }
                } else {
                    showError("Error al cargar libros: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                showLoading(false);
                showError("Error de conexión: " + t.getMessage());
            }
        });
    }

    private List<Book> convertDocsToBooks(List<Doc> docs) {
        List<Book> books = new ArrayList<>();

        for (Doc doc : docs) {
            Book book = new Book();
            book.setTitle(doc.getTitle());
            book.setAuthor(doc.getAuthor());
            book.setCoverUrl(doc.getCoverUrl());
            book.setKey(doc.getKey());

            if ( doc.getFirstPublishYear() != 0 ) {
                book.setPublishYear(String.valueOf(doc.getFirstPublishYear()));
            }

            if ( doc.getIsbn() != null && !doc.getIsbn().isEmpty() ) {
                book.setIsbn(doc.getIsbn().get(0));
            }

            books.add(book);
        }

        return books;
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBookClick(Book book) {
        // Esta implementación será manejada por MainActivity a través de un callback
        if ( getContext() instanceof BookAdapter.OnBookClickListener ) {
            ((BookAdapter.OnBookClickListener) getContext()).onBookClick(book);
        }
    }
}