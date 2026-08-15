import React, { useState, useEffect } from 'react';
import { 
  TabType, 
  Member, 
  DepositTransaction, 
  ExpenseTransaction 
} from './types';
import { Storage } from './utils/storage';
import { getCurrentMonthCode } from './utils/formatters';

import { Navbar } from './components/Navbar';
import { Dashboard } from './components/Dashboard';
import { MembersList } from './components/MembersList';
import { DepositsList } from './components/DepositsList';
import { ExpensesList } from './components/ExpensesList';
import { MemberProfileModal } from './components/MemberProfileModal';
import { MoneyReceiptModal } from './components/MoneyReceiptModal';
import { DepositFormModal } from './components/DepositFormModal';
import { ExpenseFormModal } from './components/ExpenseFormModal';
import { MemberFormModal } from './components/MemberFormModal';
import { GoogleSheetsModal } from './components/GoogleSheetsModal';
import { BackupRestoreModal } from './components/BackupRestoreModal';
import { DeleteConfirmModal } from './components/DeleteConfirmModal';

export function App() {
  // State
  const [currentTab, setCurrentTab] = useState<TabType>('dashboard');
  const [selectedMonth, setSelectedMonth] = useState<string>(getCurrentMonthCode());

  const [members, setMembers] = useState<Member[]>([]);
  const [deposits, setDeposits] = useState<DepositTransaction[]>([]);
  const [expenses, setExpenses] = useState<ExpenseTransaction[]>([]);

  // Modals state
  const [showMemberModal, setShowMemberModal] = useState(false);
  const [memberToEdit, setMemberToEdit] = useState<Member | null>(null);

  const [showDepositModal, setShowDepositModal] = useState(false);
  const [depositToEdit, setDepositToEdit] = useState<DepositTransaction | null>(null);
  const [preselectedMemberId, setPreselectedMemberId] = useState<number | undefined>(undefined);

  const [showExpenseModal, setShowExpenseModal] = useState(false);
  const [expenseToEdit, setExpenseToEdit] = useState<ExpenseTransaction | null>(null);

  const [selectedMemberProfile, setSelectedMemberProfile] = useState<Member | null>(null);
  const [receiptDeposit, setReceiptDeposit] = useState<DepositTransaction | null>(null);

  const [showSheetsModal, setShowSheetsModal] = useState(false);
  const [showBackupModal, setShowBackupModal] = useState(false);

  // Delete modal state
  const [deleteData, setDeleteData] = useState<{
    isOpen: boolean;
    title: string;
    description: string;
    onConfirm: () => void;
  }>({
    isOpen: false,
    title: '',
    description: '',
    onConfirm: () => {},
  });

  // Toast notification
  const [toastMessage, setToastMessage] = useState<string | null>(null);

  const showToast = (msg: string) => {
    setToastMessage(msg);
    setTimeout(() => {
      setToastMessage(null);
    }, 3000);
  };

  // Initial load
  useEffect(() => {
    setMembers(Storage.getMembers());
    setDeposits(Storage.getDeposits());
    setExpenses(Storage.getExpenses());
  }, []);

  // Save changes
  const handleSaveMember = (member: Member) => {
    let updated: Member[];
    const exists = members.some((m) => m.id === member.id);
    if (exists) {
      updated = members.map((m) => (m.id === member.id ? member : m));
      showToast('সদস্যের তথ্য সফলভাবে আপডেট করা হয়েছে');
    } else {
      updated = [member, ...members];
      showToast('নতুন সদস্য সফলভাবে নিবন্ধিত হয়েছে');
    }
    setMembers(updated);
    Storage.saveMembers(updated);
    if (selectedMemberProfile && selectedMemberProfile.id === member.id) {
      setSelectedMemberProfile(member);
    }
    setShowMemberModal(false);
    setMemberToEdit(null);
  };

  const handleDeleteMember = (member: Member) => {
    setDeleteData({
      isOpen: true,
      title: 'সদস্য মুছে ফেলার নিশ্চয়তা',
      description: `আপনি কি নিশ্চিত যে "${member.name}"-কে সংগঠন তালিকা থেকে মুছে ফেলতে চান?`,
      onConfirm: () => {
        const updated = members.filter((m) => m.id !== member.id);
        setMembers(updated);
        Storage.saveMembers(updated);
        if (selectedMemberProfile?.id === member.id) {
          setSelectedMemberProfile(null);
        }
        showToast('সদস্যকে তালিকা থেকে মুছে ফেলা হয়েছে');
      },
    });
  };

  const handleSaveDeposit = (deposit: DepositTransaction) => {
    let updated: DepositTransaction[];
    const exists = deposits.some((d) => d.id === deposit.id);
    if (exists) {
      updated = deposits.map((d) => (d.id === deposit.id ? deposit : d));
      showToast('জমার তথ্য আপডেট করা হয়েছে');
    } else {
      updated = [deposit, ...deposits];
      showToast('জমা সফলভাবে সংরক্ষণ করা হয়েছে');
    }
    setDeposits(updated);
    Storage.saveDeposits(updated);
    setShowDepositModal(false);
    setDepositToEdit(null);
    setPreselectedMemberId(undefined);
  };

  const handleDeleteDeposit = (dep: DepositTransaction) => {
    setDeleteData({
      isOpen: true,
      title: 'জমার রেকর্ড মুছে ফেলার নিশ্চয়তা',
      description: `আপনি কি নিশ্চিত যে "${dep.memberName}"-এর ${dep.amount} টাকার জমা রেকর্ড মুছে ফেলতে চান?`,
      onConfirm: () => {
        const updated = deposits.filter((d) => d.id !== dep.id);
        setDeposits(updated);
        Storage.saveDeposits(updated);
        showToast('জমার রেকর্ড মুছে ফেলা হয়েছে');
      },
    });
  };

  const handleSaveExpense = (expense: ExpenseTransaction) => {
    let updated: ExpenseTransaction[];
    const exists = expenses.some((e) => e.id === expense.id);
    if (exists) {
      updated = expenses.map((e) => (e.id === expense.id ? expense : e));
      showToast('খরচের তথ্য আপডেট করা হয়েছে');
    } else {
      updated = [expense, ...expenses];
      showToast('খরচের হিসাব সফলভাবে সংরক্ষিত হয়েছে');
    }
    setExpenses(updated);
    Storage.saveExpenses(updated);
    setShowExpenseModal(false);
    setExpenseToEdit(null);
  };

  const handleDeleteExpense = (exp: ExpenseTransaction) => {
    setDeleteData({
      isOpen: true,
      title: 'খরচের ভাউচার মুছে ফেলার নিশ্চয়তা',
      description: `আপনি কি নিশ্চিত যে "${exp.title}"-এর ${exp.amount} টাকার ভাউচার মুছে ফেলতে চান?`,
      onConfirm: () => {
        const updated = expenses.filter((e) => e.id !== exp.id);
        setExpenses(updated);
        Storage.saveExpenses(updated);
        showToast('খরচের ভাউচার মুছে ফেলা হয়েছে');
      },
    });
  };

  const handleRestoreData = (data: { members: Member[]; deposits: DepositTransaction[]; expenses: ExpenseTransaction[] }) => {
    setMembers(data.members);
    setDeposits(data.deposits);
    setExpenses(data.expenses);
    Storage.saveMembers(data.members);
    Storage.saveDeposits(data.deposits);
    Storage.saveExpenses(data.expenses);
    showToast('সম্পূর্ণ ডাটাবেস সফলভাবে রিস্টোর করা হয়েছে');
  };

  const handleResetToSample = () => {
    const fresh = Storage.resetToInitial();
    setMembers(fresh.members);
    setDeposits(fresh.deposits);
    setExpenses(fresh.expenses);
    showToast('প্রারম্ভিক নমুনা ডাটা সফলভাবে সেট করা হয়েছে');
  };

  return (
    <div className="min-h-screen bg-slate-100/90 text-slate-800 flex flex-col font-sans">
      
      {/* Toast Notification */}
      {toastMessage && (
        <div className="fixed bottom-5 right-5 z-50 bg-emerald-900 text-white px-5 py-3 rounded-2xl shadow-xl border border-emerald-700/50 flex items-center gap-2 text-sm font-semibold animate-in slide-in-from-bottom-5">
          <span>✓</span>
          <span>{toastMessage}</span>
        </div>
      )}

      {/* Main Top Navigation */}
      <Navbar
        currentTab={currentTab}
        onSelectTab={(tab) => {
          if (tab === 'sheets') {
            setShowSheetsModal(true);
          } else if (tab === 'backup') {
            setShowBackupModal(true);
          } else {
            setCurrentTab(tab);
          }
        }}
        onOpenAddDeposit={() => {
          setDepositToEdit(null);
          setPreselectedMemberId(undefined);
          setShowDepositModal(true);
        }}
        onOpenAddExpense={() => {
          setExpenseToEdit(null);
          setShowExpenseModal(true);
        }}
        onOpenAddMember={() => {
          setMemberToEdit(null);
          setShowMemberModal(true);
        }}
      />

      {/* Main View Area */}
      <main className="max-w-7xl w-full mx-auto px-4 sm:px-6 lg:px-8 py-6 flex-1">
        {currentTab === 'dashboard' && (
          <Dashboard
            selectedMonth={selectedMonth}
            onSelectMonth={setSelectedMonth}
            members={members}
            deposits={deposits}
            expenses={expenses}
            onOpenAddDeposit={(memberId) => {
              setDepositToEdit(null);
              setPreselectedMemberId(memberId);
              setShowDepositModal(true);
            }}
            onOpenAddExpense={() => {
              setExpenseToEdit(null);
              setShowExpenseModal(true);
            }}
            onOpenAddMember={() => {
              setMemberToEdit(null);
              setShowMemberModal(true);
            }}
            onOpenMemberProfile={(m) => setSelectedMemberProfile(m)}
            onOpenSheets={() => setShowSheetsModal(true)}
            onSelectTab={setCurrentTab}
          />
        )}

        {currentTab === 'members' && (
          <MembersList
            members={members}
            deposits={deposits}
            onOpenAddMember={() => {
              setMemberToEdit(null);
              setShowMemberModal(true);
            }}
            onOpenEditMember={(m) => {
              setMemberToEdit(m);
              setShowMemberModal(true);
            }}
            onDeleteMember={handleDeleteMember}
            onOpenMemberProfile={(m) => setSelectedMemberProfile(m)}
            onOpenAddDeposit={(memberId) => {
              setDepositToEdit(null);
              setPreselectedMemberId(memberId);
              setShowDepositModal(true);
            }}
          />
        )}

        {currentTab === 'deposits' && (
          <DepositsList
            deposits={deposits}
            members={members}
            selectedMonth={selectedMonth}
            onSelectMonth={setSelectedMonth}
            onOpenAddDeposit={() => {
              setDepositToEdit(null);
              setPreselectedMemberId(undefined);
              setShowDepositModal(true);
            }}
            onOpenEditDeposit={(dep) => {
              setDepositToEdit(dep);
              setShowDepositModal(true);
            }}
            onDeleteDeposit={handleDeleteDeposit}
            onViewReceipt={(dep) => setReceiptDeposit(dep)}
          />
        )}

        {currentTab === 'expenses' && (
          <ExpensesList
            expenses={expenses}
            selectedMonth={selectedMonth}
            onSelectMonth={setSelectedMonth}
            onOpenAddExpense={() => {
              setExpenseToEdit(null);
              setShowExpenseModal(true);
            }}
            onOpenEditExpense={(exp) => {
              setExpenseToEdit(exp);
              setShowExpenseModal(true);
            }}
            onDeleteExpense={handleDeleteExpense}
          />
        )}
      </main>

      {/* Footer */}
      <footer className="bg-white border-t border-slate-200 py-6 text-center text-xs text-slate-500 no-print">
        <div className="max-w-7xl mx-auto px-4">
          <p className="font-semibold text-slate-700">
            দক্ষিণ লতিবপুর যুব উন্নয়ন সংগঠন © {new Date().getFullYear()}
          </p>
          <p className="mt-1 text-[11px] text-slate-400">
            সামাজিক সেবামূলক আর্থিক ব্যবস্থাপনা সিস্টেম • স্বচ্ছতা ও ভ্রাতৃত্বের প্রতীক
          </p>
        </div>
      </footer>

      {/* Modals */}
      <MemberProfileModal
        member={selectedMemberProfile}
        deposits={deposits}
        onClose={() => setSelectedMemberProfile(null)}
        onOpenAddDeposit={(memberId) => {
          setDepositToEdit(null);
          setPreselectedMemberId(memberId);
          setShowDepositModal(true);
        }}
        onViewReceipt={(dep) => setReceiptDeposit(dep)}
      />

      <MoneyReceiptModal
        deposit={receiptDeposit}
        onClose={() => setReceiptDeposit(null)}
      />

      <DepositFormModal
        isOpen={showDepositModal}
        depositToEdit={depositToEdit}
        preselectedMemberId={preselectedMemberId}
        members={members}
        onClose={() => {
          setShowDepositModal(false);
          setDepositToEdit(null);
          setPreselectedMemberId(undefined);
        }}
        onSave={handleSaveDeposit}
      />

      <ExpenseFormModal
        isOpen={showExpenseModal}
        expenseToEdit={expenseToEdit}
        onClose={() => {
          setShowExpenseModal(false);
          setExpenseToEdit(null);
        }}
        onSave={handleSaveExpense}
      />

      <MemberFormModal
        isOpen={showMemberModal}
        memberToEdit={memberToEdit}
        onClose={() => {
          setShowMemberModal(false);
          setMemberToEdit(null);
        }}
        onSave={handleSaveMember}
      />

      <GoogleSheetsModal
        isOpen={showSheetsModal}
        selectedMonth={selectedMonth}
        members={members}
        deposits={deposits}
        expenses={expenses}
        onClose={() => setShowSheetsModal(false)}
      />

      <BackupRestoreModal
        isOpen={showBackupModal}
        members={members}
        deposits={deposits}
        expenses={expenses}
        onClose={() => setShowBackupModal(false)}
        onRestore={handleRestoreData}
        onResetToSample={handleResetToSample}
      />

      <DeleteConfirmModal
        isOpen={deleteData.isOpen}
        title={deleteData.title}
        description={deleteData.description}
        onConfirm={deleteData.onConfirm}
        onClose={() => setDeleteData(prev => ({ ...prev, isOpen: false }))}
      />

    </div>
  );
}

export default App;
