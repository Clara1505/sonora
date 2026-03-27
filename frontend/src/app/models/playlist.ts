import { Track } from './track';

export type PlaylistType = 'MANUAL' | 'SMART';

export interface Playlist {
  id: number;
  name: string;
  type: PlaylistType;
  imageUrl?: string;
  createdAt: string;
  tracks: Track[];
}