import { authStorage } from '../../auth/authStorage';

const API_BASE_URL =
  (typeof import.meta !== 'undefined'
    && import.meta.env?.VITE_API_BASE_URL)
    || '';

interface RequestConfig extends RequestInit {
  requiresAuth?: boolean;
}

class ApiService {
  private baseURL: string;

  constructor(baseURL: string) {
    this.baseURL = baseURL.replace(/\/+$/, '');
  }

  getBaseURL(): string {
    return this.baseURL;
  }

  private getAuthToken(): string | null {
    return authStorage.getAccessToken();
  }

  private buildHeaders(customHeaders?: HeadersInit): HeadersInit {
    const headers: Record<string, string> = {
      'Content-Type': 'application/json',
    };

    if (customHeaders) {
      new Headers(customHeaders).forEach((value, key) => {
        headers[key] = value;
      });
    }

    const token = this.getAuthToken();

    if (token) {
      headers.Authorization = `Bearer ${token}`;
    }

    return headers;
  }

  private async handleResponse<T>(response: Response): Promise<T> {
    if (response.status === 401) {
      authStorage.clear();
      window.location.replace('/login');
      throw new Error('Session expired. Please log in again.');
    }

    if (!response.ok) {
      let errorMessage = `HTTP error! status: ${response.status}`;

      try {
        const error: unknown = await response.json();

        if (
          typeof error === 'object' &&
          error !== null
        ) {
          const candidate = error as {
            message?: unknown;
            error?: unknown;
          };

          if (typeof candidate.message === 'string') {
            errorMessage = candidate.message;
          } else if (typeof candidate.error === 'string') {
            errorMessage = candidate.error;
          }
        }
      } catch {
        // Ignore invalid JSON.
      }

      throw new Error(errorMessage);
    }

    if (response.status === 204) {
      return undefined as T;
    }

    const contentType = response.headers.get('content-type');

    if (
      contentType &&
      contentType.includes('application/json')
    ) {
      return (await response.json()) as T;
    }

    return (await response.text()) as T;
  }

  async get<T>(
    endpoint: string,
    config?: RequestConfig,
  ): Promise<T> {
    const response = await fetch(
      `${this.baseURL}${endpoint}`,
      {
        method: 'GET',
        headers: this.buildHeaders(config?.headers),
        credentials: 'include',
        ...config,
      },
    );

    return this.handleResponse<T>(response);
  }

  async post<T, D = unknown>(
    endpoint: string,
    data?: D,
    config?: RequestConfig,
  ): Promise<T> {
    const response = await fetch(
      `${this.baseURL}${endpoint}`,
      {
        method: 'POST',
        headers: this.buildHeaders(config?.headers),
        credentials: 'include',
        body:
          data !== undefined
            ? JSON.stringify(data)
            : undefined,
        ...config,
      },
    );

    return this.handleResponse<T>(response);
  }

  async put<T, D = unknown>(
    endpoint: string,
    data?: D,
    config?: RequestConfig,
  ): Promise<T> {
    const response = await fetch(
      `${this.baseURL}${endpoint}`,
      {
        method: 'PUT',
        headers: this.buildHeaders(config?.headers),
        credentials: 'include',
        body:
          data !== undefined
            ? JSON.stringify(data)
            : undefined,
        ...config,
      },
    );

    return this.handleResponse<T>(response);
  }

  async patch<T, D = unknown>(
    endpoint: string,
    data?: D,
    config?: RequestConfig,
  ): Promise<T> {
    const response = await fetch(
      `${this.baseURL}${endpoint}`,
      {
        method: 'PATCH',
        headers: this.buildHeaders(config?.headers),
        credentials: 'include',
        body:
          data !== undefined
            ? JSON.stringify(data)
            : undefined,
        ...config,
      },
    );

    return this.handleResponse<T>(response);
  }

  async delete<T>(
    endpoint: string,
    config?: RequestConfig,
  ): Promise<T> {
    const response = await fetch(
      `${this.baseURL}${endpoint}`,
      {
        method: 'DELETE',
        headers: this.buildHeaders(config?.headers),
        credentials: 'include',
        ...config,
      },
    );

    return this.handleResponse<T>(response);
  }

  async upload<T>(
    endpoint: string,
    formData: FormData,
    method: 'POST' | 'PUT' = 'POST',
  ): Promise<T> {
    const token = this.getAuthToken();

    const headers: Record<string, string> = {};

    if (token) {
      headers.Authorization = `Bearer ${token}`;
    }

    const response = await fetch(
      `${this.baseURL}${endpoint}`,
      {
        method,
        headers,
        credentials: 'include',
        body: formData,
      },
    );

    return this.handleResponse<T>(response);
  }

  async postQuery<T>(
    endpoint: string,
    params: Record<string, string>,
  ): Promise<T> {
    const query = new URLSearchParams(params).toString();

    const response = await fetch(
      `${this.baseURL}${endpoint}?${query}`,
      {
        method: 'POST',
        headers: this.buildHeaders(),
        credentials: 'include',
      },
    );

    return this.handleResponse<T>(response);
  }
}

export const apiService = new ApiService(API_BASE_URL);
