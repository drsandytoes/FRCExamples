// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.led.Animation;
import com.ctre.phoenix.led.CANdle;
import com.ctre.phoenix.led.CANdle.LEDStripType;
import com.ctre.phoenix.led.CANdleConfiguration;
import com.ctre.phoenix.led.FireAnimation;
import com.ctre.phoenix.led.RainbowAnimation;
import com.ctre.phoenix.led.StrobeAnimation;
import com.ctre.phoenix.led.TwinkleAnimation;
import com.ctre.phoenix.led.TwinkleAnimation.TwinklePercent;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

public class RobotContainer {
    private CANdle candle = new CANdle(1);

    private final int TwinkleOffset = 0;
    private final int TwinkleLength = 8;
    private final int RainbowOffset = TwinkleOffset + TwinkleLength;
    private final int RainbowLength = 64;
    private final int StrobeOffset = RainbowOffset + RainbowLength;
    private final int StrobeLength = 8;
    private final int FireOffset = StrobeOffset + StrobeLength;
    private final int FireLength = 32;

    public RobotContainer() {
        CANdleConfiguration config = new CANdleConfiguration();
        config.stripType = LEDStripType.RGB; // set the strip type to RGB
        config.brightnessScalar = 0.5; // dim the LEDs to half brightness
        candle.configAllSettings(config);

        TwinkleAnimation twinkle = new TwinkleAnimation(255, 255, 255);
        twinkle.setDivider(TwinklePercent.Percent30);
        twinkle.setLedOffset(0);
        twinkle.setLedOffset(TwinkleOffset);
        twinkle.setNumLed(TwinkleLength);

        RainbowAnimation rainbow = new RainbowAnimation();
        rainbow.setLedOffset(RainbowOffset);
        rainbow.setNumLed(RainbowLength);
        rainbow.setSpeed(0.75);

        StrobeAnimation strobe = new StrobeAnimation(0, 255, 0);
        strobe.setSpeed(0.05);
        strobe.setLedOffset(StrobeOffset);
        strobe.setNumLed(StrobeLength);

        FireAnimation fire = new FireAnimation();
        fire.setLedOffset(FireOffset);
        fire.setNumLed(FireLength);

        candle.animate(twinkle, 0);
        candle.animate(rainbow, 1);
        candle.animate(strobe, 2);
        candle.animate(fire, 3);

        configureBindings();
    }

    private void configureBindings() {
    }

    public Command getAutonomousCommand() {
        return Commands.print("No autonomous command configured");
    }
}
