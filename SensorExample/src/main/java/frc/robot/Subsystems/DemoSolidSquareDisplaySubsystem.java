package frc.robot.Subsystems;

import org.gerryrig.SharedClasses.Drawing.BitmapDrawingContext;

import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class DemoSolidSquareDisplaySubsystem extends SubsystemBase {
    BitmapDrawingContext panel;
    Color color = Color.kBlack;

    public DemoSolidSquareDisplaySubsystem(BitmapDrawingContext context) {
        panel = context;
    }

    public void periodic() {
        panel.clearScreen(color);
    }

    public void set(boolean on) {
        color = on ? Color.kRed : Color.kGreen;
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
