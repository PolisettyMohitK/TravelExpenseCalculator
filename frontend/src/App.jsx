import { useState } from 'react'
import { useAuth } from './context/authContext'
import Navbar from './components/Navbar'
import AuthPage from './pages/AuthPage'
import TripsPage from './pages/TripsPage'
import AddTripPage from './pages/AddTripPage'
import TripDetailsPage from './pages/TripDetailsPage'

export default function App() {
  const { user } = useAuth()
  const [page, setPage] = useState('trips')
  const [tripId, setTripId] = useState(null)

  if (!user) return <AuthPage />

  const openTrip = (id) => {
    setTripId(id)
    setPage('details')
  }

  return (
    <>
      <Navbar page={page} onNavigate={setPage} />
      <main className="container">
        {page === 'trips' && <TripsPage onOpen={openTrip} onAdd={() => setPage('add')} />}
        {page === 'add' && <AddTripPage onSaved={openTrip} />}
        {page === 'details' && <TripDetailsPage tripId={tripId} onBack={() => setPage('trips')} />}
      </main>
    </>
  )
}
