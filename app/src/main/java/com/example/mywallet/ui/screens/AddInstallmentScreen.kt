package com.example.mywallet.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mywallet.data.model.Installment
import com.example.mywallet.viewmodel.InstallmentViewModel
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import com.example.mywallet.domain.ExpenseCalculator



@Composable
fun AddInstallmentScreen(
    modifier: Modifier = Modifier,
    viewModel: InstallmentViewModel = viewModel(),
    onDone: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var totalAmount by remember { mutableStateOf("") }
    var installmentCount by remember { mutableStateOf("") }
    var currentInstallment by remember { mutableStateOf("1") }

    val totalCount = installmentCount.toIntOrNull()
    val currentCount = currentInstallment.toIntOrNull()

    val currentInstallmentHashError =
        currentInstallment.isNotBlank() &&
                (
                        currentCount == null ||
                        currentCount < 1 ||
                                (totalCount != null && currentCount > totalCount)
                        )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(text = "Yeni Taksit", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Taksit Adı") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = totalAmount,
            onValueChange = { totalAmount = it },
            label = { Text("Toplam Tutar (₺)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = installmentCount,
            onValueChange = { installmentCount = it },
            label = { Text("Taksit Sayısı (ay)") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = currentInstallment,
            onValueChange = {newValue ->
                currentInstallment = newValue.filter { it.isDigit() }
            },
            label = {Text("Bu ay kaçıncı taksit?")},
            isError = currentInstallmentHashError,
            supportingText = {
                if (currentInstallmentHashError) {
                    Text("Değer 1 ile toplam taksit sayısı arasında olmalı")
                }
                else{
                    Text("Örneğin bu ay 4. ödeme yapılacaksa 4 gir")
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()

        )

        Spacer(modifier = Modifier.height(16.dp))

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val totalValue = totalAmount.toDoubleOrNull()
                val countValue = installmentCount.toIntOrNull()
                val currentValue = currentInstallment.toIntOrNull()
                if (
                    name.isNotBlank() &&
                    totalValue != null &&
                    countValue != null &&
                    countValue > 0 &&
                    currentValue != null &&
                    currentValue in 1..countValue
                ) {
                    val calculatedStartDate =
                        ExpenseCalculator.calculateStartDate(currentValue)

                    viewModel.addInstallment(
                        Installment(
                            name = name,
                            totalAmount = totalValue,
                            installmentCount = countValue,
                            startDate = calculatedStartDate
                        )
                    )
                    onDone()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
        ) {
            Text("Kaydet")
        }
    }

}