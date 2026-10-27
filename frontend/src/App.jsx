import { useEffect, useState } from 'react'
import { createTrip, deleteTrip, getTrips } from './api/tripApi'
import TripForm from './components/TripForm'
import TripList from './components/TripList'

export default function App() {
  const [trips, setTrips] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    getTrips()
      .then(setTrips)
      .catch(() => setError('Could not load saved trips. Is the server running?'))
      .finally(() => setLoading(false))
  }, [])

  const handleSave = async (trip) => {
    const saved = await createTrip(trip)
    setTrips((prev) => [saved, ...prev])
    setError('')
  }

  const handleDelete = async (id) => {
    try {
      await deleteTrip(id)
      setTrips((prev) => prev.filter((trip) => trip.id !== id))
    } catch {
      setError('Could not delete the trip. Please try again.')
    }
  }

  return (
    <div className="app">
      <header className="app-header">
        <h1>Trip Cost Planner</h1>
        <p className="muted">Estimate what a trip will cost and keep a record of your plans.</p>
      </header>

      <main className="layout">
        <TripForm onSave={handleSave} />
        <TripList trips={trips} loading={loading} error={error} onDelete={handleDelete} />
      </main>
    </div>
  )
}
