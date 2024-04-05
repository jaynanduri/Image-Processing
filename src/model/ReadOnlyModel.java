package model;

import java.awt.image.BufferedImage;

/**
 * This interface represents a Read-only model for displaying the result in view.
 */
public interface ReadOnlyModel {
  /**
   * Return a buffered image which doesn't contain the methods of the model.
   *
   * @param objName a string to access the image from hashmap
   * @return a buffered image object
   */
  public BufferedImage getBufferedImg(String objName);
}
