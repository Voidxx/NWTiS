package org.foi.nwtis.lsedlanic.zadaca_2.slusaci;

import org.foi.nwtis.KonfiguracijaApstraktna;
import org.foi.nwtis.NeispravnaKonfiguracija;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class KontekstSlusac implements ServletContextListener {

  private ServletContext context = null;

  public void contextInitialized(ServletContextEvent event) {
    context = event.getServletContext();
    ucitajKonfiguraciju();
    ServletContextListener.super.contextInitialized(event);

  }

  public void contextDestroyed(ServletContextEvent event) {
    context = event.getServletContext();
    context.removeAttribute("konfig");
    ServletContextListener.super.contextDestroyed(event);
  }

  private void ucitajKonfiguraciju() {
    java.util.Properties configData = new java.util.Properties();
    String path = context.getRealPath("/WEB-INF") + java.io.File.separator;
    String datoteka = context.getInitParameter("konfiguracija");
    try {
      var konfiguracija = KonfiguracijaApstraktna.preuzmiKonfiguraciju(path + datoteka);
      context.setAttribute("konfig", konfiguracija);
    } catch (NeispravnaKonfiguracija e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
