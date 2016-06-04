package com.zkjinshi.ritz.view.zoomview;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Request thread executor. We use a thread pool to execute the thread.
 * 
 * @author gaolei01@snda
 * @version 1.0.0.0
 * @see GLRequest
 * 
 */
public class GLRequestExecutor {

	private static final int THREAD_NUMBER = 15;

	private static ExecutorService threadPool = Executors
			.newFixedThreadPool(THREAD_NUMBER);

	/**
	 * Execute the request thread.
	 * 
	 * @param command
	 *            thread to be executed.
	 */
	public static void doAsync(Runnable command) {
		threadPool.execute(command);
	}
}
