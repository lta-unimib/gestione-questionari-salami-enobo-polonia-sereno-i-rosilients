import React, { useState } from 'react';
import { EyeIcon } from '@heroicons/react/24/solid';
import { useNavigate } from 'react-router-dom';

const Home = () => {
  const [query, setQuery] = useState("");
  const [results, setResults] = useState([]);
  const [hasSearched, setHasSearched] = useState(false);
  const navigate = useNavigate();

  const handleSearch = async () => {
    if (!query.trim()) return;

    try {
      const response = await fetch(`http://localhost:8080/api/questionari/search?nome=${query}`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json"
        }
      });

      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }

      const data = await response.json();
      setResults(data);
      setHasSearched(true); // Mostra il pulsante Reset dopo la ricerca
    } catch (error) {
      console.error("Errore nella fetch:", error);
    }
  };

  const handleReset = () => {
    setQuery("");  // Reset del campo di ricerca
    setResults([]); // Cancella i risultati della ricerca
    setHasSearched(false); // Nasconde il pulsante Reset
  };

  const handleKeyDown = (event) => {
    if (event.key === 'Enter') {
      handleSearch();
    }
  };

  return (
    <div className="p-4">
      {/* Sezione ricerca */}
      <div className="flex justify-center mt-16">
        <div className="flex gap-4">
          <input
            type="text"
            placeholder="Cerca un questionario"
            className="bg-personal-purple bg-opacity-20 text-black py-2 px-52 rounded-lg"
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            onKeyDown={handleKeyDown}
          />
          <button
            onClick={handleSearch}
            className="bg-personal-purple text-white py-2 px-4 rounded-lg"
          >
            Cerca
          </button>
          {hasSearched && (
            <button
              onClick={handleReset}
              className="bg-gray-300 text-black py-2 px-4 rounded-lg"
            >
              Reset
            </button>
          )}
        </div>
      </div>

      {/* Risultati della ricerca */}
      <div className="mt-16 flex justify-center">
        <div className="w-full max-w-3xl">
          {results.length > 0 && (
            <ul>
              {results.map((questionario) => {
                const nomeCreatore = questionario.emailUtente ? questionario.emailUtente.split("@")[0] : "Sconosciuto";

                return (
                  <li key={questionario.idQuestionario} className="bg-gray-100 p-4 rounded-lg mb-2">
                    <div className="flex items-center justify-between">
                      <div>
                        <p className="font-bold">{questionario.nome}</p>
                        <p className="text-sm text-gray-600">Creato da: {nomeCreatore}</p>
                      </div>

                      <div className="flex items-center gap-4">
                        <EyeIcon
                          className="w-5 h-5 text-gray-700 cursor-pointer hover:text-gray-800"
                          onClick={() => navigate(`/questionari/${questionario.idQuestionario}`)}
                        />
                        <button
                          onClick={() => navigate(`/questionari/compilaQuestionario/${questionario.idQuestionario}`)}
                          className="bg-white text-personal-purple border-2 border-personal-purple py-1 px-3 rounded-lg hover:bg-personal-purple hover:text-white transition duration-200"
                        >
                          Compila
                        </button>
                      </div>
                    </div>
                  </li>
                );
              })}
            </ul>
          )}
        </div>
      </div>

      {/* Gestione questionario compilato */}
      <div className="mx-16 mt-72">
        <h2 className="text-2xl">Gestione questionari compilati</h2>
        <div className="flex mt-5">
          <input
            type="text"
            placeholder="Inserisci un codice univoco"
            className="bg-personal-purple bg-opacity-20 text-black px-16"
          />
          <button className="bg-personal-purple text-white py-2 px-4">Invia</button>
        </div>
      </div>
    </div>
  );
};

export default Home;