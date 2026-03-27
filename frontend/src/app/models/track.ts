import { Artist } from './artist';
import { Album } from './album';

export type Genre = 'POP' | 'ROCK' | 'JAZZ' | 'CLASSICAL' | 'ELECTRONIC' | 'HIP_HOP' | 'REGGAE' | 'METAL' | 'RNB' | 'OTHER';
export type Mood = 'HAPPY' | 'SAD' | 'CHILL' | 'FOCUS' | 'HYPE' | 'ROMANTIC' | 'WORKOUT' | 'ANGRY';

export interface Track {
  id: number;
  title: string;
  duration?: number;
  genre?: Genre;
  mood?: Mood;
  filePath?: string;
  album?: Album;
  artist: Artist;
}