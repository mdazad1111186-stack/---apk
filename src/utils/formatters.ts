// Bengali numeral formatter and dates

export function toBengaliNumber(n: number | string): string {
  const bnDigits = ['০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯'];
  return n.toString().replace(/\d/g, (d) => bnDigits[parseInt(d, 10)]);
}

export function formatCurrency(amount: number): string {
  const formatted = Math.round(amount).toLocaleString('en-US');
  return `৳ ${toBengaliNumber(formatted)}`;
}

export const MONTH_NAMES_BANGLA: Record<string, string> = {
  '01': 'জানুয়ারি',
  '02': 'ফেব্রুয়ারি',
  '03': 'মার্চ',
  '04': 'এপ্রিল',
  '05': 'মে',
  '06': 'জুন',
  '07': 'জুলাই',
  '08': 'আগস্ট',
  '09': 'সেপ্টেম্বর',
  '10': 'অক্টোবর',
  '11': 'নভেম্বর',
  '12': 'ডিসেম্বর'
};

export function formatMonthBangla(monthCode: string): string {
  if (!monthCode || monthCode === 'ALL') return 'সকল মাস';
  const parts = monthCode.split('-');
  if (parts.length === 2) {
    const year = toBengaliNumber(parts[0]);
    const month = MONTH_NAMES_BANGLA[parts[1]] || parts[1];
    return `${month} ${year}`;
  }
  return monthCode;
}

export function getCurrentMonthCode(): string {
  const now = new Date();
  const year = now.getFullYear();
  const month = String(now.getMonth() + 1).padStart(2, '0');
  return `${year}-${month}`;
}

export function getTodayDateString(): string {
  const now = new Date();
  const year = now.getFullYear();
  const month = String(now.getMonth() + 1).padStart(2, '0');
  const day = String(now.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}

export function getAvailableMonthList(): { code: string; label: string }[] {
  const list: { code: string; label: string }[] = [];
  const currentYear = new Date().getFullYear();
  
  for (let year = currentYear; year >= currentYear - 2; year--) {
    for (let month = 12; month >= 1; month--) {
      const monthStr = String(month).padStart(2, '0');
      const code = `${year}-${monthStr}`;
      list.push({
        code,
        label: `${MONTH_NAMES_BANGLA[monthStr]} ${toBengaliNumber(year)}`
      });
    }
  }
  return list;
}
