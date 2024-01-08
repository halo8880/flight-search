package com.flight.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "schedule", uniqueConstraints = @UniqueConstraint(columnNames = {"departure_airport_code", "arrival_airport_code", "provider_code"}))
public class Schedule {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String providerCode;
	private double basePrice;

	@ManyToOne
	@JoinColumn(name = "departure_airport_code")
	private Airport departureAirport;
	@ManyToOne
	@JoinColumn(name = "arrival_airport_code")
	private Airport arrivalAirport;

	public Schedule(String departureAirPortCode, String arrivalAirPortCode, String providerCode, double basePrice) {
		this.departureAirport = new Airport(departureAirPortCode);
		this.arrivalAirport = new Airport(arrivalAirPortCode);
		this.providerCode = providerCode;
		this.basePrice = basePrice;
	}
}
