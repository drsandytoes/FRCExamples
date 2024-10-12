// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import org.gerryrig.SharedClasses.Subsystems.LEDPanelSubsystem;
import org.gerryrig.SharedClasses.Subsystems.LEDRegionSubsystem;

import com.ctre.phoenix6.configs.Pigeon2Configuration;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.Pigeon2;
import com.ctre.phoenix6.hardware.TalonFX;
import com.revrobotics.ColorSensorV3;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorMatch;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.RunCommand;
import frc.robot.Subsystems.DemoDisplaySubsystem;
import frc.robot.Subsystems.DemoSolidSquareDisplaySubsystem;
import frc.robot.Subsystems.MotorSubsystem;

public class RobotContainer {
    // LED configuration
    private final LEDPanelSubsystem panel = new LEDPanelSubsystem(Constants.LEDPanel.kMatrixWidth,
            Constants.LEDPanel.kMatrixHeight, Constants.LEDPanel.kLEDPWMPin);
    private final LEDRegionSubsystem mainDisplay = new LEDRegionSubsystem(panel,
            Constants.LEDPanel.LEDRegions.kMainPanel);
    private final LEDRegionSubsystem encoderSwitchDisplay = new LEDRegionSubsystem(panel,
            Constants.LEDPanel.LEDRegions.kEncoderSwitchDisplay);
    private final LEDRegionSubsystem gyroSwitchDisplay = new LEDRegionSubsystem(panel,
            Constants.LEDPanel.LEDRegions.kGryoSwitchDisplay);
    private final LEDRegionSubsystem colorSensorDisplay = new LEDRegionSubsystem(panel,
            Constants.LEDPanel.LEDRegions.kColorSensorDisplay);

    // Hardware
    private final CANcoder encoder = new CANcoder(Constants.Encoder.id);

    private final DigitalInput gyroLimitSwitch = new DigitalInput(Constants.LimitSwitch.gyroSwitch);
    private final DigitalInput encoderLimitSwitch = new DigitalInput(Constants.LimitSwitch.encoderSwitch);

    private final Pigeon2 pigeon;

    // This block runs each time this class is instantiated (though we should only
    // be doing that once). This allows us
    // to use conditional logic to setup the final instance variable without having
    // to resort to moving this code
    // into the constructor (not that that's necessarily bad).
    {
        Constants.GyroConfig gyroConfig;
        if (RobotController.getSerialNumber().equals(Constants.RobotIdentities.gerryRigSE)) {
            gyroConfig = Constants.Gyro.mike;
        } else {
            gyroConfig = Constants.Gyro.other;
        }
        pigeon = new Pigeon2(gyroConfig.id, gyroConfig.bus);
    }

    private final ColorSensorV3 colorSensor = new ColorSensorV3(Constants.ColorSensor.port);
    private final ColorMatch colorMatcher = new ColorMatch();

    private final TalonFX encoderMotor = new TalonFX(Constants.Motor.encoderMotor);
    private final TalonFX gyroMotor = new TalonFX(Constants.Motor.gyroMotor);

    // These values really should be tuned for the precise color we're looking for.
    private final Color kBlueTarget = new Color(0.143, 0.427, 0.429);
    private final Color kGreenTarget = new Color(0.197, 0.561, 0.240);
    private final Color kRedTarget = new Color(0.561, 0.232, 0.114);
    private final Color kYellowTarget = new Color(0.361, 0.524, 0.113);
    private final Color kBlackTarget = new Color(0.000, 0.000, 0.000);
    private final Color kBlack1Target = new Color(0.5, 0.5, 0.0);
    private final Color kGrayTarget = new Color(0.38, 0.41, 0.21);

    // Demo subsystems
    private final DemoDisplaySubsystem displaySubsystem = new DemoDisplaySubsystem(mainDisplay);
    private final DemoSolidSquareDisplaySubsystem encoderSwitchDisplaySubsystem = new DemoSolidSquareDisplaySubsystem(
            encoderSwitchDisplay);
    private final DemoSolidSquareDisplaySubsystem gyroDisplaySubsystem = new DemoSolidSquareDisplaySubsystem(
            gyroSwitchDisplay);
    private final DemoSolidSquareDisplaySubsystem colorSensorSwitchDisplaySubsystem = new DemoSolidSquareDisplaySubsystem(
            colorSensorDisplay);

    private final MotorSubsystem gyroMotorSubsystem = new MotorSubsystem(gyroMotor);
    private final MotorSubsystem encoderMotorSubsystem = new MotorSubsystem(encoderMotor);

    // Limit how often we do color matches
    private Color lastColorMatch = Color.kWhite;
    private int colorLoopCount = 0;

    /*
     * Main display (45 x 16) -- all bidirectional graphs
     * Absolute position
     * Relative position
     * Yaw
     * X acceleration
     * Y acceleration
     * Z acceleration
     * 
     * Right display (4 x 16) (which includes one column off the end) -- solid
     * colors
     * Limit switch 1 (4 x 4)
     * Limit switch 2 (4 x 4)
     * Detected color (4 x 4)
     * Unused: (4 x 4)
     */

    public RobotContainer() {
        System.out.println("Robot SN: " + RobotController.getSerialNumber());
        configurePigeon();
        configureColorSensor();

        configureBindings();

        setupDefaultCommands();
    }

    private void configureColorSensor() {
        colorMatcher.addColorMatch(kBlueTarget);
        colorMatcher.addColorMatch(kGreenTarget);
        colorMatcher.addColorMatch(kRedTarget);
        colorMatcher.addColorMatch(kYellowTarget);
        colorMatcher.addColorMatch(kBlackTarget);
        colorMatcher.addColorMatch(kBlack1Target);
        colorMatcher.addColorMatch(kGrayTarget);
    }

    private void configurePigeon() {
        Pigeon2Configuration configs = new Pigeon2Configuration();

        // This Pigeon is mounted Z-up, which should be the default
        configs.MountPose.MountPoseYaw = 0;
        configs.MountPose.MountPosePitch = 0;
        configs.MountPose.MountPoseRoll = 0;

        // This Pigeon has no need to trim the gyro
        configs.GyroTrim.GyroScalarX = 0;
        configs.GyroTrim.GyroScalarY = 0;
        configs.GyroTrim.GyroScalarZ = 0;

        // We want the thermal comp and no-motion cal enabled, with the compass disabled
        // for best behavior
        configs.Pigeon2Features.DisableNoMotionCalibration = false;
        configs.Pigeon2Features.DisableTemperatureCompensation = false;
        configs.Pigeon2Features.EnableCompass = false;

        // Write these configs to the Pigeon2
        pigeon.getConfigurator().apply(configs);

        // Set the yaw to 0 degrees for initial use
        pigeon.setYaw(0);
    }

    private void configureBindings() {
    }

    private void setupDefaultCommands() {
        displaySubsystem.setDefaultCommand(new RunCommand(() -> {
            // Force the rotation to normalize
            Rotation2d normalizedRotation = pigeon.getRotation2d().plus(new Rotation2d());

            displaySubsystem.setEncoderState(encoder.getPosition().getValueAsDouble(),
                    encoder.getAbsolutePosition().getValueAsDouble());
            displaySubsystem.setRotation(normalizedRotation);
            displaySubsystem.setAccelerationX(pigeon.getAccelerationX().getValueAsDouble());
            displaySubsystem.setAccelerationY(pigeon.getAccelerationY().getValueAsDouble());
            displaySubsystem.setAccelerationZ(pigeon.getAccelerationZ().getValueAsDouble());
        }, displaySubsystem).ignoringDisable(true));

        gyroDisplaySubsystem.setDefaultCommand(new RunCommand(() -> {
            gyroDisplaySubsystem.set(gyroLimitSwitch.get());
        }, gyroDisplaySubsystem).ignoringDisable(true));

        encoderSwitchDisplaySubsystem.setDefaultCommand(new RunCommand(() -> {
            encoderSwitchDisplaySubsystem.set(encoderLimitSwitch.get());
        }, encoderSwitchDisplaySubsystem).ignoringDisable(true));

        colorSensorSwitchDisplaySubsystem.setDefaultCommand(new RunCommand(() -> {
            updateMatchedColor();
            colorSensorSwitchDisplaySubsystem.setColor(lastColorMatch);
        }, colorSensorSwitchDisplaySubsystem).ignoringDisable(true));

        // Notor ones won't run while disabled
        encoderMotorSubsystem.setDefaultCommand(new RunCommand(() -> {
            // Scale encoder relative position to motor output:
            double encoderPosition = encoder.getPosition().getValueAsDouble();

            // Constrain encoder position to [-4, 4]
            encoderPosition = Math.max(encoderPosition, -4.0);
            encoderPosition = Math.min(encoderPosition, 4.0);

            double powerOutput = Constants.Motor.maxMotorOutputFraction * (encoderPosition / 4.0);
            if (Math.abs(powerOutput) < Constants.Motor.deadband || encoderLimitSwitch.get() == true) {
                powerOutput = 0.0;
            }
            encoderMotorSubsystem.setPercentOutput(powerOutput);
        }, encoderMotorSubsystem));

        gyroMotorSubsystem.setDefaultCommand(new RunCommand(() -> {
            // Convert Y acceleration to motor output. Normally it will be [-1.0, 1.0] g.
            double accelerationY = pigeon.getAccelerationY().getValueAsDouble();
            double powerOutput = Constants.Motor.maxMotorOutputFraction * accelerationY;
            if (Math.abs(powerOutput) < Constants.Motor.deadband || gyroLimitSwitch.get() == true) {
                powerOutput = 0.0;
            }
            gyroMotorSubsystem.setPercentOutput(powerOutput);
        }, gyroMotorSubsystem));
    }

    private void updateMatchedColor() {
        colorLoopCount++;
        if (colorLoopCount > 10) {
            colorLoopCount = 0;

            Color detectedColor = colorSensor.getColor();

            /**
             * Run the color match algorithm on our detected color
             */
            Color matchedColor = Color.kWhite;
            ColorMatchResult match = colorMatcher.matchClosestColor(detectedColor);

            if (match.color == kBlueTarget) {
                matchedColor = Color.kBlue;
            } else if (match.color == kRedTarget) {
                matchedColor = Color.kRed;
            } else if (match.color == kGreenTarget) {
                matchedColor = Color.kGreen;
            } else if (match.color == kYellowTarget) {
                matchedColor = Color.kYellow;
            } else if (match.color == kBlackTarget) {
                matchedColor = Color.kBlack;
            } else if (match.color == kBlack1Target) {
                matchedColor = Color.kBlack;
            } else if (match.color == kGrayTarget) {
                matchedColor = Color.kGray;
            }

            lastColorMatch = matchedColor;
        }
    }

    public Command getAutonomousCommand() {
        return Commands.print("No autonomous command configured");
    }
}
