export type PlayerType =
  | 'NIT'
  | 'TAG'
  | 'LAG'
  | 'CALLING_STATION'
  | 'LOOSE_PASSIVE'
  | 'MANIAC'
  | 'UNCLASSIFIED';

export type LeakType = 'LIMP' | 'MISSED_C_BET';
export type LeakSeverity = 'INFO' | 'MINOR' | 'MAJOR';
export type Street = 'PREFLOP' | 'FLOP' | 'TURN' | 'RIVER' | 'SHOWDOWN';

export interface Leak {
  player: string;
  type: LeakType;
  severity: LeakSeverity;
  handId: string;
  street: Street;
  explanation: string;
}

export interface PlayerReport {
  name: string;
  handsPlayed: number;
  vpip: number;
  pfr: number;
  threeBetPct: number;
  aggressionFactor: number | null;
  postflopBets: number;
  postflopRaises: number;
  postflopCalls: number;
  type: PlayerType;
  leaks: Leak[];
}

export interface AnalysisResponse {
  handsAnalyzed: number;
  players: PlayerReport[];
}
