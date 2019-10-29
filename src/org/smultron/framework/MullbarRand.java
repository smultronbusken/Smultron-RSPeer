package org.smultron.framework;

import org.rspeer.runetek.api.commons.math.Random;

public class MullbarRand {
	private static DumbnessFactor dumbnessFactor = DumbnessFactor.NORMAL;

	public static DumbnessFactor getDumbnessFactor() {
		return dumbnessFactor;
	}

	public static enum DumbnessFactor {
		NORMAL(1, "normal"),
		FAST(0.5, "fast"),
		SLOW(2, "slow"),
		RETARD(3, "really stupid"),
		FAST_AS_FUCK_BOI(0.1, "fast as fuck boiii (not tested)");

		private double scalar;
		private String name;

		public double getScalar() {
			return scalar;
		}

		@Override
		public String toString() {
			return name + " (" + scalar + " speed multiplier)";
		}

		private DumbnessFactor(double scalar, String name) {
			this.scalar = scalar;
			this.name = name;
		}
	}

	public static double getScalar() {
		return dumbnessFactor.getScalar();
	}

	public static void setDumbnessFactor(final DumbnessFactor dumbnessFactor) {
		MullbarRand.dumbnessFactor = dumbnessFactor;
	}

	public static int nextInt(final int min, final int max) {
		return Random.nextInt((int) (dumbnessFactor.scalar * min), (int) (dumbnessFactor.scalar * max));
	}
}
