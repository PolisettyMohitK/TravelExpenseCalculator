import { api } from './client'

export const registerUser = (details) => api.post('/auth/register', details).then((res) => res.data)

export const loginUser = (credentials) => api.post('/auth/login', credentials).then((res) => res.data)

export const logoutUser = () => api.post('/auth/logout')
