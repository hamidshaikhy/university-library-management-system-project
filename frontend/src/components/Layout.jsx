import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'

export default function Layout() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  async function handleLogout() {
    await logout()
    navigate('/login', { replace: true })
  }

  return (
    <div className="app-shell">
      <nav className="navbar navbar-expand-lg navbar-dark app-navbar sticky-top">
        <div className="container">
          <NavLink className="navbar-brand d-flex align-items-center gap-2" to={user.role === 'ADMIN' ? '/admin/dashboard' : '/dashboard'}>
            <span className="brand-mark"><i className="bi bi-book-half" /></span>
            <span>کتابخانه دانشگاه</span>
          </NavLink>
          <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNavbar" aria-label="نمایش منو">
            <span className="navbar-toggler-icon" />
          </button>
          <div className="collapse navbar-collapse" id="mainNavbar">
            <ul className="navbar-nav me-auto mb-2 mb-lg-0">
              {user.role === 'USER' ? (
                <>
                  <li className="nav-item"><NavLink className="nav-link" to="/dashboard">داشبورد</NavLink></li>
                  <li className="nav-item"><NavLink className="nav-link" to="/books">کتاب‌ها</NavLink></li>
                  <li className="nav-item"><NavLink className="nav-link" to="/my-reservations">رزروهای من</NavLink></li>
                  <li className="nav-item"><NavLink className="nav-link" to="/my-borrows">امانت‌های من</NavLink></li>
                </>
              ) : (
                <>
                  <li className="nav-item"><NavLink className="nav-link" to="/admin/dashboard">پنل مدیریت</NavLink></li>
                  <li className="nav-item"><NavLink className="nav-link" to="/books">نمایش کتاب‌ها</NavLink></li>
                </>
              )}
            </ul>
            <div className="d-flex align-items-center gap-2 text-white">
              <NavLink to="/profile" className="user-chip text-decoration-none">
                <i className="bi bi-person-circle" /><span>{user.fullName}</span>
              </NavLink>
              <button className="btn btn-sm btn-light" onClick={handleLogout}>
                <i className="bi bi-box-arrow-left ms-1" />خروج
              </button>
            </div>
          </div>
        </div>
      </nav>
      <main className="flex-grow-1"><Outlet /></main>
      <footer className="app-footer mt-5">
        <div className="container d-flex flex-wrap justify-content-between gap-2">
          <span>سامانه مدیریت کتابخانه دانشگاهی</span>
          <span>پروژه درس مهندسی اینترنت</span>
        </div>
      </footer>
    </div>
  )
}
