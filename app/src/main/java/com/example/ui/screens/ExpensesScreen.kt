package com.example.ui.screens

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
import com.example.data.model.ExpenseTransaction
import com.example.ui.components.ExpenseCardItem
import com.example.ui.theme.OrgRedExpense
import com.example.ui.theme.OrgRedExpenseLight
import com.example.util.Formatters

@Composable
fun ExpensesScreen(
    expenses: List<ExpenseTransaction>,
    selectedMonth: String,
    onMonthSelect: (String) -> Unit,
    onAddExpense: () -> Unit,
    onEditExpense: (ExpenseTransaction) -> Unit,
    onDeleteExpense: (ExpenseTransaction) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("ALL") }

    val months = remember { listOf("ALL") + Formatters.getRecentMonthCodes() }
    val categories = remember {
        listOf(
            "ALL",
            "সমাজকল্যাণ",
            "শিক্ষা ও ক্রীড়া",
            "চিকিৎসা সহায়তা",
            "ত্রাণ বিতরণ",
            "অফিস ও স্টেশনারি",
            "আপ্যায়ন",
            "অন্যান্য"
        )
    }

    val filteredExpenses = remember(expenses, selectedMonth, searchQuery, selectedCategoryFilter) {
        expenses.filter { exp ->
            val matchesMonth = (selectedMonth == "ALL") || (exp.targetMonth == selectedMonth)
            val matchesCategory = (selectedCategoryFilter == "ALL") || (exp.category.contains(selectedCategoryFilter))
            val matchesSearch = searchQuery.isBlank() ||
                    exp.title.contains(searchQuery, ignoreCase = true) ||
                    exp.category.contains(searchQuery, ignoreCase = true) ||
                    exp.spentBy.contains(searchQuery, ignoreCase = true) ||
                    exp.voucherNo.contains(searchQuery, ignoreCase = true) ||
                    exp.note.contains(searchQuery, ignoreCase = true)

            matchesMonth && matchesCategory && matchesSearch
        }
    }

    val totalAmount = remember(filteredExpenses) {
        filteredExpenses.sumOf { it.amount }
    }

    Scaffold(
        modifier = modifier.fillMaxSize().testTag("expenses_screen"),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddExpense,
                containerColor = OrgRedExpense,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("fab_add_expense")
            ) {
                Icon(Icons.Default.Add, contentDescription = "নতুন খরচ যোগ করুন")
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
                    placeholder = { Text("খরচের বিবরণ, খাত বা ভাউচার দিয়ে খুঁজুন...") },
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
                        .testTag("expenses_search_bar"),
                    singleLine = true
                )
            }

            // 2. Month Filter Chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
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
                                selectedContainerColor = OrgRedExpenseLight,
                                selectedLabelColor = OrgRedExpense
                            )
                        )
                    }
                }
            }

            // 3. Category Filter Chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 2.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = (selectedCategoryFilter == cat)
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategoryFilter = cat },
                            label = {
                                Text(if (cat == "ALL") "সকল খাত" else cat)
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                selectedLabelColor = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }

            // 4. Summary Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = OrgRedExpenseLight)
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
                                text = if (selectedMonth == "ALL") "সর্বমোট খরচ" else "${Formatters.formatMonthBangla(selectedMonth)} খরচ",
                                style = MaterialTheme.typography.bodySmall.copy(color = OrgRedExpense)
                            )
                            Text(
                                text = Formatters.formatTaka(totalAmount),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = OrgRedExpense
                                )
                            )
                        }

                        Text(
                            text = "মোট ভাউচার: ${filteredExpenses.size} টি",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = OrgRedExpense
                            )
                        )
                    }
                }
            }

            // 5. Expenses List
            if (filteredExpenses.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchQuery.isNotEmpty()) "কোন খরচের তথ্য খুঁজে পাওয়া যায়নি।" else "এই মাসের কোন খরচের রেকর্ড নেই। '+' বাটনে চাপ দিয়ে নতুন খরচ যোগ করুন।",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(filteredExpenses, key = { it.id }) { expense ->
                    ExpenseCardItem(
                        expense = expense,
                        onEdit = onEditExpense,
                        onDelete = onDeleteExpense
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
