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
  diagnosis: {
    validate: (sessionUuid, token) => api.get(`/diagnosis/${sessionUuid}/validate`, { params: { token } }),
    chat: (sessionUuid, token, message, images) => {
      const formData = new FormData()
      formData.append('token', token)
      formData.append('message', message)
      if (images && images.length > 0) {
        images.forEach(image => formData.append('images', image))
      }
      return api.post(`/diagnosis/${sessionUuid}/chat`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      })
    },
    feedback: (sessionUuid, thumbsUp) => api.post(`/diagnosis/${sessionUuid}/feedback`, { thumbsUp }),
    resolve: (sessionUuid, resolved) => api.post(`/diagnosis/${sessionUuid}/resolve`, { resolved }),
    getSession: (sessionUuid) => api.get(`/sessions/uuid/${sessionUuid}`)
  }
}
