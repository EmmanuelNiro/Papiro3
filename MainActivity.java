package com.example.papiro3;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.papiro3.adapter.BookAdapter;
import com.example.papiro3.model.Book;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity implements BookAdapter.OnBookClickListener {

    private static final String TAG = "MainActivity";
    private BottomNavigationView bottomNav;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar vistas
        initViews();

        // Configurar Bottom Navigation
        setupBottomNavigation();

        // Cargar fragment por defecto
        if ( savedInstanceState == null ) {
            loadFragment(new EbooksFragment(), "Ebooks");
        }
    }

    private void initViews() {
        bottomNav = findViewById(R.id.bottomNav);
        fragmentManager = getSupportFragmentManager();
    }

    private void setupBottomNavigation() {
        bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if ( itemId == R.id.nav_ebooks ) {
                loadFragment(new EbooksFragment(), "Ebooks");
                return true;
            } else if ( itemId == R.id.nav_audiobooks ) {
                loadFragment(new AudiobooksFragment(), "Audiolibros");
                return true;
            } else if ( itemId == R.id.nav_search ) {
                loadFragment(new SearchFragment(), "Buscar");
                return true;
            } else if ( itemId == R.id.nav_profile ) {
                loadFragment(new ProfileFragment(), "Perfil");
                return true;
            } else if ( itemId == R.id.nav_settings ) {
                loadFragment(new SettingsFragment(), "Configuración");
                return true;
            }

            return false;
        });
    }

    private void loadFragment(Fragment fragment, String title) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

        setTitle(title);
    }

    @Override
    public void onBookClick(Book book) {
        Intent intent = new Intent(this, BookDetailActivity.class);
        intent.putExtra(BookDetailActivity.EXTRA_BOOK, (Serializable) book);
        startActivity(intent);
    }
}