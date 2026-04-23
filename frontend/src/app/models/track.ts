import { Artist } from './artist';
import { Album } from './album';

export type Genre = 'POP' | 'ROCK' | 'JAZZ' | 'CLASSICAL' | 'ELECTRONIC' | 'HIP_HOP' | 'REGGAE' | 'METAL' | 'RNB' | 'OTHER';
export type Mood = 'HAPPY' | 'SAD' | 'CHILL' | 'FOCUS' | 'HYPE' | 'ROMANTIC' | 'WORKOUT' | 'ANGRY';

// Modèle utilisé dans l'UI (objets imbriqués)
export interface Track {
  id: number;
  title: string;
  duration?: number;
  genre?: Genre;
  mood?: Mood;
  filePath?: string;
  album?: Album;
  artist: Artist;
  createdByEmail?: string;
}

// DTO tel que retourné par le backend (IDs plats)
export interface TrackDto {
  id: number;
  title: string;
  duration?: number;
  genre?: Genre;
  mood?: Mood;
  filePath?: string;
  albumId?: number;
  artistId: number;
  createdByEmail?: string;
}