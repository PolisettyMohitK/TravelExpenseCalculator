import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api',
})

export const getTrips = () => api.get('/trips').then((res) => res.data)

export const createTrip = (trip) => api.post('/trips', trip).then((res) => res.data)

export const deleteTrip = (id) => api.delete(`/trips/${id}`)
