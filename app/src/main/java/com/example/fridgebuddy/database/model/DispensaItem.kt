package com.example.fridgebuddy.database.model

data class DispensaItem(
    val id: String = "",
    val idIngrediente: String = "",
    val nomeIngrediente: String = "",
    val idCategoria: String = "",
    val quantita: QuantitaItem = QuantitaItem(),
    val dataScadenza: String = "",
    val imgUrl: String = "",
    val createdAt: String = ""
)

data class QuantitaItem(
    val numero: Int = 0,
    val unita: String = ""
)