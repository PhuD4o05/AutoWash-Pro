import { useState, useRef, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { css } from '../lib/css'
import { fmt } from '../lib/format'
import Toast from '../components/Toast'
import { adminApi } from '../api/endpoints'

const roleVi = { customer: 'Khách hàng', receptionist: 'Tiếp tân', washer: 'Nhân viên rửa', admin: 'Quản trị' }
const roleColor = { customer: '#7fb8e0', receptionist: '#c8a253', washer: '#9b8cf0', admin: '#e08a6a' }
const bBadge = (st) => {
  const map = { Confirmed: ['#7fb8e0', 'rgba(127,184,224,0.14)'], Washing: ['var(--gold)', 'rgba(200,162,83,0.14)'], Drying: ['#7fb8e0', 'rgba(127,184,224,0.14)'], Waiting: ['#e0a36a', 'rgba(224,163,106,0.14)'], 'Checked-in': ['#9b8cf0', 'rgba(155,140,240,0.14)'], Completed: ['#6fcf97', 'rgba(111,207,151,0.14)'], Cancelled: ['#e08a6a', 'rgba(224,138,106,0.14)'], Pending: ['#e3b341', 'rgba(227,179,65,0.14)'] }
  const c = map[st] || ['#a39e92', 'rgba(255,255,255,0.08)']
  return 'padding:5px 12px;border-radius:20px;font-size:12px;font-weight:600;color:' + c[0] + ';background:' + c[1] + ';white-space:nowrap;justify-self:start;'
}
const stv = { Confirmed: 'Đã xác nhận', Washing: 'Đang rửa', Drying: 'Đang sấy', Waiting: 'Đang chờ', 'Checked-in': 'Đã nhận xe', Completed: 'Hoàn thành', Cancelled: 'Đã huỷ', Pending: 'Chờ xác nhận' }
const shiftVi = { MORNING: 'Ca sáng', AFTERNOON: 'Ca chiều', EVENING: 'Ca tối' }
const bayVi = { AVAILABLE: 'Trống', OCCUPIED: 'Đang bận', MAINTENANCE: 'Bảo trì' }
const inp = 'width:100%;background:#0f0e0c;border:1.5px solid rgba(255,255,255,0.14);border-radius:11px;padding:13px 15px;color:#f4f1ea;font-size:14.5px;'

// ---- style dropdown header ----
const hBtn = (active) => 'display:inline-flex;align-items:center;gap:5px;background:transparent;border:none;cursor:pointer;font-size:12px;color:' + (active ? 'var(--gold)' : '#8b8578') + ';letter-spacing:0.5px;text-transform:uppercase;font-family:var(--font-body);padding:0;'
const hMenu = 'position:absolute;top:calc(100% + 8px);left:0;z-index:60;min-width:170px;max-height:320px;overflow:auto;background:#16140f;border:1.5px solid rgba(255,255,255,0.16);border-radius:12px;padding:6px;box-shadow:0 14px 34px rgba(0,0,0,0.55);'
const hItem = (active) => 'display:block;width:100%;text-align:left;background:' + (active ? 'rgba(200,162,83,0.12)' : 'transparent') + ';border:none;border-radius:8px;padding:9px 12px;font-size:13px;color:' + (active ? 'var(--gold)' : '#c4bfb2') + ';cursor:pointer;font-family:var(--font-body);'
const dInp = 'width:100%;background:#0f0e0c;border:1.5px solid rgba(255,255,255,0.14);border-radius:8px;padding:8px 10px;color:#f4f1ea;font-size:13px;color-scheme:dark;'
// ---- select (báo cáo) + rút gọn số tiền ----
const selStyle = 'background:#0f0e0c;border:1.5px solid rgba(255,255,255,0.14);border-radius:10px;padding:9px 14px;color:#f4f1ea;font-size:13.5px;cursor:pointer;font-family:var(--font-body);'
const compact = (n) => { n = n || 0; if (n >= 1e6) { const v = n / 1e6; return (Number.isInteger(v) ? String(v) : v.toFixed(1)).replace('.', ',') + 'tr' } if (n >= 1e3) return Math.round(n / 1e3) + 'k'; return String(n) }

export default function AdminDashboard() {
  const [tab, setTab] = useState('reports')
  const [search, setSearch] = useState('')
  const [nameSort, setNameSort] = useState(null)          // 'az' | 'za' | null
  const [roleFilter, setRoleFilter] = useState('all')
  const [statusFilter, setStatusFilter] = useState('all')
  const [openMenu, setOpenMenu] = useState(null)
  const [selYear, setSelYear] = useState(new Date().getFullYear())
  const [selMonth, setSelMonth] = useState(new Date().getMonth())
  const [svcSearch, setSvcSearch] = useState('')
  // ---- filter/sort tab bookings ----
  const [bkNameSort, setBkNameSort] = useState(null)      // 'az' | 'za' | null
  const [bkPkgFilter, setBkPkgFilter] = useState('all')
  const [bkTimeSort, setBkTimeSort] = useState('new')     // 'new' | 'old'
  const [bkFrom, setBkFrom] = useState('')
  const [bkTo, setBkTo] = useState('')
  const [bkStatusFilter, setBkStatusFilter] = useState('all')
  const [bkSearch, setBkSearch] = useState('')
  const [toast, setToast] = useState('')
  const tt = useRef(null)
  const showToast = (m) => { setToast(m); clearTimeout(tt.current); tt.current = setTimeout(() => setToast(''), 3000) }
  const ST = { PENDING: 'Pending', CONFIRMED: 'Confirmed', CHECKED_IN: 'Checked-in', WAITING: 'Waiting', WASHING: 'Washing', DRYING: 'Drying', COMPLETED: 'Completed', CANCELLED: 'Cancelled', NO_SHOW: 'Cancelled' }


  const [stats, setStats] = useState({ revenue: 0, washes: 0, customers: 0, cancelRate: 0 })
  const [accounts, setAccounts] = useState([])
  const [services, setServices] = useState([])
  const [bookings, setBookings] = useState([])
  const [promos, setPromos] = useState([])
  const [shifts, setShifts] = useState([])
  const [bays, setBays] = useState([])

  const loadAccounts = () => adminApi.users().then((list) => setAccounts(list.map((u) => ({
    id: u.id, name: u.fullName, phone: u.phoneNumber, email: u.email || '',
    role: (u.role || 'CUSTOMER').toLowerCase(), active: u.isActive !== false,
  })))).catch(() => {})
  const loadServices = () => adminApi.packages().then((list) => setServices(list.map((s) => ({
    id: s.id, n: s.name, d: s.description || '', w: s.basePrice || 0,
    _min: s.estimatedMinutes || 30, _vtype: s.vehicleType || 'CAR', _active: s.isActive !== false,
  })))).catch(() => {})
  const loadBookings = () => adminApi.bookings().then((list) => setBookings(list.map((b) => {
    const d = b.scheduledTime ? new Date(b.scheduledTime) : null
    const when = d ? (String(d.getDate()).padStart(2, '0') + '/' + String(d.getMonth() + 1).padStart(2, '0') + ' · ' + String(d.getHours()).padStart(2, '0') + ':' + String(d.getMinutes()).padStart(2, '0')) : '—'
    return { id: 'BK' + b.id, name: b.customerName || '—', pkg: b.packageName || '—', priceN: b.totalPrice || 0, when, ts: d ? d.getTime() : 0, y: d ? d.getFullYear() : null, m: d ? d.getMonth() : null, st: ST[b.status] || 'Pending' }
  }))).catch(() => {})
  const loadPromos = () => adminApi.promotions().then((list) => setPromos(list.map((p) => ({
    id: p.id, code: p.code || p.name, desc: p.description || '',
    pct: p.discountPercent, amount: p.discountAmount,
    expiry: p.endDate ? new Date(p.endDate).toLocaleDateString('vi-VN') : '—',
    active: p.isActive !== false,
  })))).catch(() => {})
  const loadOps = () => {
    adminApi.shifts().then((list) => setShifts(list.map((s) => ({
      id: s.id, type: shiftVi[s.shiftType] || s.shiftType,
      date: s.date, time: (s.startTime || '') + '–' + (s.endTime || ''),
    })))).catch(() => {})
    adminApi.washBays().then((list) => setBays(list.map((b) => ({
      name: b.bayNumber, status: bayVi[b.status] || b.status, busy: b.status === 'OCCUPIED',
    })))).catch(() => {})
  }
  useEffect(() => {
    adminApi.stats().then(setStats).catch(() => {})
    void loadAccounts(); void loadServices(); void loadBookings(); void loadPromos(); void loadOps()
  }, [])

  // đóng dropdown header khi click ra ngoài
  useEffect(() => {
    const close = () => setOpenMenu(null)
    document.addEventListener('click', close)
    return () => document.removeEventListener('click', close)
  }, [])


  const kpis = [
    { label: 'Doanh thu (đã thu)', value: fmt(stats.revenue) + 'đ', delta: 'Tổng tiền đã thanh toán', deltaColor: '#8b8578' },
    { label: 'Lượt rửa hoàn thành', value: String(stats.washes), delta: 'Đơn COMPLETED', deltaColor: '#8b8578' },
    { label: 'Khách hàng', value: String(stats.customers), delta: 'Tài khoản khách', deltaColor: '#8b8578' },
    { label: 'Tỷ lệ huỷ', value: stats.cancelRate + '%', delta: 'Huỷ / No-show', deltaColor: '#8b8578' },
  ]
  const svcAgg = (() => {
    const m = {}
    bookings.filter((b) => b.st === 'Completed').forEach((b) => { m[b.pkg] = (m[b.pkg] || 0) + (b.priceN || 0) })
    return Object.entries(m).map(([n, a]) => ({ n, a })).sort((x, y) => y.a - x.a).slice(0, 6)
  })()
  const smax = Math.max(1, ...svcAgg.map((x) => x.a))
  // tổng tiền đã thu theo từng gói (đơn hoàn thành)
  const svcPaid = (() => {
    const m = {}
    bookings.filter((b) => b.st === 'Completed').forEach((b) => { m[b.pkg] = (m[b.pkg] || 0) + (b.priceN || 0) })
    return m
  })()
// lọc bảng giá theo search
  const serviceRows = services.filter((p) => {
    const q = svcSearch.trim().toLowerCase()
    return !q || p.n.toLowerCase().includes(q) || (p.d || '').toLowerCase().includes(q)
  })

  const years = (() => {
    const s = new Set(bookings.filter((b) => b.st === 'Completed' && b.y).map((b) => b.y))
    s.add(new Date().getFullYear())
    return [...s].sort((a, b) => b - a)
  })()
  const monthly = (() => {
    const arr = Array(12).fill(0).map(() => ({ rev: 0, cnt: 0 }))
    bookings.filter((b) => b.st === 'Completed' && b.y === selYear && b.m != null).forEach((b) => { arr[b.m].rev += (b.priceN || 0); arr[b.m].cnt += 1 })
    return arr
  })()
  const mmax = Math.max(1, ...monthly.map((x) => x.rev))
  const cur = monthly[selMonth] || { rev: 0, cnt: 0 }

  const activity = bookings.slice(0, 6).map((b) => ({ text: b.id + ' · ' + b.name + ' · ' + b.pkg, tag: stv[b.st] }))

  // ---- danh sách gói + lọc/sort tab bookings ----
  const pkgList = (() => {
    const s = new Set(services.map((sv) => sv.n).filter(Boolean))
    bookings.forEach((b) => { if (b.pkg && b.pkg !== '—') s.add(b.pkg) })
    return [...s].sort((a, b) => a.localeCompare(b, 'vi'))
  })()
  const bookingRows = bookings
      .filter((b) => {
        const okPkg = bkPkgFilter === 'all' || b.pkg === bkPkgFilter
        const q = bkSearch.trim().toLowerCase()
        const okSearch = !q || b.id.toLowerCase().includes(q) || b.name.toLowerCase().includes(q) || (b.pkg || '').toLowerCase().includes(q)
        const okStatus = bkStatusFilter === 'all' || b.st === bkStatusFilter
        let okTime = true
        if (bkFrom) okTime = okTime && b.ts >= new Date(bkFrom + 'T00:00:00').getTime()
        if (bkTo) okTime = okTime && b.ts <= new Date(bkTo + 'T23:59:59').getTime()
        return okSearch && okPkg && okStatus && okTime
      })
      .sort((x, y) => {
        if (bkNameSort === 'az') return x.name.localeCompare(y.name, 'vi')
        if (bkNameSort === 'za') return y.name.localeCompare(x.name, 'vi')
        if (bkTimeSort === 'old') return x.ts - y.ts
        return y.ts - x.ts
      })

  // ---- MODAL sửa giá ----
  const [priceModalOpen, setPriceModalOpen] = useState(false)
  const [selectedService, setSelectedService] = useState(null)
  const [priceForm, setPriceForm] = useState({ w: 0 })
  const handleEditPrice = (svc) => { setSelectedService(svc); setPriceForm({ w: svc.w }); setPriceModalOpen(true) }
  const savePriceEdit = () => {
    if (!priceForm.w || isNaN(priceForm.w) || Number(priceForm.w) <= 0) { alert('Vui lòng nhập số tiền hợp lệ!'); return }
    const s = selectedService
    adminApi.updatePackage(s.id, { name: s.n, description: s.d, basePrice: Number(priceForm.w), estimatedMinutes: s._min, vehicleType: s._vtype, isActive: s._active })
        .then(() => { void loadServices(); setPriceModalOpen(false); showToast('Đã cập nhật giá gói "' + s.n + '"') })
        .catch(() => showToast('Cập nhật giá thất bại.'))
  }

  // ---- MODAL voucher (promotion) ----
  const [promoModalOpen, setPromoModalOpen] = useState(false)
  const [promoForm, setPromoForm] = useState({ code: '', value: '', desc: '', expiry: '' })
  const handleCreateVoucher = () => {
    if (!promoForm.code.trim() || !promoForm.value) { alert('Điền mã và giá trị (%) voucher!'); return }
    if (!promoForm.expiry) { alert('Chọn hạn sử dụng!'); return }
    adminApi.createPromotion({
      code: promoForm.code.trim().toUpperCase(),
      name: promoForm.code.trim().toUpperCase(),
      description: promoForm.desc.trim() || ('Giảm ' + promoForm.value + '%'),
      discountPercent: Number(promoForm.value),
      startDate: new Date().toISOString().slice(0, 19),
      endDate: promoForm.expiry + 'T23:59:59',
    })
        .then(() => { void loadPromos(); setPromoForm({ code: '', value: '', desc: '', expiry: '' }); setPromoModalOpen(false); showToast('Đã tạo voucher.') })
        .catch((e) => showToast(e.response?.data?.message || 'Tạo voucher thất bại.'))
  }
  const deletePromo = (id) => adminApi.deletePromotion(id).then(() => { void loadPromos(); showToast('Đã xoá voucher.') }).catch(() => showToast('Xoá thất bại.'))

  // ---- MODAL tài khoản ----
  const [editOpen, setEditOpen] = useState(false)
  const [editId, setEditId] = useState(null)
  const [editIsNew, setEditIsNew] = useState(false)
  const [editForm, setEditForm] = useState({ name: '', phone: '', email: '', password: '', role: 'customer' })
  const [editError, setEditError] = useState('')
  const addAccount = () => { setEditIsNew(true); setEditId(null); setEditForm({ name: '', phone: '', email: '', password: '', role: 'customer' }); setEditError(''); setEditOpen(true) }
  const editAccount = (id) => { const a = accounts.find((x) => x.id === id); setEditIsNew(false); setEditId(id); setEditForm({ name: a.name, phone: a.phone, email: a.email || '', password: '', role: a.role }); setEditError(''); setEditOpen(true) }
  const saveEdit = () => {
    const f = editForm
    if (!f.name.trim()) return setEditError('Nhập họ tên.')
    if (f.phone.replace(/\D/g, '').length < 9) return setEditError('SĐT không hợp lệ.')
    if (editIsNew) {
      if ((f.password || '').length < 6) return setEditError('Mật khẩu ≥ 6 ký tự.')
      const p = { fullName: f.name.trim(), phone: f.phone.replace(/\s/g, ''), email: f.email.trim(), password: f.password }
      const req = f.role === 'washer' ? adminApi.createWasher(p) : f.role === 'receptionist' ? adminApi.createReceptionist(p) : adminApi.createCustomer(p)
      req.then(() => { void loadAccounts(); setEditOpen(false); showToast('Đã tạo tài khoản.') }).catch((e) => setEditError(e.response?.data?.message || 'Tạo thất bại.'))
    } else {
      adminApi.updateUser(editId, { fullName: f.name.trim(), phone: f.phone.replace(/\s/g, ''), email: f.email.trim(), role: f.role.toUpperCase() })
          .then(() => { void loadAccounts(); setEditOpen(false); showToast('Đã cập nhật tài khoản.') }).catch(() => setEditError('Cập nhật thất bại.'))
    }
  }
  const toggleAccount = (id) => {
    const a = accounts.find((x) => x.id === id)
    adminApi.toggleUser(id, a.role.toUpperCase(), !a.active)
        .then(() => { void loadAccounts(); showToast(a.active ? 'Đã vô hiệu hoá ' + a.name : 'Đã kích hoạt ' + a.name) })
        .catch(() => showToast('Thao tác thất bại.'))
  }

  const NAV = [
    { key: 'reports', label: 'Báo cáo & doanh thu', icon: '◫' },
    { key: 'accounts', label: 'Tài khoản', icon: '◐' },
    { key: 'pricing', label: 'Gói & bảng giá', icon: '₫' },
    { key: 'bookings', label: 'Booking hệ thống', icon: '≡' },
    { key: 'promos', label: 'Khuyến mãi', icon: '❖' },
    { key: 'ops', label: 'Ca làm & wash bay', icon: '⊞' },
  ]

  // ---- lọc + sort danh sách tài khoản ----
  const accountRows = accounts
      .filter((a) => {
        const q = search.trim().toLowerCase()
        const okSearch = !q || a.name.toLowerCase().includes(q) || (a.phone || '').toLowerCase().includes(q)
        const okRole = roleFilter === 'all' || a.role === roleFilter
        const okStatus = statusFilter === 'all' || (statusFilter === 'active' ? a.active : !a.active)
        return okSearch && okRole && okStatus
      })
      .sort((x, y) => {
        if (nameSort === 'az') return x.name.localeCompare(y.name, 'vi')
        if (nameSort === 'za') return y.name.localeCompare(x.name, 'vi')
        return 0
      })

  const roleOptStyle = (r) => 'flex:1;padding:11px 8px;border-radius:10px;font-size:13px;cursor:pointer;text-align:center;' + (editForm.role === r ? 'background:var(--gold);border:1px solid var(--gold);color:#100f0c;font-weight:600;' : 'background:transparent;border:1.5px solid rgba(255,255,255,0.16);color:#c4bfb2;')

  return (
      <div style={css('background:#0f0e0c;color:#f4f1ea;min-height:100vh;')}>
        <header style={css('position:sticky;top:0;z-index:50;display:flex;align-items:center;justify-content:space-between;padding:15px 30px;background:rgba(15,14,12,0.92);backdrop-filter:blur(14px);border-bottom:1.5px solid rgba(255,255,255,0.12);')}>
          <div><div style={css('font-family:var(--font-display);font-size:17px;line-height:1;')}>AutoWash Pro</div><div style={css('font-size:11px;color:#8b8578;margin-top:2px;letter-spacing:1px;')}>QUẢN TRỊ HỆ THỐNG</div></div>
          <div style={css('display:flex;align-items:center;gap:18px;')}>
            <div style={css('width:38px;height:38px;border-radius:50%;border:1px solid var(--gold);display:flex;align-items:center;justify-content:center;font-family:var(--font-display);color:var(--gold);')}>{(localStorage.getItem('fullName') || 'A').charAt(0).toUpperCase()}</div>
            <Link to="/" className="hov-danger" style={css('font-size:13px;color:#8b8578;text-decoration:none;')}>Đăng xuất</Link>
          </div>
        </header>

        <div className="dash-grid" style={css('display:grid;grid-template-columns:236px 1fr;min-height:calc(100vh - 70px);')}>
          <aside className="dash-aside" style={css('border-right:1.5px solid rgba(255,255,255,0.12);padding:24px 14px;display:flex;flex-direction:column;gap:4px;')}>
            {NAV.map((n) => {
              const a = tab === n.key
              return (<button key={n.key} onClick={() => setTab(n.key)} style={css('display:flex;align-items:center;gap:13px;width:100%;text-align:left;padding:12px 15px;border:none;border-radius:11px;font-size:14px;cursor:pointer;font-family:var(--font-body);' + (a ? 'background:rgba(200,162,83,0.12);color:var(--gold);font-weight:600;' : 'background:transparent;color:#a39e92;font-weight:500;'))}><span style={css('font-size:16px;width:20px;text-align:center;color:' + (a ? 'var(--gold)' : '#6f6a5e') + ';')}>{n.icon}</span><span>{n.label}</span></button>)
            })}
          </aside>

          <main style={css('padding:30px 36px 70px;')}>
            {tab === 'reports' && (
                <div style={css('animation:fadeUp .4s ease both;')}>
                  <h1 style={css('font-family:var(--font-display);font-size:28px;margin:0 0 4px;font-weight:500;')}>Báo cáo &amp; thống kê</h1>
                  <p style={css('font-size:14px;color:#8b8578;margin:0 0 26px;')}></p>
                  <div className="grid-4" style={css('display:grid;grid-template-columns:repeat(4,1fr);gap:14px;margin-bottom:16px;')}>
                    {kpis.map((k) => (<div key={k.label} style={css('border:1.5px solid rgba(255,255,255,0.16);border-radius:14px;padding:22px;')}><div style={css('font-size:12.5px;color:#8b8578;')}>{k.label}</div><div style={css('font-family:var(--font-display);font-size:25px;color:#f4f1ea;margin-top:8px;')}>{k.value}</div><div style={css('font-size:12px;color:' + k.deltaColor + ';margin-top:6px;')}>{k.delta}</div></div>))}
                  </div>
                  <div className="two-col" style={css('display:grid;grid-template-columns:1.4fr 1fr;gap:16px;')}>
                    <div style={css('border:1.5px solid rgba(255,255,255,0.16);border-radius:16px;padding:24px 26px;')}>
                      <div style={css('font-family:var(--font-display);font-size:18px;margin-bottom:22px;')}>Doanh thu theo dịch vụ (đơn hoàn thành)</div>
                      {svcAgg.length === 0 ? <div style={css('color:#8b8578;font-size:13.5px;')}>Chưa có đơn hoàn thành.</div> : (
                          <div style={css('display:flex;flex-direction:column;gap:16px;')}>
                            {svcAgg.map((x, i) => (<div key={x.n}><div style={css('display:flex;justify-content:space-between;font-size:13.5px;margin-bottom:7px;')}><span style={css('color:#c4bfb2;')}>{x.n}</span><span style={css('color:#f4f1ea;')}>{fmt(x.a)}đ</span></div><div style={css('height:7px;border-radius:5px;background:rgba(255,255,255,0.08);overflow:hidden;')}><div style={css('height:100%;width:' + Math.round(x.a / smax * 100) + '%;border-radius:5px;background:' + (i === 0 ? 'var(--gold)' : 'rgba(200,162,83,0.4)') + ';')}></div></div></div>))}
                          </div>)}
                    </div>
                    <div style={css('border:1.5px solid rgba(255,255,255,0.16);border-radius:16px;padding:24px 26px;')}>
                      <div style={css('font-family:var(--font-display);font-size:18px;margin-bottom:18px;')}>Hoạt động gần đây</div>
                      {activity.length === 0 ? <div style={css('color:#8b8578;font-size:13.5px;')}>Chưa có hoạt động.</div> : activity.map((a, i) => (<div key={i} style={css('display:flex;align-items:center;gap:12px;padding:11px 0;border-bottom:1px solid rgba(255,255,255,0.07);')}><span style={css('width:8px;height:8px;border-radius:50%;background:var(--gold);flex-shrink:0;')}></span><span style={css('flex:1;font-size:13px;color:#d6d1c5;')}>{a.text}</span><span style={css('font-size:11.5px;color:#8b8578;')}>{a.tag}</span></div>))}
                    </div>
                  </div>

                  {/* ==== Doanh thu theo tháng ==== */}
                  <div style={css('border:1.5px solid rgba(255,255,255,0.16);border-radius:16px;padding:24px 26px;margin-top:16px;')}>
                    <div style={css('display:flex;justify-content:space-between;align-items:center;flex-wrap:wrap;gap:14px;margin-bottom:22px;')}>
                      <div style={css('font-family:var(--font-display);font-size:18px;')}>Doanh thu theo tháng</div>
                      <div style={css('display:flex;gap:10px;')}>
                        <select value={selMonth} onChange={(e) => setSelMonth(Number(e.target.value))} style={css(selStyle)}>
                          {Array.from({ length: 12 }, (_, i) => <option key={i} value={i} style={css('background:#16140f;')}>Tháng {i + 1}</option>)}
                        </select>
                        <select value={selYear} onChange={(e) => setSelYear(Number(e.target.value))} style={css(selStyle)}>
                          {years.map((y) => <option key={y} value={y} style={css('background:#16140f;')}>Năm {y}</option>)}
                        </select>
                      </div>
                    </div>

                    <div style={css('display:flex;gap:36px;flex-wrap:wrap;margin-bottom:26px;')}>
                      <div><div style={css('font-size:12.5px;color:#8b8578;')}>Doanh thu tháng {selMonth + 1}/{selYear}</div><div style={css('font-family:var(--font-display);font-size:26px;color:var(--gold);margin-top:6px;')}>{fmt(cur.rev)}đ</div></div>
                      <div><div style={css('font-size:12.5px;color:#8b8578;')}>Số đơn hoàn thành</div><div style={css('font-family:var(--font-display);font-size:26px;color:#f4f1ea;margin-top:6px;')}>{cur.cnt}</div></div>
                    </div>

                    <div style={css('font-size:13px;color:#8b8578;margin-bottom:14px;')}>So sánh 12 tháng · {selYear}</div>
                    <div style={css('display:grid;grid-template-columns:repeat(12,1fr);gap:8px;align-items:end;height:180px;')}>
                      {monthly.map((mo, i) => {
                        const h = Math.round(mo.rev / mmax * 150)
                        const sel = i === selMonth
                        return (
                            <div key={i} onClick={() => setSelMonth(i)} style={css('display:flex;flex-direction:column;align-items:center;gap:6px;cursor:pointer;height:100%;justify-content:flex-end;')} title={fmt(mo.rev) + 'đ · ' + mo.cnt + ' đơn'}>
                              <span style={css('font-size:10.5px;color:' + (mo.rev ? '#c4bfb2' : '#5a564c') + ';white-space:nowrap;')}>{mo.rev ? compact(mo.rev) : ''}</span>
                              <div style={css('width:100%;max-width:34px;height:' + Math.max(h, mo.rev ? 4 : 2) + 'px;border-radius:6px 6px 0 0;background:' + (sel ? 'var(--gold)' : mo.rev ? 'rgba(200,162,83,0.4)' : 'rgba(255,255,255,0.08)') + ';transition:height .3s ease;')}></div>
                              <span style={css('font-size:11px;color:' + (sel ? 'var(--gold)' : '#8b8578') + ';font-weight:' + (sel ? '600' : '400') + ';')}>T{i + 1}</span>
                            </div>
                        )
                      })}
                    </div>
                  </div>
                </div>
            )}

            {tab === 'accounts' && (
                <div style={css('animation:fadeUp .4s ease both;')}>
                  <div style={css('display:flex;justify-content:space-between;align-items:flex-end;margin-bottom:20px;flex-wrap:wrap;gap:14px;')}>
                    <div><h1 style={css('font-family:var(--font-display);font-size:28px;margin:0 0 4px;font-weight:500;')}>Quản lý tài khoản</h1><p style={css('font-size:14px;color:#8b8578;margin:0;')}></p></div>
                    <button onClick={addAccount} className="hov-bright" style={css('background:var(--gold);border:none;color:#100f0c;padding:12px 22px;border-radius:30px;font-size:14px;font-weight:600;cursor:pointer;')}>+ Thêm tài khoản</button>
                  </div>

                  {/* ô tìm kiếm */}
                  <div style={css('position:relative;margin-bottom:18px;max-width:420px;')}>
                    <input value={search} onChange={(e) => setSearch(e.target.value)} placeholder="Tìm theo tên hoặc số điện thoại…" className="foc-gold" style={css(inp + 'padding-right:40px;')} />
                    {search && <button onClick={() => setSearch('')} style={css('position:absolute;right:12px;top:50%;transform:translateY(-50%);background:transparent;border:none;color:#8b8578;font-size:18px;cursor:pointer;line-height:1;')}>×</button>}
                  </div>

                  <div style={css('border:1.5px solid rgba(255,255,255,0.16);border-radius:14px;overflow:visible;')}>
                    <div style={css('display:grid;grid-template-columns:1.4fr 1fr 1fr 0.8fr 120px;padding:14px 22px;background:rgba(255,255,255,0.03);border-radius:12px 12px 0 0;font-size:12px;letter-spacing:0.5px;align-items:center;')}>
                      {/* HỌ TÊN - sort */}
                      <div style={css('position:relative;')} onClick={(e) => e.stopPropagation()}>
                        <button onClick={() => setOpenMenu(openMenu === 'name' ? null : 'name')} style={css(hBtn(!!nameSort))}>Họ tên <span style={css('font-size:9px;')}>▼</span></button>
                        {openMenu === 'name' && (
                            <div style={css(hMenu)}>
                              <button onClick={() => { setNameSort('az'); setOpenMenu(null) }} style={css(hItem(nameSort === 'az'))}>A → Z</button>
                              <button onClick={() => { setNameSort('za'); setOpenMenu(null) }} style={css(hItem(nameSort === 'za'))}>Z → A</button>
                              <button onClick={() => { setNameSort(null); setOpenMenu(null) }} style={css(hItem(!nameSort))}>Bỏ sắp xếp</button>
                            </div>
                        )}
                      </div>

                      <span style={css('font-size:12px;color:#8b8578;text-transform:uppercase;')}>Liên hệ</span>

                      {/* VAI TRÒ - lọc */}
                      <div style={css('position:relative;')} onClick={(e) => e.stopPropagation()}>
                        <button onClick={() => setOpenMenu(openMenu === 'role' ? null : 'role')} style={css(hBtn(roleFilter !== 'all'))}>Vai trò <span style={css('font-size:9px;')}>▼</span></button>
                        {openMenu === 'role' && (
                            <div style={css(hMenu)}>
                              {[['all', 'Tất cả'], ['customer', 'Khách hàng'], ['receptionist', 'Tiếp tân'], ['washer', 'Nhân viên rửa'], ['admin', 'Quản trị']].map(([k, l]) => (
                                  <button key={k} onClick={() => { setRoleFilter(k); setOpenMenu(null) }} style={css(hItem(roleFilter === k))}>{l}</button>
                              ))}
                            </div>
                        )}
                      </div>

                      {/* TRẠNG THÁI - lọc */}
                      <div style={css('position:relative;')} onClick={(e) => e.stopPropagation()}>
                        <button onClick={() => setOpenMenu(openMenu === 'status' ? null : 'status')} style={css(hBtn(statusFilter !== 'all'))}>Trạng thái <span style={css('font-size:9px;')}>▼</span></button>
                        {openMenu === 'status' && (
                            <div style={css(hMenu)}>
                              {[['all', 'Tất cả'], ['active', 'Hoạt động'], ['inactive', 'Vô hiệu']].map(([k, l]) => (
                                  <button key={k} onClick={() => { setStatusFilter(k); setOpenMenu(null) }} style={css(hItem(statusFilter === k))}>{l}</button>
                              ))}
                            </div>
                        )}
                      </div>

                      <span style={css('text-align:right;font-size:12px;color:#8b8578;text-transform:uppercase;')}>Thao tác</span>
                    </div>

                    {accountRows.length === 0 ? (
                        <div style={css('padding:34px 22px;text-align:center;color:#8b8578;font-size:13.5px;border-top:1px solid rgba(255,255,255,0.07);')}>Không tìm thấy tài khoản phù hợp.</div>
                    ) : accountRows.map((u) => (
                        <div key={u.id} style={css('display:grid;grid-template-columns:1.4fr 1fr 1fr 0.8fr 120px;padding:15px 22px;border-top:1px solid rgba(255,255,255,0.07);align-items:center;')}>
                          <div style={css('display:flex;align-items:center;gap:11px;')}><div style={css('width:34px;height:34px;border-radius:50%;background:rgba(200,162,83,0.12);border:1px solid rgba(200,162,83,0.25);display:flex;align-items:center;justify-content:center;font-family:var(--font-display);color:var(--gold);font-size:13px;flex-shrink:0;')}>{(u.name.trim()[0] || '?').toUpperCase()}</div><span style={css('font-size:14px;color:#f4f1ea;')}>{u.name}</span></div>
                          <div style={css('min-width:0;')}><div style={css('font-size:13.5px;color:#c4bfb2;')}>{u.phone}</div><div style={css('font-size:12px;color:#8b8578;margin-top:2px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap;')}>{u.email || '—'}</div></div>
                          <span style={css('font-size:12.5px;font-weight:600;color:' + (roleColor[u.role] || '#a39e92') + ';')}>{roleVi[u.role] || u.role}</span>
                          <span style={css('font-size:12.5px;font-weight:600;color:' + (u.active ? '#6fcf97' : '#e08a6a') + ';')}>{u.active ? 'Hoạt động' : 'Vô hiệu'}</span>
                          <div style={css('display:flex;gap:7px;justify-content:flex-end;')}>
                            <button onClick={() => editAccount(u.id)} className="hov-border-gold" style={css('background:transparent;border:1px solid rgba(255,255,255,0.16);color:#c4bfb2;padding:6px 11px;border-radius:8px;font-size:12px;cursor:pointer;')}>Sửa</button>
                            <button onClick={() => toggleAccount(u.id)} style={css('background:transparent;border:1px solid ' + (u.active ? 'rgba(224,138,106,0.3)' : 'rgba(111,207,151,0.3)') + ';color:' + (u.active ? '#e08a6a' : '#6fcf97') + ';padding:6px 11px;border-radius:8px;font-size:12px;cursor:pointer;')}>{u.active ? 'Vô hiệu' : 'Kích hoạt'}</button>
                          </div>
                        </div>))}
                  </div>
                </div>
            )}

            {tab === 'pricing' && (
                <div style={css('animation:fadeUp .4s ease both;')}>
                  <h1 style={css('font-family:var(--font-display);font-size:28px;margin:0 0 4px;font-weight:500;')}>Gói dịch vụ &amp; bảng giá</h1>
                  <p style={css('font-size:14px;color:#8b8578;margin:0 0 20px;')}></p>

                  {/* ô tìm kiếm gói */}
                  <div style={css('position:relative;margin-bottom:18px;max-width:420px;')}>
                    <input value={svcSearch} onChange={(e) => setSvcSearch(e.target.value)} placeholder="Tìm gói dịch vụ…" className="foc-gold" style={css(inp + 'padding-right:40px;')} />
                    {svcSearch && <button onClick={() => setSvcSearch('')} style={css('position:absolute;right:12px;top:50%;transform:translateY(-50%);background:transparent;border:none;color:#8b8578;font-size:18px;cursor:pointer;line-height:1;')}>×</button>}
                  </div>

                  <div style={css('border:1.5px solid rgba(255,255,255,0.16);border-radius:14px;overflow:hidden;')}>
                    <div style={css('display:grid;grid-template-columns:1.4fr 1fr 1fr 1fr 1.1fr 80px;padding:14px 22px;background:rgba(255,255,255,0.03);font-size:12px;color:#8b8578;letter-spacing:0.5px;text-transform:uppercase;')}><span>Gói dịch vụ</span><span>Ngày thường</span><span>Cuối tuần</span><span>Lễ / Tết</span><span>Đã thu</span><span></span></div>
                    {serviceRows.length === 0 ? (
                        <div style={css('padding:34px 22px;text-align:center;color:#8b8578;font-size:13.5px;border-top:1px solid rgba(255,255,255,0.07);')}>Không tìm thấy gói phù hợp.</div>
                    ) : serviceRows.map((p) => {
                      const paid = svcPaid[p.n] || 0
                      return (
                          <div key={p.id} style={css('display:grid;grid-template-columns:1.4fr 1fr 1fr 1fr 1.1fr 80px;padding:16px 22px;border-top:1px solid rgba(255,255,255,0.07);align-items:center;')}>
                            <div><div style={css('font-family:var(--font-display);font-size:15.5px;')}>{p.n}</div><div style={css('font-size:12.5px;color:#8b8578;margin-top:2px;')}>{p.d}</div></div>
                            <span style={css('font-size:14px;color:#f4f1ea;')}>{fmt(p.w)}đ</span>
                            <span style={css('font-size:14px;color:#a39e92;')}>{fmt(Math.round(p.w * 1.10))}đ</span>
                            <span style={css('font-size:14px;color:#a39e92;')}>{fmt(Math.round(p.w * 1.20))}đ</span>
                            <span style={css('font-size:14px;font-weight:600;color:' + (paid ? 'var(--gold)' : '#5a564c') + ';')}>{paid ? fmt(paid) + 'đ' : '—'}</span>
                            <button onClick={() => handleEditPrice(p)} className="hov-border-gold" style={css('background:transparent;border:1px solid rgba(255,255,255,0.16);color:var(--gold);padding:7px 12px;border-radius:8px;font-size:12.5px;cursor:pointer;justify-self:end;font-weight:600;')}>Sửa</button>
                          </div>
                      )
                    })}
                  </div>
                </div>
            )}

            {tab === 'bookings' && (
                <div style={css('animation:fadeUp .4s ease both;')}>
                  <h1 style={css('font-family:var(--font-display);font-size:28px;margin:0 0 4px;font-weight:500;')}>Booking toàn hệ thống</h1>
                  <p style={css('font-size:14px;color:#8b8578;margin:0 0 24px;')}></p>

                  {/* ô tìm kiếm */}
                  <div style={css('position:relative;margin-bottom:18px;max-width:420px;')}>
                    <input value={bkSearch} onChange={(e) => setBkSearch(e.target.value)} placeholder="Tìm theo mã, tên khách, gói…" className="foc-gold" style={css(inp + 'padding-right:40px;')} />
                    {bkSearch && <button onClick={() => setBkSearch('')} style={css('position:absolute;right:12px;top:50%;transform:translateY(-50%);background:transparent;border:none;color:#8b8578;font-size:18px;cursor:pointer;line-height:1;')}>×</button>}
                  </div>

                  <div style={css('border:1.5px solid rgba(255,255,255,0.16);border-radius:14px;overflow:visible;')}>
                    <div style={css('display:grid;grid-template-columns:0.7fr 1.1fr 1fr 1fr 0.9fr 0.9fr;padding:14px 22px;background:rgba(255,255,255,0.03);border-radius:12px 12px 0 0;font-size:12px;letter-spacing:0.5px;align-items:center;')}>
                      <span style={css('font-size:12px;color:#8b8578;text-transform:uppercase;')}>Mã</span>

                      {/* KHÁCH - sort */}
                      <div style={css('position:relative;')} onClick={(e) => e.stopPropagation()}>
                        <button onClick={() => setOpenMenu(openMenu === 'bname' ? null : 'bname')} style={css(hBtn(!!bkNameSort))}>Khách <span style={css('font-size:9px;')}>▼</span></button>
                        {openMenu === 'bname' && (
                            <div style={css(hMenu)}>
                              <button onClick={() => { setBkNameSort('az'); setOpenMenu(null) }} style={css(hItem(bkNameSort === 'az'))}>A → Z</button>
                              <button onClick={() => { setBkNameSort('za'); setOpenMenu(null) }} style={css(hItem(bkNameSort === 'za'))}>Z → A</button>
                              <button onClick={() => { setBkNameSort(null); setOpenMenu(null) }} style={css(hItem(!bkNameSort))}>Bỏ sắp xếp</button>
                            </div>
                        )}
                      </div>

                      {/* GÓI - lọc */}
                      <div style={css('position:relative;')} onClick={(e) => e.stopPropagation()}>
                        <button onClick={() => setOpenMenu(openMenu === 'bpkg' ? null : 'bpkg')} style={css(hBtn(bkPkgFilter !== 'all'))}>Gói <span style={css('font-size:9px;')}>▼</span></button>
                        {openMenu === 'bpkg' && (
                            <div style={css(hMenu + 'min-width:210px;')}>
                              <button onClick={() => { setBkPkgFilter('all'); setOpenMenu(null) }} style={css(hItem(bkPkgFilter === 'all'))}>Tất cả gói</button>
                              {pkgList.map((p) => (
                                  <button key={p} onClick={() => { setBkPkgFilter(p); setOpenMenu(null) }} style={css(hItem(bkPkgFilter === p))}>{p}</button>
                              ))}
                            </div>
                        )}
                      </div>

                      {/* THỜI GIAN - sort + khoảng ngày */}
                      <div style={css('position:relative;')} onClick={(e) => e.stopPropagation()}>
                        <button onClick={() => setOpenMenu(openMenu === 'btime' ? null : 'btime')} style={css(hBtn(bkTimeSort !== 'new' || !!bkFrom || !!bkTo))}>Thời gian <span style={css('font-size:9px;')}>▼</span></button>
                        {openMenu === 'btime' && (
                            <div style={css(hMenu + 'min-width:240px;')}>
                              <button onClick={() => { setBkTimeSort('new'); setOpenMenu(null) }} style={css(hItem(bkTimeSort === 'new'))}>Mới nhất trước</button>
                              <button onClick={() => { setBkTimeSort('old'); setOpenMenu(null) }} style={css(hItem(bkTimeSort === 'old'))}>Cũ nhất trước</button>
                              <div style={css('height:1px;background:rgba(255,255,255,0.1);margin:6px 4px;')}></div>
                              <div style={css('padding:4px 8px 6px;')}>
                                <label style={css('display:block;font-size:11px;color:#8b8578;margin-bottom:5px;text-transform:none;letter-spacing:0;')}>Từ ngày</label>
                                <input type="date" value={bkFrom} onChange={(e) => setBkFrom(e.target.value)} style={css(dInp + 'margin-bottom:10px;')} />
                                <label style={css('display:block;font-size:11px;color:#8b8578;margin-bottom:5px;text-transform:none;letter-spacing:0;')}>Đến ngày</label>
                                <input type="date" value={bkTo} onChange={(e) => setBkTo(e.target.value)} style={css(dInp)} />
                                {(bkFrom || bkTo) && <button onClick={() => { setBkFrom(''); setBkTo('') }} style={css('margin-top:10px;background:transparent;border:none;color:#e08a6a;font-size:12px;cursor:pointer;')}>Xoá khoảng ngày</button>}
                              </div>
                            </div>
                        )}
                      </div>

                      <span style={css('font-size:12px;color:#8b8578;text-transform:uppercase;')}>Số tiền</span>

                      {/* TRẠNG THÁI - lọc */}
                      <div style={css('position:relative;')} onClick={(e) => e.stopPropagation()}>
                        <button onClick={() => setOpenMenu(openMenu === 'bstatus' ? null : 'bstatus')} style={css(hBtn(bkStatusFilter !== 'all'))}>Trạng thái <span style={css('font-size:9px;')}>▼</span></button>
                        {openMenu === 'bstatus' && (
                            <div style={css(hMenu + 'min-width:190px;')}>
                              <button onClick={() => { setBkStatusFilter('all'); setOpenMenu(null) }} style={css(hItem(bkStatusFilter === 'all'))}>Tất cả</button>
                              {Object.entries(stv).map(([k, l]) => (
                                  <button key={String(k)} onClick={() => { setBkStatusFilter(k); setOpenMenu(null) }} style={css(hItem(bkStatusFilter === k))}>{l}</button>
                              ))}
                            </div>
                        )}
                      </div>
                    </div>

                    {bookingRows.length === 0 ? (
                        <div style={css('padding:34px 22px;text-align:center;color:#8b8578;font-size:13.5px;border-top:1px solid rgba(255,255,255,0.07);')}>Không có booking phù hợp.</div>
                    ) : bookingRows.map((b) => (
                        <div key={b.id} style={css('display:grid;grid-template-columns:0.7fr 1.1fr 1fr 1fr 0.9fr 0.9fr;padding:15px 22px;border-top:1px solid rgba(255,255,255,0.07);align-items:center;')}>
                          <span style={css('font-size:13px;color:var(--gold);font-family:var(--font-display);')}>{b.id}</span>
                          <span style={css('font-size:13.5px;color:#f4f1ea;')}>{b.name}</span>
                          <span style={css('font-size:13.5px;color:#a39e92;')}>{b.pkg}</span>
                          <span style={css('font-size:13px;color:#a39e92;')}>{b.when}</span>
                          <span style={css('font-size:13.5px;font-weight:600;color:' + (b.st === 'Completed' ? 'var(--gold)' : '#a39e92') + ';')}>{b.priceN ? fmt(b.priceN) + 'đ' : '—'}</span>
                          <span style={css(bBadge(b.st))}>{stv[b.st]}</span>
                        </div>))}
                  </div>
                </div>
            )}

            {tab === 'promos' && (
                <div style={css('animation:fadeUp .4s ease both;')}>
                  <div style={css('display:flex;justify-content:space-between;align-items:flex-end;margin-bottom:24px;flex-wrap:wrap;gap:14px;')}>
                    <div><h1 style={css('font-family:var(--font-display);font-size:28px;margin:0 0 4px;font-weight:500;')}>Khuyến mãi</h1><p style={css('font-size:14px;color:#8b8578;margin:0;')}></p></div>
                    <button onClick={() => setPromoModalOpen(true)} className="hov-bright" style={css('background:var(--gold);border:none;color:#100f0c;padding:12px 22px;border-radius:30px;font-size:14px;font-weight:600;cursor:pointer;')}>+ Tạo voucher</button>
                  </div>
                  {promos.length === 0 ? <div style={css('text-align:center;padding:40px;color:#8b8578;')}>Chưa có voucher.</div> : (
                      <div className="grid-3" style={css('display:grid;grid-template-columns:repeat(3,1fr);gap:14px;')}>
                        {promos.map((p) => (
                            <div key={p.id} style={css('border:1.5px solid rgba(255,255,255,0.16);border-radius:14px;padding:20px 22px;')}>
                              <div style={css('display:flex;justify-content:space-between;align-items:flex-start;')}><div style={css('font-family:var(--font-display);font-size:18px;color:var(--gold);')}>{p.code}</div><span style={css('font-size:11.5px;font-weight:600;padding:4px 10px;border-radius:16px;color:' + (p.active ? '#6fcf97' : '#e08a6a') + ';background:' + (p.active ? 'rgba(111,207,151,0.12)' : 'rgba(224,138,106,0.12)') + ';')}>{p.active ? 'Đang chạy' : 'Tắt'}</span></div>
                              <div style={css('font-size:13.5px;color:#d6d1c5;margin-top:8px;')}>{p.desc}{p.pct ? ' · ' + p.pct + '%' : ''}</div>
                              <div style={css('display:flex;justify-content:space-between;align-items:center;margin-top:12px;')}><span style={css('font-size:12.5px;color:#8b8578;')}>HSD {p.expiry}</span><button onClick={() => deletePromo(p.id)} className="hov-danger" style={css('background:transparent;border:none;color:#e08a6a;font-size:12.5px;cursor:pointer;')}>Xoá</button></div>
                            </div>))}
                      </div>)}
                </div>
            )}

            {tab === 'ops' && (
                <div style={css('animation:fadeUp .4s ease both;')}>
                  <h1 style={css('font-family:var(--font-display);font-size:28px;margin:0 0 4px;font-weight:500;')}>Ca làm việc &amp; khu rửa xe</h1>
                  <p style={css('font-size:14px;color:#8b8578;margin:0 0 24px;')}></p>
                  <div className="two-col" style={css('display:grid;grid-template-columns:1.2fr 1fr;gap:16px;')}>
                    <div style={css('border:1.5px solid rgba(255,255,255,0.16);border-radius:16px;padding:22px 26px;')}>
                      <div style={css('font-family:var(--font-display);font-size:17px;margin-bottom:18px;')}>Ca làm việc</div>
                      {shifts.length === 0 ? <div style={css('color:#8b8578;font-size:13.5px;')}>Chưa có ca nào.</div> : (
                          <div style={css('display:flex;flex-direction:column;gap:11px;')}>
                            {shifts.map((sh) => (<div key={sh.id} style={css('display:flex;align-items:center;gap:14px;padding:13px 16px;border:1.5px solid rgba(255,255,255,0.12);border-radius:12px;')}><div style={css('flex:1;')}><div style={css('font-size:14.5px;color:#f4f1ea;')}>{sh.type}</div><div style={css('font-size:12.5px;color:#8b8578;margin-top:2px;')}>{sh.date}</div></div><span style={css('font-size:12.5px;color:var(--gold);padding:5px 12px;border:1px solid rgba(200,162,83,0.3);border-radius:20px;')}>{sh.time}</span></div>))}
                          </div>)}
                    </div>
                    <div style={css('border:1.5px solid rgba(255,255,255,0.16);border-radius:16px;padding:22px 26px;')}>
                      <div style={css('font-family:var(--font-display);font-size:17px;margin-bottom:18px;')}>Trạng thái wash bay</div>
                      <div style={css('display:grid;grid-template-columns:1fr 1fr;gap:12px;')}>
                        {bays.map((b) => (<div key={b.name} style={css('border:1.5px solid ' + (b.busy ? 'rgba(200,162,83,0.35)' : 'rgba(255,255,255,0.14)') + ';border-radius:13px;padding:18px;background:' + (b.busy ? 'rgba(200,162,83,0.05)' : 'transparent') + ';')}><div style={css('display:flex;align-items:center;gap:8px;')}><span style={css('width:9px;height:9px;border-radius:50%;background:' + (b.busy ? 'var(--gold)' : '#3a4a3a') + ';')}></span><span style={css('font-family:var(--font-display);font-size:16px;')}>{b.name}</span></div><div style={css('font-size:13px;color:#a39e92;margin-top:10px;')}>{b.status}</div></div>))}
                      </div>
                    </div>
                  </div>
                </div>
            )}
          </main>
        </div>

        {editOpen && (
            <div onClick={() => setEditOpen(false)} style={css('position:fixed;inset:0;z-index:100;background:rgba(8,7,6,0.8);backdrop-filter:blur(6px);display:flex;align-items:center;justify-content:center;padding:24px;')}>
              <div onClick={(e) => e.stopPropagation()} style={css('width:100%;max-width:480px;border:1.5px solid rgba(255,255,255,0.14);border-radius:18px;background:#16140f;padding:30px 32px;animation:fadeUp .35s ease both;')}>
                <h3 style={css('font-family:var(--font-display);font-size:21px;margin:0 0 22px;font-weight:500;')}>{editIsNew ? 'Thêm tài khoản mới' : 'Sửa tài khoản'}</h3>
                <div style={css('display:flex;flex-direction:column;gap:16px;')}>
                  <div><label style={css('display:block;font-size:13px;color:#a39e92;margin-bottom:8px;')}>Họ và tên</label><input value={editForm.name} onChange={(e) => setEditForm({ ...editForm, name: e.target.value })} className="foc-gold" style={css(inp)} /></div>
                  <div><label style={css('display:block;font-size:13px;color:#a39e92;margin-bottom:8px;')}>Số điện thoại</label><input value={editForm.phone} onChange={(e) => setEditForm({ ...editForm, phone: e.target.value })} className="foc-gold" style={css(inp)} /></div>
                  <div><label style={css('display:block;font-size:13px;color:#a39e92;margin-bottom:8px;')}>Email</label><input value={editForm.email} onChange={(e) => setEditForm({ ...editForm, email: e.target.value })} className="foc-gold" style={css(inp)} /></div>
                  {editIsNew && <div><label style={css('display:block;font-size:13px;color:#a39e92;margin-bottom:8px;')}>Mật khẩu (≥ 6 ký tự)</label><input type="password" value={editForm.password} onChange={(e) => setEditForm({ ...editForm, password: e.target.value })} className="foc-gold" style={css(inp)} /></div>}
                  <div><label style={css('display:block;font-size:13px;color:#a39e92;margin-bottom:8px;')}>Vai trò</label><div style={css('display:flex;gap:10px;')}>{['customer', 'receptionist', 'washer'].map((r) => (<div key={r} onClick={() => setEditForm({ ...editForm, role: r })} style={css(roleOptStyle(r))}>{roleVi[r]}</div>))}</div></div>
                </div>
                {editError && <div style={css('color:#e08a6a;font-size:13.5px;margin-top:16px;')}>{editError}</div>}
                <div style={css('display:flex;justify-content:flex-end;gap:12px;margin-top:26px;')}><button onClick={() => setEditOpen(false)} style={css('background:transparent;border:1.5px solid rgba(255,255,255,0.14);border-radius:30px;color:#c4bfb2;padding:11px 24px;font-size:14px;font-weight:600;cursor:pointer;')}>Hủy</button><button onClick={saveEdit} className="hov-bright" style={css('background:var(--gold);border:none;border-radius:30px;color:#100f0c;padding:11px 26px;font-size:14px;font-weight:600;cursor:pointer;')}>Xác nhận</button></div>
              </div>
            </div>
        )}

        {priceModalOpen && selectedService && (
            <div onClick={() => setPriceModalOpen(false)} style={css('position:fixed;inset:0;z-index:100;background:rgba(8,7,6,0.8);backdrop-filter:blur(6px);display:flex;align-items:center;justify-content:center;padding:24px;')}>
              <div onClick={(e) => e.stopPropagation()} style={css('width:100%;max-width:460px;border:1.5px solid rgba(255,255,255,0.14);border-radius:18px;background:#16140f;padding:30px 32px;animation:fadeUp .35s ease both;')}>
                <h3 style={css('font-family:var(--font-display);font-size:21px;margin:0 0 4px;font-weight:500;')}>Sửa giá gói</h3>
                <p style={css('font-size:13px;color:#8b8578;margin:0 0 24px;')}>{selectedService.n}</p>
                <label style={css('display:block;color:#a39e92;font-size:13px;margin-bottom:8px;')}>Giá ngày thường (VNĐ)</label>
                <input type="number" value={priceForm.w} onChange={(e) => setPriceForm({ w: Number(e.target.value) || 0 })} className="foc-gold" style={css(inp)} />
                <div style={css('display:flex;justify-content:flex-end;gap:12px;margin-top:26px;')}><button onClick={() => setPriceModalOpen(false)} style={css('background:transparent;border:1.5px solid rgba(255,255,255,0.14);border-radius:30px;color:#c4bfb2;padding:11px 24px;font-size:14px;font-weight:600;cursor:pointer;')}>Hủy</button><button onClick={savePriceEdit} className="hov-bright" style={css('background:var(--gold);border:none;border-radius:30px;color:#100f0c;padding:11px 26px;font-size:14px;font-weight:600;cursor:pointer;')}>Lưu giá</button></div>
              </div>
            </div>
        )}

        {promoModalOpen && (
            <div onClick={() => setPromoModalOpen(false)} style={css('position:fixed;inset:0;z-index:100;background:rgba(8,7,6,0.8);backdrop-filter:blur(6px);display:flex;align-items:center;justify-content:center;padding:24px;')}>
              <div onClick={(e) => e.stopPropagation()} style={css('width:100%;max-width:480px;border:1.5px solid rgba(255,255,255,0.14);border-radius:18px;background:#16140f;padding:30px 32px;animation:fadeUp .35s ease both;')}>
                <h3 style={css('font-family:var(--font-display);font-size:21px;margin:0 0 22px;font-weight:500;')}>Tạo voucher (giảm %)</h3>
                <div style={css('display:flex;flex-direction:column;gap:16px;')}>
                  <div><label style={css('display:block;color:#a39e92;font-size:13px;margin-bottom:8px;')}>Mã voucher</label><input value={promoForm.code} onChange={(e) => setPromoForm({ ...promoForm, code: e.target.value })} placeholder="SUMMER20" className="foc-gold" style={css(inp)} /></div>
                  <div><label style={css('display:block;color:#a39e92;font-size:13px;margin-bottom:8px;')}>Phần trăm giảm (%)</label><input type="number" value={promoForm.value} onChange={(e) => setPromoForm({ ...promoForm, value: e.target.value })} placeholder="10" className="foc-gold" style={css(inp)} /></div>
                  <div><label style={css('display:block;color:#a39e92;font-size:13px;margin-bottom:8px;')}>Mô tả</label><input value={promoForm.desc} onChange={(e) => setPromoForm({ ...promoForm, desc: e.target.value })} className="foc-gold" style={css(inp)} /></div>
                  <div><label style={css('display:block;color:#a39e92;font-size:13px;margin-bottom:8px;')}>Hạn sử dụng</label><input type="date" value={promoForm.expiry} onChange={(e) => setPromoForm({ ...promoForm, expiry: e.target.value })} className="foc-gold" style={css(inp)} /></div>
                </div>
                <div style={css('display:flex;justify-content:flex-end;gap:12px;margin-top:26px;')}><button onClick={() => setPromoModalOpen(false)} style={css('background:transparent;border:1.5px solid rgba(255,255,255,0.14);border-radius:30px;color:#c4bfb2;padding:11px 24px;font-size:14px;font-weight:600;cursor:pointer;')}>Hủy</button><button onClick={handleCreateVoucher} className="hov-bright" style={css('background:var(--gold);border:none;border-radius:30px;color:#100f0c;padding:11px 26px;font-size:14px;font-weight:600;cursor:pointer;')}>Tạo</button></div>
              </div>
            </div>
        )}

        <Toast message={toast} />
      </div>
  )
}