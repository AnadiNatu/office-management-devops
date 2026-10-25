// export function useAdminUsers() {
//   const { users, loading, error, reload } = useUserDirectory();
//   return {
//     users,
//     loading,
//     error,
//     reload,
//     setUsers: (_updater: (prev: typeof users) => typeof users) => {
//       // NOTE: useUserDirectory's underlying useAsync exposes setData, but useUserDirectory
//       // doesn't forward it. Swap this file to call useAsync directly once the real
//       // list-users endpoint lands, so admin actions can update the table optimistically
//       // instead of calling reload() after every action.
//       reload();
//     },
//   };
// }

import { useCallback } from "react";
import type { UserResponseDTO } from "../api/models/authModels";
import { useAsync } from "./useCommon";
import { authService } from "../api";

// function useUserDirectory(): { users: any; loading: any; error: any; reload: any; } {
//     throw new Error("Function not implemented.");
// }


export function useAdminUsers() {
  const { data, setData, loading, error, reload } = useAsync<UserResponseDTO[]>(
    () => authService.getAllUsers(),
    []
  );

  const setUsers = useCallback(
    (updater: (prev: UserResponseDTO[]) => UserResponseDTO[]) => {
      setData((prev) => updater(prev ?? []));
    },
    [setData]
  );

  return { users: data ?? [], loading, error, reload, setUsers };
}