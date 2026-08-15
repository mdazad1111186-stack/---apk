import React, { useState } from 'react';
import { X, FileSpreadsheet, Copy, Download, Check, Calendar } from 'lucide-react';
import { Member, DepositTransaction, ExpenseTransaction } from '../types';
import { 
  generateGoogleSheetsCsv, 
  downloadCsvFile 
} from '../utils/exportUtils';
import { 
  formatMonthBangla, 
  getAvailableMonthList 
} from '../utils/formatters';

interface GoogleSheetsModalProps {
  isOpen: boolean;
  selectedMonth: string;
  members: Member[];
  deposits: DepositTransaction[];
  expenses: ExpenseTransaction[];
  onClose: () => void;
}

export const GoogleSheetsModal: React.FC<GoogleSheetsModalProps> = ({
  isOpen,
  selectedMonth: defaultMonth,
  members,
  deposits,
  expenses,
  onClose,
}) => {
  if (!isOpen) return null;

  const [month, setMonth] = useState(defaultMonth || 'ALL');
  const [copied, setCopied] = useState(false);
  const monthOptions = getAvailableMonthList();

  const csvContent = generateGoogleSheetsCsv(month, deposits, expenses, members);

  const handleCopy = () => {
    navigator.clipboard.writeText(csvContent);
    setCopied(true);
    setTimeout(() => setCopied(false), 2500);
  };

  const handleDownload = () => {
    const filename = `Latifpur_Youth_Org_${month}_Statement.csv`;
    downloadCsvFile(csvContent, filename);
  };

  return (
    <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-3 sm:p-4 overflow-y-auto">
      <div className="bg-white w-full max-w-2xl rounded-2xl shadow-2xl border border-slate-100 overflow-hidden">
        
        {/* Header */}
        <div className="bg-emerald-900 text-white p-5 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="p-2 rounded-xl bg-emerald-800 border border-emerald-700 text-emerald-200">
              <FileSpreadsheet className="w-6 h-6" />
            </div>
            <div>
              <h3 className="text-lg font-bold">গুগল সিট / এক্সেলে রূপান্তর ও রপ্তানি</h3>
              <p className="text-xs text-emerald-300">
                Google Sheets ও Excel ফরম্যাটে তাৎক্ষণিক কপি ও ডাউনলোড করুন
              </p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1.5 text-white/80 hover:text-white hover:bg-white/10 rounded-lg transition"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Body */}
        <div className="p-5 sm:p-6 space-y-4 max-h-[75vh] overflow-y-auto text-xs sm:text-sm">
          
          {/* Month Selector */}
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 bg-emerald-50/70 p-3.5 rounded-xl border border-emerald-200">
            <div className="flex items-center gap-2">
              <Calendar className="w-4 h-4 text-emerald-800 flex-shrink-0" />
              <span className="font-bold text-emerald-950">হিসাবের মাস নির্বাচন করুন:</span>
            </div>
            <select
              value={month}
              onChange={(e) => setMonth(e.target.value)}
              className="bg-white border border-emerald-300 rounded-lg px-3 py-1.5 font-semibold text-slate-800 text-xs focus:ring-2 focus:ring-emerald-500"
            >
              <option value="ALL">সকল মাসের সামগ্রিক রিপোর্ট</option>
              {monthOptions.map((opt) => (
                <option key={opt.code} value={opt.code}>
                  {opt.label}
                </option>
              ))}
            </select>
          </div>

          {/* Instructions */}
          <div className="bg-slate-50 p-4 rounded-xl border border-slate-200 text-slate-700 space-y-2">
            <h4 className="font-bold text-slate-900 flex items-center gap-1.5">
              <span>💡 কীভাবে গুগল সিটে পেস্ট করবেন?</span>
            </h4>
            <ol className="list-decimal list-inside space-y-1 text-xs text-slate-600">
              <li>নিচের <strong>"ক্লিপবোর্ডে কপি করুন"</strong> বাটনে ক্লিক করুন।</li>
              <li>আপনার ব্রাউজারে <a href="https://sheets.new" target="_blank" rel="noreferrer" className="text-emerald-700 underline font-semibold">sheets.new</a> ওপেন করুন।</li>
              <li>সিটের প্রথম ঘরে (A1) ক্লিক করে পেস্ট (Ctrl+V বা Paste) করুন। পুরো টেবিল স্বয়ংক্রিয়ভাবে বসে যাবে!</li>
              <li>অথবা সরাসরি <strong>.CSV ফাইল ডাউনলোড</strong> করে এক্সেলে খুলুন।</li>
            </ol>
          </div>

          {/* Preview Text Box */}
          <div>
            <label className="block text-slate-700 font-bold mb-1">
              সিট প্রিভিউ (Preview):
            </label>
            <textarea
              readOnly
              rows={8}
              value={csvContent}
              className="w-full bg-slate-900 text-emerald-400 font-mono text-[11px] p-3 rounded-xl border border-slate-700 select-all leading-relaxed"
            />
          </div>

          {/* Action Buttons */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3 pt-2">
            <button
              onClick={handleCopy}
              className="flex items-center justify-center gap-2 py-2.5 px-4 rounded-xl bg-emerald-700 hover:bg-emerald-800 text-white font-bold text-xs shadow transition active:scale-95"
            >
              {copied ? (
                <>
                  <Check className="w-4 h-4 text-emerald-300" />
                  <span>কপি সফল হয়েছে! সিটে পেস্ট করুন</span>
                </>
              ) : (
                <>
                  <Copy className="w-4 h-4" />
                  <span>ক্লিপবোর্ডে কপি করুন (Copy to Sheets)</span>
                </>
              )}
            </button>

            <button
              onClick={handleDownload}
              className="flex items-center justify-center gap-2 py-2.5 px-4 rounded-xl bg-slate-800 hover:bg-slate-900 text-white font-bold text-xs shadow transition active:scale-95"
            >
              <Download className="w-4 h-4" />
              <span>CSV ফাইল ডাউনলোড করুন (.csv)</span>
            </button>
          </div>

        </div>

        {/* Footer */}
        <div className="bg-slate-50 px-6 py-3.5 border-t border-slate-200 flex justify-end">
          <button
            onClick={onClose}
            className="px-4 py-2 bg-slate-200 hover:bg-slate-300 text-slate-800 font-semibold text-xs rounded-xl transition"
          >
            বন্ধ করুন
          </button>
        </div>

      </div>
    </div>
  );
};
