import {Client, type IMessage,
  type StompSubscription,} from "@stomp/stompjs";

import SockJS from "sockjs-client";

import { authStorage } from "../../../auth/authStorage";
import { WS_CONFIG } from "../../../config/webSocketConfig";

import type { WsConnectionState } from "../../../types/webocket";

import {
  MESSAGE_WS_ENDPOINTS,
} from "../../endpoints/messageEndpoints";

import type {
  MessageResponseDTO,
  MessageStatusResponseDTO,
} from "../../models/messageModel";

const APP_PREFIX = "/app";

type StateListener = (
  state: WsConnectionState
) => void;

type MessageListener = (
  message: MessageResponseDTO
) => void;

type StatusListener = (
  status: MessageStatusResponseDTO
) => void;


class MessageWebSocketClient {

  private client: Client | null = null;

  private state: WsConnectionState =
    "disconnected";

  private stateListeners =
    new Set<StateListener>();

  private messageListeners =
    new Set<MessageListener>();

  private statusListeners =
    new Set<StatusListener>();

  private subscriptions:
    StompSubscription[] = [];


  // ============================================================
  // STATE
  // ============================================================

  private setState(
    next: WsConnectionState
  ) {
    this.state = next;

    this.stateListeners.forEach(
      (listener) => listener(next)
    );
  }


  getState() {
    return this.state;
  }


  onStateChange(
    listener: StateListener
  ) {
    this.stateListeners.add(listener);

    return () => {
      this.stateListeners.delete(listener);
    };
  }


  onMessage(
    listener: MessageListener
  ) {
    this.messageListeners.add(listener);

    return () => {
      this.messageListeners.delete(listener);
    };
  }


  onStatus(
    listener: StatusListener
  ) {
    this.statusListeners.add(listener);

    return () => {
      this.statusListeners.delete(listener);
    };
  }


  // ============================================================
  // CONNECTION
  // ============================================================

  connect() {

    if (
      this.client?.active ||
      this.state === "connecting"
    ) {
      return;
    }

    const token =
      authStorage.getAccessToken();

    if (!token) {
      this.setState("disconnected");
      return;
    }

    this.setState("connecting");


    this.client = new Client({

      webSocketFactory: () =>
        new SockJS(
          WS_CONFIG.url
        ) as unknown as WebSocket,


      connectHeaders: {
        Authorization:
          `Bearer ${token}`,
      },


      reconnectDelay:
        WS_CONFIG.reconnectDelayMs,


      heartbeatIncoming:
        WS_CONFIG.heartbeatIncomingMs,


      heartbeatOutgoing:
        WS_CONFIG.heartbeatOutgoingMs,


      debug: () => {
        // Keep disabled in normal development.
        // console.debug(message);
      },


      onConnect: () => {

        console.info(
          "[Message WS] STOMP connected"
        );

        this.setState("connected");

        this.subscribe();
      },


      onDisconnect: () => {

        console.info(
          "[Message WS] STOMP disconnected"
        );

        this.setState("disconnected");
      },


      onWebSocketClose: () => {

        console.warn(
          "[Message WS] WebSocket closed"
        );

        this.setState("disconnected");
      },


      onWebSocketError: () => {

        console.warn(
          "[Message WS] WebSocket error"
        );

        this.setState("error");
      },


      onStompError: (frame) => {

        console.error(
          "[Message WS] STOMP error",
          frame.headers,
          frame.body
        );

        this.setState("error");
      },

    });


    this.client.activate();
  }


  // ============================================================
  // IMPORTANT:
  // active !== connected
  // ============================================================

  isConnected(): boolean {

    return (
      this.client !== null &&
      this.client.active &&
      this.client.connected &&
      this.state === "connected"
    );
  }


  // ============================================================
  // SUBSCRIPTIONS
  // ============================================================

  private subscribe() {

    if (!this.client || !this.client.connected) {
      return;
    }


    this.subscriptions.forEach(
      (subscription) =>
        subscription.unsubscribe()
    );


    this.subscriptions = [];


    const messageSubscription =
      this.client.subscribe(
        WS_CONFIG.subscriptions.messages,

        (frame: IMessage) => {

          try {

            const message:
              MessageResponseDTO =
              JSON.parse(frame.body);


            this.messageListeners.forEach(
              (listener) =>
                listener(message)
            );

          } catch (error) {

            console.error(
              "[Message WS] Invalid message frame",
              error
            );
          }

        }
      );


    const statusSubscription =
      this.client.subscribe(
        WS_CONFIG.subscriptions.messageStatus,

        (frame: IMessage) => {

          try {

            const status:
              MessageStatusResponseDTO =
              JSON.parse(frame.body);


            this.statusListeners.forEach(
              (listener) =>
                listener(status)
            );

          } catch (error) {

            console.error(
              "[Message WS] Invalid status frame",
              error
            );
          }

        }
      );


    this.subscriptions.push(
      messageSubscription,
      statusSubscription
    );
  }


  // ============================================================
  // DISCONNECT
  // ============================================================

  disconnect() {

    this.subscriptions.forEach(
      (subscription) =>
        subscription.unsubscribe()
    );

    this.subscriptions = [];


    void this.client?.deactivate();


    this.client = null;


    this.setState(
      "disconnected"
    );
  }


  // ============================================================
  // GENERIC PUBLISH
  // ============================================================

  private publish(
    destination: string,
    body: unknown
  ): boolean {

    if (!this.isConnected()) {
      return false;
    }


    this.client!.publish({

      destination:
        `${APP_PREFIX}${destination}`,

      body:
        JSON.stringify(body),

    });


    return true;
  }


  // ============================================================
  // SEND MESSAGE
  // ============================================================

  sendMessage(
    conversationId: number,
    content: string
  ): boolean {

    if (!this.isConnected()) {

      console.warn(
        "[Message WS] Not connected. REST fallback will be used."
      );

      return false;
    }


    this.client!.publish({

      destination:
        `${APP_PREFIX}${MESSAGE_WS_ENDPOINTS.SEND}`,

      headers: {
        conversationId:
          String(conversationId),
      },

      body:
        JSON.stringify({
          content,
        }),

    });


    return true;
  }


  // ============================================================
  // DELIVERED
  // ============================================================

  markDelivered(
    messageId: number
  ): boolean {

    return this.publish(
      MESSAGE_WS_ENDPOINTS.DELIVERED,
      {
        messageId,
      }
    );
  }


  // ============================================================
  // READ
  // ============================================================

  markRead(
    messageId: number
  ): boolean {

    return this.publish(
      MESSAGE_WS_ENDPOINTS.READ,
      {
        messageId,
      }
    );
  }

}


export const messageWebSocketClient =
  new MessageWebSocketClient();