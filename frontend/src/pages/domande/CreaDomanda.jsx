import React, { useState } from 'react';

const CreaDomanda = () => {
  const [isCreatingDomanda, setIsCreatingDomanda] = useState(false);
  const [argomentoDomanda, setArgomentoDomanda] = useState('');
  const [testoDomanda, setTestoDomanda] = useState('');
  const [utenteEmail, setUtenteEmail] = useState('');


  const handleCreaDomanda = () => {
    if (!argomentoDomanda || !testoDomanda || !utenteEmail) {
      alert('Compila tutti i campi.');
      return;
    }
  
    const domandaData = {
      argomento: argomentoDomanda,
      testoDomanda,
      emailUtente: utenteEmail
    };
  
    console.log(domandaData);
  
    fetch('http://localhost:8080/api/domande/creaDomanda', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(domandaData),
    })
      .then((response) => {
        console.log('Response:', response);  // Log per vedere la risposta del server
        if (!response.ok) {
          throw new Error('Errore nella creazione della domanda');
        }
  
        return response.text().then((text) => {
          if (text) {
            try {
              return JSON.parse(text);  // Prova a fare il parsing se c'è del testo
            } catch (e) {
              console.error('Errore durante il parsing JSON:', e);
              return {};  // Se non è JSON, ritorna un oggetto vuoto
            }
          }
          return {};  // Se non c'è testo, ritorna un oggetto vuoto
        });
      })
      .then(data => {
        if (data) {
          alert('Domanda creata con successo!');
          setArgomentoDomanda(''); // Resetta l'argomento della domanda
          setTestoDomanda(''); // Resetta il testo della domanda
          setUtenteEmail(''); // Resetta l'email
          setIsCreatingDomanda(false); // Nascondi il modulo dopo la creazione
        }
      })
      .catch(error => {
        console.error('Errore:', error);
        alert('Si è verificato un errore durante la creazione della domanda.');
      });
  };

  return (
    <div className="p-4">
      {/* Sezione per la Creazione del Questionario */}
      {!isCreatingDomanda ? (
        <div>
          <button
            onClick={() => setIsCreatingDomanda(true)}
            className="bg-green-500 text-white py-2 px-6 rounded-lg hover:bg-green-700 transition"
          >
            Crea Domanda
          </button>
        </div>
      ) : (
        <div>
        <input
          type="text"
          placeholder="Argomento della Domanda"
          value={argomentoDomanda}
          onChange={(e) => setArgomentoDomanda(e.target.value)}
          className="w-full p-3 mb-4 border border-gray-300 rounded-lg"
        />
        <input
          type="text"
          placeholder="Testo della Domanda"
          value={testoDomanda}
          onChange={(e) => setTestoDomanda(e.target.value)}
          className="w-full p-3 mb-4 border border-gray-300 rounded-lg"
        />
          <input
            type="email"
            placeholder="Email Utente"
            value={utenteEmail}
            onChange={(e) => setUtenteEmail(e.target.value)}
            className="w-full p-3 mb-4 border border-gray-300 rounded-lg"
          />
          <button
            onClick={handleCreaDomanda}
            className="bg-blue-500 text-white py-2 px-6 rounded-lg hover:bg-blue-700 transition"
          >
            Crea
          </button>
          <button
            onClick={() => setIsCreatingDomanda(false)}
            className="bg-red-500 text-white py-2 px-6 rounded-lg hover:bg-red-700 transition ml-4"
          >
            Annulla
          </button>
        </div>
      )}
    </div>
  );
};

export default CreaDomanda;
