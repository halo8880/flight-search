package com.flight.provider.provider2;

import com.flight.dto.FlightSearchDTO;
import com.flight.provider.ClientAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
public class Provider2Adapter implements ClientAdapter {
	@Autowired
	private Provider2Client provider2Client;

	@Override
	public String getProviderCode() {
		return "PR2";
	}

	@Override
	public double getFlightPrice(FlightSearchDTO flightSearchDTO, double basePrice) throws ExecutionException, InterruptedException {
		Provider2TicketRequest request = new Provider2TicketRequest();
		return provider2Client.getTicket(request, basePrice).get().getPrice();
	}
}
