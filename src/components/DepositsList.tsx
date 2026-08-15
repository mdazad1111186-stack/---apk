import React, { useState } from 'react';
import { 
  ArrowDownLeft, 
  Search, 
  Plus, 
  Calendar, 
  Edit, 
  Trash2, 
  Printer, 
  Download,
  Filter,
  CreditCard
} from 'lucide-react';
import { DepositTransaction, Member } from '../types';
import { 
  formatCurrency, 
  formatMonthBangla, 
  toBengaliNumber, 
  getAvailableMonthList 
} from '../utils/formatters';

interface DepositsListProps {
  deposits: DepositTransaction[];
  members: Member[];
  selectedMonth: string;
  onSelectMonth: (m: string) => void;
  onOpenAddDeposit: () => void;
  onOpenEditDeposit: (dep: DepositTransaction) => void;
  onDeleteDeposit: (dep: DepositTransaction) => void;
  onViewReceipt: (dep: DepositTransaction) => void;
}

export const DepositsList: React.FC<DepositsListProps> = ({
  deposits,
  members,
  selectedMonth,
  onSelectMonth,
  onOpenAddDeposit,
  onOpenEditDeposit,
  onDeleteDeposit,
  onViewReceipt,
}) => {
  const [search, setSearch] = useState('');
  const [categoryFilter, setCategoryFilter] = useState('ALL');
  const monthOptions = getAvailableMonthList();

  const filteredDeposits = deposits.filter((d) => {
    const matchesMonth = selectedMonth === 'ALL' || d.targetMonth === selectedMonth;
    const matchesSearch = 
      d.memberName.toLowerCase().includes(search.toLowerCase()) ||
      d.receiptNo.toLowerCase().includes(search.toLowerCase()) ||
      d.collectedBy.toLowerCase().includes(search.toLowerCase()) ||
      (d.note && d.note.toLowerCase().includes(search.toLowerCase()));
    const matchesCat = categoryFilter === 'ALL' || d.category === categoryFilter;

    return matchesMonth && matchesSearch && matchesCat;
  });

  const totalFilteredAmount = filteredDeposits.reduce((acc, d) => acc + d.amount, 0);

  return (
    <div className="space-y-6 pb-12">
      {/* Header Bar */}
      <div className="bg-white rounded-2xl p-5 shadow-sm border border-slate-200 flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2">
            <span className="p-1.5 rounded-lg bg-emerald-100 text-emerald-800">
              <ArrowDownLeft className="w-5 h-5" />
            </span>
            <h2 className="text-xl font-bold text-slate-800">
              জমা খাতা (চাঁদা ও অনুদান আদায়)
            </h2>
          </div>
          <p className="text-xs sm:text-sm text-slate-500 mt-1">
            নির্বাচিত সময়কাল: <strong>{formatMonthBangla(selectedMonth)}</strong> • মোট জমা: <strong className="text-emerald-700">{formatCurrency(totalFilteredAmount)}</strong> ({toBengaliNumber(filteredDeposits.length)} টি এন্ট্রি)
          </p>
        </div>

        <button
          onClick={onOpenAddDeposit}
          className="inline-flex items-center justify-center gap-2 px-4 py-2.5 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white font-semibold text-sm shadow-sm transition active:scale-95 flex-shrink-0"
        >
          <Plus className="w-4 h-4" />
          <span>+ নতুন জমা যোগ করুন</span>
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
            placeholder="সদস্যের নাম, রসিদ নম্বর বা নোট..."
            className="w-full pl-10 pr-4 py-2.5 bg-white rounded-xl border border-slate-200 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500 shadow-sm"
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
            <option value="ALL">সকল মাসের হিসাব</option>
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
            <option value="ALL">সকল জমার খাত</option>
            <option value="মাসিক চাঁদা">মাসিক চাঁদা</option>
            <option value="বিশেষ অনুদান">বিশেষ অনুদান</option>
            <option value="ভর্তি ফি">ভর্তি ফি</option>
            <option value="অন্যান্য">অন্যান্য</option>
          </select>
        </div>
      </div>

      {/* Deposits Table */}
      <div className="bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm text-slate-600">
            <thead className="bg-slate-50 text-slate-700 text-xs font-bold uppercase border-b border-slate-200">
              <tr>
                <th className="py-3.5 px-4">রসিদ নং ও তারিখ</th>
                <th className="py-3.5 px-4">সদস্যের নাম</th>
                <th className="py-3.5 px-4">খাত ও উদ্দেশ্য মাস</th>
                <th className="py-3.5 px-4">টাকা (৳)</th>
                <th className="py-3.5 px-4">পেমেন্ট মেথড</th>
                <th className="py-3.5 px-4">সংগ্রাহক</th>
                <th className="py-3.5 px-4 text-right">অ্যাকশন</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {filteredDeposits.map((dep) => (
                <tr key={dep.id} className="hover:bg-slate-50/80 transition-colors">
                  <td className="py-3.5 px-4 whitespace-nowrap">
                    <span className="font-mono text-xs font-bold text-slate-700 block">
                      {dep.receiptNo || `#${dep.id}`}
                    </span>
                    <span className="text-[11px] text-slate-400">
                      {dep.date}
                    </span>
                  </td>

                  <td className="py-3.5 px-4">
                    <span className="font-bold text-slate-800 block">
                      {dep.memberName}
                    </span>
                    {dep.note && (
                      <span className="text-xs text-slate-500 line-clamp-1">
                        {dep.note}
                      </span>
                    )}
                  </td>

                  <td className="py-3.5 px-4 whitespace-nowrap">
                    <span className="inline-block px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200">
                      {dep.category}
                    </span>
                    <span className="text-xs text-slate-400 block mt-0.5">
                      মাস: {formatMonthBangla(dep.targetMonth)}
                    </span>
                  </td>

                  <td className="py-3.5 px-4 whitespace-nowrap font-bold text-base text-emerald-600">
                    {formatCurrency(dep.amount)}
                  </td>

                  <td className="py-3.5 px-4 whitespace-nowrap text-xs text-slate-600">
                    <span className="inline-flex items-center gap-1">
                      <CreditCard className="w-3.5 h-3.5 text-slate-400" />
                      {dep.paymentMethod}
                    </span>
                  </td>

                  <td className="py-3.5 px-4 whitespace-nowrap text-xs text-slate-600">
                    {dep.collectedBy || 'অজানা'}
                  </td>

                  <td className="py-3.5 px-4 whitespace-nowrap text-right">
                    <div className="inline-flex items-center gap-1">
                      <button
                        onClick={() => onViewReceipt(dep)}
                        className="p-1.5 text-slate-400 hover:text-emerald-700 hover:bg-emerald-50 rounded-lg transition"
                        title="রশিদ দেখুন ও প্রিন্ট করুন"
                      >
                        <Printer className="w-4 h-4" />
                      </button>

                      <button
                        onClick={() => onOpenEditDeposit(dep)}
                        className="p-1.5 text-slate-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition"
                        title="এডিট করুন"
                      >
                        <Edit className="w-4 h-4" />
                      </button>

                      <button
                        onClick={() => onDeleteDeposit(dep)}
                        className="p-1.5 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition"
                        title="মুছে ফেলুন"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}

              {filteredDeposits.length === 0 && (
                <tr>
                  <td colSpan={7} className="py-12 text-center text-slate-400">
                    <ArrowDownLeft className="w-10 h-10 text-slate-300 mx-auto mb-2" />
                    <p className="font-bold text-slate-600">কোনো জমার তথ্য পাওয়া যায়নি</p>
                    <p className="text-xs text-slate-400 mt-1">ফিল্টার পরিবর্তন করুন অথবা নতুন জমা যোগ করুন।</p>
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
