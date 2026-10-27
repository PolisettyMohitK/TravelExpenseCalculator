const currency = new Intl.NumberFormat('en-IN', {
  style: 'currency',
  currency: 'INR',
  maximumFractionDigits: 2,
})

export const formatMoney = (value) => currency.format(Number(value) || 0)

export const formatDate = (iso) =>
  new Date(iso).toLocaleDateString(undefined, { day: 'numeric', month: 'short', year: 'numeric' })

export const pluralize = (count, word) => `${count} ${word}${count === 1 ? '' : 's'}`

export const EMPTY_TRIP = {
  destination: '',
  days: '',
  travellers: '',
  travelCost: '',
  accommodationCost: '',
  foodCost: '',
}

export const EXPENSES = [
  { key: 'travelCost', label: 'Travel' },
  { key: 'accommodationCost', label: 'Accommodation' },
  { key: 'foodCost', label: 'Food' },
]

// Form inputs hold strings; convert a saved trip into form values and back
export const toFormValues = (trip) =>
  Object.fromEntries(Object.keys(EMPTY_TRIP).map((key) => [key, String(trip[key])]))

export const toTripPayload = (form) => ({
  destination: form.destination.trim(),
  days: Number(form.days),
  travellers: Number(form.travellers),
  travelCost: Number(form.travelCost),
  accommodationCost: Number(form.accommodationCost),
  foodCost: Number(form.foodCost),
})

// Blank or invalid amounts count as 0 in the live preview
const toAmount = (value) => Math.max(Number(value) || 0, 0)

export function calculateTotals({ travelCost, accommodationCost, foodCost, travellers }) {
  const total = toAmount(travelCost) + toAmount(accommodationCost) + toAmount(foodCost)
  const people = Number(travellers)
  return {
    total,
    perTraveller: people > 0 ? total / people : 0,
  }
}

// Mirrors the backend rules so users get instant feedback
export function validateTrip(form) {
  const errors = {}

  if (!form.destination.trim()) errors.destination = 'Destination is required'

  if (!Number.isInteger(Number(form.days)) || Number(form.days) <= 0) {
    errors.days = 'Days must be greater than 0'
  }
  if (!Number.isInteger(Number(form.travellers)) || Number(form.travellers) <= 0) {
    errors.travellers = 'Travellers must be greater than 0'
  }

  for (const { key, label } of EXPENSES) {
    if (form[key] === '') errors[key] = `${label} cost is required`
    else if (Number(form[key]) < 0) errors[key] = `${label} cost cannot be negative`
  }

  return errors
}
