import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import ReactModal from 'react-modal';
import { TrashIcon } from '@heroicons/react/20/solid';

const QuestionariCompilati = () => {
  const [questionari, setQuestionari] = useState([]);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [questionarioToDelete, setQuestionarioToDelete] = useState(null);
  const token = localStorage.getItem("jwt");
  const userEmail = localStorage.getItem("userEmail");
  const navigate = useNavigate();

  // Ottieni i questionari compilati dell'utente
  useEffect(() => {
    if (!userEmail) return;

    let url = `http://localhost:8080/api/questionariCompilati/utente/${userEmail}`;
    
    fetch(url, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    })
    .then(response => {
      if (!response.ok) {
        throw new Error('Errore nel recupero dei questionari');
      }
      return response.json();
    })
    .then(data => {
      console.log("Risposta dal backend:", data);
      setQuestionari(data); // Aggiorna lo stato con i questionari ricevuti
    })
    .catch(error => {
      console.error('Errore:', error);
    });
  }, [userEmail, token]);

  // Per continuare la compilazione del questionario
  const continuaCompilazione = (idQuestionario, idCompilazione) => {
    navigate(`/questionari/compilaQuestionario/${idQuestionario}?idCompilazione=${idCompilazione}`);
  };

  const openDeleteModal = (idCompilazione) => {
    setQuestionarioToDelete(idCompilazione);
    setIsDeleteModalOpen(true);
  }

  const closeDeleteModal = () => {
    setIsDeleteModalOpen(false);
    setQuestionarioToDelete(null);
  }

  const handleDelete = () => {
    fetch(`http://localhost:8080/api/questionariCompilati/deleteQuestionarioCompilato/${questionarioToDelete}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    })
    .then(response => {
      if (!response.ok) {
        throw new Error('Errore nella cancellazione del questionario');
      }
      setQuestionari(questionari.filter(q => q.idCompilazione !== questionarioToDelete));
      closeDeleteModal();
    })
    .catch(error => console.error('Errore:', error))
  }

  return (
    <div className="p-8">
      <h1 className="text-2xl font-bold mt-6">Continua la compilazione</h1>
      
      <div className="mt-6">
        {questionari.length === 0 ? (
          <p className="text-center">Non ci sono questionari da completare.</p>
        ) : (
          <ul>
            {questionari.map((questionario) => (
              <li key={questionario.idCompilazione} className="border p-4 my-2 rounded-lg shadow-lg flex justify-between items-center">

                <div>       
                  <h2 className="text-xl font-semibold">{questionario.titoloQuestionario}</h2>
                  <p className="text-gray-600">Creato da: {questionario.emailCreatore}</p>
                  <p className="text-gray-600">Compilato il: {new Date(questionario.dataCompilazione).toLocaleString()}</p>
                </div>

                <div className="edit flex gap-4">
                  <button
                    onClick={() => continuaCompilazione(questionario.idQuestionario, questionario.idCompilazione)}
                    className="bg-white text-personal-purple border-2 border-personal-purple py-1 px-2 rounded-lg hover:bg-personal-purple hover:text-white transition duration-200 text-lg"
                  >
                    Continua compilazione
                  </button>
                  <button
                    onClick={() => openDeleteModal(questionario.idCompilazione)}
                    className="text-red-600 hover:text-red-800 mr-6"
                    >
                      <TrashIcon className="h-6 w-6" />
                    </button>
                </div>

              </li>
            ))}
          </ul>
        )}

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
                className="bg-red-500 text-white px-6 py-2 rounded-lg mr-4 hover:bg-red-600 transition">
                Elimina
              </button>
              <button 
                onClick={closeDeleteModal} 
                className="bg-gray-500 text-white px-6 py-2 rounded-lg hover:bg-gray-600 transition">
                Annulla
              </button>
            </div>
          </div>
        </ReactModal>
      </div>
    </div>
  );
};

export default QuestionariCompilati;
