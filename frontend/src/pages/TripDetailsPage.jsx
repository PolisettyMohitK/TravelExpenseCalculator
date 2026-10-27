import { useEffect, useState } from 'react'
import { errorMessage } from '../api/client'
import { deleteTrip, getTrip, updateTrip } from '../api/tripApi'
import TripForm from '../components/TripForm'
import { EXPENSES, formatDate, formatMoney, pluralize, toFormValues } from '../utils/trip'

export default function TripDetailsPage({ tripId, onBack }) {
  const [trip, setTrip] = useState(null)
  const [error, setError] = useState('')
  const [editing, setEditing] = useState(false)

  useEffect(() => {
    getTrip(tripId)
      .then(setTrip)
      .catch((err) => setError(errorMessage(err)))
  }, [tripId])

  const handleUpdate = async (values) => {
    setTrip(await updateTrip(tripId, values))
    setEditing(false)
  }

  const handleDelete = async () => {
    if (!window.confirm(`Delete your trip to ${trip.destination}?`)) return
    try {
      await deleteTrip(tripId)
      onBack()
    } catch (err) {
      setError(errorMessage(err))
    }
  }

  const backLink = (
    <button type="button" className="link back" onClick={onBack}>← Back to my trips</button>
  )

  if (error) return <section>{backLink}<p className="form-error" role="alert">{error}</p></section>
  if (!trip) return <section>{backLink}<p className="muted">Loading trip…</p></section>

  if (editing) {
    return (
      <section className="narrow">
        {backLink}
        <div className="page-header"><h1>Edit trip</h1></div>
        <TripForm
          initialValues={toFormValues(trip)}
          submitLabel="Save changes"
          onSubmit={handleUpdate}
          onCancel={() => setEditing(false)}
        />
      </section>
    )
  }

  return (
    <section className="narrow">
      {backLink}

      <div className="page-header">
        <div>
          <h1>{trip.destination}</h1>
          <p className="muted">
            {pluralize(trip.days, 'day')} · {pluralize(trip.travellers, 'traveller')} · Added {formatDate(trip.createdAt)}
          </p>
        </div>
        <div className="actions">
          <button type="button" className="btn-secondary" onClick={() => setEditing(true)}>Edit</button>
          <button type="button" className="btn-danger" onClick={handleDelete}>Delete</button>
        </div>
      </div>

      <div className="stats">
        <div className="card stat">
          <span className="label">Total trip cost</span>
          <strong>{formatMoney(trip.totalCost)}</strong>
        </div>
        <div className="card stat highlight">
          <span className="label">Cost per traveller</span>
          <strong>{formatMoney(trip.costPerTraveller)}</strong>
        </div>
      </div>

      <div className="card breakdown">
        <h2>Expense breakdown</h2>
        {EXPENSES.map(({ key, label }) => {
          const share = trip.totalCost > 0 ? (trip[key] / trip.totalCost) * 100 : 0
          return (
            <div key={key} className="breakdown-row">
              <div className="breakdown-text">
                <span>{label}</span>
                <span>
                  {formatMoney(trip[key])} <span className="muted small">({Math.round(share)}%)</span>
                </span>
              </div>
              <div className="bar"><div className="bar-fill" style={{ width: `${share}%` }} /></div>
            </div>
          )
        })}
      </div>
    </section>
  )
}
