import React, { useState } from 'react';

const CreaDomanda = ({ user, setUpdateDomande }) => {
  const [isCreatingDomanda, setIsCreatingDomanda] = useState(false);
  const [argomentoDomanda, setArgomentoDomanda] = useState('');
  const [testoDomanda, setTestoDomanda] = useState('');
  const [immagineFile, setImmagineFile] = useState(null);
  const [opzioni, setOpzioni] = useState([]); // Inizialmente senza opzioni


  // Funzione per aggiungere una nuova opzione
  const aggiungiOpzione = () => {
    setOpzioni([...opzioni, '']);
  };

  // Funzione per aggiornare il valore di un'opzione
  const modificaOpzione = (index, value) => {
    const nuoveOpzioni = [...opzioni];
    nuoveOpzioni[index] = value;
    setOpzioni(nuoveOpzioni);
  };

  // Funzione per rimuovere un'opzione
  const rimuoviOpzione = (index) => {
    const nuoveOpzioni = opzioni.filter((_, i) => i !== index);
    setOpzioni(nuoveOpzioni);
  };

  const handleImageUpload = (e) => {
    const file = e.target.files[0];
    if (file) {
      setImmagineFile(file); // Salva direttamente il file
    }
  };

  const handleCreaDomanda = () => {
    if (!argomentoDomanda || !testoDomanda) {
        alert('Compila tutti i campi.');
        return;
    }

    const formData = new FormData();
    formData.append('argomento', argomentoDomanda);
    formData.append('testoDomanda', testoDomanda);
    formData.append('emailUtente', user.email);

    if (immagineFile) {
        formData.append('imageFile', immagineFile);
    }

    // Aggiungiamo "opzioni" solo se ci sono opzioni valide
    if (opzioni.length > 0) {
        formData.append('opzioni', JSON.stringify(opzioni));
    }
    for (let pair of formData.entries()) {
      console.log(pair[0] + ':', pair[1]);
    }

    // console.log(domandaData);
    const token = localStorage.getItem("jwt");

    fetch('http://localhost:8080/api/domande/creaDomanda', {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
        },
        body: formData,
    })
    .then((response) => {
        if (!response.ok) {
            throw new Error('Errore nella creazione della domanda');
        }
        return response.text();
    })
    .then((data) => {
        if (data) {
            console.log("Risposta dal server:", data);
        }
        alert('Domanda creata con successo!');
        setArgomentoDomanda('');
        setTestoDomanda('');
        setImmagineFile(null);
        setOpzioni([]);
        setIsCreatingDomanda(false);
        setUpdateDomande(true)
    })
    .catch((error) => {
        console.error('Errore:', error);
        alert('Si è verificato un errore durante la creazione della domanda.');
    });
};

  

  return (
    <div className="mt-8">
      {!isCreatingDomanda ? (
        <button
          onClick={() => setIsCreatingDomanda(true)}
          className="bg-green-500 text-white py-2 px-6 rounded-lg hover:bg-green-700 transition"
        >
          Crea Domanda
        </button>
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
            type="file" 
            accept="image/*"
            onChange={handleImageUpload} 
            className="w-full p-2 border border-gray-300 rounded-lg mb-4"
          />

          {/* Sezione Opzioni */}
          {opzioni.length > 0 && (
            <div className="mb-4">
              <h3 className="font-semibold mb-2">Opzioni di risposta:</h3>
              {opzioni.map((opzione, index) => (
                <div key={index} className="flex items-center mb-2">
                  <input
                    type="text"
                    placeholder={`Opzione ${index + 1}`}
                    value={opzione}
                    onChange={(e) => modificaOpzione(index, e.target.value)}
                    className="w-full p-2 border border-gray-300 rounded-lg"
                  />
                  <button
                    onClick={() => rimuoviOpzione(index)}
                    className="ml-2 bg-red-500 text-white px-2 py-1 rounded-lg hover:bg-red-700"
                  >
                    ✖
                  </button>
                </div>
              ))}
            </div>
          )}
          <button
            onClick={aggiungiOpzione}
            className="bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-700"
          >
            + Aggiungi opzione
          </button>
          <br /><br />
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
