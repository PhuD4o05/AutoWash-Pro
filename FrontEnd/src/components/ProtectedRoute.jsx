import { Navigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

// Bọc quanh route cần đăng nhập. allow = danh sách role được phép.
// Chưa đăng nhập -> về '/'. Sai role -> về dashboard đúng của họ.
export default function ProtectedRoute({ allow, children }) {
  const { user, ready, roleDest } = useAuth()

  if (!ready) return null // đang kiểm tra token, chưa render vội (tránh nháy)

  if (!user) return <Navigate to="/" replace />

  if (allow && !allow.includes(user.role)) {
    return <Navigate to={roleDest[user.role] || '/'} replace />
  }
  return children
}
