const API_BASE_URL = 'http://localhost:8080';

export const fetchDomande = async (filtro, userEmail, token) => {
  const url = filtro === "tue" 
    ? `${API_BASE_URL}/api/domande/${userEmail}`
    : `${API_BASE_URL}/api/domande/tutteLeDomande`;

  const response = await fetch(url, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    }
  });

  if (!response.ok) {
    throw new Error('Errore nel recupero delle domande');
  }
  
  return response.json();
};

export const fetchImage = async (fileName, token) => {
  try {
    const response = await fetch(`${API_BASE_URL}${fileName}`, {
      method: 'GET',
      headers: {
        "Authorization": `Bearer ${token}`
      }
    });

    if (!response.ok) {
      throw new Error("Errore nel caricamento dell'immagine");
    }

    const contentType = response.headers.get("Content-Type");

    if (!contentType || !contentType.startsWith("image/")) {
      throw new Error("Il file restituito non è un'immagine");
    }

    const blob = await response.blob();
    return URL.createObjectURL(blob);
  } catch (error) {
    console.error("Errore nel caricamento dell'immagine:", error);
    return null;
  }
};

export const deleteDomanda = async (domandaId, token) => {
  const response = await fetch(`${API_BASE_URL}/api/domande/deleteDomanda/${domandaId}`, {
    method: 'DELETE',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    }
  });

  if (!response.ok) {
    throw new Error('Errore nell\'eliminazione della domanda');
  }

  return true;
};

export const updateDomanda = async (domandaId, formData, token) => {
  const response = await fetch(`${API_BASE_URL}/api/domande/updateDomanda/${domandaId}`, {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${token}`,
    },
    body: formData
  });

  if (!response.ok) {
    throw new Error('Errore nell\'aggiornamento della domanda');
  }

  return true;
};

export const creaDomanda = async (formData, token) => {
  const response = await fetch(`${API_BASE_URL}/api/domande/creaDomanda`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
    },
    body: formData
  });

  if (!response.ok) {
    throw new Error('Errore nella creazione della domanda');
  }

  return response.text();
};