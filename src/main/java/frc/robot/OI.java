package frc.robot;

import edu.wpi.first.wpilibj.buttons.JoystickButton;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {
	public XboxController driverControl;
	public XboxController operatorControl;
	
	public JoystickButton RobotTeleInit;
	public JoystickButton visionButton;
	public JoystickButton driverCancel;
	public JoystickButton lowGearHold;
	public JoystickButton highGearHold;
	public JoystickButton motorTest;

	public JoystickButton operatorCancel;
	public JoystickButton cargoIn;
	public JoystickButton cargoOut;
	public JoystickButton duckOut;
	public JoystickButton lidarUp;
	public JoystickButton foldIn;
	public JoystickButton hatchUp;
	public JoystickButton hatchDown;
	public JoystickButton precisionMode;
	
    public OI() {
    	
    	driverControl = new XboxController(0);
		operatorControl = new XboxController(1);
		
		lowGearHold = driverControl.leftBumper;
		highGearHold = driverControl.rightBumper;
		
		driverCancel = driverControl.a;
		
		cargoIn = 

		operatorCancel = operatorControl.a;
	}
}