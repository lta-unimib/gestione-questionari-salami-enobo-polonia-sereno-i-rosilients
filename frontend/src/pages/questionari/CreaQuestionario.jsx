import React, { useState, useEffect } from 'react';

const CreaQuestionario = ({ user, setUpdateQuestionari }) => {
  const [isCreatingQuestionario, setIsCreatingQuestionario] = useState(false);
  const [questionarioNome, setQuestionarioNome] = useState('');
  const [domande, setDomande] = useState([]);
  const [domandeSelezionate, setDomandeSelezionate] = useState([]);
  const [userEmail, setUserEmail] = useState(localStorage.getItem("userEmail"));

  useEffect(() => {
    fetch(`http://localhost:8080/api/domande/${userEmail}`, {
      headers: { Authorization: `Bearer ${localStorage.getItem("jwt")}` },
    })
      .then((res) => res.json())
      .then((data) => setDomande(data))
      .catch((error) => console.error("Errore nel recupero domande:", error));
  }, []);

  const handleCreaQuestionario = () => {
    if (!questionarioNome || domandeSelezionate.length === 0) {
      alert('Compila tutti i campi e seleziona almeno una domanda.');
      return;
    }

    const questionarioData = {
      nome: questionarioNome,
      emailUtente: userEmail,
      idDomande: domandeSelezionate.map(Number),
    };

    fetch('http://localhost:8080/api/questionari/creaQuestionario', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem("jwt")}`,
      },
      body: JSON.stringify(questionarioData),
    })
      .then(async (response) => {
        if (!response.ok) {
          const errorText = await response.text();
          throw new Error(`Errore nella creazione del questionario: ${errorText || response.status}`);
        }

        const text = await response.text();
        if (!text) return {};
        return JSON.parse(text);
      })
      .then(() => {
        alert('Questionario creato con successo!');
        setQuestionarioNome('');
        setDomandeSelezionate([]);
        setIsCreatingQuestionario(false);
        setUpdateQuestionari(true);
      })
      .catch(error => {
        console.error('Errore:', error);
        alert('Si è verificato un errore durante la creazione del questionario.');
      });
  };

  return (
    <div className="mt-8">
      {!isCreatingQuestionario ? (
        <button
          onClick={() => setIsCreatingQuestionario(true)}
          className="bg-green-500 text-white py-2 px-6 rounded-lg hover:bg-green-700 transition"
        >
          Crea Questionario
        </button>
      ) : (
        <div className="p-6 bg-white rounded-lg shadow-lg">
          <input
            type="text"
            placeholder="Nome del Questionario"
            value={questionarioNome}
            onChange={(e) => setQuestionarioNome(e.target.value)}
            className="w-full p-3 mb-4 border border-gray-300 rounded-lg"
          />

          {/* Sezione selezione domande migliorata */}
          <div className="mb-4">
            <h3 className="text-lg font-semibold mb-2">Seleziona le domande:</h3>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
              {domande.length > 0 ? (
                domande.map((d) => (
                  <label
                    key={d.idDomanda}
                    htmlFor={`domanda-${d.idDomanda}`}
                    className={`flex items-center p-4 border rounded-lg cursor-pointer transition ${
                      domandeSelezionate.includes(d.idDomanda.toString())
                        ? 'bg-blue-100 border-blue-500'
                        : 'hover:bg-gray-100 border-gray-300'
                    }`}
                  >
                    <input
                      type="checkbox"
                      id={`domanda-${d.idDomanda}`}
                      value={d.idDomanda}
                      checked={domandeSelezionate.includes(d.idDomanda.toString())}
                      onChange={(e) => {
                        const selectedId = e.target.value;
                        setDomandeSelezionate((prev) =>
                          prev.includes(selectedId)
                            ? prev.filter((id) => id !== selectedId)
                            : [...prev, selectedId]
                        );
                      }}
                      className="mr-3"
                    />
                    <span className="text-gray-700">{d.testoDomanda}</span>
                  </label>
                ))
              ) : (
                <p className="text-gray-500">Nessuna domanda disponibile.</p>
              )}
            </div>
          </div>

          {/* Bottoni */}
          <div className="flex justify-end mt-4">
            <button
              onClick={handleCreaQuestionario}
              className="bg-blue-500 text-white py-2 px-6 rounded-lg hover:bg-blue-700 transition mr-2"
            >
              Crea
            </button>
            <button
              onClick={() => setIsCreatingQuestionario(false)}
              className="bg-red-500 text-white py-2 px-6 rounded-lg hover:bg-red-700 transition"
            >
              Annulla
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default CreaQuestionario;