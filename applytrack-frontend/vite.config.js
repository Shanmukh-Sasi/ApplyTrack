import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/auth': 'http://localhost:8081',
      '/user': 'http://localhost:8081',
      '/application': 'http://localhost:8081',
      '/interview': 'http://localhost:8081',
    },
  },
});
