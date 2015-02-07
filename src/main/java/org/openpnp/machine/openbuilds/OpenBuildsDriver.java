package org.openpnp.machine.openbuilds;

import java.util.Locale;

import org.openpnp.machine.reference.ReferenceHeadMountable;
import org.openpnp.machine.reference.ReferenceNozzle;
import org.openpnp.machine.reference.driver.MarlinDriver;
import org.openpnp.model.LengthUnit;
import org.openpnp.model.Location;
import org.openpnp.spi.Nozzle;
import org.simpleframework.xml.Attribute;

public class OpenBuildsDriver extends MarlinDriver {
    @Attribute
    private double zCamRadius = 26; 
            
    @Override
    public void moveTo(ReferenceHeadMountable hm, Location location, double speed)
            throws Exception {
        location = location.subtract(hm.getHeadOffsets());

        location = location.convertToUnits(LengthUnit.Millimeters);
        
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();
        double c = location.getRotation();
        
        /*
         * Only move C and Z if it's a Nozzle.
         */
        if (!(hm instanceof Nozzle)) {
            c = Double.NaN;
            z = Double.NaN;
        }
        
        StringBuffer sb = new StringBuffer();
        if (!Double.isNaN(x) && x != this.x) {
            sb.append(String.format(Locale.US, "X%2.2f ", x));
        }
        if (!Double.isNaN(y) && y != this.y) {
            sb.append(String.format(Locale.US, "Y%2.2f ", y));
        }
        if (!Double.isNaN(z) && z != this.z) {
            double degrees = Math.toDegrees(Math.asin(z / zCamRadius));
            if (hm instanceof ReferenceNozzle) {
                ReferenceNozzle nozzle = (ReferenceNozzle) hm;
                if (nozzle.getId().equals("N2")) {
                    degrees = -degrees;
                }
            }
            sb.append(String.format(Locale.US, "Z%2.2f ", degrees));
        }
        if (!Double.isNaN(c) && c != this.c) {
            sb.append(String.format(Locale.US, "E%2.2f ", c));
        }
        if (sb.length() > 0) {
            sb.append(String.format(Locale.US, "F%2.2f", feedRateMmPerMinute));
            sendCommand("G0 " + sb.toString());
            dwell();
        }
        if (!Double.isNaN(x)) {
            this.x = x;
        }
        if (!Double.isNaN(y)) {
            this.y = y;
        }
        if (!Double.isNaN(z)) {
            this.z = z;
        }
        if (!Double.isNaN(c)) {
            this.c = c;
        }
    }
}