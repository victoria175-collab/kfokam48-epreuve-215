import React, { useState } from 'react'
import { createRoot } from 'react-dom/client'
import EcranFormateur from './ecrans/EcranFormateur.jsx'
import EcranEtudiant from './ecrans/EcranEtudiant.jsx'
import EcranRelecteur from './ecrans/EcranRelecteur.jsx'
import './styles.css'

function App() {
  const [onglet, setOnglet] = useState('formateur')

  return (
    <div className="app">
      <header className="entete">
        <h1>Présences et relectures</h1>
        <p className="sous-titre">KFOKAM48 — promotion 2026</p>
        <nav className="onglets">
          <button className={onglet === 'formateur' ? 'actif' : ''} onClick={() => setOnglet('formateur')}>
            Formateur
          </button>
          <button className={onglet === 'etudiant' ? 'actif' : ''} onClick={() => setOnglet('etudiant')}>
            Étudiant
          </button>
          <button className={onglet === 'relecteur' ? 'actif' : ''} onClick={() => setOnglet('relecteur')}>
            Relecteur
          </button>
        </nav>
      </header>
      <main>
        {onglet === 'formateur' && <EcranFormateur />}
        {onglet === 'etudiant' && <EcranEtudiant />}
        {onglet === 'relecteur' && <EcranRelecteur />}
      </main>
    </div>
  )
}

createRoot(document.getElementById('root')).render(<App />)
