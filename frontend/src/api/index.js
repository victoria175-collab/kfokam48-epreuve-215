// F3 (issue #21) : couche d'appel API dediee. Tout le fetch de l'application
// passe par ce module ; les erreurs HTTP sont converties en objet
// { code, message }, identique au format impose du contrat (B2, ENF4).
// Aucune regle metier ici : la moyenne, les statuts et les messages viennent
// de l'API, le front les affiche tels quels.

async function appeler(chemin, options = {}) {
  let reponse;
  try {
    reponse = await fetch(chemin, {
      headers: { 'Content-Type': 'application/json', ...(options.headers || {}) },
      ...options,
    });
  } catch (reseau) {
    throw { code: 'RESEAU_INJOIGNABLE', message: 'Le serveur est injoignable.' };
  }

  if (reponse.status === 204 || reponse.headers.get('content-length') === '0') {
    if (!reponse.ok) {
      throw { code: 'ERREUR_INCONNUE', message: 'Une erreur est survenue.' };
    }
    return null;
  }

  let corps = null;
  const texte = await reponse.text();
  if (texte) {
    try {
      corps = JSON.parse(texte);
    } catch (e) {
      corps = null;
    }
  }

  if (!reponse.ok) {
    if (corps && corps.code && corps.message) {
      throw corps; // format impose { code, message }, affiche tel quel
    }
    throw { code: 'ERREUR_INCONNUE', message: 'Une erreur est survenue.' };
  }

  return corps;
}

export function listerPromotions() {
  return appeler('/api/promotions');
}

export function listerEtudiants(promotionId) {
  return appeler(`/api/promotions/${promotionId}/etudiants`);
}

export function listerSessions(promotionId) {
  return appeler(`/api/sessions?promotionId=${promotionId}`);
}

export function ouvrirSession(titre, promotionId) {
  return appeler('/api/sessions', {
    method: 'POST',
    body: JSON.stringify({ titre, promotionId }),
  });
}

export function marquerPresence(code, etudiantId) {
  return appeler('/api/presences', {
    method: 'POST',
    body: JSON.stringify({ code, etudiantId }),
  });
}

export function deposerExercice(sessionId, etudiantId, lien) {
  return appeler('/api/exercices', {
    method: 'POST',
    body: JSON.stringify({ sessionId, etudiantId, lien }),
  });
}

export function listerRelectures(relecteurId) {
  return appeler(`/api/relectures?relecteurId=${relecteurId}`);
}

export function rendreRelecture(id, note, commentaire, etudiantId) {
  return appeler(`/api/relectures/${id}`, {
    method: 'POST',
    body: JSON.stringify({ note, commentaire }),
    headers: { 'X-Etudiant-Id': String(etudiantId) },
  });
}

export function listerMesExercices(etudiantId) {
  return appeler(`/api/etudiants/${etudiantId}/exercices`);
}

export function tableauDeLaPromotion(promotionId) {
  return appeler(`/api/tableau?promotionId=${promotionId}`);
}
