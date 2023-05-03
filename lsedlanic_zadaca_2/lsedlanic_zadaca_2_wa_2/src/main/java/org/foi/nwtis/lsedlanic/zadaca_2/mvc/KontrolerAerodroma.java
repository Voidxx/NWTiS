/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package org.foi.nwtis.lsedlanic.zadaca_2.mvc;

import org.foi.nwtis.lsedlanic.zadaca_2.rest.RestKlijentAerodroma;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;

/**
 *
 * @author NWTiS
 */
@Controller
@Path("aerodromi")
@RequestScoped
public class KontrolerAerodroma {

  @Inject
  private Models model;

  @GET
  @Path("pocetak")
  @View("index.jsp")
  public void pocetak() {}

  @Context
  ServletContext context;

  @GET
  @Path("svi")
  @View("aerodromi.jsp")
  public void getAerodromi(@QueryParam("odBroja") int odBroja, @QueryParam("broj") int broj) {
    try {
      RestKlijentAerodroma rca = new RestKlijentAerodroma();
      if (odBroja == 0)
        odBroja = 1;
      if (broj == 0)
        broj = 20;
      var aerodromi = rca.getAerodromi(odBroja, broj);
      String brojRedova = (String) context.getAttribute("stranica.brojRedova");
      model.put("aerodromi", aerodromi);
      model.put("odBroja", odBroja);
      model.put("broj", broj);
      model.put("brojRedova", brojRedova);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  @GET
  @Path("icao")
  @View("aerodrom.jsp")
  public void getAerodrom(@QueryParam("icao") String icao) {
    try {
      RestKlijentAerodroma rca = new RestKlijentAerodroma();
      var aerodrom = rca.getAerodrom(icao);
      model.put("aerodrom", aerodrom);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @GET
  @Path("icao/udaljenost")
  @View("aerodromiUdaljenosti.jsp")
  public void getAerodromiUdaljenost(@QueryParam("icaoOd") String icaoOd,
      @QueryParam("icaoDo") String icaoDo) {
    try {
      RestKlijentAerodroma rca = new RestKlijentAerodroma();
      var udaljenosti = rca.getAerodromiUdaljenost(icaoOd, icaoDo);
      model.put("udaljenosti", udaljenosti);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @GET
  @Path("icao/najduljiPutDrzave")
  @View("aerodromiNajvecaUdaljenost.jsp")
  public void getAerodromNajvecaUdaljenost(@QueryParam("icao") String icao) {
    try {
      RestKlijentAerodroma rca = new RestKlijentAerodroma();
      var udaljenosti = rca.getAerodromNajvecaUdaljenost(icao);
      model.put("udaljenosti", udaljenosti);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
