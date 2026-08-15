import { Member, DepositTransaction, ExpenseTransaction } from '../types';
import { INITIAL_MEMBERS, INITIAL_DEPOSITS, INITIAL_EXPENSES } from '../data/initialData';

const STORAGE_KEYS = {
  MEMBERS: 'latifpur_org_members_v1',
  DEPOSITS: 'latifpur_org_deposits_v1',
  EXPENSES: 'latifpur_org_expenses_v1',
};

export const Storage = {
  getMembers(): Member[] {
    try {
      const raw = localStorage.getItem(STORAGE_KEYS.MEMBERS);
      if (!raw) {
        localStorage.setItem(STORAGE_KEYS.MEMBERS, JSON.stringify(INITIAL_MEMBERS));
        return INITIAL_MEMBERS;
      }
      return JSON.parse(raw);
    } catch {
      return INITIAL_MEMBERS;
    }
  },

  saveMembers(members: Member[]): void {
    localStorage.setItem(STORAGE_KEYS.MEMBERS, JSON.stringify(members));
  },

  getDeposits(): DepositTransaction[] {
    try {
      const raw = localStorage.getItem(STORAGE_KEYS.DEPOSITS);
      if (!raw) {
        localStorage.setItem(STORAGE_KEYS.DEPOSITS, JSON.stringify(INITIAL_DEPOSITS));
        return INITIAL_DEPOSITS;
      }
      return JSON.parse(raw);
    } catch {
      return INITIAL_DEPOSITS;
    }
  },

  saveDeposits(deposits: DepositTransaction[]): void {
    localStorage.setItem(STORAGE_KEYS.DEPOSITS, JSON.stringify(deposits));
  },

  getExpenses(): ExpenseTransaction[] {
    try {
      const raw = localStorage.getItem(STORAGE_KEYS.EXPENSES);
      if (!raw) {
        localStorage.setItem(STORAGE_KEYS.EXPENSES, JSON.stringify(INITIAL_EXPENSES));
        return INITIAL_EXPENSES;
      }
      return JSON.parse(raw);
    } catch {
      return INITIAL_EXPENSES;
    }
  },

  saveExpenses(expenses: ExpenseTransaction[]): void {
    localStorage.setItem(STORAGE_KEYS.EXPENSES, JSON.stringify(expenses));
  },

  resetToInitial(): { members: Member[]; deposits: DepositTransaction[]; expenses: ExpenseTransaction[] } {
    localStorage.setItem(STORAGE_KEYS.MEMBERS, JSON.stringify(INITIAL_MEMBERS));
    localStorage.setItem(STORAGE_KEYS.DEPOSITS, JSON.stringify(INITIAL_DEPOSITS));
    localStorage.setItem(STORAGE_KEYS.EXPENSES, JSON.stringify(INITIAL_EXPENSES));
    return {
      members: INITIAL_MEMBERS,
      deposits: INITIAL_DEPOSITS,
      expenses: INITIAL_EXPENSES,
    };
  }
};
