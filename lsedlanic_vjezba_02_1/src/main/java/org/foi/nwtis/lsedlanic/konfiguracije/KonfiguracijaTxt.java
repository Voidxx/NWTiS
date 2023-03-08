package org.foi.nwtis.lsedlanic.konfiguracije;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.KonfiguracijaApstraktna;
import org.foi.nwtis.NeispravnaKonfiguracija;

public class KonfiguracijaTxt extends KonfiguracijaApstraktna {
	public static final String TIP = "txt";

	public KonfiguracijaTxt(String nazivDatoteke) {
		super(nazivDatoteke);
	}

	@Override
	public void spremiKonfiguraciju(String datoteka) throws NeispravnaKonfiguracija {
		var putanja = Path.of(datoteka);
		var tip = Konfiguracija.dajTipKonfiguracije(datoteka);
		if (tip == null || tip.compareTo(TIP) != 0) {
			throw new NeispravnaKonfiguracija("Datoteka '" + datoteka + "' nije ispravnog tipa: '" + TIP + "'");
		} else if (Files.exists(putanja) && (Files.isDirectory(putanja) || !Files.isWritable(putanja)))
			throw new NeispravnaKonfiguracija(
					"Datoteka '" + datoteka + "' je direktorij ili nije moguće pisati u nju.");

		try {
			this.postavke.store(Files.newOutputStream(putanja), "NWTIS lsedlanic 2023.");
		} catch (IOException e) {
			throw new NeispravnaKonfiguracija("Datoteka '" + datoteka + "' nije moguće pisati." + e.getMessage());
		}

	}

	@Override
	public void ucitajKonfiguraciju() throws NeispravnaKonfiguracija {
		var datoteka = this.nazivDatoteke;
		var putanja = Path.of(datoteka);
		var tip = Konfiguracija.dajTipKonfiguracije(datoteka);
		if (tip == null || tip.compareTo(TIP) != 0) {
			throw new NeispravnaKonfiguracija("Datoteka '" + datoteka + "' nije ispravnog tipa: '" + TIP + "'");
		} else if (Files.exists(putanja) && (Files.isDirectory(putanja) || !Files.isReadable(putanja)))
			throw new NeispravnaKonfiguracija(
					"Datoteka '" + datoteka + "' je direktorij ili nije moguće čitati iz nje.");

		try {
			this.postavke.load(Files.newInputStream(putanja));
		} catch (IOException e) {
			throw new NeispravnaKonfiguracija("Datoteka '" + datoteka + "' nije moguće čitati." + e.getMessage());
		}

	}

}
