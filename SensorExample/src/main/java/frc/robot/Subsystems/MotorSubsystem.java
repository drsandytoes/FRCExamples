package frc.robot.Subsystems;

import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.hardware.TalonFX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class MotorSubsystem extends SubsystemBase {
    private TalonFX motor;
    private final VoltageOut request = new VoltageOut(0);
    private double currentOutput = 0.0;

    public MotorSubsystem(TalonFX motor) {
        this.motor = motor;
    }

    public void periodic() {
        motor.setControl(request.withOutput(currentOutput));
    }

    public void setPercentOutput(double percent) {
        currentOutput = percent * 12.0;
    }
}
