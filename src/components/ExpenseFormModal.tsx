import React, { useState, useEffect } from 'react';
import { X, ArrowUpRight } from 'lucide-react';
import { ExpenseTransaction } from '../types';
import { getTodayDateString, getCurrentMonthCode, getAvailableMonthList } from '../utils/formatters';

interface ExpenseFormModalProps {
  isOpen: boolean;
  expenseToEdit: ExpenseTransaction | null;
  onClose: () => void;
  onSave: (expense: ExpenseTransaction) => void;
}

export const ExpenseFormModal: React.FC<ExpenseFormModalProps> = ({
  isOpen,
  expenseToEdit,
  onClose,
  onSave,
}) => {
  if (!isOpen) return null;

  const monthOptions = getAvailableMonthList();

  const [title, setTitle] = useState('');
  const [amount, setAmount] = useState<number>(0);
  const [category, setCategory] = useState('সমাজকল্যাণ');
  const [targetMonth, setTargetMonth] = useState(getCurrentMonthCode());
  const [date, setDate] = useState(getTodayDateString());
  const [spentBy, setSpentBy] = useState('');
  const [voucherNo, setVoucherNo] = useState('');
  const [note, setNote] = useState('');

  useEffect(() => {
    if (expenseToEdit) {
      setTitle(expenseToEdit.title);
      setAmount(expenseToEdit.amount);
      setCategory(expenseToEdit.category);
      setTargetMonth(expenseToEdit.targetMonth);
      setDate(expenseToEdit.date);
      setSpentBy(expenseToEdit.spentBy);
      setVoucherNo(expenseToEdit.voucherNo);
      setNote(expenseToEdit.note || '');
    } else {
      setTitle('');
      setAmount(0);
      setCategory('সমাজকল্যাণ');
      setTargetMonth(getCurrentMonthCode());
      setDate(getTodayDateString());
      setSpentBy('');
      const randomVoucher = `EXP-${new Date().getFullYear().toString().slice(2)}${String(new Date().getMonth() + 1).padStart(2, '0')}-${Math.floor(10 + Math.random() * 90)}`;
      setVoucherNo(randomVoucher);
      setNote('');
    }
  }, [expenseToEdit]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!title.trim()) {
      alert('অনুগ্রহ করে খরচের বিবরণ বা শিরোনাম লিখুন');
      return;
    }
    if (amount <= 0) {
      alert('টাকার পরিমাণ অবশ্যই ০-এর বেশি হতে হবে');
      return;
    }

    const payload: ExpenseTransaction = {
      id: expenseToEdit ? expenseToEdit.id : Date.now(),
      title: title.trim(),
      amount: Number(amount),
      category,
      targetMonth,
      date,
      timestamp: expenseToEdit ? expenseToEdit.timestamp : Date.now(),
      spentBy: spentBy.trim(),
      voucherNo: voucherNo.trim() || `EXP-${Date.now().toString().slice(-4)}`,
      note: note.trim(),
    };

    onSave(payload);
  };

  return (
    <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-3 sm:p-4 overflow-y-auto">
      <div className="bg-white w-full max-w-lg rounded-2xl shadow-2xl border border-slate-100 overflow-hidden">
        
        {/* Header */}
        <div className="bg-rose-700 text-white px-5 py-4 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <ArrowUpRight className="w-5 h-5 text-rose-200" />
            <h3 className="text-base sm:text-lg font-bold">
              {expenseToEdit ? 'খরচের ভাউচার সম্পাদন (Edit)' : 'নতুন খরচ বা ব্যয়ের ভাউচার এন্ট্রি'}
            </h3>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 text-white/80 hover:text-white hover:bg-white/10 rounded-lg transition"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="p-5 sm:p-6 space-y-4 max-h-[75vh] overflow-y-auto text-xs sm:text-sm">
          
          {/* Title */}
          <div>
            <label className="block text-slate-700 font-bold mb-1">
              খরচের বিবরণ / কাজের নাম *
            </label>
            <input
              type="text"
              required
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              placeholder="উদাঃ অসহায় রোগীর ঔষধ সহায়তা / মিটিং আপ্যায়ন..."
              className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3 py-2 text-slate-800 focus:ring-2 focus:ring-rose-500 font-medium"
            />
          </div>

          {/* Amount & Category */}
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-slate-700 font-bold mb-1">
                টাকার পরিমাণ (৳) *
              </label>
              <input
                type="number"
                min="1"
                required
                value={amount || ''}
                onChange={(e) => setAmount(Number(e.target.value))}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3 py-2 text-slate-800 font-bold text-base focus:ring-2 focus:ring-rose-500"
              />
            </div>

            <div>
              <label className="block text-slate-700 font-bold mb-1">
                ব্যয়ের খাত
              </label>
              <select
                value={category}
                onChange={(e) => setCategory(e.target.value)}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3 py-2 text-slate-800 focus:ring-2 focus:ring-rose-500"
              >
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

          {/* Target Month & Date */}
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-slate-700 font-bold mb-1">
                কোন মাসের খরচ?
              </label>
              <select
                value={targetMonth}
                onChange={(e) => setTargetMonth(e.target.value)}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3 py-2 text-slate-800 focus:ring-2 focus:ring-rose-500 font-medium"
              >
                {monthOptions.map((opt) => (
                  <option key={opt.code} value={opt.code}>
                    {opt.label}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-slate-700 font-bold mb-1">
                ব্যয়ের তারিখ
              </label>
              <input
                type="date"
                required
                value={date}
                onChange={(e) => setDate(e.target.value)}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3 py-2 text-slate-800 focus:ring-2 focus:ring-rose-500 font-medium"
              />
            </div>
          </div>

          {/* Spent By & Voucher No */}
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-slate-700 font-bold mb-1">
                ব্যয়কারী / দায়িত্বপ্রাপ্ত ব্যক্তি
              </label>
              <input
                type="text"
                value={spentBy}
                onChange={(e) => setSpentBy(e.target.value)}
                placeholder="যিনি খরচ করেছেন..."
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3 py-2 text-slate-800 focus:ring-2 focus:ring-rose-500"
              />
            </div>

            <div>
              <label className="block text-slate-700 font-bold mb-1">
                ভাউচার নম্বর (Voucher No)
              </label>
              <input
                type="text"
                value={voucherNo}
                onChange={(e) => setVoucherNo(e.target.value)}
                placeholder="EXP-2608-01"
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3 py-2 text-slate-800 font-mono text-xs focus:ring-2 focus:ring-rose-500"
              />
            </div>
          </div>

          {/* Note */}
          <div>
            <label className="block text-slate-700 font-bold mb-1">
              বিস্তারিত বিবরণ / মন্তব্য
            </label>
            <textarea
              rows={2}
              value={note}
              onChange={(e) => setNote(e.target.value)}
              placeholder="রশিদ/দোকানের নাম বা অতিরিক্ত তথ্য..."
              className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3 py-2 text-slate-800 focus:ring-2 focus:ring-rose-500"
            />
          </div>

          {/* Footer Actions */}
          <div className="pt-3 border-t border-slate-200 flex items-center justify-end gap-2">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 rounded-xl bg-slate-100 hover:bg-slate-200 text-slate-700 font-semibold text-xs transition"
            >
              বাতিল
            </button>
            <button
              type="submit"
              className="px-5 py-2 rounded-xl bg-rose-600 hover:bg-rose-700 text-white font-bold text-xs shadow-md transition active:scale-95"
            >
              {expenseToEdit ? 'ভাউচার আপডেট করুন' : 'খরচ সংরক্ষণ করুন'}
            </button>
          </div>
        </form>

      </div>
    </div>
  );
};
