package com.flight.repository;

import com.flight.entity.Airport;
import com.flight.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {
	boolean existsByDepartureAirportCodeAndArrivalAirportCode(String departureAirportCode, String arrivalAirportCode);
	List<Schedule> findByDepartureAirportCodeAndArrivalAirportCode(String departureAirportCode, String arrivalAirportCode);
}
