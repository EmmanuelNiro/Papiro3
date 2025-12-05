package com.example.papiro3.api;

import com.example.papiro3.model.BookDetailResponse;
import com.example.papiro3.model.SearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OpenLibraryAPI {

    // Búsqueda de libros
    @GET("search.json")
    Call<SearchResponse> searchBooks(
            @Query("q") String query,
            @Query("limit") int limit,
            @Query("page") int page
    );

    // Obtener detalles de un libro
    @GET("books/{key}.json")
    Call<BookDetailResponse> getBookDetails(
            @Path("key") String key
    );

}