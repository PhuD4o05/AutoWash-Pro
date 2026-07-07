import { Link } from 'react-router-dom'
import { css } from '../lib/css'

const NAV = [
  ['#services', 'Dịch vụ'], ['#pricing', 'Bảng giá'], ['#gallery', 'Thành quả'],
  ['#reviews', 'Đánh giá'], ['#contact', 'Liên hệ'],
]

export default function Navbar({ user, onGoAuth, onOpenBooking, onLogout }) {
  const initial = user ? (user.name.trim()[0] || 'K').toUpperCase() : ''
  return (
    <nav style={css('position:sticky;top:0;z-index:60;display:flex;align-items:center;justify-content:space-between;padding:16px 6vw;background:rgba(15,14,12,0.82);backdrop-filter:blur(14px);border-bottom:1px solid rgba(255,255,255,0.07);')}>
      <a href="#top" style={css('display:flex;align-items:center;gap:13px;text-decoration:none;cursor:pointer;')}>
        <span style={css("font-family:var(--font-display);font-size:20px;letter-spacing:0.5px;color:#f4f1ea;")}>AutoWash Pro</span>
      </a>

      <div className="nav-links" style={css('display:flex;gap:32px;align-items:center;')}>
        {NAV.map(([href, label]) => (
          <a key={href} href={href} className="hov-text" style={css('text-decoration:none;color:#c4bfb2;font-size:14.5px;font-weight:500;cursor:pointer;transition:color .15s;')}>{label}</a>
        ))}
      </div>

      <div style={css('display:flex;align-items:center;gap:14px;')}>
        {user ? (
          <div style={css('display:flex;align-items:center;gap:14px;')}>
            <Link to="/customer" className="hov-gold" style={css('display:flex;align-items:center;gap:9px;text-decoration:none;transition:opacity .15s;')}>
              <div style={css('width:32px;height:32px;border-radius:50%;border:1px solid var(--gold);display:flex;align-items:center;justify-content:center;font-family:var(--font-display);color:var(--gold);font-size:14px;')}>{initial}</div>
              <span style={css('font-size:14px;color:#e9e5db;')}>{user.name}</span>
            </Link>
            <button onClick={onLogout} style={css('background:none;border:none;color:#8b8578;font-size:13px;cursor:pointer;text-decoration:underline;text-underline-offset:3px;')}>Đăng xuất</button>
          </div>
        ) : (
          <button onClick={onGoAuth} className="hov-border-gold hov-gold" style={css('background:none;border:1px solid rgba(255,255,255,0.18);color:#e9e5db;padding:10px 20px;border-radius:30px;font-size:14px;font-weight:500;cursor:pointer;transition:all .15s;')}>Đăng nhập</button>
        )}
        <button onClick={onOpenBooking} className="hov-bright" style={css('background:var(--gold);border:none;color:#100f0c;padding:11px 22px;border-radius:30px;font-size:14px;font-weight:600;cursor:pointer;')}>Đặt lịch</button>
      </div>
    </nav>
  )
}
