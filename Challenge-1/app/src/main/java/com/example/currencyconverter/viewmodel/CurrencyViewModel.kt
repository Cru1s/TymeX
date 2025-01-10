package com.example.currencyconverter.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverter.data.CurrencyRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException

class CurrencyViewModel (private val repository: CurrencyRepository) : ViewModel(){

    val conversionResult = MutableLiveData<String>()
    val errorMessage = MutableLiveData<String>()

    fun convertCurrency(amount: Double, base: String, target: String){
        viewModelScope.launch {
            val result = repository.getExchangeRate(base, target)
            if(result.isSuccess){
                val rate = result.getOrNull()
                if (rate != null) {
                    val convertedAmount = String.format("%.5f", amount * rate)
                    conversionResult.postValue(convertedAmount)
                }
            } else {
                if (result.exceptionOrNull() is HttpException) {
                    val code = (result.exceptionOrNull() as HttpException).code()
                    if (code == 422) {
                        errorMessage.postValue("Unsupported currency")
                    } else {
                        errorMessage.postValue("An error occurred")
                    }
                } else {
                    Log.e("CurrencyViewModel", "An error occurred", result.exceptionOrNull())
                    val error = result.exceptionOrNull()?.message ?: "Unknown error occurred"
                    errorMessage.postValue(error)
                }
            }
        }
    }
}