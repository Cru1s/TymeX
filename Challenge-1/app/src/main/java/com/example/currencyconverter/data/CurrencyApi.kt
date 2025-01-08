package com.example.currencyconverter.data

import com.example.currencyconverter.data.model.HexarateResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CurrencyApi {
    @GET("api/rates/latest/{base}")
    suspend fun getRates(
        @Path("base") base: String,
        @Query("target") target: String
    ) : HexarateResponse
}