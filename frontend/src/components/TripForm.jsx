import { useState } from 'react'
import { calculateTotals, formatMoney, validateTrip } from '../utils/trip'

const EMPTY_FORM = {
  destination: '',
  days: '',
  travellers: '',
  travelCost: '',
  accommodationCost: '',
  foodCost: '',
}

function Field({ label, name, value, error, onChange, ...inputProps }) {
  return (
    <label className="field">
      <span className="field-label">{label}</span>
      <input
        name={name}
        value={value}
        onChange={onChange}
        aria-invalid={Boolean(error)}
        className={error ? 'has-error' : ''}
        {...inputProps}
      />
      {error && <span className="field-error">{error}</span>}
    </label>
  )
}

export default function TripForm({ onSave }) {
  const [form, setForm] = useState(EMPTY_FORM)
  const [errors, setErrors] = useState({})
  const [saving, setSaving] = useState(false)
  const [serverError, setServerError] = useState('')

  const { total, perTraveller } = calculateTotals(form)

  const handleChange = (event) => {
    const { name, value } = event.target
    setForm((prev) => ({ ...prev, [name]: value }))
    setErrors((prev) => ({ ...prev, [name]: undefined }))
  }

  const handleSubmit = async (event) => {
    event.preventDefault()
    const validationErrors = validateTrip(form)
    setErrors(validationErrors)
    if (Object.keys(validationErrors).length > 0) return

    setSaving(true)
    setServerError('')
    try {
      await onSave({
        destination: form.destination.trim(),
        days: Number(form.days),
        travellers: Number(form.travellers),
        travelCost: Number(form.travelCost),
        accommodationCost: Number(form.accommodationCost),
        foodCost: Number(form.foodCost),
      })
      setForm(EMPTY_FORM)
    } catch (err) {
      const data = err.response?.data
      if (data?.errors) setErrors(data.errors)
      setServerError(data?.message ?? 'Could not reach the server. Please try again.')
    } finally {
      setSaving(false)
    }
  }

  const fieldProps = (name) => ({ name, value: form[name], error: errors[name], onChange: handleChange })
  const moneyProps = { type: 'number', min: '0', step: '0.01', inputMode: 'decimal', placeholder: '0' }
  const countProps = { type: 'number', min: '1', step: '1', inputMode: 'numeric', placeholder: '1' }

  return (
    <form className="card trip-form" onSubmit={handleSubmit} noValidate>
      <h2>Plan a trip</h2>

      <fieldset>
        <legend>Trip details</legend>
        <Field label="Destination" placeholder="e.g. Goa" maxLength={100} {...fieldProps('destination')} />
        <div className="row">
          <Field label="Days" {...countProps} {...fieldProps('days')} />
          <Field label="Travellers" {...countProps} {...fieldProps('travellers')} />
        </div>
      </fieldset>

      <fieldset>
        <legend>Expenses</legend>
        <Field label="Travel" {...moneyProps} {...fieldProps('travelCost')} />
        <Field label="Accommodation" {...moneyProps} {...fieldProps('accommodationCost')} />
        <Field label="Food" {...moneyProps} {...fieldProps('foodCost')} />
      </fieldset>

      <div className="summary">
        <div>
          <span className="summary-label">Total trip cost</span>
          <strong className="summary-value">{formatMoney(total)}</strong>
        </div>
        <div>
          <span className="summary-label">Per traveller</span>
          <strong className="summary-value">{formatMoney(perTraveller)}</strong>
        </div>
      </div>

      {serverError && <p className="form-error" role="alert">{serverError}</p>}

      <button type="submit" className="btn-primary" disabled={saving}>
        {saving ? 'Saving…' : 'Save trip'}
      </button>
    </form>
  )
}
