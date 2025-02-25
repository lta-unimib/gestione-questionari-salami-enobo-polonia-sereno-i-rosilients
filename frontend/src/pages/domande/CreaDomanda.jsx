import React, { useState } from 'react';

const CreaDomanda = ({ user, setUpdateDomande }) => {
  const [isCreatingDomanda, setIsCreatingDomanda] = useState(false);
  const [argomentoDomanda, setArgomentoDomanda] = useState('');
  const [testoDomanda, setTestoDomanda] = useState('');
  const [immagineFile, setImmagineFile] = useState(null);
  const [opzioni, setOpzioni] = useState([]);
  const [userEmail, setUserEmail] = useState(localStorage.getItem("userEmail"));

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
      setImmagineFile(file);
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
    formData.append('emailUtente', userEmail);

    if (immagineFile) {
      formData.append('imageFile', immagineFile);
    }

    // Aggiungiamo "opzioni" solo se ci sono opzioni valide
    if (opzioni.length > 0) {
      formData.append('opzioni', JSON.stringify(opzioni));
    }

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
          className="bg-personal-purple text-white py-2.5 px-6 rounded-lg hover:bg-[#4a1ed8] transition-all flex items-center shadow-md"
        >
          <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
          </svg>
          Crea Domanda
        </button>
      ) : (
        <div className="bg-white p-6 rounded-lg shadow-lg border-t-4 border-personal-purple">
          <h2 className="text-2xl font-semibold text-personal-purple mb-4 flex items-center">
            Nuova Domanda
          </h2>
          
          <div className="space-y-4">
            <div className="relative">
              <label className="block text-sm font-medium text-gray-700 mb-1">Argomento</label>
              <input
                type="text"
                placeholder="Es: Matematica, Italiano, Storia..."
                value={argomentoDomanda}
                onChange={(e) => setArgomentoDomanda(e.target.value)}
                className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-personal-purple focus:border-transparent transition-all outline-none"
              />
            </div>
            
            <div className="relative">
              <label className="block text-sm font-medium text-gray-700 mb-1">Testo della domanda</label>
              <textarea
                placeholder="Scrivi qui la tua domanda..."
                value={testoDomanda}
                onChange={(e) => setTestoDomanda(e.target.value)}
                className="w-full p-3 border border-gray-300 rounded-lg min-h-24 focus:ring-2 focus:ring-personal-purple focus:border-transparent transition-all outline-none"
              />
            </div>
            
            <div className="relative">
              <label className="block text-sm font-medium text-gray-700 mb-1">Immagine (opzionale)</label>
              <input 
                type="file" 
                accept="image/*"
                onChange={handleImageUpload} 
                className="w-full p-2 border border-gray-300 rounded-lg file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:text-sm file:bg-personal-purple file:text-white hover:file:bg-[#4a1ed8] file:transition-all"
              />
              {immagineFile && (
                <div className="mt-2 text-sm text-gray-600 flex items-center">
                  <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4 mr-1 text-green-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                  </svg>
                  File selezionato: {immagineFile.name}
                </div>
              )}
            </div>

            {/* Sezione Opzioni */}
            <div className="bg-gray-50 p-4 rounded-lg">
              <h3 className="font-semibold mb-2 text-personal-purple flex items-center">
                Opzioni di risposta
              </h3>
              
              {opzioni.length > 0 ? (
                <div className="space-y-2 mb-4">
                  {opzioni.map((opzione, index) => (
                    <div key={index} className="flex items-center">
                      <input
                        type="text"
                        placeholder={`Opzione ${index + 1}`}
                        value={opzione}
                        onChange={(e) => modificaOpzione(index, e.target.value)}
                        className="w-full p-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-personal-purple focus:border-transparent transition-all outline-none"
                      />
                      <button
                        onClick={() => rimuoviOpzione(index)}
                        className="ml-2 bg-red-100 text-red-600 p-2 rounded-lg hover:bg-red-200 transition-all"
                        aria-label="Rimuovi opzione"
                      >
                        <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                        </svg>
                      </button>
                    </div>
                  ))}
                </div>
              ) : (
                <p className="text-sm text-gray-500 mb-2">Aggiungi delle opzioni di risposta per creare una domanda a scelta multipla.</p>
              )}
              
              <button
                onClick={aggiungiOpzione}
                className="bg-personal-purple bg-opacity-20 text-personal-purple px-4 py-2 rounded-lg hover:bg-opacity-30 transition-all flex items-center"
              >
                <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                </svg>
                Aggiungi opzione
              </button>
            </div>
          </div>
          
          <div className="flex justify-end mt-6 space-x-3">
            <button
              onClick={() => setIsCreatingDomanda(false)}
              className="px-5 py-2.5 rounded-lg border border-gray-300 text-gray-700 hover:bg-gray-100 transition-all flex items-center"
            >
              <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              </svg>
              Annulla
            </button>
            <button
              onClick={handleCreaDomanda}
              className="bg-personal-purple text-white px-5 py-2.5 rounded-lg hover:bg-[#4a1ed8] transition-all flex items-center"
            >
              <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
              </svg>
              Crea domanda
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default CreaDomanda;