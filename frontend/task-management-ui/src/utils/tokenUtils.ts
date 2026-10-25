interface JwtPayload {
  exp?: number;
  [key: string]: unknown;
}

export const decodeJwt = (token: string): JwtPayload | null => {
  try {
    const payload = token.split('.')[1];
    const json = decodeURIComponent(
      atob(payload.replace(/-/g, '+').replace(/_/g, '/'))
        .split('')
        .map((c) => '%' + c.charCodeAt(0).toString(16).padStart(2, '0'))
        .join('')
    );
    return JSON.parse(json);
  } catch {
    return null;
  }
};

export const isTokenExpired = (token: string | null, skewSeconds = 10): boolean => {
  if (!token) return true;
  const exp = decodeJwt(token)?.exp;
  if (!exp) return false;
  return exp * 1000 <= Date.now() + skewSeconds * 1000;
};