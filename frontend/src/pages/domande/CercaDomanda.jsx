// src/components/Domande/FilterSearch.jsx
import React from 'react';

const CercaDomanda = ({ filtro, setFiltro, searchTerm, setSearchTerm }) => {
  return (
    <div className="flex justify-between items-center mt-4">
      <select 
        value={filtro} 
        onChange={(e) => setFiltro(e.target.value)} 
        className="border rounded-lg p-2"
      >
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
  );
};

export default CercaDomanda;