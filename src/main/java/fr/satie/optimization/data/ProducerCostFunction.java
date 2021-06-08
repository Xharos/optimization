package fr.satie.optimization.data;

import fr.satie.optimization.csv.data.Generator;
import fr.satie.optimization.graph.GeneratorColor;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * File <b>ProducerCostFunction</b> located on fr.satie.optimization.data
 * ProducerCostFunction is a part of optimization.
 * <p>
 * Copyright (c) 2021 - 2021 ENS Paris Saclay SATIE.
 * <p>
 *
 * @author Valentin Burgaud (Xharos), {@literal <xharos@islandswars.fr>}
 * Created the 12/05/2021 at 17:44
 * @since 0.2
 */
public class ProducerCostFunction {

	private Map<Double, NodeCost> costs;
	private double                countryTotalCapacity = 0;//MW
	private String                x;
	private String                y;

	public ProducerCostFunction() {
		this.costs = new HashMap<>();
	}

	public void addGenerator(Generator gen) {
		this.countryTotalCapacity += gen.getCapacity();
		if (costs.containsKey(gen.getLincost())) {
			NodeCost nc = costs.get(gen.getLincost());
			nc.capacities += gen.getCapacity();
			nc.nodesId.add(gen.getId());
		}
		NodeCost nc = new NodeCost();
		nc.capacities = gen.getCapacity();
		nc.nodesId.add(gen.getId());
		costs.put(gen.getLincost(), nc);
	}

	public StringBuilder getCountryDataBuilder() {
		StringBuilder builder = new StringBuilder();
		DecimalFormat format  = new DecimalFormat("0.#");
		costs.forEach((linCost, capacity) -> {
			builder.append(format.format(linCost)).append('\t').append(format.format(capacity.capacities)).append('\n');
		});
		return builder;
	}

	public Map<Integer, GeneratorColor> getGeneratorBelow(double power) {
		Map<Integer, GeneratorColor>  generators = new HashMap<>();
		AtomicReference<List<Double>> cumulative = new AtomicReference<>(new ArrayList<>());
		costs.forEach((k, v) -> {
			List<Double> total = cumulative.updateAndGet(v1 -> {
				v1.add(v.capacities);
				return v1;
			});
			double sum = total.stream().mapToDouble(d -> d).sum();
			if (sum < power)
				v.nodesId.forEach(id -> generators.put(id, GeneratorColor.GREEN));
			else if (sum > power && sum - total.get(total.size() - 1) < power)
				v.nodesId.forEach(id -> generators.put(id, GeneratorColor.ORANGE));
			else
				v.nodesId.forEach(id -> generators.put(id, GeneratorColor.RED));
		});
		return generators;
	}

	public void sort() {
		costs = new TreeMap<>(costs);
	}

	/*
	 * set of python points, x in MW and y in euro
	 */
	public void storeCostFunction(String x, String y) {
		this.x = x;
		this.y = y;
	}

	private static class NodeCost {

		List<Integer> nodesId = new ArrayList<>();
		double        capacities;
	}
}
