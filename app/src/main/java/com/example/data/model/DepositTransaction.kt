package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "deposits",
    indices = [Index(value = ["memberId"]), Index(value = ["targetMonth"])]
)
data class DepositTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val memberId: Long = 0, // 0 if general collection/donor
    val memberName: String,
    val amount: Double,
    val category: String = "মাসিক চাঁদা", // মাসিক চাঁদা, এককালীন অনুদান, সাধারণ ফান্ড জমা, জরুরি ত্রাণ ফান্ড, অন্যান্য
    val targetMonth: String, // e.g., "2026-08" or "আগস্ট ২০২৬"
    val date: String, // "2026-08-15"
    val timestamp: Long = System.currentTimeMillis(),
    val paymentMethod: String = "নগদ", // নগদ, বিকাশ, নগদ রকেট, ব্যাংক
    val receiptNo: String = "",
    val note: String = "",
    val collectedBy: String = "কোষাধ্যক্ষ"
)
