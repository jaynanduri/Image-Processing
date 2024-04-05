package controller;

import java.util.List;
import java.util.Scanner;

import model.ImageCollection;
import view.IView;

/**
 * This class represents a GUI controller to manipulate the contents displayed in View. A GUI
 * controller has a View and an existing implementation of a controller as member variables.
 */
public class GuiController implements Features {
  private IView view;
  private final ApplicationController controller;
  private final ImageCollection imageCollection;

  /**
   * A public constructor to initialise the member variables of the class.
   *
   * @param control existing command line implementation of a controller
   * @param v       GUI window
   * @param imgList cache of the image operations
   */
  public GuiController(ApplicationController control, IView v, ImageCollection imgList) {
    controller = control;
    imageCollection = imgList;
    if (v != null) {
      view = v;
      view.addFeatures(this);
    }
  }

  private void imageOperation(String operation) {
    try {
      Scanner sc = new Scanner(operation);
      controller.processCommand(sc.next(), sc, imageCollection);
    } catch (Exception e) {
      view.errorMsg(e);
    }
  }

  @Override
  public void loadImageFile(String file) {
    this.imageOperation("load " + file + " view-image");
    this.setImage();
  }

  @Override
  public void saveImage(String file) {
    this.imageOperation("save " + file + " view-image");
  }

  @Override
  public void brightenImage(String value) {
    this.imageOperation("brighten " + value + " view-image view-image");
    this.setImage();
  }

  @Override
  public void hFlipImage() {
    this.imageOperation("horizontal-flip view-image view-image");
    this.setImage();
  }

  @Override
  public void vFlipImage() {
    this.imageOperation("vertical-flip view-image view-image");
    this.setImage();
  }

  @Override
  public void imageBlur() {
    this.imageOperation("image-blur view-image view-image");
    this.setImage();
  }

  @Override
  public void imageSharpen() {
    this.imageOperation("image-sharpen view-image view-image");
    this.setImage();
  }

  @Override
  public void greyTransform() {
    this.imageOperation("grey-scaled view-image view-image");
    this.setImage();
  }

  @Override
  public void sepiaTransform() {
    this.imageOperation("sepia view-image view-image");
    this.setImage();
  }

  @Override
  public void dither() {
    this.imageOperation("dither view-image view-image");
    this.setImage();
  }

  @Override
  public void greyscale(String component) {
    this.imageOperation("greyscale " + component + " view-image view-image");
    this.setImage();
  }

  @Override
  public void rgbCombine(List<String> filePaths) {
    this.imageOperation("load " + filePaths.get(0) + " view-red");
    this.imageOperation("load " + filePaths.get(1) + " view-green");
    this.imageOperation("load " + filePaths.get(2) + " view-blue");
    this.imageOperation("rgb-combine view-image view-red view-green view-blue");
    this.setImage();
  }

  @Override
  public void rgbSplit(List<String> filePaths) {
    this.imageOperation("rgb-split view-image view-red view-green view-blue");
    this.imageOperation("save " + filePaths.get(0) + " view-red");
    this.imageOperation("save " + filePaths.get(1) + " view-green");
    this.imageOperation("save " + filePaths.get(2) + " view-blue");
  }

  private void setImage() {
    if (view != null) {
      view.resetImgPanel();
      view.setImage();
      view.resetFocus();
    }
  }
}
