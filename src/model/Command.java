package model;

/**
 * This interface represents a command that can be applied on an Image object.
 */
public interface Command {
  /**
   * Apply the functionality encapsulated in the command object on this image and return the
   * transformed image.
   *
   * @param image    an Image object
   * @param width    width of image
   * @param height   height of image
   * @param maxPixel maximum allowed value of a pixel
   * @return transformed Image object
   */
  Image executeMethod(Image image, int width, int height, int maxPixel);
}
