export default function Field({ label, error, ...inputProps }) {
  return (
    <label className="field">
      <span className="field-label">{label}</span>
      <input aria-invalid={Boolean(error)} className={error ? 'has-error' : ''} {...inputProps} />
      {error && <span className="field-error">{error}</span>}
    </label>
  )
}
