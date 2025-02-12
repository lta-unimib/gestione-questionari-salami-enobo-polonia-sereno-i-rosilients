import React, { useState, useEffect } from 'react';

const CreaQuestionario = ({ user, setUpdateQuestionari }) => {
  const [isCreatingQuestionario, setIsCreatingQuestionario] = useState(false);
  const [questionarioNome, setQuestionarioNome] = useState('');
  const [domande, setDomande] = useState([]);
  const [domandeSelezionate, setDomandeSelezionate] = useState([]);

  useEffect(() => {
    fetch(`http://localhost:8080/api/domande/${user.email}`, {
      headers: { Authorization: `Bearer ${sessionStorage.getItem("jwt")}` },
    })
      .then((res) => res.json())
      .then((data) => setDomande(data))
      .catch((error) => console.error("Errore nel recupero domande:", error));
  }, []);

  const handleCreaQuestionario = () => {
    if (!questionarioNome) {
      alert('Compila tutti i campi.');
      return;
    }
  
    const questionarioData = {
      nome: questionarioNome,
      emailUtente: user.email,
      idDomande: domandeSelezionate.map(Number), // Converti in numeri
    };
  
    console.log(questionarioData);
  
    fetch('http://localhost:8080/api/questionari/creaQuestionario', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${sessionStorage.getItem("jwt")}`,
      },
      body: JSON.stringify(questionarioData),
    })
      .then((response) => {
        console.log('Response:', response);  // Log per vedere la risposta del server
        if (!response.ok) {
          throw new Error('Errore nella creazione del questionario');
        }
  
        return response.text().then((text) => {
          if (text) {
            try {
              return JSON.parse(text);  // Prova a fare il parsing se c'è del testo
            } catch (e) {
              console.error('Errore durante il parsing JSON:', e);
              return {};  // Se non è JSON, ritorna un oggetto vuoto
            }
          }
          return {};  // Se non c'è testo, ritorna un oggetto vuoto
        });
      })
      .then(data => {
        if (data) {
          alert('Questionario creato con successo!');
          setQuestionarioNome(''); // Resetta il nome
          setDomandeSelezionate([]);
          setIsCreatingQuestionario(false); // Nascondi il modulo dopo la creazione
          setUpdateQuestionari(true);
        }
      })
      .catch(error => {
        console.error('Errore:', error);
        alert('Si è verificato un errore durante la creazione del questionario.');
      });
  };

  return (
    <div className="mt-8">
      {/* Sezione per la Creazione del Questionario */}
      {!isCreatingQuestionario ? (
        <div>
          <button
            onClick={() => setIsCreatingQuestionario(true)}
            className="bg-green-500 text-white py-2 px-6 rounded-lg hover:bg-green-700 transition"
          >
            Crea Questionario
          </button>
        </div>
      ) : (
        <div>
          <input
            type="text"
            placeholder="Nome del Questionario"
            value={questionarioNome}
            onChange={(e) => setQuestionarioNome(e.target.value)}
            className="w-full p-3 mb-4 border border-gray-300 rounded-lg"
          />
          <select
            multiple
            value={domandeSelezionate}
            onChange={(e) => {
              const selectedValues = Array.from(e.target.selectedOptions, (option) => option.value);
              setDomandeSelezionate(selectedValues);
            }}
            className="w-full p-2 border"
          >
            {domande.map((d) => (
              <option key={d.idDomanda} value={d.idDomanda}>
                {d.testoDomanda}
              </option>
            ))}
          </select>
          <button
            onClick={handleCreaQuestionario}
            className="bg-blue-500 text-white py-2 px-6 rounded-lg hover:bg-blue-700 transition"
          >
            Crea
          </button>
          <button
            onClick={() => setIsCreatingQuestionario(false)}
            className="bg-red-500 text-white py-2 px-6 rounded-lg hover:bg-red-700 transition ml-4"
          >
            Annulla
          </button>
        </div>
      )}
    </div>
  );
};

export default CreaQuestionario;
