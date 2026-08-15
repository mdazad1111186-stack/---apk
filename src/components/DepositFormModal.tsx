import React, { useState, useEffect } from 'react';
import { X, ArrowDownLeft } from 'lucide-react';
import { DepositTransaction, Member } from '../types';
import { getTodayDateString, getCurrentMonthCode, getAvailableMonthList } from '../utils/formatters';

interface DepositFormModalProps {
  isOpen: boolean;
  depositToEdit: DepositTransaction | null;
  preselectedMemberId?: number;
  members: Member[];
  onClose: () => void;
  onSave: (deposit: DepositTransaction) => void;
}

export const DepositFormModal: React.FC<DepositFormModalProps> = ({
  isOpen,
  depositToEdit,
  preselectedMemberId,
  members,
  onClose,
  onSave,
}) => {
  if (!isOpen) return null;

  const monthOptions = getAvailableMonthList();

  const [memberId, setMemberId] = useState<number>(0);
  const [memberName, setMemberName] = useState('');
  const [amount, setAmount] = useState<number>(200);
  const [category, setCategory] = useState('মাসিক চাঁদা');
  const [targetMonth, setTargetMonth] = useState(getCurrentMonthCode());
  const [date, setDate] = useState(getTodayDateString());
  const [paymentMethod, setPaymentMethod] = useState('নগদ');
  const [receiptNo, setReceiptNo] = useState('');
  const [note, setNote] = useState('');
  const [collectedBy, setCollectedBy] = useState('মোঃ আরিফুল ইসলাম');

  useEffect(() => {
    if (depositToEdit) {
      setMemberId(depositToEdit.memberId);
      setMemberName(depositToEdit.memberName);
      setAmount(depositToEdit.amount);
      setCategory(depositToEdit.category);
      setTargetMonth(depositToEdit.targetMonth);
      setDate(depositToEdit.date);
      setPaymentMethod(depositToEdit.paymentMethod);
      setReceiptNo(depositToEdit.receiptNo);
      setNote(depositToEdit.note || '');
      setCollectedBy(depositToEdit.collectedBy || '');
    } else if (preselectedMemberId) {
      const selected = members.find(m => m.id === preselectedMemberId);
      if (selected) {
        setMemberId(selected.id);
        setMemberName(selected.name);
        setAmount(selected.monthlyFee || 200);
      }
      const randomReceipt = `REC-${new Date().getFullYear().toString().slice(2)}${String(new Date().getMonth() + 1).padStart(2, '0')}-${Math.floor(10 + Math.random() * 90)}`;
      setReceiptNo(randomReceipt);
      setDate(getTodayDateString());
      setTargetMonth(getCurrentMonthCode());
    } else {
      setMemberId(0);
      setMemberName('');
      setAmount(200);
      setCategory('মাসিক চাঁদা');
      setTargetMonth(getCurrentMonthCode());
      setDate(getTodayDateString());
      setPaymentMethod('নগদ');
      const randomReceipt = `REC-${new Date().getFullYear().toString().slice(2)}${String(new Date().getMonth() + 1).padStart(2, '0')}-${Math.floor(10 + Math.random() * 90)}`;
      setReceiptNo(randomReceipt);
      setNote('');
      setCollectedBy('মোঃ আরিফুল ইসলাম');
    }
  }, [depositToEdit, preselectedMemberId, members]);

  const handleMemberChange = (id: number) => {
    setMemberId(id);
    const m = members.find(item => item.id === id);
    if (m) {
      setMemberName(m.name);
      if (!depositToEdit && category === 'মাসিক চাঁদা') {
        setAmount(m.monthlyFee);
      }
    } else {
      setMemberName('');
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!memberName.trim()) {
      alert('অনুগ্রহ করে সদস্যের নাম নির্বাচন করুন বা লিখুন');
      return;
    }
    if (amount <= 0) {
      alert('টাকার পরিমাণ অবশ্যই ০-এর বেশি হতে হবে');
      return;
    }

    const payload: DepositTransaction = {
      id: depositToEdit ? depositToEdit.id : Date.now(),
      memberId: memberId,
      memberName: memberName.trim(),
      amount: Number(amount),
      category,
      targetMonth,
      date,
      timestamp: depositToEdit ? depositToEdit.timestamp : Date.now(),
      paymentMethod,
      receiptNo: receiptNo.trim() || `REC-${Date.now().toString().slice(-4)}`,
      note: note.trim(),
      collectedBy: collectedBy.trim(),
    };

    onSave(payload);
  };

  return (
    <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-3 sm:p-4 overflow-y-auto">
      <div className="bg-white w-full max-w-lg rounded-2xl shadow-2xl border border-slate-100 overflow-hidden">
        
        {/* Header */}
        <div className="bg-emerald-800 text-white px-5 py-4 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <ArrowDownLeft className="w-5 h-5 text-emerald-300" />
            <h3 className="text-base sm:text-lg font-bold">
              {depositToEdit ? 'জমার তথ্য সম্পাদন (Edit)' : 'নতুন চাঁদা বা অর্থ জমা এন্ট্রি'}
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
          
          {/* Member Selection */}
          <div>
            <label className="block text-slate-700 font-bold mb-1">
              সদস্য নির্বাচন করুন *
            </label>
            <select
              value={memberId}
              onChange={(e) => handleMemberChange(Number(e.target.value))}
              className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3 py-2 text-slate-800 focus:ring-2 focus:ring-emerald-500 font-medium"
            >
              <option value={0}>-- তালিকা থেকে সদস্য বেছে নিন --</option>
              {members.map((m) => (
                <option key={m.id} value={m.id}>
                  {m.name} ({m.designation} - মাসিক ৳{m.monthlyFee})
                </option>
              ))}
            </select>
          </div>

          {/* Or Custom Name */}
          {memberId === 0 && (
            <div>
              <label className="block text-slate-700 font-bold mb-1">
                সদস্য বা দাতার নাম (যদি তালিকায় না থাকে)
              </label>
              <input
                type="text"
                value={memberName}
                onChange={(e) => setMemberName(e.target.value)}
                placeholder="নাম লিখুন..."
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3 py-2 text-slate-800 focus:ring-2 focus:ring-emerald-500"
              />
            </div>
          )}

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
                value={amount}
                onChange={(e) => setAmount(Number(e.target.value))}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3 py-2 text-slate-800 font-bold text-base focus:ring-2 focus:ring-emerald-500"
              />
            </div>

            <div>
              <label className="block text-slate-700 font-bold mb-1">
                জমার খাত / ধরণ
              </label>
              <select
                value={category}
                onChange={(e) => setCategory(e.target.value)}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3 py-2 text-slate-800 focus:ring-2 focus:ring-emerald-500"
              >
                <option value="মাসিক চাঁদা">মাসিক চাঁদা</option>
                <option value="বিশেষ অনুদান">বিশেষ অনুদান</option>
                <option value="ভর্তি ফি">ভর্তি ফি</option>
                <option value="অন্যান্য">অন্যান্য</option>
              </select>
            </div>
          </div>

          {/* Target Month & Date */}
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-slate-700 font-bold mb-1">
                কোন মাসের চাঁদা?
              </label>
              <select
                value={targetMonth}
                onChange={(e) => setTargetMonth(e.target.value)}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3 py-2 text-slate-800 focus:ring-2 focus:ring-emerald-500 font-medium"
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
                জমার তারিখ
              </label>
              <input
                type="date"
                required
                value={date}
                onChange={(e) => setDate(e.target.value)}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3 py-2 text-slate-800 focus:ring-2 focus:ring-emerald-500 font-medium"
              />
            </div>
          </div>

          {/* Payment Method & Receipt No */}
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-slate-700 font-bold mb-1">
                পেমেন্ট মাধ্যম
              </label>
              <select
                value={paymentMethod}
                onChange={(e) => setPaymentMethod(e.target.value)}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3 py-2 text-slate-800 focus:ring-2 focus:ring-emerald-500"
              >
                <option value="নগদ">নগদ</option>
                <option value="বিকাশ">বিকাশ</option>
                <option value="নগদ (Nagad)">নগদ (Mobile)</option>
                <option value="রকেট">রকেট</option>
                <option value="ব্যাংক">ব্যাংক একাউন্ট</option>
              </select>
            </div>

            <div>
              <label className="block text-slate-700 font-bold mb-1">
                রসিদ নম্বর (Receipt No)
              </label>
              <input
                type="text"
                value={receiptNo}
                onChange={(e) => setReceiptNo(e.target.value)}
                placeholder="REC-2608-01"
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3 py-2 text-slate-800 font-mono text-xs focus:ring-2 focus:ring-emerald-500"
              />
            </div>
          </div>

          {/* Collected By */}
          <div>
            <label className="block text-slate-700 font-bold mb-1">
              অর্থ সংগ্রাহক / আদায়কারী
            </label>
            <input
              type="text"
              value={collectedBy}
              onChange={(e) => setCollectedBy(e.target.value)}
              placeholder="যিনি টাকা গ্রহণ করেছেন..."
              className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3 py-2 text-slate-800 focus:ring-2 focus:ring-emerald-500"
            />
          </div>

          {/* Notes */}
          <div>
            <label className="block text-slate-700 font-bold mb-1">
              মন্তব্য (ঐচ্ছিক)
            </label>
            <textarea
              rows={2}
              value={note}
              onChange={(e) => setNote(e.target.value)}
              placeholder="ট্রানজেকশন আইডি বা অতিরিক্ত তথ্য..."
              className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3 py-2 text-slate-800 focus:ring-2 focus:ring-emerald-500"
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
              className="px-5 py-2 rounded-xl bg-emerald-700 hover:bg-emerald-800 text-white font-bold text-xs shadow-md transition active:scale-95"
            >
              {depositToEdit ? 'আপডেট করুন' : 'জমা সংরক্ষণ করুন'}
            </button>
          </div>
        </form>

      </div>
    </div>
  );
};
