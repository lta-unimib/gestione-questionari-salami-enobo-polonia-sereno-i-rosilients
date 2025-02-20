import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const QuestionariCompilati = () => {
  const [questionari, setQuestionari] = useState([]);
  const token = localStorage.getItem("jwt");
  const userEmail = localStorage.getItem("userEmail");
  const navigate = useNavigate();

  // Ottieni i questionari compilati dell'utente
  useEffect(() => {
    if (!userEmail) return;

    let url = `http://localhost:8080/api/questionariCompilati/utente/${userEmail}`;
    
    fetch(url, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    })
    .then(response => {
      if (!response.ok) {
        throw new Error('Errore nel recupero dei questionari');
      }
      return response.json();
    })
    .then(data => {
      console.log("Risposta dal backend:", data);
      setQuestionari(data); // Aggiorna lo stato con i questionari ricevuti
    })
    .catch(error => {
      console.error('Errore:', error);
    });
  }, [userEmail, token]);

  const continuaCompilazione = (idQuestionario, idCompilazione) => {
    navigate(`/questionari/compilaQuestionario/${idQuestionario}?idCompilazione=${idCompilazione}`);
  };

  return (
    <div className="p-8">
      <h1 className="text-2xl font-bold mt-6">Continua la compilazione</h1>
      
      <div className="mt-6">
        {questionari.length === 0 ? (
          <p className="text-center">Non ci sono questionari da completare.</p>
        ) : (
          <ul>
            {questionari.map((questionario) => (
              <li key={questionario.idCompilazione} className="border p-4 rounded-lg mb-4">
                <h2 className="text-xl font-semibold">{questionario.titoloQuestionario}</h2>
                <p className="text-gray-600">Creato da: {questionario.emailCreatore}</p>
                <p className="text-gray-600">Compilato il: {new Date(questionario.dataCompilazione).toLocaleString()}</p>
                
                <button
                  onClick={() => continuaCompilazione(questionario.idQuestionario, questionario.idCompilazione)}
                  className="mt-4 bg-blue-500 text-white py-2 px-4 rounded-lg hover:bg-blue-600"
                >
                  Continua compilazione
                </button>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
};

export default QuestionariCompilati;
