/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{js,jsx,ts,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        'personal-purple': '#3603CD',  // Colore personalizzato
      },
      fontFamily: {
        jersey: ["Jersey 25", "cursive"],  // Aggiungi il font personalizzato
      },
    },
  },
  plugins: [],
}

