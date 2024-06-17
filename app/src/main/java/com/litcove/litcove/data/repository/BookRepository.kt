package com.litcove.litcove.data.repository

import com.litcove.litcove.data.api.ApiService
import com.litcove.litcove.data.api.ApiConfig
import com.litcove.litcove.data.response.BookResponse
import retrofit2.Response

class BookRepository {
    private val apiService: ApiService = ApiConfig.getApiService()
    suspend fun getBooksBySubject(subject: String, startIndex: Int, maxResults: Int): Response<BookResponse> {
        val validMaxResults = if (maxResults > 40) 40 else maxResults
        return apiService.getBooksBySubject(subject, startIndex, validMaxResults)
    }
}