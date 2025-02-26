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

// Funzione per ottenere gli headers semplici
const getHeaders = () => {
  return {
    'Content-Type': 'application/json'
  };
};

export const getQuestionariByEmail = async (userEmail) => {
  try {
    const response = await fetch(`${BASE_URL}/questionari/${userEmail}`, {
      method: 'GET',
      headers: getAuthHeaders(),
    });
    
    if (!response.ok) throw new Error('Errore nel recupero dei questionari');
    return await response.json();
  } catch (error) {
    console.error('Errore:', error);
    throw error;
  }
};

export const deleteQuestionario = async (questionarioId) => {
  try {
    const response = await fetch(`${BASE_URL}/questionari/deleteQuestionario/${questionarioId}`, {
      method: 'DELETE',
      headers: getAuthHeaders(),
    });
    
    if (!response.ok) {
      throw new Error('Errore nell\'eliminazione del questionario');
    }
    return true;
  } catch (error) {
    console.error('Errore:', error);
    throw error;
  }
};

export const updateQuestionario = async (questionarioId, questionarioData) => {
  try {
    const response = await fetch(`${BASE_URL}/questionari/updateQuestionario/${questionarioId}`, {
      method: 'PUT',
      headers: getAuthHeaders(),
      body: JSON.stringify(questionarioData),
    });
    
    if (!response.ok) {
      throw new Error("Errore nell'aggiornamento del questionario");
    }
    const text = await response.text();
    return text ? JSON.parse(text) : {};
  } catch (error) {
    console.error('Errore:', error);
    throw error;
  }
};

export const getTutteLeDomande = async () => {
  try {
    const response = await fetch(`${BASE_URL}/domande/tutteLeDomande`, {
      method: 'GET',
      headers: getAuthHeaders(),
    });
    
    if (!response.ok) throw new Error('Errore nel recupero delle domande');
    return await response.json();
  } catch (error) {
    console.error('Errore nel recupero delle domande:', error);
    throw error;
  }
};

export const getDomandeByQuestionario = async (questionarioId) => {
  try {
    const response = await fetch(`${BASE_URL}/domande/domandeByQuestionario/${questionarioId}`, {
      method: 'GET',
      headers: getAuthHeaders(),
    });
    
    if (!response.ok) throw new Error('Errore nel recupero delle domande del questionario');
    return await response.json();
  } catch (error) {
    console.error('Errore:', error);
    throw error;
  }
};

export const getAllDomande = async () => {
  try {
    const response = await fetch(`${BASE_URL}/domande/tutteLeDomande`, {
      headers: getAuthHeaders()
    });
    
    if (!response.ok) {
      throw new Error(`Errore nel recupero domande: ${response.status}`);
    }
    
    return await response.json();
  } catch (error) {
    console.error("Errore nel recupero domande:", error);
    throw error;
  }
};

export const creaQuestionario = async (questionarioData) => {
  try {
    const response = await fetch(`${BASE_URL}/questionari/creaQuestionario`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify(questionarioData)
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(`Errore nella creazione del questionario: ${errorText || response.status}`);
    }

    const text = await response.text();
    return text ? JSON.parse(text) : {};
  } catch (error) {
    console.error('Errore:', error);
    throw error;
  }
};

export const getCompilazioniUtenti = async (userEmail, idQuestionario) => {
  try {
    const url = `${BASE_URL}/questionariCompilati/others/${userEmail}/${idQuestionario}`;
    
    const response = await fetch(url, {
      method: 'GET',
      headers: getAuthHeaders()
    });
    
    if (response.status === 204) {
      console.log('Nessuna compilazione trovata.');
      return [];
    }
    
    if (!response.ok) {
      throw new Error('Errore nel recupero delle compilazioni');
    }
    
    return await response.json();
  } catch (error) {
    console.error('Errore:', error);
    throw error;
  }
};

export const inviaEmailNotifica = async (compilazioneToDelete, userCompilazioneToDelete) => {
  try {
    const response = await fetch(`${BASE_URL}/questionariCompilati/inviaEmail`, {
      method: 'POST',
      headers: getAuthHeaders(),
      body: JSON.stringify({
        compilazioneToDelete,
        userCompilazioneToDelete
      }),
    });

    if (!response.ok) {
      throw new Error("Errore durante l'invio dell'email");
    }

    return true;
  } catch (error) {
    console.error('Errore:', error);
    throw error;
  }
};

export const deleteQuestionarioCompilato = async (idCompilazione) => {
  try {
    const response = await fetch(
      `${BASE_URL}/questionariCompilati/deleteQuestionarioCompilato/${idCompilazione}`,
      {
        method: 'DELETE',
        headers: getAuthHeaders(),
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

export const getQuestionarioDetails = async (id) => {
  try {
    const response = await fetch(`${BASE_URL}/questionari/${id}/view`, {
      method: 'GET',
      headers: getHeaders(),
    });

    if (!response.ok) {
      throw new Error(`Errore HTTP: ${response.status}`);
    }
    
    return await response.json();
  } catch (error) {
    console.error('Errore nel recupero del questionario:', error);
    throw error;
  }
};

export const getQuestionarioDomande = async (id) => {
  try {
    const response = await fetch(`${BASE_URL}/questionari/${id}/domande`, {
      method: 'GET',
      headers: getHeaders(),
    });

    if (!response.ok) {
      throw new Error(`Errore HTTP: ${response.status}`);
    }
    
    return await response.json();
  } catch (error) {
    console.error('Errore nel recupero delle domande:', error);
    throw error;
  }
  
};

