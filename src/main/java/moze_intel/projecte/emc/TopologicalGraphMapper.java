package moze_intel.projecte.emc;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import java.util.*;
import moze_intel.projecte.PECore;
import moze_intel.projecte.api.mapper.arithmetic.IValueArithmetic;
import moze_intel.projecte.api.mapper.generator.IValueGenerator;
import moze_intel.projecte.emc.collector.MappingCollector;
import moze_intel.projecte.integration.kubejs.RecipeConflictResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TopologicalGraphMapper<T, V extends Comparable<V>, A extends IValueArithmetic<V>> extends MappingCollector<T, V, A> implements IValueGenerator<T, V> {

	private final V ZERO;

	public TopologicalGraphMapper(A arithmetic) {
		super(arithmetic);
		ZERO = arithmetic.getZero();
	}

	@Override
	public Map<T, V> generateValues() {
		PECore.debugLog("TopologicalGraphMapper: Starting generation...");
		
		// 1. Base Leaf Defaulting (e3)
		for (T key : usedIn.keySet()) {
			if (!fixValueBeforeInherit.containsKey(key) && !conversionsFor.containsKey(key)) {
				fixValueBeforeInherit.put(key, arithmetic.fromLong(1));
			}
		}

		// 2. Reverse Pass for Fixed Values
		PECore.debugLog("TopologicalGraphMapper: Propagating fixed values downwards...");
		propagateFixedValuesDown();

		// 3. Build Graph for SCC
		PECore.debugLog("TopologicalGraphMapper: Building dependency graph...");
		Map<T, List<T>> graph = new HashMap<>();
		for (Map.Entry<T, Set<Conversion>> entry : conversionsFor.entrySet()) {
			T output = entry.getKey();
			for (Conversion conversion : entry.getValue()) {
				for (T ingredient : conversion.ingredientsWithAmount.keySet()) {
					graph.computeIfAbsent(ingredient, k -> new ArrayList<>()).add(output);
				}
			}
		}
		
		// Add isolated nodes
		for (T node : fixValueBeforeInherit.keySet()) {
			graph.putIfAbsent(node, new ArrayList<>());
		}

		// 4. Tarjan's SCC
		PECore.debugLog("TopologicalGraphMapper: Running Tarjan's algorithm...");
		List<Set<T>> sccs = findSCCs(graph);
		PECore.debugLog("TopologicalGraphMapper: Found {} Strongly Connected Components.", sccs.size());
		
		// 5. Reverse Topological Sort of SCCs (SCCs are already in reverse topological order from Tarjan's!)
		// Tarjan's naturally outputs SCCs in reverse topological order (leaves to roots).

		Map<T, V> values = new HashMap<>();

		// 6. Resolve values
		PECore.debugLog("TopologicalGraphMapper: Resolving values...");
		for (Set<T> scc : sccs) {
			if (scc.size() == 1) {
				T node = scc.iterator().next();
				resolveSingleNode(node, values);
			} else {
				resolveSCC(scc, values);
			}
		}

		values.putAll(fixValueAfterInherit);
		values.entrySet().removeIf(entry -> arithmetic.isFree(entry.getValue()));

		PECore.debugLog("TopologicalGraphMapper: Generation complete.");
		return values;
	}

	private void propagateFixedValuesDown() {
		boolean changed = true;
		while (changed) {
			changed = false;
			for (Map.Entry<T, Conversion> entry : overwriteConversion.entrySet()) {
				T output = entry.getKey();
				Conversion conversion = entry.getValue();
				
				// Only solve if we have exactly 1 unknown leaf ingredient
				T unknownIngredient = null;
				int unknownCount = 0;
				V knownCost = ZERO;
				
				for (Iterator<Object2IntMap.Entry<T>> iterator = Object2IntMaps.fastIterator(conversion.ingredientsWithAmount); iterator.hasNext(); ) {
					Object2IntMap.Entry<T> ingEntry = iterator.next();
					T ing = ingEntry.getKey();
					if (fixValueBeforeInherit.containsKey(ing)) {
						V ingCost = conversion.arithmeticForConversion.mul(ingEntry.getIntValue(), fixValueBeforeInherit.get(ing));
						knownCost = conversion.arithmeticForConversion.add(knownCost, ingCost);
					} else {
						unknownIngredient = ing;
						unknownCount++;
					}
				}
				
				if (unknownCount == 1 && !conversionsFor.containsKey(unknownIngredient)) { // must be a leaf
					// We can solve for unknownIngredient
					// outValue * outNumber = knownCost + unknownCost * unknownAmount + baseValue
					// So unknownCost = (outValue * outNumber - knownCost - baseValue) / unknownAmount
					
					V outValue = fixValueBeforeInherit.get(output);
					if (outValue != null) {
						V targetTotal = conversion.arithmeticForConversion.mul(conversion.outnumber, outValue);
						V remaining = conversion.arithmeticForConversion.sub(targetTotal, knownCost);
						remaining = conversion.arithmeticForConversion.sub(remaining, conversion.value);
						
						int unknownAmount = conversion.ingredientsWithAmount.getInt(unknownIngredient);
						V solvedValue = conversion.arithmeticForConversion.div(remaining, unknownAmount);
						
						if (arithmetic.isGreaterThanZero(solvedValue)) {
							V current = fixValueBeforeInherit.get(unknownIngredient);
							if (current == null || solvedValue.compareTo(current) > 0) {
								fixValueBeforeInherit.put(unknownIngredient, solvedValue);
								RecipeConflictResolver.addConflict(null, unknownIngredient.toString(), "e3.setEMC('" + unknownIngredient.toString() + "', " + solvedValue + ") // Propagated from fixed output " + output);
								changed = true;
							}
						}
					}
				}
			}
		}
	}

	private void resolveSingleNode(T node, Map<T, V> values) {
		V baseValue = fixValueBeforeInherit.get(node);
		if (baseValue != null) {
			values.put(node, baseValue);
		}
		
		V minCost = null;
		Set<Conversion> convs = conversionsFor.get(node);
		if (convs != null) {
			for (Conversion conversion : convs) {
				Conversion overwrite = overwriteConversion.get(node);
				if (overwrite != null && overwrite != conversion) continue;

				V cost = calculateCost(conversion, values);
				if (arithmetic.isGreaterThanZero(cost) || conversion.arithmeticForConversion.isFree(cost)) {
					if (minCost == null || cost.compareTo(minCost) < 0) {
						minCost = cost;
					}
				}
			}
		}

		if (minCost != null) {
			V current = values.get(node);
			if (current == null || minCost.compareTo(current) < 0) {
				if (baseValue == null || minCost.compareTo(baseValue) < 0) {
					values.put(node, minCost);
				}
			}
		}
	}

	private void resolveSCC(Set<T> scc, Map<T, V> values) {
		// Localized Bellman-Ford
		boolean changed = true;
		int iterations = 0;
		int maxIterations = scc.size() + 2;
		
		Map<T, V> localValues = new HashMap<>();
		for (T node : scc) {
			V base = fixValueBeforeInherit.get(node);
			if (base != null) localValues.put(node, base);
		}
		
		while (changed && iterations < maxIterations) {
			changed = false;
			iterations++;
			
			for (T node : scc) {
				Set<Conversion> convs = conversionsFor.get(node);
				if (convs == null) continue;
				
				for (Conversion conversion : convs) {
					Conversion overwrite = overwriteConversion.get(node);
					if (overwrite != null && overwrite != conversion) continue;

					// Mix of global values and local values for ingredients
					V cost = calculateCostWithLocal(conversion, values, localValues);
					
					if (arithmetic.isGreaterThanZero(cost) || conversion.arithmeticForConversion.isFree(cost)) {
						V current = localValues.get(node);
						if (current == null || cost.compareTo(current) < 0) {
							localValues.put(node, cost);
							changed = true;
						}
					}
				}
			}
		}
		
		// If still changing after |V| iterations, it's a lossy/exploitative cycle
		if (changed) {
			for (T node : scc) {
				RecipeConflictResolver.addConflict(null, node.toString(), "// WARNING: Lossy or exploitative cycle detected in SCC containing this item.");
				PECore.LOGGER.warn("Lossy or exploitative cycle detected in SCC containing {}", node);
			}
		}
		
		values.putAll(localValues);
	}

	private V calculateCost(Conversion conversion, Map<T, V> values) {
		V total = conversion.value;
		boolean allFree = true;
		boolean hasPos = false;
		
		for (Iterator<Object2IntMap.Entry<T>> iterator = Object2IntMaps.fastIterator(conversion.ingredientsWithAmount); iterator.hasNext(); ) {
			Object2IntMap.Entry<T> entry = iterator.next();
			T ing = entry.getKey();
			V val = values.get(ing);
			if (val == null) return ZERO; // Unresolved ingredient
			
			int amt = entry.getIntValue();
			V ingCost = conversion.arithmeticForConversion.mul(amt, val);
			
			if (arithmetic.isZero(ingCost)) return ZERO;
			
			total = conversion.arithmeticForConversion.add(total, ingCost);
			if (arithmetic.isGreaterThanZero(ingCost) && amt > 0) hasPos = true;
			allFree = false;
		}
		
		if (allFree || (hasPos && arithmetic.isLessThanEqualZero(total))) return conversion.arithmeticForConversion.getFree();
		return conversion.arithmeticForConversion.div(total, conversion.outnumber);
	}

	private V calculateCostWithLocal(Conversion conversion, Map<T, V> globalValues, Map<T, V> localValues) {
		V total = conversion.value;
		boolean allFree = true;
		boolean hasPos = false;
		
		for (Iterator<Object2IntMap.Entry<T>> iterator = Object2IntMaps.fastIterator(conversion.ingredientsWithAmount); iterator.hasNext(); ) {
			Object2IntMap.Entry<T> entry = iterator.next();
			T ing = entry.getKey();
			V val = localValues.get(ing);
			if (val == null) val = globalValues.get(ing);
			if (val == null) return ZERO;
			
			int amt = entry.getIntValue();
			V ingCost = conversion.arithmeticForConversion.mul(amt, val);
			
			if (arithmetic.isZero(ingCost)) return ZERO;
			
			total = conversion.arithmeticForConversion.add(total, ingCost);
			if (arithmetic.isGreaterThanZero(ingCost) && amt > 0) hasPos = true;
			allFree = false;
		}
		
		if (allFree || (hasPos && arithmetic.isLessThanEqualZero(total))) return conversion.arithmeticForConversion.getFree();
		return conversion.arithmeticForConversion.div(total, conversion.outnumber);
	}


	// Tarjan's Algorithm Implementation
	private int time = 0;
	private List<Set<T>> findSCCs(Map<T, List<T>> graph) {
		Map<T, Integer> disc = new HashMap<>();
		Map<T, Integer> low = new HashMap<>();
		Set<T> inStack = new HashSet<>();
		Stack<T> stack = new Stack<>();
		List<Set<T>> sccs = new ArrayList<>();
		time = 0;

		for (T u : graph.keySet()) {
			if (!disc.containsKey(u)) {
				tarjanDFS(u, graph, disc, low, stack, inStack, sccs);
			}
		}
		return sccs;
	}

	private void tarjanDFS(T u, Map<T, List<T>> graph, Map<T, Integer> disc, Map<T, Integer> low, Stack<T> stack, Set<T> inStack, List<Set<T>> sccs) {
		disc.put(u, time);
		low.put(u, time);
		time++;
		stack.push(u);
		inStack.add(u);

		List<T> neighbors = graph.get(u);
		if (neighbors != null) {
			for (T v : neighbors) {
				if (!disc.containsKey(v)) {
					tarjanDFS(v, graph, disc, low, stack, inStack, sccs);
					low.put(u, Math.min(low.get(u), low.get(v)));
				} else if (inStack.contains(v)) {
					low.put(u, Math.min(low.get(u), disc.get(v)));
				}
			}
		}

		if (low.get(u).equals(disc.get(u))) {
			Set<T> scc = new LinkedHashSet<>();
			T w;
			do {
				w = stack.pop();
				inStack.remove(w);
				scc.add(w);
			} while (!w.equals(u));
			sccs.add(scc);
		}
	}
}
