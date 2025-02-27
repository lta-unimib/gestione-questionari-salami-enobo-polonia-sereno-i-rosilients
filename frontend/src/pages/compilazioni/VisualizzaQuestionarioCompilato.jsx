import { ArrowLongLeftIcon } from '@heroicons/react/24/solid';
import { QuestionMarkCircleIcon } from '@heroicons/react/24/outline';
import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { 
  fetchDomandeQuestionario, 
  fetchQuestionarioCompilato, 
  fetchRisposteCompilazione 
} from '../../services/compilazioniService';

const VisualizzaQuestionarioCompilato = () => {
  const { idCompilazione, idQuestionario } = useParams();
  const navigate = useNavigate();
  const [domande, setDomande] = useState([]);
  const [questionarioCompilato, setQuestionarioCompilato] = useState(null);
  const [risposte, setRisposte] = useState([]);
  const userEmail = localStorage.getItem("userEmail");

  useEffect(() => {
    const fetchData = async () => {
      try {
        // Fetch all data using the service functions
        const [dataDomande, dataCompilazione, dataRisposte] = await Promise.all([
          fetchDomandeQuestionario(idQuestionario),
          fetchQuestionarioCompilato(idCompilazione),
          fetchRisposteCompilazione(idCompilazione)
        ]);

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
        setDomande(dataDomande.domande || dataDomande);
      } catch (error) {
        console.error('Errore durante il recupero dei dati:', error);
      }
    };

    fetchData();
  }, [idCompilazione, idQuestionario]);

  return (
    <div className="max-w-3xl mx-auto mt-8 p-6 bg-white shadow-lg rounded-lg border-t-4 border-personal-purple">
      {questionarioCompilato ? (
        <>
          {/* Titolo e informazioni del questionario */}
          <h1 className="text-3xl font-bold text-personal-purple">{questionarioCompilato.titoloQuestionario}</h1>
          <p className="text-gray-600 mt-2">
            {userEmail === questionarioCompilato.emailCreatore
              ? "Compilato da te"
              : `Compilato da: ${questionarioCompilato.emailCreatore}`}
          </p>
          <p className="text-gray-600 mt-2">
            Data di compilazione: <strong>{new Date(questionarioCompilato.dataCompilazione).toLocaleDateString()}</strong>
          </p>
  
          {/* Sezione Risposte */}
          <h2 className="text-2xl font-semibold text-personal-purple mt-6">Risposte</h2>
          {risposte.length > 0 ? (
            <ul className="mt-4 space-y-4">
              {risposte.map((risposta) => (
                <li key={risposta.idDomanda} className="border p-4 rounded-lg shadow-md bg-gray-50 hover:bg-gray-100 transition-all">
                  {/* Argomento */}
                  <h3 className="text-md font-medium text-gray-500 mb-1">Argomento: {risposta.argomento}</h3>
  
                  <div className="flex items-center">
                    <QuestionMarkCircleIcon className="w-4 text-personal-purple"/> 
                    <p className="text-xl text-gray-900 ml-2">{risposta.testoDomanda}</p>
                  </div>
                  
                  {/* Immagine della domanda (se presente) */}
                  {risposta.imagePath && (
                    <div className="mt-4">
                      <img
                        src={`http://localhost:8080${risposta.imagePath}`}
                        alt="Immagine della domanda"
                        className="w-52 h-auto rounded-lg shadow-sm"
                      />
                    </div>
                  )}
  
                  {/* Risposta */}
                  <p className="text-md font-medium text-gray-800">Risposta:</p>
                  <p className="text-xl text-gray-900">{risposta.testoRisposta}</p>
                </li>
              ))}
            </ul>
          ) : (
            <p className="text-gray-500 mt-4">Nessuna risposta trovata.</p>
          )}
        </>
      ) : (
        <div className="flex justify-center py-12">
          <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-personal-purple" />
        </div>
      )}
  
      {/* Pulsante "Torna Indietro" */}
      <div className="flex justify-between mt-8">
        <button
          onClick={() => navigate(-1)}
          className="flex justify-around gap-2 bg-personal-purple text-white py-2.5 px-6 rounded-lg hover:bg-[#4a1ed8] transition-all w-48 shadow-md"
        >
          <ArrowLongLeftIcon className="h-5 w-5 my-auto" />
          <span className="my-auto">Torna Indietro</span>
        </button>
      </div>
    </div>
  );
};

export default VisualizzaQuestionarioCompilato;