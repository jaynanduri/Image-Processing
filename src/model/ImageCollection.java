package model;

/**
 * This interface represents a collection of images and keeps track of all images and their names.
 */
public interface ImageCollection {
  /**
   * get image from collection with the given name.
   *
   * @param objName name of the image
   * @return image object
   */
  public Image returnImage(String objName);

  /**
   * store an image with a given name.
   *
   * @param objName name for the image
   * @param image   image object
   */
  public void storeImage(String objName, Image image);
}
