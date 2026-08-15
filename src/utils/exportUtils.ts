import { Member, DepositTransaction, ExpenseTransaction } from '../types';
import { formatMonthBangla } from './formatters';

export function generateGoogleSheetsCsv(
  monthCode: string,
  deposits: DepositTransaction[],
  expenses: ExpenseTransaction[],
  members: Member[]
): string {
  const filteredDeposits = monthCode === 'ALL' ? deposits : deposits.filter(d => d.targetMonth === monthCode);
  const filteredExpenses = monthCode === 'ALL' ? expenses : expenses.filter(e => e.targetMonth === monthCode);
  
  const totalDep = filteredDeposits.reduce((acc, d) => acc + d.amount, 0);
  const totalExp = filteredExpenses.reduce((acc, e) => acc + e.amount, 0);
  const netBalance = totalDep - totalExp;

  const lines: string[] = [];
  lines.push("দক্ষিণ লতিবপুর যুব উন্নয়ন সংগঠন - হিসাব বিবরণী");
  lines.push(`হিসাবের মাস:,${formatMonthBangla(monthCode)}`);
  lines.push(`প্রস্তুত তারিখ:,${new Date().toLocaleString('bn-BD')}`);
  lines.push("");
  lines.push("সারসংক্ষেপ (Summary)");
  lines.push("মোট জমা (টাকা),মোট খরচ (টাকা),উদ্বৃত্ত/ঘাটতি স্থিতি (টাকা)");
  lines.push(`${totalDep},${totalExp},${netBalance}`);
  lines.push("");

  lines.push("--- ১. জমার বিবরণী (Collections / Deposits) ---");
  lines.push("ক্রমিক,তারিখ,সদস্যের নাম,খাত / ধরণ,টাকা (৳),পেমেন্ট মাধ্যম,রসিদ নং,সংগ্রাহক,মন্তব্য");
  filteredDeposits.forEach((dep, idx) => {
    lines.push(`${idx + 1},${dep.date},"${dep.memberName}","${dep.category}",${dep.amount},"${dep.paymentMethod}","${dep.receiptNo}","${dep.collectedBy}","${(dep.note || '').replace(/"/g, '""')}"`);
  });
  lines.push(`,,,মোট জমা:,${totalDep},,,,`);
  lines.push("");

  lines.push("--- ২. খরচের বিবরণী (Expenses) ---");
  lines.push("ক্রমিক,তারিখ,খরচের বিবরণ / খাত,টাকা (৳),খরচকারী,ভাউচার নং,মন্তব্য");
  filteredExpenses.forEach((exp, idx) => {
    lines.push(`${idx + 1},${exp.date},"${exp.title} (${exp.category})",${exp.amount},"${exp.spentBy}","${exp.voucherNo}","${(exp.note || '').replace(/"/g, '""')}"`);
  });
  lines.push(`,,,মোট খরচ:,${totalExp},,,`);
  lines.push("");

  lines.push("--- ৩. সদস্যভিত্তিক চাঁদা আদায় স্ট্যাটাস ---");
  lines.push("আইডি,সদস্যের নাম,পদবী,মোবাইল নম্বর,নির্ধারিত মাসিক চাঁদা,এই মাসে আদায়,বকেয়া / অবস্থা");
  members.forEach(m => {
    const paid = filteredDeposits.filter(d => d.memberId === m.id).reduce((s, d) => s + d.amount, 0);
    const due = paid >= m.monthlyFee ? 0 : m.monthlyFee - paid;
    const status = paid >= m.monthlyFee ? "পরিশোধিত" : `বকেয়া: ৳${due}`;
    lines.push(`${m.id},"${m.name}","${m.designation}","${m.phone}",${m.monthlyFee},${paid},"${status}"`);
  });

  return lines.join('\n');
}

export function downloadCsvFile(content: string, filename: string) {
  const blob = new Blob(["\uFEFF" + content], { type: 'text/csv;charset=utf-8;' });
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.setAttribute('download', filename);
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
}

export function generateJsonBackup(
  members: Member[],
  deposits: DepositTransaction[],
  expenses: ExpenseTransaction[]
): string {
  const data = {
    organization: "দক্ষিণ লতিবপুর যুব উন্নয়ন সংগঠন",
    version: 1,
    exportDate: new Date().toISOString(),
    members,
    deposits,
    expenses
  };
  return JSON.stringify(data, null, 2);
}

export function downloadJsonFile(content: string, filename: string) {
  const blob = new Blob([content], { type: 'application/json' });
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.setAttribute('download', filename);
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
}
