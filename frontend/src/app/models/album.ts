import { Artist } from './artist';

export interface Album {
  id: number;
  title: string;
  releaseYear?: number;
  coverUrl?: string;
  artist: Artist;
}