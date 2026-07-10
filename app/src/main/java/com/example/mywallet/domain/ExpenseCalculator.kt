package com.example.mywallet.domain

import com.example.mywallet.data.model.Installment
import com.example.mywallet.data.model.Subscription
import java.time.LocalDate
import java.time.temporal.ChronoUnit

object ExpenseCalculator {
    fun monthlyAmount(subscription: Subscription) : Double{
        return when (subscription.billingCycle){
            "YEARLY" -> subscription.amount / 12
            "WEEKLY" -> subscription.amount * 4.33
            else -> subscription.amount
        }
    }

    fun endDate(installment: Installment) : LocalDate {
        return installment.startDate.plusMonths(installment.installmentCount.toLong())
    }
    fun isInstallmentActive(installment: Installment, referenceDate: LocalDate = LocalDate.now()): Boolean{
        return referenceDate.isBefore(endDate(installment))
    }
    fun monthlyAmount(installment: Installment): Double{
        return installment.totalAmount / installment.installmentCount
    }
    fun remainingInstallments(installment: Installment, referenceDate: LocalDate = LocalDate.now()): Int {
        val elapsed = ChronoUnit.MONTHS.between(installment.startDate, referenceDate).toInt()
        val remaining = installment.installmentCount - elapsed
        return remaining.coerceAtLeast(0)
    }
}