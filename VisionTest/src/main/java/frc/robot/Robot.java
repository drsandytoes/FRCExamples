// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.LimelightHelpers.PoseEstimate;

public class Robot extends TimedRobot {
    private Command m_autonomousCommand;

    private final RobotContainer m_robotContainer;

    private int loopCount = 0;

    private static final int LOOPS_PER_SEC = 50;

    public Robot() {
        m_robotContainer = new RobotContainer();
    }

    @Override
    public void robotPeriodic() {
        CommandScheduler.getInstance().run();

        loopCount++;
        if (loopCount > 10 * LOOPS_PER_SEC) {
            loopCount = 0;
            double tagPose[] = LimelightHelpers.getTargetPose_RobotSpace("limelight-santos");
            double tx = LimelightHelpers.getTX("limelight-santos");
            double ty = LimelightHelpers.getTY("limelight-santos");
            if (tagPose.length > 0) {
                System.out.println(String.format("X: %.2fm, Y: %.2fm, Z: %.2fm\n",
                        tagPose[0], tagPose[1], tagPose[2]));
                // System.out.println(String.format("Roll: %.2f deg, Pitch: %.2f deg, Yaw: %.2f
                // deg\n",
                // tagPose[3], tagPose[4], tagPose[5]));
                System.out.println(String.format("Tx: %.2f deg, Ty: %.2f deg\n", tx, ty));

                PoseEstimate poseEstimate = LimelightHelpers.getBotPoseEstimate_wpiBlue("limelight-santos");
                if (poseEstimate != null) {
                    Pose2d pose = poseEstimate.pose;
                    double x = pose.getX();
                    double y = pose.getY();
                    System.out.println(String.format("Approximate field coordinates (%.2f, %.2f)\n", x, y));
                }
            } else {
                System.out.println("No target pose avaialble");
            }
        }

    }

    @Override
    public void disabledInit() {
    }

    @Override
    public void disabledPeriodic() {
    }

    @Override
    public void disabledExit() {
    }

    @Override
    public void autonomousInit() {
        m_autonomousCommand = m_robotContainer.getAutonomousCommand();

        if (m_autonomousCommand != null) {
            m_autonomousCommand.schedule();
        }
    }

    @Override
    public void autonomousPeriodic() {
    }

    @Override
    public void autonomousExit() {
    }

    @Override
    public void teleopInit() {
        if (m_autonomousCommand != null) {
            m_autonomousCommand.cancel();
        }
    }

    @Override
    public void teleopPeriodic() {
    }

    @Override
    public void teleopExit() {
    }

    @Override
    public void testInit() {
        CommandScheduler.getInstance().cancelAll();
    }

    @Override
    public void testPeriodic() {
    }

    @Override
    public void testExit() {
    }
}
