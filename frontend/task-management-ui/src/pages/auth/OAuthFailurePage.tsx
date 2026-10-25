import { AlertTriangle } from "lucide-react";
import { useSearchParams, Link } from "react-router-dom";
import { ROUTES } from "../../routes/routeConstants";
import Button from "../../ui/Button";

const OAuthFailurePage: React.FC = () => {
  const [params] = useSearchParams();
  const reason = params.get('reason') ?? params.get('error') ?? 'The sign-in attempt was not completed.';

  return (
    <div className="space-y-6 text-center">
      <div className="mx-auto w-fit rounded-3xl bg-red-100 p-5 text-red-600">
        <AlertTriangle size={32} />
      </div>
      <div>
        <h2 className="text-2xl font-black uppercase tracking-tight text-slate-950">Sign-in failed</h2>
        <p className="mt-2 text-sm font-bold text-slate-600">{reason}</p>
      </div>
      <Link to={ROUTES.LOGIN}>
        <Button fullWidth>Back to sign in</Button>
      </Link>
    </div>
  );
};

export default OAuthFailurePage;