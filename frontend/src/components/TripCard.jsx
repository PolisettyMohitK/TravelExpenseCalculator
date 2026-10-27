import { formatDate, formatMoney } from '../utils/trip'

export default function TripCard({ trip, onDelete }) {
  const dayLabel = trip.days === 1 ? 'day' : 'days'
  const travellerLabel = trip.travellers === 1 ? 'traveller' : 'travellers'

  return (
    <article className="card trip-card">
      <header className="trip-card-header">
        <div>
          <h3>{trip.destination}</h3>
          <p className="muted">
            {trip.days} {dayLabel} · {trip.travellers} {travellerLabel} · {formatDate(trip.createdAt)}
          </p>
        </div>
        <button
          type="button"
          className="btn-icon"
          onClick={() => onDelete(trip.id)}
          aria-label={`Delete trip to ${trip.destination}`}
          title="Delete trip"
        >
          ×
        </button>
      </header>

      <dl className="breakdown">
        <div><dt>Travel</dt><dd>{formatMoney(trip.travelCost)}</dd></div>
        <div><dt>Accommodation</dt><dd>{formatMoney(trip.accommodationCost)}</dd></div>
        <div><dt>Food</dt><dd>{formatMoney(trip.foodCost)}</dd></div>
      </dl>

      <footer className="trip-card-footer">
        <div>
          <span className="summary-label">Total</span>
          <strong>{formatMoney(trip.totalCost)}</strong>
        </div>
        <div>
          <span className="summary-label">Per traveller</span>
          <strong className="accent">{formatMoney(trip.costPerTraveller)}</strong>
        </div>
      </footer>
    </article>
  )
}
