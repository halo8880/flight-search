package com.flight.controller;

import com.flight.dto.FlightSearchDTO;
import com.flight.dto.FlightSearchResponseDTO;
import com.flight.service.SearchService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
public class SearchController {
	@Autowired
	@Qualifier("searchControllerValidator")
	private Validator searchControllerValidator;
	@Autowired
	private SearchService searchService;
	WebDataBinder binder;

	@InitBinder
	private void initBinder(WebDataBinder binder) {
		this.binder = binder;
		binder.setValidator(searchControllerValidator);
	}

	@GetMapping("search")
	public ResponseEntity searchFlights(
			@RequestParam String departureAirportCode,
			@RequestParam String arrivalAirportCode,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate departureDate
	) {
		FlightSearchDTO flightSearchDTO = new FlightSearchDTO(departureAirportCode, arrivalAirportCode, departureDate);
		Errors errors = this.binder.getValidator().validateObject(flightSearchDTO);
		if (errors.hasErrors()) {
			return ResponseEntity.badRequest().body(errors.getAllErrors());
		}
		String rs = searchService.searchFlight(flightSearchDTO);
		return ResponseEntity.ok().body(rs);
	}

	@GetMapping("poll")
	public ResponseEntity<FlightSearchResponseDTO> pollSearchResult(@RequestParam String searchId,
																	@RequestParam(required = false) Long afterSequenceNumber) {
		if (afterSequenceNumber == null) {
			afterSequenceNumber = Long.MIN_VALUE;
		}
		FlightSearchResponseDTO flightSearchResponseDTO = searchService.pollSearchResult(searchId, afterSequenceNumber);
		return ResponseEntity.ok().body(flightSearchResponseDTO);
	}
}