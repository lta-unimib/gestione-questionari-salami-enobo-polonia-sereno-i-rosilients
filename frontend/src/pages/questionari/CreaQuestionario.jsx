import React, { useState, useEffect } from 'react';

const CreaQuestionario = ({ user, setUpdateQuestionari }) => {
  const [isCreatingQuestionario, setIsCreatingQuestionario] = useState(false);
  const [questionarioNome, setQuestionarioNome] = useState('');
  const [domande, setDomande] = useState([]);
  const [domandeSelezionate, setDomandeSelezionate] = useState([]);
  const [userEmail, setUserEmail] = useState(localStorage.getItem("userEmail"));

  useEffect(() => {
    fetch(`http://localhost:8080/api/domande/tutteLeDomande`, {
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
          className="bg-personal-purple text-white py-2.5 px-6 rounded-lg hover:bg-[#4a1ed8] transition-all flex items-center shadow-md"
        >
          <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
          </svg>
          Crea Questionario
        </button>
      ) : (
        <div className="bg-white p-6 rounded-lg shadow-lg border-t-4 border-personal-purple">
          <h2 className="text-2xl font-semibold text-personal-purple mb-4 flex items-center">
            Nuovo Questionario
          </h2>
  
          {/* Campo Nome del Questionario */}
          <div className="relative mb-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">Nome del Questionario</label>
            <input
              type="text"
              placeholder="Es: Quiz di Matematica, Test di Storia..."
              value={questionarioNome}
              onChange={(e) => setQuestionarioNome(e.target.value)}
              className="w-full p-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-personal-purple focus:border-transparent transition-all outline-none"
            />
          </div>
  
          {/* Sezione Selezione Domande */}
          <div className="mb-4">
            <h3 className="text-lg font-semibold text-personal-purple mb-2">Seleziona le domande:</h3>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 max-h-40 overflow-y-auto">
              {domande.length > 0 ? (
                [...domande].reverse().map((d) => (
                  <label
                    key={d.idDomanda}
                    htmlFor={`domanda-${d.idDomanda}`}
                    className={`flex items-center p-4 border rounded-lg cursor-pointer transition ${
                      domandeSelezionate.includes(d.idDomanda.toString())
                        ? 'bg-purple-50 border-personal-purple'
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
                      className="mr-3 text-personal-purple focus:ring-personal-purple"
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
          <div className="flex justify-end mt-6 space-x-3">
            <button
              onClick={() => setIsCreatingQuestionario(false)}
              className="px-5 py-2.5 rounded-lg border border-gray-300 text-gray-700 hover:bg-gray-100 transition-all flex items-center"
            >
              <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              </svg>
              Annulla
            </button>
            <button
              onClick={handleCreaQuestionario}
              className="bg-personal-purple text-white px-5 py-2.5 rounded-lg hover:bg-[#4a1ed8] transition-all flex items-center"
            >
              <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
              </svg>
              Crea Questionario
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default CreaQuestionario;