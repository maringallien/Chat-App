package com.MarinGallien.JavaChatApp.java_chat_app.Events;

/**
 * This class contains chat-related events that will be published by the http server when:
 *      . A new chat is created
 *      . A chat is deleted
 *      . User added to a chat
 *      . user removed from a chat
 *
 * The websocket server will be listening for those events to keep its in-memory ChatMap accurate
 * Other program parts will likely be listening too
 */
public class ChatEvents {

    /**
     * Need a set of events emitted by HTTP server for backend service to perform writes to database
     *      . create new chat
     *      . delete chat
     *      . add user to chat
     *      . remove user from chat
     * These events will be intercepted by a backend service in charge of performing database reads/writes
     */

    /**
     * Need a set of events emitted by backend database service for websocket server to update in-memory map
     *      . create new chat
     *      . delete chat
     *      . add user to chat
     *      . remove user from chat
     * These events are emitted after the database operation is performed to avoid inconsistencies with in-memory map.
     * The websocket server will intercept those events and directly make an update to its map without database access.
     */
}
