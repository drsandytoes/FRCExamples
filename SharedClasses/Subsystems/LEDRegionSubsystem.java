package org.gerryrig.SharedClasses.Subsystems;

import org.gerryrig.SharedClasses.Drawing.BitmapDrawingContext;
import org.gerryrig.SharedClasses.Drawing.Point;
import org.gerryrig.SharedClasses.Drawing.Rectangle;

import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

/**
 * This class vends a subsystem that allows drawing in a certain region of an
 * underlying LEDPanelSubsystem (or other object implementing BitmapDrawingContext).
 * This allows commands to require a subsystem that owns only part of the underlying
 * LED panel, rather than having to require the entire LEDPanelSubsystem, thereby locking
 * the entire panel. Ideally, a manager object would ensure that regions are not 
 * overlapping, but that is left to the programmer for now.
 */
public class LEDRegionSubsystem extends SubsystemBase implements BitmapDrawingContext {
    protected Rectangle m_region;
    protected BitmapDrawingContext m_underlyingContext;
    protected boolean useBrightnessOverride = false;
    protected double brightnessOverride = 1.0;

    public LEDRegionSubsystem(BitmapDrawingContext realContext, Rectangle region) {
        m_region = new Rectangle(region);
        m_underlyingContext = realContext;
    }

    public void setBrightnessOverride(double brightness) {
        this.useBrightnessOverride = true;
        this.brightnessOverride = brightness;
    }

    // Helper methods
    public boolean isValidPoint(Point p) {
        if (p.x >= 0 && p.x < m_region.width && p.y >= 0 && p.y < m_region.height) {
            return true;
        } else {
            return false;
        }
    }

    public Point realPointFromRegionRelativePoint(Point regionPoint) {
        Point translatedPoint = new Point(regionPoint);
        translatedPoint.translate(m_region.x, m_region.y);
        return translatedPoint;
    }

    // BitmapDrawingContext methods
    public int width() {
        return m_region.width;
    }

    public int height() {
        return m_region.height;
    }

    public void setPixelByXY(int x, int y, Color color) {
        Point regionRelativePoint = new Point(x, y);
        if (isValidPoint(regionRelativePoint)) {
            Point realPoint = realPointFromRegionRelativePoint(regionRelativePoint);
            if (useBrightnessOverride) {
                m_underlyingContext.setPixelByXY(realPoint.x, realPoint.y, color, brightnessOverride);
            } else {
                m_underlyingContext.setPixelByXY(realPoint.x, realPoint.y, color);
            }
        }
    }

    public void setPixelByXY(int x, int y, Color color, double brightness) {
        Point regionRelativePoint = new Point(x, y);
        if (isValidPoint(regionRelativePoint)) {
            Point realPoint = realPointFromRegionRelativePoint(regionRelativePoint);
            m_underlyingContext.setPixelByXY(realPoint.x, realPoint.y, color, brightness);
        }
    }

}
