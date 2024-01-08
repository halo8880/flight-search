package com.flight.provider.provider2;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Provider2TicketRequest {
	private String departureAirportCode;
	private String arrivalAirportCode;
	private LocalDate departureDate;
}
