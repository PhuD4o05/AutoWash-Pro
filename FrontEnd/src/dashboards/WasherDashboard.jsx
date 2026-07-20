import { useState, useRef, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { css } from '../lib/css'
import Toast from '../components/Toast'
import { washerApi } from '../api/endpoints'

const FLOW = ['Waiting', 'Washing', 'Drying', 'Completed']
const STATUS_VI = { Waiting: 'Đang chờ', Washing: 'Đang rửa', Drying: 'Đang sấy', Completed: 'Hoàn thành' }
const NEXT_LABEL = { Waiting: 'Nhận xe vào bay', Washing: 'Chuyển sang sấy', Drying: 'Đánh dấu hoàn thành', Completed: 'Đã hoàn thành' }

const badge = (st) => {
    const map = { Waiting: ['#e0a36a', 'rgba(224,163,106,0.14)'], Washing: ['var(--gold)', 'rgba(200,162,83,0.14)'], Drying: ['#7fb8e0', 'rgba(127,184,224,0.14)'], Completed: ['#6fcf97', 'rgba(111,207,151,0.14)'] }
    const c = map[st] || ['#a39e92', 'rgba(255,255,255,0.08)']
    return 'padding:6px 13px;border-radius:20px;font-size:12.5px;font-weight:600;color:' + c[0] + ';background:' + c[1] + ';white-space:nowrap;'
}

export default function WasherDashboard() {
    const [tab, setTab] = useState('assigned')

    const washerName = localStorage.getItem('fullName') || 'Nhân viên'
    const now = new Date()
    const DOW_VI = ['Chủ nhật', 'Thứ 2', 'Thứ 3', 'Thứ 4', 'Thứ 5', 'Thứ 6', 'Thứ 7']
    const todayStr = DOW_VI[now.getDay()] + ', ' + String(now.getDate()).padStart(2, '0') + '/' + String(now.getMonth() + 1).padStart(2, '0') + '/' + now.getFullYear()

    const [allBookings, setAllBookings] = useState([])
    const ST = { WAITING: 'Waiting', WASHING: 'Washing', DRYING: 'Drying', COMPLETED: 'Completed', CHECKED_IN: 'Waiting', PENDING: 'Waiting', CONFIRMED: 'Waiting' }

    const loadJobs = () => {
        washerApi.jobs()
            .then((list) => setAllBookings(list.map((b) => ({
                id: 'BK' + b.id, _rawId: b.id,
                plate: b.licensePlate || '—',
                car: [b.carBrand, b.carModel].filter(Boolean).join(' ') || '—',
                color: '—',
                pkg: b.packageName || '—',
                bay: 'Bay 1',
                status: ST[b.status] || 'Waiting',
                note: b.note || '',
            }))))
            .catch(() => {})
    }
    useEffect(() => { loadJobs() }, [])

    const jobs = allBookings
    const queueAll = allBookings
        .filter((b) => ['Waiting', 'Washing', 'Drying'].includes(b.status))
        .map((b) => ({ plate: b.plate, car: b.car, pkg: b.pkg, bay: b.bay, status: b.status }))
    const history = allBookings.filter((b) => b.status === 'Completed')

    const [noteOpen, setNoteOpen] = useState(false)
    const [noteJobId, setNoteJobId] = useState(null)
    const [noteText, setNoteText] = useState('')
    const [toast, setToast] = useState('')
    const tt = useRef(null)
    const showToast = (m) => { setToast(m); clearTimeout(tt.current); tt.current = setTimeout(() => setToast(''), 3000) }

    const advance = (id) => {
        const job = allBookings.find((b) => b.id === id)
        if (!job || job.status === 'Completed') return
        washerApi.advance(job._rawId)
            .then(() => { loadJobs(); showToast((job.plate || id) + ': đã cập nhật trạng thái.') })
            .catch(() => showToast('Cập nhật thất bại.'))
    }

    const openNote = (id) => {
        const j = allBookings.find((b) => b.id === id)
        setNoteJobId(id)
        setNoteText(j?.note || '')
        setNoteOpen(true)
    }

    const saveNote = () => {
        const job = allBookings.find((b) => b.id === noteJobId)
        if (!job) return
        washerApi.saveNote(job._rawId, noteText)
            .then(() => { loadJobs(); setNoteOpen(false); showToast('Đã lưu ghi chú.') })
            .catch(() => showToast('Lưu ghi chú thất bại.'))
    }

    const NAV = [
        { key: 'assigned', label: 'Xe phân công', icon: '⊞', badge: jobs.filter((j) => j.status !== 'Completed').length },
        { key: 'queue', label: 'Hàng chờ', icon: '≡' },
        { key: 'schedule', label: 'Lịch sử làm việc', icon: '◷' },
    ]
    const doneToday = jobs.filter((j) => j.status === 'Completed').length

    const noteCarPlate = (allBookings.find((b) => b.id === noteJobId) || {}).plate || ''

    return (
        <div style={css('background:#0f0e0c;color:#f4f1ea;min-height:100vh;')}>
            <header style={css('position:sticky;top:0;z-index:50;display:flex;align-items:center;justify-content:space-between;padding:15px 30px;background:rgba(15,14,12,0.92);backdrop-filter:blur(14px);border-bottom:1.5px solid rgba(255,255,255,0.12);')}>
                <div style={css('display:flex;align-items:center;gap:12px;')}>
                    <div>
                        <div style={css('font-family:var(--font-display);font-size:17px;line-height:1;')}>AutoWash Pro</div>
                        <div style={css('font-size:11px;color:#8b8578;margin-top:2px;letter-spacing:1px;')}>NHÂN VIÊN RỬA XE</div>
                    </div>
                </div>
                <div style={css('display:flex;align-items:center;gap:18px;')}>
                    <div style={css('text-align:right;')}>
                        <div style={css('font-size:13.5px;color:#f4f1ea;')}>{washerName}</div>
                        <div style={css('font-size:11.5px;color:#8b8578;')}>{todayStr}</div>
                    </div>
                    <div style={css('width:38px;height:38px;border-radius:50%;border:1px solid var(--gold);display:flex;align-items:center;justify-content:center;font-family:var(--font-display);color:var(--gold);')}>{washerName.charAt(0).toUpperCase()}</div>
                    <Link to="/" className="hov-danger" style={css('font-size:13px;color:#8b8578;text-decoration:none;')}>Đăng xuất</Link>
                </div>
            </header>

            <div className="dash-grid" style={css('display:grid;grid-template-columns:220px 1fr;min-height:calc(100vh - 70px);')}>
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
                    <div style={css('margin-top:auto;border:1.5px solid rgba(200,162,83,0.3);border-radius:12px;padding:18px;background:rgba(200,162,83,0.04);')}>
                        <div style={css('font-size:12px;color:#8b8578;letter-spacing:0.5px;margin-bottom:8px;')}>CA HÔM NAY</div>
                        <div style={css('font-family:var(--font-display);font-size:28px;color:var(--gold);')}>{doneToday}/{jobs.length}</div>
                        <div style={css('font-size:12.5px;color:#a39e92;margin-top:2px;')}>xe đã hoàn thành</div>
                    </div>
                </aside>

                <main style={css('padding:32px 38px 70px;')}>
                    {tab === 'assigned' && (
                        <div style={css('animation:fadeUp .4s ease both;')}>
                            <h1 style={css('font-family:var(--font-display);font-size:28px;margin:0 0 4px;font-weight:500;')}>Xe được phân công</h1>
                            <p style={css('font-size:14px;color:#8b8578;margin:0 0 26px;')}>Nhận xe vào bay và cập nhật trạng thái theo tiến trình rửa.</p>
                            {jobs.length === 0 ? (
                                <div style={css('text-align:center;padding:60px 20px;color:#8b8578;')}>
                                    <div style={css('font-size:48px;margin-bottom:16px;')}>⊞</div>
                                    <div style={css('font-size:16px;')}>Chưa có xe nào được phân công</div>
                                </div>
                            ) : (
                                <div className="two-col" style={css('display:grid;grid-template-columns:repeat(2,1fr);gap:14px;')}>
                                    {jobs.map((j) => {
                                        const cur = FLOW.indexOf(j.status); const done = j.status === 'Completed'
                                        return (
                                            <div key={j.id} style={css('border:1.5px solid ' + (j.status === 'Washing' ? 'rgba(200,162,83,0.4)' : 'rgba(255,255,255,0.16)') + ';border-radius:16px;padding:22px 24px;')}>
                                                <div style={css('display:flex;justify-content:space-between;align-items:flex-start;gap:12px;margin-bottom:16px;')}>
                                                    <div>
                                                        <div style={css('font-family:var(--font-display);font-size:19px;')}>{j.plate}</div>
                                                        <div style={css('font-size:13.5px;color:#a39e92;margin-top:3px;')}>{j.car} · {j.color}</div>
                                                        <div style={css('font-size:12.5px;color:#8b8578;margin-top:2px;')}>{j.id} · {j.bay}</div>
                                                    </div>
                                                    <span style={css(badge(j.status))}>{STATUS_VI[j.status]}</span>
                                                </div>
                                                <div style={css('background:rgba(200,162,83,0.06);border-radius:10px;padding:11px 14px;margin-bottom:16px;')}>
                                                    <div style={css('font-size:12px;color:#8b8578;')}>Gói dịch vụ</div>
                                                    <div style={css('font-size:14.5px;color:#f4f1ea;margin-top:2px;')}>{j.pkg}</div>
                                                </div>
                                                <div style={css('display:flex;align-items:center;margin-bottom:18px;')}>
                                                    {FLOW.map((f, i) => {
                                                        const mark = (i < cur || (done && i === cur)) ? '✓' : ''
                                                        const dot = 'width:26px;height:26px;border-radius:50%;display:flex;align-items:center;justify-content:center;font-size:11px;z-index:2;' +
                                                            (i < cur ? 'background:var(--gold);color:#100f0c;' : i === cur ? 'background:var(--gold);color:#100f0c;' + (done ? '' : 'box-shadow:0 0 0 4px rgba(200,162,83,0.18);animation:pulse 1.4s ease-in-out infinite;') : 'background:#1c1a14;border:1.5px solid rgba(255,255,255,0.15);color:#6f6a5e;')
                                                        const line = i === 0 ? 'display:none;' : 'position:absolute;top:13px;right:50%;width:100%;height:2px;z-index:1;' + (i <= cur ? 'background:var(--gold);' : 'background:rgba(255,255,255,0.12);')
                                                        const label = 'font-size:10.5px;margin-top:7px;text-align:center;line-height:1.2;color:' + (i <= cur ? '#c4bfb2' : '#6f6a5e') + ';'
                                                        return (
                                                            <div key={i} style={css('flex:1;display:flex;flex-direction:column;align-items:center;position:relative;')}>
                                                                <div style={css(line)}></div>
                                                                <div style={css(dot)}>{mark}</div>
                                                                <div style={css(label)}>{STATUS_VI[f]}</div>
                                                            </div>
                                                        )
                                                    })}
                                                </div>
                                                <div style={css('display:flex;gap:9px;')}>
                                                    <button onClick={() => advance(j.id)} style={css(done ? 'flex:1;background:rgba(111,207,151,0.12);border:1px solid rgba(111,207,151,0.3);color:#6fcf97;padding:11px;border-radius:11px;font-size:13.5px;font-weight:600;cursor:default;' : 'flex:1;background:var(--gold);border:none;color:#100f0c;padding:11px;border-radius:11px;font-size:13.5px;font-weight:600;cursor:pointer;')}>{NEXT_LABEL[j.status]}</button>
                                                    <button onClick={() => openNote(j.id)} className="hov-border-gold" style={css('background:transparent;border:1.5px solid rgba(255,255,255,0.16);color:#c4bfb2;padding:11px 16px;border-radius:11px;font-size:13.5px;cursor:pointer;')}>Ghi chú</button>
                                                </div>
                                                {j.note && <div style={css('margin-top:12px;font-size:12.5px;color:#e0a36a;background:rgba(224,163,106,0.1);border-radius:9px;padding:9px 12px;')}>⚠ {j.note}</div>}
                                            </div>
                                        )
                                    })}
                                </div>
                            )}
                        </div>
                    )}

                    {tab === 'queue' && (
                        <div style={css('animation:fadeUp .4s ease both;')}>
                            <h1 style={css('font-family:var(--font-display);font-size:28px;margin:0 0 4px;font-weight:500;')}>Hàng chờ hiện tại</h1>
                            <p style={css('font-size:14px;color:#8b8578;margin:0 0 26px;')}>Toàn bộ xe đang chờ trong khu rửa.</p>
                            {queueAll.length === 0 ? (
                                <div style={css('text-align:center;padding:60px 20px;color:#8b8578;')}>
                                    <div style={css('font-size:48px;margin-bottom:16px;')}>≡</div>
                                    <div style={css('font-size:16px;')}>Hàng chờ trống</div>
                                </div>
                            ) : (
                                <div style={css('display:flex;flex-direction:column;gap:11px;')}>
                                    {queueAll.map((q, i) => (
                                        <div key={i} style={css('border:1.5px solid rgba(255,255,255,0.16);border-radius:14px;padding:16px 22px;display:flex;align-items:center;gap:16px;')}>
                                            <div style={css('width:32px;height:32px;border-radius:9px;background:rgba(200,162,83,0.12);border:1px solid rgba(200,162,83,0.3);display:flex;align-items:center;justify-content:center;font-family:var(--font-display);color:var(--gold);font-size:14px;')}>{i + 1}</div>
                                            <div style={css('flex:1;')}>
                                                <div style={css('font-family:var(--font-display);font-size:15.5px;')}>{q.plate} <span style={css('font-size:12.5px;color:#8b8578;')}>· {q.car}</span></div>
                                                <div style={css('font-size:13px;color:#a39e92;margin-top:2px;')}>{q.pkg} · {q.bay}</div>
                                            </div>
                                            <span style={css(badge(q.status))}>{STATUS_VI[q.status]}</span>
                                        </div>
                                    ))}
                                </div>
                            )}
                        </div>
                    )}

                    {tab === 'schedule' && (
                        <div style={css('animation:fadeUp .4s ease both;')}>
                            <h1 style={css('font-family:var(--font-display);font-size:28px;margin:0 0 4px;font-weight:500;')}>Hôm nay & Lịch sử</h1>
                            <p style={css('font-size:14px;color:#8b8578;margin:0 0 22px;')}>{todayStr} · {washerName}</p>

                            <div style={css('border:1.5px solid rgba(255,255,255,0.16);border-radius:14px;padding:20px 24px;margin-bottom:26px;display:flex;gap:40px;flex-wrap:wrap;')}>
                                <div><div style={css('font-size:12.5px;color:#8b8578;')}>Xe đã hoàn thành</div><div style={css('font-family:var(--font-display);font-size:24px;color:var(--gold);margin-top:4px;')}>{history.length}</div></div>
                                <div><div style={css('font-size:12.5px;color:#8b8578;')}>Đang xử lý</div><div style={css('font-family:var(--font-display);font-size:24px;color:#f4f1ea;margin-top:4px;')}>{queueAll.length}</div></div>
                            </div>

                            <h2 style={css('font-family:var(--font-display);font-size:20px;margin:0 0 14px;font-weight:500;')}>Xe đã làm</h2>
                            {history.length === 0 ? (
                                <div style={css('text-align:center;padding:40px 20px;color:#8b8578;')}>Chưa có xe nào hoàn thành</div>
                            ) : (
                                <div style={css('display:flex;flex-direction:column;gap:11px;')}>
                                    {history.map((h, i) => (
                                        <div key={i} style={css('border:1.5px solid rgba(255,255,255,0.14);border-radius:13px;padding:15px 20px;display:flex;align-items:center;gap:16px;')}>
                                            <div style={css('flex:1;')}>
                                                <div style={css('font-family:var(--font-display);font-size:15px;')}>{h.plate} <span style={css('font-size:12.5px;color:#8b8578;')}>· {h.car}</span></div>
                                                <div style={css('font-size:13px;color:#a39e92;margin-top:2px;')}>{h.pkg}</div>
                                            </div>
                                            <span style={css('padding:6px 13px;border-radius:20px;font-size:12.5px;font-weight:600;color:#6fcf97;background:rgba(111,207,151,0.14);')}>Hoàn thành</span>
                                        </div>
                                    ))}
                                </div>
                            )}
                        </div>
                    )}
                </main>
            </div>

            {noteOpen && (
                <div onClick={() => setNoteOpen(false)} style={css('position:fixed;inset:0;z-index:100;background:rgba(8,7,6,0.78);backdrop-filter:blur(6px);display:flex;align-items:center;justify-content:center;padding:24px;')}>
                    <div onClick={(e) => e.stopPropagation()} style={css('width:100%;max-width:440px;border:1.5px solid rgba(255,255,255,0.14);border-radius:18px;background:#16140f;padding:28px 30px;animation:fadeUp .35s ease both;')}>
                        <h3 style={css('font-family:var(--font-display);font-size:20px;margin:0 0 6px;font-weight:500;')}>Ghi chú vấn đề phát sinh</h3>
                        <p style={css('font-size:13px;color:#8b8578;margin:0 0 18px;')}>Xe {noteCarPlate}</p>
                        <textarea value={noteText} onChange={(e) => setNoteText(e.target.value)} rows={4} placeholder="VD: phát hiện vết xước cản trước, khoang nội thất có vết bẩn cứng đầu..." className="foc-gold" style={css('width:100%;background:#0f0e0c;border:1.5px solid rgba(255,255,255,0.14);border-radius:11px;padding:13px 15px;color:#f4f1ea;font-size:14px;resize:vertical;line-height:1.5;')} />
                        <div style={css('display:flex;gap:10px;margin-top:18px;')}>
                            <button onClick={() => setNoteOpen(false)} style={css('flex:1;background:transparent;border:1.5px solid rgba(255,255,255,0.18);color:#c4bfb2;padding:12px;border-radius:11px;font-size:14px;cursor:pointer;')}>Huỷ</button>
                            <button onClick={saveNote} style={css('flex:1;background:var(--gold);border:none;color:#100f0c;padding:12px;border-radius:11px;font-size:14px;font-weight:600;cursor:pointer;')}>Lưu ghi chú</button>
                        </div>
                    </div>
                </div>
            )}

            <Toast message={toast} />
        </div>
    )
}