package model;

import java.util.List;

/**
 * This class represents an extension of the existing model that supports encapsulated command
 * objects to manipulate itself.
 */
public class ImageImplExt extends ImageImpl implements ImageExt {
  /**
   * Initialise the content of an Image.
   *
   * @param width    width of image
   * @param height   height of image
   * @param maxPixel maximum allowed value for a pixel
   * @param pixels   matrix of pixel objects
   */
  protected ImageImplExt(int width, int height, int maxPixel, List<List<Pixel>> pixels) {
    super(width, height, maxPixel, pixels);
  }

  @Override
  public Image runCommand(Command command) {
    return command.executeMethod(this, width, height, maxPixel);
  }
}
