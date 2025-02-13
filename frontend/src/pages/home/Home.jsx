import React, { useState, useEffect } from 'react';

const Home = () => {
  const [query, setQuery] = useState("");
  const [results, setResults] = useState([]);

  const handleSearch = async () => {
    if (!query.trim()) return; // Evita chiamate vuote

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
    } catch (error) {
      console.error("Errore nella fetch:", error);
    }
  };

  return (
    <div className="p-4">
      {/* Sezione ricerca */}
      <div className="flex justify-center mt-16">
        <div className="flex gap-4">
          <input
            type="text"
            placeholder='Cerca un questionario'
            className='bg-personal-purple bg-opacity-20 text-black py-2 px-52 rounded-lg'
            value={query}
            onChange={(e) => setQuery(e.target.value)}
          />
          <button
            onClick={handleSearch}
            className='bg-personal-purple text-white py-2 px-4 rounded-lg'
          >
            Cerca
          </button>
        </div>
      </div>

      {/* Risultati della ricerca */}
      <div className="mt-16 flex justify-center">
        <div className="w-full max-w-3xl">
          <ul>
            {results.map((questionario) => (
              <li key={questionario.idQuestionario} className="bg-gray-100 p-4 rounded-lg mb-2">
                {questionario.nome}
              </li>
            ))}
          </ul>
        </div>
      </div>

      {/* Gestione questionario compilato */}
      <div className='mx-16 mt-72'>
        <h2 className="text-2xl ">Gestione questionari compilati</h2>
        <div className="flex mt-5">
          <input
            type="text"
            placeholder='Inserisci un codice univoco'
            className='bg-personal-purple bg-opacity-20 text-black px-16'
          />
          <button className='bg-personal-purple text-white py-2 px-4'>Invia</button>
        </div>
      </div>
    </div>
  );
};

export default Home;
