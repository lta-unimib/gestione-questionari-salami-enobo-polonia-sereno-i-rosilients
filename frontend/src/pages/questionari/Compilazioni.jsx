import React, { useState, useEffect } from 'react';
import ReactModal from 'react-modal';
import { useNavigate } from 'react-router-dom';

const Compilazioni = ({ user }) => {
  const [compilazioni, setCompilazioni] = useState([]);
  const [filtro, setFiltro] = useState("");
  const [searchTerm, setSearchTerm] = useState("");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedCompilazione, setSelectedCompilazione] = useState(null);

  const token = localStorage.getItem("jwt");
  const userEmail = localStorage.getItem("userEmail");

  const navigate = useNavigate();

  ReactModal.setAppElement('#root');

  useEffect(() => {
    console.log("useEffect attivato. UserEmail:", userEmail, "Filtro:", filtro);

    if (!userEmail) {
      console.warn("userEmail non disponibile, il fetch non verrà eseguito.");
      return;
    }

    console.log("JWT token:", token);

    const fetchCompilazioni = async (filtro) => {
      let url = "";
      let stato = "";

      if (filtro === "definitivi") {
        url = `http://localhost:8080/api/questionariCompilati/definitivi/utente/${userEmail}`;
        stato = "DEFINITIVO";
      } else if (filtro === "inSospeso") {
        url = `http://localhost:8080/api/questionariCompilati/inSospeso/utente/${userEmail}`;
        stato = "IN SOSPESO";
      } else {
        return Promise.all([
          fetch(`http://localhost:8080/api/questionariCompilati/definitivi/utente/${userEmail}`, {
            method: 'GET',
            headers: {
              'Content-Type': 'application/json',
              'Authorization': `Bearer ${token}`,
            }
          }),
          fetch(`http://localhost:8080/api/questionariCompilati/inSospeso/utente/${userEmail}`, {
            method: 'GET',
            headers: {
              'Content-Type': 'application/json',
              'Authorization': `Bearer ${token}`,
            }
          })
        ])
        .then(async (responses) => {
          const [definitiviResponse, sospesiResponse] = responses;

          if (!definitiviResponse.ok || !sospesiResponse.ok) {
            throw new Error("Errore durante il fetch delle compilazioni");
          }

          const definitiviData = await definitiviResponse.json();
          const sospesiData = await sospesiResponse.json();

          const definitiviConStato = definitiviData.map(compilazione => ({
            ...compilazione,
            stato: "DEFINITIVO"
          }));

          const sospesiConStato = sospesiData.map(compilazione => ({
            ...compilazione,
            stato: "IN SOSPESO"
          }));

          // Unisci i risultati
          const allCompilazioni = [...definitiviConStato, ...sospesiConStato];
          return allCompilazioni.sort((a, b) => new Date(b.dataCompilazione) - new Date(a.dataCompilazione));
        });
      }

      return fetch(url, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        }
      })
      .then((response) => {
        if (!response.ok) {
          throw new Error(`Errore nel recupero delle compilazioni. Status: ${response.status}`);
        }
        return response.json();
      })
      .then((data) => {
        const compilazioniConStato = data.map(compilazione => ({
          ...compilazione,
          stato: stato
        }));
        return compilazioniConStato.sort((a, b) => new Date(b.dataCompilazione) - new Date(a.dataCompilazione));
      });
    };

    // Chiamata API per recuperare le compilazioni
    fetchCompilazioni(filtro)
      .then((data) => {
        // Dopo aver recuperato le compilazioni, controlla se sono definitive o in sospeso
        Promise.all(data.map(compilazione => {
          return fetch(`http://localhost:8080/api/questionariCompilati/checkIsDefinitivo/${compilazione.idCompilazione}`, {
            method: 'GET',
            headers: {
              'Content-Type': 'application/json',
              'Authorization': `Bearer ${token}`,
            }
          })
          .then(response => response.json())
          .then(isDefinitivo => {
            compilazione.stato = isDefinitivo ? "DEFINITIVO" : "IN SOSPESO";
          })
        }))
        .then(() => {
          // Dopo aver aggiornato gli stati, imposta i dati finali nello stato
          setCompilazioni(data.sort((a, b) => new Date(b.dataCompilazione) - new Date(a.dataCompilazione)));
        });
      })
      .catch((error) => {
        console.error("Errore durante il fetch:", error);
      });
  }, [userEmail, filtro]);

  const compilazioniFiltrate = compilazioni.filter(c =>
    c.titoloQuestionario && c.titoloQuestionario.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const openModal = (compilazione) => {
    setSelectedCompilazione(compilazione);
    setIsModalOpen(true);
  };

  const closeModal = () => {
    setIsModalOpen(false);
    setSelectedCompilazione(null);
  };

  const handleTerminaCompilazione = (idCompilazione) => {
    console.log("ID Compilazione passato al click di Termina:", idCompilazione); // Aggiungi questo log
  
    const questionario = compilazioni.find(c => c.idCompilazione === idCompilazione);
    if (questionario) {
      fetch(`http://localhost:8080/api/questionariCompilati/${idCompilazione}/risposte`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        }
      })
      .then(response => {
        if (!response.ok) {
          throw new Error('Errore nel recupero delle risposte');
        }
        return response.json();
      })
      .then(risposte => {
        console.log("Risposte recuperate:", risposte);
        navigate(`/questionari/terminaQuestionario/${idCompilazione}`, { 
          state: { risposte, idCompilazione } 
        });
      })
      .catch(error => {
        console.error("Errore nel recupero delle risposte:", error);
      });
    }
  };

  const continuaCompilazione = (idQuestionario, idCompilazione) => {
    console.log("Continuare la compilazione del questionario", idQuestionario, "con id compilazione", idCompilazione);
    navigate(`/questionari/compilaQuestionario/${idQuestionario}?idCompilazione=${idCompilazione}`);
  };

  const visualizzaQuestionario = (idCompilazione) => {
    console.log("Visualizzare il questionario con idCompilazione:", idCompilazione);
    navigate(`/questionari/visualizzaQuestionarioCompilato/${idCompilazione}`);
  };

  return (
    <div className='mx-24'>
      <h1 className="text-4xl">Compilazioni</h1>
      <div className="flex justify-between items-center mt-4">
        <select value={filtro} onChange={(e) => setFiltro(e.target.value)} className="border rounded-lg p-2">
          <option value="">Tutti</option>
          <option value="definitivi">Definitivi</option>
          <option value="inSospeso">In sospeso</option>
        </select>
        <input 
          type="text"
          placeholder="Cerca per titolo..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="border rounded-lg p-2"
        />
      </div>

      {compilazioniFiltrate.length > 0 ? (
        <ul className='mt-12'>
          {compilazioniFiltrate.map(c => (
            <li key={c.idCompilazione} className="border p-4 my-2 rounded-lg shadow-lg flex justify-between">
              <div>
                <h3 className="text-xl font-semibold">{c.titoloQuestionario}</h3>
                <p>Data compilazione: {new Date(c.dataCompilazione).toLocaleDateString()}</p>
                <p>Stato: {c.stato === "DEFINITIVO" ? "✅ Definitiva" : "⏳ In sospeso"}</p>
              </div>
              
              <div className="flex items-center">
                {c.stato === "DEFINITIVO" && (
                  <button 
                    className="bg-blue-500 text-white px-4 py-2 rounded-lg hover:bg-blue-600 transition"
                    onClick={() => visualizzaQuestionario(c.idCompilazione)}
                  >
                    Visualizza
                  </button>
                )}

                {c.stato === "IN SOSPESO" && (
                  <button
                    className="bg-white text-personal-purple border-2 border-personal-purple py-1 px-3 rounded-lg hover:bg-personal-purple hover:text-white transition duration-200 ml-4"
                    onClick={() => continuaCompilazione(c.idQuestionario, c.idCompilazione)}
                  >
                    Termina
                  </button>
                )}
              </div>
            </li>
          ))}
        </ul>
      ) : (
        <p className="text-gray-500 mt-4">Nessuna compilazione trovata.</p>
      )}

      <ReactModal
        isOpen={isModalOpen}
        onRequestClose={closeModal}
        contentLabel="Dettagli compilazione"
        className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50"
        overlayClassName="modal-overlay"
      >
        {selectedCompilazione && (
          <div className="bg-white p-8 rounded-lg w-96">
            <h2 className="text-2xl font-semibold text-gray-800">Dettagli Compilazione</h2>
            <p className="mt-2"><strong>Titolo:</strong> {selectedCompilazione.titoloQuestionario}</p>
            <p className="mt-2"><strong>Data:</strong> {new Date(selectedCompilazione.dataCompilazione).toLocaleString()}</p>
            <p className="mt-2"><strong>Stato:</strong> {selectedCompilazione.stato === "DEFINITIVO" ? "✅ Definitivo" : "⏳ In sospeso"}</p>

            <button 
              onClick={closeModal} 
              className="bg-gray-500 text-white px-6 py-2 rounded-lg mt-4 hover:bg-gray-600 transition">
              Chiudi
            </button>
          </div>
        )}
      </ReactModal>
    </div>
  );
};

export default Compilazioni;