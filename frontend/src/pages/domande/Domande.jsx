import React, { useState, useEffect } from 'react';
import ReactModal from 'react-modal'; 
import { TrashIcon, PencilSquareIcon } from '@heroicons/react/24/solid';

import CreaDomanda from './CreaDomanda';

const Domande = ({ user }) => {
  const [domande, setDomande] = useState([]);
  const [updateDomande, setUpdateDomande] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [domandaIdToDelete, setDomandaIdToDelete] = useState(null);
  const [domandaToEdit, setDomandaToEdit] = useState(null);
  const [editedArgomento, setEditedArgomento] = useState('');
  const [editedTesto, setEditedTesto] = useState('');
  const [filtro, setFiltro] = useState("tue");
  const [searchTerm, setSearchTerm] = useState("");
  const token = sessionStorage.getItem("jwt");

  ReactModal.setAppElement('#root');

  useEffect(() => {
    if (!user || !user.email) return;

    let url = filtro === "tue" 
      ? `http://localhost:8080/api/domande/${user.email}`
      : `http://localhost:8080/api/domande/tutteLeDomande`;

    fetch(url, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    })
    .then(response => {
      if (!response.ok) {
        throw new Error('Errore nel recupero delle domande');
      }
      return response.json();
    })
    .then(data => {
      console.log("Risposta dal backend:", data);
      setDomande(data);
    })
    .catch(error => {
      console.error('Errore:', error);
    });

    setUpdateDomande(false);
  }, [user, filtro, updateDomande]);

  const openDeleteModal = (id) => {
    setDomandaIdToDelete(id);
    setIsDeleteModalOpen(true);
  };

  const closeDeleteModal = () => {
    setIsDeleteModalOpen(false);
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
      setDomande(prevState => prevState.filter(q => q.idDomanda !== domandaIdToDelete));
      setUpdateDomande(true);
      closeDeleteModal();
    })
    .catch(error => {
      console.error('Errore:', error);
    });
  };

  const openEditModal = (domanda) => {
    setDomandaToEdit(domanda);
    setEditedArgomento(domanda.argomento);
    setEditedTesto(domanda.testoDomanda);
    setIsEditModalOpen(true);
  };

  const closeEditModal = () => {
    setIsEditModalOpen(false);
    setDomandaToEdit(null);
  };

  const handleEditDomanda = () => {
    if (!domandaToEdit) return;

    const updatedDomanda = {
      argomento: editedArgomento,
      testoDomanda: editedTesto,
    };

    fetch(`http://localhost:8080/api/domande/updateDomanda/${domandaToEdit.idDomanda}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(updatedDomanda)
    })
    .then(response => {
      if (!response.ok) {
        throw new Error('Errore nell\'aggiornamento della domanda');
      }
      setDomande(prevState => prevState.map(d => d.idDomanda === updatedDomanda.idDomanda ? updatedDomanda : d));
      setUpdateDomande(true);
      closeEditModal();
      alert("Domanda modificata con successo! ✅");
    })
    .catch(error => {
      console.error('Errore:', error);
      alert("Si è verificato un errore durante la modifica. ❌");
    });
  };

  const domandeFiltrate = domande.filter(d => 
    d.argomento.toLowerCase().includes(searchTerm.toLowerCase())
  );


  return (
    <div className='mx-24'>
      <h1 className="text-4xl">Domande</h1>
      <div className="flex justify-between items-center mt-4">
        <select value={filtro} onChange={(e) => setFiltro(e.target.value)} className="border rounded-lg p-2">
          <option value="tue">Le tue domande</option>
          <option value="tutte">Tutte le domande</option>
        </select>
        <input 
          type="text"
          placeholder="Cerca per argomento..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="border rounded-lg p-2"
        />
      </div>
      {domandeFiltrate.length > 0 ? (
        <ul>
          {domandeFiltrate.map(d => (
            <li key={d.idDomanda} className="border p-4 my-2 rounded-lg shadow-lg flex justify-between">
              <div>
                <h3 className="text-xl font-semibold">{d.argomento}</h3>
                <p>{d.testoDomanda}</p>
                {filtro === "tutte" && <p className="text-gray-500 text-sm">Autore: {d.emailUtente}</p>}
              </div>
              <div className="edit flex gap-4">
                <button 
                  className={`text-gray-500 hover:text-gray-700 ${d.emailUtente !== user.email ? "opacity-50 cursor-not-allowed" : ""}`} 
                  onClick={() => openEditModal(d)}
                  disabled={d.emailUtente !== user.email}
                >
                  <PencilSquareIcon className="h-6 w-6" />
                </button>
                <button 
                  className={`text-red-600 hover:text-red-800 ${d.emailUtente !== user.email ? "opacity-50 cursor-not-allowed" : ""}`} 
                  onClick={() => openDeleteModal(d.idDomanda)}
                  disabled={d.emailUtente !== user.email}
                >
                  <TrashIcon className="h-6 w-6" />
                </button>
              </div>
            </li>
          ))}
        </ul>
      ) : (
        <p className="text-gray-500 mt-4">Nessuna domanda trovata.</p>
      )}
      <CreaDomanda user={user} setUpdateDomande={setUpdateDomande} />

      {/* Modal per eliminazione domanda */}
      <ReactModal
        isOpen={isDeleteModalOpen}
        onRequestClose={closeDeleteModal}
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
              onClick={closeDeleteModal} 
              className="bg-gray-500 text-white px-6 py-2 rounded-lg hover:bg-gray-600 transition">
              Annulla
            </button>
          </div>
        </div>
      </ReactModal>

      {/* Modal per modifica domanda */}
      <ReactModal
        isOpen={isEditModalOpen}
        onRequestClose={closeEditModal}
        contentLabel="Modifica domanda"
        className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50"
        overlayClassName="modal-overlay"
      >
        <div className="bg-white p-8 rounded-lg w-96">
          <h2 className="text-2xl font-semibold text-gray-800">Modifica la tua domanda</h2>
          <input 
            type="text" 
            className="border rounded-lg p-2 w-full my-2" 
            value={editedArgomento} 
            onChange={(e) => setEditedArgomento(e.target.value)} 
            placeholder="Argomento"
          />
          <textarea 
            className="border rounded-lg p-2 w-full my-2" 
            value={editedTesto} 
            onChange={(e) => setEditedTesto(e.target.value)} 
            placeholder="Testo della domanda"
          />
          <button onClick={handleEditDomanda} className="bg-blue-500 text-white px-6 py-2 rounded-lg mr-4 hover:bg-blue-600 transition">Salva</button>
          <button onClick={closeEditModal} className="bg-gray-500 text-white px-6 py-2 rounded-lg hover:bg-gray-600 transition">Annulla</button>
        </div>
      </ReactModal>
    </div>
  );
}

export default Domande;
