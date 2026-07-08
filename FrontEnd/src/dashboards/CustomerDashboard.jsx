import { useState, useEffect, useRef } from 'react'
import { Link } from 'react-router-dom'
import { css } from '../lib/css'
import { fmtVND } from '../lib/format'
import Toast from '../components/Toast'
import { useLocalStorage } from '../hooks/useLocalStorage'
import { customerApi, bookingApi, vehicleApi, publicApi } from '../api/endpoints'

const PKGS = [
  { id: 'basic', name: 'Gói Cơ Bản', priceN: 150000, desc: 'Rửa & làm sạch cơ bản' },
  { id: 'standard', name: 'Gói Tiêu Chuẩn', priceN: 350000, desc: 'Vệ sinh toàn diện trong & ngoài' },
  { id: 'premium', name: 'Gói Cao Cấp', priceN: 750000, desc: 'Đánh bóng & bảo vệ sơn' },
  { id: 'ceramic', name: 'Phủ Ceramic', priceN: 2500000, desc: 'Bảo vệ sơn 12–36 tháng' },
]
const SLOTS = [{ t: '08:00', full: false }, { t: '09:30', full: true }, { t: '11:00', full: false }, { t: '13:30', full: true }, { t: '15:00', full: false }, { t: '16:30', full: false }, { t: '18:00', full: false }]
const STATUS_FLOW = ['Pending', 'Confirmed', 'Checked-in', 'Waiting', 'Washing', 'Completed']
const STATUS_VI = { Pending: 'Chờ xác nhận', Confirmed: 'Đã xác nhận', 'Checked-in': 'Đã check-in', Waiting: 'Đang chờ', Washing: 'Đang rửa', Completed: 'Hoàn thành', Cancelled: 'Đã huỷ' }
const TIERS = [{ name: 'Member', min: 0 }, { name: 'Silver', min: 1000 }, { name: 'Gold', min: 2000 }, { name: 'Platinum', min: 5000 }]
const TIER_DISCOUNT = { Member: 0, Silver: 0.05, Gold: 0.10, Platinum: 0.15 }
const VOUCHERS = [
  { id: 'v1', name: 'Giảm 50.000đ', desc: 'Cho mọi gói dịch vụ', cost: 500, discount: 50000 },
  { id: 'v2', name: 'Giảm 10%', desc: 'Tối đa 200.000đ', cost: 900, discount: 0.10 },
  { id: 'v3', name: 'Miễn phí dưỡng lốp', desc: 'Kèm gói bất kỳ', cost: 600, discount: 80000 },
  { id: 'v4', name: 'Giảm 150.000đ', desc: 'Gói Cao Cấp trở lên', cost: 1400, discount: 150000 },
]
const CODES = { WASH50: 50000, GOLD10: 0.10, NEW100: 100000 }
const POINT_HISTORY = [
  { date: '10/06/2026', desc: 'Hoàn thành · Gói Tiêu Chuẩn', delta: '+35' },
  { date: '02/06/2026', desc: 'Hoàn thành · Phủ Ceramic', delta: '+250' },
  { date: '25/05/2026', desc: 'Đổi voucher Giảm 50.000đ', delta: '−500' },
  { date: '18/05/2026', desc: 'Hoàn thành · Gói Cao Cấp', delta: '+75' },
]
const inp = 'width:100%;background:#0f0e0c;border:1.5px solid rgba(255,255,255,0.14);border-radius:11px;padding:13px 15px;color:#f4f1ea;font-size:14.5px;'
const fmtD = (d) => { if (!d) return '—'; const p = d.split('-'); return p.length === 3 ? p[2] + '/' + p[1] + '/' + p[0] : d }
const dayType = (s) => { if (!s) return 'weekday'; const d = new Date(s).getDay(); return (d === 0 || d === 6) ? 'weekend' : 'weekday' }
const dayMult = (t) => (t === 'holiday' ? 1.3 : t === 'weekend' ? 1.15 : 1)

const hBadge = (st) => {
  const map = { Pending: ['#e3b341', 'rgba(227,179,65,0.14)'], Confirmed: ['#7fb8e0', 'rgba(127,184,224,0.14)'], 'Checked-in': ['#9b8cf0', 'rgba(155,140,240,0.14)'], Waiting: ['#e0a36a', 'rgba(224,163,106,0.14)'], Washing: ['var(--gold)', 'rgba(200,162,83,0.14)'], Completed: ['#6fcf97', 'rgba(111,207,151,0.14)'], Cancelled: ['#e08a6a', 'rgba(224,138,106,0.14)'] }
  const c = map[st] || ['#a39e92', 'rgba(255,255,255,0.08)']
  return 'display:inline-flex;align-items:center;gap:7px;padding:6px 13px;border-radius:20px;font-size:12.5px;font-weight:600;color:' + c[0] + ';background:' + c[1] + ';'
}

export default function CustomerDashboard({ user }) {
  const [tab, setTab] = useState('overview')
  const [name, setName] = useState(user?.name || '')
  const [phone, setPhone] = useState('')
  const [email, setEmail] = useState('')
  const [tier, setTier] = useState('Member')
  const [points, setPoints] = useState(0)

  useEffect(() => {
    customerApi.me()
        .then((c) => {
          setName(c.fullName || '')
          setPhone(c.phoneNumber || '')
          setEmail(c.email || '')
          setPoints(c.currentPoints ?? 0)
          const t = String(c.membershipTier || 'MEMBER')
          setTier(t.charAt(0) + t.slice(1).toLowerCase())
        })
        .catch(() => {})
  }, [])
  const [pkgs, setPkgs] = useState([])
  useEffect(() => {
    publicApi.services()
        .then((list) => setPkgs(list.map((s) => ({
          id: s.id,
          name: s.name,
          priceN: s.basePrice,
          desc: s.description || '',
        }))))
        .catch(() => {})
  }, [])

  const [pw, setPw] = useState({ open: false, old: '', n1: '', n2: '', error: '' })

  const [cars, setCars] = useState([])

  const loadCars = () => {
    vehicleApi.myVehicles()
        .then((list) => setCars(list.map((v) => ({
          id: v.id,
          plate: v.licensePlate,
          brand: v.brand,
          model: v.model,
          color: v.color,
          washes: 0,
        }))))
        .catch(() => {})
  }
  useEffect(() => { loadCars() }, [])

  const [carModal, setCarModal] = useState(false)
  const [carEditId, setCarEditId] = useState(null)
  const [carForm, setCarForm] = useState({ plate: '', brand: '', model: '', color: '' })
  const [carError, setCarError] = useState('')

  const [bookings, setBookings] = useState([])

  // Lấy booking thật từ BE + map sang shape FE
  useEffect(() => {
    bookingApi.myBookings()
        .then((list) => {
          const ST = { PENDING:'Pending', CONFIRMED:'Confirmed', CHECKED_IN:'Checked-in', WAITING:'Waiting', WASHING:'Washing', COMPLETED:'Completed', CANCELLED:'Cancelled', NO_SHOW:'Cancelled' }
          setBookings(list.map((b) => {
            const dt = b.scheduledTime ? new Date(b.scheduledTime) : null
            const dd = dt ? String(dt.getDate()).padStart(2,'0') + '/' + String(dt.getMonth()+1).padStart(2,'0') + '/' + dt.getFullYear() : '—'
            const tt = dt ? String(dt.getHours()).padStart(2,'0') + ':' + String(dt.getMinutes()).padStart(2,'0') : '—'
            return {
              id: 'BK' + b.id,
              _rawId: b.id,
              car: [b.carBrand, b.carModel].filter(Boolean).join(' ') || '—',
              plate: b.licensePlate || '—',
              pkg: b.packageName || '—',
              price: fmtVND(b.totalPrice || 0),
              date: dd,
              time: tt,
              status: ST[b.status] || 'Pending',
              branch: '—',
              live: b.status === 'WASHING',
            }
          }))
        })
        .catch(() => {})
  }, [])

  const [bk, setBk] = useState({ open: false, step: 1, carId: null, pkgId: null, date: '', time: '', pay: 'payos', voucherInput: '', voucher: null, error: '' })
  const [toast, setToast] = useState('')
  const tt = useRef(null)
  const showToast = (m) => { setToast(m); clearTimeout(tt.current); tt.current = setTimeout(() => setToast(''), 3000) }

  // Poll booking mỗi 15s, báo khi có xe vừa rửa xong (WASHING -> COMPLETED)
  const prevStatuses = useRef({})
  useEffect(() => {
    const check = () => {
      bookingApi.myBookings()
          .then((list) => {
            list.forEach((b) => {
              const before = prevStatuses.current[b.id]
              if (before === 'WASHING' && b.status === 'COMPLETED') {
                showToast('Xe của bạn đã rửa xong — mời nhận xe!')
              }
              prevStatuses.current[b.id] = b.status
            })
            loadBookings()  // cập nhật lại danh sách hiển thị
          })
          .catch(() => {})
    }
    check()                          // chạy ngay 1 lần
    const iv = setInterval(check, 15000)  // rồi lặp mỗi 15s
    return () => clearInterval(iv)
  }, [])

  const carById = (id) => cars.find((c) => c.id === id) || null
  const pkgById = (id) => pkgs.find((p) => p.id === id) || null
  const initial = (name.trim()[0] || 'A').toUpperCase()

  const savePw = () => {
    if (!pw.old || !pw.n1) return setPw({ ...pw, error: 'Vui lòng nhập đầy đủ.' })
    if (pw.n1.length < 6) return setPw({ ...pw, error: 'Mật khẩu mới tối thiểu 6 ký tự.' })
    if (pw.n1 !== pw.n2) return setPw({ ...pw, error: 'Mật khẩu nhập lại không khớp.' })
    customerApi.changePassword(pw.old, pw.n1)
        .then(() => {
          setPw({ open: false, old: '', n1: '', n2: '', error: '' })
          showToast('Đã đổi mật khẩu thành công.')
        })
        .catch((e) => setPw({ ...pw, error: e.response?.data?.message || 'Đổi mật khẩu thất bại.' }))
  }

  const openAddCar = () => { setCarEditId(null); setCarForm({ plate: '', brand: '', model: '', color: '' }); setCarError(''); setCarModal(true) }
  const openEditCar = (id) => { const c = carById(id); setCarEditId(id); setCarForm({ plate: c.plate, brand: c.brand, model: c.model, color: c.color }); setCarError(''); setCarModal(true) }
  const saveCar = () => {
    const f = carForm
    if (!f.plate.trim() || !f.brand.trim() || !f.model.trim()) return setCarError('Vui lòng nhập biển số, hãng và mẫu xe.')
    const payload = { licensePlate: f.plate.trim(), brand: f.brand.trim(), model: f.model.trim(), color: f.color.trim() }
    const req = carEditId ? vehicleApi.update(carEditId, payload) : vehicleApi.add(payload)
    req
        .then(() => { loadCars(); showToast(carEditId ? 'Đã cập nhật thông tin xe.' : 'Đã thêm xe mới.'); setCarModal(false) })
        .catch((e) => setCarError(e.response?.data?.message || 'Lưu xe thất bại.'))
  }
  const deleteCar = (id) => {
    vehicleApi.remove(id)
        .then(() => { loadCars(); showToast('Đã xoá xe.') })
        .catch(() => showToast('Xoá xe thất bại.'))
  }
  const openBooking = () => setBk({ open: true, step: 1, carId: cars[0]?.id || null, pkgId: null, selectedPkgIds: [], date: '', time: '', pay: 'payos', voucherInput: '', voucher: null, error: '' })
  const applyVoucher = () => {
    const code = bk.voucherInput.trim()
    if (CODES[code] !== undefined) { setBk({ ...bk, voucher: { code, value: CODES[code] } }); showToast('Đã áp dụng mã ' + code + '.') }
    else setBk({ ...bk, error: 'Mã giảm giá không hợp lệ.' })
  }

  const confirmBooking = () => {
    if (bk.selectedPkgIds.length === 0) {
      setBk({ ...bk, error: 'Vui lòng chọn ít nhất 1 dịch vụ.' })
      return
    }
    const scheduledTime = bk.date + 'T' + bk.time + ':00'
    bookingApi.create({
      vehicleId: bk.carId,
      packageId: bk.selectedPkgIds[0],
      packageIds: bk.selectedPkgIds,
      scheduledTime,
    })
        .then(() => {
          loadBookings()
          setBk({ ...bk, step: 5 })
        })
        .catch((e) => setBk({ ...bk, error: e.response?.data?.message || 'Đặt lịch thất bại, thử lại.' }))
  }

  const computeTotal = () => {
    if (!bk.selectedPkgIds || bk.selectedPkgIds.length === 0) return { base: 0, total: 0, discountTier: 0, discountVoucher: 0 }
    let baseSum = 0
    bk.selectedPkgIds.forEach((id) => {
      const p = pkgById(id)
      if (p) baseSum += p.priceN
    })
    const base = Math.round(baseSum * dayMult(dayType(bk.date)))
    const tierD = Math.round(base * (TIER_DISCOUNT[tier] || 0))
    let vD = 0; if (bk.voucher) { const v = bk.voucher.value; vD = v < 1 ? Math.round((base - tierD) * v) : v }
    return { base, total: Math.max(0, base - tierD - vD), discountTier: tierD, discountVoucher: vD }
  }
  const loadBookings = () => {
    bookingApi.myBookings()
        .then((list) => {
          const ST = { PENDING:'Pending', CONFIRMED:'Confirmed', CHECKED_IN:'Checked-in', WAITING:'Waiting', WASHING:'Washing', COMPLETED:'Completed', CANCELLED:'Cancelled', NO_SHOW:'Cancelled' }
          setBookings(list.map((b) => {
            const dt = b.scheduledTime ? new Date(b.scheduledTime) : null
            const dd = dt ? String(dt.getDate()).padStart(2,'0') + '/' + String(dt.getMonth()+1).padStart(2,'0') + '/' + dt.getFullYear() : '—'
            const tt = dt ? String(dt.getHours()).padStart(2,'0') + ':' + String(dt.getMinutes()).padStart(2,'0') : '—'
            return {
              id: 'BK' + b.id, _rawId: b.id,
              car: [b.carBrand, b.carModel].filter(Boolean).join(' ') || '—',
              plate: b.licensePlate || '—',
              pkg: b.packageName || '—',
              price: fmtVND(b.totalPrice || 0),
              date: dd, time: tt,
              status: ST[b.status] || 'Pending',
              branch: '—',
              live: b.status === 'WASHING',
            }
          }))
        })
        .catch(() => {})
  }
  useEffect(() => { loadBookings() }, [])
  const bkNext = () => {
    if (bk.step === 1) { if (!bk.carId) return setBk({ ...bk, error: 'Vui lòng chọn xe.' }); return setBk({ ...bk, step: 2, error: '' }) }
    if (bk.step === 2) { if (!bk.selectedPkgIds || bk.selectedPkgIds.length === 0) return setBk({ ...bk, error: 'Vui lòng chọn ít nhất 1 dịch vụ.' }); return setBk({ ...bk, step: 3, error: '' }) }
    if (bk.step === 3) { if (!bk.date || !bk.time) return setBk({ ...bk, error: 'Vui lòng chọn ngày và khung giờ.' }); return setBk({ ...bk, step: 4, error: '' }) }
    if (bk.step === 4) return confirmBooking()
    setBk({ ...bk, open: false }); setTab('history')
  }
  const bkPrev = () => { if (bk.step === 1 || bk.step === 5) return setBk({ ...bk, open: false }); setBk({ ...bk, step: bk.step - 1, error: '' }) }

  // Sửa đồng bộ cả status và st khi huỷ đơn
  const cancelBooking = (id) => {
    const b = bookings.find((x) => x.id === id)
    if (!b) return
    bookingApi.cancel(b._rawId)
        .then(() => {
          setBookings((bs) => bs.map((x) => (x.id === id ? { ...x, status: 'Cancelled', live: false } : x)))
          showToast('Đã huỷ lịch hẹn ' + id + '.')
        })
        .catch(() => showToast('Huỷ thất bại, thử lại.'))
  }
  const rescheduleBooking = (id) => { openBooking(); showToast('Chọn lại thời gian cho ' + id + '.') }

  const redeem = (v) => {
    if (points < v.cost) return showToast('Bạn chưa đủ điểm để đổi ưu đãi này.')
    setPoints((p) => p - v.cost); showToast('Đã đổi: ' + v.name + '.')
  }

  const tierInfo = () => {
    let cur = TIERS[0]; for (let i = 0; i < TIERS.length; i++) if (points >= TIERS[i].min) cur = TIERS[i]
    const next = TIERS.find((t) => t.min > points) || null
    const lo = cur.min, hi = next ? next.min : cur.min
    const pct = next ? Math.min(100, Math.round((points - lo) / (hi - lo) * 100)) : 100
    return { cur, next, pct, toNext: next ? next.min - points : 0 }
  }

  const NAV = [
    { key: 'overview', label: 'Tổng quan', icon: '◈' },
    { key: 'account', label: 'Tài khoản', icon: '◐' },
    { key: 'cars', label: 'Xe của tôi', icon: '⊞' },
    { key: 'booking', label: 'Đặt lịch', icon: '✦' },
    { key: 'history', label: 'Lịch sử & trạng thái', icon: '≡' },
    { key: 'loyalty', label: 'Điểm thưởng', icon: '❖' },
  ]

  const live = bookings.find((b) => b.live) || bookings.find((b) => b.status !== 'Completed' && b.status !== 'Cancelled') || bookings[0] ||  { id:'—', car:'—', plate:'—', status:'Pending' }
  const curIdx = STATUS_FLOW.indexOf(live?.status)
  const liveStatusText = live.status === 'Washing' ? 'Xe đang được rửa — dự kiến hoàn thành trong ~25 phút.'
      : live.status === 'Completed' ? 'Xe đã hoàn thành. Cảm ơn bạn đã sử dụng dịch vụ!'
          : 'Lịch hẹn đang ở trạng thái: ' + STATUS_VI[live.status] + '.'
  const upcoming = bookings.find((b) => b.status === 'Confirmed' || b.status === 'Pending')
  const statCards = [
    { icon: '❖', value: points.toLocaleString('vi-VN'), label: 'Điểm tích luỹ · ' + tier },
    { icon: '⊞', value: cars.length, label: 'Xe đã đăng ký' },
    { icon: '✦', value: upcoming ? upcoming.date : '—', label: 'Lịch hẹn sắp tới' },
    { icon: '≡', value: bookings.filter((b) => b.status === 'Completed').length, label: 'Lượt rửa hoàn thành' },
  ]
  const hour = new Date().getHours()
  const greeting = hour < 11 ? 'Chào buổi sáng' : hour < 14 ? 'Chào buổi trưa' : hour < 18 ? 'Chào buổi chiều' : 'Chào buổi tối'

  const selPkg = (bk.selectedPkgIds && bk.selectedPkgIds.length > 0) ? {
    priceN: bk.selectedPkgIds.reduce((sum, id) => sum + (pkgById(id)?.priceN || 0), 0),
    name: bk.selectedPkgIds.map((id) => pkgById(id)?.name).filter(Boolean).join(', ')
  } : null
  const ct = computeTotal()
  const ti = tierInfo()
  const bkCarObj = carById(bk.carId)
  const bkTitles = { 1: 'Chọn xe của bạn', 2: 'Chọn dịch vụ', 3: 'Chọn ngày & giờ', 4: 'Thanh toán', 5: 'Đặt lịch thành công' }
  const bkNexts = { 1: 'Tiếp tục', 2: 'Tiếp tục', 3: 'Tiếp tục', 4: 'Xác nhận & thanh toán', 5: 'Xem lịch hẹn' }
  const bkBacks = { 1: 'Huỷ', 2: 'Quay lại', 3: 'Quay lại', 4: 'Quay lại', 5: 'Đóng' }
  const selBox = (active) => 'border:1.5px solid ' + (active ? 'var(--gold)' : 'rgba(255,255,255,0.14)') + ';background:' + (active ? 'rgba(200,162,83,0.08)' : 'transparent') + ';border-radius:12px;padding:16px 18px;cursor:pointer;transition:all .15s;display:flex;align-items:center;gap:14px;'
  const matrixCard = (active) => 'flex:1;border:1.5px solid ' + (active ? 'var(--gold)' : 'rgba(255,255,255,0.14)') + ';border-radius:11px;padding:14px 12px;text-align:center;background:' + (active ? 'rgba(200,162,83,0.08)' : 'transparent') + ';'
  const payStyle = (active) => 'flex:1;border:1.5px solid ' + (active ? 'var(--gold)' : 'rgba(255,255,255,0.16)') + ';background:' + (active ? 'rgba(200,162,83,0.08)' : 'transparent') + ';border-radius:12px;padding:16px;cursor:pointer;text-align:left;color:#f4f1ea;transition:all .15s;'

  return (
      <div style={css('background:#0f0e0c;color:#f4f1ea;min-height:100vh;')}>
        <header style={css('position:sticky;top:0;z-index:50;display:flex;align-items:center;justify-content:space-between;padding:15px 32px;background:rgba(15,14,12,0.9);backdrop-filter:blur(14px);border-bottom:1.5px solid rgba(255,255,255,0.12);')}>
          <div style={css('display:flex;align-items:center;gap:26px;')}>
            <Link to="/" style={css('display:flex;align-items:center;gap:12px;text-decoration:none;')}>
              <span style={css('font-family:var(--font-display);font-size:18px;letter-spacing:0.5px;')}>AutoWash Pro</span>
            </Link>
            <Link to="/" className="hov-gold" style={css('font-size:13.5px;color:#8b8578;text-decoration:none;')}>← Về trang chủ</Link>
          </div>
          <div style={css('display:flex;align-items:center;gap:16px;')}>
            <div style={css('display:flex;align-items:center;gap:8px;padding:6px 14px;border:1px solid rgba(200,162,83,0.4);border-radius:30px;')}>
              <span style={css('width:7px;height:7px;border-radius:50%;background:var(--gold);')}></span>
              <span style={css('font-size:12.5px;letter-spacing:1px;color:var(--gold);')}>Hạng {tier}</span>
            </div>
            <div style={css('width:38px;height:38px;border-radius:50%;border:1px solid var(--gold);display:flex;align-items:center;justify-content:center;font-family:var(--font-display);color:var(--gold);')}>{initial}</div>
          </div>
        </header>

        <div className="dash-grid" style={css('display:grid;grid-template-columns:248px 1fr;min-height:calc(100vh - 70px);')}>
          <aside className="dash-aside" style={css('border-right:1.5px solid rgba(255,255,255,0.12);padding:28px 18px;display:flex;flex-direction:column;gap:6px;')}>
            {NAV.map((n) => {
              const active = tab === n.key
              return (
                  <button key={n.key} onClick={() => (n.key === 'booking' ? openBooking() : setTab(n.key))} style={css("display:flex;align-items:center;gap:13px;width:100%;text-align:left;padding:13px 16px;border:none;border-radius:11px;font-size:14.5px;cursor:pointer;transition:all .15s;font-family:var(--font-body);" + (active ? 'background:rgba(200,162,83,0.12);color:var(--gold);font-weight:600;' : 'background:transparent;color:#a39e92;font-weight:500;'))}>
                    <span style={css('font-size:16px;width:20px;text-align:center;color:' + (active ? 'var(--gold)' : '#6f6a5e') + ';')}>{n.icon}</span>
                    <span>{n.label}</span>
                  </button>
              )
            })}
            <Link to="/" className="hov-danger" style={css('margin-top:auto;display:flex;align-items:center;gap:13px;padding:13px 16px;border-radius:11px;text-decoration:none;color:#8b8578;font-size:14.5px;')}>
              <span style={css('font-size:16px;')}>⏻</span><span>Đăng xuất</span>
            </Link>
          </aside>

          <main style={css('padding:38px 44px 70px;max-width:1100px;')}>
            {tab === 'overview' && (
                <div style={css('animation:fadeUp .4s ease both;')}>
                  <div style={css('font-size:12px;letter-spacing:2px;text-transform:uppercase;color:var(--gold);margin-bottom:8px;')}>Bảng điều khiển</div>
                  <h1 style={css('font-family:var(--font-display);font-size:32px;margin:0 0 30px;font-weight:500;')}>{greeting}, {name}</h1>
                  <div className="grid-4" style={css('display:grid;grid-template-columns:repeat(4,1fr);gap:14px;margin-bottom:16px;')}>
                    {statCards.map((st, i) => (
                        <div key={i} style={css('border:1.5px solid rgba(255,255,255,0.16);border-radius:14px;padding:22px 20px;')}>
                          <div style={css('font-size:18px;color:var(--gold);margin-bottom:14px;')}>{st.icon}</div>
                          <div style={css('font-family:var(--font-display);font-size:26px;color:#f4f1ea;')}>{st.value}</div>
                          <div style={css('font-size:12.5px;color:#8b8578;margin-top:4px;')}>{st.label}</div>
                        </div>
                    ))}
                  </div>
                  <div style={css('border:1.5px solid rgba(200,162,83,0.35);border-radius:16px;padding:28px 30px;margin-bottom:16px;background:rgba(200,162,83,0.04);')}>
                    <div style={css('display:flex;align-items:center;justify-content:space-between;margin-bottom:6px;flex-wrap:wrap;gap:10px;')}>
                      <div style={css('display:flex;align-items:center;gap:11px;')}>
                        <span style={css('width:9px;height:9px;border-radius:50%;background:var(--gold);animation:pulse 1.4s ease-in-out infinite;')}></span>
                        <h3 style={css('font-family:var(--font-display);font-size:19px;margin:0;font-weight:500;')}>Theo dõi trực tiếp</h3>
                      </div>
                      <span style={css('font-size:13px;color:#a39e92;')}>{live.id} · {live.car} · {live.plate}</span>
                    </div>
                    <p style={css('font-size:13.5px;color:#a39e92;margin:0 0 26px;')}>{liveStatusText}</p>
                    <div style={css('display:flex;align-items:flex-start;')}>
                      {STATUS_FLOW.map((st, i) => {
                        const done = i < curIdx, current = i === curIdx
                        const dot = 'width:30px;height:30px;border-radius:50%;display:flex;align-items:center;justify-content:center;font-size:13px;z-index:2;transition:all .2s;' + (done ? 'background:var(--gold);color:#100f0c;' : current ? 'background:var(--gold);color:#100f0c;box-shadow:0 0 0 5px rgba(200,162,83,0.2);animation:pulse 1.4s ease-in-out infinite;' : 'background:#1c1a14;border:1.5px solid rgba(255,255,255,0.15);color:#6f6a5e;')
                        const line = i === 0 ? 'display:none;' : 'position:absolute;top:15px;right:50%;width:100%;height:2px;z-index:1;' + (i <= curIdx ? 'background:var(--gold);' : 'background:rgba(255,255,255,0.12);')
                        const label = 'font-size:11.5px;margin-top:9px;text-align:center;line-height:1.3;color:' + (done || current ? '#e9e5db' : '#6f6a5e') + ';' + (current ? 'font-weight:600;' : '')
                        return (
                            <div key={st} style={css('flex:1;display:flex;flex-direction:column;align-items:center;position:relative;')}>
                              <div style={css(line)}></div>
                              <div style={css(dot)}>{done ? '✓' : ''}</div>
                              <div style={css(label)}>{STATUS_VI[st]}</div>
                            </div>
                        )
                      })}
                    </div>
                  </div>
                  <div style={css('display:flex;gap:14px;flex-wrap:wrap;')}>
                    <button onClick={openBooking} className="hov-bright" style={css('background:var(--gold);border:none;color:#100f0c;padding:14px 28px;border-radius:30px;font-size:14.5px;font-weight:600;cursor:pointer;')}>+ Đặt lịch rửa xe</button>
                    <button onClick={() => setTab('cars')} className="hov-border-gold" style={css('background:transparent;border:1.5px solid rgba(255,255,255,0.2);color:#e9e5db;padding:14px 28px;border-radius:30px;font-size:14.5px;font-weight:500;cursor:pointer;')}>Quản lý xe của tôi</button>
                  </div>
                </div>
            )}

            {tab === 'account' && (
                <div style={css('animation:fadeUp .4s ease both;')}>
                  <h1 style={css('font-family:var(--font-display);font-size:30px;margin:0 0 6px;font-weight:500;')}>Quản lý tài khoản</h1>
                  <p style={css('font-size:14.5px;color:#8b8578;margin:0 0 30px;')}>Xem và chỉnh sửa thông tin cá nhân của bạn.</p>
                  <div style={css('border:1.5px solid rgba(255,255,255,0.16);border-radius:16px;padding:32px 34px;margin-bottom:16px;max-width:640px;')}>
                    <div style={css('display:flex;align-items:center;gap:20px;margin-bottom:30px;')}>
                      <div style={css('width:74px;height:74px;border-radius:50%;background:rgba(200,162,83,0.12);border:1.5px solid var(--gold);display:flex;align-items:center;justify-content:center;font-family:var(--font-display);font-size:30px;color:var(--gold);')}>{initial}</div>
                      <div>
                        <div style={css('font-family:var(--font-display);font-size:20px;')}>{name}</div>
                      </div>
                    </div>
                    <div style={css('display:grid;grid-template-columns:1fr 1fr;gap:18px;')}>
                      <div><label style={css('display:block;font-size:13px;color:#a39e92;margin-bottom:8px;')}>Họ và tên</label><input value={name} onChange={(e) => setName(e.target.value)} className="foc-gold" style={css(inp)} /></div>
                      <div><label style={css('display:block;font-size:13px;color:#a39e92;margin-bottom:8px;')}>Số điện thoại</label><input value={phone} onChange={(e) => setPhone(e.target.value)} className="foc-gold" style={css(inp)} /></div>
                      <div style={css('grid-column:span 2;')}><label style={css('display:block;font-size:13px;color:#a39e92;margin-bottom:8px;')}>Email</label><input value={email} onChange={(e) => setEmail(e.target.value)} className="foc-gold" style={css(inp)} /></div>
                    </div>
                    <button onClick={async () => {
                      try {
                        await customerApi.updateMe({ fullName: name, phone, email })
                        showToast('Đã lưu thông tin cá nhân.')
                      } catch {
                        showToast('Lưu thất bại, thử lại.')
                      }
                    }} className="hov-bright" style={css('margin-top:24px;background:var(--gold);border:none;color:#100f0c;padding:13px 30px;border-radius:11px;font-size:14px;font-weight:600;cursor:pointer;')}>Lưu thay đổi</button>
                  </div>
                  <div style={css('border:1.5px solid rgba(255,255,255,0.16);border-radius:16px;padding:26px 34px;max-width:640px;display:flex;align-items:center;justify-content:space-between;gap:16px;flex-wrap:wrap;')}>
                    <div>
                      <div style={css('font-family:var(--font-display);font-size:17px;')}>Mật khẩu</div>
                      <div style={css('font-size:13px;color:#8b8578;margin-top:3px;')}>Đổi mật khẩu định kỳ để bảo mật tài khoản.</div>
                    </div>
                    <button onClick={() => setPw({ open: true, old: '', n1: '', n2: '', error: '' })} className="hov-border-gold" style={css('background:transparent;border:1.5px solid rgba(255,255,255,0.2);color:#e9e5db;padding:12px 24px;border-radius:24px;font-size:13.5px;cursor:pointer;')}>Đổi mật khẩu</button>
                  </div>
                </div>
            )}

            {tab === 'cars' && (
                <div style={css('animation:fadeUp .4s ease both;')}>
                  <div style={css('display:flex;justify-content:space-between;align-items:flex-end;margin-bottom:28px;flex-wrap:wrap;gap:14px;')}>
                    <div>
                      <h1 style={css('font-family:var(--font-display);font-size:30px;margin:0 0 6px;font-weight:500;')}>Xe của tôi</h1>
                      <p style={css('font-size:14.5px;color:#8b8578;margin:0;')}>Thêm, chỉnh sửa và xem lịch sử rửa của từng xe.</p>
                    </div>
                    <button onClick={openAddCar} className="hov-bright" style={css('background:var(--gold);border:none;color:#100f0c;padding:13px 24px;border-radius:30px;font-size:14px;font-weight:600;cursor:pointer;')}>+ Thêm xe</button>
                  </div>
                  <div className="two-col" style={css('display:grid;grid-template-columns:repeat(2,1fr);gap:14px;')}>
                    {cars.map((c) => (
                        <div key={c.id} style={css('border:1.5px solid rgba(255,255,255,0.16);border-radius:14px;padding:22px;')}>
                          <div style={css('display:flex;gap:18px;margin-bottom:20px;')}>
                            <div style={css('flex:1;min-width:0;')}>
                              <div style={css('font-family:var(--font-display);font-size:19px;color:#f4f1ea;')}>{c.plate}</div>
                              <div style={css('font-size:13.5px;color:#a39e92;margin-top:3px;')}>{c.brand} · {c.model}</div>
                              <div style={css('display:flex;gap:8px;margin-top:12px;flex-wrap:wrap;')}>
                                <span style={css('font-size:12px;color:#c4bfb2;padding:4px 11px;border:1px solid rgba(255,255,255,0.14);border-radius:20px;')}>Màu {c.color}</span>
                                <span style={css('font-size:12px;color:var(--gold);padding:4px 11px;border:1px solid rgba(200,162,83,0.3);border-radius:20px;')}>{c.washes} lượt rửa</span>
                              </div>
                            </div>
                          </div>
                          <div style={css('display:flex;gap:9px;border-top:1px solid rgba(255,255,255,0.08);padding-top:16px;')}>
                            <button onClick={() => setTab('history')} className="hov-border-gold" style={css('flex:1;background:transparent;border:1px solid rgba(255,255,255,0.16);color:#e9e5db;padding:9px;border-radius:9px;font-size:13px;cursor:pointer;')}>Lịch sử rửa</button>
                            <button onClick={() => openEditCar(c.id)} className="hov-border-gold" style={css('background:transparent;border:1px solid rgba(255,255,255,0.16);color:#e9e5db;padding:9px 16px;border-radius:9px;font-size:13px;cursor:pointer;')}>Sửa</button>
                            <button onClick={() => deleteCar(c.id)} className="hov-danger-bg" style={css('background:transparent;border:1px solid rgba(224,138,106,0.3);color:#e08a6a;padding:9px 16px;border-radius:9px;font-size:13px;cursor:pointer;')}>Xoá</button>
                          </div>
                        </div>
                    ))}
                  </div>
                </div>
            )}

            {tab === 'history' && (
                <div style={css('animation:fadeUp .4s ease both;')}>
                  <h1 style={css('font-family:var(--font-display);font-size:30px;margin:0 0 6px;font-weight:500;')}>Lịch sử &amp; trạng thái</h1>
                  <p style={css('font-size:14.5px;color:#8b8578;margin:0 0 28px;')}>Theo dõi các lịch hẹn, trạng thái rửa xe và quản lý đặt lịch.</p>
                  <div style={css('display:flex;flex-direction:column;gap:12px;')}>
                    {bookings.map((b) => {
                      const canCancel = b.status === 'Pending' || b.status === 'Confirmed'
                      return (
                          <div key={b.id} style={css('border:1.5px solid rgba(255,255,255,0.16);border-radius:14px;padding:22px 24px;')}>
                            <div style={css('display:flex;justify-content:space-between;align-items:flex-start;gap:16px;flex-wrap:wrap;')}>
                              <div style={css('flex:1;min-width:200px;')}>
                                <div style={css('display:flex;align-items:center;gap:12px;flex-wrap:wrap;')}>
                                  <span style={css('font-family:var(--font-display);font-size:18px;color:#f4f1ea;')}>{b.pkg}</span>
                                  <span style={css(hBadge(b.status))}>
                              {b.live && <span style={css('width:7px;height:7px;border-radius:50%;background:currentColor;animation:pulse 1.4s ease-in-out infinite;')}></span>}
                                    {STATUS_VI[b.status]}
                            </span>
                                </div>
                                <div style={css('font-size:13.5px;color:#a39e92;margin-top:8px;')}>{b.id} · {b.car} · {b.plate}</div>
                                <div style={css('font-size:13.5px;color:#8b8578;margin-top:4px;')}>{b.date} lúc {b.time} · Chi nhánh {b.branch}</div>
                              </div>
                              <div style={css('text-align:right;')}>
                                <div style={css('font-family:var(--font-display);font-size:20px;color:var(--gold);')}>{b.price}</div>
                                {canCancel && (
                                    <div style={css('display:flex;gap:8px;margin-top:12px;justify-content:flex-end;')}>
                                      <button onClick={() => rescheduleBooking(b.id)} className="hov-border-gold" style={css('background:transparent;border:1px solid rgba(255,255,255,0.16);color:#e9e5db;padding:8px 14px;border-radius:8px;font-size:12.5px;cursor:pointer;')}>Đổi lịch</button>
                                      <button onClick={() => cancelBooking(b.id)} className="hov-danger-bg" style={css('background:transparent;border:1px solid rgba(224,138,106,0.3);color:#e08a6a;padding:8px 14px;border-radius:8px;font-size:12.5px;cursor:pointer;')}>Huỷ</button>
                                    </div>
                                )}
                              </div>
                            </div>
                          </div>
                      )
                    })}
                  </div>
                </div>
            )}

            {tab === 'loyalty' && (
                <div style={css('animation:fadeUp .4s ease both;')}>
                  <h1 style={css('font-family:var(--font-display);font-size:30px;margin:0 0 6px;font-weight:500;')}>Điểm thưởng &amp; hạng thành viên</h1>
                  <p style={css('font-size:14.5px;color:#8b8578;margin:0 0 28px;')}>Tích điểm sau mỗi lượt rửa, đổi điểm lấy ưu đãi và lên hạng để nhận chiết khấu cao hơn.</p>
                  <div style={css('border:1.5px solid rgba(200,162,83,0.35);border-radius:16px;padding:30px 34px;margin-bottom:16px;background:rgba(200,162,83,0.04);')}>
                    <div style={css('display:flex;justify-content:space-between;align-items:flex-start;flex-wrap:wrap;gap:16px;')}>
                      <div>
                        <div style={css('font-size:13px;color:#a39e92;margin-bottom:6px;')}>Điểm hiện có</div>
                        <div style={css('font-family:var(--font-display);font-size:42px;color:var(--gold);line-height:1;')}>{points.toLocaleString('vi-VN')}</div>
                      </div>
                      <div style={css('display:flex;align-items:center;gap:8px;padding:8px 18px;border:1px solid var(--gold);border-radius:30px;')}>
                        <span style={css('font-size:15px;color:var(--gold);')}>❖</span>
                        <span style={css('font-family:var(--font-display);font-size:16px;color:#f4f1ea;')}>Hạng {ti.cur.name}</span>
                      </div>
                    </div>
                    {ti.next && (
                        <div style={css('margin-top:24px;')}>
                          <div style={css('display:flex;justify-content:space-between;font-size:13px;color:#a39e92;margin-bottom:9px;')}><span>Tiến độ lên hạng {ti.next.name}</span><span>Còn {ti.toNext.toLocaleString('vi-VN')} điểm</span></div>
                          <div style={css('height:8px;border-radius:5px;background:rgba(255,255,255,0.1);overflow:hidden;')}><div style={css('height:100%;width:' + ti.pct + '%;background:var(--gold);border-radius:5px;transition:width .5s;')}></div></div>
                        </div>
                    )}
                  </div>
                  <div className="two-col" style={css('display:grid;grid-template-columns:1.3fr 1fr;gap:16px;')}>
                    <div>
                      <h3 style={css('font-family:var(--font-display);font-size:19px;margin:0 0 14px;font-weight:500;')}>Đổi điểm lấy voucher</h3>
                      <div style={css('display:flex;flex-direction:column;gap:14px;')}>
                        {VOUCHERS.map((v) => {
                          const canRedeem = points >= v.cost;
                          return (
                            <div key={v.id} style={css('position:relative;display:flex;border:1.5px solid ' + (canRedeem ? 'rgba(200,162,83,0.4)' : 'rgba(255,255,255,0.12)') + ';border-radius:14px;background:#17150f;overflow:hidden;min-height:115px;')}>
                              {/* Circle cutouts for ticket look */}
                              <div style={css('position:absolute;top:50%;left:-8px;transform:translateY(-50%);width:16px;height:16px;border-radius:50%;background:#0f0e0c;border-right:1.5px solid ' + (canRedeem ? 'rgba(200,162,83,0.4)' : 'rgba(255,255,255,0.12)') + ';')}></div>
                              <div style={css('position:absolute;top:50%;right:-8px;transform:translateY(-50%);width:16px;height:16px;border-radius:50%;background:#0f0e0c;border-left:1.5px solid ' + (canRedeem ? 'rgba(200,162,83,0.4)' : 'rgba(255,255,255,0.12)') + ';')}></div>
                              
                              {/* Main voucher info */}
                              <div style={css('flex:1;padding:16px 20px;display:flex;flex-direction:column;justify-content:center;position:relative;')}>
                                {/* Sticker badge */}
                                <div style={css('position:absolute;top:10px;right:10px;background:rgba(200,162,83,0.12);border:1px solid rgba(200,162,83,0.3);color:var(--gold);font-size:10px;font-weight:700;letter-spacing:1px;padding:3px 8px;border-radius:6px;text-transform:uppercase;')}>🎟️ GIFT</div>
                                <div style={css('font-family:var(--font-display);font-size:17px;color:#f4f1ea;font-weight:600;margin-bottom:4px;')}>{v.name}</div>
                                <div style={css('font-size:12.5px;color:#8b8578;line-height:1.4;padding-right:60px;')}>{v.desc}</div>
                              </div>
                              
                              {/* Dashed vertical separator */}
                              <div style={css('width:1px;border-left:2px dashed ' + (canRedeem ? 'rgba(200,162,83,0.4)' : 'rgba(255,255,255,0.15)') + ';margin:10px 0;')}></div>
                              
                              {/* Claim section */}
                              <div style={css('width:130px;padding:16px 18px;display:flex;flex-direction:column;align-items:center;justify-content:center;background:rgba(255,255,255,0.01);flex-shrink:0;')}>
                                <div style={css('font-family:var(--font-display);font-size:13.5px;color:var(--gold);font-weight:600;margin-bottom:8px;text-align:center;line-height:1.2;')}>{v.cost.toLocaleString('vi-VN')}<br /><span style={css('font-size:10px;color:#8b8578;font-family:var(--font-body);')}>điểm</span></div>
                                <button onClick={() => redeem(v)} className={canRedeem ? 'hov-bright' : ''} style={css('width:100%;border:none;border-radius:8px;padding:8px;font-size:12px;font-weight:700;' + (canRedeem ? 'background:var(--gold);color:#100f0c;cursor:pointer;' : 'background:rgba(255,255,255,0.05);color:#6f6a5e;cursor:not-allowed;'))}>
                                  {canRedeem ? 'Đổi ngay' : 'Chưa đủ'}
                                </button>
                              </div>
                            </div>
                          )
                        })}
                      </div>
                    </div>
                    <div>
                      <h3 style={css('font-family:var(--font-display);font-size:19px;margin:0 0 14px;font-weight:500;')}>Lịch sử điểm</h3>
                      <div style={css('border:1.5px solid rgba(255,255,255,0.16);border-radius:13px;padding:8px 22px;')}>
                        {POINT_HISTORY.map((h, i) => (
                            <div key={i} style={css('display:flex;justify-content:space-between;align-items:center;padding:15px 0;border-bottom:1px solid rgba(255,255,255,0.07);')}>
                              <div><div style={css('font-size:13.5px;color:#e9e5db;')}>{h.desc}</div><div style={css('font-size:12px;color:#8b8578;margin-top:2px;')}>{h.date}</div></div>
                              <div style={css('font-family:var(--font-display);font-size:16px;color:var(--gold);')}>{h.delta}</div>
                            </div>
                        ))}
                      </div>
                    </div>
                  </div>
                </div>
            )}
          </main>
        </div>

        {/* CAR MODAL */}
        {carModal && (
            <div onClick={() => setCarModal(false)} style={css('position:fixed;inset:0;z-index:100;background:rgba(15,14,12,0.45);backdrop-filter:blur(8px);display:flex;align-items:center;justify-content:center;padding:24px;')}>
              <div onClick={(e) => e.stopPropagation()} style={css('width:100%;max-width:460px;border:1.5px solid rgba(255,255,255,0.14);border-radius:18px;background:#16140f;padding:30px 32px;animation:fadeUp .35s ease both;')}>
                <h3 style={css('font-family:var(--font-display);font-size:21px;margin:0 0 22px;font-weight:500;')}>{carEditId ? 'Chỉnh sửa thông tin xe' : 'Thêm xe mới'}</h3>
                <div style={css('display:grid;grid-template-columns:1fr 1fr;gap:15px;')}>
                  <div style={css('grid-column:span 2;')}><label style={css('display:block;font-size:13px;color:#a39e92;margin-bottom:7px;')}>Biển số xe</label><input value={carForm.plate} onChange={(e) => setCarForm({ ...carForm, plate: e.target.value })} placeholder="51K-123.45" className="foc-gold" style={css(inp)} /></div>
                  <div><label style={css('display:block;font-size:13px;color:#a39e92;margin-bottom:7px;')}>Hãng xe</label><input value={carForm.brand} onChange={(e) => setCarForm({ ...carForm, brand: e.target.value })} placeholder="Mercedes-Benz" className="foc-gold" style={css(inp)} /></div>
                  <div><label style={css('display:block;font-size:13px;color:#a39e92;margin-bottom:7px;')}>Mẫu xe</label><input value={carForm.model} onChange={(e) => setCarForm({ ...carForm, model: e.target.value })} placeholder="C300 AMG" className="foc-gold" style={css(inp)} /></div>
                  <div><label style={css('display:block;font-size:13px;color:#a39e92;margin-bottom:7px;')}>Màu xe</label><input value={carForm.color} onChange={(e) => setCarForm({ ...carForm, color: e.target.value })} placeholder="Đen" className="foc-gold" style={css(inp)} /></div>
                </div>
                {carError && <div style={css('font-size:13px;color:#e08a6a;margin-top:14px;')}>{carError}</div>}
                <div style={css('display:flex;gap:10px;margin-top:22px;')}>
                  <button onClick={() => setCarModal(false)} style={css('flex:1;background:transparent;border:1.5px solid rgba(255,255,255,0.18);color:#c4bfb2;padding:13px;border-radius:11px;font-size:14px;cursor:pointer;')}>Huỷ</button>
                  <button onClick={saveCar} style={css('flex:1;background:var(--gold);border:none;color:#100f0c;padding:13px;border-radius:11px;font-size:14px;font-weight:600;cursor:pointer;')}>Lưu xe</button>
                </div>
              </div>
            </div>
        )}

        {/* BOOKING MODAL */}
        {bk.open && (
            <div onClick={() => setBk({ ...bk, open: false })} style={css('position:fixed;inset:0;z-index:100;background:rgba(15,14,12,0.45);backdrop-filter:blur(8px);display:flex;align-items:flex-start;justify-content:center;padding:40px 20px;overflow-y:auto;')}>
              <div onClick={(e) => e.stopPropagation()} style={css('width:100%;max-width:660px;border:1.5px solid rgba(255,255,255,0.14);border-radius:20px;background:#16140f;animation:fadeUp .4s ease both;overflow:hidden;')}>
                <div style={css('display:flex;align-items:center;justify-content:space-between;padding:24px 30px;border-bottom:1.5px solid rgba(255,255,255,0.1);')}>
                  <div>
                    <div style={css('font-size:12px;letter-spacing:2px;text-transform:uppercase;color:var(--gold);margin-bottom:4px;')}>Bước {bk.step}/5</div>
                    <h3 style={css('font-family:var(--font-display);font-size:21px;margin:0;font-weight:500;')}>{bkTitles[bk.step]}</h3>
                  </div>
                  <button onClick={() => setBk({ ...bk, open: false })} style={css('width:38px;height:38px;border-radius:50%;background:rgba(255,255,255,0.06);border:1px solid rgba(255,255,255,0.1);color:#c4bfb2;font-size:17px;cursor:pointer;')}>✕</button>
                </div>
                <div style={css('display:flex;gap:6px;padding:18px 30px 0;')}>
                  {[1, 2, 3, 4, 5].map((n) => <div key={n} style={css('flex:1;height:4px;border-radius:3px;background:' + (n <= bk.step ? 'var(--gold)' : 'rgba(255,255,255,0.12)') + ';')}></div>)}
                </div>
                <div style={css('padding:26px 30px 30px;')}>
                  {bk.step === 1 && (
                      <div style={css('display:flex;flex-direction:column;gap:11px;')}>
                        {cars.map((c) => (
                            <div key={c.id} onClick={() => setBk({ ...bk, carId: c.id, error: '' })} style={css(selBox(bk.carId === c.id))}>
                              <div style={css('flex:1;')}><div style={css('font-size:15px;color:#f4f1ea;font-family:var(--font-display);')}>{c.plate}</div><div style={css('font-size:13px;color:#a39e92;margin-top:2px;')}>{c.brand} · {c.model} · {c.color}</div></div>
                            </div>
                        ))}
                      </div>
                  )}
                  {bk.step === 2 && (
                      <div style={css('display:flex;flex-direction:column;gap:11px;')}>
                        {pkgs.map((p) => {
                          const selected = bk.selectedPkgIds.indexOf(p.id) !== -1
                          const toggleService = () => {
                            const newIds = selected 
                              ? bk.selectedPkgIds.filter((x) => x !== p.id)
                              : [...bk.selectedPkgIds, p.id]
                            setBk({ ...bk, selectedPkgIds: newIds, error: '' })
                          }
                          const rowStyle = 'display:flex;align-items:center;gap:14px;padding:15px 18px;border-radius:13px;transition:all .15s;cursor:pointer;' +
                            (selected ? 'border:1.5px solid var(--gold);background:rgba(200,162,83,0.08);'
                              : 'border:1px solid rgba(255,255,255,0.1);background:#0f0e0c;')
                          const checkStyle = 'width:24px;height:24px;border-radius:7px;flex-shrink:0;display:flex;align-items:center;justify-content:center;font-size:14px;font-weight:700;' +
                            (selected ? 'background:linear-gradient(135deg,var(--gold-light),var(--gold));color:#100f0c;border:1px solid var(--gold);'
                              : 'background:transparent;border:1.5px solid rgba(255,255,255,0.25);color:transparent;')
                          return (
                            <div key={p.id} onClick={toggleService} style={css(rowStyle)}>
                              <div style={css(checkStyle)}>{selected ? '✓' : ''}</div>
                              <div style={css('flex:1;')}><div style={css('font-size:15px;color:#f4f1ea;font-weight:500;')}>{p.name}</div><div style={css('font-size:12.5px;color:#a39e92;margin-top:2px;')}>{p.desc}</div></div>
                              <div style={css('font-family:var(--font-display);font-size:16px;color:var(--gold);white-space:nowrap;')}>{fmtVND(p.priceN)}</div>
                            </div>
                          )
                        })}
                      </div>
                  )}
                  {bk.step === 3 && (
                      <div style={css('display:flex;flex-direction:column;gap:22px;')}>
                        <div>
                          <label style={css('display:block;font-size:13px;color:#a39e92;margin-bottom:9px;')}>Chọn ngày</label>
                          <input type="date" value={bk.date} onChange={(e) => setBk({ ...bk, date: e.target.value, error: '' })} className="foc-gold" style={css(inp + 'color-scheme:dark;')} />
                        </div>
                        <div>
                          <div style={css('display:flex;justify-content:space-between;align-items:baseline;margin-bottom:9px;')}>
                            <label style={css('font-size:13px;color:#a39e92;')}>Bảng giá theo ngày</label>
                            <span style={css('font-size:12px;color:#8b8578;')}>Hạng {ti.cur.name} giảm thêm {Math.round((TIER_DISCOUNT[tier] || 0) * 100)}%</span>
                          </div>
                          <div style={css('display:flex;gap:10px;')}>
                            {selPkg ? [
                              { label: 'Ngày thường', val: fmtVND(Math.round(selPkg.priceN)), active: dayType(bk.date) === 'weekday' },
                              { label: 'Cuối tuần', val: fmtVND(Math.round(selPkg.priceN * 1.15)), active: dayType(bk.date) === 'weekend' },
                              { label: 'Lễ / Tết', val: fmtVND(Math.round(selPkg.priceN * 1.3)), active: false },
                            ].map((m) => (
                                <div key={m.label} style={css(matrixCard(m.active))}>
                                  <div style={css('font-size:12px;color:#a39e92;margin-bottom:6px;')}>{m.label}</div>
                                  <div style={css('font-family:var(--font-display);font-size:16px;color:#f4f1ea;')}>{m.val}</div>
                                </div>
                            )) : <div style={css('font-size:13px;color:#6f6a5e;')}>Hãy chọn gói ở bước trước.</div>}
                          </div>
                        </div>
                        <div>
                          <label style={css('display:block;font-size:13px;color:#a39e92;margin-bottom:9px;')}>Khung giờ còn trống</label>
                          <div style={css('display:grid;grid-template-columns:repeat(4,1fr);gap:9px;')}>
                            {SLOTS.map((sl) => (
                                <button key={sl.t} onClick={() => (sl.full ? null : setBk({ ...bk, time: sl.t, error: '' }))} style={css('padding:12px 0;border-radius:10px;font-size:14px;text-align:center;transition:all .15s;' + (sl.full ? 'background:transparent;border:1px dashed rgba(255,255,255,0.12);color:#5a5347;cursor:not-allowed;' : bk.time === sl.t ? 'background:var(--gold);border:1px solid var(--gold);color:#100f0c;font-weight:600;cursor:pointer;' : 'background:transparent;border:1.5px solid rgba(255,255,255,0.16);color:#c4bfb2;cursor:pointer;'))}>{sl.t}</button>
                            ))}
                          </div>
                        </div>
                      </div>
                  )}
                  {bk.step === 4 && (
                      <div style={css('display:flex;flex-direction:column;gap:20px;')}>
                        <div>
                          <label style={css('display:block;font-size:13px;color:#a39e92;margin-bottom:9px;')}>Phương thức thanh toán</label>
                          <div style={css('display:flex;gap:11px;')}>
                            <button onClick={() => setBk({ ...bk, pay: 'payos' })} style={css(payStyle(bk.pay === 'payos'))}><div style={css('font-size:15px;font-family:var(--font-display);')}>PayOS</div><div style={css('font-size:12px;color:#a39e92;margin-top:3px;')}>Chuyển khoản / QR</div></button>
                            <button onClick={() => setBk({ ...bk, pay: 'cash' })} style={css(payStyle(bk.pay === 'cash'))}><div style={css('font-size:15px;font-family:var(--font-display);')}>Tiền mặt</div><div style={css('font-size:12px;color:#a39e92;margin-top:3px;')}>Tại quầy</div></button>
                          </div>
                        </div>
                        <div>
                          <label style={css('display:block;font-size:13px;color:#a39e92;margin-bottom:9px;')}>Mã giảm giá / Voucher</label>
                          <div style={css('display:flex;gap:9px;')}>
                            <input value={bk.voucherInput} onChange={(e) => setBk({ ...bk, voucherInput: e.target.value.toUpperCase() })} placeholder="VD: WASH50, GOLD10" className="foc-gold" style={css('flex:1;background:#0f0e0c;border:1.5px solid rgba(255,255,255,0.14);border-radius:11px;padding:13px 15px;color:#f4f1ea;font-size:14px;')} />
                            <button onClick={applyVoucher} style={css('background:transparent;border:1.5px solid var(--gold);color:var(--gold);padding:0 22px;border-radius:11px;font-size:14px;font-weight:600;cursor:pointer;')}>Áp dụng</button>
                          </div>
                          {bk.voucher && <div style={css('font-size:12.5px;color:#6fcf97;margin-top:8px;')}>✓ Đã áp dụng mã {bk.voucher.code}</div>}
                        </div>
                        <div style={css('border:1.5px solid rgba(255,255,255,0.14);border-radius:13px;padding:20px 22px;display:flex;flex-direction:column;gap:11px;')}>
                          <div style={css('display:flex;justify-content:space-between;font-size:14px;')}><span style={css('color:#8b8578;flex:1;margin-right:15px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;')} title={selPkg ? selPkg.name : ''}>Chi phí ({selPkg ? selPkg.name : '—'})</span><span style={css('color:#f4f1ea;')}>{fmtVND(ct.base)}</span></div>
                          <div style={css('display:flex;justify-content:space-between;font-size:14px;')}><span style={css('color:#8b8578;')}>Chiết khấu hạng {ti.cur.name}</span><span style={css('color:#6fcf97;')}>− {fmtVND(ct.discountTier)}</span></div>
                          {ct.discountVoucher > 0 && <div style={css('display:flex;justify-content:space-between;font-size:14px;')}><span style={css('color:#8b8578;')}>Voucher</span><span style={css('color:#6fcf97;')}>− {fmtVND(ct.discountVoucher)}</span></div>}
                          <div style={css('height:1px;background:rgba(255,255,255,0.1);margin:2px 0;')}></div>
                          <div style={css('display:flex;justify-content:space-between;align-items:baseline;')}><span style={css('color:#c4bfb2;font-size:15px;')}>Tổng thanh toán</span><span style={css('font-family:var(--font-display);font-size:24px;color:var(--gold);')}>{fmtVND(ct.total)}</span></div>
                        </div>
                        {bk.error && <div style={css('font-size:13px;color:#e08a6a;')}>{bk.error}</div>}
                      </div>
                  )}
                  {bk.step === 5 && (
                      <div style={css('text-align:center;')}>
                        <div style={css('width:60px;height:60px;border-radius:50%;background:var(--gold);display:flex;align-items:center;justify-content:center;margin:0 auto 16px;font-size:28px;color:#100f0c;')}>✓</div>
                        <h3 style={css('font-family:var(--font-display);font-size:24px;margin:0 0 6px;font-weight:500;')}>Đặt lịch thành công!</h3>
                        <p style={css('font-size:14px;color:#a39e92;margin:0 0 22px;line-height:1.6;')}>Mã QR check-in đã được gửi đến email <span style={css('color:#f4f1ea;')}>{email}</span></p>
                        <div style={css('display:inline-block;padding:16px;background:#fff;border-radius:14px;margin-bottom:22px;')}>
                          <div style={css('width:128px;height:128px;background-image:linear-gradient(45deg,#000 25%,transparent 25%,transparent 75%,#000 75%),linear-gradient(45deg,#000 25%,transparent 25%,transparent 75%,#000 75%);background-size:16px 16px;background-position:0 0,8px 8px;')}></div>
                        </div>
                        <div style={css('border:1.5px solid rgba(255,255,255,0.14);border-radius:13px;padding:20px 22px;text-align:left;display:flex;flex-direction:column;gap:10px;')}>
                          <div style={css('display:flex;justify-content:space-between;font-size:14px;')}><span style={css('color:#8b8578;')}>Xe</span><span style={css('color:#f4f1ea;text-align:right;')}>{bkCarObj ? (bkCarObj.brand + ' ' + bkCarObj.model + ' · ' + bkCarObj.plate) : '—'}</span></div>
                          <div style={css('display:flex;justify-content:space-between;font-size:14px;')}><span style={css('color:#8b8578;flex-shrink:0;margin-right:15px;')} title={selPkg ? selPkg.name : ''}>Dịch vụ</span><span style={css('color:#f4f1ea;text-align:right;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;max-width:280px;')} title={selPkg ? selPkg.name : ''}>{selPkg ? selPkg.name : '—'}</span></div>
                          <div style={css('display:flex;justify-content:space-between;font-size:14px;')}><span style={css('color:#8b8578;')}>Thời gian</span><span style={css('color:#f4f1ea;')}>{(bk.time || '—')} · {fmtD(bk.date)}</span></div>
                          <div style={css('display:flex;justify-content:space-between;font-size:14px;')}><span style={css('color:#8b8578;')}>Thanh toán</span><span style={css('color:#f4f1ea;text-align:right;')}>{bk.pay === 'payos' ? 'PayOS (chuyển khoản/QR)' : 'Tiền mặt tại quầy'}</span></div>
                          <div style={css('height:1px;background:rgba(255,255,255,0.1);margin:2px 0;')}></div>
                          <div style={css('display:flex;justify-content:space-between;align-items:baseline;')}><span style={css('color:#c4bfb2;font-size:15px;')}>Tổng</span><span style={css('font-family:var(--font-display);font-size:22px;color:var(--gold);')}>{fmtVND(ct.total)}</span></div>
                        </div>
                      </div>
                  )}
                </div>
                <div style={css('display:flex;justify-content:space-between;gap:14px;padding:20px 30px;border-top:1.5px solid rgba(255,255,255,0.1);')}>
                  <button onClick={bkPrev} style={css('background:transparent;border:1.5px solid rgba(255,255,255,0.16);color:#c4bfb2;padding:14px 22px;border-radius:12px;font-size:14px;font-weight:500;cursor:pointer;')}>{bkBacks[bk.step]}</button>
                  <button onClick={bkNext} className="hov-bright" style={css('flex:1;max-width:280px;background:var(--gold);border:none;color:#100f0c;padding:14px;border-radius:12px;font-size:14.5px;font-weight:600;cursor:pointer;')}>{bkNexts[bk.step]}</button>
                </div>
              </div>
            </div>
        )}

        {/* PASSWORD MODAL */}
        {pw.open && (
            <div onClick={() => setPw({ ...pw, open: false })} style={css('position:fixed;inset:0;z-index:100;background:rgba(15,14,12,0.45);backdrop-filter:blur(8px);display:flex;align-items:center;justify-content:center;padding:24px;')}>
              <div onClick={(e) => e.stopPropagation()} style={css('width:100%;max-width:420px;border:1.5px solid rgba(255,255,255,0.14);border-radius:18px;background:#16140f;padding:30px 32px;animation:fadeUp .35s ease both;')}>
                <h3 style={css('font-family:var(--font-display);font-size:21px;margin:0 0 22px;font-weight:500;')}>Đổi mật khẩu</h3>
                <div style={css('display:flex;flex-direction:column;gap:15px;')}>
                  <input value={pw.old} onChange={(e) => setPw({ ...pw, old: e.target.value })} type="password" placeholder="Mật khẩu hiện tại" className="foc-gold" style={css(inp)} />
                  <input value={pw.n1} onChange={(e) => setPw({ ...pw, n1: e.target.value })} type="password" placeholder="Mật khẩu mới" className="foc-gold" style={css(inp)} />
                  <input value={pw.n2} onChange={(e) => setPw({ ...pw, n2: e.target.value })} type="password" placeholder="Nhập lại mật khẩu mới" className="foc-gold" style={css(inp)} />
                  {pw.error && <div style={css('font-size:13px;color:#e08a6a;')}>{pw.error}</div>}
                  <div style={css('display:flex;gap:10px;margin-top:6px;')}>
                    <button onClick={() => setPw({ ...pw, open: false })} style={css('flex:1;background:transparent;border:1.5px solid rgba(255,255,255,0.18);color:#c4bfb2;padding:13px;border-radius:11px;font-size:14px;cursor:pointer;')}>Huỷ</button>
                    <button onClick={savePw} style={css('flex:1;background:var(--gold);border:none;color:#100f0c;padding:13px;border-radius:11px;font-size:14px;font-weight:600;cursor:pointer;')}>Cập nhật</button>
                  </div>
                </div>
              </div>
            </div>
        )}

        <Toast message={toast} />
      </div>
  )
}