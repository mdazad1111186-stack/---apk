package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DepositTransaction
import com.example.ui.components.DepositCardItem
import com.example.ui.theme.OrgGreenLight
import com.example.ui.theme.OrgGreenPrimary
import com.example.util.Formatters

@Composable
fun DepositsScreen(
    deposits: List<DepositTransaction>,
    selectedMonth: String,
    onMonthSelect: (String) -> Unit,
    onAddDeposit: () -> Unit,
    onEditDeposit: (DepositTransaction) -> Unit,
    onDeleteDeposit: (DepositTransaction) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val months = remember { listOf("ALL") + Formatters.getRecentMonthCodes() }

    val filteredDeposits = remember(deposits, selectedMonth, searchQuery) {
        deposits.filter { dep ->
            val matchesMonth = (selectedMonth == "ALL") || (dep.targetMonth == selectedMonth)
            val matchesSearch = searchQuery.isBlank() ||
                    dep.memberName.contains(searchQuery, ignoreCase = true) ||
                    dep.receiptNo.contains(searchQuery, ignoreCase = true) ||
                    dep.category.contains(searchQuery, ignoreCase = true) ||
                    dep.note.contains(searchQuery, ignoreCase = true) ||
                    dep.collectedBy.contains(searchQuery, ignoreCase = true)

            matchesMonth && matchesSearch
        }
    }

    val totalAmount = remember(filteredDeposits) {
        filteredDeposits.sumOf { it.amount }
    }

    Scaffold(
        modifier = modifier.fillMaxSize().testTag("deposits_screen"),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddDeposit,
                containerColor = OrgGreenPrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("fab_add_deposit")
            ) {
                Icon(Icons.Default.Add, contentDescription = "নতুন জমা যোগ করুন")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // 1. Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("সদস্যের নাম, রসিদ বা খাত দিয়ে খুঁজুন...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "মুছুন")
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 12.dp)
                        .testTag("deposits_search_bar"),
                    singleLine = true
                )
            }

            // 2. Month Filter Chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    months.forEach { m ->
                        val isSelected = (selectedMonth == m)
                        FilterChip(
                            selected = isSelected,
                            onClick = { onMonthSelect(m) },
                            label = {
                                Text(if (m == "ALL") "সকল মাস" else Formatters.formatMonthBangla(m))
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }

            // 3. Summary Stat Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = OrgGreenLight)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (selectedMonth == "ALL") "সর্বমোট জমা" else "${Formatters.formatMonthBangla(selectedMonth)} জমা",
                                style = MaterialTheme.typography.bodySmall.copy(color = OrgGreenPrimary)
                            )
                            Text(
                                text = Formatters.formatTaka(totalAmount),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OrgGreenPrimary
                                )
                            )
                        }

                        Text(
                            text = "মোট লেনদেন: ${filteredDeposits.size} টি",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = OrgGreenPrimary
                            )
                        )
                    }
                }
            }

            // 4. Deposits List
            if (filteredDeposits.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchQuery.isNotEmpty()) "কোন জমার তথ্য খুঁজে পাওয়া যায়নি।" else "এই মাসের কোন জমার রেকর্ড নেই। '+' বাটনে চাপ দিয়ে নতুন জমা যোগ করুন।",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(filteredDeposits, key = { it.id }) { deposit ->
                    DepositCardItem(
                        deposit = deposit,
                        onEdit = onEditDeposit,
                        onDelete = onDeleteDeposit
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
