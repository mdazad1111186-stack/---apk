package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.dialogs.AddEditDepositDialog
import com.example.ui.dialogs.AddEditExpenseDialog
import com.example.ui.dialogs.AddEditMemberDialog
import com.example.ui.dialogs.BackupRestoreDialog
import com.example.ui.dialogs.DeleteConfirmDialog
import com.example.ui.dialogs.MemberProfileDialog
import com.example.ui.dialogs.SheetsExportDialog
import com.example.ui.theme.OrgGreenLight
import com.example.ui.theme.OrgGreenPrimary
import com.example.ui.theme.OrgRedExpense
import com.example.ui.viewmodel.DeleteTarget
import com.example.ui.viewmodel.OrgViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: OrgViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    val members by viewModel.members.collectAsStateWithLifecycle()
    val deposits by viewModel.deposits.collectAsStateWithLifecycle()
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val selectedMonth by viewModel.selectedMonthFilter.collectAsStateWithLifecycle()
    val summary by viewModel.dashboardSummary.collectAsStateWithLifecycle()

    // Dialog states
    val showDepositDialog by viewModel.showDepositDialog.collectAsStateWithLifecycle()
    val depositToEdit by viewModel.depositToEdit.collectAsStateWithLifecycle()

    val showExpenseDialog by viewModel.showExpenseDialog.collectAsStateWithLifecycle()
    val expenseToEdit by viewModel.expenseToEdit.collectAsStateWithLifecycle()

    val showMemberDialog by viewModel.showMemberDialog.collectAsStateWithLifecycle()
    val memberToEdit by viewModel.memberToEdit.collectAsStateWithLifecycle()

    val selectedMemberProfile by viewModel.selectedMemberProfile.collectAsStateWithLifecycle()
    val showSheetsDialog by viewModel.showSheetsDialog.collectAsStateWithLifecycle()
    val showBackupDialog by viewModel.showBackupDialog.collectAsStateWithLifecycle()
    val deleteTarget by viewModel.deleteTarget.collectAsStateWithLifecycle()

    // Toast listener
    LaunchedEffect(Unit) {
        viewModel.toastEvent.collectLatest { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize().testTag("main_screen"),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "দক্ষিণ লতিবপুর যুব উন্নয়ন সংগঠন",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(start = 12.dp, end = 8.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.openSheetsDialog() },
                        modifier = Modifier.testTag("top_bar_sheets_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.TableChart,
                            contentDescription = "গুগল সিট",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(
                        onClick = { viewModel.openBackupDialog() },
                        modifier = Modifier.testTag("top_bar_backup_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Backup,
                            contentDescription = "ব্যাকআপ",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                // 1. Dashboard Tab
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { viewModel.setSelectedTab(0) },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "ড্যাশবোর্ড") },
                    label = { Text("ড্যাশবোর্ড", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_dashboard")
                )

                // 2. Members Tab
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { viewModel.setSelectedTab(1) },
                    icon = {
                        BadgedBox(badge = {
                            if (members.isNotEmpty()) {
                                Badge { Text("${members.size}") }
                            }
                        }) {
                            Icon(Icons.Default.Group, contentDescription = "সদস্যবৃন্দ")
                        }
                    },
                    label = { Text("সদস্যবৃন্দ", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_members")
                )

                // 3. Deposits Tab
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { viewModel.setSelectedTab(2) },
                    icon = { Icon(Icons.Default.ArrowDownward, contentDescription = "জমা") },
                    label = { Text("জমা", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OrgGreenPrimary,
                        selectedTextColor = OrgGreenPrimary
                    ),
                    modifier = Modifier.testTag("nav_deposits")
                )

                // 4. Expenses Tab
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { viewModel.setSelectedTab(3) },
                    icon = { Icon(Icons.Default.ArrowUpward, contentDescription = "খরচ") },
                    label = { Text("খরচ", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = OrgRedExpense,
                        selectedTextColor = OrgRedExpense
                    ),
                    modifier = Modifier.testTag("nav_expenses")
                )

                // 5. Reports & Backup Tab
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { viewModel.setSelectedTab(4) },
                    icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "রিপোর্ট") },
                    label = { Text("রিপোর্ট", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_reports")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> DashboardScreen(
                    summary = summary,
                    selectedMonth = selectedMonth,
                    deposits = deposits,
                    expenses = expenses,
                    onMonthSelect = { viewModel.setSelectedMonthFilter(it) },
                    onAddDeposit = { viewModel.openAddDepositDialog() },
                    onAddExpense = { viewModel.openAddExpenseDialog() },
                    onAddMember = { viewModel.openAddMemberDialog() },
                    onOpenSheets = { viewModel.openSheetsDialog() },
                    onOpenBackup = { viewModel.openBackupDialog() },
                    onEditDeposit = { viewModel.openEditDepositDialog(it) },
                    onDeleteDeposit = { viewModel.confirmDelete(DeleteTarget.Deposit(it)) },
                    onEditExpense = { viewModel.openEditExpenseDialog(it) },
                    onDeleteExpense = { viewModel.confirmDelete(DeleteTarget.Expense(it)) },
                    onNavigateToDeposits = { viewModel.setSelectedTab(2) },
                    onNavigateToExpenses = { viewModel.setSelectedTab(3) }
                )

                1 -> MembersScreen(
                    members = members,
                    deposits = deposits,
                    selectedMonth = selectedMonth,
                    onAddMember = { viewModel.openAddMemberDialog() },
                    onViewProfile = { viewModel.openMemberProfile(it) },
                    onQuickDeposit = { member -> viewModel.openAddDepositDialog(preselectedMemberId = member.id) },
                    onEditMember = { viewModel.openEditMemberDialog(it) },
                    onDeleteMember = { viewModel.confirmDelete(DeleteTarget.MemberItem(it)) }
                )

                2 -> DepositsScreen(
                    deposits = deposits,
                    selectedMonth = selectedMonth,
                    onMonthSelect = { viewModel.setSelectedMonthFilter(it) },
                    onAddDeposit = { viewModel.openAddDepositDialog() },
                    onEditDeposit = { viewModel.openEditDepositDialog(it) },
                    onDeleteDeposit = { viewModel.confirmDelete(DeleteTarget.Deposit(it)) }
                )

                3 -> ExpensesScreen(
                    expenses = expenses,
                    selectedMonth = selectedMonth,
                    onMonthSelect = { viewModel.setSelectedMonthFilter(it) },
                    onAddExpense = { viewModel.openAddExpenseDialog() },
                    onEditExpense = { viewModel.openEditExpenseDialog(it) },
                    onDeleteExpense = { viewModel.confirmDelete(DeleteTarget.Expense(it)) }
                )

                4 -> ReportsBackupScreen(
                    summary = summary,
                    selectedMonth = selectedMonth,
                    members = members,
                    deposits = deposits,
                    expenses = expenses,
                    onOpenSheets = { viewModel.openSheetsDialog() },
                    onOpenBackup = { viewModel.openBackupDialog() }
                )
            }
        }
    }

    // Dialogs
    if (showDepositDialog) {
        AddEditDepositDialog(
            deposit = depositToEdit,
            members = members,
            onDismiss = { viewModel.closeDepositDialog() },
            onSave = { viewModel.saveDeposit(it) }
        )
    }

    if (showExpenseDialog) {
        AddEditExpenseDialog(
            expense = expenseToEdit,
            onDismiss = { viewModel.closeExpenseDialog() },
            onSave = { viewModel.saveExpense(it) }
        )
    }

    if (showMemberDialog) {
        AddEditMemberDialog(
            member = memberToEdit,
            onDismiss = { viewModel.closeMemberDialog() },
            onSave = { viewModel.saveMember(it) }
        )
    }

    if (selectedMemberProfile != null) {
        val memberDeposits = remember(deposits, selectedMemberProfile) {
            deposits.filter { it.memberId == selectedMemberProfile?.id }
        }
        MemberProfileDialog(
            member = selectedMemberProfile,
            memberDeposits = memberDeposits,
            onDismiss = { viewModel.closeMemberProfile() },
            onAddDeposit = { member ->
                viewModel.openAddDepositDialog(preselectedMemberId = member.id)
            },
            onEditMember = { member ->
                viewModel.openEditMemberDialog(member)
            },
            onEditDeposit = { dep ->
                viewModel.openEditDepositDialog(dep)
            },
            onDeleteDeposit = { dep ->
                viewModel.confirmDelete(DeleteTarget.Deposit(dep))
            }
        )
    }

    if (showSheetsDialog) {
        SheetsExportDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.closeSheetsDialog() }
        )
    }

    if (showBackupDialog) {
        BackupRestoreDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.closeBackupDialog() }
        )
    }

    if (deleteTarget != null) {
        DeleteConfirmDialog(
            target = deleteTarget,
            onDismiss = { viewModel.cancelDelete() },
            onConfirm = { viewModel.executeDelete() }
        )
    }
}
