import { useEffect, useState } from 'react'
import { errorMessage } from '../api/client'
import { getTrips } from '../api/tripApi'
import TripCard from '../components/TripCard'

export default function TripsPage({ onOpen, onAdd }) {
  const [trips, setTrips] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    getTrips()
      .then(setTrips)
      .catch((err) => setError(errorMessage(err)))
      .finally(() => setLoading(false))
  }, [])

  return (
    <section>
      <div className="page-header">
        <div>
          <h1>My trips</h1>
          <p className="muted">Select a trip to see its full expense breakdown.</p>
        </div>
        <button type="button" className="btn-primary" onClick={onAdd}>+ Add trip</button>
      </div>

      {loading && <p className="muted">Loading trips…</p>}
      {error && <p className="form-error" role="alert">{error}</p>}

      {!loading && !error && trips.length === 0 && (
        <div className="card empty">
          <h2>No trips yet</h2>
          <p className="muted">Add your first trip to estimate what it will cost.</p>
          <button type="button" className="btn-primary" onClick={onAdd}>Plan a trip</button>
        </div>
      )}

      <div className="trip-grid">
        {trips.map((trip) => (
          <TripCard key={trip.id} trip={trip} onOpen={onOpen} />
        ))}
      </div>
    </section>
  )
}
