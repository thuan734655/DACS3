package com.example.dacs3.network

import com.example.dacs3.models.HomeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface ApiService {
    @GET("home")
    suspend fun getHomeData(
        @Header("Authorization") bearerToken: String
    ): Response<HomeResponse>
}
