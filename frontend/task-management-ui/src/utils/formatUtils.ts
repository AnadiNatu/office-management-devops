export const cn = (...parts: Array<string | false | null | undefined>): string =>
  parts.filter(Boolean).join(' ');

export const fullName = (
  u?: { fname?: string | null; lname?: string | null } | null,
  fallback = 'Unknown user'
): string => [u?.fname, u?.lname].filter(Boolean).join(' ') || fallback;

export const initialsOf = (name?: string | null): string => {
  const parts = (name ?? '').trim().split(/\s+/).filter(Boolean);
  if (!parts.length) return '?';
  const first = parts[0][0] ?? '';
  const last = parts.length > 1 ? parts[parts.length - 1][0] ?? '' : '';
  return (first + last).toUpperCase();
};

export const truncate = (text: string, max = 80): string =>
  text.length > max ? `${text.slice(0, max - 1)}…` : text;

/** IN_PROGRESS -> "In Progress" */
export const humanizeEnum = (value?: string | null): string =>
  value
    ? value.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, (c) => c.toUpperCase())
    : '—';

export const formatFileSize = (bytes: number): string => {
  if (bytes < 1024) return `${bytes} B`;
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
  return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
};

export const pluralize = (n: number, singular: string, plural = `${singular}s`): string =>
  `${n} ${n === 1 ? singular : plural}`;

export const formatCount = (n: number): string => (n > 99 ? '99+' : String(n));