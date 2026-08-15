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
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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
import androidx.compose.material3.Switch
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
import com.example.data.model.Member
import com.example.ui.theme.OrgGreenPrimary
import com.example.util.Formatters

@Composable
fun AddEditMemberDialog(
    member: Member?,
    onDismiss: () -> Unit,
    onSave: (Member) -> Unit
) {
    if (member == null) return

    val isEdit = member.id != 0L

    var name by remember { mutableStateOf(member.name) }
    var phone by remember { mutableStateOf(member.phone) }
    var designation by remember { mutableStateOf(member.designation) }
    var monthlyFeeText by remember { mutableStateOf(if (member.monthlyFee > 0) member.monthlyFee.toString().removeSuffix(".0") else "200") }
    var joinDate by remember { mutableStateOf(member.joinDate.ifEmpty { Formatters.getTodayDate() }) }
    var address by remember { mutableStateOf(member.address) }
    var bloodGroup by remember { mutableStateOf(member.bloodGroup) }
    var isActive by remember { mutableStateOf(member.isActive) }
    var notes by remember { mutableStateOf(member.notes) }

    var showDesignationDropdown by remember { mutableStateOf(false) }
    var showBloodDropdown by remember { mutableStateOf(false) }

    val designations = listOf(
        "সভাপতি",
        "সহ-সভাপতি",
        "সাধারণ সম্পাদক",
        "যুগ্ম সাধারণ সম্পাদক",
        "সাংগঠনিক সম্পাদক",
        "সহ-সাংগঠনিক সম্পাদক",
        "কোষাধ্যক্ষ",
        "সহ-কোষাধ্যক্ষ",
        "প্রচার সম্পাদক",
        "সমাজকল্যাণ সম্পাদক",
        "শিক্ষা ও ক্রীড়া সম্পাদক",
        "ধর্ম বিষয়ক সম্পাদক",
        "দপ্তর সম্পাদক",
        "কার্যনির্বাহী সদস্য",
        "সাধারণ সদস্য",
        "উপদেষ্টা",
        "পৃষ্ঠপোষক"
    )

    val bloodGroups = listOf("", "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 24.dp)
                .testTag("add_edit_member_dialog"),
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
                        text = if (isEdit) "সদস্যের তথ্য সম্পাদনা" else "নতুন সদস্য নিবন্ধন",
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

                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("সদস্যের পুরো নাম *") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("member_name_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Designation Picker
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = designation,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("পদবী / দায়িত্ব *") },
                        leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
                        trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDesignationDropdown = true },
                        shape = RoundedCornerShape(12.dp)
                    )

                    DropdownMenu(
                        expanded = showDesignationDropdown,
                        onDismissRequest = { showDesignationDropdown = false }
                    ) {
                        designations.forEach { des ->
                            DropdownMenuItem(
                                text = { Text(des) },
                                onClick = {
                                    designation = des
                                    showDesignationDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Phone & Monthly Fee
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("মোবাইল নম্বর") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.weight(1.2f),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = monthlyFeeText,
                        onValueChange = { monthlyFeeText = it },
                        label = { Text("মাসিক চাঁদা (৳)") },
                        leadingIcon = { Icon(Icons.Default.MonetizationOn, contentDescription = null) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Blood Group & Join Date
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = bloodGroup.ifEmpty { "রক্তের গ্রুপ" },
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("রক্তের গ্রুপ") },
                            leadingIcon = { Icon(Icons.Default.Opacity, contentDescription = null) },
                            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = null) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showBloodDropdown = true },
                            shape = RoundedCornerShape(12.dp)
                        )
                        DropdownMenu(
                            expanded = showBloodDropdown,
                            onDismissRequest = { showBloodDropdown = false }
                        ) {
                            bloodGroups.forEach { bg ->
                                DropdownMenuItem(
                                    text = { Text(if (bg.isEmpty()) "নির্বাচন নেই" else bg) },
                                    onClick = {
                                        bloodGroup = bg
                                        showBloodDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = joinDate,
                        onValueChange = { joinDate = it },
                        label = { Text("যোগদানের তারিখ") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Address
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("ঠিকানা / গ্রাম") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("মন্তব্য / বিশেষ তথ্য (ঐচ্ছিক)") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Active Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isActive) "সক্রিয় সদস্য (Active)" else "নিষ্ক্রিয় সদস্য (Inactive)",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                    )
                    Switch(
                        checked = isActive,
                        onCheckedChange = { isActive = it }
                    )
                }

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
                            if (name.isBlank()) return@Button
                            val fee = monthlyFeeText.toDoubleOrNull() ?: 200.0

                            onSave(
                                member.copy(
                                    name = name.trim(),
                                    phone = phone.trim(),
                                    designation = designation,
                                    monthlyFee = fee,
                                    joinDate = joinDate,
                                    address = address.trim(),
                                    bloodGroup = bloodGroup,
                                    isActive = isActive,
                                    notes = notes.trim()
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = OrgGreenPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("save_member_btn")
                    ) {
                        Text(if (isEdit) "আপডেট করুন" else "সদস্য সংরক্ষণ করুন")
                    }
                }
            }
        }
    }
}
