package org.example;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import lombok.extern.slf4j.Slf4j;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.face.detection.DetectedFace;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Slf4j
public class FaceDetection {
  private static final HaarCascadeDetector detector = new HaarCascadeDetector();
  private static final String PATH = "/home/jakub/Obrazy";
  private final Webcam webcam = Webcam.getDefault();
  private final JFrame faceRecognizeFrame = new JFrame();
  private final JFrame discoveredFacesFrame = new JFrame();
  private final int INTERVAL = 10000;
  private BufferedImage bufferedImage;
  private WebcamPanel webcamPanel;
  private ImagePanel imagePanel;

  public FaceDetection() throws InterruptedException {
    webcam.setViewSize(WebcamResolution.VGA.getSize());
    webcam.open();

    while (true) {
      if (!webcam.isOpen()) {
        log.error("Webcam does not exist");
        break;
      }
      bufferedImage = webcam.getImage();

      if (bufferedImage != null) {
        imagePanel = new ImagePanel(bufferedImage);
        imagePanel.setPreferredSize(WebcamResolution.VGA.getSize());
        faceDetection();
        Thread.sleep(INTERVAL);
      }

      webcamPanel = new WebcamPanel(webcam);
      webcamPanel.setMirrored(true);
      webcamPanel.setImageSizeDisplayed(true);

      faceRecognizeFrame.add(imagePanel);
      faceRecognizeFrame.add(webcamPanel);
      faceRecognizeFrame.setTitle("Face Recognizer");
      faceRecognizeFrame.pack();
      faceRecognizeFrame.setLocationRelativeTo(null);
      setTheFrameToTurnOnAndTurnOff(faceRecognizeFrame);
    }
  }

  public void faceDetection() {
    List<DetectedFace> faces = detector.detectFaces(ImageUtilities.createFImage(bufferedImage));

    for (DetectedFace face : faces) {
      FImage facePatch = face.getFacePatch();
      imagePanel = new ImagePanel(ImageUtilities.createBufferedImage(facePatch));
      discoveredFacesFrame.add(imagePanel);
      log.info("Face has been recognized and saved in the selected folder");
      savePhotoToFolder(face, facePatch);
    }

    discoveredFacesFrame.setTitle("Discovered Faces");
    discoveredFacesFrame.setLayout(new FlowLayout(FlowLayout.LEFT));
    discoveredFacesFrame.setSize(500, 500);
    setTheFrameToTurnOnAndTurnOff(discoveredFacesFrame);
  }

  private void savePhotoToFolder(DetectedFace face, FImage facePatch) {
    try {
      OutputStream outputStream = new FileOutputStream(String.valueOf(new File(PATH + File.separator + face + ".jpg")));
      ImageIO.write(ImageUtilities.createBufferedImage(facePatch), "jpg", outputStream);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void setTheFrameToTurnOnAndTurnOff(JFrame frame) {
    frame.setVisible(true);
    frame.setDefaultCloseOperation(3);
  }
}