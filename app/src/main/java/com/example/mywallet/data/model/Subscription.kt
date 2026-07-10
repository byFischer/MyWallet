package com.example.mywallet.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "subscriptions")
data class Subscription(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val name: String,
    val amount: Double,
    val currency: String = "TRY",
    val billingCycle: String,
    val nextPaymentDate: LocalDate,
    val category: String? = null,
    val isActive: Boolean = true
)