package fr.satie.optimization.csv;

import fr.satie.optimization.Optimizer;
import fr.satie.optimization.csv.data.Country;
import fr.satie.optimization.csv.data.Generator;
import fr.satie.optimization.data.GraphNodal;
import fr.satie.optimization.data.GraphNode;
import fr.satie.optimization.data.ProducerCostFunction;
import fr.satie.optimization.py.PyFile;
import fr.satie.optimization.py.PyHandler;
import fr.satie.optimization.utils.OptimizerPools;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * File <b>Generators</b> located on fr.satie.optimization.csv
 * Generators is a part of optimization.
 * <p>
 * Copyright (c) 2021 - 2021 ENS Paris Saclay SATIE.
 * <p>
 *
 * @author Valentin Burgaud (Xharos), {@literal <xharos@islandswars.fr>}
 * Created the 11/05/2021 at 14:57
 * @since 0.1
 */
public class Generators implements GraphNode {

	public static final String                             DATA_PATH = "data/";
	private final       Map<Country, ProducerCostFunction> costs;
	private final       List<GraphNodal>                   generators;

	public Generators(List<String[]> csv) {
		this.costs = new HashMap<>();
		this.generators = new ArrayList<>();
		for (String[] strings : csv)
			if (strings.length >= 1 && !strings[0].startsWith("#"))
				generators.add(new Generator(strings));
		if (Optimizer.getInstance().isDebug())
			Optimizer.getLogger().debug("Successfully load {} generators from file.", generators.size());
	}

	public void computeCountryInterp1D() {
		File file = new File(DATA_PATH);
		file.mkdir();
		Set<CompletableFuture<Void>> workers = new HashSet<>();
		costs.forEach((key, value) -> workers.add(CompletableFuture.runAsync(() -> {
			String country = key.getName().toLowerCase(Locale.ROOT);
			if (Optimizer.getInstance().isDebug())
				Optimizer.getLogger().debug("Write country's generators cost to file {}.txt", country);
			try {
				StringBuilder data = value.getCountryDataBuilder();
				Files.write(Paths.get(DATA_PATH + country + ".txt"), data.toString().getBytes());
			} catch (Exception ex) {
				ex.printStackTrace();
				System.exit(1);
			}
		}, OptimizerPools.getExecutor()).thenComposeAsync(unused -> {
			String country = key.getName().toLowerCase(Locale.ROOT);
			return PyHandler.exec(PyFile.NP_POLY_INTERP1D, country);
		}, OptimizerPools.getExecutor()).thenAcceptAsync(re -> {
			String   country = key.getName().toLowerCase(Locale.ROOT);
			String[] parts   = re.split("\n");
			if (Optimizer.getInstance().isDebug())
				Optimizer.getLogger().debug("Get result from py for country " + country + ":\n{}", re);
			value.storeCostFunction(parts[0], parts[1]);
			try {
				int      count = 0;
				String[] coefs = new String[2];
				String   poly  = null;
				for (int i = 0; i < parts.length; i++) {
					if (!parts[i].startsWith("s") && count < 2) {
						coefs[coefs[0] == null ? 0 : 1] = parts[i];
						count += 1;
					}
					if (i > 0 && count == 2)
						poly = parts[i];
				}
				Files.write(Paths.get(DATA_PATH + country + "-inter" + ".txt"), String.join("\n", coefs).getBytes());
				Files.write(Paths.get(DATA_PATH + country + "-poly" + ".txt"), poly.getBytes());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, OptimizerPools.getExecutor())));
		while (workers.stream().allMatch(CompletableFuture::isDone)) {
		}
		/*try {
			Files.walk(file.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
		} catch (IOException e) {
			e.printStackTrace();
		}*/
	}

	public ProducerCostFunction getCost(Country country) {
		return costs.get(country);
	}

	@Override
	public GraphNodal getNode(int index) {
		return generators.get(index);
	}

	@Override
	public Stream<GraphNodal> getNodes() {
		return generators.stream();
	}

	public CompletableFuture<Void> perCountryCapacity() {
		return CompletableFuture.runAsync(() -> {
			getNodes().forEach(node -> {
				if (!costs.containsKey(node.getCountry()))
					costs.put(node.getCountry(), new ProducerCostFunction());
				costs.get(node.getCountry()).addGenerator((Generator) node);
			});
			costs.keySet().forEach(country -> {
				if (Optimizer.getInstance().isDebug())
					Optimizer.getLogger().debug("Sort country cost function list for {}.", country.getName());
				costs.get(country).sort();
			});
		}, OptimizerPools.getExecutor());
	}
}
