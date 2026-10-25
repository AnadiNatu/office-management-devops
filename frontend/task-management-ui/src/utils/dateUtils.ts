const parse = (value?: string | null): Date | null => {
  if (!value) return null;
  const d = new Date(value);
  return Number.isNaN(d.getTime()) ? null : d;
};

export const formatDate = (value?: string | null): string => {
  const d = parse(value);
  return d
    ? d.toLocaleDateString(undefined, { day: '2-digit', month: 'short', year: 'numeric' })
    : '—';
};

export const formatTime = (value?: string | null): string => {
  const d = parse(value);
  return d ? d.toLocaleTimeString(undefined, { hour: '2-digit', minute: '2-digit' }) : '—';
};

export const formatDateTime = (value?: string | null): string => {
  const d = parse(value);
  return d ? `${formatDate(value)}, ${formatTime(value)}` : '—';
};

const rtf = new Intl.RelativeTimeFormat(undefined, { numeric: 'auto' });
const UNITS: Array<[Intl.RelativeTimeFormatUnit, number]> = [
  ['year', 31536000],
  ['month', 2592000],
  ['day', 86400],
  ['hour', 3600],
  ['minute', 60],
];

export const formatRelative = (value?: string | null): string => {
  const d = parse(value);
  if (!d) return '—';
  const diff = Math.round((d.getTime() - Date.now()) / 1000);
  const abs = Math.abs(diff);
  if (abs < 45) return 'just now';
  for (const [unit, secs] of UNITS) {
    if (abs >= secs) return rtf.format(Math.round(diff / secs), unit);
  }
  return rtf.format(Math.round(diff / 60), 'minute');
};

// export const isOverdue = (due?: string | null, status?: string | null): boolean => {
//   const d = parse(due);
//   if (!d) return false;
//   if (status === 'COMPLETED' || status === 'CANCELLED') return false;
//   return d.getTime() < Date.now();
// };

// const pad = (n: number) => String(n).padStart(2, '0');

// /** For <input type="date"> */
// export const toDateInputValue = (value?: string | null): string => {
//   const d = parse(value);
//   return d ? `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}` : '';
// };

// /** For <input type="datetime-local"> */
// export const toDateTimeLocalValue = (value?: string | null): string => {
//   const d = parse(value);
//   return d
//     ? `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`
//     : '';
// };

/** "2026-09-30T14:30" -> "2026-09-30T14:30:00" (Spring LocalDateTime friendly) */
export const fromDateTimeLocalValue = (value: string): string | undefined => {
  if (!value) return undefined;
  return value.length === 16 ? `${value}:00` : value;
};

export function toLocalDateTime(value?: string | null): string | undefined {
  if (!value) {
    return undefined;
  }

  // Already LocalDateTime
  if (value.includes("T")) {
    return value;
  }

  // HTML <input type="date"> gives YYYY-MM-DD
  return `${value}T23:59:59`;
}

export const toDateTimeLocalValue = (
  value?: string | null
): string => {
  if (!value) return '';

  // Backend LocalDateTime:
  // 2026-10-03T18:30:00
  return value.length >= 16
    ? value.slice(0, 16)
    : value;
};

export const toDateInputValue = (
  value?: string | null
): string => {
  if (!value) return '';

  return value.slice(0, 10);
};

export const isOverdue = (
  dueDate?: string | null,
  status?: string
): boolean => {
  if (!dueDate) return false;

  if (
    status === 'COMPLETED' ||
    status === 'CANCELLED'
  ) {
    return false;
  }

  return new Date(dueDate).getTime() < Date.now();
};