import { useAuth } from '../context/authContext'

const TABS = [
  { id: 'trips', label: 'My trips' },
  { id: 'add', label: 'Add trip' },
]

export default function Navbar({ page, onNavigate }) {
  const { user, logout } = useAuth()
  // Trip details belong to the "My trips" tab
  const activeTab = page === 'details' ? 'trips' : page

  return (
    <header className="navbar">
      <div className="container navbar-inner">
        <span className="brand">
          <img src="/favicon.svg" alt="" width="28" height="28" />
          Trip Cost Planner
        </span>

        <nav className="tabs">
          {TABS.map((tab) => (
            <button
              key={tab.id}
              type="button"
              className={`tab ${activeTab === tab.id ? 'active' : ''}`}
              onClick={() => onNavigate(tab.id)}
            >
              {tab.label}
            </button>
          ))}
        </nav>

        <div className="user-menu">
          <span className="avatar" title={user.email}>{user.name.charAt(0).toUpperCase()}</span>
          <button type="button" className="btn-ghost" onClick={logout}>Log out</button>
        </div>
      </div>
    </header>
  )
}
