package com.flight.provider;

import com.flight.dto.FlightSearchDTO;

import java.util.concurrent.ExecutionException;

public interface ClientAdapter {
	String getProviderCode();
	double getFlightPrice(FlightSearchDTO flightSearchDTO, double basePrice) throws ExecutionException, InterruptedException;
}
