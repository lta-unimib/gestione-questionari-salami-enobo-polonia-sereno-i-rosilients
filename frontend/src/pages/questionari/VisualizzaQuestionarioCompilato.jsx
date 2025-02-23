import { ArrowLongLeftIcon } from '@heroicons/react/24/solid';
import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';

const VisualizzaQuestionarioCompilato = () => {
  const { idCompilazione, idQuestionario } = useParams();
  const navigate = useNavigate();
  const [domande, setDomande] = useState([]);
  const [questionarioCompilato, setQuestionarioCompilato] = useState([]);
  const [risposte, setRisposte] = useState([]);
  const userEmail = localStorage.getItem("userEmail");

  useEffect(() => {
    // Fetch per ottenere le domande
    const fetchDomande = fetch(`http://localhost:8080/api/questionari/${idQuestionario}/domande`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    })
      .then((res) => {
        if (!res.ok) {
          throw new Error('Errore nel recupero delle domande');
        }
        return res.json();
      });

    // Fetch per ottenere la compilazione
    const fetchCompilazione = fetch(`http://localhost:8080/api/questionariCompilati/${idCompilazione}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    })
      .then((res) => {
        if (!res.ok) {
          throw new Error('Errore nel recupero della compilazione');
        }
        return res.json();
      });
  
    // Fetch per ottenere le risposte
    const fetchRisposte = fetch(`http://localhost:8080/api/questionariCompilati/${idCompilazione}/risposte`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    })
      .then((res) => {
        if (!res.ok) {
          throw new Error('Errore nel recupero delle risposte');
        }
        return res.json();
      });

    Promise.all([fetchDomande, fetchCompilazione, fetchRisposte])
      .then(([dataDomande,  dataCompilazione, dataRisposte]) => {

        setQuestionarioCompilato(dataCompilazione);

        const risposteConDomande = dataRisposte.map((risposta) => {

          const domandaCorrispondente = dataDomande.find(
            (domanda) => domanda.idDomanda === risposta.idDomanda
          );

          if (domandaCorrispondente) {
            return {
              ...risposta,
              argomento: domandaCorrispondente.argomento,
              testoDomanda: domandaCorrispondente.testoDomanda,
              imagePath: domandaCorrispondente.imagePath,
            };
          }

          return risposta;
        });

        setRisposte(risposteConDomande);
        setDomande(dataDomande.domande);
      })
      .catch((err) => {
        console.error('Errore durante il recupero dei dati:', err);
      });
  }, [idCompilazione, idQuestionario]);
  

  return (
    <div className="max-w-3xl mx-auto mt-8 p-6 bg-white shadow-lg rounded-lg">
      {questionarioCompilato ? (
        <>
          <h1 className="text-3xl font-bold text-personal-purple">{questionarioCompilato.titoloQuestionario}</h1>
          <p className="text-gray-600 mt-2">
            {userEmail === questionarioCompilato.emailCreatore
              ? "Compilato da te"
              : `Compilato da: ${questionarioCompilato.emailCreatore}`}
          </p>
          <p className="text-gray-600 mt-2">
            Data di compilazione: <strong>{new Date(questionarioCompilato.dataCompilazione).toLocaleDateString()}</strong>
          </p>
          

          <h2 className="text-2xl font-semibold mt-6">Risposte</h2>
          {risposte.length > 0 ? (
            <ul className="mt-4 space-y-4">
              {risposte.map((risposta) => (
                <li key={risposta.idDomanda} className="border p-4 rounded-lg shadow-md bg-gray-100">
                  <h3 className="text-md font text-gray-700"> Argomento: {risposta.argomento}</h3>

                  <p className="text-xl text-gray-900 mt-2 ">Domanda: {risposta.testoDomanda}</p>

                  {risposta.imagePath && (
                    <div className="mt-4">
                      <img
                        src={`http://localhost:8080${risposta.imagePath}`}
                        alt="Immagine della domanda"
                        className="max-w-full h-auto rounded-lg"
                      />
                    </div>
                  )}
                  <p className="text-xl text-gray-900 mt-2">Risposta: {risposta.testoRisposta}</p>
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