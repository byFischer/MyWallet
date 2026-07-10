package com.example.mywallet.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "installments")
data class Installment(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,
    val totalAmount: Double,
    val installmentCount: Int,
    val startDate: LocalDate,
    val category: String? = null
)
