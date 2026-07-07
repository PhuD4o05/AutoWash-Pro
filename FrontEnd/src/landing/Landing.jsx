import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { css } from '../lib/css'
import Navbar from './Navbar'
import Home from './Home'
import AuthView from './AuthView'
import BookingModal from './BookingModal'
import Toast, { useToast } from '../components/Toast'
import { PKGS, NONE, ADDONS, SERVICES, GALLERY, REVIEWS, BRANCHES } from './landingData'

import { authApi } from '../api/endpoints'

const pkgById = (id) => (id === 'none' ? NONE : PKGS.find((p) => p.id === id) || null)
const addonById = (id) => ADDONS.find((a) => a.id === id) || null

export default function Landing({ user, setUser }) {
  const navigate = useNavigate()
  const [toast, showToast] = useToast()

  const [services] = useState(SERVICES)
  const [packages] = useState(PKGS)
  const [gallery] = useState(GALLERY)
  const [reviews] = useState(REVIEWS)

  const [page, setPage] = useState('home') // home | auth
  const [authTab, setAuthTab] = useState('login')
  const [authName, setAuthName] = useState('')
  const [authPhone, setAuthPhone] = useState('')
  const [authEmail, setAuthEmail] = useState('')
  const [authPassword, setAuthPassword] = useState('')
  const [authError, setAuthError] = useState('')

  const [bookingOpen, setBookingOpen] = useState(false)
  const [bookingStep, setBookingStep] = useState(1)
  const [selectedPkgId, setSelectedPkgId] = useState(null)
  const [selectedAddons, setSelectedAddons] = useState([])
  const [bVehicle, setBVehicle] = useState('Sedan')
  const [bDate, setBDate] = useState('')
  const [bTime, setBTime] = useState('')
  const [bBranch, setBBranch] = useState(BRANCHES[0])
  const [bName, setBName] = useState('')
  const [bPhone, setBPhone] = useState('')
  const [bNote, setBNote] = useState('')
  const [bookingError, setBookingError] = useState('')

  // ---- auth ----
  const goAuth = () => { setPage('auth'); setAuthError('') }   // ← THÊM LẠI
  const fillDemo = (id, pw) => { setAuthPhone(id); setAuthPassword(pw); setAuthError('') }
  const submitAuth = async () => {
  const id = (authPhone || '').trim()
  const pw = authPassword || ''

    if (authTab === 'register') {
      const email = (authEmail || '').trim()
      const phone = (authPhone || '').replace(/\s/g, '')
      if (!authName.trim()) return setAuthError('Vui lòng nhập họ tên.')
      if (!email.includes('@')) return setAuthError('Email không hợp lệ.')
      if (!/^\d{10,11}$/.test(phone)) return setAuthError('Số điện thoại phải 10–11 số.')
      if (pw.length < 6) return setAuthError('Mật khẩu tối thiểu 6 ký tự.')
      try {
        await authApi.register({ fullName: authName.trim(), email, phone, password: pw })
        showToast('Tạo tài khoản thành công! Mời đăng nhập.')
        setAuthTab('login'); setAuthError(''); setAuthPassword('')
      } catch (err) {
        setAuthError(err.response?.data?.message || 'Đăng ký thất bại.')
      }
      return
    }

  // Đăng nhập
  try {
    const data = await authApi.login(id, pw)
    localStorage.setItem('accessToken', data.accessToken)
    localStorage.setItem('refreshToken', data.refreshToken)
    localStorage.setItem('fullName', data.fullName)
    localStorage.setItem('role', data.role)

    const roleMap = {
      ADMIN:        '/admin',
      RECEPTIONIST: '/reception',
      WASHER:       '/washer',
      CUSTOMER:     '/customer',
    }
    setUser({ name: data.fullName, role: data.role.toLowerCase() })
    showToast('Đăng nhập thành công!')

    const dest = roleMap[data.role]
    if (dest) {
      setTimeout(() => navigate(dest), 600)
    } else {
      setPage('home')
    }
  } catch (err) {
    setAuthError(err.response?.data?.message || 'Sai tài khoản hoặc mật khẩu.')
  }
}
  const logout = () => { setUser(null); showToast('Đã đăng xuất.') }

  // ---- booking ----
  const openBookingFresh = () => {
    if (!user) {                                   // chưa đăng nhập
      showToast('Vui lòng đăng nhập để đặt lịch.')
      setAuthTab('login'); goAuth()                // đẩy sang trang đăng nhập
      return
    }
    setBookingOpen(true); setBookingStep(1); setSelectedPkgId(null); setSelectedAddons([]); setBookingError('')
    setBName(user.name || ''); setBPhone(user.phone || '')
  }
  const openBookingWith = (id) => {
    if (!user) {
      showToast('Vui lòng đăng nhập để đặt lịch.')
      setAuthTab('login'); goAuth()
      return
    }
    setBookingOpen(true); setBookingStep(2); setSelectedPkgId(id); setSelectedAddons([]); setBookingError('')
    setBName(user.name || ''); setBPhone(user.phone || '')
  }
  const pickPackage = (id) => { setSelectedPkgId(id); setSelectedAddons([]); setBookingStep(2); setBookingError('') }

  const toggleAddon = (id) => {
    const combo = pkgById(selectedPkgId)
    if (combo && combo.includes.indexOf(id) !== -1) {
      showToast('“' + addonById(id).name + '” đã có sẵn trong ' + combo.name + ' — bạn không cần thêm.')
      return
    }
    setSelectedAddons((cur) => (cur.indexOf(id) !== -1 ? cur.filter((x) => x !== id) : cur.concat(id)))
    setBookingError('')
  }

  const nextStep = () => {
    if (bookingStep === 1) {
      if (!selectedPkgId) return setBookingError('Vui lòng chọn một gói combo (hoặc “Gói lẻ tự chọn”).')
      return (setBookingStep(2), setBookingError(''))
    }
    if (bookingStep === 2) {
      if (selectedPkgId === 'none' && selectedAddons.length === 0) return setBookingError('Vui lòng chọn ít nhất một dịch vụ lẻ.')
      return (setBookingStep(3), setBookingError(''))
    }
    if (bookingStep === 3) {
      if (!bDate || !bTime) return setBookingError('Vui lòng chọn ngày và khung giờ.')
      return (setBookingStep(4), setBookingError(''))
    }
    if (bookingStep === 4) {
      const digits = (bPhone || '').replace(/\s/g, '')
      if (!bName.trim() || digits.length < 9) return setBookingError('Vui lòng nhập họ tên và số điện thoại hợp lệ.')
      return (setBookingStep(5), setBookingError(''))
    }
    setBookingOpen(false)
    showToast('Lịch hẹn đã được ghi nhận. Hẹn gặp bạn!')
  }
  const prevStep = () => {
    if (bookingStep === 1 || bookingStep === 5) return setBookingOpen(false)
    setBookingStep((s) => s - 1); setBookingError('')
  }

  return (
    <div id="top" style={css('background:#0f0e0c;color:#f4f1ea;min-height:100vh;overflow-x:hidden;')}>
      <Navbar user={user} onGoAuth={goAuth} onOpenBooking={openBookingFresh} onLogout={logout} />

      {page === 'home' && (
        <Home
          services={services} packages={packages} gallery={gallery} reviews={reviews}
          onOpenBooking={openBookingFresh} onSelectPackage={openBookingWith}
        />
      )}

      {page === 'auth' && (
        <AuthView
          authTab={authTab} authName={authName} authPhone={authPhone} authEmail={authEmail} authPassword={authPassword} authError={authError}
          onAuthName={(e) => setAuthName(e.target.value)}
          onAuthPhone={(e) => setAuthPhone(e.target.value)}
          onAuthEmail={(e) => setAuthEmail(e.target.value)}
          onAuthPassword={(e) => setAuthPassword(e.target.value)}
          onSetLogin={() => { setAuthTab('login'); setAuthError('') }}
          onSetRegister={() => { setAuthTab('register'); setAuthError('') }}
          onSubmit={submitAuth}
          onFillDemo={fillDemo}
        />
      )}

      {bookingOpen && (
        <BookingModal
          step={bookingStep} selectedPkgId={selectedPkgId} selectedAddons={selectedAddons}
          bVehicle={bVehicle} bDate={bDate} bTime={bTime} bBranch={bBranch} bName={bName} bPhone={bPhone} bNote={bNote} bookingError={bookingError}
          onClose={() => setBookingOpen(false)}
          onPickPackage={pickPackage} onToggleAddon={toggleAddon} onNext={nextStep} onPrev={prevStep}
          onDate={(e) => setBDate(e.target.value)} onBranch={(e) => setBBranch(e.target.value)}
          onName={(e) => setBName(e.target.value)} onPhone={(e) => setBPhone(e.target.value.replace(/[^0-9\s]/g, ''))}
          onNote={(e) => setBNote(e.target.value)} onVehicle={setBVehicle} onTime={setBTime}
        />
      )}

      <Toast message={toast} big />
    </div>
  )
}
