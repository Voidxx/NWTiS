package org.foi.nwtis.lsedlanic.vjezba_04;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.foi.nwtis.lsedlanic.vjezba_04.podaci.SystemInfo;

import com.google.gson.Gson;

public class Vjezba_04_1 {

	private SystemInfo systemInfo;

	public Vjezba_04_1() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		Vjezba_04_1 vjezba = new Vjezba_04_1();
		if (!vjezba.provjeriArgumente(args))
			return;
		SystemInfo systemInfo = new SystemInfo();
		vjezba.systemInfo = systemInfo;
		vjezba.ispisiSystemInfo();

		try {
			vjezba.spremiSystemInfo(args[0]);
		} catch (IOException e) {
			Logger.getGlobal().log(Level.SEVERE, "Greška kod spremanja: " + e.getMessage());
		}
	}

	private void spremiSystemInfo(String nazivDatoteke) throws IOException {
		var datoteka = Path.of(nazivDatoteke);
		var citac = Files.newBufferedWriter(datoteka);
		Gson gson = new Gson();
		var json = gson.toJson(this.systemInfo);
		Logger.getGlobal().log(Level.INFO, json);
		citac.write(json);
		citac.close();
	}

	private void ispisiSystemInfo() {
		Logger.getGlobal().log(Level.INFO, "OS:" + this.systemInfo.getNazivOS());
		Logger.getGlobal().log(Level.INFO, "Proizvodac:" + this.systemInfo.getProizvodacVM());
		Logger.getGlobal().log(Level.INFO, "Verzija:" + this.systemInfo.getVerzijaVM());
		Logger.getGlobal().log(Level.INFO, "VM dir:" + this.systemInfo.getDirektorijVM());
		Logger.getGlobal().log(Level.INFO, "Temp dir:" + this.systemInfo.getDirektorijTemp());
		Logger.getGlobal().log(Level.INFO, "User dir:" + this.systemInfo.getDirektorijKorisnik());

	}

	public boolean provjeriArgumente(String[] args) {
		if (args.length != 1) {
			Logger.getGlobal().log(Level.SEVERE, "Krivi argumenti!");
			return false;
		} else
			return true;
	}
}
