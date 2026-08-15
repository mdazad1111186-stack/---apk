import React, { useState } from 'react';
import { 
  Users, 
  Search, 
  Plus, 
  Phone, 
  Droplet, 
  MapPin, 
  Calendar, 
  Edit, 
  Trash2, 
  ArrowDownLeft, 
  UserCheck, 
  UserX,
  Eye
} from 'lucide-react';
import { Member, DepositTransaction } from '../types';
import { formatCurrency, toBengaliNumber } from '../utils/formatters';

interface MembersListProps {
  members: Member[];
  deposits: DepositTransaction[];
  onOpenAddMember: () => void;
  onOpenEditMember: (m: Member) => void;
  onDeleteMember: (m: Member) => void;
  onOpenMemberProfile: (m: Member) => void;
  onOpenAddDeposit: (memberId: number) => void;
}

export const MembersList: React.FC<MembersListProps> = ({
  members,
  deposits,
  onOpenAddMember,
  onOpenEditMember,
  onDeleteMember,
  onOpenMemberProfile,
  onOpenAddDeposit,
}) => {
  const [search, setSearch] = useState('');
  const [roleFilter, setRoleFilter] = useState('ALL');

  const filteredMembers = members.filter((m) => {
    const matchesSearch = 
      m.name.toLowerCase().includes(search.toLowerCase()) ||
      m.phone.includes(search) ||
      m.designation.toLowerCase().includes(search.toLowerCase()) ||
      (m.address && m.address.toLowerCase().includes(search.toLowerCase()));

    const matchesRole = 
      roleFilter === 'ALL' ||
      (roleFilter === 'EXEC' && ['সভাপতি', 'সাধারণ সম্পাদক', 'কোষাধ্যক্ষ', 'সাংগঠনিক সম্পাদক', 'প্রচার সম্পাদক', 'সমাজকল্যাণ সম্পাদক'].includes(m.designation)) ||
      (roleFilter === 'MEMBER' && m.designation === 'সদস্য') ||
      (roleFilter === 'ADVISOR' && m.designation === 'উপদেষ্টা');

    return matchesSearch && matchesRole;
  });

  return (
    <div className="space-y-6 pb-12">
      {/* Top Header & Search / Filter */}
      <div className="bg-white rounded-2xl p-5 shadow-sm border border-slate-200 flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h2 className="text-xl font-bold text-slate-800 flex items-center gap-2">
            <Users className="w-6 h-6 text-emerald-700" />
            <span>সংগঠনের সদস্য তালিকা ও প্রোফাইল</span>
          </h2>
          <p className="text-xs sm:text-sm text-slate-500 mt-0.5">
            মোট সদস্য: {toBengaliNumber(members.length)} জন (সক্রিয়: {toBengaliNumber(members.filter(m => m.isActive).length)} জন)
          </p>
        </div>

        <button
          onClick={onOpenAddMember}
          className="inline-flex items-center justify-center gap-2 px-4 py-2.5 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white font-semibold text-sm shadow-sm transition active:scale-95 flex-shrink-0"
        >
          <Plus className="w-4 h-4" />
          <span>নতুন সদস্য যুক্ত করুন</span>
        </button>
      </div>

      {/* Filter and Search Bar */}
      <div className="flex flex-col sm:flex-row gap-3">
        <div className="relative flex-1">
          <Search className="w-4 h-4 text-slate-400 absolute left-3.5 top-1/2 -translate-y-1/2" />
          <input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="সদস্যের নাম, মোবাইল নম্বর, পদবী বা ঠিকানা দিয়ে খুঁজুন..."
            className="w-full pl-10 pr-4 py-2.5 bg-white rounded-xl border border-slate-200 text-sm focus:outline-none focus:ring-2 focus:ring-emerald-500 shadow-sm"
          />
        </div>

        <div className="flex gap-2 overflow-x-auto pb-1">
          <button
            onClick={() => setRoleFilter('ALL')}
            className={`px-3.5 py-2 rounded-xl text-xs font-bold whitespace-nowrap transition ${
              roleFilter === 'ALL'
                ? 'bg-emerald-800 text-white'
                : 'bg-white text-slate-600 border border-slate-200 hover:bg-slate-50'
            }`}
          >
            সকল সদস্য ({toBengaliNumber(members.length)})
          </button>
          <button
            onClick={() => setRoleFilter('EXEC')}
            className={`px-3.5 py-2 rounded-xl text-xs font-bold whitespace-nowrap transition ${
              roleFilter === 'EXEC'
                ? 'bg-emerald-800 text-white'
                : 'bg-white text-slate-600 border border-slate-200 hover:bg-slate-50'
            }`}
          >
            কার্যনির্বাহী
          </button>
          <button
            onClick={() => setRoleFilter('MEMBER')}
            className={`px-3.5 py-2 rounded-xl text-xs font-bold whitespace-nowrap transition ${
              roleFilter === 'MEMBER'
                ? 'bg-emerald-800 text-white'
                : 'bg-white text-slate-600 border border-slate-200 hover:bg-slate-50'
            }`}
          >
            সাধারণ সদস্য
          </button>
          <button
            onClick={() => setRoleFilter('ADVISOR')}
            className={`px-3.5 py-2 rounded-xl text-xs font-bold whitespace-nowrap transition ${
              roleFilter === 'ADVISOR'
                ? 'bg-emerald-800 text-white'
                : 'bg-white text-slate-600 border border-slate-200 hover:bg-slate-50'
            }`}
          >
            উপদেষ্টা
          </button>
        </div>
      </div>

      {/* Member Cards Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        {filteredMembers.map((member) => {
          const memberDeposits = deposits.filter((d) => d.memberId === member.id);
          const totalPaid = memberDeposits.reduce((acc, d) => acc + d.amount, 0);

          return (
            <div
              key={member.id}
              className="bg-white rounded-2xl p-5 shadow-sm border border-slate-200 hover:shadow-md transition flex flex-col justify-between"
            >
              <div>
                {/* Header: Name & Status */}
                <div className="flex items-start justify-between gap-2">
                  <div className="flex items-center gap-3">
                    <div className="w-10 h-10 rounded-full bg-emerald-100 border border-emerald-200 flex items-center justify-center font-bold text-emerald-800 text-base">
                      {member.name.charAt(0)}
                    </div>
                    <div>
                      <h3 
                        onClick={() => onOpenMemberProfile(member)}
                        className="text-base font-bold text-slate-800 hover:text-emerald-700 cursor-pointer"
                      >
                        {member.name}
                      </h3>
                      <div className="inline-block mt-0.5 px-2 py-0.5 rounded-md bg-emerald-50 text-emerald-700 font-semibold text-xs border border-emerald-200">
                        {member.designation}
                      </div>
                    </div>
                  </div>

                  <div>
                    {member.isActive ? (
                      <span className="inline-flex items-center gap-1 text-[11px] font-semibold text-emerald-700 bg-emerald-100/70 px-2 py-0.5 rounded-full">
                        <UserCheck className="w-3 h-3" /> সক্রিয়
                      </span>
                    ) : (
                      <span className="inline-flex items-center gap-1 text-[11px] font-semibold text-rose-700 bg-rose-100/70 px-2 py-0.5 rounded-full">
                        <UserX className="w-3 h-3" /> নিষ্ক্রিয়
                      </span>
                    )}
                  </div>
                </div>

                {/* Details List */}
                <div className="mt-4 space-y-2 text-xs text-slate-600">
                  <div className="flex items-center gap-2">
                    <Phone className="w-3.5 h-3.5 text-slate-400 flex-shrink-0" />
                    <a href={`tel:${member.phone}`} className="hover:text-emerald-700 font-medium">
                      {member.phone || 'ফোন নেই'}
                    </a>
                  </div>

                  {member.bloodGroup && (
                    <div className="flex items-center gap-2">
                      <Droplet className="w-3.5 h-3.5 text-rose-500 flex-shrink-0" />
                      <span>রক্তের গ্রুপ: <strong className="text-rose-700">{member.bloodGroup}</strong></span>
                    </div>
                  )}

                  {member.address && (
                    <div className="flex items-center gap-2">
                      <MapPin className="w-3.5 h-3.5 text-slate-400 flex-shrink-0" />
                      <span className="truncate">{member.address}</span>
                    </div>
                  )}

                  {member.joinDate && (
                    <div className="flex items-center gap-2">
                      <Calendar className="w-3.5 h-3.5 text-slate-400 flex-shrink-0" />
                      <span>যোগদান: {member.joinDate}</span>
                    </div>
                  )}
                </div>

                {/* Financial Summary */}
                <div className="mt-4 p-3 bg-slate-50 rounded-xl border border-slate-200 flex items-center justify-between text-xs">
                  <div>
                    <span className="text-slate-500 block">মাসিক চাঁদা:</span>
                    <strong className="text-slate-800 text-sm font-bold">
                      {formatCurrency(member.monthlyFee)}
                    </strong>
                  </div>
                  <div className="text-right">
                    <span className="text-slate-500 block">মোট জমা:</span>
                    <strong className="text-emerald-600 text-sm font-bold">
                      {formatCurrency(totalPaid)}
                    </strong>
                  </div>
                </div>
              </div>

              {/* Action Buttons */}
              <div className="mt-4 pt-3 border-t border-slate-100 flex items-center justify-between gap-2">
                <button
                  onClick={() => onOpenMemberProfile(member)}
                  className="inline-flex items-center gap-1 text-xs font-semibold text-slate-600 hover:text-emerald-700 p-1.5 rounded-lg hover:bg-slate-100 transition"
                  title="প্রোফাইল ও বিস্তারিত লেজার দেখুন"
                >
                  <Eye className="w-3.5 h-3.5" />
                  <span>লেজার</span>
                </button>

                <div className="flex items-center gap-1.5">
                  <button
                    onClick={() => onOpenAddDeposit(member.id)}
                    className="inline-flex items-center gap-1 px-2.5 py-1.5 rounded-lg bg-emerald-600 hover:bg-emerald-700 text-white text-xs font-semibold shadow-sm transition active:scale-95"
                    title="এই সদস্যের জন্য চাঁদা জমা করুন"
                  >
                    <ArrowDownLeft className="w-3.5 h-3.5" />
                    <span>চাঁদা নিন</span>
                  </button>

                  <button
                    onClick={() => onOpenEditMember(member)}
                    className="p-1.5 text-slate-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition"
                    title="সদস্য তথ্য এডিট করুন"
                  >
                    <Edit className="w-4 h-4" />
                  </button>

                  <button
                    onClick={() => onDeleteMember(member)}
                    className="p-1.5 text-slate-400 hover:text-rose-600 hover:bg-rose-50 rounded-lg transition"
                    title="মুছে ফেলুন"
                  >
                    <Trash2 className="w-4 h-4" />
                  </button>
                </div>
              </div>
            </div>
          );
        })}

        {filteredMembers.length === 0 && (
          <div className="col-span-full py-12 text-center bg-white rounded-2xl border border-slate-200">
            <Users className="w-12 h-12 text-slate-300 mx-auto mb-2" />
            <p className="text-base font-bold text-slate-600">কোনো সদস্য খুঁজে পাওয়া যায়নি</p>
            <p className="text-xs text-slate-400 mt-1">দয়া করে অনুসন্ধানের বানান চেক করুন অথবা নতুন সদস্য যুক্ত করুন।</p>
          </div>
        )}
      </div>
    </div>
  );
};
