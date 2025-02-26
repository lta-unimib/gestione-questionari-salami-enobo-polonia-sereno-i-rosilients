import React, { useState, useEffect } from 'react';
import { EyeIcon, MagnifyingGlassIcon, DocumentTextIcon, TrashIcon } from '@heroicons/react/24/solid';
import { useNavigate } from 'react-router-dom';
import { fetchAllQuestionari, checkCodeExists, checkIsDefinitivo, deleteCompilazione } from '../../services/homeService';

const Home = () => {
  const [query, setQuery] = useState("");
  const [results, setResults] = useState([]);
  const [allQuestionari, setAllQuestionari] = useState([]);
  const navigate = useNavigate();
  const [codiceUnivoco, setCodiceUnivoco] = useState("");
  const [idQuestionario, setIdQuestionario] = useState(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    // Carica tutti i questionari quando il componente si monta
    const fetchAllQuestionari = async () => {
      setIsLoading(true);
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
        setResults(data); // Inizializza i risultati con tutti i questionari
      } catch (error) {
        console.error("Errore nella fetch:", error);
      } finally {
        setIsLoading(false);
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
      // Già gestito dal filtro in tempo reale
    }
  };

  const continuaCompilazioneNonRegistrato = (idQuestionario, idCompilazione) => {
    navigate(`/questionari/compilaQuestionario/${idQuestionario}?idCompilazione=${idCompilazione}`);
  };

  const visualizzaCompilazioneNonRegistrato = (idQuestionario, idCompilazione) => {
    navigate(`/questionari/visualizzaQuestionarioCompilato/${idCompilazione}/${idQuestionario}`);
  };

  const deleteCompilazioneNonRegistrato = async (idCompilazione) => {
    try {
      const response = await fetch(`http://localhost:8080/api/questionariCompilati/deleteQuestionarioCompilato/${idCompilazione}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json'
        }
      });

      if (!response.ok) {
        throw new Error("Errore nella cancellazione");
      }
      
      alert("Questionario compilato eliminato con successo");
      navigate("/");
    } catch (error) {
      alert("Errore nella cancellazione del questionario compilato, assicurati di aver inserito un codice corretto.");
      console.error(error);
    }
  };

  const handleCodeExists = async () => {
    if (!codiceUnivoco) {
      alert("Inserisci un codice univoco NUMERICO valido.");
      return;
    }

    const codiceInt = Number(codiceUnivoco);
    if (isNaN(codiceInt)) {
      alert("Il codice univoco deve essere un numero.");
      return;
    }

    try {
      const response = await fetch(`http://localhost:8080/api/questionariCompilati/utenteNonRegistrato/${codiceInt}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        }
      });

      if (!response.ok) {
        alert("Codice univoco non valido, assicurati che il codice sia corretto.");
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
      if (isDefinitivo) {
        visualizzaCompilazioneNonRegistrato(data.idQuestionario, codiceInt);
      } else {
        continuaCompilazioneNonRegistrato(data.idQuestionario, codiceInt);
      }     
    } catch (error) {
      console.error("Errore nella fetch:", error);
      alert("Si è verificato un errore durante l'operazione.");
    }
  };

  const handleCodeExistsDel = async () => {
    if (!codiceUnivoco) {
      alert("Inserisci un codice univoco NUMERICO valido.");
      return;
    }

    const codiceInt = Number(codiceUnivoco);
    if (isNaN(codiceInt)) {
      alert("Il codice univoco deve essere un numero.");
      return;
    }

    try {
      const response = await fetch(`http://localhost:8080/api/questionariCompilati/utenteNonRegistrato/${codiceInt}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        }
      });

      if (!response.ok) {
        alert("Codice univoco non valido, assicurati che il codice sia corretto.");
        return;
      }
      
      deleteCompilazioneNonRegistrato(codiceInt);
    } catch (error) {
      console.error("Errore nella fetch:", error);
      alert("Si è verificato un errore durante l'operazione.");
    }
  };

  return (
    <div className="min-h-screen py-8 px-4 sm:px-6 lg:px-8">
      {/* Header con logo e titolo */}
      <div className="max-w-6xl mx-auto">
        <div className="text-center mb-16">
          <h1 className="text-5xl font-extrabold text-personal-purple tracking-tight">
            <span className="inline-block ">Web Surveys</span>
          </h1>
          <p className="mt-3 text-xl text-gray-500">Trova e compila questionari online</p>
        </div>

        {/* Sezione ricerca */}
        <div className="max-w-3xl mx-auto mb-16">
          <div className="relative">
            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <MagnifyingGlassIcon className="h-5 w-5 text-gray-400" />
            </div>
            <input
              type="text"
              placeholder="Cerca un questionario per nome..."
              className="block w-full pl-10 pr-3 py-4 border border-gray-300 rounded-xl bg-personal-purple bg-opacity-10 text-gray-900 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-personal-purple focus:border-personal-purple text-lg transition-shadow duration-200"
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              onKeyDown={handleKeyDown}
            />
          </div>
        </div>

        {/* Risultati della ricerca */}
        <div className="max-w-6xl mx-auto mb-16">
          <h2 className="text-2xl font-bold text-gray-800 mb-6">Questionari Disponibili</h2>
          
          {isLoading ? (
            <div className="flex justify-center py-12">
              <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-personal-purple"></div>
            </div>
          ) : results.length > 0 ? (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 max-h-48 overflow-y-auto">
              {results.map((questionario) => {
                const nomeCreatore = questionario.emailUtente ? questionario.emailUtente.split("@")[0] : "Sconosciuto";
                
                return (
                  <div 
                    key={questionario.idQuestionario} 
                    className="bg-white rounded-xl shadow-md overflow-hidden hover:shadow-lg transition-shadow duration-300"
                  >
                    <div className="p-6">
                      <div className="flex items-start justify-between">
                        <div>
                          <h3 className="text-xl font-semibold text-gray-900 mb-1">{questionario.nome}</h3>
                          <p className="text-sm text-gray-600 flex items-center">
                            <span className="inline-block h-2 w-2 rounded-full bg-personal-purple mr-2"></span>
                            Creato da: {nomeCreatore}
                          </p>
                        </div>
                        <div 
                          onClick={() => navigate(`/questionari/${questionario.idQuestionario}`)}
                          className="p-2 rounded-full bg-gray-100 hover:bg-gray-200 cursor-pointer transition-colors duration-200"
                        >
                          <EyeIcon className="w-5 h-5 text-gray-700" />
                        </div>
                      </div>
                      
                      <div className="mt-6">
                        <button
                          onClick={() => navigate(`/questionari/compilaQuestionario/${questionario.idQuestionario}`)}
                          className="w-full bg-white text-personal-purple border-2 border-personal-purple py-2 px-4 rounded-lg font-medium hover:bg-personal-purple hover:text-white transition-colors duration-300 flex items-center justify-center"
                        >
                          <DocumentTextIcon className="w-5 h-5 mr-2" />
                          Compila Questionario
                        </button>
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          ) : (
            <div className="text-center py-12 bg-white rounded-xl shadow">
              <DocumentTextIcon className="mx-auto h-12 w-12 text-gray-400" />
              <h3 className="mt-2 text-lg font-medium text-gray-900">Nessun questionario trovato</h3>
              <p className="mt-1 text-sm text-gray-500">Prova a modificare i termini di ricerca.</p>
            </div>
          )}
        </div>

        {/* Gestione questionario compilato */}
        <div className="max-w-4xl mx-auto bg-white p-8 rounded-xl shadow">
          <h2 className="text-2xl font-bold text-gray-800 mb-6">Gestione questionari compilati</h2>
          <p className="text-gray-600 mb-6">Inserisci il codice univoco del tuo questionario per riprendere la compilazione o visualizzarlo.</p>
          
          <div className="flex flex-col sm:flex-row gap-4">
            <div className="flex-grow relative">
              <input
                type="text"
                placeholder="Inserisci il codice numerico"
                className="block w-full pl-4 pr-10 py-3 border border-gray-300 rounded-lg bg-gray-50 text-gray-900 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-personal-purple focus:border-personal-purple transition-all duration-200"
                value={codiceUnivoco}
                onChange={(e) => setCodiceUnivoco(e.target.value)}
              />
            </div>
            <button 
              className="py-3 px-6 bg-personal-purple text-white font-medium rounded-lg shadow hover:bg-purple-800 focus:outline-none focus:ring-2 focus:ring-purple-500 focus:ring-opacity-50 transition-colors duration-200"
              onClick={handleCodeExists}
            >
              Vai al Questionario
            </button>
            <button 
              className="py-3 px-6 bg-[#f03841] text-white font-medium rounded-lg shadow hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-opacity-50 transition-colors duration-200 flex items-center justify-center"
              onClick={handleCodeExistsDel}
            >
              <TrashIcon className="w-5 h-5 mr-2" />
              Elimina
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Home;