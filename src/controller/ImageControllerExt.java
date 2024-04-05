package controller;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;

import model.ColorTransform;
import model.Command;
import model.Image;
import model.ImageBlur;
import model.ImageCollection;
import model.ImageDither;
import model.ImageImplExt;
import model.ImageSharpen;

/**
 * This class represents an extension for the image controller application to support additional
 * commands to manipulate an image.
 */
public class ImageControllerExt extends ImageController {
  private final Map<String, Command> functionMap = new HashMap<>();

  /**
   * Create a controller to work with the image (model),
   * readable (to take inputs) and appendable (to transmit output).
   *
   * @param image      the sheet to work with (the model)
   * @param readable   the Readable object for inputs
   * @param appendable the Appendable objects to transmit any output
   */
  public ImageControllerExt(ImageCollection image, Readable readable, Appendable appendable) {
    super(image, readable, appendable);
    functionMap.put("image-blur", new ImageBlur());
    functionMap.put("image-sharpen", new ImageSharpen());
    List<List<Double>> greyMat = new ArrayList<>();
    greyMat.add(new ArrayList<>(Arrays.asList(0.2126, 0.7152, 0.0722)));
    greyMat.add(new ArrayList<>(Arrays.asList(0.2126, 0.7152, 0.0722)));
    greyMat.add(new ArrayList<>(Arrays.asList(0.2126, 0.7152, 0.0722)));
    functionMap.put("grey-scaled", new ColorTransform(greyMat));
    List<List<Double>> sepiaMat = new ArrayList<>();
    sepiaMat.add(new ArrayList<>(Arrays.asList(0.393, 0.769, 0.189)));
    sepiaMat.add(new ArrayList<>(Arrays.asList(0.349, 0.686, 0.168)));
    sepiaMat.add(new ArrayList<>(Arrays.asList(0.272, 0.534, 0.131)));
    functionMap.put("sepia", new ColorTransform(sepiaMat));
    functionMap.put("dither", new ImageDither());

  }

  @Override
  public void processCommand(String userInstruction, Scanner sc, ImageCollection imageList) {
    if (functionMap.get(userInstruction) != null) {
      try {
        String srcObjName = sc.next();
        String destObjName = sc.next();
        Image img = imageList.returnImage(srcObjName);
        Command command = functionMap.get(userInstruction);
        if (img instanceof ImageImplExt) {
          imageList.storeImage(destObjName, ((ImageImplExt) img).runCommand(command));
        }
      } catch (IllegalArgumentException e) {
        writeMessage("Error: " + e.getMessage() + System.lineSeparator());
      }
    } else {
      super.processCommand(userInstruction, sc, imageList);
    }
  }

  protected StringBuilder loadImage(String filename) {
    BufferedImage img;
    StringBuilder builder = null;
    try {
      if (filename.contains("ppm")) {
        return super.loadImage(filename);
      } else {
        img = ImageIO.read(new File(filename));
        builder = new StringBuilder();
        builder.append(img.getWidth()).append(System.lineSeparator());
        builder.append(img.getHeight()).append(System.lineSeparator());
        builder.append(255).append(System.lineSeparator());
        for (int y = 0; y < img.getHeight(); y++) {
          for (int x = 0; x < img.getWidth(); x++) {
            //Retrieving contents of a pixel
            int pixel = img.getRGB(x, y);
            //Creating a Color object from pixel value
            Color color = new Color(pixel, true);
            //Retrieving the R G B values
            int red = color.getRed();
            int green = color.getGreen();
            int blue = color.getBlue();
            builder.append(red).append(System.lineSeparator());
            builder.append(green).append(System.lineSeparator());
            builder.append(blue).append(System.lineSeparator());
          }
        }
      }
    } catch (IOException e) {
      writeMessage("Error: " + e.getMessage() + System.lineSeparator());
    }
    return builder;
  }

  protected void saveFile(Image img, String filePath) {
    try {
      if (filePath.contains("ppm")) {
        super.saveFile(img, filePath);
      } else {
        BufferedImage bufferedImage = img.getBufferedImage();
        String[] fileName = filePath.split("\\.");
        String fileExt = fileName[fileName.length - 1];
        ImageIO.write(bufferedImage, fileExt, new File(filePath));
      }
    } catch (IOException e) {
      writeMessage("Error: " + e.getMessage() + System.lineSeparator());
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
    writeMessage("image-blur src-obj-name dest-obj-name"
                 + System.lineSeparator());
    writeMessage("image-sharpen src-obj-name dest-obj-name"
                 + System.lineSeparator());
    writeMessage("grey-scaled src-obj-name dest-obj-name"
                 + System.lineSeparator());
    writeMessage("sepia src-obj-name dest-obj-name"
                 + System.lineSeparator());
    writeMessage("dither src-obj-name dest-obj-name"
                 + System.lineSeparator());
    writeMessage("run file-path"
                 + System.lineSeparator());
    writeMessage("menu (Print supported instruction list)" + System.lineSeparator());
    writeMessage("q or quit (quit the program) " + System.lineSeparator());
  }
}

