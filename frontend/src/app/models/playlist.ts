import { Track } from './track';
import { Genre, Mood } from './track';

export type PlaylistType = 'MANUAL' | 'SMART';

export interface Playlist {
  id: number;
  name: string;
  type: PlaylistType;
  imageUrl?: string;
  createdAt: string;
  userEmail?: string;
  tracks: Track[];
}

export interface PlaylistCreateRequest {
  name: string;
  type: PlaylistType;
  mood?: Mood;
  genre?: Genre;
}