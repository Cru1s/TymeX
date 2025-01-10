package com.example.currencyconverter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverter.data.CurrencyRepository
import com.example.currencyconverter.data.model.TravelEstimator
import kotlinx.coroutines.launch

class TravelEstimatorViewModel(private val repository: CurrencyRepository) : ViewModel() {

    private val _estimators = MutableLiveData<List<TravelEstimator>>(emptyList())
    val estimators: LiveData<List<TravelEstimator>> get() = _estimators

    val errorMessage = MutableLiveData<String>()

    private var nextId = 1

    fun addEstimator(destination: String, duration: Int, dailyBudget: Double, baseCurrency: String) {
        val totalCost = duration * dailyBudget
        val newEstimator = TravelEstimator(nextId++, destination, duration, dailyBudget, baseCurrency, totalCost)
        val updatedList = _estimators.value?.toMutableList() ?: mutableListOf()
        updatedList.add(newEstimator)
        _estimators.value = updatedList
    }

    fun updateEstimator(estimator: TravelEstimator) {
        val updatedList = _estimators.value?.map {
            if (it.id == estimator.id) estimator else it
        } ?: emptyList()
        _estimators.postValue(updatedList)
    }

    fun deleteSelectedEstimators() {
        val currentList = _estimators.value?.toMutableList() ?: mutableListOf()
        currentList.removeAll { it.isChecked }
        _estimators.postValue(currentList)
    }

    fun isCurrencySupported(currency: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.getExchangeRate(currency, "USD")
            if (result.isSuccess && result.getOrNull() != null) {
                callback(true) // Currency is supported
            } else {
                callback(false) // Currency is unsupported
            }
        }
    }


    fun convertTotalCost(estimator: TravelEstimator, targetCurrency: String){
        viewModelScope.launch {
            val result = repository.getExchangeRate(estimator.baseCurrency, targetCurrency)
            if(result.isSuccess){
                val rate = result.getOrDefault(0.0)
                if(rate != 0.0){
                    estimator.convertedTotalCost = estimator.totalCost * rate
                    updateEstimator(estimator)
                } else {
                    errorMessage.postValue("Conversion failed")
                }
            } else {
                errorMessage.postValue(result.exceptionOrNull()?.message ?: "Unknown error occurred")
            }

        }
    }

}