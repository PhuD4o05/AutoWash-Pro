import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: { 'Content-Type': 'application/json' },
})

// Tự động đính kèm JWT token vào mỗi request
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

// Tự động refresh token khi hết hạn (401)
api.interceptors.response.use(
  (res) => res,
  async (err) => {
    if (err.response?.status === 401) {
      const refreshToken = localStorage.getItem('refreshToken')
      if (refreshToken) {
        try {
          const res = await axios.post(
            'http://localhost:8080/api/auth/refresh',
            null,
            { params: { refreshToken } }
          )
          localStorage.setItem('accessToken', res.data.accessToken)
          err.config.headers.Authorization = `Bearer ${res.data.accessToken}`
          return axios(err.config)
        } catch {
          localStorage.clear()
          window.location.href = '/'
        }
      }
    }
    return Promise.reject(err)
  }
)

export default api