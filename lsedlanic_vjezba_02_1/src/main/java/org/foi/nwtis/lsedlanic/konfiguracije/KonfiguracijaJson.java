package org.foi.nwtis.lsedlanic.konfiguracije;

import org.foi.nwtis.KonfiguracijaApstraktna;
import org.foi.nwtis.NeispravnaKonfiguracija;

public class KonfiguracijaJson extends KonfiguracijaApstraktna {
	public static final String TIP = "json";

	public KonfiguracijaJson(String nazivDatoteke) {
		super(nazivDatoteke);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void spremiKonfiguraciju(String datoteka) throws NeispravnaKonfiguracija {
		// TODO Auto-generated method stub

	}

	@Override
	public void ucitajKonfiguraciju() throws NeispravnaKonfiguracija {
		// TODO Auto-generated method stub

	}

}
