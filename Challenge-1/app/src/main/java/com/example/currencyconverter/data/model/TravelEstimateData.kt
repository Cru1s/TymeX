package com.example.currencyconverter.data.model

data class TravelEstimator(
    val id: Int,
    val destination: String,
    val duration: Int,
    val dailyBudget: Double,
    val baseCurrency: String,
    var totalCost: Double,
    var convertedTotalCost: Double? = null,
    var isChecked: Boolean = false
)
