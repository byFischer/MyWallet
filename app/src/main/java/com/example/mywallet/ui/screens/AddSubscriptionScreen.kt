package com.example.mywallet.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mywallet.data.model.Subscription
import com.example.mywallet.data.remote.BrandSearchResult
import com.example.mywallet.ui.components.BrandArtwork
import com.example.mywallet.viewmodel.SubscriptionViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSubscriptionScreen(
    modifier: Modifier = Modifier,
    viewModel: SubscriptionViewModel = viewModel(),
    onSubscriptionAdded: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var billingCycle by remember { mutableStateOf("MONTHLY") }
    var expanded by remember { mutableStateOf(false) }
    var brandMenuExpanded by remember { mutableStateOf(false) }
    var selectedBrand by remember { mutableStateOf<BrandSearchResult?>(null) }

    val brandSearchState by viewModel.brandSearchState.collectAsState()
    val showBrandMenu = brandMenuExpanded && brandSearchState.results.isNotEmpty()

    val cycleOptions = listOf("MONTHLY" to "Aylık", "YEARLY" to "Yıllık", "WEEKLY" to "Haftalık")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(text = "Yeni Abonelik", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(24.dp))

        ExposedDropdownMenuBox(
            expanded = showBrandMenu,
            onExpandedChange = {
                brandMenuExpanded = brandSearchState.results.isNotEmpty() && !showBrandMenu
            }
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { newValue ->
                    name = newValue
                    selectedBrand = null
                    brandMenuExpanded = true
                    viewModel.searchBrands(newValue)
                },
                label = { Text("Abonelik hizmeti") },
                placeholder = { Text("Netflix, Spotify, Hepsiburada...") },
                supportingText = {
                    when {
                        selectedBrand != null -> Text(selectedBrand?.domain.orEmpty())
                        !brandSearchState.isConfigured -> {
                            Text("Marka araması kullanılamıyor; adı elle ekleyebilirsin")
                        }
                        brandSearchState.isLoading -> Text("Markalar aranıyor...")
                        brandSearchState.hasError -> {
                            Text("Arama tamamlanamadı; adı elle ekleyebilirsin")
                        }
                        brandSearchState.hasSearched && brandSearchState.results.isEmpty() -> {
                            Text("Marka bulunamadı; bu adla ekleyebilirsin")
                        }
                        else -> Text("Listeden seçersen logosu karta eklenir")
                    }
                },
                trailingIcon = {
                    when {
                        brandSearchState.isLoading -> CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        brandSearchState.results.isNotEmpty() -> {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = showBrandMenu)
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = showBrandMenu,
                onDismissRequest = { brandMenuExpanded = false }
            ) {
                brandSearchState.results.forEach { brand ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                BrandArtwork(
                                    imageUrl = brand.iconUrl,
                                    contentDescription = "${brand.name} logosu",
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = brand.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = brand.domain,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        },
                        onClick = {
                            selectedBrand = brand
                            name = brand.name
                            brandMenuExpanded = false
                            viewModel.clearBrandSearch()
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Tutar (₺)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
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
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                cycleOptions.forEach { (value, label) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            billingCycle = value
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val amountValue = amount.toDoubleOrNull()
                if (name.isNotBlank() && amountValue != null) {
                    viewModel.addSubscription(
                        Subscription(
                            name = selectedBrand?.name ?: name.trim(),
                            amount = amountValue,
                            billingCycle = billingCycle,
                            nextPaymentDate = LocalDate.now().plusMonths(1),
                            providerDomain = selectedBrand?.domain,
                            providerBrandId = selectedBrand?.brandId?.takeIf { it.isNotBlank() }
                        )
                    )
                    onSubscriptionAdded()
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
