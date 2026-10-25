import { Search } from "lucide-react";
import Input from "../../ui/Input";
import Select from "../../ui/Select";
import { TASK_STATUS_OPTIONS } from "./TaskStatusBadge";

interface TaskFiltersProps {
  search: string;
  onSearchChange: (v: string) => void;
  status: string;
  onStatusChange: (v: string) => void;
}

export const TaskFilters: React.FC<TaskFiltersProps> = ({ search, onSearchChange, status, onStatusChange }) => (
  <div className="flex flex-col gap-3 sm:flex-row">
    <div className="flex-1">
      <Input
        placeholder="Search tasks…"
        leftIcon={<Search size={16} />}
        value={search}
        onChange={(e) => onSearchChange(e.target.value)}
      />
    </div>
    <div className="sm:w-56">
      <Select
        value={status}
        onChange={(e) => onStatusChange(e.target.value)}
        options={[{ label: 'All statuses', value: '' }, ...TASK_STATUS_OPTIONS]}
      />
    </div>
  </div>
);

export default TaskFilters;