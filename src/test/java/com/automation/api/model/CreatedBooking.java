package com.automation.api.model;

/** Response body of POST /booking. */
public record CreatedBooking(int bookingid, Booking booking) {
}
