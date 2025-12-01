package com.example.papiro3.api;

import com.example.papiro3.model.SearchResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenLibraryAPI {

    // Búsqueda de libros populares
    @GET("search.json")
    Call<SearchResponse> searchBooks(
            @Query("q") String query,
            @Query("limit") int limit,
            @Query("page") int page
    );

    // Búsqueda por título
    @GET("search.json")
    Call<SearchResponse> searchByTitle(
            @Query("title") String title,
            @Query("limit") int limit
    );

    // Búsqueda por autor
    @GET("search.json")
    Call<SearchResponse> searchByAuthor(
            @Query("author") String author,
            @Query("limit") int limit
    );

    // Búsqueda por categoría/tema
    @GET("search.json")
    Call<SearchResponse> searchBySubject(
            @Query("subject") String subject,
            @Query("limit") int limit
    );
}
