import { Compass } from "lucide-react";
import { useNavigate } from "react-router-dom";
import { ROUTES } from "../../routes/routeConstants";
import Button from "../../ui/Button";

const NotFoundPage: React.FC = () => {
  const navigate = useNavigate();
  return (
    <div className="app-bg flex min-h-screen items-center justify-center p-6">
      <div className="glass-panel flex max-w-xl flex-col items-center gap-5 rounded-[3rem] p-12 text-center">
        <div className="rounded-3xl bg-indigo-100 p-5 text-indigo-600">
          <Compass size={40} />
        </div>
        <p className="text-6xl font-black tracking-tighter text-slate-950">404</p>
        <p className="text-sm font-bold text-slate-600">
          The page you're looking for doesn't exist or has moved.
        </p>
        <Button onClick={() => navigate(ROUTES.DASHBOARD, { replace: true })}>Go home</Button>
      </div>
    </div>
  );
};

export default NotFoundPage;