import React from 'react';
import { 
  ArrowDownLeft, 
  ArrowUpRight, 
  Wallet, 
  Users, 
  Calendar, 
  TrendingUp, 
  CheckCircle2, 
  Clock, 
  HeartHandshake,
  Receipt,
  FileSpreadsheet,
  AlertCircle
} from 'lucide-react';
import { Member, DepositTransaction, ExpenseTransaction } from '../types';
import { 
  formatCurrency, 
  formatMonthBangla, 
  toBengaliNumber, 
  getAvailableMonthList 
} from '../utils/formatters';

interface DashboardProps {
  selectedMonth: string;
  onSelectMonth: (m: string) => void;
  members: Member[];
  deposits: DepositTransaction[];
  expenses: ExpenseTransaction[];
  onOpenAddDeposit: (memberId?: number) => void;
  onOpenAddExpense: () => void;
  onOpenAddMember: () => void;
  onOpenMemberProfile: (m: Member) => void;
  onOpenSheets: () => void;
  onSelectTab: (tab: any) => void;
}

export const Dashboard: React.FC<DashboardProps> = ({
  selectedMonth,
  onSelectMonth,
  members,
  deposits,
  expenses,
  onOpenAddDeposit,
  onOpenAddExpense,
  onOpenAddMember,
  onOpenMemberProfile,
  onOpenSheets,
  onSelectTab,
}) => {
  const monthOptions = getAvailableMonthList();

  // All time totals
  const totalDepositAll = deposits.reduce((acc, d) => acc + d.amount, 0);
  const totalExpenseAll = expenses.reduce((acc, e) => acc + e.amount, 0);
  const currentFundBalance = totalDepositAll - totalExpenseAll;

  // Selected month totals
  const monthDeposits = selectedMonth === 'ALL' ? deposits : deposits.filter(d => d.targetMonth === selectedMonth);
  const monthExpenses = selectedMonth === 'ALL' ? expenses : expenses.filter(e => e.targetMonth === selectedMonth);
  
  const monthDepositTotal = monthDeposits.reduce((acc, d) => acc + d.amount, 0);
  const monthExpenseTotal = monthExpenses.reduce((acc, e) => acc + e.amount, 0);
  const monthNet = monthDepositTotal - monthExpenseTotal;

  // Member payment stats for selected month
  const activeMembers = members.filter(m => m.isActive);
  const paidMemberIds = new Set(monthDeposits.map(d => d.memberId));
  const paidCount = activeMembers.filter(m => paidMemberIds.has(m.id)).length;
  const unpaidMembers = activeMembers.filter(m => !paidMemberIds.has(m.id));

  // Expense breakdown by category
  const expenseCategories = monthExpenses.reduce((acc: Record<string, number>, curr) => {
    acc[curr.category] = (acc[curr.category] || 0) + curr.amount;
    return acc;
  }, {});

  return (
    <div className="space-y-6 pb-12">
      {/* Top Welcome & Month Filter Bar */}
      <div className="bg-white rounded-2xl p-4 sm:p-6 shadow-sm border border-slate-200/80 flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2">
            <span className="inline-flex items-center justify-center p-1.5 rounded-lg bg-emerald-100 text-emerald-800 text-xs font-semibold">
              হিসাব বিবরণী
            </span>
            <span className="text-xs text-slate-500 font-medium">
              দক্ষিণ লতিবপুর, বাংলাদেশ
            </span>
          </div>
          <h2 className="text-xl sm:text-2xl font-bold text-slate-800 mt-1">
            তহবিল ও সাংগঠনিক লেনদেনের সার্বিক চিত্র
          </h2>
        </div>

        {/* Month Selector */}
        <div className="flex items-center gap-2 bg-slate-50 p-1.5 rounded-xl border border-slate-200">
          <Calendar className="w-4 h-4 text-emerald-700 ml-2 flex-shrink-0" />
          <span className="text-xs font-semibold text-slate-600">মাস নির্বাচন:</span>
          <select
            value={selectedMonth}
            onChange={(e) => onSelectMonth(e.target.value)}
            className="bg-white border border-slate-300 text-slate-800 text-sm font-semibold rounded-lg px-3 py-1.5 focus:outline-none focus:ring-2 focus:ring-emerald-500 shadow-sm"
          >
            <option value="ALL">সকল মাস (সার্বিক হিসাব)</option>
            {monthOptions.map((opt) => (
              <option key={opt.code} value={opt.code}>
                {opt.label}
              </option>
            ))}
          </select>
        </div>
      </div>

      {/* Main 4 Metric Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 sm:gap-5">
        {/* Net Available Balance */}
        <div className="bg-gradient-to-br from-emerald-700 to-emerald-900 text-white rounded-2xl p-5 shadow-md relative overflow-hidden">
          <div className="absolute top-0 right-0 -mt-2 -mr-2 w-24 h-24 bg-white/10 rounded-full blur-xl pointer-events-none" />
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-emerald-200 uppercase tracking-wider">
              বর্তমান মোট তহবিল স্থিতি
            </span>
            <div className="p-2 bg-white/15 rounded-xl backdrop-blur">
              <Wallet className="w-5 h-5 text-white" />
            </div>
          </div>
          <div className="mt-3">
            <p className="text-2xl sm:text-3xl font-extrabold tracking-tight">
              {formatCurrency(currentFundBalance)}
            </p>
            <p className="text-xs text-emerald-200 mt-1">
              সর্বমোট জমা ও খরচের পর অবশিষ্ট ব্যালেন্স
            </p>
          </div>
        </div>

        {/* Selected Month Collection */}
        <div className="bg-white rounded-2xl p-5 shadow-sm border border-slate-200">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold text-slate-500 uppercase tracking-wider">
              {selectedMonth === 'ALL' ? 'সর্বমোট চাঁদা জমা' : `${formatMonthBangla(selectedMonth)}-এ জমা`}
            </span>
            <div className="p-2 bg-emerald-100 text-emerald-700 rounded-xl">
              <ArrowDownLeft className="w-5 h-5" />
            </div>
          </div>
          <div className="mt-3">
            <p className="text-2xl sm:text-3xl font-bold text-emerald-600">
              {formatCurrency(monthDepositTotal)}
            </p>
            <div className="flex items-center gap-1.5 mt-1 text-xs text-slate-500">
              <Receipt className="w-3.5 h-3.5 text-emerald-600" />
              <span>মোট {toBengaliNumber(monthDeposits.length)} টি জমার রেকর্ড</span>
            </div>
          </div>
        </div>

        {/* Selected Month Expense */}
        <div className="bg-white rounded-2xl p-5 shadow-sm border border-slate-200">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold text-slate-500 uppercase tracking-wider">
              {selectedMonth === 'ALL' ? 'সর্বমোট খরচ' : `${formatMonthBangla(selectedMonth)}-এ খরচ`}
            </span>
            <div className="p-2 bg-rose-100 text-rose-700 rounded-xl">
              <ArrowUpRight className="w-5 h-5" />
            </div>
          </div>
          <div className="mt-3">
            <p className="text-2xl sm:text-3xl font-bold text-rose-600">
              {formatCurrency(monthExpenseTotal)}
            </p>
            <div className="flex items-center gap-1.5 mt-1 text-xs text-slate-500">
              <TrendingUp className="w-3.5 h-3.5 text-rose-500" />
              <span>মোট {toBengaliNumber(monthExpenses.length)} টি ভাউচার</span>
            </div>
          </div>
        </div>

        {/* Member Collection Progress */}
        <div className="bg-white rounded-2xl p-5 shadow-sm border border-slate-200">
          <div className="flex items-center justify-between">
            <span className="text-xs font-bold text-slate-500 uppercase tracking-wider">
              চাঁদা আদায় অগ্রগতি
            </span>
            <div className="p-2 bg-blue-100 text-blue-700 rounded-xl">
              <Users className="w-5 h-5" />
            </div>
          </div>
          <div className="mt-3">
            <div className="flex items-baseline gap-2">
              <span className="text-2xl sm:text-3xl font-bold text-slate-800">
                {toBengaliNumber(paidCount)} / {toBengaliNumber(activeMembers.length)}
              </span>
              <span className="text-xs font-semibold text-slate-500">সদস্য দিয়েছেন</span>
            </div>
            <div className="w-full bg-slate-100 rounded-full h-2 mt-2.5 overflow-hidden">
              <div 
                className="bg-emerald-600 h-2 rounded-full transition-all duration-500" 
                style={{ width: `${activeMembers.length > 0 ? (paidCount / activeMembers.length) * 100 : 0}%` }}
              />
            </div>
          </div>
        </div>
      </div>

      {/* Quick Action Grid */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
        <button
          onClick={() => onOpenAddDeposit()}
          className="flex items-center justify-center gap-2 p-3.5 rounded-xl bg-emerald-50 hover:bg-emerald-100 border border-emerald-200 text-emerald-800 font-semibold text-sm transition active:scale-95 shadow-sm"
        >
          <ArrowDownLeft className="w-4 h-4 text-emerald-600" />
          <span>চাঁদা বা অনুদান জমা</span>
        </button>

        <button
          onClick={onOpenAddExpense}
          className="flex items-center justify-center gap-2 p-3.5 rounded-xl bg-rose-50 hover:bg-rose-100 border border-rose-200 text-rose-800 font-semibold text-sm transition active:scale-95 shadow-sm"
        >
          <ArrowUpRight className="w-4 h-4 text-rose-600" />
          <span>নতুন খরচ এন্ট্রি</span>
        </button>

        <button
          onClick={onOpenAddMember}
          className="flex items-center justify-center gap-2 p-3.5 rounded-xl bg-sky-50 hover:bg-sky-100 border border-sky-200 text-sky-800 font-semibold text-sm transition active:scale-95 shadow-sm"
        >
          <Users className="w-4 h-4 text-sky-600" />
          <span>নতুন সদস্য যুক্ত</span>
        </button>

        <button
          onClick={onOpenSheets}
          className="flex items-center justify-center gap-2 p-3.5 rounded-xl bg-amber-50 hover:bg-amber-100 border border-amber-200 text-amber-900 font-semibold text-sm transition active:scale-95 shadow-sm"
        >
          <FileSpreadsheet className="w-4 h-4 text-amber-600" />
          <span>গুগল সিটে রূপান্তর</span>
        </button>
      </div>

      {/* Two Columns: Recent Transactions & Unpaid Members Notice */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        
        {/* Left 2 Cols: Monthly Expense Categories & Recent Transactions */}
        <div className="lg:col-span-2 space-y-6">
          
          {/* Expense Category Breakdown */}
          <div className="bg-white rounded-2xl p-5 shadow-sm border border-slate-200">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-base font-bold text-slate-800 flex items-center gap-2">
                <HeartHandshake className="w-5 h-5 text-emerald-700" />
                <span>ব্যয়ের খাতভিত্তিক বিভাজন ({formatMonthBangla(selectedMonth)})</span>
              </h3>
              <span className="text-xs font-bold text-rose-700 bg-rose-50 px-2.5 py-1 rounded-lg border border-rose-200">
                মোট খরচ: {formatCurrency(monthExpenseTotal)}
              </span>
            </div>

            {Object.keys(expenseCategories).length === 0 ? (
              <p className="text-sm text-slate-500 py-4 text-center bg-slate-50 rounded-xl">
                এই মাসে এখনও কোনো খরচের হিসাব রেকর্ড করা হয়নি।
              </p>
            ) : (
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                {Object.entries(expenseCategories).map(([cat, amt]) => {
                  const pct = monthExpenseTotal > 0 ? Math.round((amt / monthExpenseTotal) * 100) : 0;
                  return (
                    <div key={cat} className="p-3.5 rounded-xl bg-slate-50 border border-slate-200">
                      <div className="flex items-center justify-between text-sm">
                        <span className="font-semibold text-slate-700">{cat}</span>
                        <span className="font-bold text-rose-600">{formatCurrency(amt)}</span>
                      </div>
                      <div className="w-full bg-slate-200 rounded-full h-1.5 mt-2">
                        <div 
                          className="bg-rose-500 h-1.5 rounded-full" 
                          style={{ width: `${pct}%` }}
                        />
                      </div>
                      <div className="text-right text-[11px] text-slate-400 mt-1">
                        মোট খরচের {toBengaliNumber(pct)}%
                      </div>
                    </div>
                  );
                })}
              </div>
            )}
          </div>

          {/* Recent Deposits Summary Table */}
          <div className="bg-white rounded-2xl p-5 shadow-sm border border-slate-200">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-base font-bold text-slate-800 flex items-center gap-2">
                <ArrowDownLeft className="w-5 h-5 text-emerald-700" />
                <span>সাম্প্রতিক চাঁদা ও জমার তালিকা</span>
              </h3>
              <button
                onClick={() => onSelectTab('deposits')}
                className="text-xs font-semibold text-emerald-700 hover:text-emerald-800 hover:underline"
              >
                সব জমা দেখুন &rarr;
              </button>
            </div>

            <div className="overflow-x-auto">
              <table className="w-full text-left text-sm text-slate-600">
                <thead className="bg-slate-50 text-slate-700 text-xs font-bold uppercase border-b border-slate-200">
                  <tr>
                    <th className="py-2.5 px-3">তারিখ</th>
                    <th className="py-2.5 px-3">সদস্যের নাম</th>
                    <th className="py-2.5 px-3">খাত</th>
                    <th className="py-2.5 px-3">টাকা</th>
                    <th className="py-2.5 px-3 text-right">পেমেন্ট</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {monthDeposits.slice(0, 5).map((dep) => (
                    <tr key={dep.id} className="hover:bg-slate-50/80 transition-colors">
                      <td className="py-2.5 px-3 text-xs font-medium text-slate-500 whitespace-nowrap">
                        {dep.date}
                      </td>
                      <td className="py-2.5 px-3 font-semibold text-slate-800">
                        {dep.memberName}
                      </td>
                      <td className="py-2.5 px-3 text-xs text-slate-600">
                        <span className="px-2 py-0.5 rounded-full bg-slate-100 text-slate-700">
                          {dep.category}
                        </span>
                      </td>
                      <td className="py-2.5 px-3 font-bold text-emerald-600 whitespace-nowrap">
                        {formatCurrency(dep.amount)}
                      </td>
                      <td className="py-2.5 px-3 text-right text-xs text-slate-500">
                        {dep.paymentMethod}
                      </td>
                    </tr>
                  ))}
                  {monthDeposits.length === 0 && (
                    <tr>
                      <td colSpan={5} className="py-4 text-center text-sm text-slate-400">
                        কোনো জমার রেকর্ড পাওয়া যায়নি।
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </div>

        {/* Right 1 Col: Monthly Due / Unpaid Members List */}
        <div className="space-y-6">
          <div className="bg-white rounded-2xl p-5 shadow-sm border border-slate-200">
            <div className="flex items-center justify-between mb-4">
              <h3 className="text-base font-bold text-slate-800 flex items-center gap-2">
                <Clock className="w-5 h-5 text-amber-600" />
                <span>বকেয়া সদস্য তালিকা</span>
              </h3>
              <span className="text-xs font-bold text-amber-800 bg-amber-100 px-2 py-0.5 rounded-full">
                {toBengaliNumber(unpaidMembers.length)} জন বাকি
              </span>
            </div>

            <p className="text-xs text-slate-500 mb-3">
              {formatMonthBangla(selectedMonth)} মাসের নির্ধারিত চাঁদা যারা এখনো জমা দেননি:
            </p>

            {unpaidMembers.length === 0 ? (
              <div className="p-4 bg-emerald-50 rounded-xl border border-emerald-200 text-center">
                <CheckCircle2 className="w-8 h-8 text-emerald-600 mx-auto mb-1" />
                <p className="text-sm font-bold text-emerald-800">অসাধারণ!</p>
                <p className="text-xs text-emerald-700">সব সদস্য চলতি মাসের চাঁদা পরিশোধ করেছেন।</p>
              </div>
            ) : (
              <div className="space-y-2 max-h-[380px] overflow-y-auto pr-1">
                {unpaidMembers.map((member) => (
                  <div
                    key={member.id}
                    className="p-3 rounded-xl bg-slate-50 border border-slate-200 flex items-center justify-between gap-2 hover:bg-slate-100 transition"
                  >
                    <div>
                      <h4 
                        onClick={() => onOpenMemberProfile(member)}
                        className="text-sm font-bold text-slate-800 hover:text-emerald-700 cursor-pointer"
                      >
                        {member.name}
                      </h4>
                      <p className="text-xs text-slate-500">
                        {member.designation} • {member.phone}
                      </p>
                      <p className="text-[11px] font-semibold text-rose-600 mt-0.5">
                        নির্ধারিত চাঁদা: {formatCurrency(member.monthlyFee)}
                      </p>
                    </div>

                    <button
                      onClick={() => onOpenAddDeposit(member.id)}
                      className="px-2.5 py-1.5 rounded-lg bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-semibold shadow-sm transition active:scale-95 flex-shrink-0"
                    >
                      জমা নিন
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>

          {/* Quick Info Box */}
          <div className="bg-emerald-900 text-emerald-50 rounded-2xl p-5 shadow-sm">
            <h4 className="text-sm font-bold text-white flex items-center gap-2 mb-2">
              <span>🌾 দক্ষিণ লতিবপুর যুব সংগঠন</span>
            </h4>
            <p className="text-xs text-emerald-200 leading-relaxed">
              স্বচ্ছ হিসাব, সামাজিক ঐক্য ও যুব সমাজের উন্নয়নের লক্ষ্যে সকল আর্থিক কার্যক্রম ডিজিটালভাবে সংরক্ষিত। যেকোনো প্রয়োজনে কোষাধ্যক্ষ ও সাধারণ সম্পাদকের সাথে যোগাযোগ করুন।
            </p>
          </div>
        </div>

      </div>
    </div>
  );
};
