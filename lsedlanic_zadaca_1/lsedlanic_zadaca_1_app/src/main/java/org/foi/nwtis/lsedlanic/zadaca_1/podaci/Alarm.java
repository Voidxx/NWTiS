package org.foi.nwtis.lsedlanic.zadaca_1.podaci;

import java.time.LocalTime;

public record Alarm(String idUredaj, MeteoSimulacija podaci, LocalTime vrijemeDogađanja,
    boolean odstupanjeTemperature, boolean odstupanjeTlaka, boolean odstupanjeVlage) {

}
