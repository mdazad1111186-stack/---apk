package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "expenses",
    indices = [Index(value = ["targetMonth"]), Index(value = ["category"])]
)
data class ExpenseTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: String = "সমাজকল্যাণ", // সমাজকল্যাণ ও অনুদান, শিক্ষা ও ক্রীড়া, চিকিৎসা সহায়তা, ত্রাণ বিতরণ, সভা ও অনুষ্ঠান, অফিস ও স্টেশনারি, আপ্যায়ন, অন্যান্য
    val targetMonth: String, // e.g., "2026-08" or "আগস্ট ২০২৬"
    val date: String, // "2026-08-15"
    val timestamp: Long = System.currentTimeMillis(),
    val spentBy: String = "",
    val voucherNo: String = "",
    val note: String = ""
)
