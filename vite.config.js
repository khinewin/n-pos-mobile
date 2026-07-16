import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { VitePWA } from 'vite-plugin-pwa'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    VitePWA({
      registerType: 'autoUpdate',
      injectRegister: 'auto',
      includeAssets: ['favicon.ico', 'img/logo.png', 'img/nav-logo.png'],
      workbox: {
        maximumFileSizeToCacheInBytes: 5000000 // 5MB
      },
      manifest: {
        name: 'N-POS',
        short_name: 'NPOS',
        description: 'N-POS Point of Sale',
        theme_color: '#0d6efd',
        background_color: '#ffffff',
        display: 'standalone',
        icons: [
          {
            src: 'img/logo.png',
            sizes: '192x192',
            type: 'image/png'
          },
          {
            src: 'img/logo.png',
            sizes: '512x512',
            type: 'image/png'
          }
        ]
      }
    })
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  }
})
