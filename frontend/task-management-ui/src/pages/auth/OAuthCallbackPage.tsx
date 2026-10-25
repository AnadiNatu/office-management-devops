// ASSUMPTION: gateway redirects here as /oauth2/callback?token=...&refreshToken=...

import { useState, useEffect } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import { authStorage } from "../../auth/authStorage";
import { useAuth } from "../../hooks/useAuth";
import { ROUTES } from "../../routes/routeConstants";

// Adjust the param names / flow once the real OAuth2 redirect contract is confirmed.
const OAuthCallbackPage: React.FC = () => {
  const [params] = useSearchParams();
  const navigate = useNavigate();
  const { refreshUser } = useAuth();
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const token = params.get('token');
    const refreshToken = params.get('refreshToken');

    if (!token) {
      setError('No authentication token was returned.');
      return;
    }
    authStorage.setTokens(token, refreshToken);
    refreshUser().then((user) => {
      if (user) navigate(ROUTES.DASHBOARD, { replace: true });
      else setError('Could not load your account.');
    });
  }, [params, refreshUser, navigate]);

  return (
    <div className="space-y-6 text-center">
      {error ? (
        <>
          <h2 className="text-xl font-black uppercase tracking-tight text-red-600">Sign-in failed</h2>
          <p className="text-sm font-bold text-slate-600">{error}</p>
        </>
      ) : (
        <>
          <div className="mx-auto h-10 w-10 animate-spin rounded-full border-4 border-indigo-600 border-t-transparent" />
          <p className="text-sm font-bold text-slate-600">Completing sign-in…</p>
        </>
      )}
    </div>
  );
};

export default OAuthCallbackPage;