package frc.robot.subsystems;

import java.util.Set;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.Presets;
import frc.robot.Robot;
import frc.robot.RobotMap;
import frc.robot.commands.HatchControls;

public class Hatch extends Subsystem {
  
	public final WPI_TalonSRX hatchLift = RobotMap.hatchLift;
	public final WPI_TalonSRX hatchCollect = RobotMap.hatchCollect;

	public final DigitalInput limitUp = RobotMap.hatchFlipLimitSwitchUp;
	public final DigitalInput limitDown = RobotMap.hatchFlipLimitSwitchDown;

	public final DigitalInput collectInSensor = RobotMap.collectorInSensor;
	public final DigitalInput duckInSensor = RobotMap.duckInSensor;

	private double lift;
	private double collect;
	private final double ANGLE_UP=0;
	private final double ANGLE_CENTER=100;
	private final double ANGLE_DOWN=200;
	public final double collectorPower=0.5;
	private double collectorValue=0;
	private double P = .7;
	private double I = 0.004;
	private double D = 0;
	private double K = 0;
	private int IZ = 300;
	private double PO = 1;
	private int CLE = 0;

	public Hatch() {
		/* Reset all motors */
		hatchLift.configFactoryDefault();
		hatchCollect.configFactoryDefault();

  		hatchLift.setSafetyEnabled(false);
		hatchCollect.setSafetyEnabled(false);
		/* Set Neutral Mode */
		hatchLift.setNeutralMode(NeutralMode.Brake);
		hatchCollect.setNeutralMode(NeutralMode.Brake);
		
		/* Configure the left Talon's selected sensor to a Quad Encoder*/
		hatchLift.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, Presets.timeoutMS);
		
		/* Configure the right Talon's selected sensor to a Quad Encoder*/
		hatchCollect.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder, 0, Presets.timeoutMS);
		/* Configure output and sensor direction */
		hatchLift.setInverted(false);
		hatchCollect.setInverted(false);

		/* FPID Gains for a lot of things. */
		hatchLift.config_kP(0, P);
		hatchLift.config_kI(0, I);
		hatchLift.config_kD(0, D);
		hatchLift.config_kF(0, K, Presets.timeoutMS);
		//rightMaster.config_IntegralZone(0, IZ, Presets.timeoutMS);
		hatchLift.configClosedLoopPeakOutput(0, PO, Presets.timeoutMS);
		hatchLift.configAllowableClosedloopError(0, CLE, Presets.timeoutMS);

		hatchCollect.config_kP(0, P);
		hatchCollect.config_kI(0, I);
		hatchCollect.config_kD(0, D);
		hatchCollect.config_kF(0, K, Presets.timeoutMS);
		//hatchCollect.config_IntegralZone(0, IZ, Presets.timeoutMS);
		hatchCollect.configClosedLoopPeakOutput(0, PO, Presets.timeoutMS);
		hatchCollect.configAllowableClosedloopError(0, CLE, Presets.timeoutMS);

		/**
		 * Max out the peak output (for all modes). 
		 * However you can limit the output of a given PID object with configClosedLoopPeakOutput().
		 */

		hatchLift.configPeakOutputForward(1.0, Presets.timeoutMS);
		hatchLift.configPeakOutputReverse(-1.0, Presets.timeoutMS);

		hatchCollect.configPeakOutputForward(1.0, Presets.timeoutMS);
		hatchCollect.configPeakOutputReverse(-1.0, Presets.timeoutMS);

	}
    
    @Override
  public void initDefaultCommand() {
    setDefaultCommand(new HatchControls(Presets.deadzone));
	}

  public void periodic() {
		lift = hatchLift.getSelectedSensorPosition();
		collect = hatchCollect.getSelectedSensorPosition();
		boolean collectorIn=collectInSensor.get();
		boolean duckIn=duckInSensor.get();
		Robot.networktable.table.getEntry("CollectorInSensorRaw").setBoolean(collectorIn);
		Robot.networktable.table.getEntry("DuckInSensorRaw").setBoolean(duckIn);
		if(collectorIn) {
			Robot.networktable.table.getEntry("CollectorInSensor").setString("Hatch Collector: Loaded!");
		} else {
			Robot.networktable.table.getEntry("CollectorInSensor").setString("Hatch Collector: Empty!");
		}
		if(duckIn) {
			Robot.networktable.table.getEntry("DuckInSensor").setString("Duck: Loaded!");
		} else {
			Robot.networktable.table.getEntry("DuckInSensor").setString("Duck: Empty!");
		}
		if(collectorIn) {
			collectorValue=Math.max(0,collectorValue);
		}
		if(collectorValue==0) {
			setCollectorBase(collectorValue);
		} else {
			collectorZero();
		}
		// boolean[] state = getLimitSwitchState();
		// double velocity = hatchLift.getSelectedSensorVelocity();
		/*if (Robot.networktable.table.getEntry("changeP").getDouble(P) != P ||
		Robot.networktable.table.getEntry("changeI").getDouble(I) != I ||
		Robot.networktable.table.getEntry("changeD").getDouble(D) != D ||
		Robot.networktable.table.getEntry("changeK").getDouble(K) != K ||
		(int)Robot.networktable.table.getEntry("changeIZ").getDouble(IZ) != IZ ||
		Robot.networktable.table.getEntry("changePO").getDouble(PO) != PO ||
		(int)Robot.networktable.table.getEntry("changeCLE").getDouble(CLE) != CLE) PIDChange();*/
	}

	public void setFlipper(double value) {
		hatchLift.set(ControlMode.PercentOutput, value);
	}

	public void setFlipperPosition(double value) {
		hatchLift.set(ControlMode.Position, value);
	}

	public void setCollectorBase(double value) {
		hatchCollect.set(ControlMode.PercentOutput, value);
	}

	public void setCollector(double value) {
		collectorValue=value;
	}

	public void setCollectorPosition(double value) {
		hatchCollect.set(ControlMode.Position, value);
	}

	public void setCollectorVelocity(double value) {
		hatchCollect.set(ControlMode.Velocity, value);
	}

	public void collectorIntake() {
		setCollector(collectorPower);
	}

	public void collectorPurge() {
		setCollector(-collectorPower);
	}

	public void collectorZero() {
		setCollectorVelocity(0);
	}
	
	public void setFlipperUp() {
		setFlipperPosition(ANGLE_UP);
	}

	public void setFlipperCenter() {
		setFlipperPosition(ANGLE_CENTER);
	}

	public void setFlipperDown() {
		setFlipperPosition(ANGLE_DOWN);
	}

	public void fingerLower() {
		Robot.pnuematic.fingerLower();
	}

	public void fingerRaise() {
		Robot.pnuematic.fingerRaise();
	}

	public void hatchExtend() {
		Robot.pnuematic.hatchExtend();
	}

	public void hatchRetract() {
		Robot.pnuematic.hatchRetract();
	}

	public void liftCollect(double lift, double collect) {
		hatchLift.set(ControlMode.PercentOutput, lift);
		hatchCollect.set(ControlMode.PercentOutput, collect);
	}

}