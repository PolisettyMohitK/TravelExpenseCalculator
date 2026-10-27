import TripCard from './TripCard'

export default function TripList({ trips, loading, error, onDelete }) {
  return (
    <section className="trip-list">
      <h2>
        Saved trips {trips.length > 0 && <span className="count">{trips.length}</span>}
      </h2>

      {loading && <p className="muted">Loading trips…</p>}
      {error && <p className="form-error" role="alert">{error}</p>}
      {!loading && !error && trips.length === 0 && (
        <div className="card empty">
          <p>No trips saved yet.</p>
          <p className="muted">Fill in the form to plan your first trip.</p>
        </div>
      )}

      <div className="trip-grid">
        {trips.map((trip) => (
          <TripCard key={trip.id} trip={trip} onDelete={onDelete} />
        ))}
      </div>
    </section>
  )
}
