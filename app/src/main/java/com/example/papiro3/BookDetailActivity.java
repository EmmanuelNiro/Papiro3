package com.example.papiro3;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.example.papiro3.api.RetrofitClient;
import com.example.papiro3.model.Book;
import com.example.papiro3.model.BookDetailResponse;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class BookDetailActivity extends AppCompatActivity {

    private static final String TAG = "BookDetailActivity";
    public static final String EXTRA_BOOK = "extra_book";

    // Views principales
    private ImageView ivBookCover;
    private TextView tvBookTitle;
    private TextView tvBookAuthor;
    private TextView tvPublishDate;
    private TextView tvPages;
    private TextView tvPublisher;
    private TextView tvLanguage;
    private TextView tvIsbn;
    private TextView tvDescription;
    private ChipGroup chipGroupSubjects;
    private ProgressBar progressBar;
    private ImageButton btnBack;
    private ImageButton btnFavorite;
    private View layoutBookInfo;

    // Datos
    private Book book;
    private boolean isFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        // Obtener el libro del Intent
        if (getIntent().hasExtra(EXTRA_BOOK)) {
            book = (Book) getIntent().getSerializableExtra(EXTRA_BOOK);
        } else {
            Toast.makeText(this, "Error: No se pudo cargar el libro", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Inicializar vistas
        initViews();

        // Configurar listeners
        setupListeners();

        // Mostrar información básica del libro
        displayBasicInfo();

        // Cargar detalles completos desde la API
        loadBookDetails();
    }

    private void initViews() {
        ivBookCover = findViewById(R.id.ivBookCoverDetail);
        tvBookTitle = findViewById(R.id.tvBookTitleDetail);
        tvBookAuthor = findViewById(R.id.tvBookAuthorDetail);
        tvPublishDate = findViewById(R.id.tvPublishDate);
        tvPages = findViewById(R.id.tvPages);
        tvPublisher = findViewById(R.id.tvPublisher);
        tvLanguage = findViewById(R.id.tvLanguage);
        tvIsbn = findViewById(R.id.tvIsbn);
        tvDescription = findViewById(R.id.tvDescription);
        chipGroupSubjects = findViewById(R.id.chipGroupSubjects);
        progressBar = findViewById(R.id.progressBar);
        btnBack = findViewById(R.id.btnBack);
        btnFavorite = findViewById(R.id.btnFavorite);
        layoutBookInfo = findViewById(R.id.layoutBookInfo);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnFavorite.setOnClickListener(v -> toggleFavorite());
    }

    private void displayBasicInfo() {
        // Título
        tvBookTitle.setText(book.getTitle());

        // Autor
        if (book.getAuthor() != null && !book.getAuthor().isEmpty()) {
            tvBookAuthor.setText(book.getAuthor());
        } else {
            tvBookAuthor.setText("Autor desconocido");
        }

        // Año de publicación (básico)
        if (book.getPublishYear() != null && !book.getPublishYear().isEmpty()) {
            tvPublishDate.setText(book.getPublishYear());
        }

        // Cargar portada
        if (book.getCoverUrl() != null && !book.getCoverUrl().isEmpty()) {
            Glide.with(this)
                    .load(book.getCoverUrl())
                    .placeholder(R.drawable.placeholder_book)
                    .error(R.drawable.placeholder_book)
                    .into(ivBookCover);
        } else {
            ivBookCover.setImageResource(R.drawable.placeholder_book);
        }
    }

    private void loadBookDetails() {
        // Obtener el key del libro
        String bookKey = book.getKey();

        if (bookKey == null || bookKey.isEmpty()) {
            Log.e(TAG, "No hay key disponible para cargar detalles");
            hideLoading();
            return;
        }

        showLoading();

        // Extraer el ID del key (ej: "/works/OL45883W" -> "OL45883W")
        String workId = bookKey;
        if (workId.startsWith("/works/")) {
            workId = workId.substring(7); // Remover "/works/"
        }

        Log.d(TAG, "Cargando detalles para: " + workId);

        // Llamar a la API
        Call<BookDetailResponse> call = RetrofitClient.getInstance()
                .getAPI()
                .getWorkDetails(workId);

        call.enqueue(new Callback<BookDetailResponse>() {
            @Override
            public void onResponse(Call<BookDetailResponse> call, Response<BookDetailResponse> response) {
                hideLoading();

                if (response.isSuccessful() && response.body() != null) {
                    BookDetailResponse details = response.body();
                    displayBookDetails(details);
                    Log.d(TAG, "Detalles cargados exitosamente");
                } else {
                    Log.e(TAG, "Error en respuesta: " + response.code());
                    Toast.makeText(BookDetailActivity.this,
                            "No se pudieron cargar los detalles completos",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BookDetailResponse> call, Throwable t) {
                hideLoading();
                Log.e(TAG, "Error al cargar detalles: ", t);
                Toast.makeText(BookDetailActivity.this,
                        "Error de conexión",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayBookDetails(BookDetailResponse details) {
        // Descripción
        String description = details.getDescription();
        if (description != null && !description.isEmpty()) {
            tvDescription.setText(description);
            tvDescription.setVisibility(View.VISIBLE);
        } else {
            tvDescription.setText("No hay descripción disponible");
        }

        // Fecha de publicación
        if (details.getFirstPublishDate() != null) {
            tvPublishDate.setText(details.getFirstPublishDate());
        }

        // Número de páginas
        if (details.getNumberOfPages() != null) {
            tvPages.setText(details.getNumberOfPages() + " páginas");
            tvPages.setVisibility(View.VISIBLE);
        } else {
            tvPages.setVisibility(View.GONE);
        }

        // Editorial
        List<String> publishers = details.getPublishers();
        if (publishers != null && !publishers.isEmpty()) {
            tvPublisher.setText(publishers.get(0));
            tvPublisher.setVisibility(View.VISIBLE);
        } else {
            tvPublisher.setVisibility(View.GONE);
        }

        // Idioma
        String language = details.getLanguageString();
        if (language != null) {
            tvLanguage.setText(language);
            tvLanguage.setVisibility(View.VISIBLE);
        } else {
            tvLanguage.setVisibility(View.GONE);
        }

        // ISBN
        List<String> isbn13 = details.getIsbn13();
        List<String> isbn10 = details.getIsbn10();
        if (isbn13 != null && !isbn13.isEmpty()) {
            tvIsbn.setText("ISBN: " + isbn13.get(0));
            tvIsbn.setVisibility(View.VISIBLE);
        } else if (isbn10 != null && !isbn10.isEmpty()) {
            tvIsbn.setText("ISBN: " + isbn10.get(0));
            tvIsbn.setVisibility(View.VISIBLE);
        } else {
            tvIsbn.setVisibility(View.GONE);
        }

        // Materias/Géneros
        List<String> subjects = details.getSubjects();
        if (subjects != null && !subjects.isEmpty()) {
            chipGroupSubjects.removeAllViews();

            // Mostrar máximo 10 materias
            int maxSubjects = Math.min(subjects.size(), 10);
            for (int i = 0; i < maxSubjects; i++) {
                Chip chip = new Chip(this);
                chip.setText(subjects.get(i));
                chip.setChipBackgroundColorResource(R.color.category_unselected);
                chip.setTextColor(ContextCompat.getColor(this, R.color.category_text));
                chip.setClickable(false);
                chipGroupSubjects.addView(chip);
            }

            chipGroupSubjects.setVisibility(View.VISIBLE);
        } else {
            chipGroupSubjects.setVisibility(View.GONE);
        }

        // Actualizar portada si hay mejor calidad
        String coverUrl = details.getCoverUrl();
        if (coverUrl != null && !coverUrl.isEmpty()) {
            Glide.with(this)
                    .load(coverUrl)
                    .placeholder(R.drawable.placeholder_book)
                    .error(R.drawable.placeholder_book)
                    .into(ivBookCover);
        }
    }

    private void toggleFavorite() {
        isFavorite = !isFavorite;

        if (isFavorite) {
            btnFavorite.setImageResource(R.drawable.ic_favorite_filled);
            Toast.makeText(this, "Agregado a favoritos", Toast.LENGTH_SHORT).show();
            // Aquí puedes guardar en base de datos local
        } else {
            btnFavorite.setImageResource(R.drawable.ic_favorite_border);
            Toast.makeText(this, "Eliminado de favoritos", Toast.LENGTH_SHORT).show();
            // Aquí puedes eliminar de base de datos local
        }
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        layoutBookInfo.setVisibility(View.GONE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        layoutBookInfo.setVisibility(View.VISIBLE);
    }
}