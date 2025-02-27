// Configurazione di base
const BASE_URL = 'http://localhost:8080/api';

// Funzione per ottenere gli headers di autenticazione
const getAuthHeaders = () => {
  const token = localStorage.getItem('jwt');
  return {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  };
};

/**
 * Ottiene tutte le compilazioni di un utente
 * @param {string} userEmail - Email dell'utente
 * @returns {Promise<Array>} Array di compilazioni
 */
export const getAllCompilazioni = async (userEmail) => {
  try {
    const url = `${BASE_URL}/questionariCompilati/all/utente/${userEmail}`;
    
    const response = await fetch(url, {
      method: 'GET',
      headers: getAuthHeaders()
    });
    
    if (response.status === 204) {
      return [];
    } else if (!response.ok) {
      throw new Error('Errore nel recupero delle compilazioni');
    }
    
    return await response.json();
  } catch (error) {
    console.error("Errore durante il fetch:", error);
    throw error;
  }
};

/**
 * Ottiene le compilazioni definitive di un utente
 * @param {string} userEmail - Email dell'utente
 * @returns {Promise<Array>} Array di compilazioni definitive
 */
export const getCompilazioniDefinitive = async (userEmail) => {
  try {
    const url = `${BASE_URL}/questionariCompilati/definitivi/utente/${userEmail}`;
    
    const response = await fetch(url, {
      method: 'GET',
      headers: getAuthHeaders()
    });
    
    if (response.status === 204) {
      return [];
    } else if (!response.ok) {
      throw new Error('Errore nel recupero delle compilazioni definitive');
    }
    
    return await response.json();
  } catch (error) {
    console.error("Errore durante il fetch:", error);
    throw error;
  }
};

/**
 * Ottiene le compilazioni in sospeso di un utente
 * @param {string} userEmail - Email dell'utente
 * @returns {Promise<Array>} Array di compilazioni in sospeso
 */
export const getCompilazioniInSospeso = async (userEmail) => {
  try {
    const url = `${BASE_URL}/questionariCompilati/inSospeso/utente/${userEmail}`;
    
    const response = await fetch(url, {
      method: 'GET',
      headers: getAuthHeaders()
    });
    
    if (response.status === 204) {
      return [];
    } else if (!response.ok) {
      throw new Error('Errore nel recupero delle compilazioni in sospeso');
    }
    
    return await response.json();
  } catch (error) {
    console.error("Errore durante il fetch:", error);
    throw error;
  }
};

/**
 * Controlla se una compilazione è definitiva
 * @param {number} idCompilazione - ID della compilazione
 * @returns {Promise<boolean>} true se definitiva, false altrimenti
 */
export const checkIsDefinitivo = async (idCompilazione) => {
  try {
    const response = await fetch(
      `${BASE_URL}/questionariCompilati/checkIsDefinitivo/${idCompilazione}`,
      {
        method: 'GET',
        headers: getAuthHeaders()
      }
    );

    if (!response.ok) {
      throw new Error('Errore nel verificare lo stato della compilazione');
    }

    return await response.json();
  } catch (error) {
    console.error("Errore durante la verifica dello stato:", error);
    return false;
  }
};

/**
 * Elimina una compilazione
 * @param {number} idCompilazione - ID della compilazione da eliminare
 * @returns {Promise<boolean>} true se l'eliminazione è riuscita
 */
export const deleteQuestionarioCompilato = async (idCompilazione) => {
  try {
    const response = await fetch(
      `${BASE_URL}/questionariCompilati/deleteQuestionarioCompilato/${idCompilazione}`,
      {
        method: 'DELETE',
        headers: getAuthHeaders()
      }
    );

    if (!response.ok) {
      throw new Error('Errore nella cancellazione del questionario');
    }

    return true;
  } catch (error) {
    console.error('Errore:', error);
    throw error;
  }
};

/**
 * Ottiene i dettagli di una compilazione
 * @param {number} idCompilazione - ID della compilazione
 * @returns {Promise<Object>} Dettagli della compilazione
 */
export const getCompilazioneDetails = async (idCompilazione) => {
  try {
    const response = await fetch(
      `${BASE_URL}/questionariCompilati/${idCompilazione}`,
      {
        method: 'GET',
        headers: getAuthHeaders()
      }
    );

    if (!response.ok) {
      throw new Error('Errore nel recupero dei dettagli della compilazione');
    }

    return await response.json();
  } catch (error) {
    console.error('Errore:', error);
    throw error;
  }
};

/**
 * Ottiene le compilazioni dell'utente filtrate per tipo
 * @param {string} userEmail - Email dell'utente
 * @param {string} filtro - Tipo di filtro (Tutti, Definitivi, In Sospeso)
 * @returns {Promise<Array>} Array di compilazioni filtrate
 */
export const getCompilazioniByFiltro = async (userEmail, filtro) => {
  try {
    let url;
    
    if (filtro === "Definitivi") {
      url = `${BASE_URL}/questionariCompilati/definitivi/utente/${userEmail}`;
    } else if (filtro === "In Sospeso") {
      url = `${BASE_URL}/questionariCompilati/inSospeso/utente/${userEmail}`;
    } else {
      url = `${BASE_URL}/questionariCompilati/all/utente/${userEmail}`;
    }
    
    const response = await fetch(url, {
      method: 'GET',
      headers: getAuthHeaders()
    });
    
    if (response.status === 204) {
      return [];
    } else if (!response.ok) {
      throw new Error('Errore nel recupero delle compilazioni');
    }
    
    return await response.json();
  } catch (error) {
    console.error("Errore durante il fetch:", error);
    throw error;
  }
};

/**
 * Continua la compilazione di un questionario
 * @param {number} idQuestionario - ID del questionario
 * @param {number} idCompilazione - ID della compilazione
 * @returns {Promise<Object>} Dati per continuare la compilazione
 */
export const continueCompilazione = async (idQuestionario, idCompilazione) => {
  try {
    const response = await fetch(
      `${BASE_URL}/questionariCompilati/continue/${idQuestionario}/${idCompilazione}`,
      {
        method: 'GET',
        headers: getAuthHeaders()
      }
    );

    if (!response.ok) {
      throw new Error('Errore nel recupero dei dati per continuare la compilazione');
    }

    return await response.json();
  } catch (error) {
    console.error('Errore:', error);
    throw error;
  }
};

/**
 * Visualizza una compilazione completata
 * @param {number} idCompilazione - ID della compilazione
 * @param {number} idQuestionario - ID del questionario
 * @returns {Promise<Object>} Dati della compilazione
 */
export const visualizzaQuestionarioCompilato = async (idCompilazione, idQuestionario) => {
  try {
    const response = await fetch(
      `${BASE_URL}/questionariCompilati/view/${idCompilazione}/${idQuestionario}`,
      {
        method: 'GET',
        headers: getAuthHeaders()
      }
    );

    if (!response.ok) {
      throw new Error('Errore nel recupero della compilazione');
    }

    return await response.json();
  } catch (error) {
    console.error('Errore:', error);
    throw error;
  }
};

/**
 * Fetch all questions for a specific questionnaire
 * @param {string} idQuestionario - The ID of the questionnaire
 * @returns {Promise} - Promise with the questions data
 */
export const fetchDomandeQuestionario = async (idQuestionario) => {
  const response = await fetch(`${BASE_URL}/questionari/${idQuestionario}/domande`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  });
  
  if (!response.ok) {
    throw new Error('Errore nel recupero delle domande');
  }
  
  return response.json();
};

/**
 * Fetch a specific questionnaire completion
 * @param {string} idCompilazione - The ID of the completion
 * @returns {Promise} - Promise with the completion data
 */
export const fetchQuestionarioCompilato = async (idCompilazione) => {
  const response = await fetch(`${BASE_URL}/questionariCompilati/${idCompilazione}`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  });
  
  if (!response.ok) {
    throw new Error('Errore nel recupero della compilazione');
  }
  
  return response.json();
};

/**
 * Fetch all responses for a specific questionnaire completion
 * @param {string} idCompilazione - The ID of the completion
 * @returns {Promise} - Promise with the responses data
 */
export const fetchRisposteCompilazione = async (idCompilazione) => {
  const response = await fetch(`${BASE_URL}/questionariCompilati/${idCompilazione}/risposte`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  });
  
  if (!response.ok) {
    throw new Error('Errore nel recupero delle risposte');
  }
  
  return response.json();
};

/**
 * Submit a new questionnaire completion
 * @param {Object} compilazione - The completion data
 * @returns {Promise} - Promise with the created completion
 */
export const creaCompilazione = async (compilazione) => {
  const response = await fetch(`${BASE_URL}/questionariCompilati`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(compilazione),
  });
  
  if (!response.ok) {
    throw new Error('Errore nella creazione della compilazione');
  }
  
  return response.json();
};

/**
 * Submit a response for a questionnaire completion
 * @param {string} idCompilazione - The ID of the completion
 * @param {Object} risposta - The response data
 * @returns {Promise} - Promise with the created response
 */
export const inviaRisposta = async (idCompilazione, risposta) => {
  const response = await fetch(`${BASE_URL}/questionariCompilati/${idCompilazione}/risposte`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(risposta),
  });
  
  if (!response.ok) {
    throw new Error('Errore nell\'invio della risposta');
  }
  
  return response.json();
};

/**
 * Get all completions for a specific questionnaire
 * @param {string} idQuestionario - The ID of the questionnaire
 * @returns {Promise} - Promise with the completions data
 */
export const fetchCompilazioniQuestionario = async (idQuestionario) => {
  const response = await fetch(`${BASE_URL}/questionari/${idQuestionario}/compilazioni`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  });
  
  if (!response.ok) {
    throw new Error('Errore nel recupero delle compilazioni');
  }
  
  return response.json();
};

/**
 * Delete a specific questionnaire completion
 * @param {string} idCompilazione - The ID of the completion to delete
 * @returns {Promise} - Promise with the deletion result
 */
export const eliminaCompilazione = async (idCompilazione) => {
  const response = await fetch(`${BASE_URL}/questionariCompilati/${idCompilazione}`, {
    method: 'DELETE',
    headers: {
      'Content-Type': 'application/json',
    },
  });
  
  if (!response.ok) {
    throw new Error('Errore nell\'eliminazione della compilazione');
  }
  
  return response.json();
};
export const fetchQuestionarioById = async (idQuestionario) => {
  try {
    const response = await fetch(`${BASE_URL}/questionari/${idQuestionario}/domande`);
    if (!response.ok) {
      throw new Error(`Errore nella fetch: ${response.status}`);
    }
    return await response.json();
  } catch (error) {
    console.error("Errore nel recupero del questionario:", error);
    throw error;
  }
};

export const fetchCompilazioneById = async (idCompilazione) => {
  try {
    const response = await fetch(`${BASE_URL}/questionariCompilati/${idCompilazione}`);
    if (!response.ok) {
      throw new Error('Errore nel recupero dei dati della compilazione');
    }
    return await response.json();
  } catch (error) {
    console.error('Errore nel recupero della compilazione:', error);
    throw error;
  }
};

export const fetchRisposteByCompilazione = async (idCompilazione) => {
  try {
    const response = await fetch(`${BASE_URL}/risposte/${idCompilazione}`);
    if (!response.ok) {
      throw new Error('Errore nel recupero delle risposte');
    }
    return await response.json();
  } catch (error) {
    console.error('Errore nel recupero delle risposte:', error);
    throw error;
  }
};

export const creaNuovaCompilazione = async (idQuestionario, userEmail) => {
  try {
    const response = await fetch(
      `${BASE_URL}/risposte/creaCompilazione?idQuestionario=${idQuestionario}&userEmail=${userEmail}`, 
      {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
      }
    );
    const data = await response.json();
    if (!response.ok) {
      throw new Error(data.error || 'Errore nella creazione della compilazione');
    }
    return data.idCompilazione;
  } catch (error) {
    console.error('Errore nella creazione della compilazione:', error);
    throw error;
  }
};

export const salvaRisposta = async (risposta) => {
  try {
    const response = await fetch(`${BASE_URL}/risposte/salvaRisposta`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(risposta),
    });
    if (!response.ok) {
      throw new Error('Errore nel salvataggio della risposta');
    }
    return await response.json();
  } catch (error) {
    console.error('Errore nel salvataggio della risposta:', error);
    throw error;
  }
};

export const finalizzaCompilazione = async (idCompilazione) => {
  try {
    const response = await fetch(
      `${BASE_URL}/risposte/finalizzaCompilazione?idCompilazione=${idCompilazione}`, 
      {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
      }
    );
    if (!response.ok) {
      throw new Error('Errore nella finalizzazione della compilazione');
    }
    return await response.json();
  } catch (error) {
    console.error('Errore nella finalizzazione della compilazione:', error);
    throw error;
  }
};

export const inviaEmail = async (idCompilazione, userEmail) => {
  try {
    const jwt = localStorage.getItem('jwt');
    const response = await fetch(`${BASE_URL}/risposte/inviaEmail`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${jwt}`,
      },
      body: JSON.stringify({ idCompilazione, userEmail }),
    });
    if (!response.ok) {
      throw new Error('Errore nell\'invio dell\'email');
    }
    return await response.json();
  } catch (error) {
    console.error('Errore nell\'invio dell\'email:', error);
    throw error;
  }
};