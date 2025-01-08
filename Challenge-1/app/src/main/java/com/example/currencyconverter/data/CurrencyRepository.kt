package com.example.currencyconverter.data

import retrofit2.HttpException

class CurrencyRepository(private val api : CurrencyApi) {

    suspend fun getExchangeRate(base: String, target: String) : Result<Double> {
        return try {
            val response = api.getRates(base, target)
            if(response.status_code == 200){
                Result.success(response.data.mid)
            } else {
                Result.failure(Exception("Failed to get exchange rate"))
            }
        } catch (e: HttpException) {
            // Handle specific HTTP errors like 422
            if (e.code() == 422) {
                Result.failure(Exception("Unsupported currency"))
            } else {
                Result.failure(e) // Other HTTP errors
            }
        } catch (e: Exception) {
            Result.failure(e) // Network or other errors
        }
    }
}