package com.flight.provider.provider2;

import com.flight.util.Util;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class Provider2Client {
	@Async
	public CompletableFuture<Provider2TicketResponse> getTicket(Provider2TicketRequest request, double basePrice) {
		Provider2TicketResponse response = new Provider2TicketResponse();
		response.setPrice(Util.randomPrice(basePrice));
		stimulateSlowNetwork();
		return CompletableFuture.completedFuture(response);
	}

	private static void stimulateSlowNetwork() {
		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
