package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DepositTransaction
import com.example.data.model.ExpenseTransaction
import com.example.ui.components.DepositCardItem
import com.example.ui.components.ExpenseCardItem
import com.example.ui.components.MainBalanceCard
import com.example.ui.components.MonthlyBreakdownCard
import com.example.ui.components.QuickActionsRow
import com.example.ui.theme.OrgGreenPrimary
import com.example.ui.theme.OrgRedExpense
import com.example.ui.viewmodel.DashboardSummary
import com.example.util.Formatters

@Composable
fun DashboardScreen(
    summary: DashboardSummary,
    selectedMonth: String,
    deposits: List<DepositTransaction>,
    expenses: List<ExpenseTransaction>,
    onMonthSelect: (String) -> Unit,
    onAddDeposit: () -> Unit,
    onAddExpense: () -> Unit,
    onAddMember: () -> Unit,
    onOpenSheets: () -> Unit,
    onOpenBackup: () -> Unit,
    onEditDeposit: (DepositTransaction) -> Unit,
    onDeleteDeposit: (DepositTransaction) -> Unit,
    onEditExpense: (ExpenseTransaction) -> Unit,
    onDeleteExpense: (ExpenseTransaction) -> Unit,
    onNavigateToDeposits: () -> Unit,
    onNavigateToExpenses: () -> Unit,
    modifier: Modifier = Modifier
) {
    var recentTab by remember { mutableStateOf(0) } // 0 = সাম্প্রতিক জমা, 1 = সাম্প্রতিক খরচ

    val recentDeposits = remember(deposits) { deposits.take(5) }
    val recentExpenses = remember(expenses) { expenses.take(5) }

    // Category breakdown calculation for expenses
    val categoryExpenses = remember(expenses, selectedMonth) {
        val filtered = expenses.filter { it.targetMonth == selectedMonth }
        val total = filtered.sumOf { it.amount }
        if (total == 0.0) emptyList()
        else {
            filtered.groupBy { it.category }
                .map { (cat, list) ->
                    val sum = list.sumOf { it.amount }
                    Triple(cat, sum, (sum / total).toFloat())
                }
                .sortedByDescending { it.second }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen")
    ) {
        // 1. Main Balance Hero Card
        item {
            MainBalanceCard(
                summary = summary,
                selectedMonth = selectedMonth,
                onMonthSelect = onMonthSelect
            )
        }

        // 2. Quick Actions
        item {
            QuickActionsRow(
                onAddDeposit = onAddDeposit,
                onAddExpense = onAddExpense,
                onAddMember = onAddMember,
                onOpenSheets = onOpenSheets,
                onOpenBackup = onOpenBackup
            )
        }

        // 3. Monthly Breakdown Card
        item {
            MonthlyBreakdownCard(
                summary = summary,
                selectedMonth = selectedMonth
            )
        }

        // 4. Category-wise Expense Progress (if any in selected month)
        if (categoryExpenses.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PieChart,
                                contentDescription = null,
                                tint = OrgRedExpense,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${Formatters.formatMonthBangla(selectedMonth)} খরচের খাতভিত্তিক বিভাজন",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        categoryExpenses.forEach { (cat, sum, ratio) ->
                            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = cat, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium))
                                    Text(
                                        text = "${Formatters.formatTaka(sum)} (${(ratio * 100).toInt()}%)",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = OrgRedExpense)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { ratio },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = OrgRedExpense,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. Recent Activity Section Header & Tabs
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "সাম্প্রতিক লেনদেন",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }

                    TextButton(
                        onClick = {
                            if (recentTab == 0) onNavigateToDeposits() else onNavigateToExpenses()
                        }
                    ) {
                        Text(text = "সবগুলো দেখুন", fontSize = 12.sp)
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                    }
                }

                TabRow(
                    selectedTabIndex = recentTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clip(RoundedCornerShape(10.dp))
                ) {
                    Tab(
                        selected = recentTab == 0,
                        onClick = { recentTab = 0 },
                        text = { Text("জমা (${deposits.size})", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = recentTab == 1,
                        onClick = { recentTab = 1 },
                        text = { Text("খরচ (${expenses.size})", fontWeight = FontWeight.Bold) }
                    )
                }
            }
        }

        // 6. Recent List Items
        if (recentTab == 0) {
            if (recentDeposits.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("এখনও কোন জমার হিসাব যুক্ত করা হয়নি।", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                items(recentDeposits, key = { "dep_${it.id}" }) { dep ->
                    DepositCardItem(
                        deposit = dep,
                        onEdit = onEditDeposit,
                        onDelete = onDeleteDeposit
                    )
                }
            }
        } else {
            if (recentExpenses.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("এখনও কোন খরচের হিসাব যুক্ত করা হয়নি।", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            } else {
                items(recentExpenses, key = { "exp_${it.id}" }) { exp ->
                    ExpenseCardItem(
                        expense = exp,
                        onEdit = onEditExpense,
                        onDelete = onDeleteExpense
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
