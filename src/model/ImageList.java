package model;

import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a collection of images. This collection represents a hashmap of images and
 * their corresponding names.
 */
public class ImageList implements ImageCollection {

  private final Map<String, Image> ledger;

  /**
   * Create an image list to work with the generated images. This hashmap works as a cache for the
   * application.
   */
  public ImageList() {
    this.ledger = new HashMap<>();
  }

  @Override
  public Image returnImage(String objName) {
    return this.ledger.get(objName);
  }

  @Override
  public void storeImage(String objName, Image image) {
    this.ledger.put(objName, image);
  }
}
