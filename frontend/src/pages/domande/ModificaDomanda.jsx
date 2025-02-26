// src/components/Domande/EditModal.jsx
import React, { useState, useEffect } from 'react';
import ReactModal from 'react-modal';

const ModificaDomanda = ({ isOpen, onClose, domanda, onSave, userEmail }) => {
  const [editedArgomento, setEditedArgomento] = useState('');
  const [editedTesto, setEditedTesto] = useState('');
  const [removeImage, setRemoveImage] = useState(false);
  const [editedOpzioni, setEditedOpzioni] = useState([]);
  const [editedImage, setEditedImage] = useState(null);

  // Initialize form values when domanda changes
  useEffect(() => {
    if (domanda) {
      setEditedArgomento(domanda.argomento);
      setEditedTesto(domanda.testoDomanda);
      setEditedOpzioni(domanda.opzioni || [""]);
      setRemoveImage(false);
    }
  }, [domanda]);

  const handleAddOpzione = () => {
    setEditedOpzioni([...editedOpzioni, ""]);
  };
  
  const handleRemoveOpzione = (index) => {
    const newOpzioni = [...editedOpzioni];
    newOpzioni.splice(index, 1);
    setEditedOpzioni(newOpzioni);
  };

  const handleSubmit = () => {
    const formData = new FormData();
    formData.append('argomento', editedArgomento);
    formData.append('testoDomanda', editedTesto);
    formData.append('emailUtente', userEmail);

    if (editedImage) {
      formData.append('imageFile', editedImage);
    }
    
    if (editedOpzioni && editedOpzioni.length > 0) {
      formData.append('opzioni', JSON.stringify(editedOpzioni));
    }
    
    if (removeImage) {
      formData.append('removeImage', 'true');  
    }

    onSave(formData);
  };

  return (
    <ReactModal
      isOpen={isOpen}
      onRequestClose={onClose}
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
          
          {domanda?.imagePath && (
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
          
          {/* Opzioni section */}
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
        </div>
        
        <div className="flex justify-end mt-6 space-x-3">
          <button 
            onClick={onClose} 
            className="px-5 py-2.5 rounded-lg border border-gray-300 text-gray-700 hover:bg-gray-100 transition-all"
          >
            Annulla
          </button>
          <button 
            onClick={handleSubmit} 
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
  );
};

export default ModificaDomanda;