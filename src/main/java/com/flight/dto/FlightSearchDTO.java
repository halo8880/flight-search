package com.flight.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class FlightSearchDTO {
	private String departureAirportCode;
	private String arrivalAirportCode;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate departureDate;
}
