import type { Metadata } from 'next';
import { Space_Grotesk, IBM_Plex_Mono } from 'next/font/google';
import './globals.css';
import { AppProviders } from '@/components/AppProviders';

const spaceGrotesk = Space_Grotesk({ subsets: ['latin'], variable: '--font-display' });
const plexMono = IBM_Plex_Mono({ subsets: ['latin'], weight: ['400', '600'], variable: '--font-mono' });

export const metadata: Metadata = {
  title: 'Internal Admin Dashboard',
  description: 'Production-ready internal admin dashboard template.'
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en" className={`${spaceGrotesk.variable} ${plexMono.variable}`}>
      <body className="font-[var(--font-display)]">
        <AppProviders>{children}</AppProviders>
      </body>
    </html>
  );
}
