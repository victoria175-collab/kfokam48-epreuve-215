import { useEffect, useState } from 'react'
import {
  listerEtudiants,
  listerPromotions,
  listerRelectures,
  rendreRelecture,
} from '../api/index.js'

// Issue #20 (F2, F3) : le relecteur choisit son nom (EF8), voit les relectures
// qui lui sont affectées (EF5) — lien, session, état — et rend une note entière
// de 0 à 20 avec un commentaire (EF6). Le nom de l'auteur n'apparaît nulle part
// (RG16) ; les erreurs NOTE_INVALIDE, AUTO_RELECTURE, RELECTURE_DEJA_RENDUE
// sont affichées telles que renvoyées par l'API (F3).
export default function EcranRelecteur() {
  const [promotions, setPromotions] = useState([])
  const [etudiants, setEtudiants] = useState([])
  const [promotionId, setPromotionId] = useState('')
  const [etudiantId, setEtudiantId] = useState('')
  const [relectures, setRelectures] = useState(null)
  const [chargement, setChargement] = useState(false)
  const [erreur, setErreur] = useState(null)
  const [notesEnCours, setNotesEnCours] = useState({})

  useEffect(() => {
    listerPromotions().then((liste) => {
      setPromotions(liste)
      if (liste.length > 0) {
        changerPromotion(String(liste[0].id))
      }
    })
  }, [])

  function changerPromotion(id) {
    setPromotionId(id)
    setRelectures(null)
    listerEtudiants(Number(id)).then((liste) => {
      setEtudiants(liste)
      if (liste.length > 0) {
        setEtudiantId(String(liste[0].id))
      }
    })
  }

  function chargerRelectures(id) {
    setChargement(true)
    setErreur(null)
    listerRelectures(Number(id))
      .then(setRelectures)
      .catch(setErreur)
      .finally(() => setChargement(false))
  }

  function rendre(event, relecture) {
    event.preventDefault()
    const formulaire = new FormData(event.target)
    const note = Number(formulaire.get('note'))
    const commentaire = formulaire.get('commentaire')
    setNotesEnCours((etat) => ({ ...etat, [relecture.id]: true }))
    setErreur(null)
    rendreRelecture(relecture.id, note, commentaire, Number(etudiantId))
      .then(() => chargerRelectures(etudiantId))
      .catch((e) => setErreur(e))
      .finally(() => setNotesEnCours((etat) => ({ ...etat, [relecture.id]: false })))
  }

  return (
    <div>
      <section className="carte">
        <h2>Mes relectures</h2>
        <label htmlFor="promo-relecteur">Promotion</label>
        <select
          id="promo-relecteur"
          value={promotionId}
          onChange={(e) => changerPromotion(e.target.value)}
        >
          {promotions.map((p) => (
            <option key={p.id} value={p.id}>
              {p.nom}
            </option>
          ))}
        </select>
        <label htmlFor="relecteur">Votre nom</label>
        <select
          id="relecteur"
          value={etudiantId}
          onChange={(e) => {
            setEtudiantId(e.target.value)
            chargerRelectures(e.target.value)
          }}
        >
          {etudiants.map((e) => (
            <option key={e.id} value={e.id}>
              {e.nom}
            </option>
          ))}
        </select>
        <button className="principal" onClick={() => chargerRelectures(etudiantId)}>
          Actualiser
        </button>
      </section>

      {chargement && <p className="chargement">Chargement des relectures…</p>}
      {erreur && (
        <p className="message erreur">
          {erreur.code} — {erreur.message}
        </p>
      )}

      {relectures && relectures.length === 0 && (
        <p className="chargement">Aucune relecture ne vous est affectée pour l'instant.</p>
      )}

      {relectures &&
        relectures.map((r) => (
          <section className="carte" key={r.id}>
            <h2>
              Exercice de la session « {r.sessionTitre} »{' '}
              {r.rendue ? (
                <span className="badge ok">rendue</span>
              ) : (
                <span className="badge attente">à rendre</span>
              )}
            </h2>
            <p>
              Lien de l'exercice :{' '}
              <a href={r.lienExercice} target="_blank" rel="noreferrer">
                {r.lienExercice}
              </a>
            </p>
            {!r.rendue && (
              <form onSubmit={(event) => rendre(event, r)}>
                <label htmlFor={'note-' + r.id}>Note (entier de 0 à 20)</label>
                <input
                  id={'note-' + r.id}
                  name="note"
                  type="number"
                  min={0}
                  max={20}
                  step={1}
                  required
                />
                <label htmlFor={'commentaire-' + r.id}>Commentaire</label>
                <input
                  id={'commentaire-' + r.id}
                  name="commentaire"
                  placeholder="Un mot sur le travail"
                />
                <button
                  className="principal"
                  disabled={notesEnCours[r.id]}
                >
                  {notesEnCours[r.id] ? 'Envoi…' : 'Rendre la relecture'}
                </button>
                <p className="chargement">
                  Une relecture rendue est définitive.
                </p>
              </form>
            )}
          </section>
        ))}
    </div>
  )
}
