package controller;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import model.ImageCollection;
import model.ImageList;
import model.ReadOnlyModel;
import model.ReadOnlyModelImpl;
import view.IView;
import view.JFrameView;


/**
 * This class represents the main driver program to run the application.
 */
public class ControllerMain {
  /**
   * Main method to run the application.
   *
   * @param args command line arguments from terminal
   */
  public static void main(String[] args) {
    ImageCollection model = new ImageList();
    Readable rd = new InputStreamReader(System.in);
    Appendable ap = System.out;
    if (args.length == 2 && args[0].equals("-file")) {
      String command = "run " + args[1];
      rd = new InputStreamReader(new ByteArrayInputStream(
              (command + "\nquit").getBytes()));
      ap = System.out;
      ApplicationController controller = new ImageControllerExt(model, rd, ap);
      controller.control();
    } else if (args.length == 1 && args[0].equals("-text")) {
      ApplicationController controller = new ImageControllerExt(model, rd, ap);
      controller.control();
    } else {
      ReadOnlyModel roi = new ReadOnlyModelImpl(model);
      IView view = new JFrameView("Image Editing Application", roi);
      Features controller = new GuiController(new ImageControllerExt(model, rd, ap), view, model);
    }
  }
}