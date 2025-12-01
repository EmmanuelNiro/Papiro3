package com.example.papiro3;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.papiro3.adapter.BookAdapter;
import com.example.papiro3.adapter.CategoryAdapter;
import com.example.papiro3.api.RetrofitClient;
import com.example.papiro3.model.Book;
import com.example.papiro3.model.Category;
import com.example.papiro3.model.Doc;
import com.example.papiro3.model.SearchResponse;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements BookAdapter.OnBookClickListener, CategoryAdapter.OnCategoryClickListener {

    private static final String TAG = "MainActivity";

    // RecyclerViews
    private RecyclerView recyclerViewCategories;
    private RecyclerView recyclerViewBooks;

    // Adaptadores
    private CategoryAdapter categoryAdapter;
    private BookAdapter bookAdapter;

    // Vistas
    private ProgressBar progressBar;

    // Categoría actual seleccionada
    private String currentQuery = "popular";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar vistas
        initViews();

        // Configurar RecyclerView de categorías
        setupCategoriesRecyclerView();

        // Configurar RecyclerView de libros
        setupBooksRecyclerView();

        // Cargar categorías
        loadCategories();

        // Cargar libros populares por defecto
        loadBooksByCategory(currentQuery);
    }

    private void initViews() {
        recyclerViewCategories = findViewById(R.id.recyclerViewCategories);
        recyclerViewBooks = findViewById(R.id.recyclerViewBooks);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupCategoriesRecyclerView() {
        // Crear adaptador de categorías
        categoryAdapter = new CategoryAdapter(this, this);

        // Configurar LinearLayoutManager horizontal
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
        );
        recyclerViewCategories.setLayoutManager(layoutManager);
        recyclerViewCategories.setAdapter(categoryAdapter);
    }

    private void setupBooksRecyclerView() {
        // Crear adaptador de libros
        bookAdapter = new BookAdapter(this, this);

        // Configurar GridLayoutManager con 2 columnas
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerViewBooks.setLayoutManager(layoutManager);
        recyclerViewBooks.setAdapter(bookAdapter);

        // Agregar espacio entre items
        int spacing = getResources().getDimensionPixelSize(R.dimen.grid_spacing);
        recyclerViewBooks.addItemDecoration(new GridSpacingItemDecoration(2, spacing, true));
    }

    private void loadCategories() {
        // Crear lista de categorías
        List<Category> categories = new ArrayList<>();
        categories.add(new Category("POPULAR", "popular"));
        categories.add(new Category("FICCIÓN", "fiction"));
        categories.add(new Category("ROMANCE", "romance"));
        categories.add(new Category("CIENCIA", "science"));
        categories.add(new Category("HISTORIA", "history"));
        categories.add(new Category("FANTASÍA", "fantasy"));
        categories.add(new Category("MISTERIO", "mystery"));
        categories.add(new Category("BIOGRAFÍA", "biography"));

        // Establecer la primera categoría como seleccionada
        categories.get(0).setSelected(true);

        // Pasar categorías al adaptador
        categoryAdapter.setCategories(categories);
    }

    private void loadBooksByCategory(String query) {
        showLoading(true);
        currentQuery = query;

        // Realizar llamada a la API
        Call<SearchResponse> call = RetrofitClient.getInstance()
                .getAPI()
                .searchBooks(query, 30, 1);

        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, Response<SearchResponse> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    SearchResponse searchResponse = response.body();
                    List<Doc> docs = searchResponse.getDocs();

                    if (docs != null && !docs.isEmpty()) {
                        List<Book> books = convertDocsToBooks(docs);
                        bookAdapter.setBooks(books);
                        Log.d(TAG, "Libros cargados: " + books.size() + " para categoría: " + query);
                    } else {
                        showError("No se encontraron libros para esta categoría");
                        bookAdapter.setBooks(new ArrayList<>()); // Limpiar lista
                    }
                } else {
                    showError("Error al cargar libros: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                showLoading(false);
                showError("Error de conexión: " + t.getMessage());
                Log.e(TAG, "Error: ", t);
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

            if (doc.getFirstPublishYear() != null) {
                book.setPublishYear(String.valueOf(doc.getFirstPublishYear()));
            }

            if (doc.getIsbn() != null && !doc.getIsbn().isEmpty()) {
                book.setIsbn(doc.getIsbn().get(0));
            }

            books.add(book);
        }

        return books;
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerViewBooks.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    // Callback cuando se hace clic en un libro
    @Override
    public void onBookClick(Book book) {
        Intent intent = new Intent(this, BookDetailActivity.class);
        intent.putExtra(BookDetailActivity.EXTRA_BOOK, (Parcelable) book);
        startActivity(intent);
    }

    // Callback cuando se hace clic en una categoría
    @Override
    public void onCategoryClick(Category category, int position) {
        Log.d(TAG, "Categoría seleccionada: " + category.getName() + " - Query: " + category.getQuery());

        // Scroll al inicio del RecyclerView de libros
        recyclerViewBooks.smoothScrollToPosition(0);

        // Cargar libros de la categoría seleccionada
        loadBooksByCategory(category.getQuery());
    }
}