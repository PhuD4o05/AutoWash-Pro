import { useState } from 'react'
import { Routes, Route } from 'react-router-dom'
import Landing from './landing/Landing'
import CustomerDashboard from './dashboards/CustomerDashboard'
import ReceptionDashboard from './dashboards/ReceptionDashboard'
import WasherDashboard from './dashboards/WasherDashboard'
import AdminDashboard from './dashboards/AdminDashboard'

export default function App() {
  // user khách hàng đăng nhập ở landing (để hiện avatar + cho vào /customer)
  const [user, setUser] = useState(null)

  return (
    <Routes>
      <Route path="/" element={<Landing user={user} setUser={setUser} />} />
      <Route path="/customer" element={<CustomerDashboard user={user} />} />
      <Route path="/reception" element={<ReceptionDashboard />} />
      <Route path="/washer" element={<WasherDashboard />} />
      <Route path="/admin" element={<AdminDashboard />} />
    </Routes>
  )
}
