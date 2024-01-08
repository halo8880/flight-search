package com.flight.provider.provider1;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Provider1TicketRequest {
	private String departureAirportCode;
	private String arrivalAirportCode;
	private LocalDate departureDate;
}
