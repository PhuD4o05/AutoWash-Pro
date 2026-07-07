# AutoWash Pro — React + Vite

Frontend dịch vụ rửa xe / detailing cao cấp, dựng lại **giống y chang 100%** từ thiết kế (Claude Design / DCLogic) sang React thật. Gồm **1 landing page** + **4 dashboard theo role** (Khách hàng, Tiếp tân, Nhân viên rửa, Quản trị), kèm dữ liệu giả và 4 tài khoản test.

## 🚀 Chạy dự án

```bash
npm install
npm run dev      # mở http://localhost:5173
```

Build production:

```bash
npm run build    # ra thư mục dist/
npm run preview  # xem thử bản build
```

> Yêu cầu Node 18+.

## 🔑 4 tài khoản demo (4 role để test)

Đăng nhập tại landing → nút **Đăng nhập**. Có sẵn hộp **"Tài khoản demo · 4 role để test"** — bấm vào dòng nào là tự điền, rồi bấm **Đăng nhập**.

| Role | Mã / SĐT | Mật khẩu | Vào trang |
|------|----------|----------|-----------|
| Quản trị (Admin) | `ad` | `1` | `/admin` |
| Nhân viên rửa (Washer) | `ws` | `1` | `/washer` |
| Tiếp tân (Reception) | `rep` | `1` | `/reception` |
| Khách hàng (Customer) | `0912345678` | `123456` | ở lại landing (đã đăng nhập) → bấm avatar để vào `/customer` |

> Đăng nhập là **demo phía client** (chưa nối backend). Khi cần ráp JWT của Spring Boot, chỉ sửa hàm `submitAuth` trong `src/landing/Landing.jsx` và `src/lib/accounts.js`.

## 🗺️ Routes

| Path | Trang |
|------|-------|
| `/` | Landing (hero, dịch vụ, bảng giá, thành quả, đánh giá, liên hệ + modal đặt lịch 5 bước + đăng nhập/đăng ký) |
| `/customer` | Dashboard Khách hàng: tổng quan + theo dõi trực tiếp, tài khoản, xe của tôi (CRUD), đặt lịch 5 bước, lịch sử, điểm thưởng |
| `/reception` | Dashboard Tiếp tân: check-in (QR/SĐT/walk-in), hàng chờ & phân bay, thanh toán + voucher, quản lý khách |
| `/washer` | Dashboard Nhân viên rửa: xe phân công (tiến trình rửa), hàng chờ, lịch làm việc |
| `/admin` | Dashboard Quản trị: báo cáo & doanh thu, tài khoản, gói & bảng giá, booking, khuyến mãi, ca làm & wash bay |

## 🧩 Cấu trúc

```
src/
  main.jsx               # entry + BrowserRouter
  App.jsx                # định nghĩa routes + state user
  index.css              # biến màu, font, animation, tiện ích hover/responsive
  lib/
    css.js               # helper: chuỗi CSS inline -> style object (giữ nguyên style thiết kế)
    format.js            # fmtVND / fmtDate
    accounts.js          # 4 tài khoản demo + cấu hình role
  components/
    Toast.jsx            # toast dùng chung
  landing/
    Landing.jsx          # orchestrator landing (state + định tuyến đăng nhập)
    Navbar.jsx  Home.jsx  AuthView.jsx  BookingModal.jsx
    landingData.js       # dữ liệu giả: gói, dịch vụ, gallery, review...
  dashboards/
    CustomerDashboard.jsx  ReceptionDashboard.jsx
    WasherDashboard.jsx    AdminDashboard.jsx
public/img/              # ảnh thật: hero, gallery (3), contact, auth
```

## 🎨 Ghi chú thiết kế

- **Màu chủ đạo:** vàng đồng `--gold (#c8a253)` / `--gold-light (#e3c885)` trên nền tối `#0f0e0c`.
- **Font:** thiết kế gốc dùng *Tenor Sans* (không đủ dấu tiếng Việt) nên đã thay bằng **Playfair Display** (tiêu đề) + **Manrope** (nội dung) — hỗ trợ tiếng Việt đầy đủ, vẫn giữ tinh thần serif sang trọng.
- **Ảnh:** hero, 3 ảnh gallery (Mercedes-Benz W205 / Porsche 911 / BMW M3), ảnh nền khu liên hệ và màn đăng nhập đều là ảnh thật, đặt trong `public/img/`.
- **Khác biệt có chủ đích so với thiết kế:** thiết kế mới đã bỏ hộp tài khoản demo trong màn đăng nhập — mình **thêm lại** hộp này (4 role, bấm để tự điền) để bạn test 4 dashboard cho nhanh. Bỏ đi rất dễ nếu không cần (xoá block demo trong `src/landing/AuthView.jsx`).

## 🔌 Khi ráp backend Spring Boot

- Thay auth demo: gọi `POST /api/auth/login`, lưu access/refresh token, đọc `role` từ response để điều hướng.
- Các nút "demo" trong dashboard (đổi giá, tạo voucher, đổi lịch...) hiện chỉ bắn toast — nối API tương ứng khi backend sẵn sàng.
- Dữ liệu mock nằm tập trung đầu mỗi file dashboard và trong `src/landing/landingData.js` — thay bằng dữ liệu từ API.

© 2026 AutoWash Pro — Detailing & Chăm sóc xe cao cấp.
