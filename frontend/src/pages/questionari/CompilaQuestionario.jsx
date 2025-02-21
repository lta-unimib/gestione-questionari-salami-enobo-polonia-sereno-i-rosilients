import React, { useState, useEffect } from 'react';
import { useParams, useLocation, useNavigate } from 'react-router-dom';
import { ArrowLongLeftIcon } from '@heroicons/react/24/solid';

const CompilaQuestionario = () => {
  console.log("questionario renderizzato");
  const { id } = useParams();
  const query = new URLSearchParams(useLocation().search);
  let idCompilazione = query.get("idCompilazione");
  const navigate = useNavigate(); 
  const [questionario, setQuestionario] = useState([]);
  const [idQuestionario, setIdQuestionario] = useState(null);
  const [risposte, setRisposte] = useState({});
  const [risposteMappa, setRisposteMappa] = useState({});

  const [isSubmitting, setIsSubmitting] = useState(false); 
  const [showModal, setShowModal] = useState(false); 
  const [codiceUnivoco, setCodiceUnivoco] = useState(null); 
  const [userEmail, setUserEmail] = useState(
    localStorage.getItem('jwt') ? localStorage.getItem('userEmail') : ""
  );
  const [isModalImageOpen, setIsModalImageOpen] = useState(false)

  useEffect(() => {

    if (idCompilazione) {
      const fetchIdQuestionario = async () => {
        try {
          const response = await fetch(`http://localhost:8080/api/questionariCompilati/${idCompilazione}`);
          if (!response.ok) {
            throw new Error('Errore nel recupero dei dati della compilazione');
          }

          const data = await response.json();
          setIdQuestionario(data.idQuestionario);

          fetchRisposte(idCompilazione);

          fetchQuestionario(data.idQuestionario);
        } catch (error) {
          console.error('Errore nel recupero della compilazione:', error);
          alert('Errore nel recupero della compilazione. Riprova più tardi.');
        }
      };

      fetchIdQuestionario();
    } else if (id) {
      fetchQuestionario(id);
    }
  }, [idCompilazione, id]);


  const fetchQuestionario = async (idQuestionario) => {
    try {
      const response = await fetch(`http://localhost:8080/api/questionari/${idQuestionario}/domande`);
      if (!response.ok) {
        throw new Error(`Errore nella fetch: ${response.status}`);
      }

      const data = await response.json();
      setQuestionario(data);
    } catch (error) {
      console.error("Errore nel recupero del questionario:", error);
      alert("Errore nel caricamento del questionario. Riprova più tardi.");
    }
  };


  const fetchRisposte = async (idCompilazione) => {
    try {
      const response = await fetch(`http://localhost:8080/api/risposte/${idCompilazione}`);
      if (!response.ok) {
        throw new Error('Errore nel recupero delle risposte');
      }
      const risposteMappa = await response.json();
      setRisposteMappa(risposteMappa);

      Object.entries(risposteMappa).forEach(([domandaId, valore]) => {
        handleChange(domandaId, valore);  // Aggiorna lo stato 'risposte' tramite 'handleChange'
      });

    } catch (error) {
      console.error('Errore nel recupero delle risposte:', error);
      alert('Errore nel recupero delle risposte. Riprova più tardi.');
    }
  };


  const handleChange = (domandaId, valore) => {
    console.log(`Risposta per domanda ${domandaId}: ${valore}`); 
    setRisposte((prev) => {
      const updatedRisposte = { ...prev, [domandaId]: valore };
      return updatedRisposte;
    });
  };

  const creaNuovaCompilazione = async (idQuestionario) => {
    try {

      const response = await fetch(`http://localhost:8080/api/risposte/creaCompilazione?idQuestionario=${idQuestionario}&userEmail=${userEmail}`, {
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

  const handleSalvaParziale = async () => {
    const confermaInvio = window.confirm("Sei sicuro di voler fermare la compilazione? La potrai riprendere più tardi");
    if (!confermaInvio) return;

    try {
      const domandeRisposte = questionario.filter(
        (domanda) => risposte.hasOwnProperty(domanda.idDomanda) && risposte[domanda.idDomanda] !== "" && risposte[domanda.idDomanda] !== null
      );
      if (domandeRisposte.length < 1) {
        throw new Error('Per favore, rispondi almeno ad una domanda.');
      }
      
      if (!idCompilazione) {
        idCompilazione = await creaNuovaCompilazione(id);
      }
      
      const risposteArray = domandeRisposte.map((domanda) => ({
        idCompilazione: idCompilazione,
        idDomanda: domanda.idDomanda,
        testoRisposta: risposte[domanda.idDomanda],
      }));
  
      for (const risposta of risposteArray) {
        await fetch('http://localhost:8080/api/risposte/salvaRisposta', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(risposta),
        });
      }
      if(localStorage.getItem('jwt')) {
        alert('Risposte salvate! Puoi riprendere in un secondo momento.');
        navigate('/');
      }
      handleUtenteNonRegistrato();

    } catch (error) {
      console.error('Errore nel salvataggio parziale:', error);
      alert('Errore nel salvataggio. Riprova più tardi.');
    }
  };

  const handleSubmit = async () => {
    const confermaInvio = 
    window.confirm("Sei sicuro di voler salvare le risposte? il questionario non sarà più modificabile.");
    if (!confermaInvio) return;
  
    try {
      setIsSubmitting(true);
  
      const domandeNonRisposte = questionario.filter(
        (domanda) => !risposte.hasOwnProperty(domanda.idDomanda)
      );
  
      if (domandeNonRisposte.length > 0) {
        throw new Error('Per favore, rispondi a tutte le domande prima di inviare.');
      }
  
      if (!idCompilazione) {
        idCompilazione = await creaNuovaCompilazione(id);
      }
  
      const risposteArray = Object.keys(risposte).map((idDomanda) => ({
        idCompilazione: idCompilazione,
        idDomanda: parseInt(idDomanda),
        testoRisposta: risposte[idDomanda],
      }));
  
      for (const risposta of risposteArray) {
        await fetch('http://localhost:8080/api/risposte/salvaRisposta', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(risposta),
        });
      }
  
      await fetch(`http://localhost:8080/api/risposte/finalizzaCompilazione?idCompilazione=${idCompilazione}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
      });
  
      if (localStorage.getItem('jwt')) {
        await fetch('http://localhost:8080/api/risposte/inviaEmail', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${localStorage.getItem('jwt')}`,
          },
          body: JSON.stringify({ idCompilazione, userEmail }),
        });
  
        alert('Risposte inviate e email con PDF spedita!');
        navigate('/');
      }
      handleUtenteNonRegistrato();
    } catch (error) {
      console.error('Errore nell\'invio delle risposte:', error);
      alert('Errore nell\'invio delle risposte: ' + error.message);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleUtenteNonRegistrato = () => {
    if (!localStorage.getItem('jwt')) {
      setCodiceUnivoco(idCompilazione);
      setShowModal(true);
    }
  };

  const handleCopy = () => {
    if (codiceUnivoco) {
      navigator.clipboard.writeText(codiceUnivoco)
        .then(() => {
          alert("Codice copiato negli appunti!");
          closeModal();
          navigate('/');
        })
        .catch((error) => {
          console.error("Errore nella copia del codice:", error);
        });
    }
  };

  const closeModal = () => {
    setShowModal(false);
    navigate('/');
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
                    alt="Immagine della domanda"
                    className="w-72 h-auto rounded-lg cursor-pointer"
                    onClick={() => setIsModalImageOpen(true)}
                  />
            
                  {/* Popup immagine ingrandita */}
                  {isModalImageOpen && (
                    <div 
                      className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50"
                      onClick={() => setIsModalImageOpen(false)}
                    >
                      <div className="relative bg-white p-1 rounded-lg max-w-3xl w-full">
                        {/* Immagine ingrandita */}
                        <img
                          src={`http://localhost:8080${domanda.imagePath}`}
                          alt="Immagine ingrandita"
                          className="w-full h-auto rounded-lg"
                        />
                      </div>
                    </div>
                  )}
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
                      checked={risposte[domanda.idDomanda] === opzione}
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
                  value={risposte[domanda.idDomanda] || ''}
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
            type="button"
            className="bg-gray-500 text-white py-2 px-4 rounded-lg hover:bg-gray-600"
            onClick={() => handleSalvaParziale()}
          >
            Continua più tardi
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
              'Salva e Invia'
            )}
          </button>
        </div>
      </form>

      {showModal && (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white p-6 rounded-lg shadow-lg max-w-sm w-full">
            <h2 
              className="text-xl font-semibold"
            >
              Non dimenticare il tuo codice univoco! NON avrai un altro modo di recuperarlo.
            </h2>
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