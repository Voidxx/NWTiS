package org.foi.nwtis.lsedlanic_zadaca_1;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.KonfiguracijaApstraktna;
import org.foi.nwtis.NeispravnaKonfiguracija;

public class PokretacPosluzitelja {

	public PokretacPosluzitelja(String nazivDatoteke) {

	}

	public static void main(String[] args) throws NeispravnaKonfiguracija {

		PokretacPosluzitelja pokretac = new PokretacPosluzitelja(args[0]);
		if (provjeriArgumente(args)) {
			Konfiguracija config = ucitajPostavke(args[0]);
			GlavniPosluzitelj gp = new GlavniPosluzitelj(config);
			gp.pokreniPosluzitelja();
		} else {
			Logger.getLogger(PokretacPosluzitelja.class.getName()).log(Level.SEVERE, "Nema argumenata!");
		}

	}

	public static boolean provjeriArgumente(String[] args) throws NeispravnaKonfiguracija {
		if (args.length == 0) {
			Logger.getLogger(PokretacPosluzitelja.class.getName()).log(Level.SEVERE, "Nije upisan naziv datoteke!");
			return false;
		} else {
			ucitajPostavke(args[0]);
			return true;
		}
	}

	public static Konfiguracija ucitajPostavke(String nazivDatoteke) throws NeispravnaKonfiguracija {
		Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(nazivDatoteke);
		if (konfig != null) {
			return konfig;
		} else {
			Logger.getLogger(PokretacPosluzitelja.class.getName()).log(Level.SEVERE, "Neispravna datoteka");
			return null;
		}

	}

}
