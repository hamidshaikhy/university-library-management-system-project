export function Loading({ label = 'در حال دریافت اطلاعات...' }) {
  return (
    <div className="loading-state" role="status">
      <span className="spinner-border text-primary" aria-hidden="true" />
      <span>{label}</span>
    </div>
  )
}

export function Alert({ message, type = 'danger', onClose }) {
  if (!message) return null
  return (
    <div className={`alert alert-${type} alert-dismissible shadow-sm`} role="alert">
      <i className={`bi ${type === 'success' ? 'bi-check-circle-fill' : 'bi-exclamation-triangle-fill'} ms-2`} />
      {message}
      {onClose && <button type="button" className="btn-close" onClick={onClose} aria-label="بستن" />}
    </div>
  )
}

export function EmptyState({ icon = 'bi-inbox', children }) {
  return (
    <div className="empty-state">
      <i className={`bi ${icon}`} />
      <p>{children}</p>
    </div>
  )
}

export function BookCover({ book, large = false }) {
  return (
    <div className={`book-cover ${large ? 'book-cover-large' : ''}`}>
      {book.coverUrl ? <img src={book.coverUrl} alt={`جلد ${book.title}`} /> : <i className="bi bi-book" />}
      <span className={`availability ${book.quantity > 0 ? 'available' : 'unavailable'}`}>
        {book.quantity > 0 ? 'موجود' : 'ناموجود'}
      </span>
    </div>
  )
}

export function StatusBadge({ status }) {
  const labels = {
    PENDING: 'در انتظار',
    APPROVED: 'تأییدشده',
    REJECTED: 'ردشده',
    CANCELED: 'لغوشده',
  }
  return <span className={`status-badge ${status.toLowerCase()}`}>{labels[status] || status}</span>
}

export function ConfirmButton({ onConfirm, children, className = 'btn btn-sm btn-outline-danger', message }) {
  return (
    <button
      type="button"
      className={className}
      onClick={() => {
        if (window.confirm(message || 'آیا از انجام این عملیات مطمئن هستید؟')) onConfirm()
      }}
    >
      {children}
    </button>
  )
}
