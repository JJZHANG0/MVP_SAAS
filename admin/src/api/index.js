import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 30000
})

api.interceptors.response.use(
  response => response.data,
  error => {
    console.error('API Error:', error)
    return Promise.reject(error)
  }
)

export default {
  shops: {
    getAll: () => api.get('/shops'),
    connect: (shopDomain) => api.post('/shops/connect/init', { shopDomain }),
    callback: (data) => api.post('/shops/connect/callback', data),
    syncOrders: (id) => api.post(`/shops/${id}/sync-orders`)
  },
  
  products: {
    getAll: (shopId) => api.get('/products', { params: { shopId } }),
    get: (id) => api.get(`/products/${id}`),
    create: (data) => api.post('/products', data),
    update: (id, data) => api.put(`/products/${id}`, data)
  },
  
  manuals: {
    getAll: () => api.get('/manuals'),
    get: (id) => api.get(`/manuals/${id}`),
    getByProduct: (productId) => api.get(`/manuals/product/${productId}`),
    upload: (formData) => api.post('/manuals/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    }),
    confirm: (id, data) => api.post(`/manuals/${id}/confirm`, data),
    delete: (id) => api.delete(`/manuals/${id}`)
  },
  
  sessions: {
    getAll: (params) => api.get('/sessions', { params }),
    get: (id) => api.get(`/sessions/${id}`),
    getByUuid: (uuid) => api.get(`/sessions/uuid/${uuid}`),
    getTransferred: (shopId) => api.get('/sessions/transferred', { params: { shopId } }),
    getStats: (shopId) => api.get('/sessions/stats', { params: { shopId } }),
    sendHumanReply: (id, message) => api.post(`/sessions/${id}/human-reply`, { message }),
    createSession: (data) => api.post('/diagnosis/create-session', data)
  },
  
  knowledge: {
    getAll: () => api.get('/admin/knowledge-base'),
    get: (id) => api.get(`/admin/knowledge-base/${id}`)
  },
  
  aiUsage: {
    getAll: (page, size) => api.get('/admin/ai-usage', { params: { page, size } }),
    getStats: (days) => api.get('/admin/ai-usage/stats', { params: { days } })
  }
}
