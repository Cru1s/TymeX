package com.example.currencyconverter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.currencyconverter.data.CurrencyRepository

// Factory to create instances of TravelEstimatorViewModel
class TravelEstimatorViewModelFactory(private val repository: CurrencyRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Check if the ViewModel class is TravelEstimatorViewModel
        if (modelClass.isAssignableFrom(TravelEstimatorViewModel::class.java)) {
            return TravelEstimatorViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
