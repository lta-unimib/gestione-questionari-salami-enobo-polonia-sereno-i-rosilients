// src/components/Domande/Domande.jsx
import React, { useState, useEffect } from 'react';
import CardDomanda from './CardDomanda';
import CercaDomanda from './CercaDomanda';
import EliminaDomanda from './EliminaDomanda';
import ModificaDomanda from './ModificaDomanda';
import CreaDomanda from './CreaDomanda';
import { fetchDomande, deleteDomanda, updateDomanda, fetchImage } from '../../services/domandaService';

const Domande = ({ user }) => {
  const [domande, setDomande] = useState([]);
  const [updateDomande, setUpdateDomande] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);
  const [domandaIdToDelete, setDomandaIdToDelete] = useState(null);
  const [domandaToEdit, setDomandaToEdit] = useState(null);
  const [filtro, setFiltro] = useState("tue");
  const [searchTerm, setSearchTerm] = useState("");
  const [images, setImages] = useState({});
  const token = localStorage.getItem("jwt");
  const userEmail = localStorage.getItem("userEmail");

  // Fetch questions based on filter
  useEffect(() => {
    if (!user || !userEmail) return;

    const loadData = async () => {
      try {
        const data = await fetchDomande(filtro, userEmail, token);
        setDomande(data);
        
        // Load images for each question
        const imageEntries = await Promise.all(
          data.map(async (d) => {
            if (d.imagePath) {
              const imageUrl = await fetchImage(d.imagePath, token);
              return [d.idDomanda, imageUrl];
            }
            return null;
          })
        );

        // Filter null values and create map object
        const imageMap = Object.fromEntries(imageEntries.filter(Boolean));
        setImages(imageMap);
      } catch (error) {
        console.error('Error fetching data:', error);
      }
    };

    loadData();
    setUpdateDomande(false);
  }, [user, filtro, updateDomande, userEmail, token]);

  // Filter questions by search term
  const domandeFiltrate = domande.filter(d => 
    d.argomento.toLowerCase().includes(searchTerm.toLowerCase())
  );

  // Delete modal handlers
  const openDeleteModal = (id) => {
    setDomandaIdToDelete(id);
    setIsDeleteModalOpen(true);
  };

  const closeDeleteModal = () => {
    setIsDeleteModalOpen(false);
    setDomandaIdToDelete(null);
  };

  const handleDeleteDomanda = async () => {
    try {
      await deleteDomanda(domandaIdToDelete, token);
      setDomande(prevState => prevState.filter(q => q.idDomanda !== domandaIdToDelete));
      setUpdateDomande(true);
      closeDeleteModal();
    } catch (error) {
      console.error('Error:', error);
    }
  };

  // Edit modal handlers
  const openEditModal = (domanda) => {
    setDomandaToEdit(domanda);
    setIsEditModalOpen(true);
  };

  const closeEditModal = () => {
    setIsEditModalOpen(false);
    setDomandaToEdit(null);
  };

  const handleEditDomanda = async (updatedData) => {
    try {
      await updateDomanda(domandaToEdit.idDomanda, updatedData, token);
      setUpdateDomande(true);
      closeEditModal();
      alert("Domanda modificata con successo! ✅");
    } catch (error) {
      console.error('Error:', error);
      alert("Si è verificato un errore durante la modifica. ❌");
    }
  };

  return (
    <div className='mx-24'>
      <h1 className="text-4xl">Domande</h1>
      
      <CercaDomanda 
        filtro={filtro}
        setFiltro={setFiltro}
        searchTerm={searchTerm}
        setSearchTerm={setSearchTerm}
      />

      <CreaDomanda user={user} setUpdateDomande={setUpdateDomande} />
      
      {domandeFiltrate.length > 0 ? (
        <ul className='mt-12'>
          {[...domandeFiltrate].reverse().map(d => (
            <CardDomanda 
              key={d.idDomanda}
              domanda={d}
              imageUrl={images[d.idDomanda]}
              userEmail={userEmail}
              onEdit={() => openEditModal(d)}
              onDelete={() => openDeleteModal(d.idDomanda)}
            />
          ))}
        </ul>
      ) : (
        <p className="text-gray-500 mt-4">Nessuna domanda trovata.</p>
      )}

      <EliminaDomanda
        isOpen={isDeleteModalOpen}
        onClose={closeDeleteModal}
        onDelete={handleDeleteDomanda}
      />

      {domandaToEdit && (
        <ModificaDomanda
          isOpen={isEditModalOpen}
          onClose={closeEditModal}
          domanda={domandaToEdit}
          onSave={handleEditDomanda}
          userEmail={user?.email}
        />
      )}
    </div>
  );
};

export default Domande;