import { ArrowLongLeftIcon } from '@heroicons/react/24/solid';
import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';

const VisualizzaQuestionarioCompilato = () => {
  const { idCompilazione } = useParams();
  const navigate = useNavigate();
  console.log('ID della compilazione:', idCompilazione);
  const [questionarioCompilato, setQuestionarioCompilato] = useState(null);
  const [risposte, setRisposte] = useState([]);

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
          throw new Error('Errore nel recupero del questionario compilato');
        }
        return res.json();
      })
      .then((data) => {
        setQuestionarioCompilato(data);
        setRisposte(data.risposte);
      })
      .catch((err) => console.error('Errore nel recupero del questionario compilato:', err));
  }, [idCompilazione]);

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
      </div>
    </div>
  );
};

export default VisualizzaQuestionarioCompilato;