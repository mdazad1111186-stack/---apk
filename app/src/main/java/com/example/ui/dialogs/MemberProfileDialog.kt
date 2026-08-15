package com.example.ui.dialogs

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.DepositTransaction
import com.example.data.model.Member
import com.example.ui.theme.OrgGoldContainer
import com.example.ui.theme.OrgGoldOnContainer
import com.example.ui.theme.OrgGreenLight
import com.example.ui.theme.OrgGreenPrimary
import com.example.ui.theme.OrgRedExpense
import com.example.util.Formatters

@Composable
fun MemberProfileDialog(
    member: Member?,
    memberDeposits: List<DepositTransaction>,
    onDismiss: () -> Unit,
    onAddDeposit: (Member) -> Unit,
    onEditMember: (Member) -> Unit,
    onEditDeposit: (DepositTransaction) -> Unit,
    onDeleteDeposit: (DepositTransaction) -> Unit
) {
    if (member == null) return

    val context = LocalContext.current
    val isExecutive = member.designation in listOf("সভাপতি", "সহ-সভাপতি", "সাধারণ সম্পাদক", "যুগ্ম সাধারণ সম্পাদক", "সাংগঠনিক সম্পাদক", "কোষাধ্যক্ষ", "প্রচার সম্পাদক", "সমাজকল্যাণ সম্পাদক", "উপদেষ্টা")
    val totalDeposited = memberDeposits.sumOf { it.amount }

    fun shareMemberStatement() {
        val sb = StringBuilder()
        sb.append("দক্ষিণ লতিবপুর যুব উন্নয়ন সংগঠন\n")
        sb.append("সদস্যের বিবরণী (Member Statement)\n\n")
        sb.append("নাম: ${member.name}\n")
        sb.append("পদবী: ${member.designation}\n")
        sb.append("মোবাইল: ${member.phone}\n")
        sb.append("নির্ধারিত মাসিক চাঁদা: ${Formatters.formatTaka(member.monthlyFee)}\n")
        sb.append("সর্বমোট পরিশোধিত জমা: ${Formatters.formatTaka(totalDeposited)}\n")
        sb.append("মোট কিস্তি/লেনদেন: ${memberDeposits.size} টি\n\n")
        sb.append("--- জমা বিবরণী ---\n")
        memberDeposits.forEachIndexed { idx, dep ->
            sb.append("${idx + 1}. ${dep.date} (${Formatters.formatMonthBangla(dep.targetMonth)}): ${Formatters.formatTaka(dep.amount)} [${dep.category}, ${dep.paymentMethod}]\n")
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, sb.toString())
            putExtra(Intent.EXTRA_TITLE, "${member.name} - সদস্য চাঁদা বিবরণী")
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "সদস্য স্টেটমেন্ট শেয়ার করুন")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f)
                .padding(vertical = 16.dp)
                .testTag("member_profile_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Top Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "সদস্য নিজস্ব প্রোফাইল",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "বন্ধ করুন")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Member Profile Header Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(if (isExecutive) OrgGoldContainer else OrgGreenLight),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isExecutive) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = OrgGoldOnContainer,
                                        modifier = Modifier.size(28.dp)
                                    )
                                } else {
                                    Text(
                                        text = member.name.take(1),
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = OrgGreenPrimary
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = member.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                )
                                Text(
                                    text = "${member.designation} • ${if (member.isActive) "সক্রিয় সদস্য" else "নিষ্ক্রিয়"}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = if (isExecutive) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Info rows: Phone, Blood, Address, Join Date
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (member.phone.isNotEmpty()) {
                                Row(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable {
                                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${member.phone}"))
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            context.startActivity(intent)
                                        },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Call, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(member.phone, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                }
                            }

                            if (member.bloodGroup.isNotEmpty()) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Opacity, contentDescription = null, tint = OrgRedExpense, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("রক্ত: ${member.bloodGroup}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }

                        if (member.address.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(member.address, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Stats Cards (Total Deposited & Monthly Fee)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(OrgGreenLight)
                            .padding(12.dp)
                    ) {
                        Column {
                            Text("সর্বমোট জমা প্রদান", style = MaterialTheme.typography.labelSmall.copy(color = OrgGreenPrimary))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                Formatters.formatTaka(totalDeposited),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = OrgGreenPrimary)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(12.dp)
                    ) {
                        Column {
                            Text("নির্ধারিত মাসিক চাঁদা", style = MaterialTheme.typography.labelSmall)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                Formatters.formatTaka(member.monthlyFee),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action buttons row (Add Deposit, Edit Profile, Share)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onAddDeposit(member) },
                        colors = ButtonDefaults.buttonColors(containerColor = OrgGreenPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).testTag("profile_add_deposit_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("চাঁদা জমা", fontSize = 13.sp)
                    }

                    FilledTonalButton(
                        onClick = { onEditMember(member) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f).testTag("profile_edit_member_btn")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("এডিট", fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = { shareMemberStatement() },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("profile_share_statement_btn")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(modifier = Modifier.height(8.dp))

                // Personal Transactions List Header
                Text(
                    text = "ব্যক্তিগত জমার ইতিহাস (${memberDeposits.size} টি লেনদেন)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                if (memberDeposits.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "এই সদস্যের কোন জমার লেনদেন রেকর্ড নেই।",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(memberDeposits, key = { it.id }) { dep ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "${Formatters.formatMonthBangla(dep.targetMonth)} • ${dep.category}",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                        )
                                        Text(
                                            text = "${dep.date} • মাধ্যম: ${dep.paymentMethod} ${if (dep.receiptNo.isNotEmpty()) "• রসিদ: ${dep.receiptNo}" else ""}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Text(
                                        text = "+ ${Formatters.formatTaka(dep.amount)}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = OrgGreenPrimary
                                        )
                                    )

                                    Row {
                                        IconButton(
                                            onClick = { onEditDeposit(dep) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Edit, contentDescription = "এডিট", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                        }
                                        IconButton(
                                            onClick = { onDeleteDeposit(dep) },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "মুছুন", tint = OrgRedExpense, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
