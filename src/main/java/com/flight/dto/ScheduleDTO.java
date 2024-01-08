package com.flight.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ScheduleDTO implements Serializable {
	private String departureAirportCode;
	private String arrivalAirportCode;
	private String providerCode;
	private double price;
	private long sequenceNumber;
}
