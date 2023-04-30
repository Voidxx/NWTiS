package org.foi.nwtis.lsedlanic.vjezba_04.podaci;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@NonNull
public final class SystemInfo {

	private String nazivOS, proizvodacVM, verzijaVM, direktorijVM, direktorijTemp, direktorijKorisnik;

	public SystemInfo() {
		this.nazivOS = System.getProperty("os.name");
		this.proizvodacVM = System.getProperty("java.vendor");
		this.verzijaVM = System.getProperty("java.version");
		this.direktorijVM = System.getProperty("java.home");
		this.direktorijTemp = System.getProperty("java.io.tmpdir");
		this.direktorijKorisnik = System.getProperty("user.dir");

	}

}
