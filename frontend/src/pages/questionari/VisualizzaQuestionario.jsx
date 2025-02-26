import { ArrowLongLeftIcon } from '@heroicons/react/24/solid';
import React, { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import {getQuestionarioDetails, getQuestionarioDomande} from '../../services/questionarioServices';

const VisualizzaQuestionario = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [questionario, setQuestionario] = useState(null);
  const [domande, setDomande] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);


  useEffect(() => {
    const fetchQuestionarioData = async () => {
      try {
        setLoading(true);
        
        // Fetch dettagli questionario e domande in parallelo
        const [questionarioData, domandeData] = await Promise.all([
          getQuestionarioDetails(id),
          getQuestionarioDomande(id)
        ]);
        
        setQuestionario(questionarioData);
        setDomande(domandeData);
        setError(null);
      } catch (error) {
        console.error('Errore nel caricamento dei dati:', error);
        setError('Si è verificato un errore nel caricamento del questionario');
      } finally {
        setLoading(false);
      }
    };

    fetchQuestionarioData();
  }, [id]);

  if (loading) {
    return (
      <div className="max-w-3xl mx-auto mt-8 p-6 bg-white shadow-lg rounded-lg">
        <p className="text-gray-500">Caricamento in corso...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="max-w-3xl mx-auto mt-8 p-6 bg-white shadow-lg rounded-lg">
        <div className="flex justify-between mb-6">
          <button
            onClick={() => navigate(-1)}
            className="flex justify-around gap-1 bg-gray-300 text-gray-800 py-2 px-4 rounded-lg hover:bg-gray-400 transition-all w-40"
          >
            <ArrowLongLeftIcon className="h-5 w-5 my-auto" />
            <span className="my-auto text-sm">Torna Indietro</span>
          </button>
        </div>
        <p className="text-red-500">{error}</p>
      </div>
    );
  }

  return (
    <div className="max-w-3xl mx-auto mt-8 p-6 bg-white shadow-lg rounded-lg">
      <div className="flex justify-between mb-6">
        <button
          onClick={() => navigate(-1)}
          className="flex justify-around gap-1 bg-gray-300 text-gray-800 py-2 px-4 rounded-lg hover:bg-gray-400 transition-all w-40"
        >
          <ArrowLongLeftIcon className="h-5 w-5 my-auto" />
          <span className="my-auto text-sm">Torna Indietro</span>
        </button>
        {domande.length > 0 && (
          <Link
            to={`/questionari/compilaQuestionario/${id}`}
            className="flex justify-around gap-2 bg-white border-personal-purple border-2 text-personal-purple py-2 px-4 rounded-lg hover:bg-personal-purple hover:text-white transition-all w-32"
          >
            <span className="my-auto text-sm">Compila</span>
          </Link>
        )}
      </div>

      {questionario && (
        <>
          <h1 className="text-3xl font-bold text-personal-purple">{questionario.nome}</h1>
          <p className="text-gray-600 mt-2">
            Creato da: <strong>{questionario.emailUtente?.split('@')[0]}</strong>
          </p>
  
          <h2 className="text-2xl font-semibold mt-6">Domande</h2>
          {domande.length > 0 ? (
            <ul className="mt-4 space-y-4">
              {domande.map((domanda) => (
                <li key={domanda.idDomanda} className="border p-4 rounded-lg shadow-md bg-gray-100">
                  <h3 className="text-md font text-gray-700"> Argomento: {domanda.argomento}</h3>
  
                  <p className="text-xl text-gray-900 mt-2 ">📌 {domanda.testoDomanda}</p>
  
                  {domanda.imagePath && (
                    <div className="mt-4">
                      <img
                        src={`http://localhost:8080${domanda.imagePath}`}
                        alt="Immagine della domanda"
                        className="max-w-full h-auto rounded-lg"
                      />
                    </div>
                  )}
  
                  {domanda.opzioni && domanda.opzioni.length > 0 ? (
                    <div className="mt-4">
                      <h4 className="text-lg font text-gray-700">Opzioni:</h4>
                      <ul className="list-disc list-inside">
                        {domanda.opzioni.map((opzione, index) => (
                          <li key={index} className="text-black-600">
                            {opzione}
                          </li>
                        ))}
                      </ul>
                    </div>
                  ) : null}
                </li>
              ))}
            </ul>
          ) : (
            <p className="text-gray-500 mt-4">Nessuna domanda trovata.</p>
          )}
        </>
      )}
    </div>
  );
};

export default VisualizzaQuestionario;