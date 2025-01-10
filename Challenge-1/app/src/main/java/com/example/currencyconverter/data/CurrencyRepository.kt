package com.example.currencyconverter.data

import retrofit2.HttpException

class CurrencyRepository(private val api : CurrencyApi) {

    // Function to get the exchange rate from the API
    suspend fun getExchangeRate(base: String, target: String) : Result<Double> {
        return try {
            val response = api.getRates(base, target)
            Result.success(response.data.mid)
        } catch (e: HttpException) {
            // Handle HTTP errors
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