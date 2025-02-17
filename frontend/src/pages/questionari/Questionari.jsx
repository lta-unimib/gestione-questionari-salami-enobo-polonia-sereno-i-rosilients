import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { TrashIcon, PencilSquareIcon } from '@heroicons/react/24/solid';
import ReactModal from 'react-modal';
import CreaQuestionario from './CreaQuestionario';

const Questionari = ({ user }) => {
  const [questionari, setQuestionari] = useState([]);
  const [updateQuestionari, setUpdateQuestionari] = useState(false);
  const token = localStorage.getItem('jwt');
  const [userEmail, setUserEmail] = useState(localStorage.getItem("userEmail"));
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

  return (
    <div className='mx-24'>
      <h1 className='text-4xl'>Questionari</h1>
      <h2 className='mt-8 text-2xl'>I tuoi questionari</h2>
      {questionari.length > 0 ? (
        <ul>
          {questionari.map((q) => (
            <li key={q.idQuestionario} className='border p-4 my-2 rounded-lg shadow-lg flex justify-between'>
              <Link to={`/questionari/${q.idQuestionario}`} className='text-blue-500 hover:underline text-xl font-semibold'>
                {q.nome}
              </Link>
              <div className='edit flex gap-4'>
                <button className='text-gray-500 hover:text-gray-700'>
                  <PencilSquareIcon className='h-6 w-6' />
                </button>
                <button className='text-red-600 hover:text-red-800'>
                  <TrashIcon className='h-6 w-6' />
                </button>
              </div>
            </li>
          ))}
        </ul>
      ) : (
        <p className='text-gray-500 mt-4'>Nessun questionario trovato.</p>
      )}
      <CreaQuestionario user={user} setUpdateQuestionari={setUpdateQuestionari} />
    </div>
  );
};

export default Questionari;