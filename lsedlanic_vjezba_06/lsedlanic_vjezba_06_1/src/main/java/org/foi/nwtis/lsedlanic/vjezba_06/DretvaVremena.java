package org.foi.nwtis.lsedlanic.vjezba_06;

public class DretvaVremena extends Thread {
	private static int brojDretve = 0;
	private boolean kraj = false;

	@Override
	public synchronized void start() {
		super.start();
	}

	@Override
	public void run() {
		int br = 0;
		while (br < brojCiklusa && !this.kraj) {
			System.out.println("Dretva: " + this.getName() + "brojac: " + br);
			try {
				Thread.sleep(trajanjeCiklusa);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void interrupt() {
		super.interrupt();
		this.kraj = true;
	}

	private int brojCiklusa;
	private int trajanjeCiklusa;

	public DretvaVremena(int brojCiklusa, int trajanjeCiklusa) {
		super("lsedlanic-" + brojDretve++);
		this.brojCiklusa = brojCiklusa;
		this.trajanjeCiklusa = trajanjeCiklusa;
	}

}
