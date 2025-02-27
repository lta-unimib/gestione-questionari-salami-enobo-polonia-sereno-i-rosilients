import React, { useState, useEffect } from 'react';
import { useParams, useLocation, useNavigate } from 'react-router-dom';
import { ArrowLongLeftIcon } from '@heroicons/react/24/solid';
import * as questionarioService from '../services/compilazioniService'

const CompilaQuestionario = () => {
  const { id } = useParams();
  const query = new URLSearchParams(useLocation().search);
  const [idCompilazione, setIdCompilazione] = useState(query.get("idCompilazione"));
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
  const [selectedImageId, setSelectedImageId] = useState(null);

  useEffect(() => {
    if (idCompilazione) {
      fetchCompilazioneData();
    } else if (id) {
      fetchQuestionarioData(id);
    }
  }, [idCompilazione, id]);

  const fetchCompilazioneData = async () => {
    try {
      const data = await questionarioService.fetchCompilazioneById(idCompilazione);
      setIdQuestionario(data.idQuestionario);
      
      await fetchRisposte(idCompilazione);
      await fetchQuestionarioData(data.idQuestionario);
    } catch (error) {
      alert('Errore nel recupero della compilazione. Riprova più tardi.');
    }
  };

  const fetchQuestionarioData = async (idQuestionario) => {
    try {
      const data = await questionarioService.fetchQuestionarioById(idQuestionario);
      setQuestionario(data);
    } catch (error) {
      alert("Errore nel caricamento del questionario. Riprova più tardi.");
    }
  };

  const fetchRisposte = async (idCompilazione) => {
    try {
      const risposteMappa = await questionarioService.fetchRisposteByCompilazione(idCompilazione);
      setRisposteMappa(risposteMappa);

      Object.entries(risposteMappa).forEach(([domandaId, valore]) => {
        handleChange(domandaId, valore);
      });
    } catch (error) {
      alert('Errore nel recupero delle risposte. Riprova più tardi.');
    }
  };

  const handleChange = (domandaId, valore) => {
    setRisposte((prev) => ({
      ...prev,
      [domandaId]: valore
    }));
  };

  const handleSalvaParziale = async () => {
    const confermaInvio = window.confirm("Sei sicuro di voler fermare la compilazione? La potrai riprendere più tardi");
    if (!confermaInvio) return;
  
    try {
      let currentIdCompilazione = idCompilazione;
      
      if (!currentIdCompilazione) {
        currentIdCompilazione = await questionarioService.creaNuovaCompilazione(id, userEmail);
        setIdCompilazione(currentIdCompilazione);
      }
  
      const risposteArray = questionario.map((domanda) => ({
        idCompilazione: currentIdCompilazione,
        idDomanda: domanda.idDomanda,
        testoRisposta: risposte[domanda.idDomanda] ?? "",
      }));
  
      for (const risposta of risposteArray) {
        await questionarioService.salvaRisposta(risposta);
      }
  
      if (localStorage.getItem('jwt')) {
        alert('Risposte salvate! Puoi riprendere in un secondo momento.');
        navigate('/');
      } else {
        handleUtenteNonRegistrato(currentIdCompilazione);
      }
    } catch (error) {
      alert('Errore nel salvataggio. Riprova più tardi.');
    }
  };

  const handleSubmit = async () => {
    const confermaInvio = window.confirm("Sei sicuro di voler salvare le risposte? il questionario non sarà più modificabile.");
    if (!confermaInvio) return;
  
    try {
      setIsSubmitting(true);
  
      // Validazione risposte
      const domandeNonValide = questionario.filter((domanda) => {
        const risposta = risposte[domanda.idDomanda] || "";
        if (!domanda.opzioni) {
          return risposta.trim().length < 10; // Almeno 10 caratteri
        }
        return false;
      });

      if (domandeNonValide.length > 0) {
        throw new Error('Per favore, rispondi a tutte le domande con almeno 10 caratteri prima di inviare.');
      }
      
      let currentIdCompilazione = idCompilazione;
      
      if (!currentIdCompilazione) {
        currentIdCompilazione = await questionarioService.creaNuovaCompilazione(id, userEmail);
        setIdCompilazione(currentIdCompilazione);
      }
  
      const risposteArray = Object.keys(risposte).map((idDomanda) => ({
        idCompilazione: currentIdCompilazione,
        idDomanda: parseInt(idDomanda),
        testoRisposta: risposte[idDomanda],
      }));
  
      // Salva tutte le risposte
      for (const risposta of risposteArray) {
        await questionarioService.salvaRisposta(risposta);
      }
  
      // Finalizza la compilazione
      await questionarioService.finalizzaCompilazione(currentIdCompilazione);
  
      if (localStorage.getItem('jwt')) {
        await questionarioService.inviaEmail(currentIdCompilazione, userEmail);
        alert('Risposte inviate e email con PDF spedita!');
        navigate('/');
      } else {
        handleUtenteNonRegistrato(currentIdCompilazione);
      }
    } catch (error) {
      alert('Errore nell\'invio delle risposte: ' + error.message);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleUtenteNonRegistrato = (currentIdCompilazione) => {
    if (!localStorage.getItem('jwt')) {
      setCodiceUnivoco(currentIdCompilazione);
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

  const openModalImage = (idDomanda) => {
    setSelectedImageId(idDomanda);
  };

  const closeModalImage = () => {
    setSelectedImageId(null);
  };

  if (questionario.length === 0) {
    return (
      <div className="flex justify-center py-12">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-personal-purple" />
      </div>
    );
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
                  className="w-72 h-72 rounded-lg cursor-pointer"
                  onClick={() => openModalImage(domanda.idDomanda)}
                />
          
                {/* Popup immagine ingrandita */}
                {selectedImageId === domanda.idDomanda && (
                  <div 
                    className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50"
                    onClick={closeModalImage}
                  >
                    <div className="relative bg-white p-[2px] rounded-lg max-w-3xl w-full">
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
  
            <div className="mt-16 space-y-2">
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
                  value={risposte[domanda.idDomanda] ?? ""}
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
            onClick={handleSalvaParziale}
          >
            Continua più tardi
          </button>
  
          <button
            type="submit"
            className="bg-personal-purple text-white py-2 px-4 rounded-lg hover:bg-personal-purple-dark1"
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
            <h2 className="text-xl font-semibold">
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