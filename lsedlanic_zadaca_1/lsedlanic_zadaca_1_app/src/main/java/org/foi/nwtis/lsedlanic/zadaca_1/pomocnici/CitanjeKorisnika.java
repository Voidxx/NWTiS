package org.foi.nwtis.lsedlanic.zadaca_1.pomocnici;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.lsedlanic.zadaca_1.podaci.Korisnik;

public class CitanjeKorisnika {
	public Map<String, Korisnik> ucitajDatoteku(String nazivDatoteke) throws IOException {
		var putanja = Path.of(nazivDatoteke);
		if (!Files.exists(putanja) || !Files.isDirectory(putanja) || !Files.isReadable(putanja)) {
			throw new IOException(
					"Datoteka '" + nazivDatoteke + "' ne postoji ili se ne može čitati ili je direktorij");
		}

		var korisnici = new HashMap<String, Korisnik>();
		var citac = Files.newBufferedReader(putanja, Charset.forName("UTF-8"));

		while (true) {
			var red = citac.readLine();
			if (red == null)
				break;

			var elementi = red.split(";");
			if (!redImaPetElemenata(elementi)) {
				Logger.getGlobal().log(Level.WARNING, red);
			} else {
				var admin = isAdministrator(elementi[4]);
				var korisnik = new Korisnik(elementi[0], elementi[1], elementi[2], elementi[3], admin);
				korisnici.put(elementi[2], korisnik);
			}
		}
		return korisnici;
	}

	private boolean isAdministrator(String element) {
		var admin = element.compareTo("1") == 0 ? true : false;
		return admin;
	}

	private boolean redImaPetElemenata(String[] elementi) {
		return elementi.length == 5;
	}

}
