// ============================================================================
// 4 TÀI KHOẢN DEMO — 4 ROLE KHÁC NHAU ĐỂ TEST
// ----------------------------------------------------------------------------
//  Quản trị (Admin)        : ad   / 1          -> /admin
//  Nhân viên rửa (Washer)  : ws   / 1          -> /washer
//  Tiếp tân (Reception)    : rep  / 1          -> /reception
//  Khách hàng (Customer)   : 0912345678 / 123456 -> /customer
// ----------------------------------------------------------------------------
//  Đây là auth demo phía client (chưa nối backend). Khi cần ráp JWT của
//  Spring Boot, chỉ thay phần xác thực trong landing/Landing.jsx (submitAuth).
// ============================================================================

export const ROLE_ACCOUNTS = {
  ad:  { password: '1', role: 'admin',        dest: '/admin',      name: 'Quản trị viên' },
  ws:  { password: '1', role: 'washer',       dest: '/washer',     name: 'Nhân viên rửa' },
  rep: { password: '1', role: 'receptionist', dest: '/reception',  name: 'Tiếp tân' },
}

export const CUSTOMER_DEMO = { id: '0912345678', password: '123456', name: 'Minh Tuấn' }

// Danh sách hiển thị ở hộp "Tài khoản demo" trong màn đăng nhập
export const DEMO_ACCOUNTS = [
  { label: 'Quản trị (Admin)',        id: 'ad',         pw: '1',      cred: 'ad / 1' },
  { label: 'Nhân viên rửa (Washer)',  id: 'ws',         pw: '1',      cred: 'ws / 1' },
  { label: 'Tiếp tân (Reception)',    id: 'rep',        pw: '1',      cred: 'rep / 1' },
  { label: 'Khách hàng (demo)',       id: '0912345678', pw: '123456', cred: '0912345678 / 123456' },
]
