# BoilerTracker
Vision Tracking for the 2017 FRC Game
[![Build Status](https://travis-ci.org/TheGuyWhoCodes/BoilerTracker.svg?branch=master)](https://travis-ci.org/TheGuyWhoCodes/BoilerTracker)
From the makers of LiftTracker, there is now BoilerTracker
[![Build Status](https://travis-ci.org/TheGuyWhoCodes/BoilerTracker.svg?branch=master)](https://travis-ci.org/TheGuyWhoCodes/BoilerTracker)
Here are the steps to get this working:
 - Clone the repository, using `git clone https://github.com/TheGuyWhoCodes/BoilerTracker.git`
 - Make sure you have the opencv library downloaded, and Network Tables 3.0 (included)
 - Import the project into Eclipse, and go to your build path and add the included NetworkTables jar, and opencv-XXX.jar that you downloaded
 - Change the variables inside the code to your situation, especially the distance constant, which you can find on my ChiefDelphi post [here](https://www.chiefdelphi.com/forums/showthread.php?p=1667553))
 - Open the .grip file included inside of the project on GRIP. Tune your HSV values to your liking using your webcam and go to Tools->Generate code
 - Using the Generate Code feature will export a *.java file. Open that file and copy all the code, and replace the BoilerTracker.java code inside the project
 - After that, export it as a runnable jar, (File->Export->Java->Runnable JAR file).
 - Run the jar file using `java -jar blahblahblah.jar`
 - Vision Track!

If you have any issues, open up an issue and I'll be happy to look at it.
