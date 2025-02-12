package com.himedia.luckydokiapi.exception;

public class NotAccessChatRoom extends RuntimeException {
    public NotAccessChatRoom(String message) {
        super(message);
    }
}
