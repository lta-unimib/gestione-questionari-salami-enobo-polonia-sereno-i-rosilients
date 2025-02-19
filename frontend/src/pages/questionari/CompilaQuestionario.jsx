import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { ArrowLongLeftIcon } from '@heroicons/react/24/solid';

const CompilaQuestionario = () => {
  console.log("questionario renderizzato");
  const { id } = useParams(); 
  const navigate = useNavigate(); 
  const [questionario, setQuestionario] = useState([]);
  const [risposte, setRisposte] = useState({});
  const [isSubmitting, setIsSubmitting] = useState(false); 
  const [idCompilazione, setIdCompilazione] = useState(null); 
  const [showModal, setShowModal] = useState(false); 
  const [codiceUnivoco, setCodiceUnivoco] = useState(null); 
  const [userEmail, setUserEmail] = useState(localStorage.getItem('userEmail'));

  useEffect(() => {
    const fetchQuestionario = async () => {
      try {
        console.log(`Recupero questionario con ID: ${id}`); 
        const response = await fetch(`http://localhost:8080/api/questionari/${id}/domande`);
        if (!response.ok) {
          throw new Error(`Errore nella fetch: ${response.status}`);
        }
        const data = await response.json();
        console.log("Dati ricevuti dal backend:", data); 
        setQuestionario(data);
      } catch (error) {
        console.error("Errore nel recupero del questionario:", error);
        alert("Errore nel caricamento del questionario. Riprova più tardi.");
      }
    };

    fetchQuestionario();
  }, [id]);

  const handleChange = (domandaId, valore) => {
    console.log(`Risposta per domanda ${domandaId}: ${valore}`); 
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

      return data.idCompilazione; 
    } catch (error) {
      console.error('Errore nella creazione della compilazione:', error);
      throw error;
    }
  };

  const handleSubmit = async () => {
    const confermaInvio = window.confirm("Sei sicuro di voler inviare le risposte?");
    if (!confermaInvio) {
      return; 
    }

    try {
      setIsSubmitting(true); 

      const domandeNonRisposte = questionario.filter(
        (domanda) => !risposte.hasOwnProperty(domanda.idDomanda)
      );

      if (domandeNonRisposte.length > 0) {
        throw new Error('Per favore, rispondi a tutte le domande prima di inviare.');
      }

      const idCompilazione = await creaNuovaCompilazione(id); 

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

      const token = localStorage.getItem('jwt');
      console.log("il token estratto è " + token); // Check if user is logged in

      if (token) {
        // If logged in, send email with PDF
        const emailResponse = await fetch('http://localhost:8080/api/risposte/inviaEmail', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`,
          },
          body: JSON.stringify({
            idCompilazione: idCompilazione,
            userEmail: userEmail,
          }),
        });

        if (!emailResponse.ok) {
          throw new Error('Errore nell\'invio dell\'email');
        }

        alert('Tutte le risposte inviate con successo e email inviata!');
        navigate('/'); // Redirect to home if logged in
      } else {
        // If not logged in, show the unique code modal
        setCodiceUnivoco(idCompilazione); 
        setShowModal(true); 
      }
    } catch (error) {
      console.error('Errore nell\'invio delle risposte:', error);
      alert('Errore nell\'invio delle risposte: ' + error.message);
    } finally {
      setIsSubmitting(false); 
    }
  };

  const handleCopy = () => {
    if (codiceUnivoco) {
      navigator.clipboard.writeText(codiceUnivoco)
        .then(() => {
          alert("Codice copiato negli appunti!");
          closeModal(); // Close modal after copying code
          navigate('/'); // Redirect to home after copy
        })
        .catch((error) => {
          console.error("Errore nella copia del codice:", error);
        });
    }
  };

  const closeModal = () => {
    setShowModal(false);
    navigate('/'); // Redirect to home after closing the modal
  };

  if (questionario.length === 0) {
    console.log("Questionario non trovato o in fase di caricamento..."); 
    return <p className="text-center mt-10">Caricamento...</p>;
  }

  return (
    <div className="p-8">
      <h1 className="text-2xl font-bold mt-6">Compila il questionario</h1>

      <form className="mt-6 space-y-6" onSubmit={(e) => { e.preventDefault(); handleSubmit(); }}>
        {questionario.map((domanda) => (
          <div key={domanda.idDomanda} className="p-4 border rounded-lg">
            <p className="font-semibold">{domanda.testoDomanda}</p>

            {domanda.imagePath && (
              <div className="mt-4">
                <img
                  src={`http://localhost:8080${domanda.imagePath}`}
                  alt={domanda.testoDomanda}
                  className="w-full h-auto rounded-lg" 
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
                      required
                    />
                    {opzione}
                  </label>
                ))
              ) : (
                <textarea
                  placeholder="Scrivi la tua risposta..."
                  minLength={10}
                  maxLength={300}
                  className="w-full p-2 border rounded-lg"
                  onChange={(e) => handleChange(domanda.idDomanda, e.target.value)}
                  required
                />
              )}
            </div>
          </div>
        ))}
        <div className="flex justify-between">
          <button
            type="button"
            onClick={() => navigate(-1)}
            className="flex items-center gap-2 bg-gray-300 text-gray-800 py-2 px-4 rounded-lg hover:bg-gray-400 transition-all"
          >
            <ArrowLongLeftIcon className="h-5 w-5" />
            Torna Indietro
          </button>
          <button
            type="submit"
            className="bg-blue-500 text-white py-2 px-4 rounded-lg hover:bg-blue-600"
            disabled={isSubmitting}
          >
            {isSubmitting ? (
              <>
                <span className="animate-spin mr-2">&#9696;</span> Invio in corso...
              </>
            ) : (
              'Invia Risposte'
            )}
          </button>
        </div>
      </form>

      {showModal && (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white p-6 rounded-lg shadow-lg max-w-sm w-full">
            <h2 className="text-xl font-semibold">Ecco il tuo codice univoco!</h2>
            <div className="mt-4">
              <input
                type="text"
                value={codiceUnivoco}
                readOnly
                className="w-full p-2 border rounded-lg"
              />
            </div>
            <button
              onClick={handleCopy}
              className="bg-blue-500 text-white py-2 px-4 rounded-lg mt-4 hover:bg-blue-600"
            >
              Copia il codice
            </button>
            <button
              onClick={closeModal}
              className="bg-gray-500 text-white py-2 px-4 rounded-lg mt-4 ml-2 hover:bg-gray-600"  
            >
              Chiudi
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default CompilaQuestionario;