import React, { useState, useEffect } from 'react';
import ReactModal from 'react-modal'; 
import { TrashIcon, PencilSquareIcon } from '@heroicons/react/24/solid';

import CreaDomanda from './CreaDomanda';

const Domande = ({ user }) => {
  const [domande, setDomande] = useState([]);
  const [updateDomande, setUpdateDomande] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [domandaIdToDelete, setDomandaIdToDelete] = useState(null);
  const [domandaToEdit, setDomandaToEdit] = useState(null);
  const [editedArgomento, setEditedArgomento] = useState('');
  const [editedTesto, setEditedTesto] = useState('');
  const [removeImage, setRemoveImage] = useState(false);
  const [editedOpzioni, setEditedOpzioni] = useState([]);
  const [editedImage, setEditedImage] = useState(null);
  const [filtro, setFiltro] = useState("tue");
  const [searchTerm, setSearchTerm] = useState("");
  const token = localStorage.getItem("jwt");
  const [images, setImages] = useState({}); 
  const [userEmail, setUserEmail] = useState(localStorage.getItem("userEmail"));


  ReactModal.setAppElement('#root');

  useEffect(() => {
    
  
    console.log(removeImage)
  }, [removeImage])
  

  const fetchImage = async (fileName) => {
    // console.log(fileName);
    try {
      const response = await fetch(`http://localhost:8080${fileName}`, {
        method: 'GET',
        headers: {
          "Authorization": `Bearer ${token}`
        }
      });
  
      if (!response.ok) {
        throw new Error("Errore nel caricamento dell'immagine");
      }
  
      const contentType = response.headers.get("Content-Type");
      // console.log("Tipo di contenuto dell'immagine:", contentType);
  
      if (!contentType || !contentType.startsWith("image/")) {
        throw new Error("Il file restituito non è un'immagine");
      }
  
      const blob = await response.blob();
      return URL.createObjectURL(blob);
    } catch (error) {
      console.error("Errore nel caricamento dell'immagine:", error);
      return null;
    }
  };

  // get di tutte le domande personali e globali
  useEffect(() => {
    if (!user || !userEmail) return;

    let url = filtro === "tue" 
      ? `http://localhost:8080/api/domande/${userEmail}`
      : `http://localhost:8080/api/domande/tutteLeDomande`;

    fetch(url, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    })
    .then(response => {
      if (!response.ok) {
        throw new Error('Errore nel recupero delle domande');
      }
      return response.json();
    })
    .then(data => {
      console.log("Risposta dal backend:", data);
      setDomande(data);
      

      // Carica le immagini per ogni domanda
      
      const loadImages = async () => {
        const imageEntries = await Promise.all(
          data.map(async (d) => {
            if (d.imagePath) {
              const imageUrl = await fetchImage(d.imagePath);
              return [d.idDomanda, imageUrl];
            }
            return null;
          })
        );

        // Filtra i valori nulli e crea l'oggetto mappa
        const imageMap = Object.fromEntries(imageEntries.filter(Boolean));
        setImages(imageMap);
      };

      loadImages();
    })
    .catch(error => {
      console.error('Errore:', error);
    });

    setUpdateDomande(false);
  }, [user, filtro, updateDomande]);

  // gesione Popup per eliminare la domanda
  const openDeleteModal = (id) => {
    setDomandaIdToDelete(id);
    setIsDeleteModalOpen(true);
  };

  const closeDeleteModal = () => {
    setIsDeleteModalOpen(false);
    setDomandaIdToDelete(null);
  };

  // richiesta per eliminare la domanda
  const handleDeleteDomanda = () => {
    fetch(`http://localhost:8080/api/domande/deleteDomanda/${domandaIdToDelete}`, {
      method: 'DELETE',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    })
    .then(response => {
      if (!response.ok) {
        throw new Error('Errore nell\'eliminazione della domanda');
      }
      setDomande(prevState => prevState.filter(q => q.idDomanda !== domandaIdToDelete));
      setUpdateDomande(true);
      closeDeleteModal();
    })
    .catch(error => {
      console.error('Errore:', error);
    });
  };

  // gestione Popup per modificare la domanda
  const openEditModal = (domanda) => {
    setDomandaToEdit(domanda);
    setEditedArgomento(domanda.argomento);
    setEditedTesto(domanda.testoDomanda);
    setEditedOpzioni(domanda.opzioni || [""]); 
    setRemoveImage(domanda.removeImage)
    setIsEditModalOpen(true);
  };

  const closeEditModal = () => {
    setIsEditModalOpen(false);
    setDomandaToEdit(null);
  };

  const handleAddOpzione = () => {
    setEditedOpzioni([...editedOpzioni, ""]); // Aggiungi una nuova opzione vuota
  };
  
  const handleRemoveOpzione = (index) => {
    const newOpzioni = [...editedOpzioni];
    newOpzioni.splice(index, 1); // Rimuovi l'opzione alla posizione specificata
    setEditedOpzioni(newOpzioni);
  };

  const handleEditDomanda = () => {
    if (!domandaToEdit) return;

    
    const formData = new FormData();
    formData.append('argomento', editedArgomento);
    formData.append('testoDomanda', editedTesto);
    formData.append('emailUtente', user.email);

    if (editedImage) {
        formData.append('imageFile', editedImage);
    }
    if (editedOpzioni && editedOpzioni.length > 0) {
      formData.append('opzioni', JSON.stringify(editedOpzioni));
    }
    
    if (removeImage) {
      formData.append('removeImage', 'true');  
    }
    for (let pair of formData.entries()) {
      console.log(pair[0] + ':', pair[1]);
    }
  
    fetch(`http://localhost:8080/api/domande/updateDomanda/${domandaToEdit.idDomanda}`, {
      method: 'PUT',
      headers: {
        'Authorization': `Bearer ${token}`,
      },
      // body: JSON.stringify(updatedDomanda),
      body: formData
    })
    .then(response => {
      if (!response.ok) {
        throw new Error('Errore nell\'aggiornamento della domanda');
      }
      setDomande(prevState => prevState.map(d => d.idDomanda === domandaToEdit.idDomanda ? domandaToEdit : d));
      setUpdateDomande(true);
      setEditedImage(false);
      setRemoveImage(false);
      closeEditModal();
      alert("Domanda modificata con successo! ✅");
    })
    .catch(error => {
      console.error('Errore:', error);
      alert("Si è verificato un errore durante la modifica. ❌");
    });
  };

  const domandeFiltrate = domande.filter(d => 
    d.argomento.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className='mx-24'>
      <h1 className="text-4xl">Domande</h1>
      <div className="flex justify-between items-center mt-4">
        <select value={filtro} onChange={(e) => setFiltro(e.target.value)} className="border rounded-lg p-2">
          <option value="tue">Le tue domande</option>
          <option value="tutte">Tutte le domande</option>
        </select>
        <input 
          type="text"
          placeholder="Cerca per argomento..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="border rounded-lg p-2"
        />
      </div>

      <CreaDomanda user={user} setUpdateDomande={setUpdateDomande} />
      
      {domandeFiltrate.length > 0 ? (
        <ul className='mt-12'>
          {[...domandeFiltrate].reverse().map(d => (
            <li key={d.idDomanda} className="border p-4 my-2 rounded-lg shadow-lg flex justify-between">
              <div>
                <h3 className="text-xl font-semibold">{d.argomento}</h3>
                <p>{d.testoDomanda}</p>
                {d.imagePath && images[d.idDomanda] && (
                  <img src={images[d.idDomanda]} alt="Domanda" className="h-96 w-96" />
                )}
                {d.opzioni && d.opzioni.length > 0 && (
                  <ul className="mt-2">
                    {d.opzioni.map((opzione, index) => (
                      <li key={index} className="ml-4 list-disc">{opzione}</li>
                    ))}
                  </ul>
                )}
              </div>
              <div className="edit flex gap-4">
                <button 
                  className={`text-gray-500 hover:text-gray-700 ${d.emailUtente !== userEmail ? "opacity-50 cursor-not-allowed" : ""}`} 
                  onClick={() => openEditModal(d)}
                  disabled={d.emailUtente !== userEmail}
                >
                  <PencilSquareIcon className="h-6 w-6" />
                </button>
                
                <button 
                  className={`text-red-600 hover:text-red-800 ${d.emailUtente !== userEmail ? "opacity-50 cursor-not-allowed" : ""}`} 
                  onClick={() => openDeleteModal(d.idDomanda)}
                  disabled={d.emailUtente !== userEmail}
                >
                  <TrashIcon className="h-6 w-6" />
                </button>
              </div>
            </li>
          ))}
        </ul>
      ) : (
        <p className="text-gray-500 mt-4">Nessuna domanda trovata.</p>
      )}


      {/* Modal per eliminazione domanda */}
      <ReactModal
        isOpen={isDeleteModalOpen}
        onRequestClose={closeDeleteModal}
        contentLabel="Conferma eliminazione"
        className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50"
        overlayClassName="modal-overlay"
      >
        <div className="bg-white p-8 rounded-lg w-96 text-center">
          <h2 className="text-2xl font-semibold text-gray-800">Sei sicuro di voler eliminare questa domanda?</h2>
          <div className="mt-4">
            <button 
              onClick={handleDeleteDomanda} 
              className="bg-red-500 text-white px-6 py-2 rounded-lg mr-4 hover:bg-red-600 transition">
              Elimina
            </button>
            <button 
              onClick={closeDeleteModal} 
              className="bg-gray-500 text-white px-6 py-2 rounded-lg hover:bg-gray-600 transition">
              Annulla
            </button>
          </div>
        </div>
      </ReactModal>

      {/* Modal per modifica domanda */}
      <ReactModal
        isOpen={isEditModalOpen}
        onRequestClose={closeEditModal}
        contentLabel="Modifica domanda"
        className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50"
        overlayClassName="modal-overlay"
      >
        <div className="bg-white p-8 rounded-lg w-full max-w-md shadow-xl border-t-4 border-[#3603CD]">
          <h2 className="text-2xl font-semibold text-[#3603CD] mb-4 flex items-center">
            <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
            </svg>
            Modifica la tua domanda
          </h2>
          
          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Argomento</label>
              <input 
                type="text" 
                className="border rounded-lg p-3 w-full focus:ring-2 focus:ring-[#3603CD] focus:border-transparent transition-all outline-none" 
                value={editedArgomento} 
                onChange={(e) => setEditedArgomento(e.target.value)} 
                placeholder="Inserisci l'argomento"
              />
            </div>
            
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Testo della domanda</label>
              <textarea 
                className="border rounded-lg p-3 w-full min-h-24 focus:ring-2 focus:ring-[#3603CD] focus:border-transparent transition-all outline-none" 
                value={editedTesto} 
                onChange={(e) => setEditedTesto(e.target.value)} 
                placeholder="Scrivi il testo della tua domanda qui..."
              />
            </div>
            
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">Immagine</label>
              <input
                type="file"
                accept="image/*"
                onChange={(e) => setEditedImage(e.target.files[0])}
                className="border rounded-lg p-2 w-full file:mr-4 file:py-2 file:px-4 file:rounded-lg file:border-0 file:text-sm file:bg-[#3603CD] file:text-white hover:file:bg-[#4a1ed8] file:transition-all"
              />
            </div>
            
            {domandaToEdit?.imagePath && (
              <div className="flex items-center gap-2 bg-gray-50 p-2 rounded-lg">
                <input
                  type="checkbox"
                  id="removeImage"
                  checked={removeImage}
                  onChange={() => setRemoveImage(!removeImage)}
                  className="rounded text-[#3603CD] focus:ring-[#3603CD] h-4 w-4"
                />
                <label htmlFor="removeImage" className="text-sm text-gray-700">Rimuovi immagine esistente</label>
              </div>
            )}
            
            {/* Se ci sono opzioni, permetti di modificarle */}
            {editedOpzioni.length >= 0 && (
              <div className="bg-gray-50 p-4 rounded-lg mt-4">
                <h3 className="text-lg font-semibold text-[#3603CD] mb-2 flex items-center">
                  <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                  </svg>
                  Opzioni
                </h3>
                
                {editedOpzioni.map((opzione, index) => (
                  <div key={index} className="flex items-center gap-2 mb-2">
                    <input 
                      type="text"
                      className="border rounded-lg p-2 w-full focus:ring-2 focus:ring-[#3603CD] focus:border-transparent transition-all outline-none"
                      value={opzione}
                      onChange={(e) => {
                        const newOpzioni = [...editedOpzioni];
                        newOpzioni[index] = e.target.value;
                        setEditedOpzioni(newOpzioni);
                      }}
                      placeholder={`Opzione ${index + 1}`}
                    />
                    <button 
                      onClick={() => handleRemoveOpzione(index)}
                      className="bg-red-100 text-red-600 p-2 rounded-lg hover:bg-red-200 transition-all"
                      aria-label="Rimuovi opzione"
                    >
                      <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                      </svg>
                    </button>
                  </div>
                ))}
                
                <button 
                  onClick={handleAddOpzione} 
                  className="bg-[#3603CD] bg-opacity-20 text-[#3603CD] px-4 py-2 rounded-lg mt-2 hover:bg-opacity-30 transition-all flex items-center"
                >
                  <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
                  </svg>
                  Aggiungi opzione
                </button>
              </div>
            )}
          </div>
          
          <div className="flex justify-end mt-6 space-x-3">
            <button 
              onClick={closeEditModal} 
              className="px-5 py-2.5 rounded-lg border border-gray-300 text-gray-700 hover:bg-gray-100 transition-all"
            >
              Annulla
            </button>
            <button 
              onClick={handleEditDomanda} 
              className="bg-[#3603CD] text-white px-5 py-2.5 rounded-lg hover:bg-[#4a1ed8] transition-all flex items-center"
            >
              <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
              </svg>
              Salva modifiche
            </button>
          </div>
        </div>
      </ReactModal>
    </div>
  );
};

export default Domande;
