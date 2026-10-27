import { createTrip } from '../api/tripApi'
import TripForm from '../components/TripForm'

export default function AddTripPage({ onSaved }) {
  const handleSubmit = async (trip) => {
    const saved = await createTrip(trip)
    onSaved(saved.id)
  }

  return (
    <section className="narrow">
      <div className="page-header">
        <div>
          <h1>Add a trip</h1>
          <p className="muted">Totals update as you type.</p>
        </div>
      </div>
      <TripForm submitLabel="Save trip" onSubmit={handleSubmit} />
    </section>
  )
}
