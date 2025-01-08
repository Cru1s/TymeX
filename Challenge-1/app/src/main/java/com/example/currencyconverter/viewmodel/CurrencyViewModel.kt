package com.example.currencyconverter.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverter.data.CurrencyRepository
import kotlinx.coroutines.launch

class CurrencyViewModel (private val repository: CurrencyRepository) : ViewModel(){

    val conversionResult = MutableLiveData<String>()

    fun convertCurrency(amount: Double, base: String, target: String){
        viewModelScope.launch {
            val result = repository.getExchangeRate(base, target)
            if(result.isSuccess){
                val rate = result.getOrNull()
                if (rate != null) {
                    val convertedAmount = amount * rate
                    conversionResult.postValue(convertedAmount.toString())
                    Log.d("CurrencyViewModel", "Success: Rate = $rate, Converted Amount = $convertedAmount")
                } else {
                    conversionResult.postValue("Unsupported currency")
                }
            } else {
                conversionResult.postValue("${result.exceptionOrNull()?.message}")
                Log.e("CurrencyViewModel", "${result.exceptionOrNull()?.message}")
            }
        }
    }
}