import { Camera, Trash2 } from "lucide-react";
import { useRef, useState } from "react";
import { authService } from "../../api";
import { useAuth } from "../../hooks/useAuth";
import Avatar from "../../ui/Avatar";
import Button from "../../ui/Button";
import Card from "../../ui/Card";
import { useToast } from "../../ui/Toast";
import  { getErrorMessage } from "../../utils/errorUtils";
import { fullName } from "../../utils/formatUtils";

const MAX_SIZE = 5 * 1024 * 1024;
const ACCEPTED = ['image/jpeg', 'image/png', 'image/webp'];

export const ProfilePhotoCard: React.FC = () => {
  const { user, updateUser } = useAuth();
  const toast = useToast();
  const inputRef = useRef<HTMLInputElement>(null);
  const [uploading, setUploading] = useState(false);
  const [removing, setRemoving] = useState(false);

  const handleFile = async (file: File) => {
    if (!ACCEPTED.includes(file.type)) {
      toast.error('Use a JPEG, PNG, or WEBP image.');
      return;
    }
    if (file.size > MAX_SIZE) {
      toast.error('Image must be under 5 MB.');
      return;
    }
    setUploading(true);
    try {
      const res = await authService.uploadProfilePhoto(file);
      updateUser({ profilePicture: res.photoUrl ?? null });
      toast.success('Profile photo updated.');
    } catch (e) {
      toast.error(getErrorMessage(e, 'Could not upload photo'));
    } finally {
      setUploading(false);
    }
  };

  const handleRemove = async () => {
    setRemoving(true);
    try {
      await authService.removeProfilePhoto();
      updateUser({ profilePicture: null });
      toast.success('Profile photo removed.');
    } catch (e) {
      toast.error(getErrorMessage(e, 'Could not remove photo'));
    } finally {
      setRemoving(false);
    }
  };

  return (
    <Card title="Profile photo" subtitle="Shown across tasks, comments, and messages">
      <div className="flex flex-col items-center gap-5 sm:flex-row">
        <div className="relative">
          <Avatar src={user?.profilePicture} name={fullName(user)} size="xl" />
          <button
            type="button"
            onClick={() => inputRef.current?.click()}
            disabled={uploading}
            aria-label="Change photo"
            className="absolute -bottom-1 -right-1 rounded-full bg-indigo-600 p-2.5 text-white shadow-lg hover:bg-indigo-700 disabled:opacity-60"
          >
            <Camera size={16} />
          </button>
        </div>
        <div className="flex flex-1 flex-col gap-3 text-center sm:text-left">
          <p className="text-xs font-bold text-slate-600">JPEG, PNG, or WEBP. Max 5 MB.</p>
          <div className="flex flex-wrap justify-center gap-3 sm:justify-start">
            <Button size="sm" loading={uploading} onClick={() => inputRef.current?.click()}>
              Upload new
            </Button>
            {user?.profilePicture && (
              <Button
                size="sm"
                variant="danger"
                leftIcon={<Trash2 size={14} />}
                loading={removing}
                onClick={handleRemove}
              >
                Remove
              </Button>
            )}
          </div>
        </div>
      </div>
      <input
        ref={inputRef}
        type="file"
        accept={ACCEPTED.join(',')}
        className="hidden"
        onChange={(e) => {
          const file = e.target.files?.[0];
          if (file) handleFile(file);
          e.target.value = '';
        }}
      />
    </Card>
  );
};

export default ProfilePhotoCard;