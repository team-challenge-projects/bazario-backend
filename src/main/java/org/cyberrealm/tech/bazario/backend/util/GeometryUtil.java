package org.cyberrealm.tech.bazario.backend.util;

import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

public class GeometryUtil {
    private static final GeometryFactory FACTORY = new GeometryFactory(new PrecisionModel(), 4326);
    private static final int EARTH_RADIUS_KM = 6371;

    public static Point createPoint(String wkt) {
        if (wkt == null || wkt.trim().isEmpty()) {
            return null;
        }
        WKTReader reader = new WKTReader(FACTORY);

        try {
            var geom = reader.read(wkt.trim());
            if (!(geom instanceof Point)) {
                throw new IllegalArgumentException("Not a point: " + geom.getGeometryType());
            }
            return (Point) geom;
        } catch (ParseException e) {
            return null;
        }

    }

    public static double haversine(Point startPoint, Point endPoint) {
        if (startPoint == null || endPoint == null) {
            return 0.0;
        }

        double longitudeStart = startPoint.getX();
        double latitudeStart = startPoint.getY();
        double longitudeEnd = endPoint.getX();
        double latitudeEnd = endPoint.getY();

        double latDistance = Math.toRadians(Math.abs(latitudeEnd - latitudeStart));
        double lonDistance = Math.toRadians(Math.abs(longitudeEnd - longitudeStart));
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(latitudeStart)) * Math.cos(Math.toRadians(latitudeEnd))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }
}
