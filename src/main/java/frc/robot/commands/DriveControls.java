package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;

/**
 * This is the driver movement controls
 * for the teleoperated period of a match.
 * It is also the default command for 
 * the Drivetrain.java subsystem.
 * 
 * This command does not finish.
 * 
 * @author Nathaniel
 * @see /subsystems/DriveTrain.java
 * @category Drive Command
 */
public class DriveControls extends Command {
	
	/** Controller Dead Zone */
	private double deadzone;

	/** Controller Data: Left Y */
	private double forward;
	
	/** Controller Data: Right X */
	private double rotational;

	/** Output for Motor Power */
	private double leftMotorOutput, rightMotorOutput;

	private double executes = 0;


	/**
	 * This is the driver movement controls
	 * for the teleoperated period of a match.
	 * It is also the default command for
	 * the robotDrivetrain.java subsystem.
	 * 
	 * This command requires the drivetrain subsystem
	 * so as to give it priority over other commands 
	 * accessing the drivetrain.
	 * 
	 * This command does not finish.
	 * 
	 * @author Nathaniel
	 * @see /subsystems/Drivetrain.java
	 * @category Drive Command
	 */
	public DriveControls(double deadzone) {
		requires(Robot.drivetrain);
		this.deadzone = deadzone;
	}

	protected void initialize() {
	}

	/** Called Repeatedly */
	protected void execute() {
		/* Controller Data */
		forward = Robot.oi.driverControl.getRawAxis(1);
		rotational = Robot.oi.driverControl.getRawAxis(4);
		
		/* Outputs Checked Controller Data to Motors */
		arcaderDrive(limit(deadZoneCheck(forward)), limit(deadZoneCheck(rotational)));
	}

	/**
   	 * Arcade drive method for differential drive platform.
   	 *
   	 * @param fwd The robot's speed along the X axis [-1.0..1.0]. Forward is positive.
   	 * @param rot The robot's rotation rate around the Z axis [-1.0..1.0]. Clockwise is positive.
   	 */
	public void arcaderDrive(double fwd, double rot) {
		double maxInput = Math.copySign(Math.max(Math.abs(fwd), Math.abs(rot)), fwd);

		if (fwd >= 0.0) {
			if (rot >= 0.0) {
				leftMotorOutput = maxInput;
				rightMotorOutput = fwd - rot;
			} else {
				leftMotorOutput = fwd + rot;
				rightMotorOutput = maxInput;
			}
		} else {
			if (rot >= 0.0) {
				leftMotorOutput = fwd + rot;
				rightMotorOutput = maxInput;
			} else {
				leftMotorOutput = maxInput;
				rightMotorOutput = fwd - rot;
			}
		}
		/* Percent Output */
		if (forward == 0 && rotational == 0) {
			if (executes > 20) Robot.drivetrain.zero();
			executes++;
		}
		else {
			Robot.drivetrain.tank(limit(leftMotorOutput), (limit(rightMotorOutput)), 400);
		}

		/* Velocity Output 
		Robot.drivetrain.velocity(speedModifier(limit(rightMotorOutput)), speedModifier(limit(leftMotorOutput)));*/
	}

	/** 
	 * @param val Input to check against dead zone
	 * @return If within dead zone return 0, Else return val
	 */
	private double deadZoneCheck(double val) {
		if (Math.abs(val) < deadzone) return 0;
		return val;

	}

	private double speedModifier(double val) {
		return Math.copySign(Math.pow(Math.abs(val), 3) * 1300, val);
	}

  	/**
   	 * Limit motor values to the -1.0 to +1.0 range.
   	 */
  	private double limit(double value) {
    	if (Math.abs(value) > 1.0) return Math.copySign(1, value);
    	return value;
  	}

	/** 
	 * This command should never finish as it 
	 * must remain active for the duration of
	 * any teleoperated period, and is only run
	 * during the teleoperated period.
	 */
	protected boolean isFinished() {
		return false;
	}
}