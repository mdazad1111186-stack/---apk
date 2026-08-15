package com.example.ui.dialogs

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.OrgRedExpense
import com.example.ui.viewmodel.DeleteTarget
import com.example.util.Formatters

@Composable
fun DeleteConfirmDialog(
    target: DeleteTarget?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    if (target == null) return

    val (title, message) = when (target) {
        is DeleteTarget.Deposit -> {
            "জমা হিসাব মুছে ফেলার নিশ্চিতকরণ" to
            "আপনি কি নিশ্চিত যে '${target.deposit.memberName}'-এর ${Formatters.formatTaka(target.deposit.amount)} জমার রেকর্ডটি মুছে ফেলতে চান?"
        }
        is DeleteTarget.Expense -> {
            "খরচের হিসাব মুছে ফেলার নিশ্চিতকরণ" to
            "আপনি কি নিশ্চিত যে '${target.expense.title}' (${Formatters.formatTaka(target.expense.amount)}) খরচের হিসাবটি মুছে ফেলতে চান?"
        }
        is DeleteTarget.MemberItem -> {
            "সদস্য মুছে ফেলার নিশ্চিতকরণ" to
            "আপনি কি নিশ্চিত যে '${target.member.name}' (${target.member.designation})-কে সদস্যদের তালিকা থেকে মুছে ফেলতে চান?"
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = OrgRedExpense
            )
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = OrgRedExpense),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("confirm_delete_btn")
            ) {
                Text("হ্যাঁ, মুছে ফেলুন")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("বাতিল")
            }
        },
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.testTag("delete_confirm_dialog")
    )
}
