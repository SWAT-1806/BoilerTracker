# BoilerTracker
Vision Tracking for the 2017 FRC Game

This is super experimental, we are working on grabbing an angle, but for right now it will tell you the amount of *PIXELS* you are off from the target

Here are the steps to get this working:
 - Clone the repository, using `git clone https://github.com/TheGuyWhoCodes/BoilerTracker.git`
 - Make sure you have the opencv library downloaded, and Network Tables 3.0 (included)
 - Import the project into Eclipse, and go to your build path and add the included NetworkTables jar, and opencv-XXX.jar that you downloaded
 - Change the variables inside the code to your situation
 - Open the .grip file included inside of the Project. Tune your HSV values to your liking using your webcam and go to Tools->Generate code
 - Using the Generate Code feature will export a *.java file. Open that file and copy all the code, and replace the BoilerTracker.java code inside the project
 - After that, export it as a runnable jar, (File->Export->Java->Runnable JAR file).
 - Run the jar file using `java -jar blahblahblah.jar`
 - Vision Track!

If you have any issues, open up an issue and I'll be happy to look at it.
