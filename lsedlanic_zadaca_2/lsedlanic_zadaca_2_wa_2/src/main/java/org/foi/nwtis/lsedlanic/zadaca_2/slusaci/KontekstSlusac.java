package org.foi.nwtis.lsedlanic.zadaca_2.slusaci;

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
      java.io.FileInputStream fis = new java.io.FileInputStream(path + datoteka);
      configData.load(fis);
      fis.close();
      for (java.util.Enumeration e = configData.propertyNames(); e.hasMoreElements();) {
        String key = (String) e.nextElement();
        context.setAttribute(key, configData.getProperty(key));
      }
      System.out.println("Ucitana konfiguracija!");
    } catch (Exception e) {
      System.out.println("Problem s konfiguracijom!");
    }

  }

}
