import React, { useState, useEffect } from 'react';
import { useParams, useLocation, useNavigate } from 'react-router-dom';
import { ArrowLongLeftIcon } from '@heroicons/react/24/solid';


const TerminaQuestionario = () => {

  /*
  
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
          console.log("idQuestionario (from compilazione fetch):", data.idQuestionario);

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
      const token = localStorage.getItem('jwt');
      const response = await fetch(`http://localhost:8080/api/questionari/${idQuestionario}/domande`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });
  
      if (!response.ok) {
        throw new Error(`Errore nella fetch: ${response.status}`);
      }
  
      const text = await response.text();
      console.log("Risposta ricevuta:", text); // Log per vedere il corpo della risposta
  
      // Verifica se la risposta non è vuota
      if (!text.trim()) {
        throw new Error('La risposta è vuota');
      }
  
      // Prova a parsare la risposta come JSON
      const data = JSON.parse(text);
      console.log("Dati ricevuti:", data); // Log per vedere i dati ricevuti
      setQuestionario(data);
  
    } catch (error) {
      console.error("Errore nel recupero del questionario:", error);
      alert("Errore nel caricamento del questionario. Riprova più tardi.");
    }
  };


  const fetchRisposte = async (idCompilazione) => {
    try {
      const token = localStorage.getItem('jwt');
      const response = await fetch(`http://localhost:8080/api/questionariCompilati/${idCompilazione}/risposte`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });
      if (!response.ok) {
        throw new Error('Errore nel recupero delle risposte');
      }
      const risposteMappa = await response.json();
      setRisposteMappa(risposteMappa);
      console.log("Risposte ricevute:", risposteMappa);
  
      Object.entries(risposteMappa).forEach(([idDomanda, valore]) => {
        handleChange(idDomanda, valore);
      });
  
    } catch (error) {
      console.error('Errore nel recupero delle risposte:', error);
      alert('Errore nel recupero delle risposte. Riprova più tardi.');
    }
  };


  const handleChange = (idDomanda, valore) => {
    console.log(`Risposta per domanda ${idDomanda}: ${valore}`); 
    setRisposte((prev) => {
      const updatedRisposte = { ...prev, [idDomanda]: valore };
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
  
      await fetch(`http://localhost:8080/api/questionariCompilati/${idCompilazione}/finalizza`, {
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
      <h1 className="text-2xl font-bold mt-6">Termina il questionario</h1>
  
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
            

                  {isModalImageOpen && (
                    <div 
                      className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50"
                      onClick={() => setIsModalImageOpen(false)}
                    >
                      <div className="relative bg-white p-1 rounded-lg max-w-3xl w-full">

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
  
            <div className="mt-2 space-y-3">
              {domanda.options ? (
                domanda.options.map((option, index) => (
                  <div key={index}>
                    <label className="flex items-center">
                      <input
                        type="radio"
                        name={`domanda-${domanda.idDomanda}`}
                        value={option}
                        checked={risposte[domanda.idDomanda] === option}
                        onChange={() => handleChange(domanda.idDomanda, option)}
                      />
                      <span className="ml-2">{option}</span>
                    </label>
                  </div>
                ))
              ) : (
                <textarea
                  placeholder="Scrivi la tua risposta"
                  value={risposte[domanda.idDomanda] || ''}
                  onChange={(e) => handleChange(domanda.idDomanda, e.target.value)}
                  className="w-full p-2 border rounded-md mt-2"
                />
              )}
            </div>
          </div>
        ))}

        <div className="mt-4 flex justify-between">
          <button
            type="button"
            onClick={handleSalvaParziale}
            className="px-4 py-2 bg-blue-500 text-white rounded-md"
          >
            Salva Parzialmente
          </button>
          <button
            type="submit"
            disabled={isSubmitting}
            className="px-4 py-2 bg-green-500 text-white rounded-md"
          >
            {isSubmitting ? "Invio in corso..." : "Invia"}
          </button>
        </div>
      </form>

      {showModal && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50">
          <div className="bg-white p-6 rounded-lg max-w-sm w-full">
            <p className="text-lg font-semibold">Risposte inviate!</p>
            <p className="text-sm">Codice univoco: {codiceUnivoco}</p>
            <button 
              className="mt-4 bg-blue-500 text-white rounded-lg py-2 px-6"
              onClick={handleCopy}
            >
              Copia codice
            </button>
            <button 
              className="mt-2 bg-gray-500 text-white rounded-lg py-2 px-6"
              onClick={closeModal}
            >
              Chiudi
            </button>
          </div>
        </div>
      )}
    </div>
  );

*/

};


export default TerminaQuestionario;