import React, { useState, useEffect } from 'react'
import ReactModal from 'react-modal'; 
import { TrashIcon } from '@heroicons/react/24/solid'

import CreaQuestionario from './CreaQuestionario'

const Questionari = ({ user }) => {
  const [questionari, setQuestionari] = useState([])
  const [newQuestionario, setNewQuestionario] = useState(false)
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [questionarioIdToDelete, setQuestionarioIdToDelete] = useState(null);

  const token = sessionStorage.getItem("jwt"); 

  ReactModal.setAppElement('#root');

  useEffect(() => {
    if (!user || !user.email) return;

  
    fetch(`http://localhost:8080/api/questionari/${user.email}`, {
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
      setQuestionari(data);
    })
    .catch(error => {
      console.error('Errore:', error);
    });
    setNewQuestionario(false)
    
  }, [user, newQuestionario])

  const openModal = (id) => {
    setQuestionarioIdToDelete(id);
    setIsModalOpen(true);
  };

  const closeModal = () => {
    setIsModalOpen(false);
    setQuestionarioIdToDelete(null);
  };

  const handleDeleteQuestionario = () => {
    fetch(`http://localhost:8080/api/questionari/deleteQuestionario/${questionarioIdToDelete}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    })
      .then(response => {
        if (!response.ok) {
          throw new Error('Errore nell\'eliminazione del questionario');
        }
        // Rimuovere il questionario eliminato dallo stato
        setQuestionari(prevState => prevState.filter(q => q.id !== questionarioIdToDelete));
        setNewQuestionario(true)
        closeModal();
      })
      .catch(error => {
        console.error('Errore:', error);
      });

  }
  

  return (
    <div className='mx-24'>
      <h1 className="text-4xl">Questionari</h1>
      <h2 className="mt-8 text-2xl ">I tuoi questionari</h2>
      {questionari.length > 0 ? (
        <ul>
          {questionari.map(q => (
            <li key={q.idQuestionario} className="border p-4 my-2 rounded-lg shadow-lg flex justify-between">
              <h3 className="text-xl font-semibold my-auto">{q.nome}</h3>
              <button className="text-red-600 hover:text-red-800" onClick={() => openModal(q.idQuestionario)}>
                <TrashIcon className="h-6 w-6" />
              </button>
            </li>
          ))}
        </ul>
      ) : (
        <p className="text-gray-500 mt-4">Nessun questionario trovato.</p>
      )}
      <CreaQuestionario user={user} setNewQuestionario={setNewQuestionario} />

      <ReactModal
        isOpen={isModalOpen}
        onRequestClose={closeModal}
        contentLabel="Conferma eliminazione"
        className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50"
        overlayClassName="modal-overlay"
      >
        <div className="bg-white p-8 rounded-lg w-96 text-center">
            <h2 className="text-2xl font-semibold text-gray-800">Sei sicuro di voler eliminare questo questionario?</h2>
            <div className="mt-4">
              <button 
                onClick={handleDeleteQuestionario} 
                className="bg-red-500 text-white px-6 py-2 rounded-lg mr-4 hover:bg-red-600 transition">
                Elimina
              </button>
              <button 
                onClick={closeModal} 
                className="bg-gray-500 text-white px-6 py-2 rounded-lg hover:bg-gray-600 transition">
                Annulla
              </button>
            </div>
          </div>
      </ReactModal>

    </div>
  )
}

export default Questionari