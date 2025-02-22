import React, { useState, useEffect } from 'react';
import { EyeIcon } from '@heroicons/react/24/solid';
import { useNavigate } from 'react-router-dom';

const Home = () => {
  const [query, setQuery] = useState("");
  const [results, setResults] = useState([]);
  const [allQuestionari, setAllQuestionari] = useState([]); // Stato per tutti i questionari
  const navigate = useNavigate();
  const [codiceUnivoco, setCodiceUnivoco] = useState(null);
  const [idQuestionario, setIdQuestionario] = useState(null);

  useEffect(() => {
    // Carica tutti i questionari quando il componente si monta
    const fetchAllQuestionari = async () => {
      try {
        const response = await fetch("http://localhost:8080/api/questionari/tuttiIQuestionari", {
          method: "GET",
          headers: {
            "Content-Type": "application/json"
          }
        });

        if (!response.ok) {
          throw new Error("Errore nel recupero dei questionari");
        }

        const data = await response.json();
        setAllQuestionari(data); // Salva tutti i questionari
      } catch (error) {
        console.error("Errore nella fetch:", error);
      }
    };

    fetchAllQuestionari();
  }, []); // La fetch viene eseguita solo la prima volta

  useEffect(() => {
    if (!query.trim()) {
      setResults(allQuestionari); 
    } else {
      // Filtro i questionari in base al nome
      const filteredResults = allQuestionari.filter(questionario =>
        questionario.nome.toLowerCase().includes(query.toLowerCase())
      );
      setResults(filteredResults);
    }
  }, [query, allQuestionari]); // Effettua il filtro mentre scrivi nella barra

  const handleKeyDown = (event) => {
    if (event.key === 'Enter') {
    }
  };


  const continuaCompilazioneNonRegistrato = (idQuestionario, idCompilazione) => {
    navigate(`/questionari/compilaQuestionario/${idQuestionario}?idCompilazione=${idCompilazione}`);
  };

  const visualizzaCompilazioneNonRegistrato = (idQuestionario, idCompilazione) => {
    navigate(`/questionari/visualizzaQuestionarioCompilato/${idCompilazione}`);
  };

  const deleteCompilazioneNonRegistrato = async (idCompilazione) => {
    const response = await fetch(`http://localhost:8080/api/questionariCompilati/deleteQuestionarioCompilato/${idCompilazione}`, {
      method : 'DELETE',
      headers: {
        'Content-Type' : 'application/json'
      }
    });

    if(!response.ok) {
      alert("Errore nella cancellazione del questionario compilato, assicurati di aver inserito un codice corretto.");
      return;
    } else {
      alert("Questionario compilato eliminato con successo");
      navigate("/");
    }
  };

  const handleCodeExists = async () => {
    if(codiceUnivoco === null || codiceUnivoco === "") {
      alert("Inserisci un codice univoco NUMERICO valido.");
      return;
    }

    const codiceInt = Number(codiceUnivoco);
    if(isNaN(codiceInt)) {
      alert("Il codice univoco deve essere un numero.");
      return;
    }

    try {
      const response = await fetch(`http://localhost:8080/api/questionariCompilati/utenteNonRegistrato/${codiceInt}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        }
      })

      if (!response.ok) {
        alert("Codice univoco non valido, assicurati che il codice sia corretto.");
        navigate("/");
        return;
      }

      const data = await response.json();   
      setIdQuestionario(data.idQuestionario);

      const checkDefinitivoResponse = await fetch(`http://localhost:8080/api/questionariCompilati/checkIsDefinitivo/${codiceInt}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        }
      });

      if (!checkDefinitivoResponse.ok) {
        alert("Errore nei controlli del parametro definitivo.");
        return;
      }

      const isDefinitivo = await checkDefinitivoResponse.json();
      if(isDefinitivo) {

        //TODO: Visualizza questionario compilato

      } else {
        continuaCompilazioneNonRegistrato(data.idQuestionario, codiceInt);
      }     
    }
    catch(error) {
      console.error("Errore nella fetch:", error);
      alert("Si è verificato un errore durante l'operazione handleCodeExists.");
    }
  }

  const handleCodeExistsDel = async () => {
    if(codiceUnivoco === null || codiceUnivoco === "") {
      alert("Inserisci un codice univoco NUMERICO valido.");
      return;
    }

    const codiceInt = Number(codiceUnivoco);
    if(isNaN(codiceInt)) {
      alert("Il codice univoco deve essere un numero.");
      return;
    }

    try {
      const response = await fetch(`http://localhost:8080/api/questionariCompilati/utenteNonRegistrato/${codiceInt}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        }
      })

      if (!response.ok) {
        alert("Codice univoco non valido, assicurati che il codice sia corretto.");
        navigate("/");
        return;
      }
      
      deleteCompilazioneNonRegistrato(codiceInt);
        
    }
    catch(error) {
      console.error("Errore nella fetch:", error);
      alert("Si è verificato un errore durante l'operazione handleCodeExistsDel.");
    }
  }

  return (
    <div className="p-4">
      {/* Sezione ricerca */}
      <div className="flex justify-center mt-16">
        <div className="">
          <h1 className="text-personal-purple text-6xl text-center">Web Surveys</h1>
          <div className="mt-8">
            <input
              type="text"
              placeholder="Cerca un questionario"
              className="bg-personal-purple bg-opacity-20 text-black py-2 px-52 rounded-lg"
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              onKeyDown={handleKeyDown}
            />
          </div>
        </div>
      </div>

      {/* Risultati della ricerca */}
      <div className="mt-16 flex justify-center">
        <div className="">
          {results.length > 0 ? (
            <ul className=' grid grid-cols-3 grid-rows-2 gap-16'>
              {results.map((questionario) => {
                const nomeCreatore = questionario.emailUtente ? questionario.emailUtente.split("@")[0] : "Sconosciuto";

                return (
                  <li key={questionario.idQuestionario} className="bg-gray-100 p-4 rounded-lg mb-2 w-96">
                    <div className="flex items-center justify-between">
                      <div>
                        <p className="font-bold">{questionario.nome}</p>
                        <p className="text-sm text-gray-600">Creato da: {nomeCreatore}</p>
                      </div>

                      <div className="flex items-center gap-4">
                        {/* Navigazione per visualizzazione questionario */}
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
          ) : (
            <p className="text-gray-500">Nessun questionario trovato.</p>
          )}
        </div>
      </div>

      {/* Gestione questionario compilato */}
      <div className="mx-16 mt-16">
        <h2 className="text-2xl">Gestione questionari compilati</h2>
        <div className="flex mt-5">
          <input
            type="text"
            placeholder="Inserisci un codice"
            className="bg-personal-purple bg-opacity-20 text-black px-16"
            value={codiceUnivoco ?? ""}
            onChange={(e) => setCodiceUnivoco(e.target.value ? Number(e.target.value) : null)}
          />
          <button 
            className="bg-personal-purple text-white py-2 px-4"
            onClick={handleCodeExists}
          >
            Vai al Questionario
          </button>
          <button 
            className="bg-[#5C0005] text-white py-2 px-4"
            onClick={handleCodeExistsDel}
          >
            Delete
          </button>
        </div>
      </div>
    </div>
  );
};

export default Home;
