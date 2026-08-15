package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "members")
data class Member(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val phone: String = "",
    val designation: String = "সদস্য", // সভাপতি, সাধারণ সম্পাদক, সহ-সভাপতি, যুগ্ম সম্পাদক, সাংগঠনিক সম্পাদক, কোষাধ্যক্ষ, প্রচার সম্পাদক, সমাজকল্যাণ সম্পাদক, সদস্য, উপদেষ্টা
    val monthlyFee: Double = 200.0,
    val joinDate: String = "",
    val address: String = "দক্ষিণ লতিবপুর",
    val bloodGroup: String = "",
    val isActive: Boolean = true,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
