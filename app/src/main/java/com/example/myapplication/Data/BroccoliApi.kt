package com.example.myapplication.Data

import retrofit2.Call

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface BroccoliApi {
    @Headers("Content-Type: application/json")
    @POST(" ")
    fun postEmailAccount(@Body requestBody: String): Call<String>
}