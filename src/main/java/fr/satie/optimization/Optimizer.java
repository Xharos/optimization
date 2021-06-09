package fr.satie.optimization;

import fr.satie.optimization.csv.CSVType;
import fr.satie.optimization.csv.CSVUtils;
import fr.satie.optimization.csv.DataManager;
import fr.satie.optimization.csv.Generators;
import fr.satie.optimization.csv.data.Country;
import fr.satie.optimization.data.LiteDatabase;
import fr.satie.optimization.graph.GraphDisplay;
import fr.satie.optimization.py.PyFile;
import fr.satie.optimization.py.PyHandler;
import fr.satie.optimization.utils.OptimizerPools;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.PriorityQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graphstream.graph.Graph;

/**
 * File <b>Optimizer</b> located on fr.satie.optimization
 * Optimizer is a part of optimization.
 * <p>
 * Copyright (c) 2021 - 2021 ENS Paris Saclay SATIE.
 * <p>
 *
 * @author Valentin Burgaud (Xharos), {@literal <xharos@islandswars.fr>}
 * Created the 11/05/2021 at 08:26
 * @since 0.1
 */
public class Optimizer {

	private static final Logger       LOGGER = LogManager.getLogger(Optimizer.class);
	private static       Optimizer    instance;
	private final        DataManager  manager;
	private final        boolean      debug;
	private final        LiteDatabase database;
	private              Graph        graph;
	private volatile     boolean      graphInitialize;

	public Optimizer(boolean gui, boolean debug, boolean generator, String date, String algo, int worker, String start) {
		if (instance == null)
			instance = this;
		this.manager = new DataManager();
		this.debug = debug;
		LOGGER.info("Start optimizer programm with arguments : ");
		LOGGER.info(" - gui        = {}", gui);
		LOGGER.info(" - date       = {}", date);
		LOGGER.info(" - algo       = {}", algo);
		LOGGER.info(" - start      = {}", start);
		LOGGER.info(" - debug      = {}", debug);
		LOGGER.info(" - worker     = {}", worker);
		LOGGER.info(" - generators = {}", generator);
		if (debug)
			LOGGER.debug("Some results will be print on the terminal");
		if (gui) {
			System.setProperty("org.graphstream.ui", "swing");
			LOGGER.info("Graphstream UI will use swing as graphic implementation.");
		} else
			LOGGER.info("Terminal only usages.");
		loadCSV();
		if (gui) {
			LOGGER.info("Start graph-stream thread...");
			CompletableFuture.runAsync(() -> {
				new GraphDisplay(generator);
				LOGGER.trace("UI event handled asynchronously...");
			}, OptimizerPools.getExecutor());
		}
		if (algo.equals("admm")) {
			manager.getEdges().computeLineCapacities();
			if (gui) {
				while (!graphInitialize) {
				}
				manager.getEdges().displayLine(graph);
			}
		}
		if (date.equals("all")) {
			LOGGER.info("Create database file");
			database = new LiteDatabase(algo);
			database.connect();
			database.setup(algo);
		} else
			database = null;
		LOGGER.info("Start calculus thread...");
		LOGGER.info("Compute country cost function");
		CompletableFuture<Void> cap = manager.getGenerators().perCountryCapacity().thenRunAsync(() -> manager.getGenerators().computeCountryInterp1D(), OptimizerPools.getExecutor());
		while (!cap.isDone()) {
		}
		if (!date.equals("all")) {
			CompletableFuture<String[]> result = computeConsumption(algo, date);
			while (!result.isDone()) {

			}
			try {
				LOGGER.trace("Get interpolation result from {} :\n{}", algo, result.get());
				String[]     powerPerCountry = result.get()[1].split("\n");
				List<String> countries       = Arrays.stream(Country.values()).filter(c -> !c.equals(Country.KOSOVO)).map(c -> c.getName().toLowerCase(Locale.ROOT)).sorted().collect(Collectors.toList());
				if (gui && generator)
					if (!powerPerCountry[0].equals("crash") && !powerPerCountry[0].equals("failed"))
						for (int i = 0; i < powerPerCountry.length; i++)
							manager.getGenerators().getCost(Country.getEnum(countries.get(i))).getGeneratorBelow(Double.parseDouble(powerPerCountry[i])).forEach((id, color) -> {
								graph.getNode(String.valueOf(id)).setAttribute("ui.style", "size: 6px, 6px; " +
										"shape: box; " +
										"fill-color: " + color.getColor() + ";");
							});
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else
			computeAllConsumptions(worker, algo, start);
		if (!gui)
			System.exit(0);
	}

	public static Optimizer getInstance() {
		return instance;
	}

	public static Logger getLogger() {
		return LOGGER;
	}

	public void computeAllConsumptions(int workerSize, String algo, String start) {
		CompletableFuture<Void> result = CompletableFuture.runAsync(() -> {
			try {
				List<CompletableFuture<String[]>> work  = new CopyOnWriteArrayList<>();
				PriorityQueue<String>             queue = new PriorityQueue<>(getManager().getLoads().getDates());
				while (work.size() < workerSize && queue.size() > 0) {
					String date = queue.poll();
					if (date == null)
						break;
					if (!start.equals("2012-01-01 00:00:00"))
						if (date.compareTo(start) < 0)
							continue;
					work.add(computeConsumption(algo, date));
					LOGGER.trace("Add future at date {} to workers.", date);
					if (work.size() == workerSize) {
						while (work.stream().noneMatch(CompletableFuture::isDone)) {

						}
						work.forEach(fut -> {
							if (fut.isDone()) {
								String[] re = null;
								try {
									re = fut.get();
								} catch (Exception e) {
									LOGGER.error("Concurent exception while retrieving optimization result..");
									e.printStackTrace();
									System.exit(1);
								}
								if (re != null)
									if (re[1].contains("Values in x were outside")) {
										LOGGER.warn("Scipy : values in x were outside bounds, try again for date {}", re[0]);
										queue.add(re[0]);
									} else if (re[1].equals("crash")) {
										LOGGER.error("Python process crash on date {}", re[0]);
										queue.add(re[0]);
									} else if (re[1].equals("failed")) {
										LOGGER.error("Scipy optimize failed to converge on date {}", re[0]);
										queue.add(re[0]);
									} else
										database.post(re[1], algo, re[0].replace(" ", "-").replace(":", "-"));
								work.remove(fut);
							}
						});
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, OptimizerPools.getExecutor());
		while (!result.isDone()) {
		}
	}

	public CompletableFuture<String[]> computeConsumption(String algo, String date) {
		LOGGER.info("Compute buses consumption at date {}", date);
		return manager.computeConsumption(date).thenApplyAsync((map) -> {
			StringBuilder           data       = new StringBuilder();
			DecimalFormat           format     = new DecimalFormat("0.#");
			AtomicReference<Double> totalConsu = new AtomicReference<>((double) 0);
			String[]                countries  = new String[map.size()];
			AtomicInteger           i          = new AtomicInteger(0);
			List<String>            test       = map.keySet().stream().map(c -> c.getName().toLowerCase(Locale.ROOT)).sorted().collect(Collectors.toList());
			test.forEach(c -> {
				countries[i.getAndIncrement()] = c;
				double consu = map.get(Country.getEnum(c));
				data.append(format.format(consu)).append("\n");
				totalConsu.updateAndGet(v -> v + consu);
				if (debug)
					LOGGER.debug("Country {} use {} MW at this date.", c, consu);
			});
			if (debug)
				LOGGER.debug("Total consumption in Europe is : {} MW at date : {}", totalConsu.get(), date);
			String path = Generators.DATA_PATH + date.replace(" ", "-").replace(":", "-") + ".txt";
			if (!Files.exists(Paths.get(path))) {
				if (debug)
					LOGGER.debug("Create data file for date {}", date);
				try {
					Files.write(Paths.get(path), data.toString().getBytes());
				} catch (Exception ex) {
					ex.printStackTrace();
					System.exit(1);
				}
			}
			return countries;
		}, OptimizerPools.getExecutor()).thenComposeAsync(countries -> {
			String[] args = new String[countries.length + 1];
			args[0] = date.replace(" ", "-").replace(":", "-");
			System.arraycopy(countries, 0, args, 1, countries.length);
			StringBuilder builder = new StringBuilder();
			for (String arg : args) {
				builder.append(arg).append(" ");
			}
			LOGGER.debug(builder.toString());
			return PyHandler.exec(algo.equals("scipy") ? PyFile.SCIPY_OPTIMIZE_MARKET : PyFile.ADMM_MARKET, args);
		}, OptimizerPools.getExecutor()).thenApply(result -> {
			try {
				String path = Generators.DATA_PATH + date.replace(" ", "-").replace(":", "-") + ".txt";
				Files.delete(Paths.get(path));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return new String[]{date, result};
		});
	}

	public Graph getGraph() {
		return graph;
	}

	public DataManager getManager() {
		return manager;
	}

	public boolean isDebug() {
		return debug;
	}

	public void linkGraph(Graph graph) {
		this.graph = graph;
		graphInitialize = true;
	}

	public void loadCSV() {
		long start = System.currentTimeMillis();
		CompletableFuture<Void> loadCSV = CompletableFuture.supplyAsync(CSVUtils::loadAllFiles, OptimizerPools.getExecutor()).thenAcceptAsync(readers -> {
			try {
				for (CSVType csvType : readers.keySet()) {
					LOGGER.trace("Successfully load {} file.", csvType);
					manager.load(csvType, readers.get(csvType));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, OptimizerPools.getExecutor());
		while (!loadCSV.isDone()) {
		}
		LOGGER.trace("Compute all CSV in {} s.", String.format("%.2f", (System.currentTimeMillis() - start) / 1000.0));
	}
}
