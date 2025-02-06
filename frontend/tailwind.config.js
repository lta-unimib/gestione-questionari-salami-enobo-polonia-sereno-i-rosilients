const defaultTheme = require('tailwindcss/defaultTheme');

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
        jersey: ['"Jersey 25"', ...defaultTheme.fontFamily.sans],  // Aggiungi il font personalizzato
      },
    },
  },
  plugins: [],
}

