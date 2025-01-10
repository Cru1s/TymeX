package com.example.currencyconverter.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverter.data.CurrencyRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException

class CurrencyViewModel(private val repository: CurrencyRepository) : ViewModel() {

    // LiveData to hold the result of a currency conversion
    val conversionResult = MutableLiveData<String>()

    // LiveData to hold error messages
    val errorMessage = MutableLiveData<String>()

    // Converts the given amount from base currency to target currency
    fun convertCurrency(amount: Double, base: String, target: String) {
        viewModelScope.launch {
            // Fetch exchange rate from the repository
            val result = repository.getExchangeRate(base, target)
            if (result.isSuccess) {
                val rate = result.getOrNull()
                if (rate != null) {
                    // Calculate and format the converted amount
                    val convertedAmount = String.format("%.5f", amount * rate)
                    conversionResult.postValue(convertedAmount)
                }
            } else {
                // Handle errors from the API call
                if (result.exceptionOrNull() is HttpException) {
                    val code = (result.exceptionOrNull() as HttpException).code()
                    if (code == 422) {
                        errorMessage.postValue("Unsupported currency")
                    } else {
                        // Handle generic HTTP errors
                        errorMessage.postValue("An error occurred")
                    }
                } else {
                    // Log unknown errors and post a user-friendly message
                    Log.e("CurrencyViewModel", "An error occurred", result.exceptionOrNull())
                    val error = result.exceptionOrNull()?.message ?: "Unknown error occurred"
                    errorMessage.postValue(error)
                }
            }
        }
    }
}
