package fi.utu.tech.ThreadRunner.dispatchers;

/*
 * Luokka, jossa toteutetaan staattinen tehtävien jako ts. työn tehtävä 1
 *
 * @version     1.0
 * @since       1.0
 */

import java.util.ArrayList;
import java.util.ResourceBundle;

import fi.utu.tech.ThreadRunner.tasks.Countable;
import fi.utu.tech.ThreadRunner.tasks.TaskFactory;
import fi.utu.tech.ThreadRunner.workers.Worker;
import fi.utu.tech.ThreadRunner.workers.WorkerFactory;

public class StaticDispatcher implements Dispatcher {

	/**
	 * Metodi, jossa on toteutettu staattinen tehtäväjako toiminnallisuus.
	 *
	 *
	 * @return void
	 *
	 */
	public void dispatch(ControlSet controlSet) {
		try {
			//Luo laskettavat tehtävät
			Countable co = TaskFactory.createTask(controlSet.getTaskType());
			ArrayList<Integer> ilist = co.generate(controlSet.getAmountTasks(), controlSet.getMaxTime());
			//Luo listan säikeille
			ArrayList<StaticThread> threads = new ArrayList<StaticThread>();

			//Luo apumuuttujat säikeiden jakamiseksi
			int threadCount = controlSet.getThreadCount();
			int leftoverTasks = ilist.size() % threadCount;
			int threadTasks = (ilist.size() / threadCount) + leftoverTasks;

			//Jakaa säikeet säielistaan
			for (int i = 0; i < ilist.size(); i++) {
				ArrayList<Integer> ilistThread = new ArrayList<Integer>(ilist.subList(i, Math.min(ilist.size(), i + threadTasks)));
				threads.add(new StaticThread(ilistThread, controlSet));
			}

			//Käynnistää säikeet
			for(StaticThread thread : threads) {
				thread.start();
			}
			//Odottaa aikaisempien säikeiden kuolemista
			for (StaticThread thread : threads) {
				thread.join();
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}

class StaticThread extends Thread {

	private ArrayList<Integer> ilist;
	ControlSet controlSet;

	public StaticThread(ArrayList<Integer> ilist, ControlSet controlSet) {
		this.ilist = ilist;
		this.controlSet = controlSet;
	}

	public void run() {
		try {
			Worker worker = WorkerFactory.createWorker(controlSet.getWorkerType());
			for (int time : ilist) {
				worker.count(time);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
