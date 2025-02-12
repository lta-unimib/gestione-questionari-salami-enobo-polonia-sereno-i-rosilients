import React, { useState, useEffect } from 'react'
import ReactModal from 'react-modal'; 
import { TrashIcon, PencilSquareIcon } from '@heroicons/react/24/solid'

import CreaDomanda from './CreaDomanda'

const Domande = ({ user }) => {
  const [domande, setDomande] = useState([])
  const [newDomanda, setNewDomanda] = useState(false)
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [domandaIdToDelete, setDomandaIdToDelete] = useState(null);

  const token = sessionStorage.getItem("jwt"); 

  ReactModal.setAppElement('#root');

  useEffect(() => {
    if (!user || !user.email) return;

  
    fetch(`http://localhost:8080/api/domande/${user.email}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    })
    .then(response => {
      if (!response.ok) {
        throw new Error('Errore nel recupero dei domande');
      }
      return response.json();
    })
    .then(data => {
      setDomande(data);
    })
    .catch(error => {
      console.error('Errore:', error);
    });
    setNewDomanda(false)
    
  }, [user, newDomanda])

  const openModal = (id) => {
    setDomandaIdToDelete(id);
    setIsModalOpen(true);
  };

  const closeModal = () => {
    setIsModalOpen(false);
    setDomandaIdToDelete(null);
  };

  const handleDeleteDomanda = () => {
    fetch(`http://localhost:8080/api/domande/deleteDomanda/${domandaIdToDelete}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    })
      .then(response => {
        if (!response.ok) {
          throw new Error('Errore nell\'eliminazione della domanda');
        }
        // Rimuovere la domanda eliminata dallo stato
        setDomande(prevState => prevState.filter(q => q.id !== domandaIdToDelete));
        setNewDomanda(true)
        closeModal();
      })
      .catch(error => {
        console.error('Errore:', error);
      });

  }
  

  return (
    <div className='mx-24'>
      <h1 className="text-4xl">Domande</h1>
      <h2 className="mt-8 text-2xl ">Le tue domande</h2>
      {domande.length > 0 ? (
        <ul>
          {domande.map(d => (
            <li key={d.idDomanda} className="border p-4 my-2 rounded-lg shadow-lg flex justify-between">
              <div className="header">
                  <h3 className="text-xl font-semibold my-auto">{d.argomento}</h3>
                  <p>{d.testoDomanda}</p>
              </div>
              <div className="edit flex gap-4">
                <button className="text-gray-500 hover:text-gray-700">
                  <PencilSquareIcon className="h-6 w-6" />
                </button>
                <button className="text-red-600 hover:text-red-800" onClick={() => openModal(d.idDomanda)}>
                  <TrashIcon className="h-6 w-6" />
                </button>
              </div>
            </li>
          ))}
        </ul>
      ) : (
        <p className="text-gray-500 mt-4">Nessuna domanda trovata.</p>
      )}
      <CreaDomanda user={user} setNewDomanda={setNewDomanda} />

      <ReactModal
        isOpen={isModalOpen}
        onRequestClose={closeModal}
        contentLabel="Conferma eliminazione"
        className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50"
        overlayClassName="modal-overlay"
      >
        <div className="bg-white p-8 rounded-lg w-96 text-center">
            <h2 className="text-2xl font-semibold text-gray-800">Sei sicuro di voler eliminare questa domanda?</h2>
            <div className="mt-4">
              <button 
                onClick={handleDeleteDomanda} 
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

export default Domande