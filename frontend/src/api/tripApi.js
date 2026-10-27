import { api } from './client'

export const getTrips = () => api.get('/trips').then((res) => res.data)

export const getTrip = (id) => api.get(`/trips/${id}`).then((res) => res.data)

export const createTrip = (trip) => api.post('/trips', trip).then((res) => res.data)

export const updateTrip = (id, trip) => api.put(`/trips/${id}`, trip).then((res) => res.data)

export const deleteTrip = (id) => api.delete(`/trips/${id}`)
