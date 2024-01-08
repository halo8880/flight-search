package com.flight.runner;

import com.flight.entity.Airport;
import com.flight.entity.Provider;
import com.flight.entity.Schedule;
import com.flight.repository.AirportRepository;
import com.flight.repository.ProviderRepository;
import com.flight.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GenerateTestData implements CommandLineRunner {
	@Autowired
	private AirportRepository airportRepository;
	@Autowired
	private ProviderRepository providerRepository;
	@Autowired
	private ScheduleRepository scheduleRepository;

	@Override
	public void run(String... args) throws Exception {
		List<Airport> airports = new ArrayList<>();
		airports.add(new Airport("CGK", "Soekarno-Hatta International Airport"));
		airports.add(new Airport("SGN", "Saigon International Airport"));
		airports.add(new Airport("HKG", "Hongkong"));
		airports.add(new Airport("BKK", "Bangkok"));
		airportRepository.saveAll(airports);


		List<Provider> providers = new ArrayList<>();
		providers.add(new Provider("PR1", "Provider 1"));
		providers.add(new Provider("PR2", "Provider 2"));
		providers.add(new Provider("PR3", "Provider 3"));
		providerRepository.saveAll(providers);

		List<Schedule> schedules = new ArrayList<>();
		schedules.add(new Schedule("CGK", "SGN", "PR1", 100));
		schedules.add(new Schedule("SGN", "HKG", "PR1", 100));
		schedules.add(new Schedule("SGN", "HKG", "PR2", 100));
		schedules.add(new Schedule("BKK", "HKG", "PR2", 100));
		scheduleRepository.saveAll(schedules);
	}
}
