package fr.satie.optimization.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * File <b>OptimizerPools</b> located on fr.satie.optimization.utils
 * OptimizerPools is a part of optimization.
 * <p>
 * Copyright (c) 2021 - 2021 ENS Paris Saclay SATIE.
 * <p>
 *
 * @author Valentin Burgaud (Xharos), {@literal <xharos@islandswars.fr>}
 * Created the 12/05/2021 at 15:13
 * @since 0.1
 */
public class OptimizerPools {

	private static final ExecutorService executor;

	static {
		executor = Executors.newCachedThreadPool(new OptimizerThreadFactory());
	}

	private OptimizerPools() {
	}

	/**
	 * @return a custom cached thread pool
	 */
	public static ExecutorService getExecutor() {
		return executor;
	}

	private static class OptimizerThreadFactory implements ThreadFactory {

		private final AtomicInteger count;

		OptimizerThreadFactory() {
			this.count = new AtomicInteger();
		}

		@Override
		public Thread newThread(Runnable runnable) {
			Thread thread = new Thread(runnable);
			thread.setName("op-pool-" + count.getAndIncrement());
			return thread;
		}
	}
}
