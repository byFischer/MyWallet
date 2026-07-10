package com.example.mywallet.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mywallet.data.model.Installment
import com.example.mywallet.data.model.Subscription
import com.example.mywallet.domain.ExpenseCalculator
import com.example.mywallet.viewmodel.DashboardViewModel

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = viewModel(),
    onAddSubscriptionClick: () -> Unit = {},
    onAddInstallmentClick: () -> Unit = {}
) {
    val totalExpense by viewModel.totalMonthlyExpense.collectAsState()
    val subscriptions by viewModel.activeSubscriptions.collectAsState()
    val installments by viewModel.allInstallments.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Abonelikler", "Taksitler")

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (selectedTab == 0) onAddSubscriptionClick() else onAddInstallmentClick()
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ekle")
            }
        }

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(text = "Bu Ayki Toplam Gider", fontSize = 16.sp)

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "%.2f ₺".format(totalExpense),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TabRow(selectedTabIndex = selectedTab) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            HorizontalDivider()

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Spacer(modifier = Modifier.height(16.dp))

                when (selectedTab) {
                    0 -> {
                        if (subscriptions.isEmpty()) {
                            EmptyStateCard()
                        } else {
                            LazyColumn {
                                items(subscriptions, key = { it.id }) { subscription ->
                                    SubscriptionListItem(
                                        subscription = subscription,
                                        onUpdate = { viewModel.updateSubscription(it) },
                                        onDelete = { viewModel.deleteSubscription(it) }
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }

                    1 -> {
                        if (installments.isEmpty()) {
                            EmptyStateCard()
                        } else {
                            LazyColumn {
                                items(installments, key = { it.id }) { installment ->
                                    InstallmentListItem(
                                        installment = installment,
                                        onUpdate = { viewModel.updateInstallment(it) },
                                        onDelete = { viewModel.deleteInstallment(it) }
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateCard() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Henüz veri yok")
            Text(text = "Sağ alttaki '+' butonuyla ekleyebilirsin")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionListItem(
    subscription: Subscription,
    onUpdate: (Subscription) -> Unit,
    onDelete: (Subscription) -> Unit
) {
    var expanded by remember(subscription.id) { mutableStateOf(false) }
    var name by remember(subscription.id) { mutableStateOf(subscription.name) }
    var amount by remember(subscription.id) { mutableStateOf(subscription.amount.toString()) }
    var billingCycle by remember(subscription.id) { mutableStateOf(subscription.billingCycle) }
    var cycleExpanded by remember { mutableStateOf(false) }

    val cycleOptions = listOf("MONTHLY" to "Aylık", "YEARLY" to "Yıllık", "WEEKLY" to "Haftalık")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = subscription.name, fontWeight = FontWeight.Bold)
                    Text(
                        text = when (subscription.billingCycle) {
                            "YEARLY" -> "Yıllık"
                            "WEEKLY" -> "Haftalık"
                            else -> "Aylık"
                        },
                        fontSize = 12.sp
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "%.2f ₺".format(ExpenseCalculator.monthlyAmount(subscription)),
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.Close else Icons.Default.Edit,
                            contentDescription = "Düzenle"
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Abonelik Adı") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Tutar (₺)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ExposedDropdownMenuBox(
                        expanded = cycleExpanded,
                        onExpandedChange = { cycleExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = cycleOptions.first { it.first == billingCycle }.second,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Ödeme Periyodu") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = cycleExpanded,
                            onDismissRequest = { cycleExpanded = false }
                        ) {
                            cycleOptions.forEach { (value, label) ->
                                DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = {
                                        billingCycle = value
                                        cycleExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onDelete(subscription) },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Sil")
                        }
                        Button(
                            onClick = {
                                val amountValue = amount.toDoubleOrNull()
                                if (name.isNotBlank() && amountValue != null) {
                                    onUpdate(
                                        subscription.copy(
                                            name = name,
                                            amount = amountValue,
                                            billingCycle = billingCycle
                                        )
                                    )
                                    expanded = false
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Kaydet")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InstallmentListItem(
    installment: Installment,
    onUpdate: (Installment) -> Unit,
    onDelete: (Installment) -> Unit
) {
    var expanded by remember(installment.id) { mutableStateOf(false) }
    var name by remember(installment.id) { mutableStateOf(installment.name) }
    var totalAmount by remember(installment.id) { mutableStateOf(installment.totalAmount.toString()) }
    var installmentCount by remember(installment.id) { mutableStateOf(installment.installmentCount.toString()) }

    val remaining = ExpenseCalculator.remainingInstallments(installment)
    val endDate = ExpenseCalculator.endDate(installment)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = installment.name, fontWeight = FontWeight.Bold)
                    Text(
                        text = "$remaining ay kaldı · Bitiş: $endDate",
                        fontSize = 12.sp
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "%.2f ₺".format(ExpenseCalculator.monthlyAmount(installment)),
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.Close else Icons.Default.Edit,
                            contentDescription = "Düzenle"
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Taksit Adı") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = totalAmount,
                        onValueChange = { totalAmount = it },
                        label = { Text("Toplam Tutar (₺)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = installmentCount,
                        onValueChange = { installmentCount = it },
                        label = { Text("Taksit Sayısı") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onDelete(installment) },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Sil")
                        }
                        Button(
                            onClick = {
                                val totalValue = totalAmount.toDoubleOrNull()
                                val countValue = installmentCount.toIntOrNull()
                                if (name.isNotBlank() && totalValue != null && countValue != null && countValue > 0) {
                                    onUpdate(
                                        installment.copy(
                                            name = name,
                                            totalAmount = totalValue,
                                            installmentCount = countValue
                                        )
                                    )
                                    expanded = false
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Kaydet")
                        }
                    }
                }
            }
        }
    }
}