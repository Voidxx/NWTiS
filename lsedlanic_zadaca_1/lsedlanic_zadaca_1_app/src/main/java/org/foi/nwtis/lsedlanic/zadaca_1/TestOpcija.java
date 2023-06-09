package org.foi.nwtis.lsedlanic.zadaca_1;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * Za testiranje opcija
 */
public class TestOpcija {

	/**
	 * @param args argumenti komande linije
	 */

	public static void main(String[] args) {

		// -k korisnik -l lozinka -a (ipadresa | adresa) -v mreznaVrata -t cekanje
		// ((--meteo idUredaj) |
		// --kraj)
		// -k pero -l 123456 -s localhost -p 8000 -t 0 --meteo FOI1-BME280
		// -k pero -l 123456 -s localhost -p 8000 -t 0 --kraj

		// koristi indeksirane grupe (...) poklapanja
		String sintaksa1 = "-k ([0-9a-zA-Z_]+) -l ([0-9a-zA-Z_-]+) "
				+ "-a ([0-9a-zA-Z_-[.]]+) -v ([0-9]+) -t ([0-9]+) ((--meteo ([0-9a-zA-Z_-[.]]+))|(--kraj))$";

		// koristi imenovane grupe (?<ime>...) poklpanja
		String sintaksa2 = "-k (?<korisnik>[0-9a-zA-Z_]+) -l (?<lozinka>[0-9a-zA-Z_-]+) "
				+ "-a (?<adresa>[0-9a-zA-Z_-[.]]+) -v (?<mreznaVrata>[0-9]+) -t (?<cekanje>[0-9]+) ((--meteo (?<meteo>[0-9a-zA-Z_-[.]]+))|(?<kraj>--kraj))$";

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < args.length; i++) {
			sb.append(args[i]).append(" ");
		}
		String s = sb.toString().trim();

		Pattern pattern1 = Pattern.compile(sintaksa1);
		Matcher m1 = pattern1.matcher(s);
		boolean status1 = m1.matches();
		if (status1) {
			int poc = 0;
			int kraj = m1.groupCount();
			for (int i = poc; i <= kraj; i++) {
				System.out.println(i + ". " + m1.group(i));
			}
		} else {
			System.out.println("Ne odgovara 1!");
		}

		Pattern pattern2 = Pattern.compile(sintaksa2);
		Matcher m2 = pattern2.matcher(s);
		boolean status2 = m2.matches();
		if (status2) {
			System.out.println("Korisnik: " + m2.group("korisnik"));
			System.out.println("Lozinka: " + m2.group("lozinka"));
			System.out.println("Adresa poslužitelja: " + m2.group("adresa"));
			System.out.println("Mrežna vrata: " + m2.group("mreznaVrata"));
			System.out.println("Meteo: " + m2.group("meteo"));
			System.out.println("Kraj: " + m2.group("kraj"));
		} else {
			System.out.println("Ne odgovara 2!");
		}
	}
}
