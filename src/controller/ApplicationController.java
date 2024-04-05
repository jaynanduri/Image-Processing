package controller;


import java.util.Scanner;

import model.ImageCollection;

/**
 * This interface represents an Image controller that provides a persistent execution mode or a
 * single command execution.
 */
public interface ApplicationController {
  /**
   * The main method that relinquishes control of the application to the controller.
   *
   * @throws IllegalStateException if the controller is unable to transmit output
   */
  void control() throws IllegalStateException;

  /**
   * Process the entered user instruction to manipulate the image.
   *
   * @param userInstruction an instruction
   * @param sc              scanner for parsing the entered instruction
   * @param imageList       List of images saved during the runtime of application
   */
  void processCommand(String userInstruction, Scanner sc, ImageCollection imageList);
}
