import { useEffect, useState } from 'react'
import {
  listerPromotions,
  ouvrirSession,
  tableauDeLaPromotion,
} from '../api/index.js'

// Issue #18 (F2, F3) : le formateur ouvre une session (EF1) et consulte le
// tableau de sa promotion (EF7). La moyenne est affichée telle que renvoyée
// par l'API, jamais recalculée ici (RG17, F3). États de chargement et
// d'erreur gérés (F3) ; les erreurs apparaissent au format { code, message }.
export default function EcranFormateur() {
  const [promotions, setPromotions] = useState(null)
  const [promotionId, setPromotionId] = useState('')
  const [titre, setTitre] = useState('')
  const [sessionCreee, setSessionCreee] = useState(null)
  const [chargementSession, setChargementSession] = useState(false)
  const [erreurSession, setErreurSession] = useState(null)
  const [tableau, setTableau] = useState(null)
  const [chargementTableau, setChargementTableau] = useState(false)
  const [erreurTableau, setErreurTableau] = useState(null)

  useEffect(() => {
    listerPromotions()
      .then((liste) => {
        setPromotions(liste)
        if (liste.length > 0) {
          setPromotionId(String(liste[0].id))
        }
      })
      .catch((e) => setErreurSession(e))
  }, [])

  function ouvrir(event) {
    event.preventDefault()
    setChargementSession(true)
    setErreurSession(null)
    setSessionCreee(null)
    ouvrirSession(titre, Number(promotionId))
      .then((s) => {
        setSessionCreee(s)
        setTitre('')
        chargerTableau(Number(promotionId))
      })
      .catch(setErreurSession)
      .finally(() => setChargementSession(false))
  }

  function chargerTableau(id) {
    setChargementTableau(true)
    setErreurTableau(null)
    tableauDeLaPromotion(id)
      .then(setTableau)
      .catch(setErreurTableau)
      .finally(() => setChargementTableau(false))
  }

  useEffect(() => {
    if (promotionId) {
      chargerTableau(Number(promotionId))
    }
  }, [promotionId])

  return (
    <div>
      <section className="carte">
        <h2>Ouvrir une session</h2>
        <form onSubmit={ouvrir}>
          <label htmlFor="promotion">Promotion</label>
          <select
            id="promotion"
            value={promotionId}
            onChange={(e) => setPromotionId(e.target.value)}
          >
            {promotions === null && <option>Chargement…</option>}
            {promotions &&
              promotions.map((p) => (
                <option key={p.id} value={p.id}>
                  {p.nom}
                </option>
              ))}
          </select>
          <label htmlFor="titre">Titre de la session</label>
          <input
            id="titre"
            value={titre}
            onChange={(e) => setTitre(e.target.value)}
            placeholder="Ex. Architecture logicielle"
          />
          <button className="principal" disabled={chargementSession || !titre}>
            {chargementSession ? 'Ouverture…' : 'Ouvrir la session'}
          </button>
        </form>
        {sessionCreee && (
          <p className="message succes">
            Session ouverte. Code de présence à afficher :{' '}
            <span className="code-presence">{sessionCreee.code}</span>
            <br />
            Valable jusqu'à {new Date(sessionCreee.expirationAt).toLocaleTimeString('fr-FR')}.
          </p>
        )}
        {erreurSession && (
          <p className="message erreur">
            {erreurSession.message}
          </p>
        )}
      </section>

      <section className="carte">
        <h2>Tableau de la promotion</h2>
        {chargementTableau && <p className="chargement">Chargement du tableau…</p>}
        {erreurTableau && (
          <p className="message erreur">
            {erreurTableau.code} — {erreurTableau.message}
          </p>
        )}
        {tableau && tableau.length > 0 && (
          <table>
            <thead>
              <tr>
                <th>Étudiant</th>
                <th>Présences</th>
                <th>Exercices</th>
                <th>Moyenne</th>
                <th>Relectures dues</th>
              </tr>
            </thead>
            <tbody>
              {tableau.map((ligne) => (
                <tr key={ligne.etudiantId}>
                  <td>{ligne.nom}</td>
                  <td>{ligne.presences}</td>
                  <td>{ligne.exercicesDeposes}</td>
                  <td>{ligne.moyenne === null ? '—' : ligne.moyenne}</td>
                  <td>{ligne.relecturesEnAttente}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>
    </div>
  )
}
