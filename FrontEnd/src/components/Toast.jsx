import { css } from '../lib/css'

export default function Toast({ message, big }) {
  if (!message) return null
  const dot = big
    ? 'width:26px;height:26px;border-radius:50%;background:linear-gradient(135deg,var(--gold-light),var(--gold));display:flex;align-items:center;justify-content:center;font-size:14px;color:#100f0c;flex-shrink:0;'
    : 'width:24px;height:24px;border-radius:50%;background:var(--gold);display:flex;align-items:center;justify-content:center;font-size:13px;color:#100f0c;flex-shrink:0;'
  return (
    <div style={css('position:fixed;bottom:28px;left:50%;transform:translateX(-50%);z-index:200;background:#17150f;border:1.5px solid rgba(200,162,83,0.4);border-radius:14px;padding:14px 24px;display:flex;align-items:center;gap:12px;box-shadow:0 16px 50px rgba(0,0,0,0.5);animation:fadeUp .35s ease both;')}>
      <span style={css(dot)}>✓</span>
      <span style={css('font-size:14px;color:#f4f1ea;')}>{message}</span>
    </div>
  )
}

// Hook nhỏ tạo toast tự ẩn sau 3s
import { useState, useRef, useCallback } from 'react'
export function useToast(timeout = 3000) {
  const [toast, setToast] = useState('')
  const t = useRef(null)
  const show = useCallback((m) => {
    setToast(m)
    clearTimeout(t.current)
    t.current = setTimeout(() => setToast(''), timeout)
  }, [timeout])
  return [toast, show]
}
