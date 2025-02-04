import React, { useState } from 'react';
import Modal from './Modal'; // Importa il Modal

const NavbarHome = () => {
  const [isModalOpen, setIsModalOpen] = useState(false); // Stato per aprire/chiudere il modal

  const toggleModal = () => {
    setIsModalOpen(!isModalOpen);
  };

  return (
    <nav>
      <div className="flex justify-between">
        <div className="ml-16 w-20 h-20">
          <h1 className="text-3xl">WebSurveys</h1>
        </div>
        <div className="flex justify-end bg-custom-fixtec py-4 mr-16">
          <div>
            <button onClick={toggleModal}>Registrazione</button>
          </div>
          <div className="ml-16 hover:italic">
            <button onClick={toggleModal}>Login</button>
          </div>
        </div>
      </div>

      {/* Modal Popup */}
      {isModalOpen && <Modal toggleModal={toggleModal} />}
    </nav>
  );
};

export default NavbarHome;
