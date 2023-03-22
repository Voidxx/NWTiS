package org.foi.nwtis.lsedlanic.zadaca_1;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.NeispravnaKonfiguracija;
import org.foi.nwtis.lsedlanic.zadaca_1.podaci.Korisnik;
import org.foi.nwtis.lsedlanic.zadaca_1.podaci.MeteoSimulacija;

public class SimulatorMeteo {

	public static void main(String[] args) {

		var sm = new SimulatorMeteo();
		if (!sm.provjeriArgumente(args)) {
			Logger.getLogger(PokretacPosluzitelja.class.getName()).log(Level.SEVERE, "Nije upisan naziv datoteke!");
			return;
		}

		try {
			var konf = sm.ucitajPostavke(args[0]);
			sm.pokreniSimulator(konf);
		} catch (NeispravnaKonfiguracija e) {
			Logger.getLogger(PokretacPosluzitelja.class.getName()).log(Level.SEVERE,
					"Pogreška kod učitavanja postavki iz datoteke!" + e.getMessage());
		}

	}

	private Object ucitajPostavke(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	private boolean provjeriArgumente(String[] args) {
		// TODO Auto-generated method stub
		return false;
	}

	private void pokreniSimulator(Konfiguracija konf) {
		var nazivDatoteke = konf.dajPostavku("");
		if (!Files.exists(putanja) || Files.isDirectory(putanja) || !Files.isReadable(putanja)) {
			throw new IOException(
					"Daoteka '" + nazivDatoteke + "' ne postoji ili se ne može čitati ili je direktorij.");
		}

		var korisnici = new HashMap<String, Korisnik>();
		var citac = Files.newBufferedReader(putanja, Charset.forName("UTF-8"));

		int rbroj = 0;
		MeteoSimulacija prethodniMeteo = null;

		while (true) {
			var red = citac.readLine();
			if (red == null)
				break;

			var stupci = red.split(";");
			if (!redImaPetStupaca(stupci)) {
				Logger.getGlobal().log(Level.WARNING, red);
			} else {
				var admin = isAdministrator(stupci[4]);
				var korisnik = new Korisnik(stupci[0], stupci[1], stupci[2], stupci[3], admin);
				korisnici.put(stupci[2], korisnik);
			}
		}

	}

	private boolean redImaPetStupaca(String[] stupci) {
		return stupci.length == 5;
	}

	private void posaljiMeteoPodatka(MeteoSimulacija meteoPodatak) {

	}

	private void izracunajOdradiSpavanje(MeteoSimulacija prethodniMeteo, MeteoSimulacija vazeciMeteo) {
		String prvi = prethodniMeteo.vrijeme();
		String drugi = vazeciMeteo.vrijeme();
		// TODO pretvori u ms
		int pocetak = 10;
		int kraj = 20;
		int spavanje = kraj - pocetak;
		try {
			Thread.sleep(spavanje);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
