import { useState } from "react";

const EmailVerification = ({ email, onSuccess }) => {
  const [code, setCode] = useState("");

  const handleVerify = () => {
    fetch("http://localhost:8080/utente/verifica-email", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, codice: code }),
    })
      .then((response) => {
        if (!response.ok) throw new Error("Codice errato o scaduto.");
        return response.text();
      })
      .then(() => {
        alert("Email verificata con successo!");
        onSuccess(); // Passa alla schermata di login
      })
      .catch((error) => alert(error.message));
  };

  return (
    <div className="bg-white p-6 rounded-lg w-96">
      <h2 className="text-2xl mb-4">Verifica Email</h2>
      <p className="mb-3">Inserisci il codice ricevuto via email.</p>
      <input
        type="text"
        placeholder="Codice di verifica"
        value={code}
        onChange={(e) => setCode(e.target.value)}
        className="w-full p-2 mb-3 border border-gray-300 rounded"
      />
      <button className="w-full p-2 bg-blue-500 text-white rounded" onClick={handleVerify}>
        Conferma
      </button>
    </div>
  );
};

export default EmailVerification;
