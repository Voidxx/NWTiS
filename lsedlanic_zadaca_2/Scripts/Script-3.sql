SELECT ICAO_TO, DIST_TOT FROM AIRPORTS_DISTANCE_MATRIX WHERE EXISTS(SELECT ICAO_FROM FROM AIRPORTS_DISTANCE_MATRIX WHERE ICAO_TO = 'LDZA') AND ICAO_TO != 'LDZA' ORDER BY ICAO_FROM OFFSET 1 ROWS FETCH NEXT 20 ROWS ONLY