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
		if (!Files.exists(putanja) || Files.isDirectory(putanja) || !Files.isReadable(putanja)) {
			throw new IOException(
					"Daoteka '" + nazivDatoteke + "' ne postoji ili se ne može čitati ili je direktorij.");
		}

		var korisnici = new HashMap<String, Korisnik>();
		var citac = Files.newBufferedReader(putanja, Charset.forName("UTF-8"));

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
		return korisnici;
	}

	private boolean isAdministrator(String stupac) {
		return stupac.compareTo("1") == 0 ? true : false;
	}

	private boolean redImaPetStupaca(String[] stupci) {
		return stupci.length == 5;
	}
}
