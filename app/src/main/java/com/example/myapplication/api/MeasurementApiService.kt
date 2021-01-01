package com.example.myapplication.api

import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

private const val BASE_URL =
    "http://rainbow.fis.agh.edu.pl/meteo/"


interface MeasurementApiService {
    @POST
    fun getProperties(
        @Url url: String,
        @Header("Authorization") basicAuth: String,
        @Body dev_id: MultipartBody
    ):
            Call<String>
}

object MeasurementApi {
    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()
    val retrofitService: MeasurementApiService by lazy {
        retrofit.create(MeasurementApiService::class.java)
    }
}