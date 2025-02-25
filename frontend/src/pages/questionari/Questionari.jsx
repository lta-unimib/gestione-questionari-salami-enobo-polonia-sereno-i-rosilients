import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { TrashIcon, PencilSquareIcon } from '@heroicons/react/24/solid';
import ReactModal from 'react-modal';
import CreaQuestionario from './CreaQuestionario';

const Questionari = ({ user }) => {
  const navigate = useNavigate();
  const [questionari, setQuestionari] = useState([]);
  const [idQuestionario, setIdQuestionario] = useState(null);
  const [updateQuestionari, setUpdateQuestionari] = useState(false);
  const token = localStorage.getItem('jwt');
  const [userEmail, setUserEmail] = useState(localStorage.getItem("userEmail"));;
  const [isSelectModalOpen, setIsSelectModalOpen] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [questionarioIdToDelete, setQuestionarioIdToDelete] = useState(null);
  const [questionarioToEdit, setQuestionarioToEdit] = useState(null);
  const [editedNome, setEditedNome] = useState('');
  const [domande, setDomande] = useState([]); 
  const [domandeAssociate, setDomandeAssociate] = useState([]);
  const [tutteLeDomande, setTutteLeDomande] = useState([]);
  ReactModal.setAppElement('#root');

  useEffect(() => {
    if (!user || !userEmail) return;

    fetch(`http://localhost:8080/api/questionari/${userEmail}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
      },
    })
      .then((response) => {
        if (!response.ok) throw new Error('Errore nel recupero dei questionari');
        return response.json();
      })
      .then((data) => {
        setQuestionari(data);
      })
      .catch((error) => {
        console.error('Errore:', error);
      });

    setUpdateQuestionari(false);
  }, [user, updateQuestionari]);

  // Apertura e chiusura modale eliminazione
  const openDeleteModal = (id) => {
    setQuestionarioIdToDelete(id);
    setIsDeleteModalOpen(true);
  };

  const closeDeleteModal = () => {
    setIsDeleteModalOpen(false);
    setQuestionarioIdToDelete(null);
  };

  //useEffect per recuperare le domande associate al questionario da modificare
  useEffect(() => {
    fetch('http://localhost:8080/api/domande/tutteLeDomande', {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
      },
    })
      .then((response) => response.json())
      .then((data) => {
        setTutteLeDomande(data);  // Salva tutte le domande disponibili nel db
      })
      .catch((error) => console.error('Errore nel recupero delle domande:', error));
  }, [token]);

  // Recupera le domande associate al questionario da modificare
  useEffect(() => {
    if (isEditModalOpen && questionarioToEdit) {
      fetch(`http://localhost:8080/api/domande/domandeByQuestionario/${questionarioToEdit.idQuestionario}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
      })
        .then((response) => response.json())
        .then((data) => {
          setDomande(data);  // Salva le domande associate al questionario
          setDomandeAssociate(data.map((d) => d.idDomanda.toString()));  // Imposta gli ID delle domande già associate
        })
        .catch((error) => console.error('Errore:', error));
    }
  }, [isEditModalOpen, questionarioToEdit, token]);


  // Apertura e chiusura modale modifica
  const openEditModal = (questionario) => {
    setQuestionarioToEdit(questionario);
    setEditedNome(questionario.nome);
    setIsEditModalOpen(true);
  };

  const closeEditModal = () => {
    setIsEditModalOpen(false);
    setQuestionarioToEdit(null);
  };

  // Eliminazione questionario
  const handleDeleteQuestionario = () => {
    fetch(`http://localhost:8080/api/questionari/deleteQuestionario/${questionarioIdToDelete}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
      },
    })
     .then((response) => {
      if (!response.ok) {
        throw new Error('Errore nell\'eliminazione del questionario');
      }
      setQuestionari((prevState) => prevState.filter((q) => q.idQuestionario !== questionarioIdToDelete));
        setUpdateQuestionari(true);
        closeDeleteModal();
      })
      .catch((error) => {
        console.error('Errore:', error);
      });
  };

  // Modifica questionario
  const handleEditQuestionario = () => {
    if (!questionarioToEdit) return;

    if (!editedNome || domandeAssociate.length === 0) {
      alert('Compila tutti i campi e seleziona almeno una domanda.');
      return;
    }

    const updatedQuestionario = {
      ...questionarioToEdit,
      nome: editedNome,
      idDomande: domandeAssociate.map(Number),
    };

    fetch(`http://localhost:8080/api/questionari/updateQuestionario/${questionarioToEdit.idQuestionario}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(updatedQuestionario),
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error("Errore nell'aggiornamento del questionario");
        }
        setQuestionari((prevState) => prevState.map((q) => (q.idQuestionario === updatedQuestionario.idQuestionario ? updatedQuestionario : q)));
        setUpdateQuestionari(true);
        closeEditModal();
        alert('Questionario modificato con successo! ✅');
      })
      .catch((error) => {
        console.error('Errore:', error);
        alert('Si è verificato un errore durante la modifica. ❌');
      });
  };

  // Apre il modal per la selezione delle opzioni
  const openSelectModal = () => {
    setIsSelectModalOpen(true);
  };

  // Chiude il modal per la selezione delle opzioni
  const closeSelectModal = () => {
    setIsSelectModalOpen(false);
  };

  // Visualizza il questionario
  const handleVisualizzaQuestionario = (idQuestionario) => {
    navigate(`/questionari/${idQuestionario}`);
    setIsSelectModalOpen(false);
  }

  const handleVisualizzaCompilazioni = (idQuestionario) => {
    navigate(`/visualizzaCompilazioniDiTutti/${idQuestionario}`);
    setIsSelectModalOpen(false);
  }

  return (
    <div className='mx-24'>
      <h1 className='text-4xl'>Questionari</h1>
      <h2 className='mt-8 text-2xl'>I tuoi questionari</h2>
      <CreaQuestionario user={user} setUpdateQuestionari={setUpdateQuestionari} />

      {questionari.length > 0 ? (
        <ul className='mt-12'>
          {[...questionari].reverse().map((q) => (
            <li key={q.idQuestionario} className='border p-4 my-2 rounded-lg shadow-lg flex justify-between'>
              <Link 
                to="#"
                onClick = {() => {
                    openSelectModal();
                    setIdQuestionario(q.idQuestionario);
                  }
                }
                className='text-blue-500 hover:underline text-xl font-semibold'>
                {q.nome}
              </Link>
              <div className='edit flex gap-4'>
              <button className='text-gray-500 hover:text-gray-700' onClick={() => openEditModal(q)}>
                  <PencilSquareIcon className='h-6 w-6' />
                </button>
                <button className='text-red-600 hover:text-red-800' onClick={() => openDeleteModal(q.idQuestionario)}>
                  <TrashIcon className='h-6 w-6' />
                </button>
              </div>
            </li>
          ))}
        </ul>
      ) : (
        <p className='text-gray-500 mt-4'>Nessun questionario trovato.</p>
      )}


{/* Modal per selezione opzioni */}
<ReactModal isOpen={isSelectModalOpen} onRequestClose={closeSelectModal} className='fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50'>
<div className='bg-white p-8 rounded-lg w-96 relative'>
  <button
    onClick={closeSelectModal}
    className="text-gray-300 text-2xl hover:text-gray-700 transition-colors absolute top-2 right-2"
  >
    ✖
  </button>
  <h2 className='text-2xl font-semibold mb-8 text-gray-800 text-center'>Cosa vuoi fare con questo Questionario?</h2>
  <div className='flex justify-center space-x-4'>
    <button 
      onClick = {() => handleVisualizzaQuestionario(idQuestionario)} 
      className='bg-white text-personal-purple border-2 border-personal-purple py-1 px-3 rounded-lg hover:bg-personal-purple hover:text-white transition duration-200'
      >
        Visualizza Questionario
    </button>
    <button 
    onClick = {() => handleVisualizzaCompilazioni(idQuestionario)}
    className = 'bg-white text-personal-purple border-2 border-personal-purple py-1 px-3 rounded-lg hover:bg-personal-purple hover:text-white transition duration-200'
    >
      Visualizza Compilazioni
    </button>
  </div>
</div>
</ReactModal>

{/* Modal per eliminazione */}
<ReactModal isOpen={isDeleteModalOpen} onRequestClose={closeDeleteModal} className='fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50'>
  <div className='bg-white p-8 rounded-lg w-96 text-center'>
    <h2 className='text-2xl font-semibold mt-0 mb-8 text-gray-800'>Sei sicuro di voler eliminare questo questionario?</h2>
    <button onClick={handleDeleteQuestionario} className='bg-red-500 text-white px-6 py-2 rounded-lg mr-4 hover:bg-red-600'>Elimina</button>
    <button onClick={closeDeleteModal} className='bg-gray-500 text-white px-6 py-2 rounded-lg hover:bg-gray-600'>Annulla</button>
  </div>
</ReactModal>

{/* Modal per modifica */}
<ReactModal
  isOpen={isEditModalOpen}
  onRequestClose={closeEditModal}
  className='fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50'
>
  <div className='bg-white p-8 rounded-lg w-96'>
    <h2 className='text-2xl font-semibold text-gray-800'>Modifica il nome del questionario</h2>
    <input
      type='text'
      className='border rounded-lg p-2 w-full my-2'
      value={editedNome}
      onChange={(e) => setEditedNome(e.target.value)}
      placeholder='Nome'
    />
    <div className="mb-4">
      <h3 className="text-lg font-semibold mb-2">Seleziona le domande:</h3>
      <div className="max-h-48 overflow-y-auto border rounded-lg p-2"> {/* Contenitore scorrevole */}
        {tutteLeDomande.length > 0 ? (
          tutteLeDomande.map((d) => (
            <label
              key={d.idDomanda}
              className={`flex items-center p-2 hover:bg-gray-100 rounded-lg cursor-pointer ${
                domandeAssociate.includes(d.idDomanda.toString()) ? 'bg-blue-50' : ''
              }`}
            >
              <input
                type="checkbox"
                value={d.idDomanda}
                checked={domandeAssociate.includes(d.idDomanda.toString())}
                onChange={(e) => {
                  const selectedId = e.target.value;
                  setDomandeAssociate((prev) =>
                    prev.includes(selectedId)
                      ? prev.filter((id) => id !== selectedId) // Rimuovi se già selezionata
                      : [...prev, selectedId] // Aggiungi se non selezionata
                  );
                }}
                className="mr-2"
              />
              <span className="text-gray-700">{d.testoDomanda}</span>
            </label>
          ))
        ) : (
          <p className="text-gray-500">Nessuna domanda disponibile.</p>
        )}
      </div>
    </div>
    <button
      onClick={handleEditQuestionario}
      className='bg-blue-500 text-white px-6 py-2 rounded-lg mr-4 hover:bg-blue-600'
    >
      Salva
    </button>
    <button
      onClick={closeEditModal}
      className='bg-gray-500 text-white px-6 py-2 rounded-lg hover:bg-gray-600'
    >
      Annulla
    </button>
  </div>
</ReactModal>
    </div>
  );
};

export default Questionari;