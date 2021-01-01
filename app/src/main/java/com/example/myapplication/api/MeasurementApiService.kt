package com.example.myapplication.api

import com.example.myapplication.shared.Measurement
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

private const val BASE_URL =
    "http://rainbow.fis.agh.edu.pl/meteo/"


interface MeasurementApiService {
    @POST
    suspend fun getProperties(
        @Url url: String,
        @Header("Authorization") basicAuth: String,
        @Body dev_id: MultipartBody
    ):
            List<Measurement>
}

object MeasurementApi {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BASE_URL)
        .build()
    val retrofitService: MeasurementApiService by lazy {
        retrofit.create(MeasurementApiService::class.java)
    }
}