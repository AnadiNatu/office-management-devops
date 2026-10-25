import  { useState, useEffect, useCallback, useRef,  useMemo, type RefObject, type DependencyList } from "react";
import type { UserResponseDTO } from "../api/models/authModels";
import  { APP_CONFIG } from "../config/appConfig";
import { getErrorMessage } from "../utils/errorUtils";
import { fullName } from "../utils/formatUtils";
import type { LoadStatus } from "../types/common";
import { authService } from "../api";


export function useDebounce<T>(value: T, delay = 300): T {
  const [debounced, setDebounced] = useState(value);
  useEffect(() => {
    const t = setTimeout(() => setDebounced(value), delay);
    return () => clearTimeout(t);
  }, [value, delay]);
  return debounced;
}

export function useDisclosure(initial = false) {
  const [isOpen, setIsOpen] = useState(initial);
  const open = useCallback(() => setIsOpen(true), []);
  const close = useCallback(() => setIsOpen(false), []);
  const toggle = useCallback(() => setIsOpen((v) => !v), []);
  return { isOpen, open, close, toggle };
}

export function useClickOutside(
  ref: RefObject<HTMLElement | null>,
  handler: () => void,
  enabled = true
) {
  const handlerRef = useRef(handler);
  handlerRef.current = handler;

  useEffect(() => {
    if (!enabled) return;
    const listener = (e: MouseEvent | TouchEvent) => {
      const el = ref.current;
      if (!el || el.contains(e.target as Node)) return;
      handlerRef.current();
    };
    document.addEventListener('mousedown', listener);
    document.addEventListener('touchstart', listener);
    return () => {
      document.removeEventListener('mousedown', listener);
      document.removeEventListener('touchstart', listener);
    };
  }, [ref, enabled]);
}

export function useMediaQuery(query: string): boolean {
  const [matches, setMatches] = useState(() =>
    typeof window !== 'undefined' ? window.matchMedia(query).matches : false
  );
  useEffect(() => {
    const mql = window.matchMedia(query);
    const onChange = () => setMatches(mql.matches);
    onChange();
    mql.addEventListener('change', onChange);
    return () => mql.removeEventListener('change', onChange);
  }, [query]);
  return matches;
}

export function useDocumentTitle(title: string) {
  useEffect(() => {
    document.title = title ? `${title} · ${APP_CONFIG.name}` : APP_CONFIG.name;
  }, [title]);
}

/**
 * Generic async loader with stale-response protection.
 * Re-runs when `deps` change; call `reload()` to refetch manually.
 */
export function useAsync<T>(
  fn: () => Promise<T>,
  deps: DependencyList = [],
  initialData: T | null = null
) {
  const [data, setData] = useState<T | null>(initialData);
  const [status, setStatus] = useState<LoadStatus>('loading');
  const [error, setError] = useState<string | null>(null);
  const requestId = useRef(0);
  const fnRef = useRef(fn);
  fnRef.current = fn;

  const run = useCallback(async () => {
    const id = ++requestId.current;
    setStatus('loading');
    setError(null);
    try {
      const result = await fnRef.current();
      if (id === requestId.current) {
        setData(result);
        setStatus('success');
      }
      return result;
    } catch (e) {
      if (id === requestId.current) {
        setError(getErrorMessage(e));
        setStatus('error');
      }
      return null;
    }
  }, []);

  useEffect(() => {
    run();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, deps);

  return { data, setData, status, error, loading: status === 'loading', reload: run };
}

/**
 * User directory (reassignment picker, management, messaging).
 * Uses the PLACEHOLDER list-users call in userService.
 */
export function useUserDirectory(enabled = true) {
  const { data, loading, error, reload } = useAsync<UserResponseDTO[]>(
    () => (enabled ? authService.getAllUsers() : Promise.resolve([])),
    [enabled]
  );

  const users = useMemo(() => data ?? [], [data]);
  const byId = useMemo(() => new Map(users.map((u) => [u.id, u])), [users]);

  const nameOf = useCallback(
    (id?: number | null, fallback = 'Unassigned') => {
      if (id == null) return fallback;
      const u = byId.get(id);
      return u ? fullName(u) : `User #${id}`;
    },
    [byId]
  );

  return { users, byId, nameOf, loading, error, reload };
}