package org.foi.nwtis.lsedlanic.zadaca_1;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.lsedlanic.zadaca_1.podaci.Korisnik;
import org.foi.nwtis.lsedlanic.zadaca_1.podaci.Lokacija;
import org.foi.nwtis.lsedlanic.zadaca_1.podaci.Uredaj;
import org.foi.nwtis.lsedlanic.zadaca_1.pomocnici.CitanjeKorisnika;

/**
 * 
 * Klasa GLavniPoslužitelj koja je zadužena za otvaranje veze na određenim
 * mrežnim vratima/portu
 * 
 * @author Leon Sedlanic
 *
 */
public class GlavniPosluzitelj {

	protected Konfiguracija konf;
	protected int brojRadnika;
	protected int maksVrijemeNeaktivnosti;
	protected Map<String, Korisnik> korisnici;
	protected Map<String, Lokacija> lokacije;
	protected Map<String, Uredaj> uredaji;
	private int ispis = 0;

	public GlavniPosluzitelj(Konfiguracija config) {
		this.konf = konf;
//		this.brojRadnika = Integer.parseInt(konf.dajPostavku("brojRadnika"));
//		this.maksVrijemeNeaktivnosti = Integer.parseInt(konf.dajPostavku("maksVrijemeNeaktivnosti"));
		this.ispis = Integer.parseInt(konf.dajPostavku("Ispis"));
	}

	public void pokreniPosluzitelja() {
		try {
			this.ucitajKorisnike();
		} catch (IOException e) {
			Logger.getGlobal().log(Level.SEVERE, e.getMessage());
		}
		// TODO ucitaj ostale podatke (lokacija, uredaji)
	}

	private void ucitajKorisnike() throws IOException {
		var nazivDatoteke = this.konf.dajPostavku("datotekaKorisnika");
		var citac = new CitanjeKorisnika();
		this.korisnici = citac.ucitajDatoteku(nazivDatoteke);
		if (this.ispis == 1) {
			for (String korime : this.korisnici.keySet()) {
				var k = this.korisnici.get(korime);
				Logger.getGlobal().log(Level.INFO, "Korisnik: " + k.prezime() + " " + k.ime());
			}
		}

	}
}
