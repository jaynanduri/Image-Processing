package model;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

/**
 * This interface represents an Image model and contains method signatures for some
 * basic operations.
 */
public interface Image {
  /**
   * A public getter method for pixels of the image.
   *
   * @return pixel map of the image
   */
  public List<List<ImageImpl.Pixel>> getPixels();

  /**
   * Brightens the image by a certain quantity.
   *
   * @param quantity increase the pixel value by this quantity.
   * @return brightened image object
   */
  public Image brighten(int quantity);

  /**
   * Flips the image horizontally.
   *
   * @return image object
   */
  public Image horizontalFlip();

  /**
   * Flips the image vertically.
   *
   * @return image object
   */
  public Image verticalFlip();

  /**
   * Convert RGB image into a grey scale component.
   *
   * @param component any valid grey-scale component
   * @return image object
   */
  public Image greyScale(String component);

  /**
   * Split the image into red, green and blue channels.
   *
   * @return a 3 image objects
   */
  public Map<String, Image> rgbSplit();

  /**
   * Combines red, green and blue maps into a single RGB image.
   *
   * @param greenComp green component grey img
   * @param blueComp  blue component grey img
   * @return combined RGB img
   * @throws NullPointerException If the passed images are null
   */
  public Image rgbCombine(Image greenComp, Image blueComp) throws NullPointerException;

  /**
   * Encode the content of the image into a string.
   *
   * @return string builder object containing image data
   */
  StringBuilder encodeImage();

  BufferedImage getBufferedImage();
}
