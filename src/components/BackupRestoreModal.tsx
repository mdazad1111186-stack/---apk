import React, { useState } from 'react';
import { X, Database, Download, Upload, Copy, Check, RotateCcw, AlertTriangle } from 'lucide-react';
import { Member, DepositTransaction, ExpenseTransaction } from '../types';
import { generateJsonBackup, downloadJsonFile } from '../utils/exportUtils';
import { toBengaliNumber } from '../utils/formatters';

interface BackupRestoreModalProps {
  isOpen: boolean;
  members: Member[];
  deposits: DepositTransaction[];
  expenses: ExpenseTransaction[];
  onClose: () => void;
  onRestore: (data: { members: Member[]; deposits: DepositTransaction[]; expenses: ExpenseTransaction[] }) => void;
  onResetToSample: () => void;
}

export const BackupRestoreModal: React.FC<BackupRestoreModalProps> = ({
  isOpen,
  members,
  deposits,
  expenses,
  onClose,
  onRestore,
  onResetToSample,
}) => {
  if (!isOpen) return null;

  const [copied, setCopied] = useState(false);
  const [restoreText, setRestoreText] = useState('');
  const [statusMsg, setStatusMsg] = useState<{ type: 'success' | 'error'; text: string } | null>(null);

  const jsonBackup = generateJsonBackup(members, deposits, expenses);

  const handleCopy = () => {
    navigator.clipboard.writeText(jsonBackup);
    setCopied(true);
    setTimeout(() => setCopied(false), 2500);
  };

  const handleDownload = () => {
    const filename = `Latifpur_Youth_Org_Backup_${new Date().toISOString().slice(0, 10)}.json`;
    downloadJsonFile(jsonBackup, filename);
  };

  const handleRestore = () => {
    try {
      if (!restoreText.trim()) {
        setStatusMsg({ type: 'error', text: 'অনুগ্রহ করে ব্যাকআপ JSON কোড পেস্ট করুন' });
        return;
      }
      const parsed = JSON.parse(restoreText);
      if (!parsed.members || !Array.isArray(parsed.members)) {
        throw new Error('অবৈধ ব্যাকআপ ফাইল স্ট্রাকচার');
      }

      onRestore({
        members: parsed.members || [],
        deposits: parsed.deposits || [],
        expenses: parsed.expenses || [],
      });

      setStatusMsg({
        type: 'success',
        text: `সফলভাবে ${toBengaliNumber(parsed.members.length)} জন সদস্য, ${toBengaliNumber(parsed.deposits?.length || 0)} টি জমা ও ${toBengaliNumber(parsed.expenses?.length || 0)} টি খরচের তথ্য রিস্টোর করা হয়েছে!`,
      });
      setRestoreText('');
    } catch (e: any) {
      setStatusMsg({ type: 'error', text: `রিস্টোর ব্যর্থ হয়েছে: ${e.message || 'ভুল ফরম্যাট'}` });
    }
  };

  const handleFileUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = (event) => {
      const content = event.target?.result as string;
      setRestoreText(content);
    };
    reader.readAsText(file);
  };

  return (
    <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-3 sm:p-4 overflow-y-auto">
      <div className="bg-white w-full max-w-2xl rounded-2xl shadow-2xl border border-slate-100 overflow-hidden">
        
        {/* Header */}
        <div className="bg-slate-900 text-white p-5 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="p-2 rounded-xl bg-slate-800 border border-slate-700 text-emerald-400">
              <Database className="w-6 h-6" />
            </div>
            <div>
              <h3 className="text-lg font-bold">ডাটা ব্যাকআপ ও রিস্টোর ব্যবস্থাপনা</h3>
              <p className="text-xs text-slate-400">
                আপনার সকল ডাটা লোকাল ডিভাইসে সুরক্ষিত ও স্থানান্তরযোগ্য
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
        <div className="p-5 sm:p-6 space-y-6 max-h-[75vh] overflow-y-auto text-xs sm:text-sm">
          
          {/* Status Message */}
          {statusMsg && (
            <div
              className={`p-3 rounded-xl border flex items-center gap-2 ${
                statusMsg.type === 'success'
                  ? 'bg-emerald-50 text-emerald-900 border-emerald-300'
                  : 'bg-rose-50 text-rose-900 border-rose-300'
              }`}
            >
              <span className="font-semibold">{statusMsg.text}</span>
            </div>
          )}

          {/* 1. Export Section */}
          <div className="bg-slate-50 p-4 rounded-xl border border-slate-200 space-y-3">
            <div className="flex items-center justify-between">
              <h4 className="font-bold text-slate-800 text-sm flex items-center gap-2">
                <Download className="w-4 h-4 text-emerald-600" />
                <span>১. সম্পূর্ণ ডাটা ব্যাকআপ ডাউনলোড (Backup)</span>
              </h4>
              <span className="text-xs text-slate-500 font-semibold">
                {toBengaliNumber(members.length)} সদস্য • {toBengaliNumber(deposits.length)} জমা • {toBengaliNumber(expenses.length)} খরচ
              </span>
            </div>

            <p className="text-xs text-slate-600">
              সকল তথ্য একটি সুরক্ষিত JSON ফাইলে রূপান্তর করে ডাউনলোড বা কপি করে সংরক্ষণ করে রাখুন।
            </p>

            <div className="flex flex-wrap gap-2">
              <button
                onClick={handleDownload}
                className="flex items-center gap-1.5 py-2 px-3.5 rounded-lg bg-emerald-700 hover:bg-emerald-800 text-white font-bold text-xs shadow transition active:scale-95"
              >
                <Download className="w-4 h-4" />
                <span>JSON ব্যাকআপ ফাইল ডাউনলোড</span>
              </button>

              <button
                onClick={handleCopy}
                className="flex items-center gap-1.5 py-2 px-3.5 rounded-lg bg-slate-200 hover:bg-slate-300 text-slate-800 font-semibold text-xs transition"
              >
                {copied ? <Check className="w-4 h-4 text-emerald-600" /> : <Copy className="w-4 h-4" />}
                <span>{copied ? 'কপি হয়েছে!' : 'ক্লিপবোর্ডে কপি'}</span>
              </button>
            </div>
          </div>

          {/* 2. Restore Section */}
          <div className="bg-slate-50 p-4 rounded-xl border border-slate-200 space-y-3">
            <h4 className="font-bold text-slate-800 text-sm flex items-center gap-2">
              <Upload className="w-4 h-4 text-blue-600" />
              <span>২. পূর্বে সংরক্ষিত ব্যাকআপ রিস্টোর (Restore)</span>
            </h4>

            <p className="text-xs text-slate-600">
              আপনার ব্যাকআপ ফাইল (.json) সিলেক্ট করুন অথবা টেক্সট কোড নিচে পেস্ট করুন:
            </p>

            <input
              type="file"
              accept=".json"
              onChange={handleFileUpload}
              className="text-xs text-slate-600 file:mr-3 file:py-1.5 file:px-3 file:rounded-lg file:border-0 file:text-xs file:font-semibold file:bg-blue-100 file:text-blue-800 hover:file:bg-blue-200 cursor-pointer"
            />

            <textarea
              rows={4}
              value={restoreText}
              onChange={(e) => setRestoreText(e.target.value)}
              placeholder="অথবা ব্যাকআপ JSON কোড এখানে পেস্ট করুন..."
              className="w-full bg-white font-mono text-[11px] p-2.5 rounded-xl border border-slate-300 text-slate-800 focus:ring-2 focus:ring-blue-500"
            />

            <button
              onClick={handleRestore}
              className="flex items-center gap-1.5 py-2 px-4 rounded-lg bg-blue-600 hover:bg-blue-700 text-white font-bold text-xs shadow transition active:scale-95"
            >
              <Upload className="w-4 h-4" />
              <span>ডাটা রিস্টোর নিশ্চিত করুন</span>
            </button>
          </div>

          {/* 3. Reset Section */}
          <div className="p-4 rounded-xl border border-rose-200 bg-rose-50/60 flex items-center justify-between gap-3">
            <div>
              <h5 className="font-bold text-rose-900 flex items-center gap-1.5 text-xs">
                <AlertTriangle className="w-4 h-4 text-rose-600" />
                <span>নমুনা ডাটায় পুনরায় রিসেট (Sample Data)</span>
              </h5>
              <p className="text-[11px] text-rose-700 mt-0.5">
                সংগঠনের প্রারম্ভিক নমুনা তথ্যসমূহ আবার লোড করতে চান?
              </p>
            </div>
            <button
              onClick={() => {
                if (window.confirm('আপনি কি নিশ্চিত যে সকল ডাটা মুছে প্রাথমিক নমুনা ডাটায় রিসেট করতে চান?')) {
                  onResetToSample();
                  setStatusMsg({ type: 'success', text: 'সফলভাবে প্রারম্ভিক নমুনা ডাটা সেট করা হয়েছে।' });
                }
              }}
              className="py-1.5 px-3 rounded-lg bg-rose-600 hover:bg-rose-700 text-white font-bold text-xs shadow transition whitespace-nowrap"
            >
              <RotateCcw className="w-3.5 h-3.5 inline mr-1" />
              রিসেট করুন
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
