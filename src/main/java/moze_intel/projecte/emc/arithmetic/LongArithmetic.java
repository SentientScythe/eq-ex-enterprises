package moze_intel.projecte.emc.arithmetic;

import moze_intel.projecte.api.mapper.arithmetic.IValueArithmetic;
import moze_intel.projecte.config.MappingConfig;

public class LongArithmetic implements IValueArithmetic<Long> {

	@Override
	public boolean isLessThanZero(Long value) {
		return value < 0;
	}

	@Override
	public boolean isLessThanEqualZero(Long value) {
		return value <= 0;
	}

	@Override
	public boolean isGreaterThanZero(Long value) {
		return value > 0;
	}

	@Override
	public boolean isGreaterThanEqualZero(Long value) {
		return value >= 0;
	}

	@Override
	public Long getZero() {
		return 0L;
	}

	@Override
	public Long fromLong(long value) {
		return value;
	}

	@Override
	public Long add(Long a, Long b) {
		return a + b;
	}

	@Override
	public Long sub(Long a, Long b) {
		return a - b;
	}

	@Override
	public Long mul(long a, Long b) {
		return a * b;
	}

	@Override
	public Long div(Long a, long b) {
		if (b == 0) {
			return 0L; // Fallback for division by zero
		}
		
		boolean ceil = MappingConfig.roundingModeCeil();
		if (ceil) {
			return (a + b - 1) / b;
		} else {
			return a / b; // Floor by default due to integer division
		}
	}

	@Override
	public Long getFree() {
		return Long.MIN_VALUE;
	}

	@Override
	public boolean isFree(Long value) {
		return value != null && value == Long.MIN_VALUE;
	}
}
