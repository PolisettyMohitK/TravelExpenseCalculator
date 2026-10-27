import { useEffect, useState } from 'react'
import { loginUser, logoutUser, registerUser } from '../api/authApi'
import { api, setAuthToken } from '../api/client'
import { AuthContext } from './authContext'

const STORAGE_KEY = 'trip-planner-user'

function loadUser() {
  try {
    const user = JSON.parse(localStorage.getItem(STORAGE_KEY))
    setAuthToken(user?.token)
    return user
  } catch {
    return null
  }
}

function saveUser(user) {
  setAuthToken(user?.token)
  try {
    if (user) localStorage.setItem(STORAGE_KEY, JSON.stringify(user))
    else localStorage.removeItem(STORAGE_KEY)
  } catch {
    // Storage unavailable (e.g. private mode): stay logged in for this tab only
  }
}

export default function AuthProvider({ children }) {
  const [user, setUser] = useState(loadUser)

  const startSession = (session) => {
    saveUser(session)
    setUser(session)
  }

  const endSession = () => {
    saveUser(null)
    setUser(null)
  }

  // An expired or unknown token sends the user back to the login screen
  useEffect(() => {
    const id = api.interceptors.response.use(null, (err) => {
      if (err.response?.status === 401) endSession()
      return Promise.reject(err)
    })
    return () => api.interceptors.response.eject(id)
  }, [])

  const value = {
    user,
    login: async (credentials) => startSession(await loginUser(credentials)),
    register: async (details) => startSession(await registerUser(details)),
    logout: () => logoutUser().catch(() => {}).finally(endSession),
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
