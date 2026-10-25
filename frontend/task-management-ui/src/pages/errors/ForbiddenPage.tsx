import { ShieldOff } from "lucide-react";
import { useNavigate } from "react-router-dom";
import { ROUTES } from "../../routes/routeConstants";
import Button from "../../ui/Button";

const ForbiddenPage: React.FC = () => {
  const navigate = useNavigate();
  return (
    <div className="glass-panel mx-auto mt-10 flex max-w-xl flex-col items-center gap-5 rounded-[3rem] p-12 text-center">
      <div className="rounded-3xl bg-red-100 p-5 text-red-600">
        <ShieldOff size={40} />
      </div>
      <h1 className="text-3xl font-black uppercase tracking-tighter text-slate-950">Access denied</h1>
      <p className="text-sm font-bold text-slate-600">
        Your role doesn't have permission to open this page.
      </p>
      <Button onClick={() => navigate(ROUTES.DASHBOARD, { replace: true })}>Back to dashboard</Button>
    </div>
  );
};

export default ForbiddenPage;