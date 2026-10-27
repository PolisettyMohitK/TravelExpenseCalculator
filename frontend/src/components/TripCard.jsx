import { formatDate, formatMoney, pluralize } from '../utils/trip'

export default function TripCard({ trip, onOpen }) {
  return (
    <button type="button" className="card trip-card" onClick={() => onOpen(trip.id)}>
      <span className="trip-card-title">{trip.destination}</span>
      <span className="muted small">
        {pluralize(trip.days, 'day')} · {pluralize(trip.travellers, 'traveller')}
      </span>

      <span className="trip-card-footer">
        <span>
          <span className="label">Total</span>
          <strong>{formatMoney(trip.totalCost)}</strong>
        </span>
        <span className="align-right">
          <span className="label">Per traveller</span>
          <strong className="accent">{formatMoney(trip.costPerTraveller)}</strong>
        </span>
      </span>

      <span className="muted small">Added {formatDate(trip.createdAt)}</span>
    </button>
  )
}
