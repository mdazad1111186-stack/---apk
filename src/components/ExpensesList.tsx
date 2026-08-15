import React, { useState } from 'react';
import { 
  ArrowUpRight, 
  Search, 
  Plus, 
  Calendar, 
  Edit, 
  Trash2, 
  Filter,
  FileText,
  UserCheck
} from 'lucide-react';
import { ExpenseTransaction } from '../types';
import { 
  formatCurrency, 
  formatMonthBangla, 
  toBengaliNumber, 
  getAvailableMonthList 
} from '../utils/formatters';

interface ExpensesListProps {
  expenses: ExpenseTransaction[];
  selectedMonth: string;
  onSelectMonth: (m: string) => void;
  onOpenAddExpense: () => void;
  onOpenEditExpense: (exp: ExpenseTransaction) => void;
  onDeleteExpense: (exp: ExpenseTransaction) => void;
}

export const ExpensesList: React.FC<ExpensesListProps> = ({
  expenses,
  selectedMonth,
  onSelectMonth,
  onOpenAddExpense,
  onOpenEditExpense,
  onDeleteExpense,
}) => {
  const [search, setSearch] = useState('');
  const [categoryFilter, setCategoryFilter] = useState('ALL');
  const monthOptions = getAvailableMonthList();

  const filteredExpenses = expenses.filter((e) => {
    const matchesMonth = selectedMonth === 'ALL' || e.targetMonth === selectedMonth;
    const matchesSearch = 
      e.title.toLowerCase().includes(search.toLowerCase()) ||
      e.voucherNo.toLowerCase().includes(search.toLowerCase()) ||
      e.spentBy.toLowerCase().includes(search.toLowerCase()) ||
      (e.note && e.note.toLowerCase().includes(search.toLowerCase()));
    const matchesCat = categoryFilter === 'ALL' || e.category === categoryFilter;

    return matchesMonth && matchesSearch && matchesCat;
  });

  const totalFilteredAmount = filteredExpenses.reduce((acc, e) => acc + e.amount, 0);

  return (
    <div className="space-y-6 pb-12">
      {/* Header Bar */}
      <div className="bg-white rounded-2xl p-5 shadow-sm border border-slate-200 flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2">
            <span className="p-1.5 rounded-lg bg-rose-100 text-rose-800">
              <ArrowUpRight className="w-5 h-5" />
            </span>
            <h2 className="text-xl font-bold text-slate-800">
              খরচ খাতা (ব্যয় ও ভাউচার ট্র্যাকিং)
            </h2>
          </div>
          <p className="text-xs sm:text-sm text-slate-500 mt-1">
            নির্বাচিত সময়কাল: <strong>{formatMonthBangla(selectedMonth)}</strong> • মোট ব্যয়: <strong className="text-rose-600">{formatCurrency(totalFilteredAmount)}</strong> ({toBengaliNumber(filteredExpenses.length)} টি ভাউচার)
          </p>
        </div>

        <button
          onClick={onOpenAddExpense}
          className="inline-flex items-center justify-center gap-2 px-4 py-2.5 rounded-xl bg-rose-600 hover:bg-rose-700 text-white font-semibold text-sm shadow-sm transition active:scale-95 flex-shrink-0"
        >
          <Plus className="w-4 h-4" />
          <span>+ নতুন খরচ যুক্ত করুন</span>
        </button>
      </div>

      {/* Filter and Search Bar */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
        {/* Search */}
        <div className="relative sm:col-span-1">
          <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="খরচের বিবরণ, ভাউচার নং বা ব্যয়কারী..."
            className="w-full pl-10 pr-4 py-2.5 bg-white rounded-xl border border-slate-200 text-sm focus:outline-none focus:ring-2 focus:ring-rose-500 shadow-sm"
          />
        </div>

        {/* Month Selector */}
        <div className="flex items-center gap-2 bg-white px-3 py-1.5 rounded-xl border border-slate-200 shadow-sm">
          <Calendar className="w-4 h-4 text-slate-500 flex-shrink-0" />
          <select
            value={selectedMonth}
            onChange={(e) => onSelectMonth(e.target.value)}
            className="w-full bg-transparent text-sm font-semibold text-slate-700 focus:outline-none"
          >
            <option value="ALL">সকল মাসের ব্যয়</option>
            {monthOptions.map((opt) => (
              <option key={opt.code} value={opt.code}>
                {opt.label}
              </option>
            ))}
          </select>
        </div>

        {/* Category Selector */}
        <div className="flex items-center gap-2 bg-white px-3 py-1.5 rounded-xl border border-slate-200 shadow-sm">
          <Filter className="w-4 h-4 text-slate-500 flex-shrink-0" />
          <select
            value={categoryFilter}
            onChange={(e) => setCategoryFilter(e.target.value)}
            className="w-full bg-transparent text-sm font-semibold text-slate-700 focus:outline-none"
          >
            <option value="ALL">সকল ব্যয়ের খাত</option>
            <option value="সমাজকল্যাণ">সমাজকল্যাণ</option>
            <option value="চিকিৎসা সহায়তা">চিকিৎসা সহায়তা</option>
            <option value="শিক্ষা সহায়তা">শিক্ষা সহায়তা</option>
            <option value="ত্রাণ ও পুনর্বাসন">ত্রাণ ও পুনর্বাসন</option>
            <option value="অফিস ও স্টেশনারি">অফিস ও স্টেশনারি</option>
            <option value="রাস্তা সংস্কার">রাস্তা সংস্কার</option>
            <option value="মসজিদ ও ধর্মীয়">মসজিদ ও ধর্মীয়</option>
            <option value="অন্যান্য">অন্যান্য</option>
          </select>
        </div>
      </div>

      {/* Expenses Table */}
      <div className="bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm text-slate-600">
            <thead className="bg-slate-50 text-slate-700 text-xs font-bold uppercase border-b border-slate-200">
              <tr>
                <th className="py-3.5 px-4">ভাউচার ও তারিখ</th>
                <th className="py-3.5 px-4">খরচের বিবরণ</th>
                <th className="py-3.5 px-4">খাত ও উদ্দেশ্য মাস</th>
                <th className="py-3.5 px-4">টাকা (৳)</th>
                <th className="py-3.5 px-4">ব্যয়কারী / দায়িত্বপ্রাপ্ত</th>
                <th className="py-3.5 px-4 text-right">অ্যাকশন</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {filteredExpenses.map((exp) => (
                <tr key={exp.id} className="hover:bg-slate-50/80 transition-colors">
                  <td className="py-3.5 px-4 whitespace-nowrap">
                    <span className="font-mono text-xs font-bold text-slate-700 block">
                      {exp.voucherNo || `#EXP-${exp.id}`}
                    </span>
                    <span className="text-[11px] text-slate-400">
                      {exp.date}
                    </span>
                  </td>

                  <td className="py-3.5 px-4">
                    <span className="font-bold text-slate-800 block text-sm">
                      {exp.title}
                    </span>
                    {exp.note && (
                      <span className="text-xs text-slate-500 line-clamp-1 mt-0.5">
                        {exp.note}
                      </span>
                    )}
                  </td>

                  <td className="py-3.5 px-4 whitespace-nowrap">
                    <span className="inline-block px-2.5 py-0.5 rounded-full text-xs font-semibold bg-rose-50 text-rose-700 border border-rose-200">
                      {exp.category}
                    </span>
                    <span className="text-xs text-slate-400 block mt-0.5">
                      মাস: {formatMonthBangla(exp.targetMonth)}
                    </span>
                  </td>

                  <td className="py-3.5 px-4 whitespace-nowrap font-bold text-base text-rose-600">
                    {formatCurrency(exp.amount)}
                  </td>

                  <td className="py-3.5 px-4 whitespace-nowrap text-xs text-slate-700">
                    <span className="inline-flex items-center gap-1">
                      <UserCheck className="w-3.5 h-3.5 text-slate-400" />
                      {exp.spentBy || 'অজানা'}
                    </span>
                  </td>

                  <td className="py-3.5 px-4 whitespace-nowrap text-right">
                    <div className="inline-flex items-center gap-1">
                      <button
                        onClick={() => onOpenEditExpense(exp)}
                        className="p-1.5 text-slate-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition"
                        title="এডিট করুন"
                      >
                        <Edit className="w-4 h-4" />
                      </button>

                      <button
                        onClick={() => onDeleteExpense(exp)}
                        className="p-1.5 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition"
                        title="মুছে ফেলুন"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}

              {filteredExpenses.length === 0 && (
                <tr>
                  <td colSpan={6} className="py-12 text-center text-slate-400">
                    <ArrowUpRight className="w-10 h-10 text-slate-300 mx-auto mb-2" />
                    <p className="font-bold text-slate-600">কোনো খরচের ভাউচার পাওয়া যায়নি</p>
                    <p className="text-xs text-slate-400 mt-1">ফিল্টার পরিবর্তন করুন অথবা নতুন খরচের ভাউচার যুক্ত করুন।</p>
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
