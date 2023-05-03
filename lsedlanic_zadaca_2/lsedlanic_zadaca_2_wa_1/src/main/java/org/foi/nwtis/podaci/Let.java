package org.foi.nwtis.podaci;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor()
public class Let {

  @Getter
  @Setter
  private String avion;
  @Getter
  @Setter
  private String odrediste;


  public Let() {}
}
