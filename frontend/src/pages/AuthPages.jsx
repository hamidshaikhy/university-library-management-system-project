import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'
import { apiErrorMessage } from '../lib/api.js'
import { Alert } from '../components/UI.jsx'

function AuthShell({ title, subtitle, children, footer }) {
  return (
    <main className="auth-page">
      <section className="auth-visual">
        <div className="auth-brand"><span className="brand-mark"><i className="bi bi-book-half" /></span> کتابخانه دانشگاه</div>
        <div className="auth-quote">
          <span className="eyebrow text-white-50">پروژه مهندسی اینترنت</span>
          <h1>دانش، در دسترس همه</h1>
          <p>سامانه یکپارچه جست‌وجو، رزرو، امانت و مدیریت کتاب‌های دانشگاه.</p>
        </div>
        <div className="auth-circles" aria-hidden="true"><span /><span /><span /></div>
      </section>
      <section className="auth-form-wrap">
        <div className="auth-card">
          <div className="mb-4">
            <span className="eyebrow">حساب کاربری</span>
            <h2>{title}</h2>
            <p className="text-secondary mb-0">{subtitle}</p>
          </div>
          {children}
          <div className="auth-footer">{footer}</div>
        </div>
      </section>
    </main>
  )
}

export function LoginPage() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({ email: '', password: '' })
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  async function submit(event) {
    event.preventDefault()
    setError('')
    setSubmitting(true)
    try {
      const user = await login(form)
      navigate(user.role === 'ADMIN' ? '/admin/dashboard' : '/dashboard', { replace: true })
    } catch (requestError) {
      setError(apiErrorMessage(requestError))
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <AuthShell
      title="ورود به سامانه"
      subtitle="با ایمیل دانشگاهی و رمز عبور وارد شوید."
      footer={<>حساب ندارید؟ <Link to="/register">ثبت‌نام کنید</Link></>}
    >
      <Alert message={error} />
      <form onSubmit={submit} className="vstack gap-3">
        <label className="form-label mb-0">ایمیل
          <div className="input-icon mt-2"><i className="bi bi-envelope" /><input type="email" className="form-control" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} placeholder="name@example.com" required autoFocus /></div>
        </label>
        <label className="form-label mb-0">رمز عبور
          <div className="input-icon mt-2"><i className="bi bi-lock" /><input type="password" className="form-control" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} placeholder="حداقل ۶ کاراکتر" required /></div>
        </label>
        <button className="btn btn-primary btn-lg w-100 mt-2" disabled={submitting}>
          {submitting ? <><span className="spinner-border spinner-border-sm ms-2" />در حال ورود...</> : <><i className="bi bi-box-arrow-in-left ms-2" />ورود به حساب</>}
        </button>
      </form>
      <div className="demo-accounts mt-4">
        <strong><i className="bi bi-info-circle ms-2" />حساب‌های آزمایشی</strong>
        <small>مدیر: admin@library.local / Admin123</small>
        <small>کاربر: user@library.local / User123</small>
      </div>
    </AuthShell>
  )
}

export function RegisterPage() {
  const { register } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({ fullName: '', email: '', password: '' })
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  async function submit(event) {
    event.preventDefault()
    setError('')
    setSubmitting(true)
    try {
      await register(form)
      navigate('/dashboard', { replace: true })
    } catch (requestError) {
      setError(apiErrorMessage(requestError))
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <AuthShell
      title="ساخت حساب جدید"
      subtitle="پس از ثبت‌نام مستقیم وارد داشبورد می‌شوید."
      footer={<>قبلاً ثبت‌نام کرده‌اید؟ <Link to="/login">وارد شوید</Link></>}
    >
      <Alert message={error} />
      <form onSubmit={submit} className="vstack gap-3">
        <label className="form-label mb-0">نام و نام خانوادگی
          <div className="input-icon mt-2"><i className="bi bi-person" /><input className="form-control" value={form.fullName} onChange={(e) => setForm({ ...form, fullName: e.target.value })} required autoFocus /></div>
        </label>
        <label className="form-label mb-0">ایمیل
          <div className="input-icon mt-2"><i className="bi bi-envelope" /><input type="email" className="form-control" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} required /></div>
        </label>
        <label className="form-label mb-0">رمز عبور
          <div className="input-icon mt-2"><i className="bi bi-lock" /><input type="password" minLength="6" className="form-control" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} required /></div>
          <small className="text-secondary">حداقل ۶ کاراکتر</small>
        </label>
        <button className="btn btn-primary btn-lg w-100 mt-2" disabled={submitting}>
          {submitting ? <><span className="spinner-border spinner-border-sm ms-2" />در حال ثبت...</> : <><i className="bi bi-person-plus ms-2" />ثبت‌نام</>}
        </button>
      </form>
    </AuthShell>
  )
}
