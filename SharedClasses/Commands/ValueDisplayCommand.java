package org.gerryrig.SharedClasses.Commands;

import java.util.function.Supplier;

import org.gerryrig.SharedClasses.Drawing.BitmapFont;
import org.gerryrig.SharedClasses.Drawing.Point;

import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Subsystem;

public class ValueDisplayCommand extends TextCommand {
    protected Supplier<Number> m_supplier;

    public <T extends Subsystem, BitmapDrawinContext> ValueDisplayCommand(Supplier<Number> supplier, Point origin, BitmapFont font, Color color, T subsystem) {
        super("", origin, font, color, subsystem);
        m_supplier = supplier;
    }

    @Override
    public void execute() {
        // Change our string to the latest value before letting our superclass draw it
        m_text = m_supplier.get().toString();

        // Clear the screen
        m_ledSubsystem.clearScreen(Color.kBlack);

        super.execute();
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
