package model;

import java.awt.image.BufferedImage;

/**
 * This class represents a read-only image. A read-only image has a collection of images and
 * returns a buffered image.
 */
public class ReadOnlyModelImpl implements ReadOnlyModel {
  private final ImageCollection imgList;

  /**
   * A public constructor to initialise the member variables.
   *
   * @param list collection of images (cache)
   */
  public ReadOnlyModelImpl(ImageCollection list) {
    this.imgList = list;
  }

  @Override
  public BufferedImage getBufferedImg(String objName) {
    return imgList.returnImage(objName).getBufferedImage();
  }
}
