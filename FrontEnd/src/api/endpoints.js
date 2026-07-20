// Lớp gọi API gộp — mỗi nhóm là 1 object. Các đợt sau cứ thêm hàm vào đây.
import api from '../lib/api'

export const authApi = {
  login: (loginId, password) => api.post('/auth/login', { identifier: loginId, password }).then((r) => r.data),  register: (payload) => api.post('/auth/register', payload).then((r) => r.data),
  refresh: (refreshToken) => api.post('/auth/refresh', null, { params: { refreshToken } }).then((r) => r.data),
}

export const customerApi = {
  me: () => api.get('/customers/me').then((r) => r.data),
  updateMe: (payload) => api.put('/customers/me', payload).then((r) => r.data),
}

export const loyaltyApi = {
  me: () => api.get('/loyalty/me').then((r) => r.data),
  redeem: (payload) => api.post('/loyalty/redeem', payload).then((r) => r.data),
}

export const publicApi = {
  services: () => api.get('/public/services').then((r) => r.data),
}

export const bookingApi = {
  myBookings: () => api.get('/bookings/my-bookings').then((r) => r.data),
  cancel: (id) => api.delete('/bookings/' + id).then((r) => r.data),
  create: (payload) => api.post('/bookings', payload).then((r) => r.data),
}

export const vehicleApi = {
  myVehicles: () => api.get('/vehicles/my-vehicles').then((r) => r.data),
  add: (payload) => api.post('/vehicles', payload).then((r) => r.data),
  update: (id, payload) => api.put('/vehicles/' + id, payload).then((r) => r.data),
  remove: (id) => api.delete('/vehicles/' + id).then((r) => r.data),
}

export const washerApi = {
  jobs: () => api.get('/washer/jobs').then((r) => r.data),
  advance: (id) => api.put('/washer/jobs/' + id + '/advance').then((r) => r.data),
  saveNote: (id, note) => api.put('/washer/jobs/' + id + '/note', { note }).then((r) => r.data),
}

export const receptionApi = {
  bookings: () => api.get('/reception/bookings').then((r) => r.data),
  checkin: (id) => api.put('/reception/bookings/' + id + '/checkin').then((r) => r.data),
  confirmPayment: (id) => api.post('/reception/bookings/' + id + '/confirm-payment').then((r) => r.data),
  assignBay: (id, bayId) => api.put('/reception/bookings/' + id + '/bay', null, { params: { bayId } }).then((r) => r.data),
  bays: () => api.get('/reception/bays').then((r) => r.data),
  lookupCustomer: (phone) => api.get('/reception/customer', { params: { phone } }).then((r) => r.data),
  walkin: (payload) => api.post('/receptionist/walkin', payload).then((r) => r.data),
  customers: () => api.get('/reception/customers').then((r) => r.data),
  createCustomer: (payload) => api.post('/reception/customers', payload).then((r) => r.data),
  resetPassword: (id, password) => api.put('/reception/customers/' + id + '/reset-password', { password }).then((r) => r.data),
}

export const adminApi = {
  users: () => api.get('/admin/users').then((r) => r.data),
  updateUser: (id, payload) => api.put('/admin/users/' + id, payload).then((r) => r.data),
  toggleUser: (id, role, activate) => activate
      ? api.put('/admin/users/' + id + '/' + role + '/activate')
      : api.delete('/admin/users/' + id + '/' + role),
  createCustomer: (payload) => api.post('/admin/customers', payload).then((r) => r.data),
  createReceptionist: (p) => api.post('/admin/receptionists', null, { params: p }),
  createWasher: (p) => api.post('/admin/washers', null, { params: p }),
  bookings: () => api.get('/admin/bookings').then((r) => r.data),
  stats: () => api.get('/admin/stats').then((r) => r.data),
  packages: () => api.get('/admin/packages').then((r) => r.data),
  updatePackage: (id, payload) => api.put('/admin/packages/' + id, payload).then((r) => r.data),
  promotions: () => api.get('/admin/promotions').then((r) => r.data),
  createPromotion: (payload) => api.post('/admin/promotions', payload).then((r) => r.data),
  deletePromotion: (id) => api.delete('/admin/promotions/' + id),
  shifts: () => api.get('/admin/shifts').then((r) => r.data),
  washBays: () => api.get('/admin/wash-bays').then((r) => r.data),
}