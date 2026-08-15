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
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.data.model.DepositTransaction
import com.example.data.model.Member
import com.example.ui.theme.OrgGreenPrimary
import com.example.util.Formatters

@Composable
fun AddEditDepositDialog(
    deposit: DepositTransaction?,
    members: List<Member>,
    onDismiss: () -> Unit,
    onSave: (DepositTransaction) -> Unit
) {
    if (deposit == null) return

    val isEdit = deposit.id != 0L

    var selectedMemberId by remember { mutableStateOf(deposit.memberId) }
    var memberName by remember { mutableStateOf(deposit.memberName) }
    var amountText by remember { mutableStateOf(if (deposit.amount > 0) deposit.amount.toString().removeSuffix(".0") else "") }
    var category by remember { mutableStateOf(deposit.category) }
    var targetMonth by remember { mutableStateOf(deposit.targetMonth) }
    var date by remember { mutableStateOf(deposit.date.ifEmpty { Formatters.getTodayDate() }) }
    var paymentMethod by remember { mutableStateOf(deposit.paymentMethod) }
    var receiptNo by remember { mutableStateOf(deposit.receiptNo) }
    var note by remember { mutableStateOf(deposit.note) }
    var collectedBy by remember { mutableStateOf(deposit.collectedBy) }

    var showMemberDropdown by remember { mutableStateOf(false) }
    var showCategoryDropdown by remember { mutableStateOf(false) }
    var showMonthDropdown by remember { mutableStateOf(false) }
    var showMethodDropdown by remember { mutableStateOf(false) }

    val categories = listOf("মাসিক চাঁদা", "এককালীন অনুদান", "সাধারণ ফান্ড জমা", "জরুরি ত্রাণ ফান্ড", "অন্যান্য")
    val paymentMethods = listOf("নগদ", "বিকাশ", "নগদ (Nagad App)", "রকেট", "ব্যাংক ট্রান্সফার")
    val recentMonths = remember { Formatters.getRecentMonthCodes() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 24.dp)
                .testTag("add_edit_deposit_dialog"),
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
                // Dialog Title Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEdit) "জমা হিসাব সম্পাদনা (Edit)" else "নতুন জমা / চাঁদা যোগ করুন",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "বন্ধ করুন")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Member Selector
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = memberName.ifEmpty { "সদস্য নির্বাচন করুন (ঐচ্ছিক)" },
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("সদস্যের নাম") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showMemberDropdown = true }
                            .testTag("deposit_member_picker"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    DropdownMenu(
                        expanded = showMemberDropdown,
                        onDismissRequest = { showMemberDropdown = false },
                        modifier = Modifier.fillMaxWidth(0.85f)
                    ) {
                        DropdownMenuItem(
                            text = { Text("সাধারণ অনুদান / সদস্যহীন") },
                            onClick = {
                                selectedMemberId = 0
                                memberName = "সাধারণ শুভাকাঙ্ক্ষী / অনুদান"
                                showMemberDropdown = false
                            }
                        )
                        members.forEach { m ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(m.name, fontWeight = FontWeight.Bold)
                                        Text("${m.designation} • মাসিক নির্ধারিত: ৳${m.monthlyFee.toInt()}", style = MaterialTheme.typography.bodySmall)
                                    }
                                },
                                onClick = {
                                    selectedMemberId = m.id
                                    memberName = m.name
                                    if (amountText.isEmpty() || amountText == "0") {
                                        amountText = m.monthlyFee.toString().removeSuffix(".0")
                                    }
                                    showMemberDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Amount Field
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("টাকার পরিমাণ (৳) *") },
                    leadingIcon = { Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = OrgGreenPrimary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("deposit_amount_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Target Month Selector
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = Formatters.formatMonthBangla(targetMonth),
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("কোন মাসের চাঁদা/জমা *") },
                        leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showMonthDropdown = true }
                            .testTag("deposit_month_picker"),
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

                Spacer(modifier = Modifier.height(12.dp))

                // Category & Payment Method Row
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
                            label = { Text("খাত") },
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

                    // Payment Method
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = paymentMethod,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("পদ্ধতি") },
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showMethodDropdown = true },
                            shape = RoundedCornerShape(12.dp)
                        )
                        DropdownMenu(
                            expanded = showMethodDropdown,
                            onDismissRequest = { showMethodDropdown = false }
                        ) {
                            paymentMethods.forEach { method ->
                                DropdownMenuItem(
                                    text = { Text(method) },
                                    onClick = {
                                        paymentMethod = method
                                        showMethodDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Date & Receipt No
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
                        value = receiptNo,
                        onValueChange = { receiptNo = it },
                        label = { Text("রসিদ / ট্রানজেকশন নং") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Collector
                OutlinedTextField(
                    value = collectedBy,
                    onValueChange = { collectedBy = it },
                    label = { Text("আদায়কারী / সংগ্রাহক") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Note
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("মন্তব্য (ঐচ্ছিক)") },
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
                            if (amt <= 0.0) return@Button
                            val finalMemberName = if (memberName.isEmpty()) "সাধারণ অনুদান" else memberName

                            onSave(
                                deposit.copy(
                                    memberId = selectedMemberId,
                                    memberName = finalMemberName,
                                    amount = amt,
                                    category = category,
                                    targetMonth = targetMonth,
                                    date = date,
                                    paymentMethod = paymentMethod,
                                    receiptNo = receiptNo,
                                    note = note,
                                    collectedBy = collectedBy
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = OrgGreenPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("save_deposit_btn")
                    ) {
                        Text(if (isEdit) "আপডেট করুন" else "জমা সংরক্ষণ করুন")
                    }
                }
            }
        }
    }
}
