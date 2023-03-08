package org.foi.nwtis.lsedlanic.vjezba_02;

import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.KonfiguracijaApstraktna;
import org.foi.nwtis.NeispravnaKonfiguracija;

public class Vjezba_02_3 {

	public Vjezba_02_3() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws NeispravnaKonfiguracija {
		switch (args.length) {
		case 1:
			Konfiguracija konf = KonfiguracijaApstraktna.preuzmiKonfiguraciju(args[0]);
			var postavke = konf.dajSvePostavke();
			for (Object o : postavke.keySet()) {
				String k = (String) o;
				String v = konf.dajPostavku(k);
				System.out.println(k + " = " + v);

			}
			break;
		case 2:
			konf = KonfiguracijaApstraktna.preuzmiKonfiguraciju(args[0]);
			String k = args[1];
			String v = konf.dajPostavku(k);
			System.out.println(k + " = " + v);
			break;

		case 3:
			konf = KonfiguracijaApstraktna.preuzmiKonfiguraciju(args[0]);
			String k1 = args[1];
			String v1 = args[2];
			konf.spremiPostavku(k1, v1);
			konf.spremiKonfiguraciju();
			System.out.println("Dodana postavka");
			break;
		default:
			System.out.println("Krivi broj argumenata!");

		}

	}

}
