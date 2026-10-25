
import React, { useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import {
  ArrowRight,
  Bell,
  CheckSquare,
  Clock,
  FileText,
  MessagesSquare,
  Users,
} from 'lucide-react';
import { useAuth } from '../../hooks/useAuth';
import { useTasks } from '../../hooks/useTasks';
import { useConversations } from '../../hooks/useMessaging';
import { useUserDirectory } from '../../hooks/useCommon';
import { can } from '../../auth/permissions';
import { Card } from '../../ui/Card';
import { Badge } from '../../ui/Badge';
import { LoadingState } from '../../components/common/LoadingState';
import { EmptyState } from '../../components/common/EmptyState';
import { TaskStatusBadge } from '../../components/task/TaskStatusBadge';
import { ROUTES } from '../../routes/routeConstants';
import { formatDate, isOverdue } from '../../utils/dateUtils';
import { useUnreadCount } from '../../hooks/useNotification';

const MotionDiv = motion.div as any;

interface StatCard {
  label: string;
  value: number;
  icon: React.ReactNode;
  color: string;
  bg: string;
  path: string;
}

const DashboardPage: React.FC = () => {
  const { user } = useAuth();
  const navigate = useNavigate();

  const {
    tasks = [],
    loading: tasksLoading,
    canViewAll,
  } = useTasks();

  const { count: unreadNotifications } = useUnreadCount();

  const {
    conversations = [],
    loading: conversationsLoading,
  } = useConversations();

  const canSeeDirectory = can(
    user?.role,
    'ACCESS_MANAGEMENT'
  );

  const {
    users = [],
    loading: usersLoading,
  } = useUserDirectory(canSeeDirectory);

  /*
   * ---------------------------------------------------------
   * SAFE DATA NORMALIZATION
   * ---------------------------------------------------------
   *
   * The dashboard should never crash because an API response
   * contains an omitted/null collection.
   *
   * In particular, conversation.participantIds was previously
   * used directly with .find(), which caused:
   *
   * Cannot read properties of undefined (reading 'find')
   *
   * when participantIds was missing from a conversation response.
   */

  const safeTasks = Array.isArray(tasks)
    ? tasks
    : [];

  const safeConversations = Array.isArray(
    conversations
  )
    ? conversations
    : [];

  const safeUsers = Array.isArray(users)
    ? users
    : [];

  const myTasks = useMemo(
    () =>
      canViewAll
        ? safeTasks.filter(
            (task) =>
              task.assignedToId === user?.id
          )
        : safeTasks,
    [
      safeTasks,
      canViewAll,
      user?.id,
    ]
  );

  const pendingCount = useMemo(
    () =>
      myTasks.filter(
        (task) =>
          task.status === 'CREATED' ||
          task.status === 'ASSIGNED' ||
          task.status === 'IN_PROGRESS'
      ).length,
    [myTasks]
  );

  const completedCount = useMemo(
    () =>
      myTasks.filter(
        (task) =>
          task.status === 'COMPLETED'
      ).length,
    [myTasks]
  );

  const overdueCount = useMemo(
    () =>
      myTasks.filter(
        (task) =>
          isOverdue(
            task.dueDate,
            task.status
          )
      ).length,
    [myTasks]
  );

  const activeOrgTasks = useMemo(
    () =>
      safeTasks.filter(
        (task) =>
          task.status !== 'COMPLETED' &&
          task.status !== 'CANCELLED'
      ).length,
    [safeTasks]
  );

  const stats: StatCard[] = canViewAll
    ? [
        {
          label: 'Total Personnel',
          value: safeUsers.length,
          icon: <Users />,
          color: 'text-blue-800',
          bg: 'bg-blue-100',
          path: ROUTES.MANAGEMENT,
        },
        {
          label: 'Active Objectives',
          value: activeOrgTasks,
          icon: <FileText />,
          color: 'text-purple-800',
          bg: 'bg-purple-100',
          path: ROUTES.TASKS,
        },
        {
          label: 'Overdue Tasks',
          value: overdueCount,
          icon: <Clock />,
          color: 'text-red-800',
          bg: 'bg-red-100',
          path: ROUTES.TASKS,
        },
      ]
    : [
        {
          label: 'My Pending Tasks',
          value: pendingCount,
          icon: <Clock />,
          color: 'text-orange-800',
          bg: 'bg-orange-100',
          path: ROUTES.TASKS,
        },
        {
          label: 'Tasks Completed',
          value: completedCount,
          icon: <CheckSquare />,
          color: 'text-green-800',
          bg: 'bg-green-100',
          path: ROUTES.TASKS,
        },
        {
          label: 'Unread Notifications',
          value: unreadNotifications ?? 0,
          icon: <Bell />,
          color: 'text-indigo-800',
          bg: 'bg-indigo-100',
          path: ROUTES.NOTIFICATIONS,
        },
      ];

  const upcomingTasks = useMemo(
    () =>
      [...myTasks]
        .filter(
          (task) =>
            task.status !== 'COMPLETED' &&
            task.status !== 'CANCELLED'
        )
        .sort((a, b) =>
          (
            a.dueDate ?? ''
          ).localeCompare(
            b.dueDate ?? ''
          )
        )
        .slice(0, 5),
    [myTasks]
  );

  const recentConversations = useMemo(
    () =>
      safeConversations
        .filter(
          (conversation) =>
            conversation != null
        )
        .slice(0, 5),
    [safeConversations]
  );

  return (
    <div className="space-y-10 pb-20">
      <div className="flex flex-col gap-2">
        <h1 className="text-4xl md:text-5xl font-black uppercase leading-none tracking-tighter text-slate-950">
          {canViewAll
            ? 'Command Center'
            : `Welcome, ${
                user?.fname ?? 'there'
              }`}
        </h1>

        <p className="text-base font-bold text-slate-700 md:text-lg">
          {canViewAll
            ? 'Operational metrics across the organization'
            : "Here's what needs your attention today"}
        </p>
      </div>

      <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 md:gap-8 lg:grid-cols-3">
        {stats.map(
          (stat, idx) => (
            <MotionDiv
              key={stat.label}
              initial={{
                opacity: 0,
                y: 20,
              }}
              animate={{
                opacity: 1,
                y: 0,
              }}
              transition={{
                delay: idx * 0.08,
              }}
              onClick={() =>
                navigate(
                  stat.path
                )
              }
              className="group relative flex cursor-pointer items-center justify-between overflow-hidden rounded-[2.5rem] border border-slate-200 bg-white p-6 shadow-2xl shadow-slate-300/10 transition-all hover:border-indigo-600 active:scale-95 md:p-8"
            >
              <div className="relative z-10">
                <p className="mb-2 text-[10px] font-black uppercase tracking-[0.3em] text-slate-500">
                  {stat.label}
                </p>

                <h3 className="mt-1 text-4xl font-black leading-none tracking-tighter text-slate-950 md:text-5xl">
                  {tasksLoading ||
                  (canViewAll &&
                    usersLoading)
                    ? '—'
                    : stat.value}
                </h3>
              </div>

              <div
                className={`rounded-2xl p-4 shadow-lg transition-transform group-hover:scale-110 md:p-5 ${stat.bg} ${stat.color}`}
              >
                {React.cloneElement(
                  stat.icon as React.ReactElement<any>,
                  {
                    size: 28,
                  }
                )}
              </div>
            </MotionDiv>
          )
        )}
      </div>

      <div className="grid grid-cols-1 gap-8 md:gap-10 lg:grid-cols-2">
        <Card
          title={
            canViewAll
              ? 'System Mission Log'
              : 'My Active Tasks'
          }
          icon={
            <FileText size={22} />
          }
          actions={
            <button
              onClick={() =>
                navigate(
                  ROUTES.TASKS
                )
              }
              className="rounded-xl bg-slate-950 px-5 py-2.5 text-[10px] font-black uppercase tracking-[0.2em] text-white hover:bg-slate-800"
            >
              View all
            </button>
          }
        >
          {tasksLoading ? (
            <LoadingState
              variant="skeleton"
              rows={3}
            />
          ) : upcomingTasks.length ===
            0 ? (
            <EmptyState
              icon={
                <CheckSquare size={40} />
              }
              title="No active tasks"
              description="You're all caught up."
            />
          ) : (
            <div className="space-y-2">
              {upcomingTasks.map(
                (task) => {
                  const overdue =
                    isOverdue(
                      task.dueDate,
                      task.status
                    );

                  return (
                    <div
                      key={task.id}
                      onClick={() =>
                        navigate(
                          ROUTES.TASK_DETAIL(
                            task.id
                          )
                        )
                      }
                      className="group flex cursor-pointer items-center justify-between gap-4 rounded-2xl border border-transparent p-4 transition-all hover:border-slate-200 hover:bg-slate-50"
                    >
                      <div className="flex min-w-0 items-center gap-4">
                        <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-2xl bg-slate-950 text-lg font-black text-white group-hover:bg-indigo-600">
                          {task.title
                            .charAt(0)
                            .toUpperCase()}
                        </div>

                        <div className="min-w-0">
                          <h4 className="truncate text-sm font-black uppercase text-slate-950">
                            {task.title}
                          </h4>

                          <div className="mt-1.5 flex items-center gap-2">
                            <TaskStatusBadge
                              status={
                                task.status
                              }
                              size="sm"
                            />

                            {task.dueDate && (
                              <span
                                className={`text-[9px] font-black uppercase ${
                                  overdue
                                    ? 'text-red-600'
                                    : 'text-slate-400'
                                }`}
                              >
                                {formatDate(
                                  task.dueDate
                                )}
                              </span>
                            )}
                          </div>
                        </div>
                      </div>

                      <ArrowRight
                        size={18}
                        className="shrink-0 text-slate-300 group-hover:text-slate-950"
                      />
                    </div>
                  );
                }
              )}
            </div>
          )}
        </Card>

        <Card
          title="Recent Conversations"
          icon={
            <MessagesSquare size={22} />
          }
          actions={
            <button
              onClick={() =>
                navigate(
                  ROUTES.MESSAGES
                )
              }
              className="rounded-xl bg-slate-950 px-5 py-2.5 text-[10px] font-black uppercase tracking-[0.2em] text-white hover:bg-slate-800"
            >
              Open inbox
            </button>
          }
        >
          {conversationsLoading ? (
            <LoadingState
              variant="skeleton"
              rows={3}
            />
          ) : recentConversations.length ===
            0 ? (
            <EmptyState
              icon={
                <MessagesSquare size={40} />
              }
              title="No conversations yet"
              description="Start a conversation from the Messages page."
            />
          ) : (
            <div className="space-y-2">
              {recentConversations.map(
                (conversation) => {
                  /*
                   * IMPORTANT:
                   *
                   * Do not directly call:
                   *
                   * conversation.participantIds.find(...)
                   *
                   * because the backend response may temporarily
                   * omit participantIds or return it as null.
                   *
                   * Normalizing it to [] makes the dashboard
                   * resilient while still displaying all available
                   * conversation information.
                   */
                  const participantIds =
                    Array.isArray(
                      conversation.participantUserIds
                    )
                      ? conversation.participantUserIds
                      : [];

                  const otherId =
                    participantIds.find(
                      (id) =>
                        id !==
                        user?.id
                    );

                  return (
                    <div
                      key={conversation.id}
                      onClick={() =>
                        navigate(
                          ROUTES.CONVERSATION(
                            conversation.id
                          )
                        )
                      }
                      className="group flex cursor-pointer items-center justify-between gap-4 rounded-2xl border border-transparent p-4 transition-all hover:border-slate-200 hover:bg-slate-50"
                    >
                      <div className="min-w-0">
                        <h4 className="truncate text-sm font-black uppercase text-slate-950">
                          Conversation #
                          {
                            conversation.id
                          }
                        </h4>

                        <p className="mt-1 truncate text-xs font-bold text-slate-500">
                          {
                            conversation
                              .lastMessage
                              ?.content ??
                            'No messages yet'
                          }
                        </p>
                      </div>

                      {otherId !=
                        null &&
                        participantIds.length >
                          0 && (
                          <Badge
                            tone="indigo"
                            size="sm"
                          >
                            {
                              participantIds.length
                            }{' '}
                            people
                          </Badge>
                        )}
                    </div>
                  );
                }
              )}
            </div>
          )}
        </Card>
      </div>
    </div>
  );
};

export default DashboardPage;