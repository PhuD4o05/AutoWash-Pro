import { useState, useRef, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { css } from '../lib/css'
import { fmtVND } from '../lib/format'
import Toast from '../components/Toast'
Dashboar
import { receptionApi, publicApi } from '../api/endpoints'

const STATUS_VI = { Confirmed: 'Đã xác nhận', 'Checked-in': 'Đã nhận xe', Waiting: 'Đang chờ', Washing: 'Đang rửa', Drying: 'Đang sấy', Completed: 'Hoàn thành', Cancelled: 'Đã huỷ', Pending: 'Chờ xác nhận' }
const CODES = { WASH50: 50000, GOLD10: 0.10, NEW100: 100000 }
const badge = (st) => {
  const map = { Pending: ['#e3b341', 'rgba(227,179,65,0.14)'], Confirmed: ['#7fb8e0', 'rgba(127,184,224,0.14)'], 'Checked-in': ['#9b8cf0', 'rgba(155,140,240,0.14)'], Waiting: ['#e0a36a', 'rgba(224,163,106,0.14)'], Washing: ['var(--gold)', 'rgba(200,162,83,0.14)'], Drying: ['#7fb8e0', 'rgba(127,184,224,0.14)'], Completed: ['#6fcf97', 'rgba(111,207,151,0.14)'] }
  const c = map[st] || ['#a39e92', 'rgba(255,255,255,0.08)']
  return 'padding:6px 13px;border-radius:20px;font-size:12.5px;font-weight:600;color:' + c[0] + ';background:' + c[1] + ';white-space:nowrap;'
}
const inp = 'background:#0f0e0c;border:1.5px solid rgba(255,255,255,0.14);border-radius:11px;padding:13px 15px;color:#f4f1ea;font-size:14.5px;'

// ---- style dropdown header (đồng bộ với AdminDashboard) ----
const hBtn = (active) => 'display:inline-flex;align-items:center;gap:5px;background:transparent;border:none;cursor:pointer;font-size:12px;color:' + (active ? 'var(--gold)' : '#8b8578') + ';letter-spacing:0.5px;text-transform:uppercase;font-family:var(--font-body);padding:0;'
const hMenu = 'position:absolute;top:calc(100% + 8px);left:0;z-index:60;min-width:170px;max-height:320px;overflow:auto;background:#16140f;border:1.5px solid rgba(255,255,255,0.16);border-radius:12px;padding:6px;box-shadow:0 14px 34px rgba(0,0,0,0.55);'
const hItem = (active) => 'display:block;width:100%;text-align:left;background:' + (active ? 'rgba(200,162,83,0.12)' : 'transparent') + ';border:none;border-radius:8px;padding:9px 12px;font-size:13px;color:' + (active ? 'var(--gold)' : '#c4bfb2') + ';cursor:pointer;font-family:var(--font-body);'

export default function ReceptionDashboard() {
  const [tab, setTab] = useState('checkin')
  const [phoneLookup, setPhoneLookup] = useState('')
  const [lookupResult, setLookupResult] = useState(null)
  const [wi, setWi] = useState({ name: '', phone: '', plate: '', pkg: null, account: true, error: '' })

  const [allBookings, setAllBookings] = useState([])
  const [bays, setBays] = useState([])
  const [pkgs, setPkgs] = useState([])

  const [custSearch, setCustSearch] = useState('')
  const [sortName, setSortName] = useState('')      // '' | 'az' | 'za'
  const [filterTier, setFilterTier] = useState('')  // '' | MEMBER | SILVER | GOLD | PLATINUM
  const [sortPoints, setSortPoints] = useState('')  // '' | 'asc' | 'desc'
  const [openMenu, setOpenMenu] = useState(null)

  // đóng dropdown header khi click ra ngoài
  useEffect(() => {
    const close = () => setOpenMenu(null)
    document.addEventListener('click', close)
    return () => document.removeEventListener('click', close)
  }, [])

  // Quản lý khách vẫn là localStorage (BE chưa có list/CRUD khách cho reception -> sẽ nối ở Admin)
  const [customers, setCustomers] = useState([])
  const loadCustomers = () => {
    receptionApi.customers()
        .then((list) => setCustomers(list.map((c) => ({
          id: c.id,
          name: c.fullName,
          phone: c.phoneNumber,
          tier: c.membershipTier || 'MEMBER',
          points: c.currentPoints ?? 0,
        }))))
        .catch(() => {})
  }

  const ST = { PENDING: 'Pending', CONFIRMED: 'Confirmed', CHECKED_IN: 'Checked-in', WAITING: 'Waiting', WASHING: 'Washing', DRYING: 'Drying', COMPLETED: 'Completed', CANCELLED: 'Cancelled', NO_SHOW: 'Cancelled' }
  const fmtWhen = (s) => {
    if (!s) return '—'
    const d = new Date(s)
    return String(d.getDate()).padStart(2, '0') + '/' + String(d.getMonth() + 1).padStart(2, '0') + ' · ' + String(d.getHours()).padStart(2, '0') + ':' + String(d.getMinutes()).padStart(2, '0')
  }
  const loadBookings = () => {
    receptionApi.bookings()
        .then((list) => setAllBookings(list.map((b) => ({
          id: 'BK' + b.id, _rawId: b.id,
          customerId: b.customerId,
          name: b.customerName || '—',
          plate: b.licensePlate || '—',
          car: [b.carBrand, b.carModel].filter(Boolean).join(' ') || '—',
          pkg: b.packageName || '—',
          priceN: b.totalPrice || 0,
          when: fmtWhen(b.scheduledTime),
          status: ST[b.status] || 'Pending',
          bay: b.bayNumber || '',
          paid: !!b.paid,
          tier: 'Member', points: 0,
        }))))
        .catch(() => {})
  }
  useEffect(() => {
    loadBookings()
    loadCustomers()
    receptionApi.bays().then((list) => setBays(list.map((b) => ({ name: b.bayNumber, id: b.id, state: b.status, busy: b.status === 'OCCUPIED' })))).catch(() => {})
    publicApi.services().then((list) => {
      const mapped = list.map((s) => ({ id: s.id, name: s.name }))
      setPkgs(mapped)
      setWi((w) => (w.pkg == null && mapped[0] ? { ...w, pkg: mapped[0].id } : w))
    }).catch(() => {})
  }, [])

  const queue = allBookings.filter((b) => ['Confirmed', 'Checked-in', 'Waiting', 'Washing', 'Drying', 'Pending'].includes(b.status))
  const pay = allBookings.filter((b) => b.status === 'Completed' && !b.paid)

  const bayPlan = (() => {
    const free = bays.filter((b) => !b.busy).map((b) => b.name)
    const plan = {}
    let i = 0
    queue.forEach((q) => {
      if (q.bay) { plan[q.id] = q.bay; return }
      if (i < free.length) { plan[q.id] = free[i]; i++ }
    })
    return plan
  })()

  const [paySelId, setPaySelId] = useState(null)
  const [payVoucherInput, setPayVoucherInput] = useState('')
  const [payVoucher, setPayVoucher] = useState(null)
  const [custModal, setCustModal] = useState(null)
  const [custForm, setCustForm] = useState({ name: '', phone: '', password: '' })
  const [custResetId, setCustResetId] = useState(null)
  const [custError, setCustError] = useState('')
  const [toast, setToast] = useState('')
  const tt = useRef(null)
  const showToast = (m) => { setToast(m); clearTimeout(tt.current); tt.current = setTimeout(() => setToast(''), 3000) }
  const [payMethod, setPayMethod] = useState('cash') // 'cash' | 'qr'

  const lookupPhone = () => {
    receptionApi.lookupCustomer(phoneLookup.replace(/\s/g, ''))
        .then((c) => {
          const bk = allBookings.find((b) => b.customerId === c.id && (b.status === 'Pending' || b.status === 'Confirmed'))
          setLookupResult({
            name: c.fullName, tier: c.membershipTier || 'Member',
            booking: bk ? (bk.id + ' · ' + bk.when) : 'Không có lịch chờ check-in',
            car: bk ? (bk.car + ' · ' + bk.plate) : '—',
            points: c.currentPoints ?? 0,
            _rawId: bk ? bk._rawId : null,
          })
        })
        .catch(() => { setLookupResult(null); showToast('Không tìm thấy khách với SĐT này.') })
  }

  const scanQr = () => showToast('Tính năng camera QR đang ở chế độ demo. Dùng tra cứu SĐT để check-in.')

  const confirmCheckin = () => {
    if (!lookupResult?._rawId) return showToast('Khách chưa có lịch để check-in.')
    receptionApi.checkin(lookupResult._rawId)
        .then(() => { loadBookings(); showToast('Đã check-in ' + lookupResult.name + '.'); setLookupResult(null); setPhoneLookup('') })
        .catch(() => showToast('Check-in thất bại.'))
  }

  const createWalkin = () => {
    if (!wi.name.trim() || wi.phone.length < 9 || !wi.plate.trim()) return setWi({ ...wi, error: 'Nhập họ tên, SĐT hợp lệ và biển số xe.' })
    if (!wi.pkg) return setWi({ ...wi, error: 'Chọn gói dịch vụ.' })
    receptionApi.walkin({
      phone: wi.phone.trim(), fullName: wi.name.trim(),
      licensePlate: wi.plate.trim(), packageId: wi.pkg,
    })
        .then(() => { loadBookings(); showToast('Đã tạo đơn walk-in cho ' + wi.name.trim() + '.'); setWi({ name: '', phone: '', plate: '', pkg: pkgs[0]?.id || null, account: true, error: '' }); setTab('queue') })
        .catch((e) => setWi({ ...wi, error: e.response?.data?.message || 'Tạo walk-in thất bại.' }))
  }

  const setBay = (id, bayName) => {
    const item = allBookings.find((b) => b.id === id)
    const bay = bays.find((x) => x.name === bayName)
    if (!item || !bay) return
    receptionApi.assignBay(item._rawId, bay.id)
        .then(() => { loadBookings(); showToast('Đã phân ' + id + ' vào ' + bayName + '.') })
        .catch(() => showToast('Gán bay thất bại.'))
  }

  const qCancel = (id) => showToast('Huỷ đơn ' + id + ' — cần endpoint huỷ cho reception (làm sau).')

  const openCreateCust = () => { setCustModal('create'); setCustForm({ name: '', phone: '', password: '' }); setCustError('') }
  const openResetPw = (id) => { setCustModal('reset'); setCustResetId(id); setCustForm({ name: '', phone: '', password: '' }); setCustError('') }
  const saveCust = () => {
    const f = custForm
    if (custModal === 'create') {
      if (!f.name.trim() || f.phone.replace(/\D/g, '').length < 9) return setCustError('Nhập họ tên và SĐT hợp lệ.')
      if ((f.password || '').length < 6) return setCustError('Mật khẩu tối thiểu 6 ký tự.')
      receptionApi.createCustomer({ fullName: f.name.trim(), phone: f.phone.replace(/\s/g, ''), password: f.password })
          .then(() => { loadCustomers(); setCustModal(null); showToast('Đã tạo tài khoản khách ' + f.name.trim() + '.') })
          .catch((e) => setCustError(e.response?.data?.message || 'Tạo thất bại.'))
    } else {
      if ((f.password || '').length < 6) return setCustError('Mật khẩu mới tối thiểu 6 ký tự.')
      receptionApi.resetPassword(custResetId, f.password)
          .then(() => { setCustModal(null); showToast('Đã đặt lại mật khẩu.') })
          .catch(() => setCustError('Đặt lại thất bại.'))
    }
  }

  const applyPayVoucher = () => {
    const c = payVoucherInput.trim()
    if (CODES[c] !== undefined) { setPayVoucher({ code: c, value: CODES[c] }); showToast('Đã áp dụng ' + c + '.') }
    else showToast('Mã không hợp lệ.')
  }

  const doPay = () => {
    const item = pay.find((p) => p.id === paySelId)
    if (!item) return
    receptionApi.confirmPayment(item._rawId)
        .then(() => { loadBookings(); setPaySelId(null); setPayVoucher(null); setPayVoucherInput(''); showToast('Đã thu tiền từ ' + item.name + '.') })
        .catch(() => showToast('Xác nhận thanh toán thất bại.'))
  }

  const NAV = [
    { key: 'checkin', label: 'Check-in', icon: '⊹' },
    { key: 'queue', label: 'Hàng chờ & bay', icon: '≡', badge: queue.length },
    { key: 'payment', label: 'Thanh toán', icon: '₫', badge: pay.length },
    { key: 'customers', label: 'Khách hàng', icon: '◐' },
  ]
  const cur = pay.find((p) => p.id === paySelId) || null
  let disc = 0; if (cur && payVoucher) { const v = payVoucher.value; disc = v < 1 ? Math.round((cur.priceN || 0) * v) : v }
  const total = cur ? Math.max(0, (cur.priceN || 0) - disc) : 0
  const today = new Date().toLocaleDateString('vi-VN', { weekday: 'long', day: 'numeric', month: 'numeric', year: 'numeric' })
  const receptionName = localStorage.getItem('fullName') || 'Lễ tân'
  const custResetName = (customers.find((c) => c.id === custResetId) || {}).name || ''

  const custView = (() => {
    let arr = customers.filter((c) => {
      const q = custSearch.trim().toLowerCase()
      return !q || (c.name || '').toLowerCase().includes(q) || (c.phone || '').includes(q) || String(c.id).includes(q)
    })
    if (filterTier) arr = arr.filter((c) => String(c.tier || '').toUpperCase() === filterTier)
    if (sortName) arr = [...arr].sort((a, b) => (a.name || '').localeCompare(b.name || '', 'vi') * (sortName === 'az' ? 1 : -1))
    else if (sortPoints) arr = [...arr].sort((a, b) => ((a.points || 0) - (b.points || 0)) * (sortPoints === 'asc' ? 1 : -1))
    return arr
  })()

  return (
      <div style={css('background:#0f0e0c;color:#f4f1ea;min-height:100vh;')}>
        <header style={css('position:sticky;top:0;z-index:50;display:flex;align-items:center;justify-content:space-between;padding:15px 30px;background:rgba(15,14,12,0.92);backdrop-filter:blur(14px);border-bottom:1.5px solid rgba(255,255,255,0.12);')}>
          <div style={css('display:flex;align-items:center;gap:12px;')}>
            <div>
              <div style={css('font-family:var(--font-display);font-size:17px;line-height:1;')}>AutoWash Pro</div>
              <div style={css('font-size:11px;color:#8b8578;margin-top:2px;letter-spacing:1px;')}>TIẾP TÂN · QUẦY LỄ TÂN</div>
            </div>
          </div>
          <div style={css('display:flex;align-items:center;gap:18px;')}>
            <div style={css('text-align:right;')}>
              <div style={css('font-size:13.5px;color:#f4f1ea;')}>{receptionName}</div>
              <div style={css('font-size:11.5px;color:#8b8578;')}>{today}</div>
            </div>
            <div style={css('width:38px;height:38px;border-radius:50%;border:1px solid var(--gold);display:flex;align-items:center;justify-content:center;font-family:var(--font-display);color:var(--gold);')}>{receptionName.charAt(0).toUpperCase()}</div>
            <Link to="/" className="hov-danger" style={css('font-size:13px;color:#8b8578;text-decoration:none;')}>Đăng xuất</Link>
          </div>
        </header>

        <div className="dash-grid" style={css('display:grid;grid-template-columns:230px 1fr;min-height:calc(100vh - 70px);')}>
          <aside className="dash-aside" style={css('border-right:1.5px solid rgba(255,255,255,0.12);padding:26px 16px;display:flex;flex-direction:column;gap:6px;')}>
            {NAV.map((n) => {
              const a = tab === n.key
              return (
                  <button key={n.key} onClick={() => setTab(n.key)} style={css('display:flex;align-items:center;gap:13px;width:100%;text-align:left;padding:13px 15px;border:none;border-radius:11px;font-size:14.5px;cursor:pointer;font-family:var(--font-body);' + (a ? 'background:rgba(200,162,83,0.12);color:var(--gold);font-weight:600;' : 'background:transparent;color:#a39e92;font-weight:500;'))}>
                    <span style={css('font-size:16px;width:20px;text-align:center;color:' + (a ? 'var(--gold)' : '#6f6a5e') + ';')}>{n.icon}</span><span>{n.label}</span>
                    {n.badge ? <span style={css('margin-left:auto;background:var(--gold);color:#100f0c;font-size:11px;font-weight:700;min-width:20px;height:20px;border-radius:10px;display:flex;align-items:center;justify-content:center;padding:0 6px;')}>{n.badge}</span> : null}
                  </button>
              )
            })}
            <div style={css('margin-top:auto;border:1.5px solid rgba(255,255,255,0.12);border-radius:12px;padding:16px;')}>
              <div style={css('font-size:12px;color:#8b8578;margin-bottom:10px;letter-spacing:0.5px;')}>TÌNH TRẠNG KHU RỬA</div>
              {bays.map((b) => (
                  <div key={b.name} style={css('display:flex;align-items:center;gap:9px;margin-bottom:8px;')}>
                    <span style={css('width:9px;height:9px;border-radius:50%;background:' + (b.busy ? 'var(--gold)' : '#3a4a3a') + ';' + (b.busy ? '' : 'border:1px solid #4a6a4a;'))}></span>
                    <span style={css('font-size:12.5px;color:#c4bfb2;')}>{b.name}</span>
                    <span style={css('margin-left:auto;font-size:11.5px;color:#8b8578;')}>{b.state}</span>
                  </div>
              ))}
            </div>
          </aside>

          <main style={css('padding:32px 38px 70px;')}>
            {tab === 'checkin' && (
                <div style={css('animation:fadeUp .4s ease both;')}>
                  <h1 style={css('font-family:var(--font-display);font-size:28px;margin:0 0 4px;font-weight:500;')}>Check-in khách hàng</h1>
                  <p style={css('font-size:14px;color:#8b8578;margin:0 0 26px;')}>Tìm kiếm bằng số điện thoại hoặc đặt lịch cho khách vãng lai.</p>
                  <div className="two-col" style={css('display:grid;grid-template-columns:340px 1fr;gap:18px;')}>
                    <div style={css('border:1.5px solid rgba(255,255,255,0.16);border-radius:16px;padding:24px;display:flex;flex-direction:column;align-items:center;')}>
                      <div style={css('font-size:13px;color:#a39e92;align-self:flex-start;margin-bottom:16px;letter-spacing:0.5px;')}>QUÉT MÃ QR</div>
                      <div style={css('position:relative;width:200px;height:200px;border-radius:14px;background:#0a0908;border:1.5px solid rgba(200,162,83,0.3);overflow:hidden;display:flex;align-items:center;justify-content:center;')}>
                        <div style={css('position:absolute;left:6%;right:6%;height:2px;background:var(--gold);box-shadow:0 0 12px var(--gold);animation:scanline 2.4s ease-in-out infinite;')}></div>
                        <span style={css('font-family:monospace;font-size:11px;color:#5a5347;')}>[ CAMERA QR ]</span>
                      </div>
                      <button onClick={scanQr} className="hov-bright" style={css('margin-top:18px;width:100%;background:var(--gold);border:none;color:#100f0c;padding:13px;border-radius:11px;font-size:14px;font-weight:600;cursor:pointer;')}>Mô phỏng quét QR</button>
                    </div>


                    <div style={css('display:flex;flex-direction:column;gap:18px;')}>
                      <div style={css('border:1.5px solid rgba(255,255,255,0.16);border-radius:16px;padding:24px;')}>
                        <div style={css('font-size:13px;color:#a39e92;margin-bottom:14px;letter-spacing:0.5px;')}>CHECK-IN THỦ CÔNG BẰNG SĐT</div>
                        <div style={css('display:flex;gap:10px;')}>
                          <input value={phoneLookup} onChange={(e) => setPhoneLookup(e.target.value.replace(/[^0-9]/g, ''))} placeholder="Nhập số điện thoại khách..." className="foc-gold" style={css('flex:1;' + inp)} />
                          <button onClick={lookupPhone} style={css('background:transparent;border:1.5px solid var(--gold);color:var(--gold);padding:0 22px;border-radius:11px;font-size:14px;font-weight:600;cursor:pointer;')}>Tra cứu</button>
                        </div>
                        {lookupResult && (
                            <div style={css('margin-top:16px;border:1.5px solid rgba(200,162,83,0.3);border-radius:12px;padding:16px 18px;background:rgba(200,162,83,0.05);display:flex;align-items:center;justify-content:space-between;gap:14px;flex-wrap:wrap;')}>
                              <div>
                                <div style={css('font-family:var(--font-display);font-size:16px;')}>{lookupResult.name} <span style={css('font-size:12.5px;color:var(--gold);')}>· {lookupResult.tier}</span></div>
                                <div style={css('font-size:13px;color:#a39e92;margin-top:3px;')}>{lookupResult.booking} · {lookupResult.car}</div>
                                <div style={css('font-size:12.5px;color:#8b8578;margin-top:2px;')}>{lookupResult.points} điểm tích luỹ</div>
                              </div>
                              <button onClick={confirmCheckin} style={css('background:var(--gold);border:none;color:#100f0c;padding:11px 20px;border-radius:10px;font-size:13.5px;font-weight:600;cursor:pointer;')}>Check-in →</button>
                            </div>
                        )}
                      </div>

                      <div style={css('border:1.5px solid rgba(255,255,255,0.16);border-radius:16px;padding:24px;flex:1;')}>
                        <div style={css('font-size:13px;color:#a39e92;margin-bottom:14px;letter-spacing:0.5px;')}>TẠO LỊCH CHO KHÁCH VÃNG LAI (WALK-IN)</div>
                        <div style={css('display:grid;grid-template-columns:1fr 1fr;gap:12px;')}>
                          <input value={wi.name} onChange={(e) => setWi({ ...wi, name: e.target.value })} placeholder="Họ tên khách" className="foc-gold" style={css(inp)} />
                          <input value={wi.phone} onChange={(e) => setWi({ ...wi, phone: e.target.value.replace(/[^0-9]/g, '') })} placeholder="Số điện thoại" className="foc-gold" style={css(inp)} />
                          <input value={wi.plate} onChange={(e) => setWi({ ...wi, plate: e.target.value })} placeholder="Biển số xe" className="foc-gold" style={css(inp)} />
                          <select value={wi.pkg || ''} onChange={(e) => setWi({ ...wi, pkg: Number(e.target.value) })} className="foc-gold" style={css(inp + 'cursor:pointer;')}>
                            {pkgs.map((p) => <option key={p.id} value={p.id}>{p.name}</option>)}
                          </select>
                        </div>
                        {wi.error && <div style={css('font-size:13px;color:#e08a6a;margin-top:12px;')}>{wi.error}</div>}
                        <button onClick={createWalkin} className="hov-bright" style={css('margin-top:16px;background:var(--gold);border:none;color:#100f0c;padding:13px 28px;border-radius:11px;font-size:14px;font-weight:600;cursor:pointer;')}>Tạo booking &amp; đưa vào hàng chờ</button>
                      </div>
                    </div>
                  </div>
                </div>
            )}

            {tab === 'queue' && (
                <div style={css('animation:fadeUp .4s ease both;')}>
                  <h1 style={css('font-family:var(--font-display);font-size:28px;margin:0 0 4px;font-weight:500;')}>Hàng chờ &amp; phân khu rửa</h1>
                  <p style={css('font-size:14px;color:#8b8578;margin:0 0 26px;')}>Phân xe vào wash bay và theo dõi tiến độ.</p>
                  {queue.length === 0 ? (
                      <div style={css('text-align:center;padding:60px 20px;color:#8b8578;')}>
                        <div style={css('font-size:48px;margin-bottom:16px;')}>≡</div>
                        <div style={css('font-size:16px;')}>Hàng chờ trống</div>
                      </div>
                  ) : (
                      <div style={css('display:flex;flex-direction:column;gap:11px;')}>
                        {queue.map((q, i) => (
                            <div key={q.id} style={css('border:1.5px solid rgba(255,255,255,0.16);border-radius:14px;padding:18px 22px;display:flex;align-items:center;gap:18px;flex-wrap:wrap;')}>
                              <div style={css('width:34px;height:34px;border-radius:9px;background:rgba(200,162,83,0.12);border:1px solid rgba(200,162,83,0.3);display:flex;align-items:center;justify-content:center;font-family:var(--font-display);color:var(--gold);font-size:15px;flex-shrink:0;')}>{i + 1}</div>
                              <div style={css('flex:1;min-width:160px;')}>
                                <div style={css('font-family:var(--font-display);font-size:16px;')}>{q.name} <span style={css('font-size:12px;color:#8b8578;')}>· {q.plate}</span></div>
                                <div style={css('font-size:13px;color:#a39e92;margin-top:3px;')}>{q.pkg} · {q.id}</div>
                              </div>
                              <span style={css(badge(q.status))}>{STATUS_VI[q.status]}</span>
                              <select value={q.bay || bayPlan[q.id] || ''} onChange={(e) => setBay(q.id, e.target.value)} className="foc-gold" style={css('background:#0f0e0c;border:1.5px solid rgba(255,255,255,0.16);border-radius:10px;padding:9px 12px;color:#f4f1ea;font-size:13px;cursor:pointer;min-width:120px;')}>
                                <option value="">— Chọn bay —</option>
                                {bays.map((b) => <option key={b.name} value={b.name}>{b.name}</option>)}
                              </select>
                              <button onClick={() => qCancel(q.id)} className="hov-danger-bg" style={css('background:transparent;border:1px solid rgba(224,138,106,0.3);color:#e08a6a;padding:8px 13px;border-radius:8px;font-size:12.5px;cursor:pointer;')}>Huỷ</button>
                            </div>
                        ))}
                      </div>
                  )}
                </div>
            )}

            {tab === 'payment' && (
                <div style={css('animation:fadeUp .4s ease both;')}>
                  <h1 style={css('font-family:var(--font-display);font-size:28px;margin:0 0 4px;font-weight:500;')}>Thanh toán</h1>
                  <p style={css('font-size:14px;color:#8b8578;margin:0 0 26px;')}>Xác nhận thu tiền các đơn đã hoàn thành.</p>
                  <div className="two-col" style={css('display:grid;grid-template-columns:1fr 360px;gap:18px;align-items:start;')}>
                    <div style={css('display:flex;flex-direction:column;gap:11px;')}>
                      {pay.length === 0 ? (
                          <div style={css('text-align:center;padding:40px 20px;color:#8b8578;')}>Không có đơn nào cần thu tiền.</div>
                      ) : pay.map((p) => (
                          <div key={p.id} onClick={() => { setPaySelId(p.id); setPayVoucher(null); setPayVoucherInput(''); setPayMethod('cash') }} style={css('display:flex;align-items:center;gap:16px;border:1.5px solid ' + (paySelId === p.id ? 'var(--gold)' : 'rgba(255,255,255,0.16)') + ';background:' + (paySelId === p.id ? 'rgba(200,162,83,0.06)' : 'transparent') + ';border-radius:14px;padding:18px 22px;cursor:pointer;transition:all .15s;')}>
                            <div style={css('flex:1;')}>
                              <div style={css('font-family:var(--font-display);font-size:16px;')}>{p.name}</div>
                              <div style={css('font-size:13px;color:#a39e92;margin-top:3px;')}>{p.pkg} · {p.plate} · {p.id}</div>
                            </div>
                            <div style={css('font-family:var(--font-display);font-size:18px;color:var(--gold);')}>{fmtVND(p.priceN || 0)}</div>
                          </div>
                      ))}
                    </div>
                    <div style={css('border:1.5px solid rgba(255,255,255,0.16);border-radius:16px;padding:24px;position:sticky;top:90px;')}>
                      {cur ? (
                          <>
                            <div style={css('font-size:13px;color:#a39e92;margin-bottom:6px;letter-spacing:0.5px;')}>HOÁ ĐƠN · {cur.id}</div>
                            <div style={css('font-family:var(--font-display);font-size:19px;margin-bottom:18px;')}>{cur.name}</div>
                            <div style={css('display:flex;gap:8px;margin-bottom:16px;')}>
                              <input value={payVoucherInput} onChange={(e) => setPayVoucherInput(e.target.value.toUpperCase())} placeholder="Mã voucher" className="foc-gold" style={css('flex:1;background:#0f0e0c;border:1.5px solid rgba(255,255,255,0.14);border-radius:10px;padding:11px 13px;color:#f4f1ea;font-size:13.5px;')} />
                              <button onClick={applyPayVoucher} style={css('background:transparent;border:1.5px solid var(--gold);color:var(--gold);padding:0 16px;border-radius:10px;font-size:13px;font-weight:600;cursor:pointer;')}>Áp dụng</button>
                            </div>
                            <div style={css('display:flex;flex-direction:column;gap:9px;padding:16px 0;border-top:1px solid rgba(255,255,255,0.1);border-bottom:1px solid rgba(255,255,255,0.1);margin-bottom:16px;')}>
                              <div style={css('display:flex;justify-content:space-between;font-size:13.5px;')}><span style={css('color:#8b8578;')}>Tạm tính</span><span style={css('color:#f4f1ea;')}>{fmtVND(cur.priceN || 0)}</span></div>
                              {disc > 0 && <div style={css('display:flex;justify-content:space-between;font-size:13.5px;')}><span style={css('color:#8b8578;')}>Voucher {payVoucher.code}</span><span style={css('color:#6fcf97;')}>− {fmtVND(disc)}</span></div>}
                              <div style={css('display:flex;justify-content:space-between;align-items:baseline;')}><span style={css('color:#c4bfb2;font-size:14.5px;')}>Phải thu</span><span style={css('font-family:var(--font-display);font-size:22px;color:var(--gold);')}>{fmtVND(total)}</span></div>
                            </div>
                            <div style={css('display:flex;gap:9px;margin-bottom:16px;')}>
                              {[['cash', 'Tiền mặt'], ['qr', 'Chuyển khoản QR']].map(([m, label]) => {
                                const on = payMethod === m
                                return (
                                    <button key={m} onClick={() => setPayMethod(m)} style={css('flex:1;padding:11px;border-radius:10px;font-size:13px;font-weight:600;cursor:pointer;font-family:var(--font-body);' + (on ? 'background:rgba(200,162,83,0.12);border:1.5px solid var(--gold);color:var(--gold);' : 'background:transparent;border:1.5px solid rgba(255,255,255,0.14);color:#a39e92;'))}>{label}</button>
                                )
                              })}
                            </div>

                            {/* Ô QR — mẫu, để sẵn móc API */}
                            {payMethod === 'qr' && (
                                <div style={css('display:flex;flex-direction:column;align-items:center;gap:10px;margin-bottom:16px;padding:18px;border:1.5px solid rgba(255,255,255,0.12);border-radius:12px;background:#0a0908;')}>
                                  <img src={'https://api.qrserver.com/v1/create-qr-code/?size=180x180&data=' + encodeURIComponent('AUTOWASH|' + cur.id + '|' + total)} alt="QR thanh toán" style={css('width:180px;height:180px;border-radius:8px;background:#fff;padding:6px;')} />
                                  <div style={css('font-size:12.5px;color:#8b8578;text-align:center;')}>Quét mã để chuyển khoản {fmtVND(total)}</div>
                                </div>
                            )}
                            <div style={css('display:flex;gap:9px;')}>
                              {payMethod === 'cash' && (
                                  <button onClick={doPay} className="hov-bright" style={css('flex:1;background:var(--gold);border:none;color:#100f0c;padding:13px;border-radius:11px;font-size:13.5px;font-weight:600;cursor:pointer;')}>Hoàn tất</button>
                              )}
                              {payMethod === 'qr' && (
                                  <button onClick={doPay} className="hov-bright" style={css('flex:1;background:var(--gold);border:none;color:#100f0c;padding:13px;border-radius:11px;font-size:13.5px;font-weight:600;cursor:pointer;')}>Xác nhận thu tiền</button>
                              )}
                            </div>
                          </>
                      ) : (
                          <div style={css('text-align:center;padding:40px 10px;color:#6f6a5e;font-size:13.5px;')}>Chọn một khách ở danh sách bên trái để lập hoá đơn.</div>
                      )}
                    </div>
                  </div>
                </div>
            )}

            {tab === 'customers' && (
                <div style={css('animation:fadeUp .4s ease both;')}>
                  <div style={css('display:flex;justify-content:space-between;align-items:flex-end;margin-bottom:20px;flex-wrap:wrap;gap:14px;')}>
                    <div><h1 style={css('font-family:var(--font-display);font-size:28px;margin:0 0 4px;font-weight:500;')}>Hỗ trợ khách hàng</h1><p style={css('font-size:14px;color:#8b8578;margin:0;')}></p></div>
                    <button onClick={openCreateCust} className="hov-bright" style={css('background:var(--gold);border:none;color:#100f0c;padding:12px 22px;border-radius:30px;font-size:14px;font-weight:600;cursor:pointer;')}>+ Tạo tài khoản khách</button>
                  </div>

                  {/* ô tìm kiếm */}
                  <div style={css('position:relative;margin-bottom:18px;max-width:420px;')}>
                    <input value={custSearch} onChange={(e) => setCustSearch(e.target.value)} placeholder="Tìm theo tên, SĐT, mã khách..." className="foc-gold" style={css(inp + 'width:100%;padding-right:40px;')} />
                    {custSearch && <button onClick={() => setCustSearch('')} style={css('position:absolute;right:12px;top:50%;transform:translateY(-50%);background:transparent;border:none;color:#8b8578;font-size:18px;cursor:pointer;line-height:1;')}>×</button>}
                  </div>

                  <div style={css('border:1.5px solid rgba(255,255,255,0.16);border-radius:14px;overflow:visible;')}>
                    <div style={css('display:grid;grid-template-columns:1.4fr 1fr 0.9fr 0.9fr 130px;padding:14px 22px;background:rgba(255,255,255,0.03);border-radius:12px 12px 0 0;font-size:12px;letter-spacing:0.5px;align-items:center;')}>
                      {/* KHÁCH HÀNG - sort tên */}
                      <div style={css('position:relative;')} onClick={(e) => e.stopPropagation()}>
                        <button onClick={() => setOpenMenu(openMenu === 'cname' ? null : 'cname')} style={css(hBtn(!!sortName))}>Khách hàng <span style={css('font-size:9px;')}>▼</span></button>
                        {openMenu === 'cname' && (
                            <div style={css(hMenu)}>
                              <button onClick={() => { setSortName('az'); setSortPoints(''); setOpenMenu(null) }} style={css(hItem(sortName === 'az'))}>A → Z</button>
                              <button onClick={() => { setSortName('za'); setSortPoints(''); setOpenMenu(null) }} style={css(hItem(sortName === 'za'))}>Z → A</button>
                              <button onClick={() => { setSortName(''); setOpenMenu(null) }} style={css(hItem(!sortName))}>Bỏ sắp xếp</button>
                            </div>
                        )}
                      </div>

                      <span style={css('font-size:12px;color:#8b8578;text-transform:uppercase;')}>Số điện thoại</span>

                      {/* HẠNG - lọc */}
                      <div style={css('position:relative;')} onClick={(e) => e.stopPropagation()}>
                        <button onClick={() => setOpenMenu(openMenu === 'tier' ? null : 'tier')} style={css(hBtn(!!filterTier))}>Hạng <span style={css('font-size:9px;')}>▼</span></button>
                        {openMenu === 'tier' && (
                            <div style={css(hMenu)}>
                              {[['', 'Tất cả'], ['MEMBER', 'Member'], ['SILVER', 'Silver'], ['GOLD', 'Gold'], ['PLATINUM', 'Platinum']].map(([k, l]) => (
                                  <button key={k} onClick={() => { setFilterTier(k); setOpenMenu(null) }} style={css(hItem(filterTier === k))}>{l}</button>
                              ))}
                            </div>
                        )}
                      </div>

                      {/* ĐIỂM - sort */}
                      <div style={css('position:relative;')} onClick={(e) => e.stopPropagation()}>
                        <button onClick={() => setOpenMenu(openMenu === 'points' ? null : 'points')} style={css(hBtn(!!sortPoints))}>Điểm <span style={css('font-size:9px;')}>▼</span></button>
                        {openMenu === 'points' && (
                            <div style={css(hMenu)}>
                              <button onClick={() => { setSortPoints('asc'); setSortName(''); setOpenMenu(null) }} style={css(hItem(sortPoints === 'asc'))}>Thấp → Cao</button>
                              <button onClick={() => { setSortPoints('desc'); setSortName(''); setOpenMenu(null) }} style={css(hItem(sortPoints === 'desc'))}>Cao → Thấp</button>
                              <button onClick={() => { setSortPoints(''); setOpenMenu(null) }} style={css(hItem(!sortPoints))}>Bỏ sắp xếp</button>
                            </div>
                        )}
                      </div>

                      <span style={css('font-size:12px;color:#8b8578;text-transform:uppercase;')}>Thao tác</span>
                    </div>
                    {custView.length === 0 ? (
                        <div style={css('padding:30px;text-align:center;color:#8b8578;font-size:13.5px;')}>{customers.length === 0 ? 'Chưa có khách trong bộ nhớ.' : 'Không tìm thấy khách phù hợp.'}</div>
                    ) : custView.map((c) => (
                        <div key={c.id} style={css('display:grid;grid-template-columns:1.4fr 1fr 0.9fr 0.9fr 130px;padding:15px 22px;border-top:1px solid rgba(255,255,255,0.07);align-items:center;')}>
                          <div style={css('display:flex;align-items:center;gap:11px;')}>
                            <div style={css('width:34px;height:34px;border-radius:50%;background:rgba(200,162,83,0.12);border:1px solid rgba(200,162,83,0.25);display:flex;align-items:center;justify-content:center;font-family:var(--font-display);color:var(--gold);font-size:13px;flex-shrink:0;')}>{(c.name.trim()[0] || '?').toUpperCase()}</div>
                            <span style={css('font-size:14px;color:#f4f1ea;')}>{c.name}</span>
                          </div>
                          <span style={css('font-size:13.5px;color:#a39e92;')}>{c.phone}</span>
                          <span style={css('font-size:13px;color:var(--gold);')}>{c.tier}</span>
                          <span style={css('font-size:13.5px;color:#c4bfb2;')}>{(c.points || 0).toLocaleString('vi-VN')}</span>
                          <button onClick={() => openResetPw(c.id)} className="hov-border-gold" style={css('justify-self:start;background:transparent;border:1px solid rgba(255,255,255,0.16);color:#c4bfb2;padding:7px 13px;border-radius:8px;font-size:12.5px;cursor:pointer;')}>Đổi mật khẩu</button>
                        </div>
                    ))}
                  </div>
                </div>
            )}
          </main>
        </div>

        {custModal && (
            <div onClick={() => setCustModal(null)} style={css('position:fixed;inset:0;z-index:100;background:rgba(8,7,6,0.8);backdrop-filter:blur(6px);display:flex;align-items:center;justify-content:center;padding:24px;')}>
              <div onClick={(e) => e.stopPropagation()} style={css('width:100%;max-width:430px;border:1.5px solid rgba(255,255,255,0.14);border-radius:18px;background:#16140f;padding:30px 32px;animation:fadeUp .35s ease both;')}>
                <h3 style={css('font-family:var(--font-display);font-size:21px;margin:0 0 6px;font-weight:500;')}>{custModal === 'create' ? 'Tạo tài khoản khách hàng' : 'Đặt lại mật khẩu'}</h3>
                {custModal === 'create' ? (
                    <div style={css('display:flex;flex-direction:column;gap:15px;margin-top:18px;')}>
                      <input value={custForm.name} onChange={(e) => setCustForm({ ...custForm, name: e.target.value })} placeholder="Họ và tên khách" className="foc-gold" style={css(inp)} />
                      <input value={custForm.phone} onChange={(e) => setCustForm({ ...custForm, phone: e.target.value })} placeholder="Số điện thoại" className="foc-gold" style={css(inp)} />
                      <input value={custForm.password} onChange={(e) => setCustForm({ ...custForm, password: e.target.value })} type="password" placeholder="Mật khẩu khởi tạo (≥ 6 ký tự)" className="foc-gold" style={css(inp)} />
                    </div>
                ) : (
                    <div style={css('display:flex;flex-direction:column;gap:15px;margin-top:18px;')}>
                      <p style={css('font-size:13.5px;color:#a39e92;margin:0;')}>Đặt lại mật khẩu cho <span style={css('color:#f4f1ea;')}>{custResetName}</span></p>
                      <input value={custForm.password} onChange={(e) => setCustForm({ ...custForm, password: e.target.value })} type="password" placeholder="Mật khẩu mới (≥ 6 ký tự)" className="foc-gold" style={css(inp)} />
                    </div>
                )}
                {custError && <div style={css('font-size:13px;color:#e08a6a;margin-top:14px;')}>{custError}</div>}
                <div style={css('display:flex;gap:10px;margin-top:20px;')}>
                  <button onClick={() => setCustModal(null)} style={css('flex:1;background:transparent;border:1.5px solid rgba(255,255,255,0.18);color:#c4bfb2;padding:13px;border-radius:11px;font-size:14px;cursor:pointer;')}>Huỷ</button>
                  <button onClick={saveCust} style={css('flex:1;background:var(--gold);border:none;color:#100f0c;padding:13px;border-radius:11px;font-size:14px;font-weight:600;cursor:pointer;')}>Lưu</button>
                </div>
              </div>
            </div>
        )}

        <Toast message={toast} />
      </div>
  )
}