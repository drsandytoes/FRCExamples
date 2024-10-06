package org.gerryrig.SharedClasses.Drawing;

import edu.wpi.first.wpilibj.util.Color;

// This interface defines a common rectangular drawing context that can be used by 
// various things that expect something like an LED panel. It's abstracted to allow
// for subdividing a real LED panel into subregions. 

public interface BitmapDrawingContext {
    public void setPixelByXY(int x, int y, Color color);
    public void setPixelByXY(int x, int y, int red, int green, int blue);
    public void clearScreen(Color color);

    public int width();
    public int height();
}
