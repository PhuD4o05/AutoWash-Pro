import { css } from '../lib/css'

const inputStyle = 'width:100%;background:#0f0e0c;border:1px solid rgba(255,255,255,0.12);border-radius:12px;padding:14px 16px;color:#f4f1ea;font-size:15px;'

export default function AuthView({
                                   authTab, authName, authEmail, authPhone, authPassword, authError,
                                   onAuthName, onAuthEmail, onAuthPhone, onAuthPassword,
                                   onSetLogin, onSetRegister, onSubmit,
                                 }) {
  const isLogin = authTab === 'login'
  const isRegister = authTab === 'register'
  const tabActive = 'border:none;border-radius:9px;padding:11px;font-size:14.5px;font-weight:600;cursor:pointer;background:var(--gold);color:#100f0c;'
  const tabIdle = 'border:none;border-radius:9px;padding:11px;font-size:14.5px;font-weight:600;cursor:pointer;background:transparent;color:#a39e92;'

  return (
      <div style={css('min-height:calc(100vh - 73px);display:flex;align-items:center;justify-content:center;padding:50px 6vw;position:relative;')}>
        <div style={css('position:absolute;inset:0;background:url("/img/auth.jpg") center / cover no-repeat;pointer-events:none;')}></div>
        <div style={css('width:100%;max-width:430px;border:1px solid rgba(255,255,255,0.09);border-radius:22px;background:linear-gradient(180deg,#17150f,#121009);padding:40px 38px;position:relative;z-index:2;box-shadow:0 30px 80px rgba(0,0,0,0.5);animation:fadeUp 0.5s ease both;')}>
          <div style={css('text-align:center;margin-bottom:28px;')}>
            <h2 style={css('font-family:var(--font-display);font-size:27px;margin:0 0 6px;font-weight:500;')}>{isRegister ? 'Tạo tài khoản mới' : 'Chào mừng trở lại'}</h2>
            <p style={css('font-size:14px;color:#a39e92;margin:0;')}>{isRegister ? 'Đăng ký bằng email & số điện thoại' : 'Đăng nhập bằng email hoặc số điện thoại'}</p>
          </div>

          <div style={css('display:grid;grid-template-columns:1fr 1fr;gap:6px;background:#0f0e0c;border:1px solid rgba(255,255,255,0.07);border-radius:13px;padding:5px;margin-bottom:28px;')}>
            <button onClick={onSetLogin} style={css(isLogin ? tabActive : tabIdle)}>Đăng nhập</button>
            <button onClick={onSetRegister} style={css(isRegister ? tabActive : tabIdle)}>Đăng ký</button>
          </div>

          <div style={css('display:flex;flex-direction:column;gap:18px;')}>
            {isRegister && (
                <div>
                  <label style={css('display:block;font-size:13px;color:#a39e92;margin-bottom:8px;')}>Họ và tên</label>
                  <input value={authName} onChange={onAuthName} placeholder="Nguyễn Văn A" className="foc-gold" style={css(inputStyle)} />
                </div>
            )}

            {isRegister ? (
                <>
                  <div>
                    <label style={css('display:block;font-size:13px;color:#a39e92;margin-bottom:8px;')}>Email</label>
                    <input value={authEmail} onChange={onAuthEmail} type="email" placeholder="email@vidu.com" className="foc-gold" style={css(inputStyle)} />
                  </div>
                  <div>
                    <label style={css('display:block;font-size:13px;color:#a39e92;margin-bottom:8px;')}>Số điện thoại</label>
                    <input value={authPhone} onChange={onAuthPhone} inputMode="numeric" placeholder="0912345678" className="foc-gold" style={css(inputStyle)} />
                  </div>
                </>
            ) : (
                <div>
                  <label style={css('display:block;font-size:13px;color:#a39e92;margin-bottom:8px;')}>Email hoặc Số điện thoại</label>
                  <input value={authPhone} onChange={onAuthPhone} placeholder="email@vidu.com hoặc 0912345678" className="foc-gold" style={css(inputStyle)} />
                </div>
            )}

            <div>
              <label style={css('display:block;font-size:13px;color:#a39e92;margin-bottom:8px;')}>Mật khẩu</label>
              <input value={authPassword} onChange={onAuthPassword} type="password" placeholder="••••••" className="foc-gold" style={css(inputStyle)} />
            </div>
            {authError && (
                <div style={css('font-size:13px;color:#e08a6a;background:rgba(224,138,106,0.1);border:1px solid rgba(224,138,106,0.25);border-radius:10px;padding:10px 13px;')}>{authError}</div>
            )}
            <button onClick={onSubmit} className="hov-bright-strong" style={css('background:linear-gradient(135deg,var(--gold-light),var(--gold));border:none;color:#100f0c;padding:15px;border-radius:12px;font-size:15px;font-weight:700;cursor:pointer;margin-top:4px;')}>{isRegister ? 'Tạo tài khoản' : 'Đăng nhập'}</button>
            
            <div style={css('display:flex;align-items:center;gap:12px;margin:8px 0 2px;')}>
              <div style={css('flex:1;height:1px;background:rgba(255,255,255,0.08);')}></div>
              <span style={css('font-size:12px;color:#6f6a5e;text-transform:uppercase;letter-spacing:0.5px;')}>Hoặc</span>
              <div style={css('flex:1;height:1px;background:rgba(255,255,255,0.08);')}></div>
            </div>

            <button onClick={() => alert('Đăng nhập Gmail (OAuth2) đang được cấu hình cùng Backend...')} className="hov-border-gold" style={css('background:transparent;border:1.5px solid rgba(255,255,255,0.14);color:#f4f1ea;padding:13px;border-radius:12px;font-size:14.5px;font-weight:600;cursor:pointer;display:flex;align-items:center;justify-content:center;gap:10px;transition:all 0.15s;')}>
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" fill="#4285F4"/>
                <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" fill="#34A853"/>
                <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.06H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.94l2.85-2.22.81-.63z" fill="#FBBC05"/>
                <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.06l3.66 2.84c.87-2.6 3.3-4.52 6.16-4.52z" fill="#EA4335"/>
              </svg>
              Đăng nhập bằng Google
            </button>
          </div>
        </div>
      </div>
  )
}