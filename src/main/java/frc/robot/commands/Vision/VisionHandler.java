package frc.robot.commands.Vision;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.Vision;

public class VisionHandler extends Command {

    private Vision vision = Robot.vision;
    private Line[] lines;
    private Blob[] blobs;
    private Point[] points;
    private boolean processing = false;
    
    private int mode = 0;
    private int lineCam = -1;
    private int blobCam = -1;
    private int pointCam = -1;

    /**
     * A vision processing line detection processor for use in autonomous alignment among other things
     * @author Nathaniel
     */
    public VisionHandler(int mode) {
        requires(Robot.vision);
        this.mode = mode;
    }

    protected void initialize() {
        pointCam = 0;
    }

    protected void execute() {
        pointChooser(Robot.vision.getPoints(pointCam));
    }

    private void lineChooser(double[] incomingData) {
        int total = incomingData.length / 7;
        for (int i = 0; i < incomingData.length; i += 7) {
            double[] arr = {incomingData[i], incomingData[i] + 1, incomingData[i] + 2, incomingData[i] + 3, incomingData[i] + 4, incomingData[i] + 5, incomingData[i] + 6};
            lines[--total] = new Line(lineCam, arr);
        }
    }

    private void blobChooser(double[] incomingData) {
        int total = incomingData.length / 6;
        for (int i = 0; i < incomingData.length; i += 6) {
            double[] arr = {incomingData[i], incomingData[i] + 1, incomingData[i] + 2, incomingData[i] + 3, incomingData[i] + 4, incomingData[i] + 5};
            blobs[--total] = new Blob(blobCam, arr);
        }
    }

    private void pointChooser(double[] incomingData) {
        int total = incomingData.length / 6;
        for (int i = 0; i < incomingData.length; i += 2) {
            double[] arr = {incomingData[i], incomingData[i] + 1};
            points[--total] = new Point(pointCam, arr);
        }
        vision.points = getPoints();
    }

    public Line[] getLines() {
        return lines;
    }

    public Blob[] getBlobs() {
        return blobs;
    }

    public Point[] getPoints() {
        return points;
    }

    public boolean toggleVision() {
        processing = !processing;
        return processing;
    }

    protected boolean isFinished() {
        return false;
    }
}