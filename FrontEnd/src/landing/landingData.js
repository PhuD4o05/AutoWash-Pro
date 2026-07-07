// ===== DỮ LIỆU GIẢ CHO LANDING (giống hệt thiết kế) — nạp vào state, tương tác được =====

export const PKGS = [
  { id: 'basic', name: 'Gói Cơ Bản', price: '150.000đ', priceN: 150000, period: '/lượt',
    desc: 'Rửa ngoại thất & làm sạch cơ bản', popular: false,
    includes: ['wash'],
    features: ['Rửa thân vỏ & gầm xe', 'Hút bụi nội thất', 'Lau khô & xịt thơm', 'Thời gian ~30 phút'] },
  { id: 'standard', name: 'Gói Tiêu Chuẩn', price: '350.000đ', priceN: 350000, period: '/lượt',
    desc: 'Vệ sinh toàn diện trong & ngoài', popular: true,
    includes: ['wash', 'interior', 'tire'],
    features: ['Toàn bộ Gói Cơ Bản', 'Vệ sinh nội thất chi tiết', 'Dưỡng lốp & taplo', 'Làm sạch kính chuyên sâu', 'Thời gian ~60 phút'] },
  { id: 'premium', name: 'Gói Cao Cấp', price: '750.000đ', priceN: 750000, period: '/lượt',
    desc: 'Đánh bóng & bảo vệ bề mặt sơn', popular: false,
    includes: ['wash', 'interior', 'tire', 'polish', 'wax', 'engine'],
    features: ['Toàn bộ Gói Tiêu Chuẩn', 'Đánh bóng sơn 1 bước', 'Phủ wax bảo vệ', 'Dọn khoang máy'] },
  { id: 'ceramic', name: 'Phủ Ceramic', price: '2.500.000đ', priceN: 2500000, period: 'từ',
    desc: 'Bảo vệ sơn cao cấp, bền 12–36 tháng', popular: false,
    includes: ['wash', 'polish', 'ceramic'],
    features: ['Làm sạch & decon bề mặt', 'Đánh bóng hoàn thiện', 'Phủ ceramic 9H', 'Bảo hành lớp phủ'] },
]

export const NONE = {
  id: 'none', name: 'Gói lẻ tự chọn', price: 'Tự chọn', priceN: 0,
  desc: 'Không lấy combo — tự chọn từng dịch vụ bên dưới', includes: [],
}

export const ADDONS = [
  { id: 'wash',     name: 'Rửa & hút bụi cơ bản',      priceN: 150000,  desc: 'Rửa thân vỏ, gầm xe & hút bụi khoang' },
  { id: 'interior', name: 'Vệ sinh nội thất chi tiết', priceN: 250000,  desc: 'Làm sạch sâu ghế, trần, sàn & khe kẽ' },
  { id: 'tire',     name: 'Dưỡng lốp & taplo',         priceN: 80000,   desc: 'Phục hồi độ đen lốp & bề mặt nhựa' },
  { id: 'polish',   name: 'Đánh bóng sơn 1 bước',      priceN: 400000,  desc: 'Xử lý vết xoáy mờ, tăng độ bóng' },
  { id: 'wax',      name: 'Phủ wax bảo vệ',            priceN: 200000,  desc: 'Chống bám bẩn, độ bóng bền ~2 tháng' },
  { id: 'engine',   name: 'Dọn khoang máy',            priceN: 180000,  desc: 'Vệ sinh & dưỡng khoang động cơ' },
  { id: 'ozone',    name: 'Khử mùi Ozone',             priceN: 150000,  desc: 'Diệt khuẩn & khử mùi nội thất' },
  { id: 'ceramic',  name: 'Phủ Ceramic 9H',            priceN: 2500000, desc: 'Lớp phủ bảo vệ sơn bền 12–36 tháng' },
]

export const TIMES = ['08:00', '09:30', '11:00', '13:30', '15:00', '16:30', '18:00']
export const VEHICLES = ['Sedan', 'SUV / CUV', 'Bán tải', 'MPV 7 chỗ', 'Xe sang / Coupe']
export const BRANCHES = [
  'Chi nhánh Quận 1 — 12 Lê Lợi',
  'Chi nhánh Quận 7 — 88 Nguyễn Thị Thập',
  'Chi nhánh Thủ Đức — 25 Võ Văn Ngân',
]

export const SERVICES = [
  { icon: '✦', title: 'Rửa xe ngoại thất',     desc: 'Quy trình rửa không chạm, an toàn cho lớp sơn, làm sạch toàn bộ thân vỏ và gầm xe.' },
  { icon: '❖', title: 'Vệ sinh nội thất',      desc: 'Làm sạch sâu ghế, trần, sàn và các khe kẽ — trả lại không gian thơm tho như mới.' },
  { icon: '◆', title: 'Phủ Ceramic 9H',        desc: 'Lớp phủ nano bảo vệ sơn khỏi tia UV, hoá chất và trầy xước nhẹ, bền tới 36 tháng.' },
  { icon: '✺', title: 'Đánh bóng & xoá xước',  desc: 'Xử lý vết xoáy, ố mờ, phục hồi độ sâu và độ bóng nguyên bản của bề mặt sơn.' },
  { icon: '⚙', title: 'Dọn khoang máy',        desc: 'Vệ sinh và dưỡng khoang động cơ an toàn, làm bật lên vẻ chỉn chu tổng thể.' },
  { icon: '❂', title: 'Khử mùi & diệt khuẩn',  desc: 'Công nghệ Ozone khử mùi, diệt khuẩn nội thất, an toàn cho sức khoẻ gia đình.' },
]

// Gallery — dùng ảnh thật (giống thiết kế mới)
export const GALLERY = [
  { title: 'Mercedes-Benz W205', sub: 'Phủ Ceramic 9H toàn xe',   img: '/img/gallery1.jpg' },
  { title: 'Porsche 911',        sub: 'Detail nội thất + khử mùi', img: '/img/gallery2.webp' },
  { title: 'BMW M3',             sub: 'Đánh bóng & xoá xước sơn',  img: '/img/gallery3.webp' },
]

export const REVIEWS = [
  { stars: '★★★★★', initial: 'T', name: 'Anh Minh Tuấn', text: 'Xe mình sáng bóng như mới sau khi phủ ceramic. Nhân viên tỉ mỉ, đúng hẹn, không gian chờ rất sang.' },
  { stars: '★★★★★', initial: 'H', name: 'Chị Thu Hà',    text: 'Đặt lịch online tiện, tới là làm ngay không phải chờ. Nội thất sạch và thơm hơn hẳn, rất hài lòng.' },
  { stars: '★★★★★', initial: 'B', name: 'Anh Quốc Bảo',  text: 'Đánh bóng xoá gần hết vết xước dăm. Giá xứng đáng với chất lượng, chắc chắn sẽ quay lại.' },
]
