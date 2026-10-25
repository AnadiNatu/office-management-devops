export type WsConnectionState = 'disconnected' | 'connecting' | 'connected' | 'error';

export interface WsSubscription {
  destination: string;
  unsubscribe: () => void;
}