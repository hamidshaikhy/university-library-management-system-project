import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error?.response?.status === 401 && !error.config?.url?.includes('/auth/login')) {
      window.location.assign('/login')
    }
    return Promise.reject(error)
  },
)

export function apiErrorMessage(error) {
  const data = error?.response?.data
  if (data?.fieldErrors && Object.keys(data.fieldErrors).length > 0) {
    return Object.values(data.fieldErrors)[0]
  }
  return data?.message || 'ارتباط با سرور برقرار نشد. دوباره تلاش کنید.'
}

export default api
