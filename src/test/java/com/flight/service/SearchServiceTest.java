package com.flight.service;

import com.flight.dto.FlightSearchDTO;
import com.flight.dto.FlightSearchResponseDTO;
import com.flight.entity.Schedule;
import com.flight.provider.provider1.Provider1Client;
import com.flight.provider.provider1.Provider1TicketResponse;
import com.flight.provider.provider2.Provider2Client;
import com.flight.provider.provider2.Provider2TicketResponse;
import com.flight.repository.ScheduleRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
public class SearchServiceTest {
	@Autowired
	private SearchService searchService;
	@MockBean
	private Provider1Client Provider1Client;
	@MockBean
	private Provider2Client Provider2Client;
	@MockBean
	private ScheduleRepository scheduleRepository;
	@Autowired
	private RedisService redisService;

	@Test
	public void testSearchFlightSuccess() throws InterruptedException {
		FlightSearchDTO flightSearchDTO = new FlightSearchDTO("SGN", "HKG", LocalDate.of(3000, 1, 1));
		when(scheduleRepository
				.findByDepartureAirportCodeAndArrivalAirportCode(eq(flightSearchDTO.getDepartureAirportCode()), eq(flightSearchDTO.getArrivalAirportCode()))).thenReturn(mockSchedules());
		//wait for 2 seconds before returning the result
		when(Provider2Client.getTicket(Mockito.any(), Mockito.anyDouble())).then(invocation -> {
			Thread.sleep(3000);
			Provider2TicketResponse response = new Provider2TicketResponse();
			response.setPrice(150);
			return CompletableFuture.completedFuture(response);
		});
		when(Provider1Client.getTicket(Mockito.any(), Mockito.anyDouble())).then(invocation -> {
			Thread.sleep(1000);
			Provider1TicketResponse response = new Provider1TicketResponse();
			response.setPrice(120);
			return CompletableFuture.completedFuture(response);
		});
		String searchId = searchService.searchFlight(flightSearchDTO);
		Thread.sleep(2000);
		FlightSearchResponseDTO flightSearchResponseDTO = (FlightSearchResponseDTO) redisService.getFromRedis(searchId);
		Assertions.assertEquals(1, flightSearchResponseDTO.getSchedules().size());

		Thread.sleep(4000);
		flightSearchResponseDTO = (FlightSearchResponseDTO) redisService.getFromRedis(searchId);
		Assertions.assertEquals(2, flightSearchResponseDTO.getSchedules().size());

		Assertions.assertEquals(flightSearchDTO.getDepartureDate(), flightSearchResponseDTO.getDepartureDate());
		Assertions.assertEquals(flightSearchDTO.getArrivalAirportCode(), flightSearchResponseDTO.getSchedules().get(0).getArrivalAirportCode());
		Assertions.assertEquals(flightSearchDTO.getDepartureAirportCode(), flightSearchResponseDTO.getSchedules().get(0).getDepartureAirportCode());
	}

	private List<Schedule> mockSchedules() {
		List<Schedule> schedules = new ArrayList<>();
		schedules.add(new Schedule("SGN", "HKG", "PR1", 100));
		schedules.add(new Schedule("SGN", "HKG", "PR2", 100));
		return schedules;
	}
}
