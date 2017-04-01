

import java.util.ArrayList;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class Processing {

	/**
	 * 
	 * static method to load opencv and networkTables
	 */
	static{ 
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

	}
//	Process for GRIP	
	//static BoilerTracker tracker;
	public static VideoCapture videoCapture;
//	Constants for known variables

	public static final double OFFSET_TO_FRONT = 0;
	public static final double CAMERA_WIDTH = 640;
	public static final double WIDTH_CLOSENESS = .20;
	public static final double DISTANCE_CONSTANT = .34;
	public static final double ANGLE_OFFSET = -2.2333;
	public static final double LENGTH_OF_TARGET = 15;
	public static boolean shouldRun = true;
	
	
	static NetworkTable table;
	public static BoilerTracker ayy = new BoilerTracker();
	public static Mat matOriginal = new Mat();
	static double lengthBetweenContours;
	static double distanceFromTarget;
	static double lengthError;
	static double[] centerX;
	static double[] centerY;
	static double[] sideX;
	static Rect r1;
	static Rect r2;
	/**
	 * 
	 * @param args command line arguments0
	 * just the main loop for the program and the entry points
	 */
	public static void main(String[] args) {
		NetworkTable.setClientMode();
		NetworkTable.setTeam(1806);
		NetworkTable.setIPAddress("roborio-1806-frc.local");
		NetworkTable.initialize();
		table = NetworkTable.getTable("BoilerTracker");
		r1 = new Rect();
		r2 = new Rect();
		while(shouldRun){
			try {
//				opens up the camera stream and tries to load it
				videoCapture = new VideoCapture();
				centerX = new double[] {0};
				centerY = new double[] {0};
				//tracker = new LiftTracker();
				videoCapture.open("http://roborio-1806-frc.local:1181/?action=stream");
				// change that to your team number boi("http://roborio-XXXX-frc.local:1181/?action=stream");
				while(!videoCapture.isOpened()){
					System.out.println("Didn't open Camera, restart jar");
				}
//				time to actually process the acquired images
				while(videoCapture.isOpened()){
					processImage();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
//		make sure the java process quits when the loop finishes
		videoCapture.release();
		System.exit(0);
	}
	public static void processImage(){
		System.out.println("Processing Started");
		
//		only run for the specified time
		while(true){
			//System.out.println("Hey I'm Processing Something!");
			videoCapture.read(matOriginal);
			ayy.process(matOriginal);
			// putting everything on network tables, change as you will
			table.putNumber("Distance From Center in Pixels", returnCenterX()); //returns distance from center
			table.putNumberArray("centerX", centerX); // returns centerX values of contours
			table.putNumberArray("centerY", centerY); // returns centerY of contours
			table.putNumberArray("sides", returnSides()); // returns the x values of each side 
			table.putNumber("distanceFromTarget", returnDistance()); // returns distance from target 
			table.putNumber("angleFromGoal", getAngle()); // returns angle to goal
		}
		
	}
	//LOTS OF STUFF
	public static double returnCenterX(){
			// This is the center value returned by GRIP thank WPI
		if(!ayy.filterContoursOutput.isEmpty()){
			if(ayy.filterContoursOutput.size() == 2){
				//System.out.println("I see two of the targets");
				r1 = Imgproc.boundingRect(ayy.filterContoursOutput.get(1));
				r2 = Imgproc.boundingRect(ayy.filterContoursOutput.get(0)); 
				centerX = new double[]{r1.x + (r1.width / 2), r2.x + (r2.width / 2)};
				centerY = new double[]{r1.y + (r1.height / 2), r2.y + (r2.height / 2)};
				// subtracts one another to get length in pixels
				//lengthBetweenContours = Math.abs((centerX[0] + centerX[1]) / 2) - 320;
				lengthBetweenContours = Math.abs((centerX[0] + centerX[1]) / 2) - CAMERA_WIDTH /2;
			} else if(ayy.filterContoursOutput.size() == 1){
				//System.out.println("I see one of the targets");
				r1 = Imgproc.boundingRect(ayy.filterContoursOutput.get(0));
				centerX = new double[]{r1.x + (r1.width / 2)};
				centerY = new double[]{r1.y + (r1.height / 2)};
				lengthBetweenContours = Math.abs((centerX[0]) - CAMERA_WIDTH /2);
			} else {
				Rect[] rectangleArray = new Rect[ayy.filterContoursOutput.size()];
				//System.out.println("I see: " + rectangleArray.length);
				for(int i = 0 ; i < ayy.filterContoursOutput.size(); i++){
					rectangleArray[i] = Imgproc.boundingRect(ayy.filterContoursOutput().get(i)); 
					//System.out.println("Object" + i + " X " + rectangleArray[i].x + " Y "+rectangleArray[i].y + "Width = " + rectangleArray[i].width);
				}
				ArrayList<ArrayList<Integer>> Pairs = new ArrayList<ArrayList<Integer>>();
				for(int i = 0; i < rectangleArray.length; i++){
					for(int j =i+1; j < rectangleArray.length; j++){
						if(rectangleArray[i].width * (1-WIDTH_CLOSENESS) <= rectangleArray[j].width 
								&& rectangleArray[i].width * (1+WIDTH_CLOSENESS) >= rectangleArray[j].width){
								ArrayList<Integer> tempPairs = new ArrayList<Integer>();
								tempPairs.add(i);
								tempPairs.add(j);
								Pairs.add(tempPairs);
								//System.out.println("\t Found Pair" + i + "and " + j);
						}
						
					}
				}
				if(Pairs.size() != 0){
					double bestDistance = 1000000;
					int currentBest = -1;
					for(int i = 0; i < Pairs.size(); i++){
						ArrayList<Integer> tempPairs = Pairs.get(i);
						Rect r1 = rectangleArray[tempPairs.get(0)];
						Rect r2 = rectangleArray[tempPairs.get(1)];
						double[] r1Points = {r1.x + (r1.width /2) , r1.y + (r1.height / 2)};
						double[] r2Points = {r2.x + (r2.width /2) , r2.y + (r2.height / 2)};
						double distanceBetweenPoints = Math.sqrt(Math.pow((r2Points[0] - r1Points[0]), 2) + (Math.pow((r2Points[1] - r1Points[1]), 2)));
						//System.out.println("\t r1 X : " + r1.x);
						//System.out.println("\t r2 X : " + r2.x);

						if(distanceBetweenPoints < bestDistance){
							//System.out.println("\t Best Distance = " + distanceBetweenPoints );
							currentBest = i;
							bestDistance = distanceBetweenPoints;
						} 
					}
					ArrayList<Integer> tempPairs = Pairs.get(currentBest);
					r1 = rectangleArray[tempPairs.get(0)];
					r2 = rectangleArray[tempPairs.get(1)];	
					centerX = new double[]{r1.x + (r1.width / 2), r2.x + (r2.width / 2)};
					centerY = new double[]{r1.y + (r1.height / 2), r2.y + (r2.height / 2)};
					//System.out.println("\t Best Pairs Found: " + tempPairs.get(0) +" "+ tempPairs.get(1));
					// subtracts one another to get length in pixels
					lengthBetweenContours = Math.abs((centerX[0] + centerX[1]) / 2) - CAMERA_WIDTH /2 ;
					
				}
			}
		}
		Imgcodecs.imwrite("output.png", matOriginal);
		return lengthBetweenContours;
	}
	public static double returnDistance(){
		double distance = 0;
		if(!ayy.filterContoursOutput.isEmpty() && ayy.filterContoursOutput.size() >= 2){
			if(centerY.length >= 2){
				if(centerY[0] > centerY[1]){
					// checks for biggest contour
					distance = centerY[1] * DISTANCE_CONSTANT;
				} else {
					distance = centerY[0] * DISTANCE_CONSTANT;
				}
			}
		} else {
			distance = centerY[0] * DISTANCE_CONSTANT;
		}
		return distance;
	}
	public static double[] returnSides(){
		// this method returns the x values of the signs per the contours
		double leftSide;
		double rightSide;
		leftSide = r1.x;
		rightSide = r1.x + r1.width;
		sideX = new double[]{leftSide, rightSide};
		return sideX;
	}
	public static double getAngle(){
		// 8.5in is for the distance from center to center from goal, then divide by lengthBetweenCenters in pixels to get proportion
		double angleToGoal = 0;
		//Looking for the 2 blocks to actually start trig
		if(!ayy.filterContoursOutput.isEmpty() && ayy.filterContoursOutput.size() >= 1){
			// Think of this as a multiplication factor to find the in. we are away from the target
			double constants = LENGTH_OF_TARGET / (sideX[0] - sideX[1]);
			double lengthBetweenInches = lengthBetweenContours * constants;
			// Math brought to you by Ariaaagno and Jones
			angleToGoal = Math.atan(lengthBetweenInches / returnDistance());
			angleToGoal = Math.toDegrees(angleToGoal);
			angleToGoal = angleToGoal + ANGLE_OFFSET;
			// adds in offset
			System.out.println(angleToGoal);
		}
		return angleToGoal;
	}
}