package com.example.ui.viewmodel

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.DepositTransaction
import com.example.data.model.ExpenseTransaction
import com.example.data.model.Member
import com.example.data.repository.OrgRepository
import com.example.util.Formatters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class DeleteTarget {
    data class Deposit(val deposit: DepositTransaction) : DeleteTarget()
    data class Expense(val expense: ExpenseTransaction) : DeleteTarget()
    data class MemberItem(val member: Member) : DeleteTarget()
}

data class DashboardSummary(
    val totalDepositAllTime: Double = 0.0,
    val totalExpenseAllTime: Double = 0.0,
    val currentBalance: Double = 0.0,
    val totalDepositThisMonth: Double = 0.0,
    val totalExpenseThisMonth: Double = 0.0,
    val netThisMonth: Double = 0.0,
    val memberCount: Int = 0,
    val paidMembersCountThisMonth: Int = 0
)

class OrgViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: OrgRepository
    init {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        repository = OrgRepository(db)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                AppDatabase.populateInitialData(db)
            } catch (_: Exception) {}
        }
    }

    val members: StateFlow<List<Member>> = repository.allMembers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val deposits: StateFlow<List<DepositTransaction>> = repository.allDeposits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenses: StateFlow<List<ExpenseTransaction>> = repository.allExpenses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // UI Navigation & Filters
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _selectedMonthFilter = MutableStateFlow(Formatters.getCurrentMonthCode())
    val selectedMonthFilter: StateFlow<String> = _selectedMonthFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Dialog & UI State
    private val _depositToEdit = MutableStateFlow<DepositTransaction?>(null)
    val depositToEdit: StateFlow<DepositTransaction?> = _depositToEdit.asStateFlow()
    private val _showDepositDialog = MutableStateFlow(false)
    val showDepositDialog: StateFlow<Boolean> = _showDepositDialog.asStateFlow()

    private val _expenseToEdit = MutableStateFlow<ExpenseTransaction?>(null)
    val expenseToEdit: StateFlow<ExpenseTransaction?> = _expenseToEdit.asStateFlow()
    private val _showExpenseDialog = MutableStateFlow(false)
    val showExpenseDialog: StateFlow<Boolean> = _showExpenseDialog.asStateFlow()

    private val _memberToEdit = MutableStateFlow<Member?>(null)
    val memberToEdit: StateFlow<Member?> = _memberToEdit.asStateFlow()
    private val _showMemberDialog = MutableStateFlow(false)
    val showMemberDialog: StateFlow<Boolean> = _showMemberDialog.asStateFlow()

    private val _selectedMemberProfile = MutableStateFlow<Member?>(null)
    val selectedMemberProfile: StateFlow<Member?> = _selectedMemberProfile.asStateFlow()

    private val _deleteTarget = MutableStateFlow<DeleteTarget?>(null)
    val deleteTarget: StateFlow<DeleteTarget?> = _deleteTarget.asStateFlow()

    private val _showSheetsDialog = MutableStateFlow(false)
    val showSheetsDialog: StateFlow<Boolean> = _showSheetsDialog.asStateFlow()

    private val _showBackupDialog = MutableStateFlow(false)
    val showBackupDialog: StateFlow<Boolean> = _showBackupDialog.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    // Dashboard calculations
    val dashboardSummary: StateFlow<DashboardSummary> = combine(
        deposits,
        expenses,
        members,
        _selectedMonthFilter
    ) { allDep, allExp, allMemb, currentMonth ->
        val totalDepAll = allDep.sumOf { it.amount }
        val totalExpAll = allExp.sumOf { it.amount }
        val balance = totalDepAll - totalExpAll

        val monthDep = allDep.filter { it.targetMonth == currentMonth }.sumOf { it.amount }
        val monthExp = allExp.filter { it.targetMonth == currentMonth }.sumOf { it.amount }
        val netMonth = monthDep - monthExp

        val paidMemberIdsThisMonth = allDep.filter { it.targetMonth == currentMonth }.map { it.memberId }.toSet()
        val paidCount = allMemb.count { paidMemberIdsThisMonth.contains(it.id) }

        DashboardSummary(
            totalDepositAllTime = totalDepAll,
            totalExpenseAllTime = totalExpAll,
            currentBalance = balance,
            totalDepositThisMonth = monthDep,
            totalExpenseThisMonth = monthExp,
            netThisMonth = netMonth,
            memberCount = allMemb.size,
            paidMembersCountThisMonth = paidCount
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardSummary())

    fun setSelectedTab(tab: Int) {
        _selectedTab.value = tab
    }

    fun setSelectedMonthFilter(month: String) {
        _selectedMonthFilter.value = month
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Deposit actions
    fun openAddDepositDialog(preselectedMemberId: Long = 0) {
        _depositToEdit.value = if (preselectedMemberId > 0) {
            val member = members.value.find { it.id == preselectedMemberId }
            DepositTransaction(
                memberId = preselectedMemberId,
                memberName = member?.name ?: "",
                amount = member?.monthlyFee ?: 200.0,
                targetMonth = _selectedMonthFilter.value,
                date = Formatters.getTodayDate()
            )
        } else {
            DepositTransaction(
                targetMonth = _selectedMonthFilter.value,
                date = Formatters.getTodayDate(),
                amount = 200.0,
                memberName = ""
            )
        }
        _showDepositDialog.value = true
    }

    fun openEditDepositDialog(deposit: DepositTransaction) {
        _depositToEdit.value = deposit
        _showDepositDialog.value = true
    }

    fun closeDepositDialog() {
        _showDepositDialog.value = false
        _depositToEdit.value = null
    }

    fun saveDeposit(deposit: DepositTransaction) {
        viewModelScope.launch {
            if (deposit.id == 0L) {
                repository.insertDeposit(deposit)
                emitToast("জমা সফলভাবে যুক্ত হয়েছে")
            } else {
                repository.updateDeposit(deposit)
                emitToast("জমার তথ্য আপডেট করা হয়েছে")
            }
            closeDepositDialog()
        }
    }

    // Expense actions
    fun openAddExpenseDialog() {
        _expenseToEdit.value = ExpenseTransaction(
            targetMonth = _selectedMonthFilter.value,
            date = Formatters.getTodayDate(),
            amount = 0.0,
            title = ""
        )
        _showExpenseDialog.value = true
    }

    fun openEditExpenseDialog(expense: ExpenseTransaction) {
        _expenseToEdit.value = expense
        _showExpenseDialog.value = true
    }

    fun closeExpenseDialog() {
        _showExpenseDialog.value = false
        _expenseToEdit.value = null
    }

    fun saveExpense(expense: ExpenseTransaction) {
        viewModelScope.launch {
            if (expense.id == 0L) {
                repository.insertExpense(expense)
                emitToast("খরচের হিসাব সফলভাবে যুক্ত হয়েছে")
            } else {
                repository.updateExpense(expense)
                emitToast("খরচের হিসাব আপডেট করা হয়েছে")
            }
            closeExpenseDialog()
        }
    }

    // Member actions
    fun openAddMemberDialog() {
        _memberToEdit.value = Member(
            name = "",
            joinDate = Formatters.getTodayDate(),
            monthlyFee = 200.0
        )
        _showMemberDialog.value = true
    }

    fun openEditMemberDialog(member: Member) {
        _memberToEdit.value = member
        _showMemberDialog.value = true
    }

    fun closeMemberDialog() {
        _showMemberDialog.value = false
        _memberToEdit.value = null
    }

    fun saveMember(member: Member) {
        viewModelScope.launch {
            if (member.id == 0L) {
                repository.insertMember(member)
                emitToast("নতুন সদস্য যুক্ত করা হয়েছে")
            } else {
                repository.updateMember(member)
                // Also update profile view if open
                if (_selectedMemberProfile.value?.id == member.id) {
                    _selectedMemberProfile.value = member
                }
                emitToast("সদস্যের তথ্য আপডেট করা হয়েছে")
            }
            closeMemberDialog()
        }
    }

    // Member Profile Sheet
    fun openMemberProfile(member: Member) {
        _selectedMemberProfile.value = member
    }

    fun closeMemberProfile() {
        _selectedMemberProfile.value = null
    }

    // Delete actions
    fun confirmDelete(target: DeleteTarget) {
        _deleteTarget.value = target
    }

    fun cancelDelete() {
        _deleteTarget.value = null
    }

    fun executeDelete() {
        val target = _deleteTarget.value ?: return
        viewModelScope.launch {
            when (target) {
                is DeleteTarget.Deposit -> {
                    repository.deleteDeposit(target.deposit)
                    emitToast("জমা মুছে ফেলা হয়েছে")
                }
                is DeleteTarget.Expense -> {
                    repository.deleteExpense(target.expense)
                    emitToast("খরচ মুছে ফেলা হয়েছে")
                }
                is DeleteTarget.MemberItem -> {
                    repository.deleteMember(target.member)
                    if (_selectedMemberProfile.value?.id == target.member.id) {
                        _selectedMemberProfile.value = null
                    }
                    emitToast("সদস্যকে তালিকা থেকে সরানো হয়েছে")
                }
            }
            _deleteTarget.value = null
        }
    }

    // Sheets Export
    fun openSheetsDialog() {
        _showSheetsDialog.value = true
    }

    fun closeSheetsDialog() {
        _showSheetsDialog.value = false
    }

    fun generateGoogleSheetsCsv(month: String): String {
        val filteredDep = if (month == "ALL") deposits.value else deposits.value.filter { it.targetMonth == month }
        val filteredExp = if (month == "ALL") expenses.value else expenses.value.filter { it.targetMonth == month }
        return repository.generateMonthlyGoogleSheetsCsv(
            month = if (month == "ALL") "সকল মাসের সামগ্রিক হিসাব" else Formatters.formatMonthBangla(month),
            deposits = filteredDep,
            expenses = filteredExp,
            members = members.value
        )
    }

    fun copySheetsCsvToClipboard(context: Context, month: String) {
        val csv = generateGoogleSheetsCsv(month)
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("লতিবপুর যুব সংগঠন হিসাব - গুগল সিট", csv)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "গুগল সিটের ডেটা ক্লিপবোর্ডে কপি করা হয়েছে! সিটে সরাসরি পেস্ট করুন।", Toast.LENGTH_LONG).show()
    }

    fun shareSheetsCsv(context: Context, month: String) {
        val csv = generateGoogleSheetsCsv(month)
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, csv)
            putExtra(Intent.EXTRA_TITLE, "দক্ষিণ লতিবপুর যুব উন্নয়ন সংগঠন - ${Formatters.formatMonthBangla(month)} হিসাব")
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "গুগল সিট / ইমেইল / হোয়াটসঅ্যাপে হিসাব শেয়ার করুন")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    // Backup & Restore
    fun openBackupDialog() {
        _showBackupDialog.value = true
    }

    fun closeBackupDialog() {
        _showBackupDialog.value = false
    }

    fun generateJsonBackup(): String {
        return repository.createJsonBackup(
            members = members.value,
            deposits = deposits.value,
            expenses = expenses.value
        )
    }

    fun copyBackupToClipboard(context: Context) {
        val json = generateJsonBackup()
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("সংগঠন ডাটা ব্যাকআপ", json)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "সম্পূর্ণ ডাটা ব্যাকআপ ক্লিপবোর্ডে কপি করা হয়েছে!", Toast.LENGTH_LONG).show()
    }

    fun shareBackup(context: Context) {
        val json = generateJsonBackup()
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, json)
            putExtra(Intent.EXTRA_TITLE, "লতিবপুর যুব উন্নয়ন সংগঠন - ডাটা ব্যাকআপ")
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "ব্যাকআপ শেয়ার বা সংরক্ষণ করুন")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    fun restoreBackup(json: String, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.restoreFromJson(json)
            result.onSuccess { msg ->
                emitToast(msg)
                onComplete(true, msg)
            }.onFailure { err ->
                val msg = "রিস্টোর ব্যর্থ হয়েছে: ${err.localizedMessage}"
                emitToast(msg)
                onComplete(false, msg)
            }
        }
    }

    fun resetToSampleData() {
        viewModelScope.launch {
            repository.resetWithSampleData()
            emitToast("নমুনা ডাটা সফলভাবে সেট করা হয়েছে")
        }
    }

    private suspend fun emitToast(message: String) {
        _toastEvent.emit(message)
    }
}
