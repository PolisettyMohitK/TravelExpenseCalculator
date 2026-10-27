import { useState } from 'react'
import { errorMessage } from '../api/client'
import { EMPTY_TRIP, calculateTotals, formatMoney, toTripPayload, validateTrip } from '../utils/trip'
import Field from './Field'

const moneyProps = { type: 'number', min: '0', step: '0.01', inputMode: 'decimal', placeholder: '0' }
const countProps = { type: 'number', min: '1', step: '1', inputMode: 'numeric', placeholder: '1' }

export default function TripForm({ initialValues = EMPTY_TRIP, submitLabel, onSubmit, onCancel }) {
  const [form, setForm] = useState(initialValues)
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
      await onSubmit(toTripPayload(form))
    } catch (err) {
      setErrors(err.response?.data?.errors ?? {})
      setServerError(errorMessage(err))
      setSaving(false)
    }
  }

  const fieldProps = (name) => ({ name, value: form[name], error: errors[name], onChange: handleChange })

  return (
    <form className="card form" onSubmit={handleSubmit} noValidate>
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
          <span className="label">Total trip cost</span>
          <strong className="summary-value">{formatMoney(total)}</strong>
        </div>
        <div>
          <span className="label">Per traveller</span>
          <strong className="summary-value">{formatMoney(perTraveller)}</strong>
        </div>
      </div>

      {serverError && <p className="form-error" role="alert">{serverError}</p>}

      <div className="actions">
        {onCancel && (
          <button type="button" className="btn-secondary" onClick={onCancel}>Cancel</button>
        )}
        <button type="submit" className="btn-primary" disabled={saving}>
          {saving ? 'Saving…' : submitLabel}
        </button>
      </div>
    </form>
  )
}
