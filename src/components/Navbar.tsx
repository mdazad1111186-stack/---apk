import React from 'react';
import { 
  LayoutDashboard, 
  Users, 
  ArrowDownLeft, 
  ArrowUpRight, 
  FileSpreadsheet, 
  Database, 
  PlusCircle
} from 'lucide-react';
import { TabType } from '../types';

interface NavbarProps {
  currentTab: TabType;
  onSelectTab: (tab: TabType) => void;
  onOpenAddDeposit: () => void;
  onOpenAddExpense: () => void;
  onOpenAddMember: () => void;
}

export const Navbar: React.FC<NavbarProps> = ({
  currentTab,
  onSelectTab,
  onOpenAddDeposit,
  onOpenAddExpense,
  onOpenAddMember,
}) => {
  return (
    <header className="bg-emerald-800 text-white shadow-lg sticky top-0 z-30 no-print">
      {/* Top Banner */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16 sm:h-20">
          
          {/* Logo & Title */}
          <div className="flex items-center gap-3">
            <div className="w-11 h-11 sm:w-12 sm:h-12 rounded-xl bg-white/10 backdrop-blur border border-white/20 flex items-center justify-center text-2xl shadow-inner flex-shrink-0">
              🌿
            </div>
            <div>
              <h1 className="text-lg sm:text-2xl font-bold tracking-tight text-white leading-tight">
                দক্ষিণ লতিবপুর যুব উন্নয়ন সংগঠন
              </h1>
              <p className="text-xs sm:text-sm text-emerald-200 font-medium">
                ডিজিটাল হিসাব খাতা ও সদস্য ব্যবস্থাপনা পোর্টাল
              </p>
            </div>
          </div>

          {/* Quick Action Buttons on Header (Desktop) */}
          <div className="hidden lg:flex items-center gap-2.5">
            <button
              onClick={onOpenAddDeposit}
              className="inline-flex items-center gap-1.5 px-3.5 py-2 rounded-lg bg-emerald-600 hover:bg-emerald-500 text-white text-sm font-medium shadow transition active:scale-95"
            >
              <ArrowDownLeft className="w-4 h-4 text-emerald-200" />
              <span>+ চাঁদা জমা</span>
            </button>

            <button
              onClick={onOpenAddExpense}
              className="inline-flex items-center gap-1.5 px-3.5 py-2 rounded-lg bg-rose-600/90 hover:bg-rose-600 text-white text-sm font-medium shadow transition active:scale-95"
            >
              <ArrowUpRight className="w-4 h-4 text-rose-200" />
              <span>- খরচ ভাউচার</span>
            </button>

            <button
              onClick={onOpenAddMember}
              className="inline-flex items-center gap-1.5 px-3.5 py-2 rounded-lg bg-emerald-950/70 hover:bg-emerald-950 text-emerald-100 text-sm font-medium border border-emerald-700/50 shadow transition active:scale-95"
            >
              <PlusCircle className="w-4 h-4" />
              <span>নতুন সদস্য</span>
            </button>
          </div>
        </div>
      </div>

      {/* Navigation Tabs */}
      <div className="bg-emerald-900/90 border-t border-emerald-700/40">
        <div className="max-w-7xl mx-auto px-2 sm:px-6 lg:px-8">
          <nav className="flex space-x-1 sm:space-x-2 overflow-x-auto py-2 scrollbar-none">
            <button
              onClick={() => onSelectTab('dashboard')}
              className={`flex items-center gap-2 px-3.5 py-2 rounded-lg text-sm font-semibold whitespace-nowrap transition-all ${
                currentTab === 'dashboard'
                  ? 'bg-white text-emerald-900 shadow-sm'
                  : 'text-emerald-100 hover:bg-emerald-800/80 hover:text-white'
              }`}
            >
              <LayoutDashboard className="w-4 h-4" />
              <span>ড্যাশবোর্ড</span>
            </button>

            <button
              onClick={() => onSelectTab('members')}
              className={`flex items-center gap-2 px-3.5 py-2 rounded-lg text-sm font-semibold whitespace-nowrap transition-all ${
                currentTab === 'members'
                  ? 'bg-white text-emerald-900 shadow-sm'
                  : 'text-emerald-100 hover:bg-emerald-800/80 hover:text-white'
              }`}
            >
              <Users className="w-4 h-4" />
              <span>সদস্য তালিকা</span>
            </button>

            <button
              onClick={() => onSelectTab('deposits')}
              className={`flex items-center gap-2 px-3.5 py-2 rounded-lg text-sm font-semibold whitespace-nowrap transition-all ${
                currentTab === 'deposits'
                  ? 'bg-white text-emerald-900 shadow-sm'
                  : 'text-emerald-100 hover:bg-emerald-800/80 hover:text-white'
              }`}
            >
              <ArrowDownLeft className="w-4 h-4 text-emerald-400" />
              <span>জমা খাতা</span>
            </button>

            <button
              onClick={() => onSelectTab('expenses')}
              className={`flex items-center gap-2 px-3.5 py-2 rounded-lg text-sm font-semibold whitespace-nowrap transition-all ${
                currentTab === 'expenses'
                  ? 'bg-white text-emerald-900 shadow-sm'
                  : 'text-emerald-100 hover:bg-emerald-800/80 hover:text-white'
              }`}
            >
              <ArrowUpRight className="w-4 h-4 text-rose-400" />
              <span>খরচ খাতা</span>
            </button>

            <button
              onClick={() => onSelectTab('sheets')}
              className={`flex items-center gap-2 px-3.5 py-2 rounded-lg text-sm font-semibold whitespace-nowrap transition-all ${
                currentTab === 'sheets'
                  ? 'bg-white text-emerald-900 shadow-sm'
                  : 'text-emerald-100 hover:bg-emerald-800/80 hover:text-white'
              }`}
            >
              <FileSpreadsheet className="w-4 h-4 text-amber-300" />
              <span>গুগল সিট এক্সপোর্ট</span>
            </button>

            <button
              onClick={() => onSelectTab('backup')}
              className={`flex items-center gap-2 px-3.5 py-2 rounded-lg text-sm font-semibold whitespace-nowrap transition-all ${
                currentTab === 'backup'
                  ? 'bg-white text-emerald-900 shadow-sm'
                  : 'text-emerald-100 hover:bg-emerald-800/80 hover:text-white'
              }`}
            >
              <Database className="w-4 h-4" />
              <span>ডাটা ব্যাকআপ / রিস্টোর</span>
            </button>
          </nav>
        </div>
      </div>
    </header>
  );
};
