import type { UserResponseDTO } from "../../api/models/authModels";
import Avatar from "../../ui/Avatar";
import Badge from "../../ui/Badge";
import { fullName } from "../../utils/formatUtils";
import { ROLE_LABELS, normalizeRole } from "../../utils/roleUtils";

interface UserTableProps {
  users: UserResponseDTO[];
  renderActions?: (user: UserResponseDTO) => React.ReactNode;
}

export const UserTable: React.FC<UserTableProps> = ({ users, renderActions }) => (
  <div className="overflow-x-auto">
    <table className="w-full text-left">
      <thead>
        <tr className="bg-slate-50/80 text-[10px] font-black uppercase tracking-widest text-slate-500">
          <th className="p-5">User</th>
          <th className="p-5">Role</th>
          <th className="p-5">Status</th>
          {renderActions && <th className="p-5 text-right">Actions</th>}
        </tr>
      </thead>
      <tbody className="divide-y divide-slate-100">
        {users.map((u) => (
          <tr key={u.id} className="transition-colors hover:bg-slate-50/60">
            <td className="p-5">
              <div className="flex items-center gap-3">
                <Avatar src={u.profilePicture} name={fullName(u)} size="sm" />
                <div className="min-w-0">
                  <p className="truncate text-xs font-black uppercase text-slate-900">{fullName(u)}</p>
                  <p className="truncate text-[11px] font-semibold text-slate-500">{u.email}</p>
                </div>
              </div>
            </td>
            <td className="p-5">
              <Badge tone="indigo" size="sm">
                {ROLE_LABELS[normalizeRole(u.role)]}
              </Badge>
            </td>
            <td className="p-5">
              <div className="flex flex-wrap gap-1.5">
                <Badge tone={u.enabled ? 'green' : 'red'} size="sm" dot>
                  {u.enabled ? 'Active' : 'Disabled'}
                </Badge>
                <Badge tone={u.emailVerified ? 'blue' : 'amber'} size="sm">
                  {u.emailVerified ? 'Email verified' : 'Unverified'}
                </Badge>
              </div>
            </td>
            {renderActions && <td className="p-5 text-right">{renderActions(u)}</td>}
          </tr>
        ))}
      </tbody>
    </table>
  </div>
);

export default UserTable;