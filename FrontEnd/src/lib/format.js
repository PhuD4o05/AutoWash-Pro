// Helper định dạng tiền tệ & ngày, dùng chung cho landing và các dashboard.
export const fmtVND = (n) => (n || 0).toLocaleString('vi-VN') + 'đ'
export const fmt = (n) => (n || 0).toLocaleString('vi-VN')

export const fmtDate = (d) => {
  if (!d) return '—'
  const parts = (d || '').split('-')
  if (parts.length !== 3) return d
  return parts[2] + '/' + parts[1] + '/' + parts[0]
}
