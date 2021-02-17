package org.example;

import javax.swing.*;
import java.awt.*;

class ImagePanel extends JPanel {
  private final Image img;

  public ImagePanel(Image img) {
    this.img = img;

    Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));

    setPreferredSize(size);
    setMinimumSize(size);
    setMaximumSize(size);
    setSize(size);
    setLayout(null);
  }

  @Override
  public void paintComponent(Graphics g) {
    g.drawImage(this.img, 0, 0, null);
  }
}