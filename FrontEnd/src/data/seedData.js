// src/data/seedData.js
export const seedBookings = [
    {
        id: 'BK1042', name: 'Minh Tuấn', plate: '51K-123.45', car: 'Mercedes C300', color: 'Đen',
        pkg: 'Gói Cao Cấp', price: '750.000đ', priceN: 750000,
        date: '16/06/2026', time: '15:00', when: '16/06 · 15:00',
        branch: 'Quận 1', bay: 'Bay 1',
        status: 'Washing', st: 'Washing',
        tier: 'Gold', points: 2450, live: false, note: ''
    },
    {
        id: 'BK1045', name: 'Thu Hà', plate: '30A-678.90', car: 'VinFast VF8', color: 'Trắng',
        pkg: 'Gói Tiêu Chuẩn', price: '350.000đ', priceN: 350000,
        date: '16/06/2026', time: '15:30', when: '16/06 · 15:30',
        branch: 'Quận 1', bay: '',
        status: 'Confirmed', st: 'Confirmed',
        tier: 'Silver', points: 1240, live: false, note: ''
    },
    {
        id: 'BK1046', name: 'Quốc Bảo', plate: '29B-555.12', car: 'Mazda CX-5', color: 'Trắng',
        pkg: 'Phủ Ceramic', price: '2.500.000đ', priceN: 2500000,
        date: '16/06/2026', time: '16:00', when: '16/06 · 16:00',
        branch: 'Quận 1', bay: '',
        status: 'Pending', st: 'Pending',
        tier: 'Platinum', points: 6100, live: false, note: ''
    },
    {
        id: 'BK1039', name: 'Thu Hà', plate: '30A-678.90', car: 'VinFast VF8', color: 'Trắng',
        pkg: 'Gói Tiêu Chuẩn', price: '350.000đ', priceN: 350000,
        date: '10/06/2026', time: '09:30', when: '10/06 · 09:30',
        branch: 'Quận 7', bay: 'Bay 2',
        status: 'Completed', st: 'Completed',
        tier: 'Silver', points: 1240, live: false, note: ''
    },
    {
        id: 'BK1031', name: 'Quốc Bảo', plate: '29B-555.12', car: 'Mazda CX-5', color: 'Trắng',
        pkg: 'Phủ Ceramic', price: '2.500.000đ', priceN: 2500000,
        date: '02/06/2026', time: '08:00', when: '02/06 · 08:00',
        branch: 'Quận 1', bay: 'Bay 1',
        status: 'Completed', st: 'Completed',
        tier: 'Platinum', points: 6100, live: false, note: ''
    },
    {
        id: 'BK1025', name: 'Lan Anh', plate: '51F-228.80', car: 'Toyota Vios', color: 'Bạc',
        pkg: 'Gói Cơ Bản', price: '150.000đ', priceN: 150000,
        date: '28/05/2026', time: '11:00', when: '28/05 · 11:00',
        branch: 'Thủ Đức', bay: '',
        status: 'Cancelled', st: 'Cancelled',
        tier: 'Member', points: 0, live: false, note: ''
    },
]

export const seedCustomers = [
    { id: 1, name: 'Minh Tuấn', phone: '0912 345 678', tier: 'Gold', points: 2450, password: '123456' },
    { id: 2, name: 'Thu Hà', phone: '0987 654 321', tier: 'Silver', points: 1240, password: '123456' },
    { id: 3, name: 'Quốc Bảo', phone: '0903 222 888', tier: 'Platinum', points: 6100, password: '123456' },
]

export const seedCars = {
    '0912 345 678': [
        { id: 1, plate: '51K-123.45', brand: 'Mercedes-Benz', model: 'C300 AMG', color: 'Đen', washes: 12 },
        { id: 2, plate: '30A-678.90', brand: 'VinFast', model: 'VF8 Plus', color: 'Trắng', washes: 5 },
    ],
    '0987 654 321': [
        { id: 1, plate: '30A-678.90', brand: 'VinFast', model: 'VF8 Plus', color: 'Trắng', washes: 5 },
    ],
}