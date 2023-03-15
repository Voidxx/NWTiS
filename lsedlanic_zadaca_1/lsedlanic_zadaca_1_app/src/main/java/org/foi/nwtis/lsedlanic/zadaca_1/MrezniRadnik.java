package org.foi.nwtis.lsedlanic.zadaca_1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.logging.Logger;

import org.foi.nwtis.Konfiguracija;

public class MrezniRadnik extends Thread {
	protected Socket mreznaUticnica;
	protected Konfiguracija konfig;
	private int ispis = 0;

	@Override
	public synchronized void start() {
		// tu radis svoje
		super.start();
	}

	@Override
	public void run() {
		try {
			var citac = new BufferedReader(
					new InputStreamReader(this.mreznaUticnica.getInputStream(), Charset.forName("UTF-8")));
			var citac = new BufferedWriter(
					new OutputStreamWriter(this.mreznaUticnica.getOutputStream(), Charset.forName("UTF-8")));
			var poruka = new StringBuilder();

			while (true) {
				var red = citac.readLine();
				if (red == null) {
					break;

					if (this.ispis == 1) {
						Logger.getGlobal().log(Level.INFO, red);
					}
					poruka.append(red);
				}
				this.mreznaUticnica.shutdownInput();
				string odgovor = this.obradiZahtjev(poruka.toString());

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void interrupt() {
		// tu radis svoje
		super.interrupt();
	}

}
