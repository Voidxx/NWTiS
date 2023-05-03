package org.foi.nwtis.podaci;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor()
public class LetPolazak {

  @Getter
  @Setter
  private String ID;
  @Getter
  @Setter
  private String icao24;
  @Getter
  @Setter
  private String firstSeen;
  @Getter
  @Setter
  private String estDepartureAirport;
  @Getter
  @Setter
  private String lastSeen;
  @Getter
  @Setter
  private String estArrivalAirport;
  @Getter
  @Setter
  private String callSign;
  @Getter
  @Setter
  private String estDepartureAirportHorizDistance;
  @Getter
  @Setter
  private String estDepartureAirportVertDistance;
  @Getter
  @Setter
  private String estArrivalAirportHorizDistance;
  @Getter
  @Setter
  private String estArrivalAirportVertDistance;
  @Getter
  @Setter
  private String departureAirportCandidatesCount;
  @Getter
  @Setter
  private String arrivalAirportCandidatesCount;
  @Getter
  @Setter
  private String stored;

  public LetPolazak() {}
}
