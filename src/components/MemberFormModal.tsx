import React, { useState, useEffect } from 'react';
import { X, UserPlus, Users } from 'lucide-react';
import { Member } from '../types';
import { getTodayDateString } from '../utils/formatters';

interface MemberFormModalProps {
  isOpen: boolean;
  memberToEdit: Member | null;
  onClose: () => void;
  onSave: (member: Member) => void;
}

export const MemberFormModal: React.FC<MemberFormModalProps> = ({
  isOpen,
  memberToEdit,
  onClose,
  onSave,
}) => {
  if (!isOpen) return null;

  const [name, setName] = useState('');
  const [phone, setPhone] = useState('');
  const [designation, setDesignation] = useState('সদস্য');
  const [monthlyFee, setMonthlyFee] = useState<number>(200);
  const [address, setAddress] = useState('দক্ষিণ লতিবপুর');
  const [bloodGroup, setBloodGroup] = useState('A+');
  const [joinDate, setJoinDate] = useState(getTodayDateString());
  const [isActive, setIsActive] = useState(true);
  const [notes, setNotes] = useState('');

  useEffect(() => {
    if (memberToEdit) {
      setName(memberToEdit.name);
      setPhone(memberToEdit.phone);
      setDesignation(memberToEdit.designation);
      setMonthlyFee(memberToEdit.monthlyFee);
      setAddress(memberToEdit.address || '');
      setBloodGroup(memberToEdit.bloodGroup || 'A+');
      setJoinDate(memberToEdit.joinDate || getTodayDateString());
      setIsActive(memberToEdit.isActive);
      setNotes(memberToEdit.notes || '');
    } else {
      setName('');
      setPhone('');
      setDesignation('সদস্য');
      setMonthlyFee(200);
      setAddress('দক্ষিণ লতিবপুর');
      setBloodGroup('A+');
      setJoinDate(getTodayDateString());
      setIsActive(true);
      setNotes('');
    }
  }, [memberToEdit]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim()) {
      alert('সদস্যের নাম লিখুন');
      return;
    }

    const payload: Member = {
      id: memberToEdit ? memberToEdit.id : Date.now(),
      name: name.trim(),
      phone: phone.trim(),
      designation,
      monthlyFee: Number(monthlyFee),
      address: address.trim(),
      bloodGroup,
      joinDate,
      isActive,
      notes: notes.trim(),
      createdAt: memberToEdit ? memberToEdit.createdAt : Date.now(),
    };

    onSave(payload);
  };

  return (
    <div className="fixed inset-0 z-50 bg-slate-900/60 backdrop-blur-sm flex items-center justify-center p-3 sm:p-4 overflow-y-auto">
      <div className="bg-white w-full max-w-lg rounded-2xl shadow-2xl border border-slate-100 overflow-hidden">
        
        {/* Header */}
        <div className="bg-emerald-800 text-white px-5 py-4 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <UserPlus className="w-5 h-5 text-emerald-300" />
            <h3 className="text-base sm:text-lg font-bold">
              {memberToEdit ? 'সদস্যের তথ্য সম্পাদন (Edit Profile)' : 'সংগঠনে নতুন সদস্য নিবন্ধন'}
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
          
          {/* Name */}
          <div>
            <label className="block text-slate-700 font-bold mb-1">
              সদস্যের পূর্ণ নাম *
            </label>
            <input
              type="text"
              required
              value={name}
              onChange={(e) => setName(e.target.value)}
              placeholder="উদাঃ মোঃ রফিকুল ইসলাম"
              className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3 py-2 text-slate-800 focus:ring-2 focus:ring-emerald-500 font-medium"
            />
          </div>

          {/* Designation & Monthly Fee */}
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-slate-700 font-bold mb-1">
                সংগঠনের পদবী *
              </label>
              <select
                value={designation}
                onChange={(e) => setDesignation(e.target.value)}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3 py-2 text-slate-800 focus:ring-2 focus:ring-emerald-500 font-medium"
              >
                <option value="সভাপতি">সভাপতি</option>
                <option value="সহ-সভাপতি">সহ-সভাপতি</option>
                <option value="সাধারণ সম্পাদক">সাধারণ সম্পাদক</option>
                <option value="যুগ্ম সাধারণ সম্পাদক">যুগ্ম সাধারণ সম্পাদক</option>
                <option value="কোষাধ্যক্ষ">কোষাধ্যক্ষ</option>
                <option value="সাংগঠনিক সম্পাদক">সাংগঠনিক সম্পাদক</option>
                <option value="প্রচার সম্পাদক">প্রচার সম্পাদক</option>
                <option value="সমাজকল্যাণ সম্পাদক">সমাজকল্যাণ সম্পাদক</option>
                <option value="শিক্ষা ও সংস্কৃতি সম্পাদক">শিক্ষা ও সংস্কৃতি সম্পাদক</option>
                <option value="দপ্তর সম্পাদক">দপ্তর সম্পাদক</option>
                <option value="সদস্য">সাধারণ সদস্য</option>
                <option value="উপদেষ্টা">উপদেষ্টা</option>
              </select>
            </div>

            <div>
              <label className="block text-slate-700 font-bold mb-1">
                নির্ধারিত মাসিক চাঁদা (৳) *
              </label>
              <input
                type="number"
                min="0"
                required
                value={monthlyFee}
                onChange={(e) => setMonthlyFee(Number(e.target.value))}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3 py-2 text-slate-800 font-bold text-base focus:ring-2 focus:ring-emerald-500"
              />
            </div>
          </div>

          {/* Phone & Blood Group */}
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-slate-700 font-bold mb-1">
                মোবাইল নম্বর
              </label>
              <input
                type="tel"
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
                placeholder="01XXXXXXXXX"
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3 py-2 text-slate-800 focus:ring-2 focus:ring-emerald-500"
              />
            </div>

            <div>
              <label className="block text-slate-700 font-bold mb-1">
                রক্তের গ্রুপ
              </label>
              <select
                value={bloodGroup}
                onChange={(e) => setBloodGroup(e.target.value)}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3 py-2 text-slate-800 focus:ring-2 focus:ring-emerald-500"
              >
                <option value="A+">A (+ve)</option>
                <option value="A-">A (-ve)</option>
                <option value="B+">B (+ve)</option>
                <option value="B-">B (-ve)</option>
                <option value="O+">O (+ve)</option>
                <option value="O-">O (-ve)</option>
                <option value="AB+">AB (+ve)</option>
                <option value="AB-">AB (-ve)</option>
                <option value="">জানা নেই</option>
              </select>
            </div>
          </div>

          {/* Address & Join Date */}
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-slate-700 font-bold mb-1">
                ঠিকানা / পাড়া
              </label>
              <input
                type="text"
                value={address}
                onChange={(e) => setAddress(e.target.value)}
                placeholder="দক্ষিণ লতিবপুর"
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3 py-2 text-slate-800 focus:ring-2 focus:ring-emerald-500"
              />
            </div>

            <div>
              <label className="block text-slate-700 font-bold mb-1">
                যোগদানের তারিখ
              </label>
              <input
                type="date"
                value={joinDate}
                onChange={(e) => setJoinDate(e.target.value)}
                className="w-full bg-slate-50 border border-slate-300 rounded-xl px-3 py-2 text-slate-800 focus:ring-2 focus:ring-emerald-500"
              />
            </div>
          </div>

          {/* Active Status Switch */}
          <div className="flex items-center gap-3 bg-slate-50 p-3 rounded-xl border border-slate-200">
            <input
              type="checkbox"
              id="isActive"
              checked={isActive}
              onChange={(e) => setIsActive(e.target.checked)}
              className="w-4 h-4 text-emerald-600 rounded focus:ring-emerald-500"
            />
            <label htmlFor="isActive" className="text-slate-800 font-semibold cursor-pointer">
              সক্রিয় সদস্য (Active Member)
            </label>
          </div>

          {/* Notes */}
          <div>
            <label className="block text-slate-700 font-bold mb-1">
              মন্তব্য বা বিশেষ দায়িত্ব
            </label>
            <textarea
              rows={2}
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
              placeholder="সদস্য সম্পর্কে অতিরিক্ত কোনো তথ্য..."
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
              {memberToEdit ? 'প্রোফাইল আপডেট করুন' : 'সদস্য সংরক্ষণ করুন'}
            </button>
          </div>
        </form>

      </div>
    </div>
  );
};
