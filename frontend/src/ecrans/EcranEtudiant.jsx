import { useEffect, useState } from 'react'
import {
  deposerExercice,
  listerEtudiants,
  listerMesExercices,
  listerPromotions,
  listerSessions,
  marquerPresence,
} from '../api/index.js'

// Issue #19 (F2, F3, ENF1) : l'étudiant choisit sa promotion, son nom et la
// session dans des listes (EF8), marque sa présence (EF2) puis dépose le lien
// de son exercice (EF3). Les erreurs CODE_INCONNU, DEJA_PRESENT, CODE_EXPIRE
// et LIEN_INVALIDE sont affichées telles que renvoyées par l'API (F3) ;
// aucune règle métier ici. Mise en page utilisable à 360 px (ENF1).
export default function EcranEtudiant() {
  const [promotions, setPromotions] = useState([])
  const [etudiants, setEtudiants] = useState([])
  const [sessions, setSessions] = useState([])
  const [promotionId, setPromotionId] = useState('')
  const [etudiantId, setEtudiantId] = useState('')
  const [sessionId, setSessionId] = useState('')
  const [code, setCode] = useState('')
  const [lien, setLien] = useState('')
  const [chargement, setChargement] = useState(false)
  const [messagePresence, setMessagePresence] = useState(null)
  const [messageDepot, setMessageDepot] = useState(null)
  const [mesExercices, setMesExercices] = useState(null)
  const [erreurExercices, setErreurExercices] = useState(null)

  useEffect(() => {
    listerPromotions().then((liste) => {
      setPromotions(liste)
      if (liste.length > 0) {
        changerPromotion(String(liste[0].id))
      }
    })
  }, [])

  // EF12 (issue #15), note retenue RG23 (issue #34) : la note et son
  // indicateur provisoire viennent de l'API, le front les affiche tels quels.
  function rafraichirMesExercices() {
    if (!etudiantId) return
    setErreurExercices(null)
    listerMesExercices(Number(etudiantId))
      .then(setMesExercices)
      .catch((e) => setErreurExercices(e.message))
  }

  useEffect(() => {
    rafraichirMesExercices()
  }, [etudiantId])

  function changerPromotion(id) {
    setPromotionId(id)
    listerEtudiants(Number(id)).then((liste) => {
      setEtudiants(liste)
      if (liste.length > 0) {
        setEtudiantId(String(liste[0].id))
      }
    })
    listerSessions(Number(id)).then((liste) => {
      setSessions(liste)
      const ouverte = liste.find((s) => s.clotureeAt === null)
      if (ouverte) {
        setSessionId(String(ouverte.id))
      }
    })
  }

  function marquer(event) {
    event.preventDefault()
    setChargement(true)
    setMessagePresence(null)
    marquerPresence(code.trim().toUpperCase(), Number(etudiantId))
      .then(() => {
        setMessagePresence({ succes: true, texte: 'Présence enregistrée. Bon cours !' })
        setCode('')
      })
      .catch((e) => setMessagePresence({ succes: false, texte: `${e.message}` }))
      .finally(() => setChargement(false))
  }

  function deposer(event) {
    event.preventDefault()
    setChargement(true)
    setMessageDepot(null)
    deposerExercice(Number(sessionId), Number(etudiantId), lien.trim())
      .then((exercice) => {
        setMessageDepot({
          succes: true,
          texte:
            exercice.statut === 'EN_ATTENTE_RELECTURE'
              ? 'Exercice déposé : un relecteur a déjà été désigné.'
              : 'Exercice déposé : un relecteur sera désigné dès qu\'un autre étudiant sera présent.',
        })
        setLien('')
      })
      .catch((e) => setMessageDepot({ succes: false, texte: e.message }))
      .finally(() => setChargement(false))
  }

  return (
    <div>
      <section className="carte">
        <h2>Qui êtes-vous ?</h2>
        <label htmlFor="promo-etudiant">Promotion</label>
        <select
          id="promo-etudiant"
          value={promotionId}
          onChange={(e) => changerPromotion(e.target.value)}
        >
          {promotions.map((p) => (
            <option key={p.id} value={p.id}>
              {p.nom}
            </option>
          ))}
        </select>
        <label htmlFor="etudiant">Votre nom</label>
        <select
          id="etudiant"
          value={etudiantId}
          onChange={(e) => setEtudiantId(e.target.value)}
        >
          {etudiants.map((e) => (
            <option key={e.id} value={e.id}>
              {e.nom}
            </option>
          ))}
        </select>
      </section>

      <section className="carte">
        <h2>Marquer sa présence</h2>
        <form onSubmit={marquer}>
          <label htmlFor="session-presence">Session</label>
          <select
            id="session-presence"
            value={sessionId}
            onChange={(e) => setSessionId(e.target.value)}
          >
            {sessions.map((s) => (
              <option key={s.id} value={s.id}>
                {s.titre}
              </option>
            ))}
          </select>
          <label htmlFor="code">Code affiché par le formateur</label>
          <input
            id="code"
            value={code}
            onChange={(e) => setCode(e.target.value)}
            placeholder="6 caractères"
            maxLength={6}
            autoCapitalize="characters"
          />
          <button className="principal" disabled={chargement || !code || !sessionId}>
            {chargement ? 'Envoi…' : 'Je suis présent'}
          </button>
        </form>
        {messagePresence && (
          <p className={'message ' + (messagePresence.succes ? 'succes' : 'erreur')}>
            {messagePresence.texte}
          </p>
        )}
      </section>

      <section className="carte">
        <h2>Déposer son exercice</h2>
        <form onSubmit={deposer}>
          <label htmlFor="session-depot">Session</label>
          <select
            id="session-depot"
            value={sessionId}
            onChange={(e) => setSessionId(e.target.value)}
          >
            {sessions.map((s) => (
              <option key={s.id} value={s.id}>
                {s.titre}
              </option>
            ))}
          </select>
          <label htmlFor="lien">Lien vers votre exercice</label>
          <input
            id="lien"
            value={lien}
            onChange={(e) => setLien(e.target.value)}
            placeholder="https://…"
          />
          <button className="principal" disabled={chargement || !lien || !sessionId}>
            {chargement ? 'Dépôt…' : 'Déposer'}
          </button>
        </form>
        {messageDepot && (
          <p className={'message ' + (messageDepot.succes ? 'succes' : 'erreur')}>
            {messageDepot.texte}
          </p>
        )}
      </section>

      <section className="carte">
        <h2>Mes exercices et mes notes</h2>
        <button type="button" onClick={rafraichirMesExercices} disabled={!etudiantId}>
          Actualiser
        </button>
        {erreurExercices && <p className="message erreur">{erreurExercices}</p>}
        {mesExercices !== null && mesExercices.length === 0 && (
          <p className="texte-doux">Aucun exercice déposé pour le moment.</p>
        )}
        {(mesExercices || []).map((exercice) => (
          <div className="exercice" key={exercice.id}>
            <div className="exercice-titre">
              {exercice.sessionTitre}{' '}
              <span className={'badge ' + classeStatut(exercice.statut)}>
                {libelleStatut(exercice.statut)}
              </span>
            </div>
            {exercice.note !== null && exercice.note !== undefined ? (
              <div>
                Note retenue : <strong>{exercice.note}</strong>
                {exercice.noteProvisoire && (
                  <span className="provisoire"> provisoire — un second relecteur n'a pas encore rendu</span>
                )}
                {exercice.commentaire && <div className="texte-doux">« {exercice.commentaire} »</div>}
              </div>
            ) : (
              <div className="texte-doux">Aucune note reçue pour le moment.</div>
            )}
          </div>
        ))}
      </section>
    </div>
  )
}

// Les libellés et couleurs restent de la présentation : les statuts et la
// note viennent de l'API, aucune règle métier recalculée ici (F3).
function libelleStatut(statut) {
  switch (statut) {
    case 'DEPOSE':
      return 'en attente de relecteur'
    case 'EN_ATTENTE_RELECTURE':
      return 'en attente de relecture'
    case 'RELU':
      return 'relu'
    default:
      return statut
  }
}

function classeStatut(statut) {
  switch (statut) {
    case 'RELU':
      return 'ok'
    case 'EN_ATTENTE_RELECTURE':
      return 'attente'
    default:
      return ''
  }
}
