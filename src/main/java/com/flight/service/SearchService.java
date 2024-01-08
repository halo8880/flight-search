package com.flight.service;

import com.flight.dto.FlightSearchDTO;
import com.flight.dto.FlightSearchResponseDTO;
import com.flight.dto.ScheduleDTO;
import com.flight.entity.Schedule;
import com.flight.provider.ClientAdapter;
import com.flight.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Service
public class SearchService {
	@Value("${flight.app.maxThreads}")
	private int maxThreads;
	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	private RedisService redisService;
	@Autowired
	private ScheduleRepository scheduleRepository;

	public String searchFlight(FlightSearchDTO flightSearchDTO) {
		String searchId = UUID.randomUUID().toString();
		List<Schedule> schedules = scheduleRepository.findByDepartureAirportCodeAndArrivalAirportCode(flightSearchDTO.getDepartureAirportCode(), flightSearchDTO.getArrivalAirportCode());
		Map<String, List<Schedule>> schedulesByProvider = schedules.stream().collect(Collectors.groupingBy(Schedule::getProviderCode));

		Executor executor = Executors.newFixedThreadPool(10);
		Map<String, ClientAdapter> beansOfType = applicationContext.getBeansOfType(ClientAdapter.class);
		ReentrantLock lock = new ReentrantLock();
		for (ClientAdapter adapter : beansOfType.values()) {
			executor.execute(() -> {
				try {
					System.out.println("Searching for provider " + adapter.getProviderCode());
					if (!schedulesByProvider.containsKey(adapter.getProviderCode())) {
						System.out.println("No schedule found for provider " + adapter.getProviderCode());
						return;
					}
					System.out.println("Found " + adapter.getProviderCode());
					for (Schedule schedule : schedulesByProvider.get(adapter.getProviderCode())) {
						ScheduleDTO scheduleDTO = new ScheduleDTO();
						scheduleDTO.setDepartureAirportCode(schedule.getDepartureAirport().getCode());
						scheduleDTO.setArrivalAirportCode(schedule.getArrivalAirport().getCode());
						scheduleDTO.setProviderCode(schedule.getProviderCode());
						scheduleDTO.setPrice(adapter.getFlightPrice(flightSearchDTO, schedule.getBasePrice()));
						scheduleDTO.setSequenceNumber(stimulateGenerateSequenceNumberService());
						try {
							if (lock.tryLock(5000, TimeUnit.MILLISECONDS)) {
								FlightSearchResponseDTO cachedData = (FlightSearchResponseDTO) redisService.getFromRedis(searchId);

								if (cachedData == null) {
									System.out.println("No cached data found for searchId " + searchId);
									cachedData = new FlightSearchResponseDTO();
									cachedData.setSearchId(searchId);
									cachedData.setDepartureDate(flightSearchDTO.getDepartureDate());
								}
								cachedData.getSchedules().add(scheduleDTO);
								System.out.println("Saving to redis " + searchId + ", size=" + cachedData.getSchedules().size());
								redisService.saveToRedis(searchId, cachedData);
							} else {
								System.out.println("Failed to acquire lock for searchId " + searchId + ", providerCode=" + adapter.getProviderCode());
							}
						} finally {
							lock.unlock();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}
		return searchId;
	}

	/**
	 * for distributed systems, this method should be replaced with a service that generate sequence number
	 */
	private long stimulateGenerateSequenceNumberService() {
		return System.currentTimeMillis();
	}

	public FlightSearchResponseDTO pollSearchResult(String searchId, long afterSequenceNumber) {
		FlightSearchResponseDTO cachedResult = (FlightSearchResponseDTO) redisService.getFromRedis(searchId);
		if (cachedResult != null) {
			List<ScheduleDTO> schedules = cachedResult.getSchedules().stream()
					.filter(scheduleDTO -> scheduleDTO.getSequenceNumber() > afterSequenceNumber)
					.sorted(Comparator.comparingLong(ScheduleDTO::getSequenceNumber))
					.collect(Collectors.toList());
			cachedResult.setSchedules(schedules);
			long lastSequenceNumber = schedules.isEmpty() ? Integer.MIN_VALUE : schedules.get(schedules.size() - 1).getSequenceNumber();
			cachedResult.setLastSequenceNumber(lastSequenceNumber);
		}
		return cachedResult;
	}
}
