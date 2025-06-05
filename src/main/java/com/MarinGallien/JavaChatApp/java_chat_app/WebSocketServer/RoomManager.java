package com.MarinGallien.JavaChatApp.java_chat_app.WebSocketServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Set;

public class RoomManager {
    private static final Logger logger = LoggerFactory.getLogger(RoomManager.class);

    // Thread safe map to store roomID:Set<userID>
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();

    // Constructor
    public RoomManager(){}

    // Creates a room of 2 members
    public String createPrivateRoom(String userID1, String userID2) {
        // Validate input parameters
        if (!checkId(userID1)){
            logger.warn("Cannot create private room: userID1 is null or empty");
            return null;
        }

        if (!checkId(userID1)){
            logger.warn("Cannot create private room: userID2 is null or empty");
            return null;
        }

        if (userID1.trim().equals(userID2.trim())){
            logger.warn("Cannot create private room: userID1 and userID2 are the same");
            return null;
        }

        // Create a private room ID for single user chats to avoid duplicates
        String privateRoomId = genPrivateRoomId(userID1, userID2);

        // Check if the room already exists
        if (roomExists(privateRoomId)){
            logger.debug("A private room alread exists betweem user {} and {}: {}", userID1, userID2, privateRoomId);
            return privateRoomId;
        }

        // Create the new room
        Room privateRoom = new Room(privateRoomId);

        // Add users to the room
        privateRoom.addMember(userID1);
        privateRoom.addMember(userID2);

        // Add room to rooms map
        rooms.put(privateRoomId, privateRoom);

        logger.info("Created a private room {} between user {} and {}", privateRoomId, userID1, userID2);
        return privateRoomId;
    }

    // Check user ID is valid
    private boolean checkId(String userID) {
        return userID != null && !userID.trim().isEmpty();
    }

    private String genPrivateRoomId(String userID1, String userID2) {
        String[] sortedIDs = {userID1, userID2};
        Arrays.sort(sortedIDs);
        return "PRIVATE_" + sortedIDs[0] + "_" + sortedIDs[1];
    }

    // Creates a room of >3 members
    public String createGroupRoom(String creatorID, Set<String> initialMembers) {
        // Validate input parameters
        if (!checkId(creatorID)){
            logger.warn("Cannot create group room: creator ID is null or empty");
            return null;
        }

        // Validate set of members
        if (initialMembers  == null || initialMembers.isEmpty()){
            logger.warn("Cannot create group room: initial members list is null or empty");
            return null;
        }

        // Create the room and add creator to it
        Room groupRoom = new Room();
        groupRoom.addMember(creatorID.trim());

        // Add each member to room and make sure creator ID is not one of them. Set prevents duplicates internally
        for (String userID : initialMembers){
            if (checkId(userID) && !userID.trim().equals(creatorID.trim())){
                groupRoom.addMember(userID.trim());
            } else {
                logger.warn("User ID {} could not be added to room because ID was null or empty");
            }
        }

        // Create room and add to rooms map
        String groupRoomId = groupRoom.getRoomID();
        rooms.put(groupRoomId, groupRoom);

        logger.info("Created group room {} with creator {} and {} initial members",
                groupRoomId, creatorID, initialMembers.size());

        return groupRoomId;
    }

    // Deletes a single room
    public boolean deleteRoom(String roomID) {
        if (!checkId(roomID)){
            logger.warn("Could not delete room: room ID is null or empty");
            return false;
        }

        Room removedRoom = rooms.remove(roomID.trim());

        if (removedRoom != null) {
            logger.info("Room {} with {} participants was successfully removed", roomID, removedRoom.getMembersCount());
            return true;
        } else {
            logger.warn("Room {} could not be removed because it does not exist", roomID);
            return false;
        }
    }

    // Deletes all rooms
    public boolean clearAllRooms() {
        int roomCount = rooms.size();
        rooms.clear();
        logger.info("{} rooms were deleted", roomCount);
    }

    // Returns a room object corresponding to roomID
    public Room getRoom(String roomID) {
        if (!checkId(roomID)) {
            logger.warn("Could not retrieve room because ID is null or empty");
            return null;
        }

        return rooms.get(roomID.trim());
    }

    // Returns the total number of rooms
    public int getTotalRooms() {
        return rooms.size();
    }

    // Returns a set of room IDs
    public Set<String> getAllRoomIDs() {
        return new HashSet<>(rooms.keySet());
    }

    // Checks if the room exists
    public boolean roomExists(String roomID) {
        if (!checkId(roomID)) {
            logger.warn("Could not check if room exists because room ID is null or empty");
            return false;
        }
        return rooms.containsKey(roomID);
    }

    // Override toString for debugging
    @Override
    public String toString() {
        return "RoomManager{" +
                "totalRooms=" + rooms.size() +
                ", rooms=" + rooms.keySet() +
                '}';
    }
}
