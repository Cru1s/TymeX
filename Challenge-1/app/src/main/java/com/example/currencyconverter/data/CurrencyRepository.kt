package com.example.currencyconverter.data

class CurrencyRepository(private val api : CurrencyApi) {

    suspend fun getExchangeRate(base: String, target: String) : Result<Double> {
        return try {
            val response = api.getRates(base, target)
            if(response.status_code == 200){
                Result.success(response.data.mid)
            } else {
                Result.failure(Exception("Error: ${response.status_code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}