import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { TrashIcon, PencilSquareIcon } from '@heroicons/react/24/solid';
import ReactModal from 'react-modal';
import CreaQuestionario from './CreaQuestionario';
import{getQuestionariByEmail, getTutteLeDomande, getDomandeByQuestionario, updateQuestionario, deleteQuestionario} from '../../services/questionarioService';


const Questionari = ({ user }) => {
  const navigate = useNavigate();
  const [questionari, setQuestionari] = useState([]);
  const [idQuestionario, setIdQuestionario] = useState(null);
  const [updateQuestionari, setUpdateQuestionari] = useState(false);
  const [userEmail, setUserEmail] = useState(localStorage.getItem("userEmail"));
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

  // Caricamento dei questionari dell'utente
  useEffect(() => {
    if (!user || !userEmail) return;

    const fetchQuestionari = async () => {
      try {
        const data = await getQuestionariByEmail(userEmail);
        setQuestionari(data);
      } catch (error) {
        console.error('Errore nel caricamento dei questionari:', error);
      }
    };

    fetchQuestionari();
    setUpdateQuestionari(false);
  }, [user, updateQuestionari, userEmail]);

  // Apertura e chiusura modale eliminazione
  const openDeleteModal = (id) => {
    setQuestionarioIdToDelete(id);
    setIsDeleteModalOpen(true);
  };

  const closeDeleteModal = () => {
    setIsDeleteModalOpen(false);
    setQuestionarioIdToDelete(null);
  };

  // Caricamento di tutte le domande disponibili
  useEffect(() => {
    const fetchTutteLeDomande = async () => {
      try {
        const data = await getTutteLeDomande();
        setTutteLeDomande(data);
      } catch (error) {
        console.error('Errore nel caricamento delle domande:', error);
      }
    };

    fetchTutteLeDomande();
  }, []);

  // Recupera le domande associate al questionario da modificare
  useEffect(() => {
    if (isEditModalOpen && questionarioToEdit) {
      const fetchDomandeQuestionario = async () => {
        try {
          const data = await getDomandeByQuestionario(questionarioToEdit.idQuestionario);
          setDomande(data);
          setDomandeAssociate(data.map((d) => d.idDomanda.toString()));
        } catch (error) {
          console.error('Errore nel recupero delle domande associate:', error);
        }
      };

      fetchDomandeQuestionario();
    }
  }, [isEditModalOpen, questionarioToEdit]);

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
  const handleDeleteQuestionario = async () => {
    try {
      await deleteQuestionario(questionarioIdToDelete);
      setQuestionari((prevState) => prevState.filter((q) => q.idQuestionario !== questionarioIdToDelete));
      setUpdateQuestionari(true);
      closeDeleteModal();
    } catch (error) {
      console.error('Errore nell\'eliminazione:', error);
    }
  };

  // Modifica questionario
  const handleEditQuestionario = async () => {
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

    try {
      await updateQuestionario(questionarioToEdit.idQuestionario, updatedQuestionario);
      setQuestionari((prevState) => 
        prevState.map((q) => 
          (q.idQuestionario === updatedQuestionario.idQuestionario ? updatedQuestionario : q)
        )
      );
      setUpdateQuestionari(true);
      closeEditModal();
      alert('Questionario modificato con successo! ✅');
    } catch (error) {
      console.error('Errore durante la modifica:', error);
      alert('Si è verificato un errore durante la modifica. ❌');
    }
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
                className='text-personal-purple opacity-[0.75] underline text-xl font-semibold'>
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

      {/* Modal per modifica del questionario */}
      <ReactModal
        isOpen={isEditModalOpen}
        onRequestClose={closeEditModal}
        className='fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50'
        overlayClassName="modal-overlay"
      >
        <div className='bg-white p-8 rounded-lg w-full max-w-md shadow-xl border-t-4 border-personal-purple'>
          <h2 className='text-2xl font-semibold text-personal-purple mb-4 flex items-center'>
            <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
            </svg>
            Modifica il questionario
          </h2>
          
          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Nome del questionario</label>
              <input
                type='text'
                className='border rounded-lg p-3 w-full focus:ring-2 focus:ring-personal-purple focus:border-transparent transition-all outline-none'
                value={editedNome}
                onChange={(e) => setEditedNome(e.target.value)}
                placeholder='Inserisci il nome del questionario'
              />
            </div>
            
            <div className="bg-gray-50 p-4 rounded-lg">
              <h3 className="text-lg font-semibold text-[personal-purple3603CD] mb-2 flex items-center">
                Seleziona le domande
              </h3>
              
              <div className="max-h-48 overflow-y-auto border border-gray-200 rounded-lg">
                {tutteLeDomande.length > 0 ? (
                  tutteLeDomande.map((d) => (
                    <label
                      key={d.idDomanda}
                      className={`flex items-center p-3 hover:bg-gray-100 cursor-pointer transition-all ${
                        domandeAssociate.includes(d.idDomanda.toString()) 
                        ? 'bg-personal-purple bg-opacity-10 border-l-4 border-personal-purple' 
                        : 'border-l-4 border-transparent'
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
                              ? prev.filter((id) => id !== selectedId)
                              : [...prev, selectedId]
                          );
                        }}
                        className="rounded text-black focus:ring-black mr-3 h-4 w-4"
                      />
                      <span className={`${
                        domandeAssociate.includes(d.idDomanda.toString()) 
                        ? 'text-black font-medium' 
                        : 'text-gray-700'
                      }`}>
                        {d.testoDomanda}
                      </span>
                    </label>
                  ))
                ) : (
                  <div className="flex items-center justify-center p-4 text-gray-500">
                    <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-2 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                    </svg>
                    Nessuna domanda disponibile.
                  </div>
                )}
              </div>
              
              <div className="mt-2 text-xs text-gray-500 flex items-center">
                <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
                Seleziona le domande da includere nel questionario
              </div>
            </div>
          </div>
          
          <div className="flex justify-end mt-6 space-x-3">
            <button
              onClick={closeEditModal}
              className='px-5 py-2.5 rounded-lg border border-gray-300 text-gray-700 hover:bg-gray-100 transition-all'
            >
              Annulla
            </button>
            <button
              onClick={handleEditQuestionario}
              className='bg-personal-purple text-white px-5 py-2.5 rounded-lg hover:bg-[#4a1ed8] transition-all flex items-center'
            >
              <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
              </svg>
              Salva modifiche
            </button>
          </div>
        </div>
      </ReactModal>
    </div>
  );
};

export default Questionari;