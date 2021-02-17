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
public class FaceDetection extends JFrame {
  private static final HaarCascadeDetector detector = new HaarCascadeDetector();
  private static final String PATH = "/home/jakub/Obrazy";
  private final Webcam webcam = Webcam.getDefault();
  private BufferedImage bufferedImage;
  private WebcamPanel webcamPanel;
  private ImagePanel imagePanel;

  public FaceDetection() {
    webcam.setViewSize(WebcamResolution.VGA.getSize());
    webcam.open();
    setTitle("Face Recognizer");

    webcamPanel = new WebcamPanel(webcam);
    webcamPanel.setImageSizeDisplayed(true);
    webcamPanel.setMirrored(true);

    bufferedImage = webcam.getImage();
    imagePanel = new ImagePanel(bufferedImage);
    imagePanel.setPreferredSize(WebcamResolution.VGA.getSize());

    add(imagePanel);
    add(webcamPanel);
    turnOffTheFrame();
    pack();
    setLocationRelativeTo(null);
    setTheFrameToTurnOn(FaceDetection.this);
  }

  public void faceDetection() {
    JFrame discoveredFaces = new JFrame("Discovered Faces");
    List<DetectedFace> faces = detector.detectFaces(ImageUtilities.createFImage(bufferedImage));

    for (DetectedFace face : faces) {
      FImage facePatch = face.getFacePatch();
      imagePanel = new ImagePanel(ImageUtilities.createBufferedImage(facePatch));
      discoveredFaces.add(imagePanel);
      log.info("Face has been recognized and saved in the selected folder");
      savePhotoToFolder(face, facePatch);
    }

    discoveredFaces.setLayout(new FlowLayout(FlowLayout.LEFT));
    discoveredFaces.setSize(500, 500);
    turnOffTheFrame();
    setTheFrameToTurnOn(discoveredFaces);
  }

  private void savePhotoToFolder(DetectedFace face, FImage facePatch) {
    try {
      OutputStream outputStream = new FileOutputStream(new File(PATH + File.separator + face + ".jpg"));
      ImageIO.write(ImageUtilities.createBufferedImage(facePatch), "jpg", outputStream);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void setTheFrameToTurnOn(JFrame frame) {
    frame.setVisible(true);
  }

  private void turnOffTheFrame() {
    setDefaultCloseOperation(3);
  }
}