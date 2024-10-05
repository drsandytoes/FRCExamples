// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import org.gerryrig.SharedClasses.Subsystems.HorizontalMeterSubsystem;
import org.gerryrig.SharedClasses.Subsystems.LEDPanelSubsystem;

import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.configs.MagnetSensorConfigs;
import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.signals.AbsoluteSensorRangeValue;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  private CANcoder m_encoder = new CANcoder(Constants.Encoder.canID);
  private double m_currentPosition = 0.0;
  private double m_currentAbsPosition = 0.0;
  private double m_currentVelocity = 0.0;

  ShuffleboardTab m_encoderTab;
  GenericEntry m_absOffsetEntry;
  GenericEntry m_positionEntry;
  GenericEntry m_shouldUpdatePositionOnEnableEntry;
  GenericEntry m_shouldUpdateAbsOffsetOnEnableEntry;

  private LEDPanelSubsystem m_panel = new LEDPanelSubsystem(Constants.LED.width, Constants.LED.height, Constants.LED.pwmPin);
  private HorizontalMeterSubsystem m_meter = new HorizontalMeterSubsystem(m_panel, 3);

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    initEncoder();
    configureDashboard();
  }

  public void configureDashboard() {
    m_encoderTab = Shuffleboard.getTab("Encoder");
    // Set things up so that the value are automatically sent to the dashboard
    // without having to call putDouble() all the time.
    m_encoderTab.addDouble("Abs Position", () -> m_currentAbsPosition);
    m_encoderTab.addDouble("Position", () -> m_currentPosition);
    m_encoderTab.addDouble("Velocity", () ->  m_currentVelocity);

    // WPI_CANCoder implements sendable, so we can send it directly to the dashboard!
    m_encoderTab.add(m_encoder);

    // Create two entries that allow for updating the position and magnet offset when auto or teleop are enabled.
    // Have both a boolean for whether we should do it, and an entry for the actual value to be used.
    // Let's group the boolean and the value for each into a list so they're organized automatically!
    ShuffleboardLayout m_magnetOffsetLayout = m_encoderTab
      .getLayout("Update Mag Position on Enable", BuiltInLayouts.kList)
      .withSize(2, 2);

      ShuffleboardLayout m_positionLayout = m_encoderTab
      .getLayout("Update Position on Enable", BuiltInLayouts.kList)
      .withSize(2, 2);

    // Put up an offset entry that we can read when the robot is enabled
    m_shouldUpdateAbsOffsetOnEnableEntry = m_magnetOffsetLayout.add("Update on Enable?", false)
    .withWidget(BuiltInWidgets.kToggleSwitch)
    .getEntry();
    m_absOffsetEntry = m_magnetOffsetLayout.add("Abs Position Offset", 0.0).getEntry();

    m_shouldUpdatePositionOnEnableEntry = m_positionLayout.add("Update on Enable?", false)
    .withWidget(BuiltInWidgets.kToggleSwitch)
    .getEntry();
    m_positionEntry = m_positionLayout.add("Position", 0.0).getEntry();
  }

  public void initEncoder() {
        var coderConfig = new CANcoderConfiguration();
        coderConfig.MagnetSensor.AbsoluteSensorRange = AbsoluteSensorRangeValue.Signed_PlusMinusHalf;

        m_encoder.getConfigurator().apply(coderConfig);
  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    // Run the scheduler for subsystems and commands
    CommandScheduler.getInstance().run();

    m_currentAbsPosition = m_encoder.getAbsolutePosition().getValueAsDouble(); // revolutions (absolute) [-0.5, 0.5]
    m_currentPosition = m_encoder.getPosition().getValueAsDouble();    // accumulated revolutions
    m_currentVelocity = m_encoder.getVelocity().getValueAsDouble();    // rotations / second

    updateLEDDisplay();
  }

  public void updateLEDDisplay() {
    // Absolute position (fraction of a circle)
    m_meter.setCenterOutputChannel(0, m_currentAbsPosition * 2.0, Color.kRed, Color.kGreen);

    // Velocity (fractional revolutions per second) +/- 4 revolutions per second
    m_meter.setCenterOutputChannel(1, m_currentVelocity / 4, Color.kBlue, Color.kYellow);

    // Current position (-24 to + 24 revolutions)
    double fractionalPosition = (m_currentPosition / 360.0 / 24);
    m_meter.setCenterOutputChannel(2, fractionalPosition , Color.kGreen, Color.kRed);
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);

    makeUpdatesOnInit();
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      default:
        // Put default auto code here
        break;
    }
  }

  public void makeUpdatesOnInit() {
    boolean shouldUpdatePosition = m_shouldUpdatePositionOnEnableEntry.getBoolean(false);
    boolean shouldUpdateAbsOffset = m_shouldUpdateAbsOffsetOnEnableEntry.getBoolean(false);

    if (shouldUpdateAbsOffset) {
      double newOffset = m_absOffsetEntry.getDouble(0.0);
      MagnetSensorConfigs sensorConfig = new MagnetSensorConfigs();
      m_encoder.getConfigurator().refresh(sensorConfig);
      sensorConfig.MagnetOffset = newOffset;
      m_encoder.getConfigurator().apply(sensorConfig);
    }

    if (shouldUpdatePosition) {
      double newPosition = m_positionEntry.getDouble(0.0);
      m_encoder.setPosition(newPosition);
    }
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {
    makeUpdatesOnInit();
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {}

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {}

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {}
}
