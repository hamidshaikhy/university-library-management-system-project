import { NavLink, Outlet } from 'react-router-dom'

const links = [
  ['/admin/dashboard', 'bi-grid-1x2', 'داشبورد'],
  ['/admin/books', 'bi-book', 'کتاب‌ها'],
  ['/admin/authors', 'bi-pen', 'نویسندگان'],
  ['/admin/categories', 'bi-tags', 'دسته‌بندی‌ها'],
  ['/admin/borrows', 'bi-arrow-left-right', 'امانت‌ها'],
  ['/admin/reservations', 'bi-bookmark-check', 'رزروها'],
  ['/admin/users', 'bi-people', 'کاربران'],
]

export default function AdminLayout() {
  return (
    <div className="container-fluid py-4">
      <div className="row g-4">
        <div className="col-lg-3 col-xl-2">
          <aside className="admin-sidebar p-3">
            <div className="sidebar-title">مدیریت سامانه</div>
            <nav className="nav flex-column gap-1 mt-3">
              {links.map(([to, icon, title]) => (
                <NavLink key={to} className="sidebar-link" to={to}>
                  <i className={`bi ${icon}`} />{title}
                </NavLink>
              ))}
            </nav>
          </aside>
        </div>
        <div className="col-lg-9 col-xl-10"><Outlet /></div>
      </div>
    </div>
  )
}
