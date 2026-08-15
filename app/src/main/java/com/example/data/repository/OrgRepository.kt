package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.DepositTransaction
import com.example.data.model.ExpenseTransaction
import com.example.data.model.Member
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject

class OrgRepository(private val database: AppDatabase) {

    private val memberDao = database.memberDao()
    private val depositDao = database.depositDao()
    private val expenseDao = database.expenseDao()

    // Member streams
    val allMembers: Flow<List<Member>> = memberDao.getAllMembers()
    suspend fun getMemberById(id: Long): Member? = memberDao.getMemberById(id)
    fun getMemberFlow(id: Long): Flow<Member?> = memberDao.getMemberFlow(id)
    suspend fun insertMember(member: Member): Long = memberDao.insertMember(member)
    suspend fun updateMember(member: Member) = memberDao.updateMember(member)
    suspend fun deleteMember(member: Member) = memberDao.deleteMember(member)
    suspend fun deleteMemberById(id: Long) = memberDao.deleteMemberById(id)

    // Deposit streams
    val allDeposits: Flow<List<DepositTransaction>> = depositDao.getAllDeposits()
    fun getDepositsByMember(memberId: Long): Flow<List<DepositTransaction>> = depositDao.getDepositsByMember(memberId)
    fun getDepositsByMonth(month: String): Flow<List<DepositTransaction>> = depositDao.getDepositsByMonth(month)
    suspend fun insertDeposit(deposit: DepositTransaction): Long = depositDao.insertDeposit(deposit)
    suspend fun updateDeposit(deposit: DepositTransaction) = depositDao.updateDeposit(deposit)
    suspend fun deleteDeposit(deposit: DepositTransaction) = depositDao.deleteDeposit(deposit)
    suspend fun deleteDepositById(id: Long) = depositDao.deleteDepositById(id)

    // Expense streams
    val allExpenses: Flow<List<ExpenseTransaction>> = expenseDao.getAllExpenses()
    fun getExpensesByMonth(month: String): Flow<List<ExpenseTransaction>> = expenseDao.getExpensesByMonth(month)
    suspend fun insertExpense(expense: ExpenseTransaction): Long = expenseDao.insertExpense(expense)
    suspend fun updateExpense(expense: ExpenseTransaction) = expenseDao.updateExpense(expense)
    suspend fun deleteExpense(expense: ExpenseTransaction) = expenseDao.deleteExpense(expense)
    suspend fun deleteExpenseById(id: Long) = expenseDao.deleteExpenseById(id)

    suspend fun resetWithSampleData() {
        memberDao.clearAllMembers()
        depositDao.clearAllDeposits()
        expenseDao.clearAllExpenses()
        AppDatabase.populateInitialData(database)
    }

    // Google Sheets CSV format export
    fun generateMonthlyGoogleSheetsCsv(
        month: String,
        deposits: List<DepositTransaction>,
        expenses: List<ExpenseTransaction>,
        members: List<Member>
    ): String {
        val sb = StringBuilder()
        sb.append("দক্ষিণ লতিবপুর যুব উন্নয়ন সংগঠন - মাসিক লেনদেন হিসাব\n")
        sb.append("হিসাবের মাস:,$month\n")
        sb.append("প্রস্তুত তারিখ:,${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date())}\n\n")

        val totalDeposits = deposits.sumOf { it.amount }
        val totalExpenses = expenses.sumOf { it.amount }
        val balance = totalDeposits - totalExpenses

        sb.append("সারসংক্ষেপ (Summary)\n")
        sb.append("মোট জমা (টাকা),মোট খরচ (টাকা),মাসিক উদ্বৃত্ত/ঘাটতি (টাকা)\n")
        sb.append("$totalDeposits,$totalExpenses,$balance\n\n")

        sb.append("--- জমার বিবরণী (Collections / Deposits) ---\n")
        sb.append("ক্রমিক,তারিখ,সদস্যের নাম,খাত / ধরণ,টাকা (৳),পেমেন্ট মাধ্যম,রসিদ নং,সংগ্রাহক,মন্তব্য\n")
        deposits.forEachIndexed { index, dep ->
            sb.append("${index + 1},${dep.date},\"${dep.memberName}\",\"${dep.category}\",${dep.amount},\"${dep.paymentMethod}\",\"${dep.receiptNo}\",\"${dep.collectedBy}\",\"${dep.note.replace("\"", "\"\"")}\"\n")
        }
        sb.append(",,,মোট জমা:,$totalDeposits,,,,\n\n")

        sb.append("--- খরচের বিবরণী (Expenses) ---\n")
        sb.append("ক্রমিক,তারিখ,খরচের বিবরণ / খাত,টাকা (৳),খরচকারী,ভাউচার নং,মন্তব্য\n")
        expenses.forEachIndexed { index, exp ->
            sb.append("${index + 1},${exp.date},\"${exp.title} (${exp.category})\",${exp.amount},\"${exp.spentBy}\",\"${exp.voucherNo}\",\"${exp.note.replace("\"", "\"\"")}\"\n")
        }
        sb.append(",,,মোট খরচ:,$totalExpenses,,,\n\n")

        sb.append("--- সদস্যভিত্তিক চাঁদা আদায় স্ট্যাটাস ---\n")
        sb.append("আইডি,সদস্যের নাম,পদবী,মোবাইল,মাসিক নির্ধারিত চাঁদা,এই মাসে প্রদত্ত জমা,বকেয়া / স্থিতি\n")
        members.forEach { m ->
            val paidThisMonth = deposits.filter { it.memberId == m.id }.sumOf { it.amount }
            val due = if (paidThisMonth >= m.monthlyFee) 0.0 else (m.monthlyFee - paidThisMonth)
            val status = if (paidThisMonth >= m.monthlyFee) "পরিশোধিত" else "বকেয়া: ৳$due"
            sb.append("${m.id},\"${m.name}\",\"${m.designation}\",\"${m.phone}\",${m.monthlyFee},$paidThisMonth,\"$status\"\n")
        }

        return sb.toString()
    }

    // Export Complete JSON Backup
    fun createJsonBackup(
        members: List<Member>,
        deposits: List<DepositTransaction>,
        expenses: List<ExpenseTransaction>
    ): String {
        val root = JSONObject()
        root.put("organization", "দক্ষিণ লতিবপুর যুব উন্নয়ন সংগঠন")
        root.put("version", 1)
        root.put("backupDate", System.currentTimeMillis())
        root.put("formattedDate", java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date()))

        val membersArray = JSONArray()
        members.forEach { m ->
            val obj = JSONObject()
            obj.put("id", m.id)
            obj.put("name", m.name)
            obj.put("phone", m.phone)
            obj.put("designation", m.designation)
            obj.put("monthlyFee", m.monthlyFee)
            obj.put("joinDate", m.joinDate)
            obj.put("address", m.address)
            obj.put("bloodGroup", m.bloodGroup)
            obj.put("isActive", m.isActive)
            obj.put("notes", m.notes)
            obj.put("createdAt", m.createdAt)
            membersArray.put(obj)
        }
        root.put("members", membersArray)

        val depositsArray = JSONArray()
        deposits.forEach { d ->
            val obj = JSONObject()
            obj.put("id", d.id)
            obj.put("memberId", d.memberId)
            obj.put("memberName", d.memberName)
            obj.put("amount", d.amount)
            obj.put("category", d.category)
            obj.put("targetMonth", d.targetMonth)
            obj.put("date", d.date)
            obj.put("timestamp", d.timestamp)
            obj.put("paymentMethod", d.paymentMethod)
            obj.put("receiptNo", d.receiptNo)
            obj.put("note", d.note)
            obj.put("collectedBy", d.collectedBy)
            depositsArray.put(obj)
        }
        root.put("deposits", depositsArray)

        val expensesArray = JSONArray()
        expenses.forEach { e ->
            val obj = JSONObject()
            obj.put("id", e.id)
            obj.put("title", e.title)
            obj.put("amount", e.amount)
            obj.put("category", e.category)
            obj.put("targetMonth", e.targetMonth)
            obj.put("date", e.date)
            obj.put("timestamp", e.timestamp)
            obj.put("spentBy", e.spentBy)
            obj.put("voucherNo", e.voucherNo)
            obj.put("note", e.note)
            expensesArray.put(obj)
        }
        root.put("expenses", expensesArray)

        return root.toString(2)
    }

    // Restore from JSON string
    suspend fun restoreFromJson(jsonString: String): Result<String> {
        return try {
            val root = JSONObject(jsonString)
            val membersArray = root.optJSONArray("members") ?: JSONArray()
            val depositsArray = root.optJSONArray("deposits") ?: JSONArray()
            val expensesArray = root.optJSONArray("expenses") ?: JSONArray()

            val members = mutableListOf<Member>()
            for (i in 0 until membersArray.length()) {
                val obj = membersArray.getJSONObject(i)
                members.add(
                    Member(
                        id = obj.optLong("id", 0),
                        name = obj.optString("name", "Unknown"),
                        phone = obj.optString("phone", ""),
                        designation = obj.optString("designation", "সদস্য"),
                        monthlyFee = obj.optDouble("monthlyFee", 200.0),
                        joinDate = obj.optString("joinDate", ""),
                        address = obj.optString("address", "দক্ষিণ লতিবপুর"),
                        bloodGroup = obj.optString("bloodGroup", ""),
                        isActive = obj.optBoolean("isActive", true),
                        notes = obj.optString("notes", ""),
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                    )
                )
            }

            val deposits = mutableListOf<DepositTransaction>()
            for (i in 0 until depositsArray.length()) {
                val obj = depositsArray.getJSONObject(i)
                deposits.add(
                    DepositTransaction(
                        id = obj.optLong("id", 0),
                        memberId = obj.optLong("memberId", 0),
                        memberName = obj.optString("memberName", ""),
                        amount = obj.optDouble("amount", 0.0),
                        category = obj.optString("category", "মাসিক চাঁদা"),
                        targetMonth = obj.optString("targetMonth", ""),
                        date = obj.optString("date", ""),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                        paymentMethod = obj.optString("paymentMethod", "নগদ"),
                        receiptNo = obj.optString("receiptNo", ""),
                        note = obj.optString("note", ""),
                        collectedBy = obj.optString("collectedBy", "")
                    )
                )
            }

            val expenses = mutableListOf<ExpenseTransaction>()
            for (i in 0 until expensesArray.length()) {
                val obj = expensesArray.getJSONObject(i)
                expenses.add(
                    ExpenseTransaction(
                        id = obj.optLong("id", 0),
                        title = obj.optString("title", "খরচ"),
                        amount = obj.optDouble("amount", 0.0),
                        category = obj.optString("category", "সাধারণ"),
                        targetMonth = obj.optString("targetMonth", ""),
                        date = obj.optString("date", ""),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                        spentBy = obj.optString("spentBy", ""),
                        voucherNo = obj.optString("voucherNo", ""),
                        note = obj.optString("note", "")
                    )
                )
            }

            // Clear and insert
            memberDao.clearAllMembers()
            depositDao.clearAllDeposits()
            expenseDao.clearAllExpenses()

            if (members.isNotEmpty()) memberDao.insertMembers(members)
            if (deposits.isNotEmpty()) depositDao.insertDeposits(deposits)
            if (expenses.isNotEmpty()) expenseDao.insertExpenses(expenses)

            Result.success("সফলভাবে ${members.size} জন সদস্য, ${deposits.size} টি জমা ও ${expenses.size} টি খরচের তথ্য রিস্টোর করা হয়েছে।")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
