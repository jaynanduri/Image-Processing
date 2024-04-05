package controller;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

import model.Image;
import model.ImageCollection;
import model.ImageImpl;

/**
 * This class represents the controller of an interactive image editing application.
 * This controller offers a simple text interface in which the user can
 * type instructions to manipulate an image.
 */
public class ImageController implements ApplicationController {
  protected final ImageCollection imageCollection;
  protected final Appendable appendable;
  protected final Readable readable;

  /**
   * Create a controller to work with the image (model),
   * readable (to take inputs) and appendable (to transmit output).
   *
   * @param image      the sheet to work with (the model)
   * @param readable   the Readable object for inputs
   * @param appendable the Appendable objects to transmit any output
   */
  public ImageController(ImageCollection image, Readable readable, Appendable appendable) {
    if ((image == null) || (readable == null) || (appendable == null)) {
      throw new IllegalArgumentException("Sheet, readable or appendable is null");
    }
    this.imageCollection = image;
    this.appendable = appendable;
    this.readable = readable;
  }

  @Override
  public void control() throws IllegalStateException {
    Scanner sc = new Scanner(readable);
    boolean quit = false;


    //print the welcome message
    this.welcomeMessage();

    while (!quit && sc.hasNext()) { //continue until the user quits
      writeMessage("Type instruction: "); //prompt for the instruction name
      String userInstruction = sc.next(); //take an instruction name
      if (userInstruction.equals("quit") || userInstruction.equals("q")) {
        quit = true;
      } else {
        processCommand(userInstruction, sc, imageCollection);
      }
    }

    //after the user has quit, print farewell message
    this.farewellMessage();
  }

  @Override
  public void processCommand(String userInstruction, Scanner sc, ImageCollection imageList) {

    switch (userInstruction) {
      case "load": //load image from file
        try {
          String filePath = sc.next();
          String objName = sc.next();
          imageList.storeImage(objName,
                  new ImageImpl.ImageBuilder().loadFile(this.loadImage(filePath)));
        } catch (IllegalArgumentException | IOException e) {
          writeMessage("Error: " + e.getMessage() + System.lineSeparator());
        }
        break;
      case "brighten": //brighten image by a value
        try {
          int val = sc.nextInt();
          String srcObj = sc.next();
          String destObj = sc.next();
          Image img = imageList.returnImage(srcObj);
          StringBuilder cloneImg = img.encodeImage();
          Image img2 = new ImageImpl.ImageBuilder().loadFile(cloneImg);
          imageList.storeImage(destObj, img2.brighten(val));
        } catch (IllegalArgumentException e) {
          writeMessage("Error: " + e.getMessage() + System.lineSeparator());
        }
        break;
      case "horizontal-flip":
        try {
          String srcObj = sc.next();
          String destObj = sc.next();
          Image img = imageList.returnImage(srcObj);
          StringBuilder cloneImg = img.encodeImage();
          Image img2 = new ImageImpl.ImageBuilder().loadFile(cloneImg);
          imageList.storeImage(destObj, img2.horizontalFlip());
        } catch (IllegalArgumentException e) {
          writeMessage("Error: " + e.getMessage() + System.lineSeparator());
        }
        break;
      case "vertical-flip":
        try {
          String srcObj = sc.next();
          String destObj = sc.next();
          Image img = imageList.returnImage(srcObj);
          StringBuilder cloneImg = img.encodeImage();
          Image img2 = new ImageImpl.ImageBuilder().loadFile(cloneImg);
          imageList.storeImage(destObj, img2.verticalFlip());
        } catch (IllegalArgumentException e) {
          writeMessage("Error: " + e.getMessage() + System.lineSeparator());
        }
        break;
      case "greyscale":
        try {
          String comp = sc.next();
          String srcObj = sc.next();
          String destObj = sc.next();
          Image img = imageList.returnImage(srcObj);
          imageList.storeImage(destObj, img.greyScale(comp));
        } catch (IllegalArgumentException e) {
          writeMessage("Error: " + e.getMessage() + System.lineSeparator());
        }
        break;
      case "save":
        try {
          String filePath = sc.next();
          String objName = sc.next();
          Image img = imageList.returnImage(objName);
          this.saveFile(img, filePath);
        } catch (IllegalArgumentException | IOException e) {
          writeMessage("Error: " + e.getMessage() + System.lineSeparator());
        }
        break;
      case "rgb-split":
        try {
          String srcObj = sc.next();
          String redObj = sc.next();
          String greenObj = sc.next();
          String blueObj = sc.next();
          Image img = imageList.returnImage(srcObj);
          Map<String, Image> rgbImages = img.rgbSplit();
          imageList.storeImage(redObj, rgbImages.get("red"));
          imageList.storeImage(greenObj, rgbImages.get("green"));
          imageList.storeImage(blueObj, rgbImages.get("blue"));
        } catch (IllegalArgumentException e) {
          writeMessage("Error: " + e.getMessage() + System.lineSeparator());
        }
        break;
      case "rgb-combine":
        try {
          String destObj = sc.next();
          String redObj = sc.next();
          String greenObj = sc.next();
          String blueObj = sc.next();
          Image redImg = imageList.returnImage(redObj);
          Image greenImg = imageList.returnImage(greenObj);
          Image blueImg = imageList.returnImage(blueObj);
          imageList.storeImage(destObj, redImg.rgbCombine(greenImg, blueImg));
        } catch (IllegalArgumentException e) {
          writeMessage("Error: " + e.getMessage() + System.lineSeparator());
        }
        break;
      case "run": {
        try {
          String filePath = sc.next();
          this.executeFile(filePath);
        } catch (Exception e) {
          writeMessage("Error: " + e.getMessage() + System.lineSeparator());
        }
        break;
      }
      case "menu": //print the menu of supported instructions
        welcomeMessage();
        break;
      default: //error due to unrecognized instruction
        writeMessage("Undefined instruction: " + userInstruction + System.lineSeparator());
    }
  }

  protected void executeFile(String file) throws IOException {
    Scanner sc = new Scanner(new FileInputStream(file));
    // read the file line by line, and populate a string. This will throw away any comment lines
    try {
      while (sc.hasNextLine()) {
        String s = sc.nextLine();
        if (s.length() > 0) {
          if (s.charAt(0) != '#') {
            Scanner scanner = new Scanner(s);
            String command = scanner.next();
            this.processCommand(command, scanner, imageCollection);
          }
        }
      }
    } catch (Exception e) {
      writeMessage("Error: " + e.getMessage() + System.lineSeparator());
    }
  }

  protected void writeMessage(String message) throws IllegalStateException {
    try {
      appendable.append(message);

    } catch (IOException e) {
      throw new IllegalStateException(e.getMessage());
    }
  }

  protected void printMenu() throws IllegalStateException {
    writeMessage("Supported user instructions are: " + System.lineSeparator());
    writeMessage("load file-path object-name(call the image with this name)"
                 + System.lineSeparator());
    writeMessage("brighten value src-object-name dest-object-name"
                 + System.lineSeparator());
    writeMessage("vertical-flip src-object-name dest-object-name"
                 + System.lineSeparator());
    writeMessage("horizontal-flip src-object-name dest-object-name"
                 + System.lineSeparator());
    writeMessage("greyscale value/red/green/blue/intensity/luma-component " +
                 "src-object-name dest-object-name" + System.lineSeparator());
    writeMessage("save file-path object-name"
                 + System.lineSeparator());
    writeMessage("rgb-split src-object-name object-name-red object-name-green object-name-blue"
                 + System.lineSeparator());
    writeMessage("rgb-combine dest-object-name src-name-red src-name-green src-name-blue"
                 + System.lineSeparator());
    writeMessage("run file-path"
                 + System.lineSeparator());
    writeMessage("menu (Print supported instruction list)" + System.lineSeparator());
    writeMessage("q or quit (quit the program) " + System.lineSeparator());
  }

  protected void welcomeMessage() throws IllegalStateException {
    writeMessage("Welcome to the Image Manipulation program!" + System.lineSeparator());
    printMenu();
  }

  protected void farewellMessage() throws IllegalStateException {
    writeMessage("Thank you for using this program!");
  }

  protected StringBuilder loadImage(String filename) throws IOException {
    Scanner sc = null;
    StringBuilder k = null;
    try {
      sc = new Scanner(new FileInputStream(filename));
      StringBuilder builder = new StringBuilder();
      //read the file line by line, and populate a string. This will throw away any comment lines
      while (Objects.requireNonNull(sc).hasNextLine()) {
        String s = sc.nextLine();
        if (s.charAt(0) != '#') {
          builder.append(s).append(System.lineSeparator());
        }
      }
      //now set up the scanner to read from the string we just built
      sc = new Scanner(builder.toString());
      k = new StringBuilder();
      String token;
      token = sc.next();
      if (!token.equals("P3")) {
        throw new IllegalArgumentException("Invalid PPM file: plain RAW file should begin with P3");
      }
      int width = sc.nextInt();
      k.append(width).append(System.lineSeparator());
      int height = sc.nextInt();
      k.append(height).append(System.lineSeparator());
      int maxVal = sc.nextInt();
      k.append(maxVal).append(System.lineSeparator());
      for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
          int r = sc.nextInt();
          k.append(r).append(System.lineSeparator());
          int g = sc.nextInt();
          k.append(g).append(System.lineSeparator());
          int b = sc.nextInt();
          k.append(b).append(System.lineSeparator());
          if ((r > 255 || b > 255 || g > 255) || (r < 0 || b < 0 || g < 0)) {
            throw new IllegalStateException("Invalid pixel value passed in the file.");
          }
        }
      }
    } catch (IOException e) {
      writeMessage("Error: " + e.getMessage() + System.lineSeparator());
    }
    return k;
  }

  protected void saveFile(Image img, String filePath) throws IOException {
    OutputStream os = new FileOutputStream(filePath);
    OutputStreamWriter writer = new OutputStreamWriter(os);

    writer.write("P3\n");
    writer.write(img.encodeImage().toString());
    writer.close();
  }
}
