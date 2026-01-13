import type { Config } from 'tailwindcss';

const config: Config = {
  content: ['./src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        slate: {
          925: '#0b0f1a'
        }
      },
      boxShadow: {
        soft: '0 10px 30px rgba(15, 23, 42, 0.1)'
      }
    }
  },
  plugins: []
};

export default config;
