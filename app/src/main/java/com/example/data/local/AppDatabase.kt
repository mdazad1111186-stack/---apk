package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.DepositTransaction
import com.example.data.model.ExpenseTransaction
import com.example.data.model.Member
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Member::class, DepositTransaction::class, ExpenseTransaction::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun memberDao(): MemberDao
    abstract fun depositDao(): DepositDao
    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "latifpur_youth_org.db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(AppDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }
        }

        suspend fun populateInitialData(database: AppDatabase) {
            val memberDao = database.memberDao()
            val depositDao = database.depositDao()
            val expenseDao = database.expenseDao()

            if (memberDao.getMemberCount() == 0) {
                val members = listOf(
                    Member(1, "মোঃ তারেক রহমান", "01711000001", "সভাপতি", 500.0, "2024-01-01", "দক্ষিণ লতিবপুর, বাজার রোড", "A+", true, "প্রতিষ্ঠাতা সদস্য"),
                    Member(2, "মোঃ সাজ্জাদ হোসেন", "01812000002", "সাধারণ সম্পাদক", 500.0, "2024-01-01", "দক্ষিণ লতিবপুর, পূর্বপাড়া", "O+", true, "সক্রিয় সংগঠক"),
                    Member(3, "মোঃ আরিফুল ইসলাম", "01913000003", "কোষাধ্যক্ষ", 500.0, "2024-01-01", "দক্ষিণ লতিবপুর, মধ্যপাড়া", "B+", true, "অর্থ বিষয়ক দায়িত্বে"),
                    Member(4, "হাসান মাহমুদ", "01614000004", "সাংগঠনিক সম্পাদক", 300.0, "2024-02-15", "দক্ষিণ লতিবপুর, উত্তরপাড়া", "AB+", true, ""),
                    Member(5, "মাহবুবুর রহমান", "01515000005", "প্রচার সম্পাদক", 300.0, "2024-03-01", "দক্ষিণ লতিবপুর, পশ্চিমপাড়া", "O-", true, ""),
                    Member(6, "কামরুল হাসান", "01716000006", "সমাজকল্যাণ সম্পাদক", 300.0, "2024-03-10", "দক্ষিণ লতিবপুর, স্কুল রোড", "A-", true, "ত্রাণ ও পুনর্বাসন কাজ"),
                    Member(7, "রাকিবুল হাসান", "01817000007", "সদস্য", 200.0, "2024-04-01", "দক্ষিণ লতিবপুর", "B-", true, ""),
                    Member(8, "মোঃ তানভীর আহমেদ", "01918000008", "সদস্য", 200.0, "2024-04-05", "দক্ষিণ লতিবপুর", "O+", true, ""),
                    Member(9, "জাহিদুল ইসলাম", "01619000009", "সদস্য", 200.0, "2024-05-01", "দক্ষিণ লতিবপুর", "A+", true, ""),
                    Member(10, "আলমগীর হোসেন", "01720000010", "উপদেষ্টা", 1000.0, "2024-01-01", "দক্ষিণ লতিবপুর", "O+", true, "প্রধান পৃষ্ঠপোষক")
                )
                memberDao.insertMembers(members)

                val deposits = listOf(
                    DepositTransaction(
                        id = 1,
                        memberId = 1,
                        memberName = "মোঃ তারেক রহমান",
                        amount = 500.0,
                        category = "মাসিক চাঁদা",
                        targetMonth = "2026-08",
                        date = "2026-08-01",
                        paymentMethod = "নগদ",
                        receiptNo = "REC-2608-01",
                        note = "আগস্ট মাসের চাঁদা",
                        collectedBy = "মোঃ আরিফুল ইসলাম"
                    ),
                    DepositTransaction(
                        id = 2,
                        memberId = 2,
                        memberName = "মোঃ সাজ্জাদ হোসেন",
                        amount = 500.0,
                        category = "মাসিক চাঁদা",
                        targetMonth = "2026-08",
                        date = "2026-08-02",
                        paymentMethod = "বিকাশ",
                        receiptNo = "REC-2608-02",
                        note = "বিকাশ ট্রানজেকশন: 9LK8M2",
                        collectedBy = "মোঃ আরিফুল ইসলাম"
                    ),
                    DepositTransaction(
                        id = 3,
                        memberId = 3,
                        memberName = "মোঃ আরিফুল ইসলাম",
                        amount = 500.0,
                        category = "মাসিক চাঁদা",
                        targetMonth = "2026-08",
                        date = "2026-08-03",
                        paymentMethod = "নগদ",
                        receiptNo = "REC-2608-03",
                        note = "আগস্ট মাসের চাঁদা",
                        collectedBy = "মোঃ আরিফুল ইসলাম"
                    ),
                    DepositTransaction(
                        id = 4,
                        memberId = 10,
                        memberName = "আলমগীর হোসেন",
                        amount = 5000.0,
                        category = "বিশেষ অনুদান",
                        targetMonth = "2026-08",
                        date = "2026-08-05",
                        paymentMethod = "ব্যাংক",
                        receiptNo = "REC-2608-04",
                        note = "গ্রামের অসহায়দের চিকিৎসা ফান্ডে অনুদান",
                        collectedBy = "মোঃ তারেক রহমান"
                    ),
                    DepositTransaction(
                        id = 5,
                        memberId = 4,
                        memberName = "হাসান মাহমুদ",
                        amount = 300.0,
                        category = "মাসিক চাঁদা",
                        targetMonth = "2026-08",
                        date = "2026-08-06",
                        paymentMethod = "নগদ",
                        receiptNo = "REC-2608-05",
                        note = "",
                        collectedBy = "মোঃ আরিফুল ইসলাম"
                    ),
                    DepositTransaction(
                        id = 6,
                        memberId = 6,
                        memberName = "কামরুল হাসান",
                        amount = 300.0,
                        category = "মাসিক চাঁদা",
                        targetMonth = "2026-08",
                        date = "2026-08-07",
                        paymentMethod = "নগদ",
                        receiptNo = "REC-2608-06",
                        note = "",
                        collectedBy = "মোঃ আরিফুল ইসলাম"
                    )
                )
                depositDao.insertDeposits(deposits)

                val expenses = listOf(
                    ExpenseTransaction(
                        id = 1,
                        title = "অসহায় রোগীর ঔষধ ক্রয় সহায়তা",
                        amount = 2500.0,
                        category = "চিকিৎসা সহায়তা",
                        targetMonth = "2026-08",
                        date = "2026-08-08",
                        spentBy = "কামরুল হাসান",
                        voucherNo = "EXP-2608-01",
                        note = "লতিবপুর মধ্যপাড়ার অসুস্থ চাচাকে ঔষধ কিনে দেয়া হয়েছে"
                    ),
                    ExpenseTransaction(
                        id = 2,
                        title = "সংগঠনের মাসিক মিটিং আপ্যায়ন ও স্টেশনারি",
                        amount = 650.0,
                        category = "অফিস ও স্টেশনারি",
                        targetMonth = "2026-08",
                        date = "2026-08-04",
                        spentBy = "মোঃ সাজ্জাদ হোসেন",
                        voucherNo = "EXP-2608-02",
                        note = "রেজিস্টার খাতা ও চা-বিস্কুট খরচ"
                    ),
                    ExpenseTransaction(
                        id = 3,
                        title = "মসজিদ সংলগ্ন রাস্তা সংস্কার স্বেচ্ছাশ্রমের সরঞ্জাম",
                        amount = 1200.0,
                        category = "সমাজকল্যাণ",
                        targetMonth = "2026-08",
                        date = "2026-08-10",
                        spentBy = "হাসান মাহমুদ",
                        voucherNo = "EXP-2608-03",
                        note = "কোদাল, টুকরি ও বালু পরিবহন খরচ"
                    )
                )
                expenseDao.insertExpenses(expenses)
            }
        }
    }
}
