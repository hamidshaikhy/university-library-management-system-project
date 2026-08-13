import { Navigate, Route, Routes } from 'react-router-dom'
import { useAuth } from './context/AuthContext.jsx'
import { Loading } from './components/UI.jsx'
import Layout from './components/Layout.jsx'
import AdminLayout from './components/AdminLayout.jsx'
import { LoginPage, RegisterPage } from './pages/AuthPages.jsx'
import {
  BookDetailPage,
  BooksPage,
  DashboardPage,
  MyBorrowsPage,
  MyReservationsPage,
  NotFoundPage,
  ProfilePage,
} from './pages/UserPages.jsx'
import {
  AdminAuthorsPage,
  AdminBooksPage,
  AdminBorrowsPage,
  AdminCategoriesPage,
  AdminDashboardPage,
  AdminReservationsPage,
  AdminUsersPage,
} from './pages/AdminPages.jsx'

function ProtectedRoute({ role, children }) {
  const { user, loading } = useAuth()
  if (loading) return <Loading label="در حال بررسی ورود..." />
  if (!user) return <Navigate to="/login" replace />
  if (role && user.role !== role) return <Navigate to={user.role === 'ADMIN' ? '/admin/dashboard' : '/dashboard'} replace />
  return children
}

function PublicOnly({ children }) {
  const { user, loading } = useAuth()
  if (loading) return <Loading label="در حال بررسی ورود..." />
  if (user) return <Navigate to={user.role === 'ADMIN' ? '/admin/dashboard' : '/dashboard'} replace />
  return children
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<PublicOnly><LoginPage /></PublicOnly>} />
      <Route path="/register" element={<PublicOnly><RegisterPage /></PublicOnly>} />
      <Route element={<ProtectedRoute><Layout /></ProtectedRoute>}>
        <Route index element={<HomeRedirect />} />
        <Route path="profile" element={<ProfilePage />} />
        <Route path="books" element={<BooksPage />} />
        <Route path="books/:id" element={<BookDetailPage />} />
        <Route path="dashboard" element={<ProtectedRoute role="USER"><DashboardPage /></ProtectedRoute>} />
        <Route path="my-reservations" element={<ProtectedRoute role="USER"><MyReservationsPage /></ProtectedRoute>} />
        <Route path="my-borrows" element={<ProtectedRoute role="USER"><MyBorrowsPage /></ProtectedRoute>} />
        <Route path="admin" element={<ProtectedRoute role="ADMIN"><AdminLayout /></ProtectedRoute>}>
          <Route index element={<Navigate to="dashboard" replace />} />
          <Route path="dashboard" element={<AdminDashboardPage />} />
          <Route path="users" element={<AdminUsersPage />} />
          <Route path="authors" element={<AdminAuthorsPage />} />
          <Route path="categories" element={<AdminCategoriesPage />} />
          <Route path="books" element={<AdminBooksPage />} />
          <Route path="borrows" element={<AdminBorrowsPage />} />
          <Route path="reservations" element={<AdminReservationsPage />} />
        </Route>
      </Route>
      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  )
}

function HomeRedirect() {
  const { user } = useAuth()
  return <Navigate to={user.role === 'ADMIN' ? '/admin/dashboard' : '/dashboard'} replace />
}
