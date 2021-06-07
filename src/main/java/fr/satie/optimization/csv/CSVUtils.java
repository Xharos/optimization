package fr.satie.optimization.csv;

import com.opencsv.CSVReader;
import fr.satie.optimization.Optimizer;
import fr.satie.optimization.utils.OptimizerPools;
import java.io.Reader;
import java.net.URI;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * File <b>CSVUtils</b> located on fr.satie.optimization.csv
 * CSVUtils is a part of optimization.
 * <p>
 * Copyright (c) 2021 - 2021 ENS Paris Saclay SATIE.
 * <p>
 *
 * @author Valentin Burgaud (Xharos), {@literal <xharos@islandswars.fr>}
 * Created the 11/05/2021 at 14:48
 * @since 0.1
 */
public class CSVUtils {

	public static Map<CSVType, List<String[]>> loadAllFiles() {
		Map<CSVType, CompletableFuture<List<String[]>>> workers = new HashMap<>();
		for (CSVType file : CSVType.values()) {
			try {
				URI uri = ClassLoader.getSystemResource(String.valueOf(file)).toURI();
				if ("jar".equals(uri.getScheme())) {
					for (FileSystemProvider provider : FileSystemProvider.installedProviders()) {
						if (provider.getScheme().equalsIgnoreCase("jar")) {
							try {
								provider.getFileSystem(uri);
							} catch (FileSystemNotFoundException e) {
								provider.newFileSystem(uri, Collections.emptyMap());
							}
						}
					}
				}
				Reader reader = Files.newBufferedReader(Paths.get(uri));
				workers.put(file, CSVUtils.readAll(reader));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		while (workers.values().stream().anyMatch(fut -> !fut.isDone())) {
		}
		try {
			Map<CSVType, List<String[]>> result = new HashMap<>();
			for (CSVType file : CSVType.values()) {
				result.put(file, workers.get(file).get());
			}
			Optimizer.getLogger().info("Finishing reading all files ({}).", result.size());
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	public static CompletableFuture<List<String[]>> readAll(Reader reader) {
		return CompletableFuture.supplyAsync(() -> {
			CSVReader      csvReader = new CSVReader(reader);
			List<String[]> list;
			try {
				list = csvReader.readAll();
				reader.close();
				csvReader.close();
				Optimizer.getLogger().trace("Successfully load {} lines from a CSV file.", list.size());
				return list;
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}, OptimizerPools.getExecutor());
	}
}
