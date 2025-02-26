// src/components/Domande/QuestionCard.jsx
import React from 'react';
import { TrashIcon, PencilSquareIcon } from '@heroicons/react/24/solid';

const CardDomanda = ({ domanda, imageUrl, userEmail, onEdit, onDelete }) => {
  const isOwner = domanda.emailUtente === userEmail;

  return (
    <li className="border p-4 my-2 rounded-lg shadow-lg flex justify-between">
      <div>
        <h3 className="text-xl font-semibold">{domanda.argomento}</h3>
        <p>{domanda.testoDomanda}</p>
        {domanda.imagePath && imageUrl && (
          <img src={imageUrl} alt="Domanda" className="h-96 w-96" />
        )}
        {domanda.opzioni && domanda.opzioni.length > 0 && (
          <ul className="mt-2">
            {domanda.opzioni.map((opzione, index) => (
              <li key={index} className="ml-4 list-disc">{opzione}</li>
            ))}
          </ul>
        )}
      </div>
      <div className="edit flex gap-4">
        <button 
          className={`text-gray-500 hover:text-gray-700 ${!isOwner ? "opacity-50 cursor-not-allowed" : ""}`} 
          onClick={onEdit}
          disabled={!isOwner}
        >
          <PencilSquareIcon className="h-6 w-6" />
        </button>
        
        <button 
          className={`text-red-600 hover:text-red-800 ${!isOwner ? "opacity-50 cursor-not-allowed" : ""}`} 
          onClick={onDelete}
          disabled={!isOwner}
        >
          <TrashIcon className="h-6 w-6" />
        </button>
      </div>
    </li>
  );
};

export default CardDomanda;