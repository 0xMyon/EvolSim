package com.github.myon.evolsim.engine;

import com.github.myon.util.Queue;

public class Worker {

	private final Queue<WorkItem> tasks = new Queue<>();

	private boolean running = true;

	public Worker(final int threads) {
		for (int i = 0; i < threads; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (Worker.this.running) {
						final WorkItem task = Worker.this.tasks.pop();
						task.run();
						if (task.requeue()) {
							Worker.this.tasks.add(task);
						}
					}
				}
			}, "MyWorker#" + i).start();
		}
	}

	public void stop() {
		this.running = false;
	}

	public void add(final WorkItem task) {
		this.tasks.add(task);
	}

	public void remove(final WorkItem task) {
		this.tasks.remove(task);
	}

}
