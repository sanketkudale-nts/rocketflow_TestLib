package com.tracki.ui.custom.socket;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.tracki.utils.CommonUtils;
import com.tracki.utils.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;

public class WebSocketManager {

    private final static String TAG = "WebSocketManager";
    private static WebSocketManager webSocketManager;
    private boolean isConnected = false;
    private WebSocketClient mWebSocketClient;
    private boolean isConnectionOpen = false;
    private SocketListener socketListener;
    private Gson gson;

    private WebSocketManager(String url, String id, String tkn) {
        try {
            gson = new Gson();
            URI uri;
            uri = new URI(url + "?id=" + id + "&token=" + tkn + "&name=WebSocketManager");
//            uri = new URI("ws://staging.rocketflyer.in?id=3&token=1222212&name=PK");
            Log.e(TAG, uri.toString());

            mWebSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    isConnected = true;
                    isConnectionOpen = true;
                    Log.e(TAG, "Opened");
                    if (socketListener != null) {
                        socketListener.onOpen();
                    }
                }

                @Override
                public void onMessage(final String message) {
                    try {
//                        final String message = s;
//                        char pac = message.charAt(0);
                        Log.e(TAG, "message task " + message);
                        String[] pac = message.split(":");
                        if (message.length() > 1) {
                            int firstIndex = message.indexOf(":");
                            String extractedData = message.substring(firstIndex + 1);

                            //this data part is common for all
                            JSONObject jsonObject = new JSONObject(extractedData);
                            String data = (String) jsonObject.get("data");
                            Log.e(TAG, "Packet is " + pac[0] + " data part is " + data);
                            BaseModel response = null;
                            //convert char to int
//                            int packet = Character.getNumericValue(pac);
                            int packet = Integer.parseInt(pac[0]);
                            switch (packet) {
                                case 0://DISCONNECT
                                    response = gson.fromJson(data, ConnectionDetail.class);
                                    break;
                                case 1://CONNECT
                                    response = gson.fromJson(data, ConnectionResponse.class);
                                    break;
//                                case 2://HEARTBEAT
//                                    break;
                                case 3://MESSAGE
                                    response = gson.fromJson(data, Messages.class);
                                    break;
                                case 4://OPEN_OR_CREATE_ROOM
                                    response = gson.fromJson(data, OpenCreateRoomModel.class);
                                    break;
                                case 5://JOIN_ROOM
                                    break;
                                case 6://LEAVE_ROOM
                                    break;
                                case 7://LOAD_CONVERSATION
                                    response = gson.fromJson(data, OpenCreateRoomModel.class);
                                    break;
                                case 8://DELETE_ROOM
                                    break;
                                case 9://RENAME_ROOM
                                    break;
                                case 10://LOAD_ROOM_CONNECTIONS
                                    break;
                                case 11://CONNECTION_STATE_CHANGED
                                    response = gson.fromJson(data, ConnectionDetail.class);
                                    break;
                                default:
                                    Log.i(TAG, "message is " + message);
                                    break;
                            }
                            if (socketListener != null && response != null) {
                                System.out.println("response--> " + response.toString());
                                socketListener.onSocketResponse(packet, response);
                            }
                        } else {
                            Log.e(TAG, "packet is " + pac[0]);
                            int packet = Integer.parseInt(pac[0]);
                            if(packet==2){
                                socketListener.onSocketResponse(packet, null);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onClose(int i, String s, boolean b) {
                    isConnected = false;
                    isConnectionOpen = false;
                    Log.e(TAG, "Closed with" + s);
                    webSocketManager = null;
                    if (socketListener != null) {
                        socketListener.closed();
                    }
                }

                @Override
                public void onError(Exception e) {
                    isConnected = false;
                    isConnectionOpen = false;
                    Log.e(TAG, "Error " + e.getMessage());
                    webSocketManager = null;
                    if (socketListener != null) {
                        socketListener.closed();
                    }
                }
            };
            connect();

        } catch (Exception e) {
            Log.e(TAG, "URI Syntax Error");
            if (socketListener != null) {
                socketListener.closed();
            }
        }
    }

    /**
     * Get the instance of the this class.
     *
     * @param url url
     * @param id  id
     * @param tkn token
     * @return instance of the current class.
     */
    public static WebSocketManager getInstance(String url, String id, String tkn) {
        if (webSocketManager == null) {
            synchronized (WebSocketManager.class) {
                if (webSocketManager == null) {
                    webSocketManager = new WebSocketManager(url, id, tkn);
                }
            }
        }
        return webSocketManager;
    }

    public boolean isConnected() {
        return isConnected && mWebSocketClient.isOpen();
    }

    private void connect() {
        mWebSocketClient.connect();
    }

    public void disconnect() {
        removeListener();
        mWebSocketClient.close();
        webSocketManager = null;
    }

    public boolean isOpen() {
        return isConnectionOpen && mWebSocketClient.isOpen();
    }

    /**
     * Method used to get the status of all the users passed into the strings.
     *
     * @param connectionIds ids of the users.
     */
    public void connectPacket(String connectionIds) {
        if (isConnected && isOpen()) {
            SocketDataRequest socketData = new SocketDataRequest();
            socketData.setPacket1(connectionIds);
            String connect = CommonUtils.createSocketRequest(PacketInfo.P1, socketData);
            //after open get all the connection ids and check status.
            mWebSocketClient.send(connect);
            Log.e(TAG, "connectPacket() called");
        } else {
            throw new RuntimeException("Please call connect before this method.");
        }
    }

    /**
     * Open or create chat room for conversation
     *
     * @param buddyIds hashMap of buddies
     */
    public void openCreateRoom(@Nullable ArrayList<String> buddyIds, String roomId, boolean loadMsgs, int msgCount) {
        if (isConnected && isOpen()) {
            SocketDataRequest socketData = new SocketDataRequest();
            socketData.setPacket4(buddyIds);
            socketData.setRoomId(roomId);
            socketData.setLoadMsgs(loadMsgs);
            socketData.setMsgCount(msgCount);
            String connect = CommonUtils.createSocketRequest(PacketInfo.P4, socketData);
            //after open get all the connection ids and check status.
            mWebSocketClient.send(connect);
        } else {
            throw new RuntimeException("Please call connect before this method.");
        }
    }
    public void sendMedia(String url, String roomId,String type) {
        if (isConnected && isOpen()) {
            SocketDataRequest socketData = new SocketDataRequest();
            socketData.setPacket1(url);
            socketData.setRoomId(roomId);
            socketData.setType(type);
            String connect = CommonUtils.createSocketRequest(PacketInfo.P3, socketData);
            //after open get all the connection ids and check status.
            mWebSocketClient.send(connect);
        } else {
            throw new RuntimeException("Please call connect before this method.");
        }
    }
    public void send(String message, String roomId) {
        if (isConnected && isOpen()) {
            SocketDataRequest socketData = new SocketDataRequest();
            socketData.setPacket1(message);
            socketData.setRoomId(roomId);
            socketData.setType("TEXT");
            String connect = CommonUtils.createSocketRequest(PacketInfo.P3, socketData);
            //after open get all the connection ids and check status.
            mWebSocketClient.send(connect);
        } else {
            throw new RuntimeException("Please call connect before this method.");
        }
    }
    public void sendHeartBeat(String message, String roomId) {
        if (isConnected && isOpen()) {
            SocketDataRequest socketData = new SocketDataRequest();
            socketData.setPacket1(message);
            socketData.setRoomId(roomId);
            socketData.setType("TEXT");
            String connect = CommonUtils.createSocketRequest(PacketInfo.P2, socketData);
            //after open get all the connection ids and check status.
            mWebSocketClient.send(connect);
        } else {
            throw new RuntimeException("Please call connect before this method.");
        }
    }

    public void addListener(SocketListener listener) {
        this.socketListener = listener;
    }

    public void removeListener() {
        this.socketListener = null;
    }

    /**
     * Method used to get the next set of messages and return to the screen
     *
     * @param roomId   room id of the user
     * @param pn       page number of the messages
     * @param msgCount total count of the messages
     */
    public void loadMore(@NonNull String roomId, int pn, int msgCount) {
        if (isConnected && isOpen()) {
            SocketDataRequest socketData = new SocketDataRequest();
            socketData.setRoomId(roomId);
            socketData.setPageNumber(pn);
            socketData.setMsgCount(msgCount);
            String connect = CommonUtils.createSocketRequest(PacketInfo.P7, socketData);
            //Load more messages after user click on the load more button
            mWebSocketClient.send(connect);
        } else {
            throw new RuntimeException("Please call connect before this method.");
        }
    }

    public interface SocketListener {
        void onSocketResponse(int eventName, BaseModel baseModel);

        void onOpen();

        void closed();
    }

    public static class SocketDataRequest {
        private String packet1 = null;
        private ArrayList<String> packet4 = null;
        private String roomId = null;
        private boolean loadMsgs = false;
        private int msgCount;
        private String type = null;
        private int pageNumber;

        public int getPageNumber() {
            return pageNumber;
        }

        public void setPageNumber(int pageNumber) {
            this.pageNumber = pageNumber;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getMsgCount() {
            return msgCount;
        }

        public void setMsgCount(int msgCount) {
            this.msgCount = msgCount;
        }

        public boolean isLoadMsgs() {
            return loadMsgs;
        }

        public void setLoadMsgs(boolean loadMsgs) {
            this.loadMsgs = loadMsgs;
        }

        public String getRoomId() {
            return roomId;
        }

        public void setRoomId(String roomId) {
            this.roomId = roomId;
        }

        public ArrayList<String> getPacket4() {
            return packet4;
        }

        public void setPacket4(ArrayList<String> packet4) {
            this.packet4 = packet4;
        }

        public String getPacket1() {
            return packet1;
        }

        void setPacket1(String packet1) {
            this.packet1 = packet1;
        }
    }
}
