package com.example.currencyconverter.data.model

data class HexarateResponse(
    val status_code: Int,
    val data: HexarateData,
)

data class HexarateData(
    val base: String,
    val target: String,
    val mid: Double,
    val unit: Int,
    val timestamp: String,
)
