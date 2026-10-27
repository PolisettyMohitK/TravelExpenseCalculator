const currency = new Intl.NumberFormat('en-IN', {
  style: 'currency',
  currency: 'INR',
  maximumFractionDigits: 2,
})

export const formatMoney = (value) => currency.format(Number(value) || 0)

export const formatDate = (iso) =>
  new Date(iso).toLocaleDateString(undefined, { day: 'numeric', month: 'short', year: 'numeric' })

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

  for (const [field, label] of [
    ['travelCost', 'Travel cost'],
    ['accommodationCost', 'Accommodation cost'],
    ['foodCost', 'Food cost'],
  ]) {
    if (form[field] === '') errors[field] = `${label} is required`
    else if (Number(form[field]) < 0) errors[field] = `${label} cannot be negative`
  }

  return errors
}
