import { css } from '../lib/css'
import { PKGS, NONE, ADDONS, TIMES, VEHICLES } from './landingData'
import { fmtVND, fmtDate } from '../lib/format'

const pkgById = (id) => (id === 'none' ? NONE : PKGS.find((p) => p.id === id) || null)
const addonById = (id) => ADDONS.find((a) => a.id === id) || null

const STEP_TITLES = { 1: 'Chọn gói combo', 2: 'Thêm dịch vụ lẻ', 3: 'Thời gian', 4: 'Thông tin liên hệ', 5: 'Xác nhận lịch hẹn' }
const NEXT_LABELS = { 1: 'Tiếp tục', 2: 'Tiếp tục', 3: 'Tiếp tục', 4: 'Tiếp tục', 5: 'Hoàn tất' }
const BACK_LABELS = { 1: 'Huỷ', 2: 'Quay lại', 3: 'Quay lại', 4: 'Quay lại', 5: 'Đóng' }

const chip = (active) => 'padding:10px 16px;border-radius:30px;font-size:14px;cursor:pointer;font-weight:500;transition:all .15s;' +
  (active ? 'background:var(--gold);border:1px solid var(--gold);color:#100f0c;font-weight:600;' : 'background:transparent;border:1px solid rgba(255,255,255,0.14);color:#c4bfb2;')

const inputStyle = 'width:100%;background:#0f0e0c;border:1px solid rgba(255,255,255,0.12);border-radius:12px;padding:14px 16px;color:#f4f1ea;font-size:15px;'

export default function BookingModal({
  step, selectedPkgId, selectedAddons, bVehicle, bDate, bTime, bBranch, bName, bPhone, bNote, bookingError,
  onClose, onPickPackage, onToggleAddon, onNext, onPrev,
  onDate, onBranch, onName, onPhone, onNote, onVehicle, onTime,
}) {
  const combo = pkgById(selectedPkgId)
  const sel = combo
  const comboName = combo ? combo.name : '—'
  const comboPrice = combo ? combo.priceN : 0
  const effAddons = selectedAddons.filter((id) => !(combo && combo.includes.indexOf(id) !== -1))
  const addonsTotal = effAddons.reduce((t, id) => t + (addonById(id)?.priceN || 0), 0)
  const grandTotal = comboPrice + addonsTotal
  const summaryAddons = effAddons.map((id) => { const a = addonById(id); return { name: a.name, priceLabel: '+ ' + fmtVND(a.priceN) } })

  const progressBars = [1, 2, 3, 4, 5].map((n) => 'flex:1;height:4px;border-radius:3px;background:' + (n <= step ? 'linear-gradient(90deg,var(--gold-light),var(--gold))' : 'rgba(255,255,255,0.1)') + ';')

  const errorBox = (
    <div style={css('font-size:13px;color:#e08a6a;background:rgba(224,138,106,0.1);border:1px solid rgba(224,138,106,0.25);border-radius:10px;padding:10px 13px;')}>{bookingError}</div>
  )

  return (
    <div onClick={onClose} style={css('position:fixed;inset:0;z-index:100;background:rgba(8,7,6,0.78);backdrop-filter:blur(6px);display:flex;align-items:flex-start;justify-content:center;padding:40px 20px;overflow-y:auto;')}>
      <div onClick={(e) => e.stopPropagation()} style={css('width:100%;max-width:680px;border:1px solid rgba(255,255,255,0.1);border-radius:22px;background:linear-gradient(180deg,#17150f,#121009);box-shadow:0 40px 100px rgba(0,0,0,0.6);animation:fadeUp 0.4s ease both;overflow:hidden;')}>
        <div style={css('display:flex;align-items:center;justify-content:space-between;padding:24px 32px;border-bottom:1px solid rgba(255,255,255,0.07);')}>
          <div>
            <div style={css('font-size:12px;letter-spacing:2px;text-transform:uppercase;color:var(--gold);margin-bottom:4px;')}>Đặt lịch · Bước {step}/5</div>
            <h3 style={css('font-family:var(--font-display);font-size:22px;margin:0;font-weight:500;')}>{STEP_TITLES[step]}</h3>
          </div>
          <button onClick={onClose} className="hov-white-bg" style={css('width:38px;height:38px;border-radius:50%;background:rgba(255,255,255,0.06);border:1px solid rgba(255,255,255,0.1);color:#c4bfb2;font-size:18px;cursor:pointer;flex-shrink:0;')}>✕</button>
        </div>

        <div style={css('display:flex;gap:6px;padding:18px 32px 0;')}>
          {progressBars.map((s, i) => <div key={i} style={css(s)}></div>)}
        </div>

        <div style={css('padding:28px 32px 32px;')}>
          {step === 1 && (
            <div style={css('display:flex;flex-direction:column;gap:12px;')}>
              {PKGS.concat([NONE]).map((p) => {
                const isSel = selectedPkgId === p.id
                return (
                  <div key={p.id} onClick={() => onPickPackage(p.id)} style={css('display:flex;align-items:center;gap:16px;padding:18px 22px;border-radius:14px;cursor:pointer;transition:all .15s;' + (isSel ? 'border:1.5px solid var(--gold);background:rgba(200,162,83,0.08);' : 'border:1px solid rgba(255,255,255,0.1);background:#0f0e0c;'))}>
                    <div style={css('flex:1;')}>
                      <div style={css('font-family:var(--font-display);font-size:18px;color:#f4f1ea;')}>{p.name}</div>
                      <div style={css('font-size:13px;color:#a39e92;margin-top:3px;')}>{p.desc}</div>
                    </div>
                    <div style={css('font-family:var(--font-display);font-size:20px;color:var(--gold);white-space:nowrap;')}>{p.price}</div>
                  </div>
                )
              })}
            </div>
          )}

          {step === 2 && (
            <div style={css('display:flex;flex-direction:column;gap:14px;')}>
              <p style={css('font-size:13.5px;color:#a39e92;margin:0;line-height:1.55;')}>
                Chọn thêm và kết hợp các dịch vụ lẻ tuỳ ý. Dịch vụ đã có trong gói <span style={css('color:var(--gold);font-weight:600;')}>{comboName}</span> được đánh dấu sẵn — nhấn vào sẽ có thông báo bạn không cần thêm.
              </p>
              <div style={css('display:flex;flex-direction:column;gap:10px;')}>
                {ADDONS.map((a) => {
                  const included = !!(combo && combo.includes.indexOf(a.id) !== -1)
                  const selected = !included && selectedAddons.indexOf(a.id) !== -1
                  const rowStyle = 'display:flex;align-items:center;gap:14px;padding:15px 18px;border-radius:13px;transition:all .15s;cursor:pointer;' +
                    (included ? 'border:1px solid rgba(200,162,83,0.28);background:rgba(200,162,83,0.06);'
                      : selected ? 'border:1.5px solid var(--gold);background:rgba(200,162,83,0.08);'
                      : 'border:1px solid rgba(255,255,255,0.1);background:#0f0e0c;')
                  const checkStyle = 'width:24px;height:24px;border-radius:7px;flex-shrink:0;display:flex;align-items:center;justify-content:center;font-size:14px;font-weight:700;' +
                    ((selected || included) ? 'background:linear-gradient(135deg,var(--gold-light),var(--gold));color:#100f0c;border:1px solid var(--gold);'
                      : 'background:transparent;border:1.5px solid rgba(255,255,255,0.25);color:transparent;')
                  const priceStyle = 'font-family:var(--font-display);font-size:16px;white-space:nowrap;color:' + (included ? '#6b665c' : 'var(--gold)') + ';' + (included ? 'text-decoration:line-through;' : '')
                  return (
                    <div key={a.id} onClick={() => onToggleAddon(a.id)} style={css(rowStyle)}>
                      <div style={css(checkStyle)}>{(selected || included) ? '✓' : ''}</div>
                      <div style={css('flex:1;')}>
                        <div style={css('font-size:15px;color:#f4f1ea;font-weight:500;')}>{a.name}</div>
                        <div style={css('font-size:12.5px;margin-top:3px;color:' + (included ? 'var(--gold)' : '#8b8578') + ';')}>{included ? ('Đã bao gồm trong ' + comboName) : a.desc}</div>
                      </div>
                      <div style={css(priceStyle)}>{fmtVND(a.priceN)}</div>
                    </div>
                  )
                })}
              </div>
              {bookingError && errorBox}
              <div style={css('display:flex;justify-content:space-between;align-items:baseline;padding-top:14px;border-top:1px solid rgba(255,255,255,0.08);margin-top:4px;')}>
                <span style={css('font-size:14px;color:#c4bfb2;')}>Tạm tính (gói + dịch vụ lẻ)</span>
                <span style={css('font-family:var(--font-display);font-size:22px;color:var(--gold);')}>{fmtVND(grandTotal)}</span>
              </div>
            </div>
          )}

          {step === 3 && (
            <div style={css('display:flex;flex-direction:column;gap:22px;')}>
              <div>
                <label style={css('display:block;font-size:13px;color:#a39e92;margin-bottom:9px;')}>Loại xe</label>
                <div style={css('display:flex;flex-wrap:wrap;gap:9px;')}>
                  {VEHICLES.map((v) => <button key={v} onClick={() => onVehicle(v)} style={css(chip(bVehicle === v))}>{v}</button>)}
                </div>
              </div>
              <div style={css('display:grid;grid-template-columns:1fr 1fr;gap:16px;')}>
                <div>
                  <label style={css('display:block;font-size:13px;color:#a39e92;margin-bottom:9px;')}>Ngày</label>
                  <input type="date" value={bDate} onChange={onDate} className="foc-gold" style={css('width:100%;background:#0f0e0c;border:1px solid rgba(255,255,255,0.12);border-radius:11px;padding:13px 14px;color:#f4f1ea;font-size:14.5px;color-scheme:dark;')} />
                </div>
              </div>
              <div>
                <label style={css('display:block;font-size:13px;color:#a39e92;margin-bottom:9px;')}>Khung giờ</label>
                <div style={css('display:flex;flex-wrap:wrap;gap:9px;')}>
                  {TIMES.map((t) => <button key={t} onClick={() => onTime(t)} style={css(chip(bTime === t))}>{t}</button>)}
                </div>
              </div>
            </div>
          )}

          {step === 4 && (
            <div style={css('display:flex;flex-direction:column;gap:18px;')}>
              <div>
                <label style={css('display:block;font-size:13px;color:#a39e92;margin-bottom:8px;')}>Họ và tên</label>
                <input value={bName} onChange={onName} placeholder="Nguyễn Văn A" className="foc-gold" style={css(inputStyle)} />
              </div>
              <div>
                <label style={css('display:block;font-size:13px;color:#a39e92;margin-bottom:8px;')}>Số điện thoại</label>
                <div className="foc-gold" style={css('display:flex;align-items:center;background:#0f0e0c;border:1px solid rgba(255,255,255,0.12);border-radius:12px;padding:0 16px;')}>
                  <span style={css('font-size:15px;color:#8b8578;padding-right:11px;border-right:1px solid rgba(255,255,255,0.12);')}>+84</span>
                  <input value={bPhone} onChange={onPhone} inputMode="numeric" placeholder="912 345 678" style={css('flex:1;background:none;border:none;padding:14px 0 14px 13px;color:#f4f1ea;font-size:15px;')} />
                </div>
              </div>
              <div>
                <label style={css('display:block;font-size:13px;color:#a39e92;margin-bottom:8px;')}>Ghi chú (tuỳ chọn)</label>
                <textarea value={bNote} onChange={onNote} rows={3} placeholder="Ví dụ: xe có vết xước cần xử lý, ưu tiên khu vực bánh xe..." className="foc-gold" style={css(inputStyle + 'font-size:14.5px;resize:vertical;line-height:1.5;')} />
              </div>
              {bookingError && errorBox}
            </div>
          )}

          {step === 5 && (
            <div>
              <div style={css('text-align:center;margin-bottom:24px;')}>
                <div style={css('width:64px;height:64px;border-radius:50%;background:linear-gradient(135deg,var(--gold-light),var(--gold));display:flex;align-items:center;justify-content:center;margin:0 auto 16px;font-size:30px;color:#100f0c;box-shadow:0 10px 30px rgba(200,162,83,0.35);')}>✓</div>
                <h3 style={css('font-family:var(--font-display);font-size:25px;margin:0 0 8px;font-weight:500;')}>Đặt lịch thành công!</h3>
                <p style={css('font-size:14.5px;color:#a39e92;margin:0;line-height:1.6;')}>Cảm ơn bạn. Chúng tôi sẽ gọi xác nhận trong ít phút.</p>
              </div>
              <div style={css('background:#0f0e0c;border:1px solid rgba(255,255,255,0.08);border-radius:14px;padding:22px 24px;display:flex;flex-direction:column;gap:13px;')}>
                <div style={css('display:flex;justify-content:space-between;font-size:14.5px;')}>
                  <span style={css('color:#8b8578;')}>Gói combo</span>
                  <span style={css('color:#f4f1ea;font-weight:600;text-align:right;')}>{sel ? sel.name : '—'} · {sel ? fmtVND(sel.priceN) : '—'}</span>
                </div>
                {summaryAddons.length > 0 && (
                  <div style={css('display:flex;flex-direction:column;gap:9px;padding:11px 14px;background:rgba(200,162,83,0.05);border:1px solid rgba(200,162,83,0.18);border-radius:10px;')}>
                    <span style={css('font-size:12px;letter-spacing:1px;text-transform:uppercase;color:var(--gold);')}>Dịch vụ lẻ thêm</span>
                    {summaryAddons.map((la, i) => (
                      <div key={i} style={css('display:flex;justify-content:space-between;font-size:13.5px;')}>
                        <span style={css('color:#c4bfb2;')}>{la.name}</span><span style={css('color:#f4f1ea;')}>{la.priceLabel}</span>
                      </div>
                    ))}
                  </div>
                )}
                <div style={css('display:flex;justify-content:space-between;font-size:14.5px;')}><span style={css('color:#8b8578;')}>Loại xe</span><span style={css('color:#f4f1ea;')}>{bVehicle}</span></div>
                <div style={css('display:flex;justify-content:space-between;font-size:14.5px;')}><span style={css('color:#8b8578;')}>Thời gian</span><span style={css('color:#f4f1ea;')}>{(bTime || '—')} · {fmtDate(bDate)}</span></div>
                <div style={css('display:flex;justify-content:space-between;font-size:14.5px;')}><span style={css('color:#8b8578;')}>Liên hệ</span><span style={css('color:#f4f1ea;')}>{(bName || '—')} · +84 {(bPhone || '—')}</span></div>
                <div style={css('height:1px;background:rgba(255,255,255,0.08);margin:3px 0;')}></div>
                <div style={css('display:flex;justify-content:space-between;align-items:baseline;')}><span style={css('color:#c4bfb2;font-size:15px;')}>Tổng tạm tính</span><span style={css('font-family:var(--font-display);font-size:24px;color:var(--gold);')}>{fmtVND(grandTotal)}</span></div>
              </div>
            </div>
          )}
        </div>

        <div style={css('display:flex;justify-content:space-between;align-items:center;gap:14px;padding:20px 32px;border-top:1px solid rgba(255,255,255,0.07);')}>
          <button onClick={onPrev} style={css('background:none;border:1px solid rgba(255,255,255,0.16);color:#c4bfb2;padding:14px 22px;border-radius:12px;font-size:14.5px;font-weight:600;cursor:pointer;')}>{BACK_LABELS[step]}</button>
          <button onClick={onNext} className="hov-bright-strong" style={css('flex:1;max-width:240px;background:linear-gradient(135deg,var(--gold-light),var(--gold));border:none;color:#100f0c;padding:14px;border-radius:12px;font-size:15px;font-weight:700;cursor:pointer;')}>{NEXT_LABELS[step]}</button>
        </div>
      </div>
    </div>
  )
}
