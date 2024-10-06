package frc.robot.Subsystems;

import org.gerryrig.SharedClasses.Subsystems.HorizontalMeterSubsystem;
import org.gerryrig.SharedClasses.Subsystems.LEDRegionSubsystem;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Constants.MeterConfig;

// This class manages the main bar graph display on the left side (45x16)
// showing the encoders relative and absolute position, as well as
// the yaw, and x/y/z acceleration from the gyro.
public class DemoDisplaySubsystem extends HorizontalMeterSubsystem {
    double encoderPosition = 0.0;
    double encoderAbsolutePosition = 0.0;
    double accelerationX = 0.0;
    double accelerationY = 0.0;
    double accelerationZ = 0.0;
    Rotation2d currentRotation = new Rotation2d();

    public DemoDisplaySubsystem(LEDRegionSubsystem panel) {
        super(panel, Constants.MeterConfig.kNumChannels);

        clearUnusedArea();
    }

    public void periodic() {
        // Scale absolute encoder position: [-0.5, 0.5] => [-1.0, 1.0]
        setCenterOutputChannel(Constants.MeterConfig.kAbsolutePositionIndex, encoderAbsolutePosition / 0.5, 
            Constants.MeterConfig.kNegativeAbsolutePositionColor, Constants.MeterConfig.kPositiveAbsolutePositionColor);

        // Constrain relative encoder position to [-5.0, 5.0]; scale to [-1.0, 1.0]. The channel will clip automatically so we 
        // don't need to enforce the maximum value here.
        setCenterOutputChannel(Constants.MeterConfig.kPositionIndex, encoderPosition / Constants.MeterConfig.kPositionMax, 
            Constants.MeterConfig.kNegativePositionColor, Constants.MeterConfig.kPositivePositionColor);

        // Scale rotation (yaw): [-0.5, 0.5] => [-1.0, 1.0]
        setCenterOutputChannel(Constants.MeterConfig.kRotationPositionIndex, currentRotation.getRotations() / 0.5, 
            Constants.MeterConfig.kNegativeRotationColor, Constants.MeterConfig.kPositiveRotationColor);

        // Acceleration: [-2.0, 2.0]
        setCenterOutputChannel(Constants.MeterConfig.kAccelerationX, accelerationX / 2.0, 
            Constants.MeterConfig.kNegativeAccelerationColor, Constants.MeterConfig.kPositiveAccelerationColor);
        setCenterOutputChannel(Constants.MeterConfig.kAccelerationY, accelerationY / 2.0, 
            Constants.MeterConfig.kNegativeAccelerationColor, Constants.MeterConfig.kPositiveAccelerationColor);
        setCenterOutputChannel(Constants.MeterConfig.kAccelerationZ, accelerationZ / 2.0, 
            Constants.MeterConfig.kNegativeAccelerationColor, Constants.MeterConfig.kPositiveAccelerationColor);
    }

    public void setEncoderState(double position, double absolutePosition) {
        encoderPosition = position;
        encoderAbsolutePosition = absolutePosition;
    }

    public void setRotation(Rotation2d rotation) {
        currentRotation = rotation;
    }

    public void setAccelerationX(double acceleration) {
        accelerationX = acceleration;
    }

    public void setAccelerationY(double acceleration) {
        accelerationY = acceleration;
    }

    public void setAccelerationZ(double acceleration) {
        accelerationZ = acceleration;
    }
}
