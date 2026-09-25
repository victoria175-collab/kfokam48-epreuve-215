import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// En developpement, l'appel API passe par le proxy Vite : la couche api/ ne
// connait que des chemins relatifs, aucune regle metier cote client (F3).
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
})
