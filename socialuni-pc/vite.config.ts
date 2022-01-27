import {fileURLToPath, URL} from 'url'

import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
import vuetify from '@vuetify/vite-plugin'
import Inspect from 'vite-plugin-inspect'
import myExample from './rollup-plugin-my-example.js';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    // 仅适用于开发模式
    Inspect(),
    myExample(),
    vue(),
    // https://github.com/vuetifyjs/vuetify-loader/tree/next/packages/vite-plugin
    vuetify({
      autoImport: true,
    }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    proxy: {
      '/api/': {
        // '/api/': 'https://api.socialuni.cn:8091',
        target: 'https://localpc.socialuni.cn:8091',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  },
  css: {
    preprocessorOptions: {
      sass: {
        additionalData: "@import '@/styles/index.scss'",
      },
    },
  },
})

