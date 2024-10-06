package frc.robot;

import org.gerryrig.SharedClasses.Drawing.Rectangle;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.util.Color;

public final class Constants {
    public final class ColorSensor {
        public static final I2C.Port port = I2C.Port.kOnboard;
    }

    public final class Encoder {
        public static final int id = 22;
    }

    public final class LimitSwitch {
        public static final int gyroSwitch = 0;
        public static final int encoderSwitch = 1;
    }

    public final class Motor {
        public static final int gyroMotor = 30;
        public static final int encoderMotor = 31;

        public static final double deadband = 0.01; // percent output
        public static final double maxMotorOutputFraction = 0.25;
    }

    public final class Gyro {
        public static final int id = 10;
    }

    public final class LEDPanel {
        public static final int kLEDPWMPin = 0;
        public static final int kMatrixHeight = 16;
        public static final int kMatrixWidth = 16 * 3 + 1;

        public static final double kBrightness = 0.2;

        public final static class LEDRegions {
            // Main 3x1 panel display (45 x 16)
            public final static Rectangle kMainPanel = new Rectangle(0, 0, 45, kMatrixHeight);

            // Gyro switch display
            public final static Rectangle kGryoSwitchDisplay = new Rectangle(45, 0, 4, 4);

            // Encoder switch display
            public final static Rectangle kEncoderSwitchDisplay = new Rectangle(45, 4, 4, 4);

            // Color sensor display
            public final static Rectangle kColorSensorDisplay = new Rectangle(45, 8, 4, 4);
        }
    }

    public final class MeterConfig {
        public static final int kPositionIndex = 0;
        public static final Color kPositivePositionColor = Color.kWhite;
        public static final Color kNegativePositionColor = Color.kGray;
        public static final double kPositionMax = 5.0;

        public static final int kAbsolutePositionIndex = 1;
        public static final Color kPositiveAbsolutePositionColor = Color.kBlue;
        public static final Color kNegativeAbsolutePositionColor = Color.kLightBlue;

        public static final int kRotationPositionIndex = 2;
        public static final Color kPositiveRotationColor = Color.kPurple;
        public static final Color kNegativeRotationColor = Color.kOrange;

        public static final int kAccelerationX = 3;
        public static final int kAccelerationY = 4;
        public static final int kAccelerationZ = 5;
        public static final Color kPositiveAccelerationColor = Color.kGreen;
        public static final Color kNegativeAccelerationColor = Color.kRed;


        public static final int kNumChannels = 6;
    }

}
