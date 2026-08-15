import React from 'react';
import { 
  X, 
  Phone, 
  MapPin, 
  Droplet, 
  Calendar, 
  ArrowDownLeft, 
  UserCheck, 
  UserX,
  CreditCard,
  Printer
} from 'lucide-react';
import { Member, DepositTransaction } from '../types';
import { formatCurrency, formatMonthBangla, toBengaliNumber } from '../utils/formatters';

interface MemberProfileModalProps {
  member: Member | null;
  deposits: DepositTransaction[];
  onClose: () => void;
  onOpenAddDeposit: (memberId: number) => void;
  onViewReceipt: (dep: DepositTransaction) => void;
}

export const MemberProfileModal: React.FC<MemberProfileModalProps> = ({
  member,
  deposits,
  onClose,
  onOpenAddDeposit,
  onViewReceipt,
}) => {
  if (!member) return null;

  const memberDeposits = deposits.filter((d) => d.memberId === member.id);
  const totalPaid = memberDeposits.reduce((acc, d) => acc + d.amount, 0);

  return (
    <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-3 sm:p-4 overflow-y-auto">
      <div className="bg-white w-full max-w-2xl rounded-2xl shadow-2xl border border-slate-100 overflow-hidden animate-in fade-in zoom-in-95 duration-200">
        
        {/* Modal Header */}
        <div className="bg-gradient-to-r from-emerald-800 to-emerald-950 text-white p-5 sm:p-6 flex items-start justify-between relative">
          <div className="flex items-center gap-4">
            <div className="w-14 h-14 rounded-2xl bg-white/10 border border-white/20 flex items-center justify-center text-2xl font-bold text-emerald-200 shadow-inner">
              {member.name.charAt(0)}
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h3 className="text-xl font-bold text-white">{member.name}</h3>
                {member.isActive ? (
                  <span className="inline-flex items-center gap-1 text-[11px] font-semibold text-emerald-200 bg-emerald-700/80 px-2 py-0.5 rounded-full">
                    <UserCheck className="w-3 h-3" /> সক্রিয়
                  </span>
                ) : (
                  <span className="inline-flex items-center gap-1 text-[11px] font-semibold text-rose-200 bg-rose-700/80 px-2 py-0.5 rounded-full">
                    <UserX className="w-3 h-3" /> নিষ্ক্রিয়
                  </span>
                )}
              </div>
              <p className="text-sm text-emerald-200 mt-0.5">
                {member.designation} • সদস্য আইডি #{toBengaliNumber(member.id)}
              </p>
            </div>
          </div>

          <button
            onClick={onClose}
            className="p-2 text-white/80 hover:text-white hover:bg-white/10 rounded-xl transition"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Modal Body */}
        <div className="p-5 sm:p-6 space-y-6 max-h-[70vh] overflow-y-auto">
          
          {/* Personal Info Grid */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3.5 text-xs bg-slate-50 p-4 rounded-xl border border-slate-200">
            <div className="flex items-center gap-2">
              <Phone className="w-4 h-4 text-emerald-600 flex-shrink-0" />
              <span>মোবাইল: <strong className="text-slate-800 text-sm">{member.phone || 'দেওয়া নেই'}</strong></span>
            </div>

            {member.bloodGroup && (
              <div className="flex items-center gap-2">
                <Droplet className="w-4 h-4 text-rose-500 flex-shrink-0" />
                <span>রক্তের গ্রুপ: <strong className="text-rose-700 text-sm">{member.bloodGroup}</strong></span>
              </div>
            )}

            {member.address && (
              <div className="flex items-center gap-2 sm:col-span-2">
                <MapPin className="w-4 h-4 text-emerald-600 flex-shrink-0" />
                <span>ঠিকানা: <strong className="text-slate-800">{member.address}</strong></span>
              </div>
            )}

            {member.joinDate && (
              <div className="flex items-center gap-2">
                <Calendar className="w-4 h-4 text-emerald-600 flex-shrink-0" />
                <span>যোগদানের তারিখ: <strong className="text-slate-800">{member.joinDate}</strong></span>
              </div>
            )}

            {member.notes && (
              <div className="sm:col-span-2 text-slate-600 italic bg-white p-2.5 rounded-lg border border-slate-200">
                "{member.notes}"
              </div>
            )}
          </div>

          {/* Financial Overview Cards */}
          <div className="grid grid-cols-2 gap-3">
            <div className="p-4 bg-emerald-50 rounded-xl border border-emerald-200">
              <span className="text-xs text-emerald-800 font-semibold block">নির্ধারিত মাসিক চাঁদা</span>
              <span className="text-xl font-bold text-emerald-900 mt-1 block">
                {formatCurrency(member.monthlyFee)}
              </span>
            </div>

            <div className="p-4 bg-blue-50 rounded-xl border border-blue-200">
              <span className="text-xs text-blue-800 font-semibold block">সর্বমোট জমাকৃত চাঁদা</span>
              <span className="text-xl font-bold text-blue-900 mt-1 block">
                {formatCurrency(totalPaid)}
              </span>
            </div>
          </div>

          {/* Payment History Table */}
          <div>
            <div className="flex items-center justify-between mb-3">
              <h4 className="text-sm font-bold text-slate-800 flex items-center gap-1.5">
                <CreditCard className="w-4 h-4 text-emerald-700" />
                <span>চাঁদা জমার লেজার ও ইতিহাস ({toBengaliNumber(memberDeposits.length)} টি)</span>
              </h4>

              <button
                onClick={() => {
                  onClose();
                  onOpenAddDeposit(member.id);
                }}
                className="inline-flex items-center gap-1 px-3 py-1.5 rounded-lg bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-semibold shadow-sm transition active:scale-95"
              >
                <ArrowDownLeft className="w-3.5 h-3.5" />
                <span>+ চাঁদা জমা নিন</span>
              </button>
            </div>

            <div className="border border-slate-200 rounded-xl overflow-hidden">
              <table className="w-full text-left text-xs text-slate-600">
                <thead className="bg-slate-100 text-slate-700 font-bold uppercase border-b border-slate-200">
                  <tr>
                    <th className="py-2.5 px-3">তারিখ</th>
                    <th className="py-2.5 px-3">মাস</th>
                    <th className="py-2.5 px-3">টাকা</th>
                    <th className="py-2.5 px-3">মাধ্যম</th>
                    <th className="py-2.5 px-3">রসিদ নং</th>
                    <th className="py-2.5 px-3 text-right">রসিদ</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {memberDeposits.map((dep) => (
                    <tr key={dep.id} className="hover:bg-slate-50">
                      <td className="py-2 px-3 whitespace-nowrap">{dep.date}</td>
                      <td className="py-2 px-3 font-semibold text-slate-800">
                        {formatMonthBangla(dep.targetMonth)}
                      </td>
                      <td className="py-2 px-3 font-bold text-emerald-600">
                        {formatCurrency(dep.amount)}
                      </td>
                      <td className="py-2 px-3">{dep.paymentMethod}</td>
                      <td className="py-2 px-3 font-mono text-[11px]">{dep.receiptNo || `#${dep.id}`}</td>
                      <td className="py-2 px-3 text-right">
                        <button
                          onClick={() => onViewReceipt(dep)}
                          className="p-1 text-slate-400 hover:text-emerald-700 transition"
                          title="মানি রিসিট দেখুন"
                        >
                          <Printer className="w-3.5 h-3.5" />
                        </button>
                      </td>
                    </tr>
                  ))}

                  {memberDeposits.length === 0 && (
                    <tr>
                      <td colSpan={6} className="py-6 text-center text-slate-400">
                        এখনো কোনো জমার রেকর্ড নেই।
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </div>

        {/* Modal Footer */}
        <div className="bg-slate-50 px-6 py-4 border-t border-slate-200 flex justify-end">
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
