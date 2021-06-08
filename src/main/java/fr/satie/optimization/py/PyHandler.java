package fr.satie.optimization.py;

import fr.satie.optimization.Optimizer;
import fr.satie.optimization.utils.OptimizerPools;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.CompletableFuture;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

/**
 * File <b>PyHandler</b> located on fr.satie.optimization.py
 * PyHandler is a part of optimization.
 * <p>
 * Copyright (c) 2021 - 2021 ENS Paris Saclay SATIE.
 * <p>
 *
 * @author Valentin Burgaud (Xharos), {@literal <xharos@islandswars.fr>}
 * Created the 19/05/2021 at 09:54
 * @since 0.2
 */
public class PyHandler {

	private static final String PATH = "src/main/resources/";

	public static CompletableFuture<String> exec(PyFile file, String... args) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				if (Optimizer.getInstance().isDebug())
					Optimizer.getLogger().debug("Async execute python : {}", file.getFileName());
				ByteArrayOutputStream outputStream  = new ByteArrayOutputStream();
				PumpStreamHandler     streamHandler = new PumpStreamHandler(outputStream);
				DefaultExecutor       executor      = new DefaultExecutor();
				executor.setStreamHandler(streamHandler);
				executor.execute(CommandLine.parse("python " + PATH + file.getFileName() + ".py " + String.join(" ", args)));
				return outputStream.toString().trim();
			} catch (Exception e) {
				return "crash";
			}
		}, OptimizerPools.getExecutor());
	}

}
