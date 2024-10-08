package org.gerryrig.SharedClasses.Drawing;

import edu.wpi.first.wpilibj.util.Color;

// This interface defines a common rectangular drawing context that can be used by 
// various things that expect something like an LED panel. It's abstracted to allow
// for subdividing a real LED panel into subregions. 

public interface BitmapDrawingContext {
    public void setBrightnessOverride(double brightness);

    public void setPixelByXY(int x, int y, Color color);

    // Overrides brightness at the panel level
    public void setPixelByXY(int x, int y, Color color, double brightness); 

    public int width();
    public int height();

    default public void clearScreen(Color color) {
        color = color != null ? color : Color.kBlack;
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                setPixelByXY(x, y, color);
            }
        }
    }

    default public void setPixelByXY(int x, int y, int red, int green, int blue) {
        Color color = new Color(red, green, blue);
        setPixelByXY(x, y, color);
    }

    default public void setPixelByXY(int x, int y, int red, int green, int blue, double brightness) {
        Color color = new Color(red, green, blue);
        setPixelByXY(x, y, color, brightness);
    }
}
