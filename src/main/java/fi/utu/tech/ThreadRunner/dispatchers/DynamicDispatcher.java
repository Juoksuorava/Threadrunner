package fi.utu.tech.ThreadRunner.dispatchers;

/*
 * Luokka, jossa toteutetaan dynaaminen tehtävien jako ts. työn tehtävä 2
 * 
* @version     1.0                 
* @since       1.0          
*/

import fi.utu.tech.ThreadRunner.tasks.Countable;
import fi.utu.tech.ThreadRunner.tasks.TaskFactory;
import fi.utu.tech.ThreadRunner.workers.Worker;
import fi.utu.tech.ThreadRunner.workers.WorkerFactory;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DynamicDispatcher implements Dispatcher {

	/**
	 * Metodi, jossa on toteutetaan dynaamisen tehtäväjaon toiminnallisuus.
	 * <p>
	 * <p>
	 * ControlSet Käyttöliittymässä asetettu arvot välittyvät tässä oliossa
	 *
	 * @return void
	 */
	public void dispatch(ControlSet controlSet) {
		try {
			Countable co = TaskFactory.createTask(controlSet.getTaskType());
			ArrayList<Integer> ilist = co.generate(controlSet.getAmountTasks(), controlSet.getMaxTime());

			int maxThreadCount = controlSet.getThreadCount();
			ExecutorService threadPool = Executors.newFixedThreadPool(maxThreadCount);

			ArrayList<Runnable> tasks = new ArrayList<>();

			for (Integer i : ilist) {
				Runnable r = new DynamicThread(i, controlSet);
				tasks.add(r);
			}

			for (Runnable r : tasks) {
				threadPool.execute(r);
			}

			threadPool.shutdown();
			try {
				if (!threadPool.awaitTermination(3500, TimeUnit.MILLISECONDS)) {
					threadPool.shutdownNow();
				}
			} catch (InterruptedException ie) {
					threadPool.shutdownNow();
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}

class DynamicThread implements Runnable {

	private Integer i;
	ControlSet controlSet;

	public DynamicThread (Integer i, ControlSet controlSet) {
		this.i = i;
		this.controlSet = controlSet;
	}

	public void run() {
		try {
			Worker worker = WorkerFactory.createWorker(controlSet.getWorkerType());
			for (Integer n = 0; n > i; n++) {
				worker.count(n);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
