package view;

import controller.Features;

/**
 * This interface represents a GUI window to perform operation on an image.
 */
public interface IView {
  /**
   * Use this to provide view with all the callbacks from controller.
   */
  void addFeatures(Features features);

  /**
   * Display image in image panel. This function acts as a refresh to display the resultant
   * image after an operation is performed. Also, display the histogram of the image.
   */
  void setImage();

  /**
   * Clear the contents displayed in image panel.
   */
  void resetImgPanel();

  /**
   * Display a helpful error msg to the user.
   *
   * @param e error
   */
  void errorMsg(Exception e);

  /**
   * Reset the focus on the appropriate part of the view that has the keyboard listener attached
   * to it, so that keyboard events will still flow through.
   */
  void resetFocus();
}
