package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.PersonAdd
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DepositTransaction
import com.example.data.model.Member
import com.example.ui.components.MemberCardItem
import com.example.ui.theme.OrgGreenLight
import com.example.ui.theme.OrgGreenPrimary
import com.example.util.Formatters

@Composable
fun MembersScreen(
    members: List<Member>,
    deposits: List<DepositTransaction>,
    selectedMonth: String,
    onAddMember: () -> Unit,
    onViewProfile: (Member) -> Unit,
    onQuickDeposit: (Member) -> Unit,
    onEditMember: (Member) -> Unit,
    onDeleteMember: (Member) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("ALL") } // ALL, EXECUTIVE, GENERAL, ADVISOR

    val executiveRoles = listOf("সভাপতি", "সহ-সভাপতি", "সাধারণ সম্পাদক", "যুগ্ম সাধারণ সম্পাদক", "সাংগঠনিক সম্পাদক", "কোষাধ্যক্ষ", "প্রচার সম্পাদক", "সমাজকল্যাণ সম্পাদক", "শিক্ষা ও ক্রীড়া সম্পাদক")

    val filteredMembers = remember(members, searchQuery, selectedCategoryFilter) {
        members.filter { m ->
            val matchesSearch = searchQuery.isBlank() ||
                    m.name.contains(searchQuery, ignoreCase = true) ||
                    m.phone.contains(searchQuery, ignoreCase = true) ||
                    m.designation.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedCategoryFilter) {
                "EXECUTIVE" -> m.designation in executiveRoles
                "GENERAL" -> m.designation !in executiveRoles && m.designation != "উপদেষ্টা" && m.designation != "পৃষ্ঠপোষক"
                "ADVISOR" -> m.designation == "উপদেষ্টা" || m.designation == "পৃষ্ঠপোষক"
                else -> true
            }

            matchesSearch && matchesFilter
        }
    }

    val totalDepositsByMember = remember(deposits) {
        deposits.groupBy { it.memberId }
            .mapValues { (_, list) -> list.sumOf { it.amount } }
    }

    val paidThisMonthMemberIds = remember(deposits, selectedMonth) {
        deposits.filter { it.targetMonth == selectedMonth }.map { it.memberId }.toSet()
    }

    Scaffold(
        modifier = modifier.fillMaxSize().testTag("members_screen"),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddMember,
                containerColor = OrgGreenPrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("fab_add_member")
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = "নতুন সদস্য যোগ করুন")
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
                    placeholder = { Text("নাম, পদবী বা মোবাইল দিয়ে খুঁজুন...") },
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
                        .testTag("members_search_bar"),
                    singleLine = true
                )
            }

            // 2. Category Filter Chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedCategoryFilter == "ALL",
                        onClick = { selectedCategoryFilter = "ALL" },
                        label = { Text("সকল সদস্য (${members.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                    FilterChip(
                        selected = selectedCategoryFilter == "EXECUTIVE",
                        onClick = { selectedCategoryFilter = "EXECUTIVE" },
                        label = { Text("কার্যনির্বাহী কমিটি") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                    FilterChip(
                        selected = selectedCategoryFilter == "GENERAL",
                        onClick = { selectedCategoryFilter = "GENERAL" },
                        label = { Text("সাধারণ সদস্য") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                    FilterChip(
                        selected = selectedCategoryFilter == "ADVISOR",
                        onClick = { selectedCategoryFilter = "ADVISOR" },
                        label = { Text("উপদেষ্টামণ্ডলী") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            // 3. Stats Bar
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "মোট প্রদর্শিত: ${filteredMembers.size} জন",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = "চলতি মাসে আদায়: ${paidThisMonthMemberIds.size} জন",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = OrgGreenPrimary
                            )
                        )
                    }
                }
            }

            // 4. Members List
            if (filteredMembers.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchQuery.isNotEmpty()) "কোন সদস্যের তথ্য খুঁজে পাওয়া যায়নি।" else "এখনও কোন সদস্য যুক্ত করা হয়নি। '+' বাটনে চাপ দিয়ে নতুন সদস্য যুক্ত করুন।",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(filteredMembers, key = { it.id }) { member ->
                    val totalDep = totalDepositsByMember[member.id] ?: 0.0
                    val isPaid = paidThisMonthMemberIds.contains(member.id)

                    MemberCardItem(
                        member = member,
                        totalDeposited = totalDep,
                        isPaidThisMonth = isPaid,
                        onViewProfile = onViewProfile,
                        onQuickDeposit = onQuickDeposit,
                        onEdit = onEditMember,
                        onDelete = onDeleteMember
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
