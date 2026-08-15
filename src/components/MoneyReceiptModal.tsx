import React from 'react';
import { X, Printer, CheckCircle } from 'lucide-react';
import { DepositTransaction } from '../types';
import { formatCurrency, formatMonthBangla, toBengaliNumber } from '../utils/formatters';

interface MoneyReceiptModalProps {
  deposit: DepositTransaction | null;
  onClose: () => void;
}

export const MoneyReceiptModal: React.FC<MoneyReceiptModalProps> = ({
  deposit,
  onClose,
}) => {
  if (!deposit) return null;

  const handlePrint = () => {
    window.print();
  };

  return (
    <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-3 sm:p-4 overflow-y-auto">
      <div className="bg-white w-full max-w-lg rounded-2xl shadow-2xl border border-slate-200 overflow-hidden">
        
        {/* Top Control Bar */}
        <div className="bg-slate-100 px-4 py-3 border-b border-slate-200 flex items-center justify-between no-print">
          <span className="text-xs font-bold text-slate-600">ডিজিটাল মানি রিসিট (Money Receipt)</span>
          <div className="flex items-center gap-2">
            <button
              onClick={handlePrint}
              className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg bg-emerald-700 hover:bg-emerald-800 text-white text-xs font-semibold shadow-sm transition active:scale-95"
            >
              <Printer className="w-3.5 h-3.5" />
              <span>প্রিন্ট / সেভ করুন</span>
            </button>
            <button
              onClick={onClose}
              className="p-1.5 text-slate-400 hover:text-slate-700 hover:bg-slate-200 rounded-lg transition"
            >
              <X className="w-4 h-4" />
            </button>
          </div>
        </div>

        {/* Printable Receipt Paper */}
        <div className="p-6 sm:p-8 bg-white border-4 border-dashed border-emerald-700/30 m-4 rounded-xl relative">
          {/* Watermark */}
          <div className="absolute inset-0 flex items-center justify-center opacity-[0.04] pointer-events-none text-9xl font-black">
            🌿
          </div>

          {/* Org Header */}
          <div className="text-center pb-4 border-b-2 border-emerald-800">
            <h2 className="text-lg sm:text-xl font-extrabold text-emerald-900 leading-tight">
              দক্ষিণ লতিবপুর যুব উন্নয়ন সংগঠন
            </h2>
            <p className="text-[11px] text-slate-600 font-medium">
              দক্ষিণ লতিবপুর, বাংলাদেশ • ঐক্য, সেবা ও যুব কল্যাণ
            </p>
            <div className="inline-block mt-1.5 px-3 py-0.5 rounded-full bg-emerald-100 text-emerald-900 text-xs font-bold">
              জমা রসিদ / MONEY RECEIPT
            </div>
          </div>

          {/* Receipt Info */}
          <div className="grid grid-cols-2 text-xs py-3 gap-2 border-b border-slate-200">
            <div>
              <span className="text-slate-500">রসিদ নম্বর:</span>
              <strong className="block font-mono text-slate-800 text-sm">
                {deposit.receiptNo || `#REC-${toBengaliNumber(deposit.id)}`}
              </strong>
            </div>
            <div className="text-right">
              <span className="text-slate-500">তারিখ:</span>
              <strong className="block text-slate-800 text-sm">{deposit.date}</strong>
            </div>
          </div>

          {/* Core Body Fields */}
          <div className="py-4 space-y-3 text-xs sm:text-sm">
            <div className="flex justify-between border-b border-slate-100 pb-1.5">
              <span className="text-slate-500">সদস্যের নাম:</span>
              <strong className="text-slate-900 font-bold">{deposit.memberName}</strong>
            </div>

            <div className="flex justify-between border-b border-slate-100 pb-1.5">
              <span className="text-slate-500">জমার উদ্দেশ্য / খাত:</span>
              <span className="text-slate-800 font-semibold">{deposit.category}</span>
            </div>

            <div className="flex justify-between border-b border-slate-100 pb-1.5">
              <span className="text-slate-500">হিসাবের মাস:</span>
              <span className="text-slate-800 font-semibold">{formatMonthBangla(deposit.targetMonth)}</span>
            </div>

            <div className="flex justify-between border-b border-slate-100 pb-1.5">
              <span className="text-slate-500">পেমেন্ট মেথড:</span>
              <span className="text-slate-800 font-semibold">{deposit.paymentMethod}</span>
            </div>

            {deposit.note && (
              <div className="flex justify-between border-b border-slate-100 pb-1.5">
                <span className="text-slate-500">মন্তব্য:</span>
                <span className="text-slate-700 italic">{deposit.note}</span>
              </div>
            )}

            {/* Total Amount Box */}
            <div className="mt-4 p-3 bg-emerald-50 rounded-xl border border-emerald-200 flex items-center justify-between">
              <span className="text-xs font-bold text-emerald-900 uppercase">মোট প্রাপ্ত টাকা</span>
              <span className="text-xl sm:text-2xl font-extrabold text-emerald-800">
                {formatCurrency(deposit.amount)}
              </span>
            </div>
          </div>

          {/* Signatures */}
          <div className="pt-8 grid grid-cols-2 text-center text-xs text-slate-600 gap-4">
            <div>
              <div className="border-t border-slate-400 pt-1 font-semibold text-slate-700">
                {deposit.collectedBy || 'আদায়কারী'}
              </div>
              <span className="text-[10px] text-slate-400">অর্থ সংগ্রাহক</span>
            </div>
            <div>
              <div className="border-t border-slate-400 pt-1 font-semibold text-slate-700">
                কোষাধ্যক্ষ / সভাপতি
              </div>
              <span className="text-[10px] text-slate-400">অনুমোদিত স্বাক্ষর</span>
            </div>
          </div>

          {/* Footer Note */}
          <div className="mt-6 text-center text-[10px] text-slate-400 border-t border-slate-100 pt-2">
            এটি দক্ষিণ লতিবপুর যুব উন্নয়ন সংগঠনের একটি স্বয়ংক্রিয় সফটওয়্যার রসিদ।
          </div>
        </div>

      </div>
    </div>
  );
};
