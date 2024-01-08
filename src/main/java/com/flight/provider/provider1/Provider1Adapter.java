package com.flight.provider.provider1;

import com.flight.dto.FlightSearchDTO;
import com.flight.provider.ClientAdapter;
import com.flight.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
public class Provider1Adapter implements ClientAdapter {
	@Autowired
	private Provider1Client provider1Client;
	@Autowired
	private RedisService redisService;

	@Override
	public String getProviderCode() {
		return "PR1";
	}

	@Override
	public double getFlightPrice(FlightSearchDTO flightSearchDTO, double basePrice) throws ExecutionException, InterruptedException {
		Provider1TicketRequest request = new Provider1TicketRequest();
		return provider1Client.getTicket(request, basePrice).get().getPrice();
	}
}
