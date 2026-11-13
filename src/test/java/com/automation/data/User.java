package com.automation.data;

public record User(String username, String password) {

    /** Never leak passwords into logs or reports. */
    @Override
    public String toString() {
        return "User[" + username + "]";
    }
}
