package frc.robot.Subsystems;

import org.gerryrig.SharedClasses.Drawing.BitmapDrawingContext;

import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class DemoSolidSquareDisplaySubsystem extends SubsystemBase {
    BitmapDrawingContext panel;
    Color color = Color.kBlack;

    public DemoSolidSquareDisplaySubsystem(BitmapDrawingContext context) {
        panel = context;
        context.setBrightnessOverride(1.0);
    }

    public void periodic() {
        panel.clearScreen(color);
    }

    public void set(boolean on) {
        color = on ? new Color(255, 0, 0) : new Color(0, 255, 0);
    }

    public void setOn() {
        set(true);
    }

    public void setOff() {
        set(false);
    }

    public void setColor(Color color) {
        this.color = color;
    }
    
}
