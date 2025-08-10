package com.example.fridgebuddy.database.model

data class FilterOption(
    val id: String,
    val label: String,
    val isSelected: Boolean = false
)