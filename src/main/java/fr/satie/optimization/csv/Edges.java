package fr.satie.optimization.csv;

import fr.satie.optimization.Optimizer;
import fr.satie.optimization.csv.data.Country;
import fr.satie.optimization.csv.data.EdgeLine;
import fr.satie.optimization.data.GraphNodal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.graphstream.graph.Graph;

/**
 * File <b>Edges</b> located on fr.satie.optimization.csv
 * Edges is a part of optimization.
 * <p>
 * Copyright (c) 2021 - 2021 ENS Paris Saclay SATIE.
 * <p>
 *
 * @author Valentin Burgaud (Xharos), {@literal <xharos@islandswars.fr>}
 * Created the 12/05/2021 at 16:18
 * @since 0.2
 */
public class Edges {

	private final List<EdgeLine> edges;
	private final double[][]     lineCapacity;

	public Edges(List<String[]> csv) {
		int nbCountry = Country.values().length - 1;
		this.lineCapacity = new double[nbCountry][nbCountry];
		this.edges = new ArrayList<>();
		for (String[] strings : csv)
			if (strings.length >= 1 && !strings[0].startsWith("#"))
				edges.add(new EdgeLine(strings));
		if (Optimizer.getInstance().isDebug())
			Optimizer.getLogger().debug("Successfully load {} edges from file.", edges.size());
	}

	public void computeLineCapacities() {
		Optimizer.getLogger().trace("Compute transfer limit between countries");
		List<String> countries = Arrays.stream(Country.values()).filter(c -> !c.equals(Country.KOSOVO)).map(c -> c.getName().toLowerCase(Locale.ROOT)).sorted().collect(Collectors.toList());
		Networks     networks  = Optimizer.getInstance().getManager().getNetworks();
		getNodes().forEach(edge -> {
			GraphNodal from = networks.getNode(edge.getFrom());
			GraphNodal to   = networks.getNode(edge.getTo());
			if (!from.getCountry().equals(to.getCountry())) {
				int indexFrom = countries.indexOf(from.getCountry().getName().toLowerCase(Locale.ROOT));
				int indexTo   = countries.indexOf(to.getCountry().getName().toLowerCase(Locale.ROOT));
				lineCapacity[Math.min(indexFrom, indexTo)][Math.max(indexFrom, indexTo)] += edge.getLimit();
			}
		});
		int           nbCountry = Country.values().length - 1;
		StringBuilder builder   = new StringBuilder();
		for (int i = 0; i < nbCountry; i++) {
			for (int j = 0; j < nbCountry; j++) {
				builder.append((int) lineCapacity[i][j]);
				if (j != nbCountry - 1)
					builder.append("\t");
			}
			if (i != nbCountry - 1)
				builder.append("\n");
		}
		if (Optimizer.getInstance().isDebug())
			Optimizer.getLogger().debug(builder.toString());
		String path = Generators.DATA_PATH + "line" + ".txt";
		if (!Files.exists(Paths.get(path))) {
			if (Optimizer.getInstance().isDebug())
				Optimizer.getLogger().debug("Create data file for line capacity.");
			try {
				Files.write(Paths.get(path), builder.toString().getBytes());
			} catch (Exception ex) {
				ex.printStackTrace();
				System.exit(1);
			}
		}
	}

	public void displayLine(Graph graph) {
		List<String> countries = Arrays.stream(Country.values()).filter(c -> !c.equals(Country.KOSOVO)).map(c -> c.getName().toLowerCase(Locale.ROOT)).sorted().collect(Collectors.toList());
		Networks     networks  = Optimizer.getInstance().getManager().getNetworks();
		getNodes().forEach(edge -> {
			GraphNodal from = networks.getNode(edge.getFrom());
			GraphNodal to   = networks.getNode(edge.getTo());
			if (!from.getCountry().equals(to.getCountry()))
				graph.getEdge(edge.toString()).setAttribute("ui.style", "fill-color: red;");
		});
	}

	public Stream<EdgeLine> getNodes() {
		return edges.stream();
	}

	private class LineCapacity {

		Country from;
		Country to;
	}

}
