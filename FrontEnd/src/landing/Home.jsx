import { css } from '../lib/css'

export default function Home({ services, packages, gallery, reviews, onOpenBooking, onSelectPackage }) {
  return (
    <div>
      {/* ===================== HERO (ảnh nền) ===================== */}
      <section className="hero-grid" style={css('display:grid;grid-template-columns:1.05fr 0.95fr;gap:48px;align-items:center;padding:80px 6vw 70px;max-width:1500px;margin:0 auto;background:url("/img/hero.webp") center / cover no-repeat;')}>
        <div style={css('animation:fadeUp 0.7s ease both;')}>
          <div style={css('display:inline-flex;align-items:center;gap:12px;margin-bottom:30px;')}>
            <span style={css('width:26px;height:1px;background:var(--gold);')}></span>
            <span style={css('font-size:11.5px;letter-spacing:3px;text-transform:uppercase;color:var(--gold);')}>Detailing &amp; Chăm sóc xe cao cấp</span>
          </div>
          <h1 style={css('font-family:var(--font-display);font-size:clamp(40px,5.2vw,66px);line-height:1.08;margin:0 0 24px;font-weight:500;letter-spacing:-0.5px;')}>
            Chăm xe bằng sự<br /><span style={css('color:rgb(200,162,83);')}>tinh tế</span>
          </h1>
          <p style={css('font-size:16.5px;line-height:1.75;color:#fffefe;max-width:460px;margin:0 0 38px;')}>
            Dịch vụ rửa xe, vệ sinh nội thất và phủ ceramic chuẩn detailing — tỉ mỉ trong từng chi tiết, bảo vệ giá trị xe của bạn theo thời gian.
          </p>
          <div style={css('display:flex;gap:16px;flex-wrap:wrap;align-items:center;')}>
            <button onClick={onOpenBooking} className="hov-bright" style={css('background:var(--gold);border:none;color:#100f0c;padding:15px 34px;border-radius:32px;font-size:15px;font-weight:600;cursor:pointer;')}>Đặt lịch ngay</button>
            <a href="#pricing" className="hov-gold" style={css('text-decoration:none;display:inline-flex;align-items:center;gap:8px;font-size:15px;font-weight:500;color:#c4bfb2;cursor:pointer;')}>Xem bảng giá →</a>
          </div>
          <div className="hero-stats" style={css('display:flex;gap:56px;margin-top:54px;')}>
            {[['12K+', 'Lượt xe phục vụ'], ['9 năm', 'Kinh nghiệm'], ['4.9★', 'Đánh giá trung bình']].map(([n, l]) => (
              <div key={l}>
                <div style={css('font-family:var(--font-display);font-size:32px;color:#f4f1ea;')}>{n}</div>
                <div style={css('font-size:12.5px;color:#ffffff;margin-top:4px;letter-spacing:0.3px;')}>{l}</div>
              </div>
            ))}
          </div>
        </div>
        <div></div>
      </section>

      {/* ===================== SERVICES ===================== */}
      <section id="services" style={css('padding:104px 6vw;max-width:1320px;margin:0 auto;')}>
        <div style={css('text-align:center;margin-bottom:64px;')}>
          <div style={css('font-size:11.5px;letter-spacing:3px;text-transform:uppercase;color:var(--gold);margin-bottom:16px;')}>Dịch vụ</div>
          <h2 style={css('font-family:var(--font-display);font-size:clamp(30px,3.4vw,44px);margin:0;font-weight:500;')}>Chăm sóc toàn diện cho xe của bạn</h2>
        </div>
        <div className="grid-3" style={css('display:grid;grid-template-columns:repeat(3,1fr);gap:14px;')}>
          {services.map((sv, i) => (
            <div key={i} className="hov-border-gold-soft" style={css('padding:38px 32px;border:1.5px solid rgba(255,255,255,0.22);border-radius:14px;background:transparent;transition:border-color .2s;')}>
              <div style={css('font-size:23px;color:var(--gold);margin-bottom:24px;')}>{sv.icon}</div>
              <h3 style={css('font-family:var(--font-display);font-size:20px;margin:0 0 11px;font-weight:500;')}>{sv.title}</h3>
              <p style={css('font-size:14.5px;line-height:1.7;color:#9b9689;margin:0;')}>{sv.desc}</p>
            </div>
          ))}
        </div>
      </section>

      {/* ===================== PRICING ===================== */}
      <section id="pricing" style={css('padding:40px 6vw 104px;max-width:1320px;margin:0 auto;')}>
        <div style={css('text-align:center;margin-bottom:64px;')}>
          <div style={css('font-size:11.5px;letter-spacing:3px;text-transform:uppercase;color:var(--gold);margin-bottom:16px;')}>Bảng giá</div>
          <h2 style={css('font-family:var(--font-display);font-size:clamp(30px,3.4vw,44px);margin:0;font-weight:500;')}>Lựa chọn gói phù hợp với bạn</h2>
        </div>
        <div className="grid-4" style={css('display:grid;grid-template-columns:repeat(4,1fr);gap:14px;align-items:stretch;')}>
          {packages.map((pkg) => (
            <div key={pkg.id} style={css('position:relative;display:flex;flex-direction:column;padding:40px 30px 34px;border-radius:14px;' + (pkg.popular ? 'border:1.5px solid var(--gold);background:rgba(200,162,83,0.04);' : 'border:1.5px solid rgba(255,255,255,0.22);background:transparent;'))}>
              {pkg.popular && (
                <div style={css('position:absolute;top:-11px;left:50%;transform:translateX(-50%);background:var(--gold);color:#100f0c;font-size:10px;font-weight:700;letter-spacing:1.5px;padding:5px 14px;border-radius:20px;text-transform:uppercase;white-space:nowrap;')}>Phổ biến</div>
              )}
              <h3 style={css('font-family:var(--font-display);font-size:22px;margin:0 0 6px;font-weight:500;')}>{pkg.name}</h3>
              <p style={css('font-size:13.5px;color:#a39e92;margin:0 0 20px;min-height:38px;line-height:1.5;')}>{pkg.desc}</p>
              <div style={css('display:flex;align-items:baseline;gap:6px;margin-bottom:26px;')}>
                <span style={css('font-size:13px;color:#8b8578;')}>{pkg.period}</span>
                <span style={css('font-family:var(--font-display);font-size:30px;color:#f4f1ea;')}>{pkg.price}</span>
              </div>
              <div style={css('display:flex;flex-direction:column;gap:11px;margin-bottom:28px;')}>
                {pkg.features.map((ft, j) => (
                  <div key={j} style={css('display:flex;align-items:flex-start;gap:10px;font-size:14px;color:#c4bfb2;line-height:1.4;')}>
                    <span style={css('color:var(--gold);font-size:13px;margin-top:2px;')}>✓</span><span>{ft}</span>
                  </div>
                ))}
              </div>
              <button onClick={() => onSelectPackage(pkg.id)} className={pkg.popular ? 'hov-bright' : 'hov-border-gold'} style={css('margin-top:auto;' + (pkg.popular ? 'background:var(--gold);border:none;color:#100f0c;' : 'background:transparent;border:1px solid rgba(255,255,255,0.16);color:#f4f1ea;') + 'padding:13px;border-radius:10px;font-size:14px;font-weight:600;cursor:pointer;transition:all .2s;')}>Đặt gói này</button>
            </div>
          ))}
        </div>
      </section>

      {/* ===================== GALLERY (ảnh thật) ===================== */}
      <section id="gallery" style={css('padding:40px 6vw 90px;max-width:1400px;margin:0 auto;')}>
        <div style={css('text-align:center;margin-bottom:54px;')}>
          <div style={css('font-size:11.5px;letter-spacing:3px;text-transform:uppercase;color:var(--gold);margin-bottom:16px;')}>Thành quả thực tế</div>
          <h2 style={css('font-family:var(--font-display);font-size:clamp(30px,3.4vw,44px);margin:0;font-weight:500;')}>Kết quả sau khi chăm sóc</h2>
        </div>
        <div className="grid-3" style={css('display:grid;grid-template-columns:repeat(3,1fr);gap:20px;')}>
          {gallery.map((g, i) => (
            <div key={i} style={css('position:relative;border:1px solid rgba(255,255,255,0.08);border-radius:16px;overflow:hidden;height:320px;background:url("' + g.img + '") center / cover no-repeat;')}>
              <div style={css('position:absolute;left:0;right:0;bottom:0;padding:40px 18px 16px;background:linear-gradient(180deg,transparent,rgba(8,7,6,0.92));pointer-events:none;')}>
                <div style={css('font-size:15px;color:#f4f1ea;font-weight:600;')}>{g.title}</div>
                <div style={css('font-size:13px;color:#c4bfb2;margin-top:2px;')}>{g.sub}</div>
              </div>
            </div>
          ))}
        </div>
      </section>

      {/* ===================== REVIEWS ===================== */}
      <section id="reviews" style={css('padding:40px 6vw 90px;background:linear-gradient(180deg,#0f0e0c,#131008,#0f0e0c);')}>
        <div style={css('max-width:1400px;margin:0 auto;')}>
          <div style={css('text-align:center;margin-bottom:54px;')}>
            <div style={css('font-size:11.5px;letter-spacing:3px;text-transform:uppercase;color:var(--gold);margin-bottom:16px;')}>Khách hàng nói gì</div>
            <h2 style={css('font-family:var(--font-display);font-size:clamp(30px,3.4vw,44px);margin:0;font-weight:500;')}>Được tin tưởng bởi hàng nghìn chủ xe</h2>
          </div>
          <div className="grid-3" style={css('display:grid;grid-template-columns:repeat(3,1fr);gap:20px;')}>
            {reviews.map((rv, i) => (
              <div key={i} style={css('padding:38px 34px;border:1.5px solid rgba(255,255,255,0.22);border-radius:14px;background:transparent;display:flex;flex-direction:column;')}>
                <div style={css('color:var(--gold);font-size:14px;letter-spacing:4px;margin-bottom:22px;')}>{rv.stars}</div>
                <p style={css('font-size:16px;line-height:1.75;color:#cfcabe;margin:0 0 28px;flex:1;')}>“{rv.text}”</p>
                <div style={css('display:flex;align-items:center;gap:13px;')}>
                  <div style={css('width:42px;height:42px;border-radius:50%;background:rgba(200,162,83,0.1);display:flex;align-items:center;justify-content:center;font-family:var(--font-display);color:var(--gold);')}>{rv.initial}</div>
                  <div><div style={css('font-size:14.5px;color:#f4f1ea;font-weight:500;')}>{rv.name}</div></div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* ===================== CONTACT (ảnh nền) ===================== */}
      <section id="contact" style={css('padding:90px 6vw;max-width:1400px;margin:0 auto;background:url("/img/contact.jpg") center / cover no-repeat;overflow:visible;')}>
        <div>
          <div style={css('font-size:11.5px;letter-spacing:3px;text-transform:uppercase;color:#ffbe37;margin-bottom:16px;')}>Liên hệ &amp; địa chỉ</div>
          <h2 style={css('font-family:var(--font-display);font-size:clamp(28px,3.2vw,42px);margin:0 0 30px;font-weight:500;')}>Trung tâm chăm sóc xe<div>AutoWash Pro</div></h2>
          <div style={css('display:flex;flex-direction:column;gap:22px;')}>
            {[
              ['⌖', 'Địa chỉ trung tâm', '12 Lê Lợi, Phường Bến Nghé, Quận 1, TP. Hồ Chí Minh'],
              ['☏', 'Hotline đặt lịch', '1900 6789 · 0901 234 567'],
              ['◷', 'Giờ mở cửa', 'Thứ 2 – Chủ Nhật: 7:30 – 20:00'],
            ].map(([icon, label, value]) => (
              <div key={label} style={css('display:flex;gap:16px;align-items:flex-start;')}>
                <div style={css('font-size:18px;color:var(--gold);flex-shrink:0;width:20px;line-height:1.5;')}>{icon}</div>
                <div>
                  <div style={css('font-size:13px;color:#8b8578;margin-bottom:3px;')}>{label}</div>
                  <div style={css('font-size:15.5px;color:#ffffff;line-height:1.5;')}>{value}</div>
                </div>
              </div>
            ))}
          </div>
          <button onClick={onOpenBooking} className="hov-bright" style={css('margin-top:36px;background:var(--gold);border:none;color:#100f0c;padding:15px 34px;border-radius:32px;font-size:15px;font-weight:600;cursor:pointer;')}>Đặt lịch ngay</button>
        </div>
      </section>

      {/* ===================== FOOTER ===================== */}
      <footer style={css('border-top:1px solid rgba(255,255,255,0.07);padding:50px 6vw 40px;')}>
        <div style={css('max-width:1400px;margin:0 auto;display:flex;justify-content:space-between;align-items:center;flex-wrap:wrap;gap:20px;')}>
          <div style={css('display:flex;align-items:center;gap:12px;')}>
            <span style={css('font-family:var(--font-display);font-size:18px;')}>AutoWash Pro</span>
          </div>
          <div style={css('font-size:13.5px;color:#8b8578;')}>© 2026 AutoWash Pro · Detailing &amp; Chăm sóc xe cao cấp</div>
        </div>
      </footer>
    </div>
  )
}
