package com.example.mywallet.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mywallet.data.model.Installment
import com.example.mywallet.data.model.Subscription
import com.example.mywallet.domain.ExpenseCalculator
import com.example.mywallet.viewmodel.DashboardViewModel
import kotlinx.coroutines.launch
import java.time.YearMonth
import java.util.Locale

private const val MONTH_PAGE_COUNT = 25

private fun formatCurrency(amount: Double): String =
    "%,.2f ₺".format(Locale("tr", "TR"), amount)

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = viewModel(),
    onAddSubscriptionClick: () -> Unit = {},
    onAddInstallmentClick: () -> Unit = {}
) {
    val subscriptions by viewModel.activeSubscriptions.collectAsState()
    val installments by viewModel.allInstallments.collectAsState()

    val currentMonth = remember { YearMonth.now() }
    val monthPagerState = rememberPagerState(pageCount = { MONTH_PAGE_COUNT })
    val tabPagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()

    val tabTitles = listOf("Abonelikler", "Taksitler")

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (tabPagerState.currentPage == 0) onAddSubscriptionClick()
                    else onAddInstallmentClick()
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
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
            MonthExpenseHeader(
                pagerState = monthPagerState,
                subscriptions = subscriptions,
                installments = installments,
                currentMonth = currentMonth
            )

            Spacer(modifier = Modifier.height(8.dp))

            TabRow(
                selectedTabIndex = tabPagerState.currentPage,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                tabTitles.forEachIndexed { index, title ->
                    val selected = tabPagerState.currentPage == index
                    Tab(
                        selected = selected,
                        onClick = { scope.launch { tabPagerState.animateScrollToPage(index) } },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

            HorizontalPager(
                state = tabPagerState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { page ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    when (page) {
                        0 -> SubscriptionsList(
                            subscriptions = subscriptions,
                            onUpdate = { viewModel.updateSubscription(it) },
                            onDelete = { viewModel.deleteSubscription(it) }
                        )

                        1 -> InstallmentsList(
                            installments = installments,
                            onUpdate = { viewModel.updateInstallment(it) },
                            onDelete = { viewModel.deleteInstallment(it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthExpenseHeader(
    pagerState: PagerState,
    subscriptions: List<Subscription>,
    installments: List<Installment>,
    currentMonth: YearMonth
) {
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            val month = currentMonth.plusMonths(page.toLong())
            val total = ExpenseCalculator.totalExpenseForMonth(subscriptions, installments, month)
            ExpenseHeaderPage(
                monthLabel = ExpenseCalculator.monthLabel(month),
                total = total,
                isCurrentMonth = page == 0
            )
        }

        // Önceki aya dön (yalnızca mevcut ayın ilerisindeyken görünür)
        if (pagerState.currentPage > 0) {
            IconButton(
                onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Önceki ay",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Sonraki ayın tahminine geç
        if (pagerState.currentPage < MONTH_PAGE_COUNT - 1) {
            IconButton(
                onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Sonraki ay",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ExpenseHeaderPage(
    monthLabel: String,
    total: Double,
    isCurrentMonth: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = monthLabel,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = formatCurrency(total),
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Sabit yükseklikte alt satır — sayfalar arası zıplamayı önler
        Box(
            modifier = Modifier.height(30.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isCurrentMonth) {
                Text(
                    text = "Bu Ayki Toplam Gider",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "Tahmini Gider",
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SubscriptionsList(
    subscriptions: List<Subscription>,
    onUpdate: (Subscription) -> Unit,
    onDelete: (Subscription) -> Unit
) {
    if (subscriptions.isEmpty()) {
        EmptyState(
            title = "Henüz abonelik yok",
            subtitle = "Sağ alttaki '+' butonuyla ilk aboneliğini ekleyebilirsin"
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(subscriptions, key = { it.id }) { subscription ->
                SubscriptionListItem(
                    subscription = subscription,
                    onUpdate = onUpdate,
                    onDelete = onDelete
                )
            }
        }
    }
}

@Composable
private fun InstallmentsList(
    installments: List<Installment>,
    onUpdate: (Installment) -> Unit,
    onDelete: (Installment) -> Unit
) {
    if (installments.isEmpty()) {
        EmptyState(
            title = "Henüz taksit yok",
            subtitle = "Sağ alttaki '+' butonuyla ilk taksitini ekleyebilirsin"
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(installments, key = { it.id }) { installment ->
                InstallmentListItem(
                    installment = installment,
                    onUpdate = onUpdate,
                    onDelete = onDelete
                )
            }
        }
    }
}

@Composable
private fun EmptyState(title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(72.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "＋",
                    fontSize = 32.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun Avatar(letter: String, containerColor: androidx.compose.ui.graphics.Color, contentColor: androidx.compose.ui.graphics.Color) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(containerColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letter.uppercase(Locale("tr", "TR")),
            color = contentColor,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
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
    val cycleLabel = when (subscription.billingCycle) {
        "YEARLY" -> "Yıllık"
        "WEEKLY" -> "Haftalık"
        else -> "Aylık"
    }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Avatar(
                    letter = subscription.name.take(1).ifBlank { "?" },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = subscription.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = cycleLabel,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formatCurrency(ExpenseCalculator.monthlyAmount(subscription)),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "aylık",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.Close else Icons.Default.Edit,
                        contentDescription = if (expanded) "Kapat" else "Düzenle",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Abonelik Adı") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Tutar (₺)") },
                        singleLine = true,
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
    val paid = (installment.installmentCount - remaining).coerceIn(0, installment.installmentCount)
    val endDate = ExpenseCalculator.endDate(installment)
    val progress = if (installment.installmentCount > 0) {
        paid.toFloat() / installment.installmentCount.toFloat()
    } else 0f
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "progress")

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Avatar(
                    letter = installment.name.take(1).ifBlank { "?" },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = installment.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (remaining > 0) "$remaining ay kaldı" else "Tamamlandı",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = formatCurrency(ExpenseCalculator.monthlyAmount(installment)),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "aylık",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.Close else Icons.Default.Edit,
                        contentDescription = if (expanded) "Kapat" else "Düzenle",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(50)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "$paid / ${installment.installmentCount} ödendi · Bitiş: ${
                    ExpenseCalculator.monthLabel(YearMonth.from(endDate.minusMonths(1)))
                }",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Taksit Adı") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = totalAmount,
                        onValueChange = { totalAmount = it },
                        label = { Text("Toplam Tutar (₺)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = installmentCount,
                        onValueChange = { installmentCount = it },
                        label = { Text("Taksit Sayısı") },
                        singleLine = true,
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
