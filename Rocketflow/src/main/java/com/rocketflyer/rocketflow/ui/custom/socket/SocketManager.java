package com.rocketflyer.rocketflow.ui.custom.socket;//package com.tracki.ui.custom.socket;
//
//import com.github.nkzawa.emitter.Emitter;
//import com.github.nkzawa.socketio.client.IO;
//import com.github.nkzawa.socketio.client.Socket;
//import com.tracki.utils.Log;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//public class SocketManager {
//    public final static String NEW_MESSAGE = "1";
//    public final static String JOIN_ROOM = "2";
//    public final static String LEAVE_ROOM = "3";
//    public final static String CREATE_ROOM = "4";
//    public final static String TYPING = "5";
//    public final static String STOP_TYPING = "6";
//    private final static String TAG = "SocketManager";
//    private static SocketManager socketManager = null;
//    private Socket mSocket = null;
//    private boolean isConnected = false;
//    private EventListenerO eventListener;
//
//    private SocketManager(String url, String id, String tkn) {
//        try {
//
////            String mainUrl = url + "?id=" + id + "&token=" + tkn;
//            String mainUrl = "http://staging.rocketflyer.in?id=1&token=1222212&name=PK";
//            Log.e(TAG, mainUrl);
//            mSocket = IO.socket(mainUrl);
//        } catch (Exception e) {
//            Log.e(TAG, "URI Syntax Error");
//        }
//    }
//
//    /**
//     * Method used to get the instance of the SocketManager class
//     *
//     * @return SocketManager instance
//     */
//    public static SocketManager getInstance(String chatUrl, String id, String tkn) {
//        if (socketManager == null) {
//            synchronized (SocketManager.class) {
//                if (socketManager == null) {
//                    socketManager = new SocketManager(chatUrl, id, tkn);
//                }
//            }
//        }
//        return socketManager;
//    }
//
//    /**
//     * Method used to clear the instance of @{@link SocketManager}.
//     */
//    public void clear() {
//        socketManager = null;
//    }
//
//    /**
//     * Connect socket
//     */
//    public void connect(EventListenerO eventListenerO) {
//        Log.e(TAG, "inside connect method");
//        this.eventListener = eventListenerO;
//        mSocket.on(Socket.EVENT_CONNECT, new EventHandler(Socket.EVENT_CONNECT));
//        mSocket.on(Socket.EVENT_DISCONNECT, new EventHandler(Socket.EVENT_DISCONNECT));
//        mSocket.on(Socket.EVENT_CONNECT_ERROR, new EventHandler(Socket.EVENT_CONNECT_ERROR));
//        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, new EventHandler(Socket.EVENT_CONNECT_TIMEOUT));
//        mSocket.on(NEW_MESSAGE, new EventHandler(NEW_MESSAGE));
//        mSocket.on(CREATE_ROOM, new EventHandler(CREATE_ROOM));
//        mSocket.on(JOIN_ROOM, new EventHandler(JOIN_ROOM));
//        mSocket.on(LEAVE_ROOM, new EventHandler(LEAVE_ROOM));
//        mSocket.on(TYPING, new EventHandler(TYPING));
//        mSocket.on(STOP_TYPING, new EventHandler(STOP_TYPING));
//        mSocket.connect();
//    }
//
//    /**
//     * Disconnect socket
//     */
//    public void disconnect() {
//        this.eventListener = null;
//        mSocket.disconnect();
//    }
//
//    /**
//     * Used to check the status of the socket.
//     *
//     * @return true if socket is connected else false
//     */
//    public boolean isConnected() {
//        return isConnected && mSocket.connected();
//    }
//
//    public void sendMessage(String eventName, Object object) {
//        mSocket.emit(eventName, object/*, new Ack() {
//            @Override
//            public void call(Object... args) {
//
//            }
//        }*/);
//    }
//
//    /**
//     * Method used to handle the callback of socket response.
//     */
//    public interface EventListenerO {
//
//        void onResponse(String eventName, JSONObject jsonObject);
//    }
//
//    /**
//     * Method used to handle the callback of socket response.
//     */
//    public interface EventListenerA {
//        void onResponse(String eventName, JSONArray jsonArray);
//    }
//
//    /**
//     * Class that is used to listen event for sockets.
//     */
//    private class EventHandler implements Emitter.Listener {
//        private String eventName;
//
//        EventHandler(String eventName) {
//            this.eventName = eventName;
//        }
//
//        @Override
//        public void call(Object... args) {
//            try {
////                Log.e(TAG, "inside call method " + eventName);
//                switch (eventName) {
//                    case Socket.EVENT_CONNECT:
//                        if (!isConnected) {
//                            isConnected = true;
//                        }
//                        Log.e(TAG, "inside call method EVENT_CONNECT " + eventName);
//                        break;
//                    case Socket.EVENT_DISCONNECT:
//                        isConnected = false;
//                        Log.e(TAG, "inside call method EVENT_DISCONNECT " + eventName);
//                        break;
//                    case Socket.EVENT_CONNECT_ERROR:
//                        Log.e(TAG, "inside call method EVENT_CONNECT_ERROR " + eventName);
//                        break;
//                    case Socket.EVENT_CONNECT_TIMEOUT:
//                        Log.e(TAG, "inside call method EVENT_CONNECT_TIMEOUT" + eventName);
//                        break;
//                    case NEW_MESSAGE:
//                        if (eventListener != null) {
//                            eventListener.onResponse(eventName, (JSONObject) args[0]);
//                        }
//                        break;
//                    case CREATE_ROOM:
//                        if (eventListener != null) {
//                            eventListener.onResponse(eventName, (JSONObject) args[0]);
//                        }
//                        break;
//                    case JOIN_ROOM:
//                        if (eventListener != null) {
//                            eventListener.onResponse(eventName, (JSONObject) args[0]);
//                        }
//                        break;
//                    case LEAVE_ROOM:
//                        if (eventListener != null) {
//                            eventListener.onResponse(eventName, (JSONObject) args[0]);
//                        }
//                        break;
//                    case TYPING:
//                        if (eventListener != null) {
//                            eventListener.onResponse(eventName, (JSONObject) args[0]);
//                        }
//                        break;
//                    case STOP_TYPING:
//                        if (eventListener != null) {
//                            eventListener.onResponse(eventName, (JSONObject) args[0]);
//                        }
//                        break;
//                    default:
//                        break;
//                }
//            } catch (Exception e) {
//                Log.e(TAG, "Exception inside call() method for event " + eventName + " " + e);
//            }
//        }
//    }
//}