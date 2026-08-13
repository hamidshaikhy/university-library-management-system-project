import { useEffect, useState } from 'react'
import api, { apiErrorMessage } from '../lib/api.js'
import { Alert, ConfirmButton, EmptyState, Loading, StatusBadge } from '../components/UI.jsx'

const faDate = (value) => (value ? new Intl.DateTimeFormat('fa-IR').format(new Date(value)) : '—')
const today = new Date().toISOString().slice(0, 10)

function PageHeading({ eyebrow = 'مدیریت سامانه', title, action }) {
  return <div className="section-heading"><div><span className="eyebrow">{eyebrow}</span><h1>{title}</h1></div>{action}</div>
}

export function AdminDashboardPage() {
  const [data, setData] = useState(null)
  const [error, setError] = useState('')
  useEffect(() => { api.get('/admin/dashboard').then(({ data: result }) => setData(result)).catch((e) => setError(apiErrorMessage(e))) }, [])
  const cards = data ? [
    ['bi-book', 'text-primary', 'bg-primary-subtle', 'تعداد کتاب‌ها', data.bookCount],
    ['bi-people', 'text-success', 'bg-success-subtle', 'کاربران', data.userCount],
    ['bi-arrow-left-right', 'text-warning-emphasis', 'bg-warning-subtle', 'امانت‌های فعال', data.activeBorrowCount],
    ['bi-bookmark-check', 'text-danger', 'bg-danger-subtle', 'رزروهای در انتظار', data.pendingReservationCount],
  ] : []
  return <><Alert message={error} /><PageHeading title="داشبورد مدیریت" />{!data && !error ? <Loading /> : <><div className="row g-3">{cards.map(([icon, color, bg, label, value]) => <div className="col-sm-6 col-xl-3" key={label}><div className="admin-stat-card"><span className={`stat-icon ${color} ${bg}`}><i className={`bi ${icon}`} /></span><small>{label}</small><strong>{value}</strong></div></div>)}</div><section className="admin-welcome mt-4"><div><span className="eyebrow text-white-50">پنل مدیر</span><h2>مدیریت کامل کتابخانه</h2><p>از منوی کناری کتاب‌ها، نویسندگان، دسته‌بندی‌ها، رزروها و امانت‌ها را مدیریت کنید.</p></div><i className="bi bi-speedometer2" /></section></>}</>
}

export function AdminUsersPage() {
  const [items, setItems] = useState(null)
  const [error, setError] = useState('')
  useEffect(() => { api.get('/admin/users').then(({ data }) => setItems(data)).catch((e) => setError(apiErrorMessage(e))) }, [])
  return <><Alert message={error} /><PageHeading title="کاربران" />{!items ? <Loading /> : <div className="table-card"><div className="table-responsive"><table className="table align-middle mb-0"><thead><tr><th>نام</th><th>ایمیل</th><th>نقش</th></tr></thead><tbody>{items.map((item) => <tr key={item.id}><td><strong>{item.fullName}</strong></td><td dir="ltr" className="text-end">{item.email}</td><td><span className={`badge ${item.role === 'ADMIN' ? 'text-bg-dark' : 'text-bg-primary'}`}>{item.role === 'ADMIN' ? 'مدیر' : 'کاربر'}</span></td></tr>)}</tbody></table></div></div>}</>
}

function SimpleCrudPage({ endpoint, title, noun, primaryField, primaryLabel, secondaryField, secondaryLabel }) {
  const emptyForm = { [primaryField]: '', [secondaryField]: '' }
  const [items, setItems] = useState(null)
  const [form, setForm] = useState(emptyForm)
  const [editingId, setEditingId] = useState(null)
  const [showForm, setShowForm] = useState(false)
  const [message, setMessage] = useState({ text: '', type: 'danger' })
  const [saving, setSaving] = useState(false)

  function load() {
    api.get(`/admin/${endpoint}`).then(({ data }) => setItems(data)).catch((e) => setMessage({ text: apiErrorMessage(e), type: 'danger' }))
  }
  useEffect(load, [endpoint])

  function startCreate() {
    setEditingId(null)
    setForm(emptyForm)
    setShowForm(true)
  }
  function startEdit(item) {
    setEditingId(item.id)
    setForm({ [primaryField]: item[primaryField] || '', [secondaryField]: item[secondaryField] || '' })
    setShowForm(true)
  }
  async function submit(event) {
    event.preventDefault()
    setSaving(true)
    try {
      if (editingId) await api.put(`/admin/${endpoint}/${editingId}`, form)
      else await api.post(`/admin/${endpoint}`, form)
      setMessage({ text: `${noun} با موفقیت ${editingId ? 'ویرایش' : 'اضافه'} شد.`, type: 'success' })
      setShowForm(false)
      load()
    } catch (e) {
      setMessage({ text: apiErrorMessage(e), type: 'danger' })
    } finally {
      setSaving(false)
    }
  }
  async function remove(id) {
    try {
      const { data } = await api.delete(`/admin/${endpoint}/${id}`)
      setMessage({ text: data.message, type: 'success' })
      load()
    } catch (e) {
      setMessage({ text: apiErrorMessage(e), type: 'danger' })
    }
  }

  return <><Alert message={message.text} type={message.type} onClose={() => setMessage({ text: '', type: 'danger' })} /><PageHeading title={title} action={<button className="btn btn-primary" onClick={startCreate}><i className="bi bi-plus-lg ms-2" />افزودن {noun}</button>} />{showForm && <section className="form-card mb-4"><div className="d-flex justify-content-between align-items-center mb-3"><h2 className="h5 mb-0">{editingId ? `ویرایش ${noun}` : `${noun} جدید`}</h2><button className="btn-close" onClick={() => setShowForm(false)} aria-label="بستن" /></div><form onSubmit={submit} className="row g-3"><div className="col-md-5"><label className="form-label">{primaryLabel}</label><input className="form-control" value={form[primaryField]} onChange={(e) => setForm({ ...form, [primaryField]: e.target.value })} required autoFocus /></div><div className="col-md-5"><label className="form-label">{secondaryLabel}</label><input className="form-control" value={form[secondaryField]} onChange={(e) => setForm({ ...form, [secondaryField]: e.target.value })} /></div><div className="col-md-2 d-grid align-self-end"><button className="btn btn-primary" disabled={saving}>{saving ? 'در حال ذخیره...' : 'ذخیره'}</button></div></form></section>}{!items ? <Loading /> : <div className="table-card"><div className="table-responsive"><table className="table align-middle mb-0"><thead><tr><th>{primaryLabel}</th><th>{secondaryLabel}</th><th className="text-end">عملیات</th></tr></thead><tbody>{items.map((item) => <tr key={item.id}><td><strong>{item[primaryField]}</strong></td><td className="text-secondary">{item[secondaryField] || '—'}</td><td className="text-end"><div className="btn-group"><button className="btn btn-sm btn-outline-primary" onClick={() => startEdit(item)}><i className="bi bi-pencil" /> ویرایش</button><ConfirmButton className="btn btn-sm btn-outline-danger" onConfirm={() => remove(item.id)} message={`${noun} «${item[primaryField]}» حذف شود؟`}><i className="bi bi-trash3" /> حذف</ConfirmButton></div></td></tr>)}{items.length === 0 && <tr><td colSpan="3"><EmptyState>موردی ثبت نشده است.</EmptyState></td></tr>}</tbody></table></div></div>}</>
}

export function AdminAuthorsPage() {
  return <SimpleCrudPage endpoint="authors" title="نویسندگان" noun="نویسنده" primaryField="fullName" primaryLabel="نام نویسنده" secondaryField="biography" secondaryLabel="زندگی‌نامه کوتاه" />
}

export function AdminCategoriesPage() {
  return <SimpleCrudPage endpoint="categories" title="دسته‌بندی‌ها" noun="دسته‌بندی" primaryField="title" primaryLabel="عنوان دسته‌بندی" secondaryField="description" secondaryLabel="توضیحات" />
}

const emptyBook = { title: '', isbn: '', publishYear: '', quantity: 0, description: '', coverUrl: '', authorId: '', categoryId: '' }

export function AdminBooksPage() {
  const [data, setData] = useState(null)
  const [form, setForm] = useState(emptyBook)
  const [editingId, setEditingId] = useState(null)
  const [showForm, setShowForm] = useState(false)
  const [message, setMessage] = useState({ text: '', type: 'danger' })
  const [saving, setSaving] = useState(false)

  function load() { api.get('/admin/books').then(({ data: result }) => setData(result)).catch((e) => setMessage({ text: apiErrorMessage(e), type: 'danger' })) }
  useEffect(load, [])

  function create() { setEditingId(null); setForm(emptyBook); setShowForm(true) }
  function edit(book) {
    setEditingId(book.id)
    setForm({ title: book.title, isbn: book.isbn, publishYear: book.publishYear || '', quantity: book.quantity, description: book.description || '', coverUrl: book.coverUrl || '', authorId: book.author.id, categoryId: book.category.id })
    setShowForm(true)
  }
  async function submit(event) {
    event.preventDefault()
    setSaving(true)
    const payload = { ...form, publishYear: form.publishYear ? Number(form.publishYear) : null, quantity: Number(form.quantity), authorId: Number(form.authorId), categoryId: Number(form.categoryId) }
    try {
      if (editingId) await api.put(`/admin/books/${editingId}`, payload)
      else await api.post('/admin/books', payload)
      setMessage({ text: `کتاب با موفقیت ${editingId ? 'ویرایش' : 'اضافه'} شد.`, type: 'success' })
      setShowForm(false)
      load()
    } catch (e) { setMessage({ text: apiErrorMessage(e), type: 'danger' }) } finally { setSaving(false) }
  }
  async function remove(id) {
    try { const { data: result } = await api.delete(`/admin/books/${id}`); setMessage({ text: result.message, type: 'success' }); load() }
    catch (e) { setMessage({ text: apiErrorMessage(e), type: 'danger' }) }
  }

  return <><Alert message={message.text} type={message.type} onClose={() => setMessage({ text: '', type: 'danger' })} /><PageHeading title="کتاب‌ها" action={<button className="btn btn-primary" onClick={create}><i className="bi bi-plus-lg ms-2" />افزودن کتاب</button>} />{showForm && <section className="form-card mb-4"><div className="d-flex justify-content-between mb-3"><h2 className="h5">{editingId ? 'ویرایش کتاب' : 'کتاب جدید'}</h2><button className="btn-close" onClick={() => setShowForm(false)} /></div><form className="row g-3" onSubmit={submit}><div className="col-md-6"><label className="form-label">عنوان</label><input className="form-control" value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} required /></div><div className="col-md-6"><label className="form-label">ISBN</label><input className="form-control" dir="ltr" value={form.isbn} onChange={(e) => setForm({ ...form, isbn: e.target.value })} required /></div><div className="col-md-3"><label className="form-label">سال انتشار</label><input type="number" className="form-control" value={form.publishYear} onChange={(e) => setForm({ ...form, publishYear: e.target.value })} /></div><div className="col-md-3"><label className="form-label">موجودی</label><input type="number" min="0" className="form-control" value={form.quantity} onChange={(e) => setForm({ ...form, quantity: e.target.value })} required /></div><div className="col-md-3"><label className="form-label">نویسنده</label><select className="form-select" value={form.authorId} onChange={(e) => setForm({ ...form, authorId: e.target.value })} required><option value="">انتخاب کنید</option>{data?.authors.map((item) => <option key={item.id} value={item.id}>{item.fullName}</option>)}</select></div><div className="col-md-3"><label className="form-label">دسته‌بندی</label><select className="form-select" value={form.categoryId} onChange={(e) => setForm({ ...form, categoryId: e.target.value })} required><option value="">انتخاب کنید</option>{data?.categories.map((item) => <option key={item.id} value={item.id}>{item.title}</option>)}</select></div><div className="col-12"><label className="form-label">آدرس تصویر جلد</label><input type="url" className="form-control" dir="ltr" value={form.coverUrl} onChange={(e) => setForm({ ...form, coverUrl: e.target.value })} /></div><div className="col-12"><label className="form-label">توضیحات</label><textarea rows="3" className="form-control" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} /></div><div className="col-12 d-flex gap-2"><button className="btn btn-primary" disabled={saving}>{saving ? 'در حال ذخیره...' : 'ذخیره کتاب'}</button><button type="button" className="btn btn-light" onClick={() => setShowForm(false)}>انصراف</button></div></form></section>}{!data ? <Loading /> : <div className="table-card"><div className="table-responsive"><table className="table align-middle mb-0"><thead><tr><th>کتاب</th><th>ISBN</th><th>دسته‌بندی</th><th>موجودی</th><th className="text-end">عملیات</th></tr></thead><tbody>{data.books.map((book) => <tr key={book.id}><td><strong>{book.title}</strong><small className="d-block text-secondary">{book.author.fullName}</small></td><td dir="ltr">{book.isbn}</td><td>{book.category.title}</td><td><span className={`badge ${book.quantity > 0 ? 'text-bg-success' : 'text-bg-danger'}`}>{book.quantity}</span></td><td className="text-end"><div className="btn-group"><button className="btn btn-sm btn-outline-primary" onClick={() => edit(book)}><i className="bi bi-pencil" /> ویرایش</button><ConfirmButton onConfirm={() => remove(book.id)} message={`کتاب «${book.title}» حذف شود؟`}><i className="bi bi-trash3" /> حذف</ConfirmButton></div></td></tr>)}</tbody></table></div></div>}</>
}

export function AdminBorrowsPage() {
  const [data, setData] = useState(null)
  const [form, setForm] = useState({ userId: '', bookId: '', dueDate: today })
  const [showForm, setShowForm] = useState(false)
  const [message, setMessage] = useState({ text: '', type: 'danger' })
  function load() { api.get('/admin/borrows').then(({ data: result }) => setData(result)).catch((e) => setMessage({ text: apiErrorMessage(e), type: 'danger' })) }
  useEffect(load, [])
  async function submit(event) {
    event.preventDefault()
    try { await api.post('/admin/borrows', { userId: Number(form.userId), bookId: Number(form.bookId), dueDate: form.dueDate }); setMessage({ text: 'امانت با موفقیت ثبت شد.', type: 'success' }); setShowForm(false); load() }
    catch (e) { setMessage({ text: apiErrorMessage(e), type: 'danger' }) }
  }
  async function returnBook(id) {
    try { const { data: result } = await api.post(`/admin/borrows/${id}/return`); setMessage({ text: result.message, type: 'success' }); load() }
    catch (e) { setMessage({ text: apiErrorMessage(e), type: 'danger' }) }
  }
  return <><Alert message={message.text} type={message.type} onClose={() => setMessage({ text: '', type: 'danger' })} /><PageHeading title="امانت‌های فعال" action={<button className="btn btn-primary" onClick={() => setShowForm(true)}><i className="bi bi-plus-lg ms-2" />ثبت امانت</button>} />{showForm && <section className="form-card mb-4"><div className="d-flex justify-content-between mb-3"><h2 className="h5">ثبت امانت جدید</h2><button className="btn-close" onClick={() => setShowForm(false)} /></div><form className="row g-3" onSubmit={submit}><div className="col-md-4"><label className="form-label">کاربر</label><select className="form-select" value={form.userId} onChange={(e) => setForm({ ...form, userId: e.target.value })} required><option value="">انتخاب کاربر</option>{data?.users.filter((u) => u.role === 'USER').map((u) => <option value={u.id} key={u.id}>{u.fullName} — {u.email}</option>)}</select></div><div className="col-md-4"><label className="form-label">کتاب</label><select className="form-select" value={form.bookId} onChange={(e) => setForm({ ...form, bookId: e.target.value })} required><option value="">انتخاب کتاب</option>{data?.books.map((b) => <option value={b.id} key={b.id} disabled={b.quantity <= 0}>{b.title} ({b.quantity} جلد)</option>)}</select></div><div className="col-md-2"><label className="form-label">سررسید</label><input type="date" min={today} className="form-control" value={form.dueDate} onChange={(e) => setForm({ ...form, dueDate: e.target.value })} required /></div><div className="col-md-2 d-grid align-self-end"><button className="btn btn-primary">ثبت</button></div></form></section>}{!data ? <Loading /> : <div className="table-card"><div className="table-responsive"><table className="table align-middle mb-0"><thead><tr><th>کتاب</th><th>امانت‌گیرنده</th><th>تاریخ امانت</th><th>سررسید</th><th /></tr></thead><tbody>{data.borrows.map((item) => <tr key={item.id}><td><strong>{item.book.title}</strong></td><td>{item.user.fullName}<small className="d-block text-secondary">{item.user.email}</small></td><td>{faDate(item.borrowDate)}</td><td>{faDate(item.dueDate)}</td><td className="text-end"><ConfirmButton className="btn btn-sm btn-success" message="بازگشت این کتاب ثبت شود؟" onConfirm={() => returnBook(item.id)}><i className="bi bi-check2-circle" /> ثبت بازگشت</ConfirmButton></td></tr>)}{data.borrows.length === 0 && <tr><td colSpan="5"><EmptyState>امانت فعالی وجود ندارد.</EmptyState></td></tr>}</tbody></table></div></div>}</>
}

export function AdminReservationsPage() {
  const [items, setItems] = useState(null)
  const [message, setMessage] = useState({ text: '', type: 'danger' })
  function load() { api.get('/admin/reservations').then(({ data }) => setItems(data)).catch((e) => setMessage({ text: apiErrorMessage(e), type: 'danger' })) }
  useEffect(load, [])
  async function act(id, action) {
    try { const { data } = await api.post(`/admin/reservations/${id}/${action}`); setMessage({ text: data.message, type: 'success' }); load() }
    catch (e) { setMessage({ text: apiErrorMessage(e), type: 'danger' }) }
  }
  return <><Alert message={message.text} type={message.type} onClose={() => setMessage({ text: '', type: 'danger' })} /><PageHeading title="رزروها" />{!items ? <Loading /> : <div className="table-card"><div className="table-responsive"><table className="table align-middle mb-0"><thead><tr><th>کتاب</th><th>کاربر</th><th>تاریخ</th><th>وضعیت</th><th className="text-end">عملیات</th></tr></thead><tbody>{items.map((item) => <tr key={item.id}><td><strong>{item.book.title}</strong><small className="d-block text-secondary">موجودی: {item.book.quantity}</small></td><td>{item.user.fullName}<small className="d-block text-secondary">{item.user.email}</small></td><td>{faDate(item.reservationDate)}</td><td><StatusBadge status={item.status} /></td><td className="text-end">{item.status === 'PENDING' && <div className="btn-group"><ConfirmButton className="btn btn-sm btn-success" message="با تأیید، یک امانت ۱۴ روزه ساخته شود؟" onConfirm={() => act(item.id, 'approve')}><i className="bi bi-check-lg" /> تأیید</ConfirmButton><ConfirmButton onConfirm={() => act(item.id, 'reject')} message="این رزرو رد شود؟"><i className="bi bi-x-lg" /> رد</ConfirmButton></div>}</td></tr>)}{items.length === 0 && <tr><td colSpan="5"><EmptyState>رزروی وجود ندارد.</EmptyState></td></tr>}</tbody></table></div></div>}</>
}
