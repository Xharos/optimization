package fr.satie.optimization.csv;

import fr.satie.optimization.csv.data.Country;
import fr.satie.optimization.data.GraphNodal;
import fr.satie.optimization.utils.OptimizerPools;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import org.graphstream.graph.ElementNotFoundException;

/**
 * File <b>DataManager</b> located on fr.satie.optimization.csv
 * DataManager is a part of optimization.
 * <p>
 * Copyright (c) 2021 - 2021 ENS Paris Saclay SATIE.
 * <p>
 *
 * @author Valentin Burgaud (Xharos), {@literal <xharos@islandswars.fr>}
 * Created the 12/05/2021 at 16:15
 * @since 0.2
 */
public class DataManager {

	private Generators generators;
	private Networks   networks;
	private Edges      edges;
	private Loads      loads;

	public CompletableFuture<Map<Country, Double>> computeConsumption(String date) {
		return CompletableFuture.supplyAsync(() -> {
			Map<Country, Double> consu = new HashMap<>();
			getNetworks().getNodes().forEach(node -> {
				double nodeConsu = loadBusFromDate(date, node.getId());
				consu.compute(node.getCountry(), (k, v) -> v == null ? nodeConsu : v + nodeConsu);
			});
			return consu;
		}, OptimizerPools.getExecutor());
	}

	public Edges getEdges() {
		return edges;
	}

	public Generators getGenerators() {
		return generators;
	}

	public Loads getLoads() {
		return loads;
	}

	public Networks getNetworks() {
		return networks;
	}

	public GraphNodal getNode(int id) {
		return Stream.concat(getNetworks().getNodes(), getGenerators().getNodes()).filter(nod -> nod.getId() == id).findFirst().orElseThrow(ElementNotFoundException::new);
	}

	public void load(CSVType csvType, List<String[]> lines) {
		switch (csvType) {
			case NETWORKS:
				this.networks = new Networks(lines);
				break;
			case GENERATORS:
				this.generators = new Generators(lines);
				break;
			case NETWORKS_EDGES:
				this.edges = new Edges(lines);
				break;
			case LOAD:
				this.loads = new Loads(lines);
				break;
		}
	}

	public double loadBusFromDate(String dateToLoad, int busId) {
		return loads.getData(dateToLoad, busId);
	}
}
