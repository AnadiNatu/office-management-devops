import axios from 'axios';
import { API_CONFIG } from '../../config/apiConfig';
import { attachAuthInterceptor } from './authInterceptor';
import { attachErrorInterceptor } from './errorInterceptor';

export const axiosInstance = axios.create({
  baseURL: API_CONFIG.baseUrl,
  timeout: API_CONFIG.timeout,
  headers: { 'Content-Type': 'application/json' },
});

// Order matters: auth (refresh/retry) first, then error normalisation.
attachAuthInterceptor(axiosInstance);
attachErrorInterceptor(axiosInstance);