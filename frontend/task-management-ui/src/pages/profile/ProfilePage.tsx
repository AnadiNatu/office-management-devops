import PageHeader from "../../components/common/PageHeader";
import ChangePasswordCard from "../../features/auth/ChangePasswordCard";
import EmailVerificationCard from "../../features/auth/EmailVerificationCard";
import PhoneVerificationCard from "../../features/auth/PhoneVerficationCard";
import ProfilePhotoCard from "../../features/auth/ProfilePhotoCard";
import { useAuth } from "../../hooks/useAuth";
import Badge from "../../ui/Badge";
import Card from "../../ui/Card";
import { ROLE_LABELS } from "../../utils/roleUtils";

const ProfilePage: React.FC = () => {
  const { user } = useAuth();
  if (!user) return null;

  return (
    <div className="space-y-8 pb-16">
      <PageHeader title="Profile" subtitle="Manage your identity and account security" />

      <Card padded={false} variant="dark" className="p-8">
        <div className="flex flex-wrap items-center justify-between gap-4">
          <div>
            <p className="text-xs font-black uppercase tracking-widest text-slate-400">Account</p>
            <h2 className="mt-1 text-2xl font-black uppercase tracking-tight">
              {user.fname} {user.lname}
            </h2>
            <p className="mt-1 text-sm font-bold text-slate-300">{user.email}</p>
          </div>
          <Badge tone="indigo">{ROLE_LABELS[user.role]}</Badge>
        </div>
      </Card>

      <ProfilePhotoCard />
      <EmailVerificationCard />
      <PhoneVerificationCard />
      <ChangePasswordCard />
    </div>
  );
};

export default ProfilePage;