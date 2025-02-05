import React, { useState } from 'react';
import Modal from './Modal'; // Importa il Modal

const NavbarHome = () => {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [formType, setFormType] = useState(null); // Stato per il tipo di form

  const toggleModal = (type = null) => {
    setIsModalOpen(type !== null); // Se type è null, chiudi il modal
    setFormType(type);
  };

  return (
    <nav>
      <div className="flex justify-between">
        <div className="ml-16 w-20 h-20">
          <h1 className="text-3xl">WebSurveys</h1>
        </div>
        <div className="flex justify-end bg-custom-fixtec py-4 mr-16">
          <div>
            <button onClick={() => toggleModal('register')}>Registrazione</button>
          </div>
          <div className="ml-16 hover:italic">
            <button onClick={() => toggleModal('login')}>Login</button>
          </div>
        </div>
      </div>

      {/* Modal Popup */}
      {isModalOpen && <Modal toggleModal={toggleModal} formType={formType} />}
    </nav>
  );
};

export default NavbarHome;
