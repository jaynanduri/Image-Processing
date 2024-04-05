package controller;

import java.util.List;

/**
 * This interface represents action listeners for to transform the contents in GUI.
 */
public interface Features {
  /**
   * Function to display image in view.
   *
   * @param file path of the image
   */
  void loadImageFile(String file);

  /**
   * Save image displayed in view to the specified path in disk.
   *
   * @param file path of the file
   */
  void saveImage(String file);

  /**
   * Brighten displayed image by a value.
   *
   * @param value integer vallue
   */
  void brightenImage(String value);

  /**
   * Perform horizontal-flip on displayed image in view.
   */
  void hFlipImage();

  /**
   * Perform vertical-flip on displayed image in view.
   */
  void vFlipImage();

  /**
   * Perform image blur operation on the displayed image in view.
   */
  void imageBlur();

  /**
   * Perform image sharpen operation on displayed image in view.
   */
  void imageSharpen();

  /**
   * Perform grey color transformation on image displayed in view.
   */
  void greyTransform();

  /**
   * Perform sepia color transformation on image displayed in view.
   */
  void sepiaTransform();

  /**
   * Perform image dither operation on image displayed in view.
   */
  void dither();

  /**
   * Visualise individual greyscale components of the displayed image in view.
   *
   * @param component image component
   */
  void greyscale(String component);

  /**
   * Combine red, green and blue greyscale images to generate a combined rgb image. This image is
   * displayed in the view.
   *
   * @param filePaths paths for the greyscale images
   */
  void rgbCombine(List<String> filePaths);

  /**
   * Split the RGB image into red, green and blue grey-scale images and save them in the specified
   * paths in disk.
   *
   * @param filePaths paths where the images are saved
   */
  void rgbSplit(List<String> filePaths);
}
