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
      <div className="flex justify-between my-5">
        <div className="ml-16">
          <h1 className="text-3xl text-personal-purple font-semibold">WebSurveys</h1>
        </div>
        <div className="flex justify-end mr-16">
          <div className='my-auto'>
            <button onClick={() => toggleModal('register')}>Registrazione</button>
          </div>
          <div className="ml-16 bg-personal-purple text-white px-8 rounded-md py-1">
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
