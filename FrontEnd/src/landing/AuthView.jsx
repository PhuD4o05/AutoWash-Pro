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
          </div>
        </div>
      </div>
  )
}