import React from 'react';
import { BrowserRouter,  } from 'react-router-dom';
import { AuthProvider } from './auth/AuthProvider';
import { AppRoutes } from './routes/AppRoutes';
import { ToastProvider } from './ui/Toast';

const App: React.FC = () => (
  <BrowserRouter>
    <ToastProvider>
      <AuthProvider>
        <AppRoutes />
      </AuthProvider>
    </ToastProvider>
  </BrowserRouter>
);

export default App;

// const App: React.FC = () => {
//   return <RouterProvider router={router} />;
// };

// export default App;