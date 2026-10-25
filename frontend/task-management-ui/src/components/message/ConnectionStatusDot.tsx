import type { WsConnectionState } from "../../types/webocket";
import { cn } from "../../utils/formatUtils";



const LABEL: Record<WsConnectionState, string> = {
  connected: 'Live',
  connecting: 'Connecting…',
  disconnected: 'Offline',
  error: 'Connection error',
};

const COLOR: Record<WsConnectionState, string> = {
  connected: 'bg-emerald-500',
  connecting: 'bg-amber-500 animate-pulse',
  disconnected: 'bg-slate-300',
  error: 'bg-red-500',
};

export const ConnectionStatusDot: React.FC<{ state: WsConnectionState }> = ({ state }) => (
  <span className="flex items-center gap-1.5 text-[9px] font-black uppercase tracking-widest text-slate-400">
    <span className={cn('h-1.5 w-1.5 rounded-full', COLOR[state])} aria-hidden />
    {LABEL[state]}
  </span>
);

export default ConnectionStatusDot;