import React, { useState } from "react";

const Verify = ({ toggleModal, email }) => { 
  const [verificationCode, setVerificationCode] = useState("");

  const handleVerify = async (event) => {
    event.preventDefault();

    console.log("Verifica in corso...", { email, verificationCode });

    const user = {
      email, 
      verificationCode 
    };

    try {
      const response = await fetch("http://localhost:8080/auth/verify", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify(user),
      });

      const data = await response.json();

      if (response.status === 401) {
        alert(data.message || "Credenziali errate");
        return;
      }

      if (response.ok) {
        alert(data.message);
        toggleModal(); // Chiudi il modal dopo la verifica
      }
    } catch (error) {
      console.error("Errore:", error);
      alert("Si è verificato un errore durante la verifica.");
    }
  };

  return (
    <div className="bg-white p-6 rounded-lg w-96">
      <h2 className="text-2xl mb-4">Verifica Account</h2>
      <form onSubmit={handleVerify}>
        <input
          type="text"
          placeholder="Codice di verifica"
          value={verificationCode}
          onChange={(e) => setVerificationCode(e.target.value)}
          className="w-full p-2 mb-3 border border-gray-300 rounded"
          required
        />
        <button type="submit" className="w-full p-2 bg-blue-500 text-white rounded">
          Conferma
        </button>
      </form>
    </div>
  );
};

export default Verify;
