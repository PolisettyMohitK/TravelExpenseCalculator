import { useState } from 'react'
import { errorMessage } from '../api/client'
import Field from '../components/Field'
import { useAuth } from '../context/authContext'

const EMPTY_FORM = { name: '', email: '', password: '' }

export default function AuthPage() {
  const { login, register } = useAuth()
  const [isRegister, setIsRegister] = useState(false)
  const [form, setForm] = useState(EMPTY_FORM)
  const [errors, setErrors] = useState({})
  const [message, setMessage] = useState('')
  const [submitting, setSubmitting] = useState(false)

  const handleChange = (event) => {
    const { name, value } = event.target
    setForm((prev) => ({ ...prev, [name]: value }))
    setErrors((prev) => ({ ...prev, [name]: undefined }))
  }

  const switchMode = () => {
    setIsRegister((prev) => !prev)
    setErrors({})
    setMessage('')
  }

  const handleSubmit = async (event) => {
    event.preventDefault()
    setSubmitting(true)
    setMessage('')
    try {
      if (isRegister) await register(form)
      else await login({ email: form.email, password: form.password })
    } catch (err) {
      setErrors(err.response?.data?.errors ?? {})
      setMessage(errorMessage(err))
      setSubmitting(false)
    }
  }

  const fieldProps = (name) => ({ name, value: form[name], error: errors[name], onChange: handleChange })

  return (
    <div className="auth-page">
      <div className="auth-intro">
        <img src="/favicon.svg" alt="" width="44" height="44" />
        <h1>Trip Cost Planner</h1>
        <p className="muted">Estimate what your trips will cost and keep every plan in one place.</p>
      </div>

      <form className="card form auth-card" onSubmit={handleSubmit} noValidate>
        <h2>{isRegister ? 'Create your account' : 'Welcome back'}</h2>

        {isRegister && <Field label="Name" autoComplete="name" {...fieldProps('name')} />}
        <Field label="Email" type="email" autoComplete="email" {...fieldProps('email')} />
        <Field
          label="Password"
          type="password"
          autoComplete={isRegister ? 'new-password' : 'current-password'}
          placeholder={isRegister ? 'At least 6 characters' : ''}
          {...fieldProps('password')}
        />

        {message && <p className="form-error" role="alert">{message}</p>}

        <button type="submit" className="btn-primary" disabled={submitting}>
          {submitting ? 'Please wait…' : isRegister ? 'Create account' : 'Log in'}
        </button>

        <p className="muted small center">
          {isRegister ? 'Already have an account?' : 'New here?'}{' '}
          <button type="button" className="link" onClick={switchMode}>
            {isRegister ? 'Log in' : 'Create an account'}
          </button>
        </p>
      </form>
    </div>
  )
}
