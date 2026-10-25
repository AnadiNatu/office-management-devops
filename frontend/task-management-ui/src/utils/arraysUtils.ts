/** Coerces common list response shapes into a plain array. */
export function ensureArray<T>(value: unknown): T[] {
  if (Array.isArray(value)) return value as T[];
  if (value && typeof value === 'object') {
    const v = value as Record<string, unknown>;
    if (Array.isArray(v.content)) return v.content as T[]; // Spring Page
    if (Array.isArray(v.data)) return v.data as T[]; // wrapper / Axios response
    if (Array.isArray(v.items)) return v.items as T[];
  }
  return [];
}