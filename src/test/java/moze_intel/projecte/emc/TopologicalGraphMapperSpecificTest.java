package moze_intel.projecte.emc;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import moze_intel.projecte.api.mapper.arithmetic.IValueArithmetic;
import moze_intel.projecte.emc.arithmetic.HiddenBigFractionArithmetic;
import moze_intel.projecte.emc.collector.LongToBigFractionCollector;
import moze_intel.projecte.emc.generator.BigFractionToLongGenerator;
import org.apache.commons.math3.fraction.BigFraction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import moze_intel.projecte.utils.EMCHelper;
import java.util.List;

@DisplayName("Test Topological graph mapper specific features")
class TopologicalGraphMapperSpecificTest {

	private BigFractionToLongGenerator<String> valueGenerator;
	private moze_intel.projecte.api.mapper.collector.IExtendedMappingCollector<String, Long, IValueArithmetic<BigFraction>> mappingCollector;

	@BeforeEach
	void setup() {
		TopologicalGraphMapper<String, BigFraction, IValueArithmetic<BigFraction>> mapper = new TopologicalGraphMapper<>(new HiddenBigFractionArithmetic());
		valueGenerator = new BigFractionToLongGenerator<>(mapper);
		mappingCollector = new LongToBigFractionCollector<>(mapper);
	}

	@Test
	@DisplayName("Test Base Leaf Defaulting (e3)")
	void testBaseLeafDefaulting() {
		// "leaf" has no conversion and no fixed value. It should default to 1.
		mappingCollector.addConversion(1, "output", List.of("leaf", "leaf"));
		
		Object2LongMap<String> values = valueGenerator.generateValues();
		Assertions.assertEquals(1, values.getLong("leaf"));
		Assertions.assertEquals(2, values.getLong("output"));
	}


}
