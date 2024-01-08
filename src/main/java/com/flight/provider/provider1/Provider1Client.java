package com.flight.provider.provider1;

import com.flight.util.Util;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class Provider1Client {
	@Async
	public CompletableFuture<Provider1TicketResponse> getTicket(Provider1TicketRequest request, double basePrice) {
		Provider1TicketResponse response = new Provider1TicketResponse();
		response.setPrice(Util.randomPrice(basePrice));
		stimulateSlowNetwork();
		return CompletableFuture.completedFuture(response);
	}

	private static void stimulateSlowNetwork() {
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
