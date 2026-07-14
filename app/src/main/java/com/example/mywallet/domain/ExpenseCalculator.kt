package com.example.mywallet.domain

import com.example.mywallet.data.model.Installment
import com.example.mywallet.data.model.Subscription
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

object ExpenseCalculator {

    private val trLocale = Locale("tr", "TR")

    fun monthlyAmount(subscription: Subscription) : Double{
        return when (subscription.billingCycle){
            "YEARLY" -> subscription.amount / 12
            "WEEKLY" -> subscription.amount * 4.33
            else -> subscription.amount
        }
    }

    fun calculateStartDate(
        currentInstallment: Int,
        referenceMonth: YearMonth = YearMonth.now()
    ): LocalDate {
        require(currentInstallment > 0)

        return referenceMonth
            .minusMonths((currentInstallment - 1).toLong())
            .atDay(1)
    }

    fun currentInstallmentNumber(
        installment: Installment,
        referenceMonth: YearMonth = YearMonth.now()
    ): Int? {
        val startMonth = YearMonth.from(installment.startDate)
        val elapsedMonths = ChronoUnit.MONTHS
            .between(startMonth, referenceMonth)
            .toInt()
        val currentNumber = elapsedMonths + 1

        return currentNumber.takeIf { it in 1..installment.installmentCount }
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

    /**
     * Bir taksitin belirli bir ay içinde hâlâ ödeniyor olup olmadığını döndürür.
     * Başladığı aydan itibaren [installmentCount] ay boyunca aktiftir.
     */
    fun isInstallmentActiveInMonth(installment: Installment, month: YearMonth): Boolean {
        val startMonth = YearMonth.from(installment.startDate)
        val endMonthExclusive = startMonth.plusMonths(installment.installmentCount.toLong())
        return !month.isBefore(startMonth) && month.isBefore(endMonthExclusive)
    }

    /**
     * Verilen ay için tahmini toplam gideri hesaplar.
     * Abonelikler her ay tekrarlanır; taksitler yalnızca o ay hâlâ devam ediyorsa dâhil edilir.
     * Böylece ilerideki aylarda biten taksitler otomatik olarak düşer.
     */
    fun totalExpenseForMonth(
        subscriptions: List<Subscription>,
        installments: List<Installment>,
        month: YearMonth
    ): Double {
        val subscriptionTotal = subscriptions.sumOf { monthlyAmount(it) }
        val installmentTotal = installments
            .filter { isInstallmentActiveInMonth(it, month) }
            .sumOf { monthlyAmount(it) }
        return subscriptionTotal + installmentTotal
    }

    /** O ay içinde biten (son ödemesi bu ay olan) taksit sayısı. */
    fun installmentsEndingInMonth(installments: List<Installment>, month: YearMonth): Int {
        return installments.count { installment ->
            val startMonth = YearMonth.from(installment.startDate)
            val lastActiveMonth = startMonth.plusMonths((installment.installmentCount - 1).toLong())
            lastActiveMonth == month
        }
    }

    /** "Temmuz 2026" gibi Türkçe ay + yıl etiketi üretir. */
    fun monthLabel(month: YearMonth): String {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", trLocale)
        return month.atDay(1).format(formatter)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(trLocale) else it.toString() }
    }


}
