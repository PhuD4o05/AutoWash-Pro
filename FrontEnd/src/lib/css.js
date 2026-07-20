// Chuyển 1 chuỗi CSS inline (giống hệt thiết kế gốc) thành style object cho React.
// Nhờ helper này, mình giữ nguyên 100% các style string từ file thiết kế.
export function css(str) {
  if (!str) return {}
  if (typeof str === 'object') return str
  const obj = {}
  // tách theo ';' nhưng không phá vỡ url(...) / gradient(...) có chứa ';'? (CSS inline không có ';' trong url)
  str.split(';').forEach((rule) => {
    const idx = rule.indexOf(':')
    if (idx === -1) return
    const prop = rule.slice(0, idx).trim()
    const val = rule.slice(idx + 1).trim()
    if (!prop) return
    const camel = prop.replace(/-([a-z])/g, (_, c) => c.toUpperCase())
    obj[camel] = val
  })
  return obj
}
