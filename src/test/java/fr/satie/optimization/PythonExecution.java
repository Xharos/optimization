package fr.satie.optimization;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * File <b>PythonExecution</b> located on fr.satie.optimization
 * PythonExecution is a part of optimization.
 * <p>
 * Copyright (c) 2021 - 2021 ENS Paris Saclay SATIE.
 * <p>
 *
 * @author Valentin Burgaud (Xharos), {@literal <xharos@islandswars.fr>}
 * Created the 18/05/2021 at 14:53
 * @since 0.2
 */
public class PythonExecution {

	@Test
	public void givenPythonScript_whenPythonProcessExecuted_thenSuccess() throws ExecuteException, IOException {
		String      line    = "python src/test/resources/hello.py";
		CommandLine cmdLine = CommandLine.parse(line);

		ByteArrayOutputStream outputStream  = new ByteArrayOutputStream();
		PumpStreamHandler     streamHandler = new PumpStreamHandler(outputStream);

		DefaultExecutor executor = new DefaultExecutor();
		executor.setStreamHandler(streamHandler);

		int exitCode = executor.execute(cmdLine);
		Assertions.assertEquals(0, exitCode, "No errors should be detected");
	}

}
