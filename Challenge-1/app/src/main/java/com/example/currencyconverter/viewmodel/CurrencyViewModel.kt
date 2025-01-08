package com.example.currencyconverter.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverter.data.CurrencyRepository
import kotlinx.coroutines.launch

class CurrencyViewModel (private val repository: CurrencyRepository) : ViewModel(){

    val conversionResult = MutableLiveData<String>()
    val errorMessage = MutableLiveData<String>()

    fun convertCurrency(amount: Double, base: String, target: String){
        viewModelScope.launch {
            val result = repository.getExchangeRate(base, target)
            if(result.isSuccess){
                val rate = result.getOrDefault(0.0)
                if (rate != 0.0) {
                    val convertedAmount = String.format("%.5f", amount * rate)
                    conversionResult.postValue(convertedAmount)
                    Log.d("CurrencyViewModel", "Success: Rate = $rate, Converted Amount = $convertedAmount")
                } else {
                    errorMessage.postValue("Unsupported currency")
                }
            } else {
                val error = result.exceptionOrNull()?.message ?: "Unknown error occurred"
                errorMessage.postValue(error)
                Log.e("CurrencyViewModel", "${result.exceptionOrNull()?.message}")
            }
        }
    }
}