import { ArrowLongLeftIcon } from '@heroicons/react/24/solid';
import React, { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import ReactModal from 'react-modal';
import { TrashIcon } from '@heroicons/react/24/solid';

const VisualizzaQuestionarioCompilato = () => {
  const { idCompilazione } = useParams();
  const navigate = useNavigate();
  console.log('ID della compilazione:', idCompilazione);
  const [questionarioCompilato, setQuestionarioCompilato] = useState(null);
  const [risposte, setRisposte] = useState([]);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);

  // Fetch questionario compilato
  useEffect(() => {
    fetch(`http://localhost:8080/api/questionariCompilati/${idCompilazione}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    })
      .then((res) => {
        if (!res.ok) {
          throw new Error(`Errore HTTP: ${res.status}`);
        }
        return res.json();
      })
      .then((data) => {
        setQuestionarioCompilato(data);
        setRisposte(data.risposte);
      })
      .catch((err) => console.error('Errore nel recupero del questionario compilato:', err));
  }, [idCompilazione]);

  const openDeleteModal = () => {
    setIsDeleteModalOpen(true);
  };

  const closeDeleteModal = () => {
    setIsDeleteModalOpen(false);
  };

  const handleDelete = () => {
    const token = localStorage.getItem('jwt');
  
    if (!token) {
      console.error('Token mancante');
      return;
    }
  
    fetch(`http://localhost:8080/api/deleteQuestionarioCompilato/${idCompilazione}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`, 
      },
    })
      .then((res) => {
        if (!res.ok) {
          throw new Error(`Errore HTTP: ${res.status}`);
        }
        navigate('/questionari/compilazioni');
      })
      .catch((err) => console.error('Errore nella cancellazione del questionario compilato:', err));
  };

  return (
    <div className="max-w-3xl mx-auto mt-8 p-6 bg-white shadow-lg rounded-lg">
      {questionarioCompilato ? (
        <>
          <h1 className="text-3xl font-bold text-personal-purple">{questionarioCompilato.nome}</h1>
          <p className="text-gray-600 mt-2">
            Creato da: <strong>{questionarioCompilato.emailUtente?.split('@')[0]}</strong>
          </p>
          <p className="text-gray-600 mt-2">
            Data di compilazione: <strong>{new Date(questionarioCompilato.dataCompilazione).toLocaleDateString()}</strong>
          </p>

          <h2 className="text-2xl font-semibold mt-6">Risposte</h2>
          {risposte.length > 0 ? (
            <ul className="mt-4 space-y-4">
              {risposte.map((risposta) => (
                <li key={risposta.idDomanda} className="border p-4 rounded-lg shadow-md bg-gray-100">
                  <h3 className="text-md font text-gray-700">Domanda ID: {risposta.idDomanda}</h3>
                  <p className="text-xl text-gray-900 mt-2">📌 {risposta.testoRisposta}</p>
                </li>
              ))}
            </ul>
          ) : (
            <p className="text-gray-500 mt-4">Nessuna risposta trovata.</p>
          )}
        </>
      ) : (
        <p className="text-gray-500">Caricamento in corso...</p>
      )}

      <div className="flex justify-between mt-8">
        <button
          onClick={() => navigate(-1)}
          className="flex justify-around gap-2 bg-gray-300 text-gray-800 py-2 px-4 rounded-lg hover:bg-gray-400 transition-all w-48"
        >
          <ArrowLongLeftIcon className="h-5 w-5 my-auto" />
          <span className="my-auto">Torna Indietro</span>
        </button>

        <button
          onClick={openDeleteModal}
          className="flex justify-around gap-2 bg-red-500 text-white py-2 px-4 rounded-lg hover:bg-red-600 transition-all w-32"
        >
          <TrashIcon className="h-5 w-5 my-auto" />
          <span className="my-auto">Elimina</span>
        </button>
      </div>

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

export default VisualizzaQuestionarioCompilato;