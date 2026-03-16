package com.example.teatro.model;

/**
 * Ticket
 * Simple data model representing a booked ticket.
 * Used by MyTicketsAdapter to populate the RecyclerView in MyTicketsActivity.
 */
public class Ticket {

    private final String bookingRef;   // e.g. "TT-42314"
    private final String eventName;    // e.g. "Hamlet"
    private final String eventDate;    // e.g. "15 Jul 2025 · 20:00"
    private final String seats;        // e.g. "A-3  A-4"
    private final String totalPrice;   // e.g. "37.00 €"

    public Ticket(String bookingRef, String eventName, String eventDate,
                  String seats, String totalPrice) {
        this.bookingRef  = bookingRef;
        this.eventName   = eventName;
        this.eventDate   = eventDate;
        this.seats       = seats;
        this.totalPrice  = totalPrice;
    }

    public String getBookingRef()  { return bookingRef; }
    public String getEventName()   { return eventName; }
    public String getEventDate()   { return eventDate; }
    public String getSeats()       { return seats; }
    public String getTotalPrice()  { return totalPrice; }
}