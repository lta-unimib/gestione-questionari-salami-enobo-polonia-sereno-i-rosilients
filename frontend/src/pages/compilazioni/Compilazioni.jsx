import React, { useState, useEffect } from 'react';
import ReactModal from 'react-modal';
import { useNavigate } from 'react-router-dom';
import { EyeIcon } from '@heroicons/react/24/solid';
import { TrashIcon } from '@heroicons/react/20/solid';

const Compilazioni = ({ user }) => {
  const [compilazioni, setCompilazioni] = useState([]);
  const [filtro, setFiltro] = useState("Tutti");
  const [searchTerm, setSearchTerm] = useState("");
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [compilazioneToDelete, setCompilazioneToDelete] = useState(null);
  const token = localStorage.getItem("jwt");
  const userEmail = localStorage.getItem("userEmail");

  const navigate = useNavigate();

  ReactModal.setAppElement('#root');

  useEffect(() => {

    if (!userEmail) {
      console.warn("userEmail non disponibile, il fetch non verrà eseguito.");
      return;
    }

    let url = "";
    let stato = "";

    if (filtro === "Definitivi") {
      url = `http://localhost:8080/api/questionariCompilati/definitivi/utente/${userEmail}`;
    } else if (filtro === "In Sospeso") {
      url = `http://localhost:8080/api/questionariCompilati/inSospeso/utente/${userEmail}`;
    } else if (filtro === "Tutti") {
      url = `http://localhost:8080/api/questionariCompilati/all/utente/${userEmail}`;
    }

    fetch(url, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`,
        }
      })
      .then((response) => {
        if (response.status === 204) {
            return [];
        } else if (!response.ok) {
            throw new Error('Errore nel recupero delle compilazioni');
        }
        return response.json();
      })
      .then(async (data) => {
    
        const compilazioniConStato = await Promise.all(
          data.map(async (compilazione) => {
            const isDefinitivo = await handleIsDefinitivo(compilazione.idCompilazione);
            return {
              ...compilazione,
              stato: isDefinitivo ? "DEFINITIVO" : "IN SOSPESO",
            };
          })
        );
    
        const filteredCompilazioni = compilazioniConStato.filter((compilazione) => {
          if (filtro === "Definitivi") {
            return compilazione.stato === "DEFINITIVO";
          } else if (filtro === "In Sospeso") {
            return compilazione.stato === "IN SOSPESO";
          } else {
            return true;
          }
        });
    
        const sortedCompilazioni = filteredCompilazioni.sort(
          (a, b) => new Date(b.dataCompilazione) - new Date(a.dataCompilazione)
        );
    
        setCompilazioni(sortedCompilazioni);
      })
      .catch((error) => {
        console.error("Errore durante il fetch:", error);
      });
  }, [userEmail, filtro]);

  const compilazioniFiltrate = compilazioni.filter(c =>
    c.titoloQuestionario && c.titoloQuestionario.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const continuaCompilazione = (idQuestionario, idCompilazione) => {
    navigate(`/questionari/compilaQuestionario/${idQuestionario}?idCompilazione=${idCompilazione}`);
  };

  const visualizzaQuestionario = (idCompilazione, idQuestionario) => {
    navigate(`/questionari/visualizzaQuestionarioCompilato/${idCompilazione}/${idQuestionario}`);
  };

  const handleIsDefinitivo = async (idCompilazione) => {
    try {
      const response = await fetch(
        `http://localhost:8080/api/questionariCompilati/checkIsDefinitivo/${idCompilazione}`,
        {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`,
          }
        }
      );
  
      if (!response.ok) {
        throw new Error('Errore nel verificare lo stato della compilazione');
      }
  
      const result = await response.json();
      return result;
    } catch (error) {
      console.error("Errore durante la verifica dello stato:", error);
      return false;
    }
  };

  const openDeleteModal = (idCompilazione) => {
    setIsDeleteModalOpen(true);
    setCompilazioneToDelete(idCompilazione);
  };

  const closeDeleteModal = () => {
    setCompilazioneToDelete(null);
    setIsDeleteModalOpen(false);
  };

  const handleDelete = async () => {
    try {

      const deleteResponse = await fetch(
          `http://localhost:8080/api/questionariCompilati/deleteQuestionarioCompilato/${compilazioneToDelete}`,
          {
              method: 'DELETE',
              headers: {
                  'Content-Type': 'application/json',
                  Authorization: `Bearer ${token}`,
              },
          }
      );

      if (!deleteResponse.ok) {
          throw new Error('Errore nella cancellazione del questionario');
      }

      setCompilazioni(compilazioni.filter((q) => q.idCompilazione !== compilazioneToDelete));

      closeDeleteModal();

    } catch (error) {
        console.error('Errore:', error);
    }
};

  return (
    <div className='mx-24'>
      <h1 className="text-4xl">Compilazioni</h1>
      <div className="flex justify-between items-center mt-4">
        <select value={filtro} onChange={(e) => setFiltro(e.target.value)} className="border rounded-lg p-2">
          <option value="Tutti">Tutti</option>
          <option value="Definitivi">Definitivi</option>
          <option value="In Sospeso">In sospeso</option>
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
                <p className="text-gray-700 text-sm">Data compilazione: {new Date(c.dataCompilazione).toLocaleDateString()}</p>
                <p className="text-gray-700 text-sm">Stato: {c.stato === "DEFINITIVO" ? "✅ Definitiva" : "⏳ In sospeso"}</p>
              </div>
              
              <div className="flex items-center">
                {c.stato === "DEFINITIVO" && (
                  <EyeIcon
                    className="w-5 h-5 text-gray-700 cursor-pointer hover:text-gray-800"
                    onClick={() => visualizzaQuestionario(c.idCompilazione, c.idQuestionario)}
                  />
                )}

                {c.stato === "IN SOSPESO" && (
                  <button
                    className="bg-white text-personal-purple border-2 border-personal-purple py-1 px-3 rounded-lg hover:bg-personal-purple hover:text-white transition duration-200 ml-4"
                    onClick={() => continuaCompilazione(c.idQuestionario, c.idCompilazione)}
                  >
                    Continua Compilazione
                  </button>
                )}
                <button
                  onClick={() => openDeleteModal(c.idCompilazione)}
                  className="text-red-600 ml-5 hover:text-red-800 mr-6"
                  >
                  <TrashIcon className="h-6 w-6" />
                </button>
              </div>
            </li>
          ))}
        </ul>
      ) : (
        <p className="text-gray-500 mt-4">Nessuna compilazione trovata.</p>
      )}

      {/* Modal per confermare l'eliminazione */}
      <ReactModal
        isOpen={isDeleteModalOpen}
        onRequestClose={closeDeleteModal}
        contentLabel="Conferma eliminazione"
        className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50"
        overlayClassName="modal-overlay"
      >
        <div className="bg-white p-8 rounded-lg w-96 text-center">
          <h2 className="text-2xl font-semibold text-gray-800">Sei sicuro di voler eliminare questa compilazione?</h2>
          <div className="mt-4">
            <button
              onClick={handleDelete}
              className="bg-red-500 text-white px-6 py-2 rounded-lg mr-4 hover:bg-red-600 transition"
            >
              Elimina
            </button>
            <button
              onClick={closeDeleteModal}
              className="bg-gray-500 text-white px-6 py-2 rounded-lg hover:bg-gray-600 transition"
            >
              Annulla
            </button>
          </div>
        </div>
      </ReactModal>
    </div>
  );
};

export default Compilazioni;