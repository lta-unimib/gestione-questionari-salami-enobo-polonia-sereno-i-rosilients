import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';

const VisualizzaQuestionario = () => {
  const { id } = useParams();
  console.log('ID del questionario:', id);
  const [questionario, setQuestionario] = useState(null);
  const [domande, setDomande] = useState([]);

  useEffect(() => {
    // Fetch dettagli questionario (titolo e creatore)
    fetch(`http://localhost:8080/api/questionari/${id}/view`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    })
      .then((res) => {
        console.log("Response:", res); // Log della risposta
        if (!res.ok) {
          throw new Error(`Errore HTTP: ${res.status}`);
        }
        return res.json();
      })
      .then((data) => {
        console.log("Data:", data); // Log dei dati
        setQuestionario(data);
      })
      .catch((err) => console.error('Errore nel recupero del questionario:', err));
  
    // Fetch domande del questionario
    fetch(`http://localhost:8080/api/questionari/${id}/domande`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    })
      .then((res) => {
        console.log("Response:", res); // Log della risposta
        if (!res.ok) {
          throw new Error(`Errore HTTP: ${res.status}`);
        }
        return res.json();
      })
      .then((data) => {
        console.log("Domande:", data); // Log delle domande
        setDomande(data);
      })
      .catch((err) => console.error('Errore nel recupero delle domande:', err));
  }, [id]);

  return (
    <div className="max-w-3xl mx-auto mt-8 p-6 bg-white shadow-lg rounded-lg">
      {questionario ? (
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
                  {/* Argomento non in grassetto */}
                  <h3 className="text-md font text-gray-700"> Argomento: {domanda.argomento}</h3>
  
                  {/* Testo della domanda in grassetto */}
                  <p className="text-xl text-gray-900 mt-2 ">📌 {domanda.testoDomanda}</p>
  
                  {/* Visualizzazione dell'immagine, se presente */}
                  {domanda.imagePath && (
                    <div className="mt-4">
                      <img
                        src={`http://localhost:8080${domanda.imagePath}`}
                        alt="Immagine della domanda"
                        className="max-w-full h-auto rounded-lg"
                      />
                    </div>
                  )}
  
                  {/* Visualizzazione delle opzioni */}
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
      ) : (
        <p className="text-gray-500">Caricamento in corso...</p>
      )}
    </div>
  );
  
};

export default VisualizzaQuestionario;