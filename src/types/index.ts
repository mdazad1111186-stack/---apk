export interface Member {
  id: number;
  name: string;
  phone: string;
  designation: string; // সভাপতি, সাধারণ সম্পাদক, কোষাধ্যক্ষ, সাংগঠনিক সম্পাদক, প্রচার সম্পাদক, সমাজকল্যাণ সম্পাদক, সদস্য, উপদেষ্টা
  monthlyFee: number;
  joinDate: string;
  address: string;
  bloodGroup: string;
  isActive: boolean;
  notes: string;
  createdAt: number;
}

export interface DepositTransaction {
  id: number;
  memberId: number;
  memberName: string;
  amount: number;
  category: string; // মাসিক চাঁদা, বিশেষ অনুদান, ভর্তি ফি, অন্যান্য
  targetMonth: string; // YYYY-MM e.g. "2026-08"
  date: string; // YYYY-MM-DD
  timestamp: number;
  paymentMethod: string; // নগদ, বিকাশ, নগদ, রকেট, ব্যাংক
  receiptNo: string;
  note: string;
  collectedBy: string;
}

export interface ExpenseTransaction {
  id: number;
  title: string;
  amount: number;
  category: string; // সমাজকল্যাণ, চিকিৎসা সহায়তা, শিক্ষা সহায়তা, ত্রাণ ও পুনর্বাসন, অফিস ও মিটিং, রাস্তা সংস্কার, মসজিদ ও ধর্মীয়, অন্যান্য
  targetMonth: string; // YYYY-MM
  date: string; // YYYY-MM-DD
  timestamp: number;
  spentBy: string;
  voucherNo: string;
  note: string;
}

export type TabType = 'dashboard' | 'members' | 'deposits' | 'expenses' | 'sheets' | 'backup';
