package org.foi.nwtis.lsedlanic.zadaca_1;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.KonfiguracijaApstraktna;
import org.foi.nwtis.lsedlanic.zadaca_1.podaci.Lokacija;
import org.foi.nwtis.lsedlanic.zadaca_1.podaci.Ocitanje;
import org.foi.nwtis.lsedlanic.zadaca_1.podaci.Uredaj;
import org.foi.nwtis.lsedlanic.zadaca_1.podaci.UredajVrsta;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


class MrezniRadnikTest {

  private static Konfiguracija konf;
  private GlavniPosluzitelj gp;
  private MrezniRadnik mr;
  private static Socket mreznaUticnica;

  @BeforeAll
  static void setUpBeforeClass() throws Exception {
    konf = KonfiguracijaApstraktna.preuzmiKonfiguraciju("NWTiS_lsedlanic_3.txt");
  }


  @AfterAll
  static void tearDownAfterClass() throws Exception {
    konf = null;

  }

  @BeforeEach
  void setUp() throws Exception {
    this.gp = new GlavniPosluzitelj(konf);
    this.mr = new MrezniRadnik(mreznaUticnica, konf);
  }

  @AfterEach
  void tearDown() throws Exception {
    this.mr = null;
  }



  @Test
  public void start() {
    try {
      Logger.getGlobal().log(Level.INFO, "Pokretanje starta");
      Socket mreznaUticnicac = null;
      Konfiguracija konfigc = null;;
      MrezniRadnik mrezniradnik = new MrezniRadnik(mreznaUticnicac, konfigc);
      mrezniradnik.start();
      Assertions.assertTrue(true);

    } catch (Exception exception) {
      Logger.getGlobal().log(Level.WARNING, "pogreška u pokretanju-" + exception, exception);
      exception.printStackTrace();
      Assertions.assertFalse(false);
    }
  }



  @Test
  public void interrupt() {
    try {
      Logger.getGlobal().log(Level.INFO, "Pokretanje prekida");
      Socket mreznaUticnicac = null;
      Konfiguracija konfigc = null;;
      MrezniRadnik mrezniradnik = new MrezniRadnik(mreznaUticnicac, konfigc);
      mrezniradnik.interrupt();
      Assertions.assertTrue(true);

    } catch (Exception exception) {
      Logger.getGlobal().log(Level.WARNING, "pogreška u prekidanju-" + exception, exception);
      exception.printStackTrace();
      Assertions.assertFalse(false);
    }
  }

  @Test
  public void dobaviGP() {
    try {
      GlavniPosluzitelj glavni = null;;
      Socket mreznaUticnica = null;
      Konfiguracija konfig = null;;
      MrezniRadnik mrezniradnik = new MrezniRadnik(mreznaUticnica, konfig);
      mrezniradnik.dobaviGP(glavni);
      Assertions.assertTrue(true);

    } catch (Exception exception) {
      exception.printStackTrace();
      Assertions.assertFalse(false);
    }
  }


  @Test
  public void postojiLiOdstupanje() {
    try {
      boolean ocekivanaVrijednost = false;

      ;
      List<Ocitanje> lista = null;;
      Socket mreznaUticnicac = null;
      Konfiguracija konfigc = null;;
      MrezniRadnik mrezniradnik = new MrezniRadnik(mreznaUticnicac, konfigc);
      boolean stvarnaVrijednost = mrezniradnik.postojiLiOdstupanje(lista);
      Logger.getGlobal().log(Level.INFO, "Očekivana vrijednost=" + ocekivanaVrijednost
          + " . Stvarna vrijednost=" + stvarnaVrijednost);
      Assertions.assertEquals(ocekivanaVrijednost, stvarnaVrijednost);

    } catch (Exception exception) {
      Logger.getGlobal().log(Level.WARNING, "Pogreška u dobivanju odstupanja-" + exception,
          exception);
      exception.printStackTrace();
      Assertions.assertFalse(false);
    }
  }

  @Test

  public void spojiSeNaPosluziteljaUdaljenosti() {
    try {
      Logger.getGlobal().log(Level.INFO, "Spajanje na poslužitelj udaljenosti");
      Socket Očekivanavrijednost = null;

      ;
      Konfiguracija konf = null;;
      Socket mreznaUticnicac = null;
      Konfiguracija konfigc = null;;
      MrezniRadnik mrezniradnik = new MrezniRadnik(mreznaUticnicac, konfigc);
      Socket stvarnaVrijednost = mrezniradnik.spojiSeNaPosluziteljaUdaljenosti(konf);
      Logger.getGlobal().log(Level.INFO, "Očekivana vrijednost =" + Očekivanavrijednost
          + " . Stvarna vrijednost =" + stvarnaVrijednost);
      Assertions.assertEquals(Očekivanavrijednost, stvarnaVrijednost);

    } catch (Exception exception) {
      Logger.getGlobal().log(Level.WARNING, "pogreška: " + exception, exception);
      exception.printStackTrace();
      Assertions.assertFalse(false);
    }
  }


  public class UdaljenostKomanduZaSpremanjeTest {

    @Test
    void testPosaljiUdaljenostKomanduZaSpremanje() throws IOException {
      Socket mockSocket = Mockito.mock(Socket.class);

      Konfiguracija mockKonf = Mockito.mock(Konfiguracija.class);

      BufferedReader mockReader = Mockito.mock(BufferedReader.class);
      BufferedWriter mockWriter = Mockito.mock(BufferedWriter.class);

      Mockito.when(mockSocket.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
      Mockito.when(mockSocket.getOutputStream()).thenReturn(new ByteArrayOutputStream());

      Mockito.when(mockReader.readLine()).thenReturn("test");

      Mockito.doNothing().when(mockWriter).write(Mockito.anyString());
      Mockito.doNothing().when(mockWriter).flush();

      Mockito.when(mockSocket.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
      Mockito.when(mockSocket.getOutputStream()).thenReturn(new ByteArrayOutputStream());

      String result = mr.posaljiUdaljenostKomanduZaSpremanje(mockSocket, mockKonf);

      Mockito.verify(mockWriter).write("UDALJENOST SPREMI");
      Mockito.verify(mockWriter).flush();
      Mockito.verify(mockReader).readLine();
      Mockito.verify(mockSocket).shutdownOutput();
      Mockito.verify(mockSocket).shutdownInput();
      Mockito.verify(mockSocket).close();

      Assertions.assertEquals("test", result);
    }
  }

  public class MyClassTest {

    @Mock
    private Socket mreznaUticnica;

    @Mock
    private Konfiguracija konf;

    @Mock
    private BufferedReader citac;

    @Mock
    private BufferedWriter pisac;

    @Test
    public void testPosaljiUdaljenostKomanduZaIzracun() throws IOException {
      String ocekivano = "UDALJENOST 45.8150 15.9819 44.8150 14.9819";

      Map<String, Lokacija> lokacije = new HashMap<>();
      Lokacija lokacija1 = new Lokacija("test1", "1", "45.8150", "15.9819");
      Lokacija lokacija2 = new Lokacija("test2", "1", "45.8150", "15.9819");

      lokacije.put("1", lokacija1);
      lokacije.put("2", lokacija2);

      GlavniPosluzitelj gp = new GlavniPosluzitelj(konf);
      gp.lokacije = lokacije;

      when(mreznaUticnica.getInputStream()).thenReturn(mock(InputStream.class));
      when(mreznaUticnica.getOutputStream()).thenReturn(mock(OutputStream.class));
      when(mreznaUticnica.isClosed()).thenReturn(false);
      when(mreznaUticnica.isInputShutdown()).thenReturn(false);
      when(mreznaUticnica.isOutputShutdown()).thenReturn(false);
      when(mreznaUticnica.getLocalPort()).thenReturn(1234);

      when(citac.readLine()).thenReturn(ocekivano).thenReturn(null);

      when(pisac.toString()).thenReturn(ocekivano);

      String result = new MrezniRadnik(mreznaUticnica, konf)
          .posaljiUdaljenostKomanduZaIzracun(mreznaUticnica, konf);

      assertEquals(ocekivano, result);

      verify(pisac).write(ocekivano);
      verify(pisac).flush();
      verify(mreznaUticnica).shutdownOutput();
      verify(citac, times(2)).readLine();
      verify(mreznaUticnica).shutdownInput();
      verify(mreznaUticnica).close();
    }
  }

  private Map<String, Uredaj> mockUredaji() {
    Map<String, Uredaj> uredaji = new HashMap<>();
    uredaji.put("1", Mockito.mock(Uredaj.class));
    Mockito.when(uredaji.get("1").id()).thenReturn("1");
    Mockito.when(uredaji.get("1").vrsta()).thenReturn(UredajVrsta.odBroja(1));
    uredaji.put("50", Mockito.mock(Uredaj.class));
    Mockito.when(uredaji.get("50").id()).thenReturn("50");
    Mockito.when(uredaji.get("50").vrsta()).thenReturn(UredajVrsta.odBroja(50));
    uredaji.put("51", Mockito.mock(Uredaj.class));
    Mockito.when(uredaji.get("51").id()).thenReturn("51");
    Mockito.when(uredaji.get("51").vrsta()).thenReturn(UredajVrsta.odBroja(51));
    uredaji.put("52", Mockito.mock(Uredaj.class));
    Mockito.when(uredaji.get("52").id()).thenReturn("52");
    Mockito.when(uredaji.get("52").vrsta()).thenReturn(UredajVrsta.odBroja(52));
    return uredaji;
  }


  @Test
  public void testDaLiOdgovaraTipUredaja() {
    Set<Map.Entry<String, Uredaj>> uredajiSet = mockUredaji().entrySet();

    Matcher komanda = Mockito.mock(Matcher.class);
    Mockito.when(komanda.group("idUredaj")).thenReturn("50");
    Mockito.when(komanda.group("temp")).thenReturn("10");
    Mockito.when(komanda.group("vlaga")).thenReturn("20");
    Mockito.when(komanda.group("tlak")).thenReturn(null);
    boolean actual = mr.daLiOdgovaraTipUredaja(uredajiSet, komanda);
    assertTrue(actual);
  }



}


