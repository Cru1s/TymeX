package com.example.currencyconverter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.currencyconverter.data.CurrencyRepository
import com.example.currencyconverter.data.model.TravelEstimator
import kotlinx.coroutines.launch

class TravelEstimatorViewModel(private val repository: CurrencyRepository) : ViewModel() {

    // LiveData to hold the list of travel estimators
    private val _estimators = MutableLiveData<List<TravelEstimator>>(emptyList())
    val estimators: LiveData<List<TravelEstimator>> get() = _estimators

    val errorMessage = MutableLiveData<String>()

    private var nextId = 1 // Counter to assign unique IDs to new estimators

    // Adds a new travel estimator to the list
    fun addEstimator(destination: String, duration: Int, dailyBudget: Double, baseCurrency: String) {
        val totalCost = duration * dailyBudget
        val newEstimator = TravelEstimator(nextId++, destination, duration, dailyBudget, baseCurrency, totalCost)
        val updatedList = _estimators.value?.toMutableList() ?: mutableListOf()
        updatedList.add(newEstimator)
        _estimators.value = updatedList
    }

    // Updates an existing estimator's details
    fun updateEstimator(estimator: TravelEstimator) {
        val updatedList = _estimators.value?.map {
            if (it.id == estimator.id) estimator else it // Replace matching estimator
        } ?: emptyList()
        _estimators.postValue(updatedList)
    }

    // Deletes estimators that are marked as checked
    fun deleteSelectedEstimators() {
        val currentList = _estimators.value?.toMutableList() ?: mutableListOf()
        currentList.removeAll { it.isChecked }
        _estimators.postValue(currentList)
    }

    // Verifies if a given currency is supported by checking its exchange rate
    fun isCurrencySupported(currency: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = repository.getExchangeRate(currency, "USD")
            callback(result.isSuccess && result.getOrNull() != null)
        }
    }

    // Converts the total cost of an estimator to a target currency
    fun convertTotalCost(estimator: TravelEstimator, targetCurrency: String) {
        viewModelScope.launch {
            val result = repository.getExchangeRate(estimator.baseCurrency, targetCurrency)
            if (result.isSuccess) {
                val rate = result.getOrDefault(0.0)
                if (rate != 0.0) {
                    estimator.convertedTotalCost = estimator.totalCost * rate
                    updateEstimator(estimator) // Update with the converted cost
                } else {
                    errorMessage.postValue("Conversion failed") // Handle invalid conversion rate
                }
            } else {
                errorMessage.postValue(result.exceptionOrNull()?.message ?: "Unknown error occurred")
            }
        }
    }
}
