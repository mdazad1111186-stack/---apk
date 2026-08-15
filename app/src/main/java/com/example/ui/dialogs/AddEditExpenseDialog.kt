package com.example.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ExpenseTransaction
import com.example.ui.theme.OrgRedExpense
import com.example.util.Formatters

@Composable
fun AddEditExpenseDialog(
    expense: ExpenseTransaction?,
    onDismiss: () -> Unit,
    onSave: (ExpenseTransaction) -> Unit
) {
    if (expense == null) return

    val isEdit = expense.id != 0L

    var title by remember { mutableStateOf(expense.title) }
    var amountText by remember { mutableStateOf(if (expense.amount > 0) expense.amount.toString().removeSuffix(".0") else "") }
    var category by remember { mutableStateOf(expense.category) }
    var targetMonth by remember { mutableStateOf(expense.targetMonth) }
    var date by remember { mutableStateOf(expense.date.ifEmpty { Formatters.getTodayDate() }) }
    var spentBy by remember { mutableStateOf(expense.spentBy) }
    var voucherNo by remember { mutableStateOf(expense.voucherNo) }
    var note by remember { mutableStateOf(expense.note) }

    var showCategoryDropdown by remember { mutableStateOf(false) }
    var showMonthDropdown by remember { mutableStateOf(false) }

    val categories = listOf(
        "সমাজকল্যাণ ও দান",
        "শিক্ষা ও ক্রীড়া",
        "চিকিৎসা সহায়তা",
        "ত্রাণ বিতরণ",
        "সভা ও অনুষ্ঠান",
        "অফিস ও স্টেশনারি",
        "আপ্যায়ন",
        "যাতায়াত ও পরিবহন",
        "অন্যান্য খরচ"
    )
    val recentMonths = remember { Formatters.getRecentMonthCodes() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 24.dp)
                .testTag("add_edit_expense_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEdit) "খরচের হিসাব সম্পাদনা (Edit)" else "নতুন খরচ যোগ করুন",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = OrgRedExpense
                        )
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "বন্ধ করুন")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("খরচের বিবরণ / উদ্দেশ্য *") },
                    leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                    placeholder = { Text("যেমন: মসজিদ রাস্তা সংস্কার, চিকিৎসা সহায়তা ইত্যাদি") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("expense_title_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Amount
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("খরচের পরিমাণ (টাকা ৳) *") },
                    leadingIcon = { Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = OrgRedExpense) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("expense_amount_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Category & Month Selection Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Category
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("খরচের খাত") },
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showCategoryDropdown = true },
                            shape = RoundedCornerShape(12.dp)
                        )
                        DropdownMenu(
                            expanded = showCategoryDropdown,
                            onDismissRequest = { showCategoryDropdown = false }
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = {
                                        category = cat
                                        showCategoryDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    // Month
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = Formatters.formatMonthBangla(targetMonth),
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("মাসের হিসাব") },
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showMonthDropdown = true },
                            shape = RoundedCornerShape(12.dp)
                        )
                        DropdownMenu(
                            expanded = showMonthDropdown,
                            onDismissRequest = { showMonthDropdown = false }
                        ) {
                            recentMonths.forEach { m ->
                                DropdownMenuItem(
                                    text = { Text(Formatters.formatMonthBangla(m)) },
                                    onClick = {
                                        targetMonth = m
                                        showMonthDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Date & Spent By
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("তারিখ (YYYY-MM-DD)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = spentBy,
                        onValueChange = { spentBy = it },
                        label = { Text("খরচকারী / দায়িত্বপ্রাপ্ত") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Voucher No
                OutlinedTextField(
                    value = voucherNo,
                    onValueChange = { voucherNo = it },
                    label = { Text("ভাউচার / ক্যাশমেমো নং (ঐচ্ছিক)") },
                    leadingIcon = { Icon(Icons.Default.Receipt, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Note
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("বিস্তারিত বিবরণ / মন্তব্য (ঐচ্ছিক)") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("বাতিল")
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = {
                            val amt = amountText.toDoubleOrNull() ?: 0.0
                            if (amt <= 0.0 || title.isBlank()) return@Button

                            onSave(
                                expense.copy(
                                    title = title.trim(),
                                    amount = amt,
                                    category = category,
                                    targetMonth = targetMonth,
                                    date = date,
                                    spentBy = spentBy.trim(),
                                    voucherNo = voucherNo.trim(),
                                    note = note.trim()
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = OrgRedExpense),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("save_expense_btn")
                    ) {
                        Text(if (isEdit) "আপডেট করুন" else "খরচ সংরক্ষণ করুন")
                    }
                }
            }
        }
    }
}
