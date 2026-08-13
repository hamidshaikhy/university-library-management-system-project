import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams, useSearchParams } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'
import api, { apiErrorMessage } from '../lib/api.js'
import { Alert, BookCover, ConfirmButton, EmptyState, Loading, StatusBadge } from '../components/UI.jsx'

const faDate = (value) => (value ? new Intl.DateTimeFormat('fa-IR').format(new Date(value)) : '—')

function BookCard({ book }) {
  return (
    <article className="book-card h-100">
      <BookCover book={book} />
      <div className="p-3">
        <span className="book-category">{book.category.title}</span>
        <h3>{book.title}</h3>
        <p className="text-secondary mb-3">{book.author.fullName}</p>
        <Link to={`/books/${book.id}`} className="btn btn-primary w-100">مشاهده کتاب</Link>
      </div>
    </article>
  )
}

export function DashboardPage() {
  const { user } = useAuth()
  const [data, setData] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    api.get('/dashboard').then(({ data: result }) => setData(result)).catch((e) => setError(apiErrorMessage(e)))
  }, [])

  if (!data && !error) return <Loading />
  return (
    <div className="container py-4 py-md-5">
      <Alert message={error} />
      <section className="hero-panel mb-4">
        <div>
          <span className="eyebrow text-white-50">داشبورد دانشجو</span>
          <h1>سلام {user.fullName} 👋</h1>
          <p>کتاب‌های جدید را ببینید، رزرو کنید و وضعیت امانت‌هایتان را بررسی کنید.</p>
          <Link to="/books" className="btn btn-light btn-lg"><i className="bi bi-search ms-2" />جست‌وجوی کتاب</Link>
        </div>
        <i className="bi bi-journal-bookmark-fill hero-icon" />
      </section>
      {data && (
        <>
          <div className="row g-3 mb-5">
            <div className="col-md-6"><Link to="/my-borrows" className="stat-card text-decoration-none"><span className="stat-icon bg-primary-subtle text-primary"><i className="bi bi-arrow-left-right" /></span><div><small>امانت‌های فعال من</small><strong>{data.activeBorrowCount}</strong></div><i className="bi bi-chevron-left me-auto" /></Link></div>
            <div className="col-md-6"><Link to="/my-reservations" className="stat-card text-decoration-none"><span className="stat-icon bg-warning-subtle text-warning-emphasis"><i className="bi bi-bookmark" /></span><div><small>همه رزروهای من</small><strong>{data.reservationCount}</strong></div><i className="bi bi-chevron-left me-auto" /></Link></div>
          </div>
          <div className="section-heading"><div><span className="eyebrow">تازه‌های کتابخانه</span><h2>جدیدترین کتاب‌ها</h2></div><Link to="/books" className="btn btn-outline-primary">مشاهده همه</Link></div>
          <div className="row g-4">
            {data.latestBooks.map((book) => <div className="col-sm-6 col-lg-3" key={book.id}><BookCard book={book} /></div>)}
          </div>
        </>
      )}
    </div>
  )
}

export function BooksPage() {
  const [searchParams, setSearchParams] = useSearchParams()
  const [form, setForm] = useState({ q: searchParams.get('q') || '', categoryId: searchParams.get('categoryId') || '' })
  const [data, setData] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    setData(null)
    api.get('/books', { params: Object.fromEntries([...searchParams].filter(([, value]) => value)) })
      .then(({ data: result }) => setData(result))
      .catch((e) => setError(apiErrorMessage(e)))
  }, [searchParams])

  function search(event) {
    event.preventDefault()
    const next = {}
    if (form.q.trim()) next.q = form.q.trim()
    if (form.categoryId) next.categoryId = form.categoryId
    setSearchParams(next)
  }

  return (
    <div className="container py-5">
      <div className="section-heading"><div><span className="eyebrow">مخزن کتابخانه</span><h1>کتاب‌ها</h1></div></div>
      <form className="filter-panel row g-3 align-items-end mb-4" onSubmit={search}>
        <div className="col-md-7"><label className="form-label">جست‌وجوی عنوان</label><div className="input-icon"><i className="bi bi-search" /><input className="form-control" value={form.q} onChange={(e) => setForm({ ...form, q: e.target.value })} placeholder="مثلاً کدنویسی تمیز" /></div></div>
        <div className="col-md-3"><label className="form-label">دسته‌بندی</label><select className="form-select" value={form.categoryId} onChange={(e) => setForm({ ...form, categoryId: e.target.value })}><option value="">همه دسته‌ها</option>{data?.categories.map((category) => <option key={category.id} value={category.id}>{category.title}</option>)}</select></div>
        <div className="col-md-2 d-grid"><button className="btn btn-primary">جست‌وجو</button></div>
      </form>
      <Alert message={error} />
      {!data && !error ? <Loading /> : data?.books.length === 0 ? <EmptyState icon="bi-search">کتابی با این مشخصات پیدا نشد.</EmptyState> : (
        <div className="row g-4">{data?.books.map((book) => <div className="col-sm-6 col-lg-4 col-xl-3" key={book.id}><BookCard book={book} /></div>)}</div>
      )}
    </div>
  )
}

export function BookDetailPage() {
  const { id } = useParams()
  const { user } = useAuth()
  const [book, setBook] = useState(null)
  const [message, setMessage] = useState({ text: '', type: 'danger' })
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    api.get(`/books/${id}`).then(({ data }) => setBook(data)).catch((e) => setMessage({ text: apiErrorMessage(e), type: 'danger' }))
  }, [id])

  async function reserve() {
    setSubmitting(true)
    setMessage({ text: '', type: 'danger' })
    try {
      await api.post(`/books/${id}/reservations`)
      setMessage({ text: 'درخواست رزرو با موفقیت ثبت شد.', type: 'success' })
    } catch (e) {
      setMessage({ text: apiErrorMessage(e), type: 'danger' })
    } finally {
      setSubmitting(false)
    }
  }

  if (!book && !message.text) return <Loading />
  return (
    <div className="container py-5">
      <Alert message={message.text} type={message.type} onClose={() => setMessage({ text: '', type: 'danger' })} />
      {book && <div className="detail-card"><div className="row g-4 align-items-start"><div className="col-md-4 col-lg-3"><BookCover book={book} large /></div><div className="col-md-8 col-lg-9"><span className="book-category">{book.category.title}</span><h1>{book.title}</h1><p className="lead text-secondary">{book.author.fullName}</p><div className="book-meta"><span><i className="bi bi-upc-scan" />ISBN: {book.isbn}</span><span><i className="bi bi-calendar3" />سال انتشار: {book.publishYear || 'نامشخص'}</span><span><i className="bi bi-stack" />موجودی: {book.quantity}</span></div><hr /><h2 className="h5">درباره کتاب</h2><p className="text-secondary description-text">{book.description || 'توضیحی برای این کتاب ثبت نشده است.'}</p><div className="d-flex flex-wrap gap-2 mt-4"><Link to="/books" className="btn btn-outline-secondary"><i className="bi bi-arrow-right ms-2" />بازگشت</Link>{user.role === 'USER' && <button className="btn btn-primary" disabled={submitting || book.quantity <= 0} onClick={reserve}><i className="bi bi-bookmark-plus ms-2" />{submitting ? 'در حال ثبت...' : 'درخواست رزرو'}</button>}</div></div></div></div>}
    </div>
  )
}

export function MyReservationsPage() {
  const [items, setItems] = useState(null)
  const [message, setMessage] = useState({ text: '', type: 'danger' })

  function load() {
    api.get('/me/reservations').then(({ data }) => setItems(data)).catch((e) => setMessage({ text: apiErrorMessage(e), type: 'danger' }))
  }
  useEffect(load, [])

  async function cancel(id) {
    try {
      const { data } = await api.post(`/me/reservations/${id}/cancel`)
      setMessage({ text: data.message, type: 'success' })
      load()
    } catch (e) {
      setMessage({ text: apiErrorMessage(e), type: 'danger' })
    }
  }

  return (
    <div className="container py-5">
      <Alert message={message.text} type={message.type} onClose={() => setMessage({ text: '', type: 'danger' })} />
      <div className="section-heading"><div><span className="eyebrow">حساب کاربری</span><h1>رزروهای من</h1></div><Link to="/books" className="btn btn-primary">رزرو کتاب جدید</Link></div>
      {!items ? <Loading /> : <div className="table-card"><div className="table-responsive"><table className="table align-middle mb-0"><thead><tr><th>کتاب</th><th>تاریخ درخواست</th><th>وضعیت</th><th /></tr></thead><tbody>{items.map((item) => <tr key={item.id}><td><strong>{item.book.title}</strong><small className="d-block text-secondary">{item.book.author.fullName}</small></td><td>{faDate(item.reservationDate)}</td><td><StatusBadge status={item.status} /></td><td className="text-end">{item.status === 'PENDING' && <ConfirmButton onConfirm={() => cancel(item.id)} message="این رزرو لغو شود؟">لغو رزرو</ConfirmButton>}</td></tr>)}{items.length === 0 && <tr><td colSpan="4"><EmptyState icon="bi-bookmark">هنوز رزروی ثبت نکرده‌اید.</EmptyState></td></tr>}</tbody></table></div></div>}
    </div>
  )
}

export function MyBorrowsPage() {
  const [items, setItems] = useState(null)
  const [error, setError] = useState('')
  useEffect(() => { api.get('/me/borrows').then(({ data }) => setItems(data)).catch((e) => setError(apiErrorMessage(e))) }, [])
  return (
    <div className="container py-5">
      <Alert message={error} />
      <div className="section-heading"><div><span className="eyebrow">حساب کاربری</span><h1>امانت‌های من</h1></div><Link to="/books" className="btn btn-outline-primary">مشاهده کتاب‌ها</Link></div>
      {!items ? <Loading /> : <div className="table-card"><div className="table-responsive"><table className="table align-middle mb-0"><thead><tr><th>کتاب</th><th>تاریخ امانت</th><th>سررسید</th><th>وضعیت</th></tr></thead><tbody>{items.map((item) => <tr key={item.id}><td><strong>{item.book.title}</strong><small className="d-block text-secondary">{item.book.author.fullName}</small></td><td>{faDate(item.borrowDate)}</td><td>{faDate(item.dueDate)}</td><td>{item.returned ? <span className="status-badge approved">بازگردانده‌شده</span> : <span className={`status-badge ${new Date(item.dueDate) < new Date() ? 'rejected' : 'pending'}`}>{new Date(item.dueDate) < new Date() ? 'دیرکرد' : 'فعال'}</span>}</td></tr>)}{items.length === 0 && <tr><td colSpan="4"><EmptyState icon="bi-arrow-left-right">امانتی برای شما ثبت نشده است.</EmptyState></td></tr>}</tbody></table></div></div>}
    </div>
  )
}

export function ProfilePage() {
  const [profile, setProfile] = useState(null)
  const [error, setError] = useState('')
  useEffect(() => { api.get('/profile').then(({ data }) => setProfile(data)).catch((e) => setError(apiErrorMessage(e))) }, [])
  return (
    <div className="container py-5"><Alert message={error} />{!profile && !error ? <Loading /> : profile && <div className="profile-card mx-auto"><div className="profile-avatar"><i className="bi bi-person" /></div><h2>{profile.fullName}</h2><p className="text-secondary">{profile.email}</p><span className="badge rounded-pill text-bg-primary px-3 py-2">{profile.role === 'ADMIN' ? 'مدیر سیستم' : 'کاربر عادی'}</span><hr /><p className="text-secondary mb-0">این اطلاعات از جدول users در MySQL و از طریق REST API خوانده شده است.</p></div>}</div>
  )
}

export function NotFoundPage() {
  const navigate = useNavigate()
  return <main className="error-body"><div className="error-card"><div className="error-code">404</div><i className="bi bi-signpost-split error-icon" /><h1>صفحه پیدا نشد</h1><p>آدرسی که وارد کرده‌اید در سامانه وجود ندارد.</p><button className="btn btn-primary" onClick={() => navigate('/')}>بازگشت به صفحه اصلی</button></div></main>
}
