package com.example.papiro3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.papiro3.api.RetrofitClient;
import com.example.papiro3.model.AuthorName;
import com.example.papiro3.model.Book;
import com.example.papiro3.model.BookDetailResponse;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookDetailActivity extends AppCompatActivity {

    public static final String EXTRA_BOOK = "book";
    private static final String TAG = "BookDetailActivity";

    // Vistas
    private ImageView coverImage;
    private TextView titleText;
    private TextView authorText;
    private TextView categoryText;
    private RatingBar ratingBar;
    private TextView ratingCountText;
    private TextView descriptionText;
    private TextView publisherText;
    private TextView publishYearText;
    private TextView pagesText;
    private ProgressBar progressBar;
    private ScrollView scrollView;
    private ImageButton backButton;
    private ImageButton favoriteButton;
    private ImageButton shareButton;

    private Book currentBook;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        // Inicializar vistas
        initViews();

        // Obtener el libro del intent
        if ( getIntent().hasExtra(EXTRA_BOOK) ) {
            currentBook = getIntent().getParcelableExtra(EXTRA_BOOK);
            displayBookInfo();
            loadBookDetails();
        } else {
            showError("No se pudo cargar el libro");
            finish();
        }

        // Configurar listeners de botones
        setupListeners();
    }

    private void initViews() {
        coverImage = findViewById(R.id.coverImage);
        titleText = findViewById(R.id.titleText);
        authorText = findViewById(R.id.authorText);
        categoryText = findViewById(R.id.categoryText);
        ratingBar = findViewById(R.id.ratingBar);
        ratingCountText = findViewById(R.id.ratingCountText);
        descriptionText = findViewById(R.id.descriptionText);
        publisherText = findViewById(R.id.publisherText);
        publishYearText = findViewById(R.id.publishYearText);
        pagesText = findViewById(R.id.pagesText);
        progressBar = findViewById(R.id.progressBar);
        scrollView = findViewById(R.id.scrollView);
        backButton = findViewById(R.id.backButton);
        favoriteButton = findViewById(R.id.favoriteButton);
        shareButton = findViewById(R.id.shareButton);
    }

    private void displayBookInfo() {
        if ( currentBook == null ) return;

        // Cargar portada
        if ( currentBook.getCoverUrl() != null && !currentBook.getCoverUrl().isEmpty() ) {
            Glide.with(this)
                    .load(currentBook.getCoverUrl())
                    .placeholder(R.drawable.ic_book)
                    .into(coverImage);
        }

        // Mostrar información básica
        titleText.setText(currentBook.getTitle() != null ? currentBook.getTitle() : "Título desconocido");
        authorText.setText(currentBook.getAuthor() != null ? "Por " + currentBook.getAuthor() : "Autor desconocido");
        categoryText.setText("Ficción"); // Será actualizado con datos de API

        // Rating inicial (será actualizado)
        ratingBar.setRating(3.5f);
        ratingCountText.setText("3.5★ (245 reseñas)");

        // Descripción inicial
        descriptionText.setText("Cargando información del libro...");
    }

    private void loadBookDetails() {
        if ( currentBook == null || currentBook.getKey() == null ) {
            return;
        }

        showLoading(true);

        // Llamar a la API para obtener detalles del libro
        String bookKey = currentBook.getKey();

        RetrofitClient.getInstance()
                .getAPI()
                .getBookDetails(bookKey)
                .enqueue(new Callback<BookDetailResponse>() {
                    @Override
                    public void onResponse(Call<BookDetailResponse> call, Response<BookDetailResponse> response) {
                        showLoading(false);

                        if ( response.isSuccessful() && response.body() != null ) {
                            BookDetailResponse details = response.body();
                            updateUIWithDetails(details);
                            Log.d(TAG, "Detalles cargados exitosamente");
                        } else {
                            Log.d(TAG, "Respuesta vacía de API");
                        }
                    }

                    @Override
                    public void onFailure(Call<BookDetailResponse> call, Throwable t) {
                        showLoading(false);
                        Log.e(TAG, "Error al cargar detalles: " + t.getMessage());
                    }
                });
    }


    private void setupListeners() {
        // Botón de atrás
        backButton.setOnClickListener(v -> finish());

        // Botón de favorito
        favoriteButton.setOnClickListener(v -> toggleFavorite());

        // Botón de compartir
        shareButton.setOnClickListener(v -> shareBook());
    }

    private void toggleFavorite() {
        isFavorite = !isFavorite;
        if ( isFavorite ) {
            favoriteButton.setImageResource(android.R.drawable.ic_dialog_info);
            Toast.makeText(this, "Agregado a favoritos", Toast.LENGTH_SHORT).show();
        } else {
            favoriteButton.setImageResource(android.R.drawable.ic_input_add);
            Toast.makeText(this, "Removido de favoritos", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareBook() {
        if ( currentBook == null ) return;

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                "Mira este libro: " + currentBook.getTitle() + " por " + currentBook.getAuthor());
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, "Compartir libro"));
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        scrollView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void updateUIWithDetails(BookDetailResponse details) {
        if ( details == null ) return;

        // Actualizar título (ya es seguro)
        if ( details.getTitle() != null && !details.getTitle().isEmpty() ) {
            titleText.setText(details.getTitle());
        }

        // Actualizar autor (CORREGIDO: con comprobación de nulidad y tamaño)
        if ( details.getAuthors() != null && !details.getAuthors().isEmpty() && details.getAuthors().get(0) != null && details.getAuthors().get(0).getName() != null ) {
            authorText.setText("Por " + details.getAuthors().get(0).getName());
        } else {
            // Opcional: mantener el texto que ya tenía si la API no devuelve autor
            // authorText.setText("Autor desconocido");
        }

        // Actualizar descripción (mejorado)
        if ( details.getDescription() != null && !details.getDescription().isEmpty() ) {
            descriptionText.setText(details.getDescription());
        } else {
            descriptionText.setText("Sin descripción disponible");
        }

        // Actualizar información de publicación (CORREGIDO: con comprobación)
        if ( details.getPublishers() != null && !details.getPublishers().isEmpty() && details.getPublishers().get(0) != null ) {
            publisherText.setText("Editorial: " + details.getPublishers().get(0));
            publisherText.setVisibility(View.VISIBLE); // Muestra el campo
        } else {
            publisherText.setVisibility(View.GONE); // Oculta el campo si no hay info
        }

        if ( details.getPublish_date() != null && !details.getPublish_date().isEmpty() ) {
            publishYearText.setText("Publicado: " + details.getPublish_date());
            publishYearText.setVisibility(View.VISIBLE);
        } else {
            publishYearText.setVisibility(View.GONE);
        }

        if ( details.getNumber_of_pages() > 0 ) {
            pagesText.setText("Páginas: " + details.getNumber_of_pages());
            pagesText.setVisibility(View.VISIBLE);
        } else {
            pagesText.setVisibility(View.GONE);
        }

        // Actualizar categoría (CORREGIDO: con comprobación)
        if ( details.getSubjects() != null && !details.getSubjects().isEmpty() && details.getSubjects().get(0) != null ) {
            categoryText.setText(details.getSubjects().get(0));
        }

        // Actualizar rating (ya es seguro)
        if ( details.getRating() > 0 ) {
            ratingBar.setRating((float) details.getRating());
            ratingCountText.setText(String.format("%.1f★ (%d reseñas)",
                    details.getRating(), details.getRatingCount()));
        }
    }
}
