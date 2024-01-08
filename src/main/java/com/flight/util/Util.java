package com.flight.util;

import java.util.Random;

public class Util {
	public static double randomPrice(double basePrice) {
		Random random = new Random();
		double randomFactor = 0.3 * basePrice;
		boolean addOrSubtract = random.nextBoolean();

		if (addOrSubtract) {
			return basePrice + random.nextDouble() * randomFactor;
		} else {
			return basePrice - random.nextDouble() * randomFactor;
		}
	}
}
