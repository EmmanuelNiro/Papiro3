package com.example.papiro3;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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

public class SearchFragment extends Fragment implements BookAdapter.OnBookClickListener {

    private static final String TAG = "SearchFragment";

    private EditText searchInput;
    private RecyclerView recyclerView;
    private BookAdapter bookAdapter;
    private ProgressBar progressBar;
    private TextView tvNoResults;

    private Call<SearchResponse> currentCall;

    public SearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializar vistas
        initViews(view);

        // Configurar RecyclerView
        setupRecyclerView();

        // Configurar búsqueda
        setupSearchListener();
    }

    private void initViews(View view) {
        searchInput = view.findViewById(R.id.searchInput);
        recyclerView = view.findViewById(R.id.recyclerViewSearch);
        progressBar = view.findViewById(R.id.progressBarSearch);
        tvNoResults = view.findViewById(R.id.tvNoResults);
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

    private void setupSearchListener() {
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();

                if ( query.length() > 2 ) {
                    // Cancelar búsqueda anterior si existe
                    if ( currentCall != null ) {
                        currentCall.cancel();
                    }

                    // Realizar nueva búsqueda
                    searchBooks(query);
                } else if ( query.length() == 0 ) {
                    // Limpiar resultados
                    bookAdapter.setBooks(new ArrayList<>());
                    tvNoResults.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void searchBooks(String query) {
        showLoading(true);
        tvNoResults.setVisibility(View.GONE);

        // Realizar llamada a la API
        currentCall = RetrofitClient.getInstance()
                .getAPI()
                .searchBooks(query, 30, 1);

        currentCall.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                showLoading(false);

                if ( response.isSuccessful() && response.body() != null ) {
                    SearchResponse searchResponse = response.body();
                    List<Doc> docs = searchResponse.getDocs();

                    if ( docs != null && !docs.isEmpty() ) {
                        List<Book> books = convertDocsToBooks(docs);
                        bookAdapter.setBooks(books);
                        recyclerView.setVisibility(View.VISIBLE);
                        tvNoResults.setVisibility(View.GONE);
                        Log.d(TAG, "Búsqueda completada: " + books.size() + " resultados");
                    } else {
                        showNoResults();
                    }
                } else {
                    showError("Error al realizar la búsqueda: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                if ( !call.isCanceled() ) {
                    showLoading(false);
                    showError("Error de conexión: " + t.getMessage());
                }
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

    private void showNoResults() {
        showLoading(false);
        tvNoResults.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Cancelar cualquier llamada pendiente
        if ( currentCall != null && !currentCall.isCanceled() ) {
            currentCall.cancel();
        }
    }
}