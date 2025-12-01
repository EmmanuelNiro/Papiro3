package com.example.papiro3.api;

import com.example.papiro3.model.BookDetailResponse;
import com.example.papiro3.model.SearchResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OpenLibraryAPI {

    /**
     * Buscar libros por query
     * @param query Término de búsqueda
     * @param limit Número de resultados
     * @param page Página de resultados
     * @return Lista de libros encontrados
     */
    @GET("search.json")
    Call<SearchResponse> searchBooks(
            @Query("q") String query,
            @Query("limit") int limit,
            @Query("page") int page
    );

    /**
     * Obtener detalles de una obra (work)
     * @param workId ID de la obra (ej: "OL45883W")
     * @return Detalles completos del libro
     */
    @GET("works/{workId}.json")
    Call<BookDetailResponse> getWorkDetails(
            @Path("workId") String workId
    );

    /**
     * Obtener detalles de un libro específico
     * @param bookId ID del libro (ej: "OL7353617M")
     * @return Detalles completos del libro
     */
    @GET("books/{bookId}.json")
    Call<BookDetailResponse> getBookDetails(
            @Path("bookId") String bookId
    );
}
