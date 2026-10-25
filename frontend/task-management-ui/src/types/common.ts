export type Nullable<T> = T | null;

export interface SelectOption<T = string | number> {
  label: string;
  value: T;
}

export type LoadStatus = 'idle' | 'loading' | 'success' | 'error';

export interface AsyncState<T> {
  data: T;
  status: LoadStatus;
  error: string | null;
}