import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';

const CompilaQuestionario = () => {
  const { id } = useParams(); // Ottiene l'ID del questionario dalla route
  const [questionario, setQuestionario] = useState(null);
  const [risposte, setRisposte] = useState({});

  useEffect(() => {
    const fetchQuestionario = async () => {
      try {
        const response = await fetch(`http://localhost:8080/api/questionari/${id}`);
        if (!response.ok) {
          throw new Error(`Errore nella fetch: ${response.status}`);
        }
        const data = await response.json();
        setQuestionario(data);
      } catch (error) {
        console.error("Errore nel recupero del questionario:", error);
      }
    };

    fetchQuestionario();
  }, [id]);

  const handleChange = (domandaId, valore) => {
    setRisposte((prev) => ({ ...prev, [domandaId]: valore }));
  };

  const handleSubmit = () => {
    console.log("Risposte inviate:", risposte);
    // Qui puoi fare una chiamata API per salvare le risposte
  };

  if (!questionario) {
    return <p className="text-center mt-10">Caricamento...</p>;
  }

  return (
    <div className="p-8">
      <h1 className="text-2xl font-bold">{questionario.nome}</h1>
      <p className="text-gray-700 mt-2">Compila il questionario</p>

      <form className="mt-6 space-y-6">
        {questionario.domande.map((domanda) => (
          <div key={domanda.idDomanda} className="p-4 border rounded-lg">
            <p className="font-semibold">{domanda.testoDomanda}</p>
            <div className="mt-2 space-y-2">
              {domanda.opzioni.map((opzione, index) => (
                <label key={index} className="flex items-center gap-2">
                  <input
                    type="radio"
                    name={`domanda-${domanda.idDomanda}`}
                    value={opzione}
                    onChange={(e) => handleChange(domanda.idDomanda, e.target.value)}
                  />
                  {opzione}
                </label>
              ))}
            </div>
          </div>
        ))}
        <button
          type="button"
          onClick={handleSubmit}
          className="bg-blue-500 text-white py-2 px-4 rounded-lg hover:bg-blue-600"
        >
          Invia Risposte
        </button>
      </form>
    </div>
  );
};

export default CompilaQuestionario;