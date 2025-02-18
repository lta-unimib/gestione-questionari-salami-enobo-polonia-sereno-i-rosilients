import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';

const CompilaQuestionario = () => {
  console.log("questionario renderizzato");
  const { id } = useParams(); // Ottiene l'ID del questionario dalla route
  const [questionario, setQuestionario] = useState([]);
  const [risposte, setRisposte] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false); // Stato per il caricamento

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
        setQuestionario(data);
      } catch (error) {
        console.error("Errore nel recupero del questionario:", error);
        alert("Errore nel caricamento del questionario. Riprova più tardi.");
      }
    };

    fetchQuestionario();
  }, [id]);

  const handleChange = (domandaId, valore) => {
    console.log(`Risposta per domanda ${domandaId}: ${valore}`); // Log per vedere la risposta scelta per ogni domanda
    setRisposte((prev) => {
      const updatedRisposte = { ...prev, [domandaId]: valore };
      return updatedRisposte;
    });
  };

  const creaNuovaCompilazione = async (idQuestionario) => {
    try {
      const response = await fetch(`http://localhost:8080/api/risposte/creaCompilazione?idQuestionario=${idQuestionario}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      const data = await response.json();
      if (!response.ok) {
        throw new Error(data.error || 'Errore nella creazione della compilazione');
      }

      return data.idCompilazione; // Restituisci l'ID della nuova compilazione
    } catch (error) {
      console.error('Errore nella creazione della compilazione:', error);
      throw error;
    }
  };

  const handleSubmit = async () => {
    // Finestra di conferma prima dell'invio
    const confermaInvio = window.confirm("Sei sicuro di voler inviare le risposte?");
    if (!confermaInvio) {
      return; 
    }

    try {
      setIsSubmitting(true); // Attiva l'indicatore di caricamento

      // Verifica che tutte le domande siano state risposte
      const domandeNonRisposte = questionario.filter(
        (domanda) => !risposte.hasOwnProperty(domanda.idDomanda)
      );

      if (domandeNonRisposte.length > 0) {
        throw new Error('Per favore, rispondi a tutte le domande prima di inviare.');
      }

      const idCompilazione = await creaNuovaCompilazione(id); // Crea una nuova compilazione

      const risposteArray = Object.keys(risposte).map((idDomanda) => {
        return {
          idCompilazione: idCompilazione,
          idDomanda: parseInt(idDomanda),
          testoRisposta: risposte[idDomanda],
        };
      });

      for (const risposta of risposteArray) {
        const response = await fetch('http://localhost:8080/api/risposte/salvaRisposta', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(risposta),
        });

        if (!response.ok) {
          const errorData = await response.json();
          throw new Error(errorData.error || 'Errore nell\'invio della risposta');
        }
      }

      alert('Tutte le risposte inviate con successo');
    } catch (error) {
      console.error('Errore nell\'invio delle risposte:', error);
      alert('Errore nell\'invio delle risposte: ' + error.message);
    } finally {
      setIsSubmitting(false); // Disattiva l'indicatore di caricamento
    }
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
          disabled={isSubmitting} // Disabilita il pulsante durante l'invio
        >
          {isSubmitting ? (
            <>
              <span className="animate-spin mr-2">&#9696;</span> Invio in corso...
            </>
          ) : (
            'Invia Risposte'
          )}
        </button>
      </form>
    </div>
  );
};

export default CompilaQuestionario;