import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';

const CompilaQuestionario = () => {
  console.log("questionario renderizzato");
  const { id } = useParams(); // Ottiene l'ID del questionario dalla route
  const [questionario, setQuestionario] = useState([]);
  const [risposte, setRisposte] = useState({});

  useEffect(() => {
    const fetchQuestionario = async () => {
      try {
        console.log(`Recupero questionario con ID: ${id}`); // Log per monitorare l'ID del questionario
        const response = await fetch(`http://localhost:8080/api/questionari/${id}/domande`);
        if (!response.ok) {
          throw new Error(`Errore nella fetch: ${response.status}`);
        }
        const data = await response.json();
        console.log("Dati ricevuti dal backend:", data); // Log per visualizzare i dati ricevuti dal backend
        setQuestionario(data); // Imposta direttamente l'array di domande
      } catch (error) {
        console.error("Errore nel recupero del questionario:", error);
      }
    };

    fetchQuestionario();
  }, [id]);

  const handleChange = (domandaId, valore) => {
    console.log(`Risposta per domanda ${domandaId}: ${valore}`); // Log per vedere la risposta scelta per ogni domanda
    setRisposte((prev) => {
      const updatedRisposte = { ...prev, [domandaId]: valore };
      console.log("Risposte aggiornate:", updatedRisposte); // Log per visualizzare le risposte aggiornate
      return updatedRisposte;
    });
  };

  const handleSubmit = () => {
    console.log("Risposte inviate:", risposte); // Log per visualizzare le risposte al momento dell'invio
    // Qui puoi fare una chiamata API per salvare le risposte
  };

  if (questionario.length === 0) {
    console.log("Questionario non trovato o in fase di caricamento..."); // Log se il questionario non è ancora stato caricato
    return <p className="text-center mt-10">Caricamento...</p>;
  }

  return (
    <div className="p-8">
      <h1 className="text-2xl font-bold">Compila il questionario</h1>
  
      <form className="mt-6 space-y-6">
        {questionario.map((domanda) => (
          <div key={domanda.idDomanda} className="p-4 border rounded-lg">
            <p className="font-semibold">{domanda.testoDomanda}</p>
  
            {/* Visualizzazione immagine se presente */}
            {domanda.imagePath && (
              <div className="mt-4">
                <img
                  src={`http://localhost:8080${domanda.imagePath}`} // Assicurati che il percorso sia corretto
                  alt={domanda.testoDomanda}
                  className="w-full h-auto rounded-lg" // Modifica le dimensioni se necessario
                />
              </div>
            )}
  
            <div className="mt-2 space-y-2">
              {domanda.opzioni?.length > 0 ? (
                domanda.opzioni.map((opzione, index) => (
                  <label key={index} className="flex items-center gap-2">
                    <input
                      type="radio"
                      name={`domanda-${domanda.idDomanda}`}
                      value={opzione}
                      onChange={(e) => handleChange(domanda.idDomanda, e.target.value)}
                    />
                    {opzione}
                  </label>
                ))
              ) : (
                <textarea
                  placeholder="Scrivi la tua risposta..."
                  className="w-full p-2 border rounded-lg"
                  onChange={(e) => handleChange(domanda.idDomanda, e.target.value)}
                />
              )}
            </div>
          </div>
        ))}
        <button
          type="button"
          onClick={handleSubmit}
          className="bg-blue-500 text-white py-2 px-4 rounded-lg hover:bg-blue-600"
        >
          Invia Risposte
        </button>
      </form>
    </div>
  );
  
};

export default CompilaQuestionario;
