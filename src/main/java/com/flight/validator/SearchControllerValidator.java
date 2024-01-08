package com.flight.validator;

import com.flight.dto.FlightSearchDTO;
import com.flight.repository.ScheduleRepository;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;

@Component
public class SearchControllerValidator implements Validator {
	@Autowired
	private ScheduleRepository scheduleRepository;

	@Override
	public boolean supports(Class<?> clazz) {
		return FlightSearchDTO.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		FlightSearchDTO flightSearchDTO = (FlightSearchDTO) target;
		if (StringUtils.isBlank(flightSearchDTO.getArrivalAirportCode())) {
			errors.rejectValue("arrivalAirportCode", "arrivalAirportCode.empty");
		}
		if (StringUtils.isBlank(flightSearchDTO.getDepartureAirportCode())) {
			errors.rejectValue("departureAirportCode", "departureAirportCode.empty");
		}
		if (flightSearchDTO.getDepartureDate() == null) {
			errors.rejectValue("departureDate", "departureDate.empty");
		}
		if (flightSearchDTO.getDepartureDate() != null && flightSearchDTO.getDepartureDate().isBefore(LocalDate.now())) {
			errors.rejectValue("departureDate", "departureDate.invalid");
		}
		if (StringUtils.isNotBlank(flightSearchDTO.getArrivalAirportCode()) && StringUtils.isNotBlank(flightSearchDTO.getDepartureAirportCode()) && flightSearchDTO.getArrivalAirportCode().equals(flightSearchDTO.getDepartureAirportCode())) {
			errors.rejectValue("arrivalAirportCode", "departureAirportCode.arrivalAirportCode.same");
		}
//		if (StringUtils.isNotBlank(flightSearchDTO.getArrivalAirportCode()) && !airportRepository.existsById(flightSearchDTO.getArrivalAirportCode())) {
//			errors.rejectValue("arrivalAirportCode", "arrivalAirportCode.invalid");
//		}
//		if (StringUtils.isNotBlank(flightSearchDTO.getDepartureAirportCode()) && !airportRepository.existsById(flightSearchDTO.getDepartureAirportCode())) {
//			errors.rejectValue("departureAirportCode", "departureAirportCode.invalid");
//		}
		if (StringUtils.isNotBlank(flightSearchDTO.getArrivalAirportCode()) && StringUtils.isNotBlank(flightSearchDTO.getDepartureAirportCode())
				&& !scheduleRepository.existsByDepartureAirportCodeAndArrivalAirportCode(flightSearchDTO.getDepartureAirportCode(), flightSearchDTO.getArrivalAirportCode())) {
			errors.rejectValue("arrivalAirportCode", "airportCode.notSupported");
		}
	}
}
