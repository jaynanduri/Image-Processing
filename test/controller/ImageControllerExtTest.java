package controller;

import org.junit.Test;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import javax.imageio.ImageIO;

import model.Command;
import model.Image;
import model.ImageBlur;
import model.ImageCollection;
import model.ImageDither;
import model.ImageExt;
import model.ImageImpl;
import model.ImageList;
import model.ImageSharpen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * This class represents test class for the Image controller. It tests the functionality of an
 * image controller application.
 */
public class ImageControllerExtTest {
  private StringBuilder loadPPM(String fileName) throws FileNotFoundException {
    Scanner sc = new Scanner(new FileInputStream(fileName));
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
    StringBuilder k = new StringBuilder();
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
      }
    }
    return k;
  }

  private StringBuilder loadImage(String fileName) throws IOException {
    BufferedImage img = ImageIO.read(new File(fileName));
    StringBuilder builder = new StringBuilder();
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
    return builder;
  }

  /**
   * Test case to verify the welcome messages.
   */
  @Test
  public void testWelcomeMsg() {
    Readable rd = new InputStreamReader(new ByteArrayInputStream("\nquit".getBytes()));
    Appendable ap = new StringBuilder();
    ImageCollection model = new ImageList();
    ApplicationController controller = new ImageControllerExt(model, rd, ap);
    controller.control();
    //Welcome msg contents
    List<String> welcomeMsg = List.of(("Welcome to the Image Manipulation program!" +
            "\nSupported user instructions are: \nload file-path " +
            "object-name(call the image with this name)" +
            "\nbrighten value src-object-name dest-object-name" +
            "\nvertical-flip src-object-name dest-object-name" +
            "\nhorizontal-flip src-object-name dest-object-name" +
            "\ngreyscale value/red/green/blue/intensity/luma-component "
            + "src-object-name dest-object-name" +
            "\nsave file-path object-name" +
            "\nrgb-split src-object-name object-name-red " +
            "object-name-green object-name-blue" +
            "\nrgb-combine dest-object-name src-name-red " +
            "src-name-green src-name-blue" +
            "\nimage-blur src-obj-name dest-obj-name" +
            "\nimage-sharpen src-obj-name dest-obj-name" +
            "\ngrey-scaled src-obj-name dest-obj-name" +
            "\nsepia src-obj-name dest-obj-name" +
            "\ndither src-obj-name dest-obj-name" +
            "\nrun file-path" +
            "\nmenu (Print supported " +
            "instruction list)\nq or " +
            "quit (quit the program) ").split("\n"));
    List<String> output = List.of(ap.toString().split("\n"));
    assertEquals(output.subList(0, output.size() - 1), welcomeMsg);
  }

  /**
   * Test case to verify the farewell message.
   */
  @Test
  public void testFareWellMsg() {
    Readable rd = new InputStreamReader(new ByteArrayInputStream("\nquit".getBytes()));
    Appendable ap = new StringBuilder();
    ImageCollection model = new ImageList();
    ApplicationController controller = new ImageControllerExt(model, rd, ap);
    controller.control();
    //farewell msg contents
    List<String> fareWellMsg = List.of(("Type instruction: Thank you for using this program!"));
    List<String> output = List.of(ap.toString().split("\n"));
    assertEquals(output.subList(output.size() - 1, output.size()), fareWellMsg);
  }

  /**
   * Test case to pass a valid PPM file as input.
   *
   * @throws FileNotFoundException if not a valid ppm file.
   */
  @Test
  public void testValidInputToModelPPM() throws FileNotFoundException {
    Image img = new ImageImpl.ImageBuilder().loadFile(this.loadPPM("JD.ppm"));
    Readable rd = new InputStreamReader(new ByteArrayInputStream(("load JD.ppm JD" +
            "\nquit").getBytes()));
    Appendable ap = new StringBuilder();
    ImageCollection model = new ImageList();
    ApplicationController controller = new ImageControllerExt(model, rd, ap);
    controller.control();

    //check if the contents of controller are working correctly.
    assertEquals(img, model.returnImage("JD"));
  }

  /**
   * Test case to pass a valid JPG file as input.
   *
   * @throws IOException if a valid file is not passed.
   */
  @Test
  public void testValidInputToModelJPG() throws IOException {
    Image actualImg = new ImageImpl.ImageBuilder().loadFile(this.loadImage("Dog.jpg"));
    Readable rd = new InputStreamReader(new ByteArrayInputStream(("load Dog.jpg jay" +
            "\nquit").getBytes()));
    Appendable ap = new StringBuilder();
    ImageCollection model = new ImageList();
    ApplicationController controller = new ImageControllerExt(model, rd, ap);
    controller.control();

    //check if the contents of controller are working correctly.
    assertEquals(actualImg, model.returnImage("jay"));
  }

  /**
   * Test case to pass a valid PNG file as input.
   *
   * @throws IOException if a valid file is not passed.
   */
  @Test
  public void testValidInputToModelPNG() throws IOException {
    Image actualImg = new ImageImpl.ImageBuilder().loadFile(
            this.loadImage("JAY.png"));
    Readable rd = new InputStreamReader(new ByteArrayInputStream(("load JAY.png jay" +
            "\nquit").getBytes()));
    Appendable ap = new StringBuilder();
    ImageCollection model = new ImageList();
    ApplicationController controller = new ImageControllerExt(model, rd, ap);
    controller.control();

    //check if the contents of controller are working correctly.
    assertEquals(actualImg, model.returnImage("jay"));
  }

  /**
   * Test case to pass a valid BMP file as input.
   *
   * @throws IOException if a valid file is not passed.
   */
  @Test
  public void testValidInputToModelBMP() throws IOException {
    Image actualImg = new ImageImpl.ImageBuilder().loadFile(
            this.loadImage("TIGER.bmp"));
    Readable rd = new InputStreamReader(new ByteArrayInputStream(("load TIGER.bmp jay" +
            "\nquit").getBytes()));
    Appendable ap = new StringBuilder();
    ImageCollection model = new ImageList();
    ApplicationController controller = new ImageControllerExt(model, rd, ap);
    controller.control();

    //check if the contents of controller are working correctly.
    assertEquals(actualImg, model.returnImage("jay"));
  }

  /**
   * Test case to verify when an invalid ppm file is passed as input.
   *
   * @throws FileNotFoundException if valid file is not passed.
   */
  @Test(expected = FileNotFoundException.class)
  public void testInValidInputToModelPPM() throws FileNotFoundException {
    Image img = new ImageImpl.ImageBuilder().loadFile(this.loadPPM("JJD.ppm"));
    Readable rd = new InputStreamReader(new ByteArrayInputStream(("load JD.ppm JD" +
            "\nquit").getBytes()));
    Appendable ap = new StringBuilder();
    ImageCollection model = new ImageList();
    ApplicationController controller = new ImageControllerExt(model, rd, ap);
    controller.control();

    //check if the contents of controller are working correctly.
    assertEquals(img, model.returnImage("JD"));
  }

  /**
   * Test case to verify when an invalid JPG file is passed as input.
   *
   * @throws FileNotFoundException if valid file is not passed.
   */
  @Test(expected = IOException.class)
  public void testInValidInputToModelJPG() throws IOException {
    Image actualImg = new ImageImpl.ImageBuilder().loadFile(this.loadImage("DG.jpg"));
    Readable rd = new InputStreamReader(new ByteArrayInputStream(("load DOG.jpg dog" +
            "\nquit").getBytes()));
    Appendable ap = new StringBuilder();
    ImageCollection model = new ImageList();
    ApplicationController controller = new ImageControllerExt(model, rd, ap);
    controller.control();

    //check if the contents of controller are working correctly.
    assertEquals(actualImg, model.returnImage("dog"));
  }

  /**
   * Test case to verify when an invalid PNG file is passed as input.
   *
   * @throws FileNotFoundException if valid file is not passed.
   */
  @Test(expected = IOException.class)
  public void testInValidInputToModelPNG() throws IOException {
    Image actualImg = new ImageImpl.ImageBuilder().loadFile(
            this.loadImage("JY.png"));
    Readable rd = new InputStreamReader(new ByteArrayInputStream(("load JAY.png jay" +
            "\nquit").getBytes()));
    Appendable ap = new StringBuilder();
    ImageCollection model = new ImageList();
    ApplicationController controller = new ImageControllerExt(model, rd, ap);
    controller.control();

    //check if the contents of controller are working correctly.
    assertEquals(actualImg, model.returnImage("jay"));
  }

  /**
   * Test case to verify when an invalid BMP file is passed as input.
   *
   * @throws FileNotFoundException if valid file is not passed.
   */
  @Test(expected = IOException.class)
  public void testInValidInputToModelBMP() throws IOException {
    Image actualImg = new ImageImpl.ImageBuilder().loadFile(
            this.loadImage("TIER.bmp"));
    Readable rd = new InputStreamReader(new ByteArrayInputStream(("load TIGER.bmp jay" +
            "\nquit").getBytes()));
    Appendable ap = new StringBuilder();
    ImageCollection model = new ImageList();
    ApplicationController controller = new ImageControllerExt(model, rd, ap);
    controller.control();

    //check if the contents of controller are working correctly.
    assertEquals(actualImg, model.returnImage("jay"));
  }

  /**
   * Test case to verify when an invalid command is passed as input.
   *
   * @throws FileNotFoundException if valid command is not passed.
   */
  @Test
  public void testInValidInputCommands() throws FileNotFoundException {
    Image actualImg = new ImageImpl.ImageBuilder().loadFile(this.loadPPM("JD.ppm"));
    Readable rd = new InputStreamReader(new ByteArrayInputStream(("lad JD.ppm JD" +
            "\nquit").getBytes()));
    Appendable ap = new StringBuilder();
    ImageCollection model = new ImageList();
    ApplicationController controller = new ImageControllerExt(model, rd, ap);
    controller.control();

    //check if the contents of controller are working correctly.
    assertNull(model.returnImage("JD"));
  }


  /**
   * Test case to verify when an wrong file name is passed as input.
   *
   * @throws FileNotFoundException if wrong file name is not passed.
   */
  @Test
  public void testInValidInputCommandsWithWrongFilename() {
    Readable rd = new InputStreamReader(new ByteArrayInputStream(("load JJ.ppm JD" +
            "\nquit").getBytes()));
    Appendable ap = new StringBuilder();
    ImageCollection model = new ImageList();
    ApplicationController controller = new ImageControllerExt(model, rd, ap);
    controller.control();
    List<String> errorMsg = List.of(("Error: Invalid Image passed in input."));
    List<String> output = List.of(ap.toString().split("\n"));
    assertEquals(output.subList(19, output.size() - 1), errorMsg);
  }

  /**
   * Test case to verify when a wrong file name is passed as input to save the file.
   *
   * @throws FileNotFoundException if wrong file name is not passed.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testInValidInputCommandsWithWrongFilenameToSave() throws IllegalArgumentException {
    Readable rd = new InputStreamReader(new ByteArrayInputStream(("load JD.ppm JD" +
            "\nsave JD.txt JD" +
            "\nquit").getBytes()));
    Appendable ap = new StringBuilder();
    ImageCollection model = new ImageList();
    ApplicationController controller = new ImageControllerExt(model, rd, ap);
    controller.control();
    String errorMsg = "Error: Invalid Image Extension passed in input.";
    assertTrue(ap.toString().contains(errorMsg));
  }

  /**
   * Test case to verify the transitions of image from PPM to PNG and back to PPM.
   *
   * @throws FileNotFoundException if valid file is not passed.
   */
  @Test
  public void transitionCaseFromPPMtoPNGtoPPM() throws IOException {
    Image actualImg = new ImageImpl.ImageBuilder().loadFile(this.loadPPM("JD.ppm"));
    Image actualImgNewPPM =
            new ImageImpl.ImageBuilder().loadFile(this.loadPPM("JDTransition.ppm"));
    Image actualImgPNG = new ImageImpl.ImageBuilder().loadFile(this.loadImage("JD.png"));
    Image actualImgJPG = new ImageImpl.ImageBuilder().loadFile(this.loadImage("JD.jpg"));
    Image actualImgBMP = new ImageImpl.ImageBuilder().loadFile(this.loadImage("JD.bmp"));

    Readable rd = new InputStreamReader(new ByteArrayInputStream(("load JD.ppm JD" +
            "\nbrighten 50 JD JD-b" +
            "\nsave JD.jpg JD-b" +
            "\nload JD.jpg JDJPG" +
            "\ndither JDJPG JDJPG-d" +
            "\nsave JD.png JDJPG-d" +
            "\nload JD.png JDPNG" +
            "\nvertical-flip JDPNG JDPNG-VF" +
            "\nsave JD.bmp JDPNG-VF" +
            "\nload JD.bmp JDBMP" +
            "\nhorizontal-flip JDBMP JDBMP-HF" +
            "\nsave JDTransition.ppm JDBMP-HF" +
            "\nquit").getBytes()));
    Appendable ap = new StringBuilder();
    ImageCollection model = new ImageList();
    ApplicationController controller = new ImageControllerExt(model, rd, ap);
    controller.control();
    assertEquals(actualImg, model.returnImage("JD"));
    assertEquals(actualImgPNG, model.returnImage("JDJPG-d"));
    assertEquals(actualImgJPG, model.returnImage("JD-b"));
    assertEquals(actualImgBMP, model.returnImage("JDPNG-VF"));
    assertEquals(actualImgNewPPM, model.returnImage("JDBMP-HF"));
  }

  /**
   * Test sequence of operations passed to the controller to manipulate an image.
   *
   * @throws IOException if image doesn't exist.
   */
  @Test
  public void testMultipleOperations() throws IOException {
    Image actualImg = new ImageImpl.ImageBuilder().loadFile(
            this.loadImage("JAY.png"));
    Command command = new ImageDither();
    actualImg = ((ImageExt) actualImg).runCommand(command);
    actualImg = actualImg.horizontalFlip();
    actualImg = actualImg.verticalFlip();
    actualImg = actualImg.brighten(50);
    Readable rd = new InputStreamReader(new ByteArrayInputStream(("load JAY.png jay" +
            "\ndither jay jay-dither" +
            "\nhorizontal-flip jay-dither j-dh" +
            "\nvertical-flip j-dh j-dhv" +
            "\nbrighten 50 j-dhv j-dhvb" +
            "\nquit").getBytes()));
    Appendable ap = new StringBuilder();
    ImageCollection model = new ImageList();
    ApplicationController controller = new ImageControllerExt(model, rd, ap);
    controller.control();

    //check if the contents of controller are working correctly.
    assertEquals(actualImg, model.returnImage("j-dhvb"));
  }

  /**
   * Test sequence of operations passed to the controller to manipulate a JPG image.
   *
   * @throws IOException if image doesn't exist.
   */
  @Test
  public void testMultipleOperationsOnJPG() throws IOException {
    Image actualImg = new ImageImpl.ImageBuilder().loadFile(
            this.loadImage("Dog.jpg"));
    Image actualImgini = new ImageImpl.ImageBuilder().loadFile(
            this.loadImage("Dog.jpg"));
    Command command = new ImageDither();
    Command command2 = new ImageBlur();
    Command command4 = new ImageSharpen();
    actualImg = ((ImageExt) actualImg).runCommand(command);
    actualImg = ((ImageExt) actualImg).runCommand(command2);
    actualImg = ((ImageExt) actualImg).runCommand(command4);
    actualImg = actualImg.horizontalFlip();
    actualImg = actualImg.verticalFlip();
    actualImg = actualImg.horizontalFlip();
    actualImg = actualImg.verticalFlip();
    actualImg = actualImg.brighten(50);
    actualImg = actualImg.brighten(-50);
    Image actualImgR = actualImg.greyScale("red-component");
    Image actualImgG = actualImg.greyScale("green-component");
    Image actualImgB = actualImg.greyScale("blue-component");
    Image actualImgI = actualImg.greyScale("intensity-component");
    Image actualImgL = actualImg.greyScale("luma-component");
    Image actualImgV = actualImg.greyScale("value-component");
    Image imgSplit = (Image) actualImg.rgbSplit();
    Image finalIMG = actualImgR.rgbCombine(actualImgG, actualImgB);
    Readable rd = new InputStreamReader(new ByteArrayInputStream(("load Dog.jpg dog" +
            "\ndither dog dog-d" +
            "\nsepia dog-ds dog-d" +
            "\nimage-blur dog-dsib dog-ds" +
            "\ngrey-scaled dog-dsibgs dog dsib" +
            "\nimage-sharpen dog-dsibgss dog-dsibgs" +
            "\nhorizontal-flip dog-h dog-dsibgss" +
            "\nvertical-flip dog-hv dog-h" +
            "\nhorizontal-flip dog-hvh dog-hv" +
            "\nvertical-flip dog-hvhv dog-hvh" +
            "\nbrighten 50 dog-hvhvb dog-hvhv" +
            "\nbrighten -50 dog-hvhvbd dog-hvhvb" +
            "\ngreyscale red-component dog-hvhvbdr dog-hvhvbd" +
            "\ngreyscale green-component dog-hvhvbdrg dog-hvhvbdr" +
            "\ngreyscale blue-component dog-hvhvbdrgb dog-hvhvbdrg" +
            "\ngreyscale intensity-component dog-hvhvbdrgbi dog-hvhvbdrgb" +
            "\ngreyscale luma-component dog-hvhvbdrgbil dog-hvhvbdrgbi" +
            "\ngreyscale value-component dog-hvhvbdrgbilv dog-hvhvbdrgbil" +
            "\nrgb-combine final dog-hvhvbdr dog-hvhvbdrg dog-hvhvbdrgb" +
            "\nsave dogFinal.jpg final" +
            "\nquit").getBytes()));
    Appendable ap = new StringBuilder();
    ImageCollection model = new ImageList();
    ApplicationController controller = new ImageControllerExt(model, rd, ap);
    controller.control();

    //check if the contents of controller are working correctly.
    assertEquals(actualImgini, model.returnImage("dog"));
    assertEquals(finalIMG, model.returnImage("final"));
  }

  /**
   * Test sequence of operations passed to the controller to manipulate a PNG image.
   *
   * @throws IOException if image doesn't exist.
   */
  @Test
  public void testMultipleOperationsOnPNG() throws IOException {
    Image actualImg = new ImageImpl.ImageBuilder().loadFile(
            this.loadImage("JAY.png"));
    Image actualImgini = new ImageImpl.ImageBuilder().loadFile(
            this.loadImage("JAY.png"));
    Command command = new ImageDither();
    Command command2 = new ImageBlur();
    Command command4 = new ImageSharpen();
    actualImg = ((ImageExt) actualImg).runCommand(command);
    actualImg = ((ImageExt) actualImg).runCommand(command2);
    actualImg = ((ImageExt) actualImg).runCommand(command4);
    actualImg = actualImg.horizontalFlip();
    actualImg = actualImg.verticalFlip();
    actualImg = actualImg.horizontalFlip();
    actualImg = actualImg.verticalFlip();
    actualImg = actualImg.brighten(50);
    actualImg = actualImg.brighten(-50);
    Image actualImgR = actualImg.greyScale("red-component");
    Image actualImgG = actualImg.greyScale("green-component");
    Image actualImgB = actualImg.greyScale("blue-component");
    Image actualImgI = actualImg.greyScale("intensity-component");
    Image actualImgL = actualImg.greyScale("luma-component");
    Image actualImgV = actualImg.greyScale("value-component");
    Image imgSplit = (Image) actualImg.rgbSplit();
    Image finalIMG = actualImgR.rgbCombine(actualImgG, actualImgB);
    Readable rd = new InputStreamReader(new ByteArrayInputStream(("load JAY.png jay" +
            "\ndither jay jay-d" +
            "\nsepia jay-ds jay-d" +
            "\nimage-blur jay-dsib jay-ds" +
            "\ngrey-scaled jay-dsibgs jay dsib" +
            "\nimage-sharpen jay-dsibgss jay-dsibgs" +
            "\nhorizontal-flip jay-h jay-dsibgss" +
            "\nvertical-flip jay-hv jay-h" +
            "\nhorizontal-flip jay-hvh jay-hv" +
            "\nvertical-flip jay-hvhv jay-hvh" +
            "\nbrighten 50 jay-hvhvb jay-hvhv" +
            "\nbrighten -50 jay-hvhvbd jay-hvhvb" +
            "\ngreyscale red-component jay-hvhvbdr jay-hvhvbd" +
            "\ngreyscale green-component jay-hvhvbdrg jay-hvhvbdr" +
            "\ngreyscale blue-component jay-hvhvbdrgb jay-hvhvbdrg" +
            "\ngreyscale intensity-component jay-hvhvbdrgbi jay-hvhvbdrgb" +
            "\ngreyscale luma-component jay-hvhvbdrgbil jay-hvhvbdrgbi" +
            "\ngreyscale value-component jay-hvhvbdrgbilv jay-hvhvbdrgbil" +
            "\nrgb-combine final jay-hvhvbdr jay-hvhvbdrg jay-hvhvbdrgb" +
            "\nsave jayFinal.png final" +
            "\nquit").getBytes()));
    Appendable ap = new StringBuilder();
    ImageCollection model = new ImageList();
    ApplicationController controller = new ImageControllerExt(model, rd, ap);
    controller.control();

    //check if the contents of controller are working correctly.
    assertEquals(actualImgini, model.returnImage("jay"));
    assertEquals(finalIMG, model.returnImage("final"));
  }

  /**
   * Test sequence of operations passed to the controller to manipulate a PPM image.
   *
   * @throws IOException if image doesn't exist.
   */
  @Test
  public void testMultipleOperationsOnPPM() throws IOException {
    Image actualImg = new ImageImpl.ImageBuilder().loadFile(
            this.loadPPM("JD.ppm"));
    Image actualImgini = new ImageImpl.ImageBuilder().loadFile(
            this.loadPPM("JD.ppm"));
    Command command = new ImageDither();
    Command command2 = new ImageBlur();
    Command command4 = new ImageSharpen();
    actualImg = ((ImageExt) actualImg).runCommand(command);
    actualImg = ((ImageExt) actualImg).runCommand(command2);
    actualImg = ((ImageExt) actualImg).runCommand(command4);
    actualImg = actualImg.horizontalFlip();
    actualImg = actualImg.verticalFlip();
    actualImg = actualImg.horizontalFlip();
    actualImg = actualImg.verticalFlip();
    actualImg = actualImg.brighten(50);
    actualImg = actualImg.brighten(-50);
    Image actualImgR = actualImg.greyScale("red-component");
    Image actualImgG = actualImg.greyScale("green-component");
    Image actualImgB = actualImg.greyScale("blue-component");
    Image actualImgI = actualImg.greyScale("intensity-component");
    Image actualImgL = actualImg.greyScale("luma-component");
    Image actualImgV = actualImg.greyScale("value-component");
    Image imgSplit = (Image) actualImg.rgbSplit();
    Image finalIMG = actualImgR.rgbCombine(actualImgG, actualImgB);
    Readable rd = new InputStreamReader(new ByteArrayInputStream(("load JD.ppm JD" +
            "\ndither JD JD-d" +
            "\nsepia JD-ds JD-d" +
            "\nimage-blur JD-dsib JD-ds" +
            "\ngrey-scaled JD-dsibgs JD dsib" +
            "\nimage-sharpen JD-dsibgss JD-dsibgs" +
            "\nhorizontal-flip JD-h JD-dsibgss" +
            "\nvertical-flip JD-hv JD-h" +
            "\nhorizontal-flip JD-hvh JD-hv" +
            "\nvertical-flip JD-hvhv JD-hvh" +
            "\nbrighten 50 JD-hvhvb JD-hvhv" +
            "\nbrighten -50 JD-hvhvbd JD-hvhvb" +
            "\ngreyscale red-component JD-hvhvbdr JD-hvhvbd" +
            "\ngreyscale green-component JD-hvhvbdrg JD-hvhvbdr" +
            "\ngreyscale blue-component JD-hvhvbdrgb JD-hvhvbdrg" +
            "\ngreyscale intensity-component JD-hvhvbdrgbi JD-hvhvbdrgb" +
            "\ngreyscale luma-component JD-hvhvbdrgbil JD-hvhvbdrgbi" +
            "\ngreyscale value-component JD-hvhvbdrgbilv JD-hvhvbdrgbil" +
            "\nrgb-combine final JD-hvhvbdr JD-hvhvbdrg JD-hvhvbdrgb" +
            "\nsave JDFinal.ppm final" +
            "\nquit").getBytes()));
    Appendable ap = new StringBuilder();
    ImageCollection model = new ImageList();
    ApplicationController controller = new ImageControllerExt(model, rd, ap);
    controller.control();

    //check if the contents of controller are working correctly.
    assertEquals(actualImgini, model.returnImage("JD"));
    assertEquals(finalIMG, model.returnImage("final"));
  }


  /**
   * Test sequence of operations passed to the controller to manipulate a BMP image.
   *
   * @throws IOException if image doesn't exist.
   */
  @Test
  public void testMultipleOperationsOnBMP() throws IOException {
    Image actualImg = new ImageImpl.ImageBuilder().loadFile(
            this.loadPPM("TIGER.bmp"));
    Image actualImgini = new ImageImpl.ImageBuilder().loadFile(
            this.loadPPM("TIGER.bmp"));
    Command command = new ImageDither();
    Command command2 = new ImageBlur();
    Command command4 = new ImageSharpen();
    actualImg = ((ImageExt) actualImg).runCommand(command);
    actualImg = ((ImageExt) actualImg).runCommand(command2);
    actualImg = ((ImageExt) actualImg).runCommand(command4);
    actualImg = actualImg.horizontalFlip();
    actualImg = actualImg.verticalFlip();
    actualImg = actualImg.horizontalFlip();
    actualImg = actualImg.verticalFlip();
    actualImg = actualImg.brighten(50);
    actualImg = actualImg.brighten(-50);
    Image actualImgR = actualImg.greyScale("red-component");
    Image actualImgG = actualImg.greyScale("green-component");
    Image actualImgB = actualImg.greyScale("blue-component");
    Image actualImgI = actualImg.greyScale("intensity-component");
    Image actualImgL = actualImg.greyScale("luma-component");
    Image actualImgV = actualImg.greyScale("value-component");
    Image imgSplit = (Image) actualImg.rgbSplit();
    Image finalIMG = actualImgR.rgbCombine(actualImgG, actualImgB);
    Readable rd = new InputStreamReader(new ByteArrayInputStream(("load TIGER.bmp tiger" +
            "\ndither tiger tiger-d" +
            "\nsepia tiger-ds tiger-d" +
            "\nimage-blur tiger-dsib tiger-ds" +
            "\ngrey-scaled tiger-dsibgs tiger dsib" +
            "\nimage-sharpen tiger-dsibgss tiger-dsibgs" +
            "\nhorizontal-flip tiger-h tiger-dsibgss" +
            "\nvertical-flip tiger-hv tiger-h" +
            "\nhorizontal-flip tiger-hvh tiger-hv" +
            "\nvertical-flip tiger-hvhv tiger-hvh" +
            "\nbrighten 50 tiger-hvhvb tiger-hvhv" +
            "\nbrighten -50 tiger-hvhvbd tiger-hvhvb" +
            "\ngreyscale red-component tiger-hvhvbdr tiger-hvhvbd" +
            "\ngreyscale green-component tiger-hvhvbdrg tiger-hvhvbdr" +
            "\ngreyscale blue-component tiger-hvhvbdrgb tiger-hvhvbdrg" +
            "\ngreyscale intensity-component tiger-hvhvbdrgbi tiger-hvhvbdrgb" +
            "\ngreyscale luma-component tiger-hvhvbdrgbil tiger-hvhvbdrgbi" +
            "\ngreyscale value-component tiger-hvhvbdrgbilv tiger-hvhvbdrgbil" +
            "\nrgb-combine final tiger-hvhvbdr tiger-hvhvbdrg tiger-hvhvbdrgb" +
            "\nsave tigerFinal.bmp final" +
            "\nquit").getBytes()));
    Appendable ap = new StringBuilder();
    ImageCollection model = new ImageList();
    ApplicationController controller = new ImageControllerExt(model, rd, ap);
    controller.control();

    //check if the contents of controller are working correctly.
    assertEquals(actualImgini, model.returnImage("tiger"));
    assertEquals(finalIMG, model.returnImage("final"));

  }

  /**
   * Test sequence of save operations passed to the controller to manipulate a BMP image.
   *
   * @throws IOException if image doesn't exist.
   */
  @Test
  public void testMultipleSaveOnBMP() throws IOException {
    Image actualImg = new ImageImpl.ImageBuilder().loadFile(
            this.loadImage("TIGER.bmp"));
    Image imgPNG = new ImageImpl.ImageBuilder().loadFile(
            this.loadImage("TIGER.png"));
    Image imgJPG = new ImageImpl.ImageBuilder().loadFile(
            this.loadImage("TIGER.jpg"));
    Image imgPPM = new ImageImpl.ImageBuilder().loadFile(
            this.loadPPM("TIGER.ppm"));

    Readable rd = new InputStreamReader(new ByteArrayInputStream(("load TIGER.bmp tiger" +
            "\nsave TIGER.png tiger" +
            "\nsave TIGER.ppm tiger" +
            "\nsave TIGER.jpg tiger" +
            "\nload TIGER.png tiger-png" +
            "\nload TIGER.jpg tiger-jpg" +
            "\nload TIGER.ppm tiger-ppm" +
            "\nquit").getBytes()));
    Appendable ap = new StringBuilder();
    ImageCollection model = new ImageList();
    ApplicationController controller = new ImageControllerExt(model, rd, ap);
    controller.control();
    assertEquals(actualImg, model.returnImage("tiger"));
    assertEquals(imgPNG, model.returnImage("tiger-png"));
    assertEquals(imgJPG, model.returnImage("tiger-jpg"));
    assertEquals(imgPPM, model.returnImage("tiger-ppm"));

  }

  /**
   * Test sequence of save operations passed to the controller to manipulate a JPG image.
   *
   * @throws IOException if image doesn't exist.
   */
  @Test
  public void testMultipleSaveOnJPG() throws IOException {
    Image actualImg = new ImageImpl.ImageBuilder().loadFile(
            this.loadImage("Dog.jpg"));
    Image imgPNG = new ImageImpl.ImageBuilder().loadFile(
            this.loadImage("Dog.png"));
    Image imgBMP = new ImageImpl.ImageBuilder().loadFile(
            this.loadImage("Dog.bmp"));
    Image imgPPM = new ImageImpl.ImageBuilder().loadFile(
            this.loadPPM("Dog.ppm"));

    Readable rd = new InputStreamReader(new ByteArrayInputStream(("load Dog.jpg tiger" +
            "\nsave Dog.png tiger" +
            "\nsave Dog.ppm tiger" +
            "\nsave Dog.bmp tiger" +
            "\nload Dog.png tiger-png" +
            "\nload Dog.jpg tiger-bmp" +
            "\nload Dog.ppm tiger-ppm" +
            "\nquit").getBytes()));
    Appendable ap = new StringBuilder();
    ImageCollection model = new ImageList();
    ApplicationController controller = new ImageControllerExt(model, rd, ap);
    controller.control();
    assertEquals(actualImg, model.returnImage("tiger"));
    assertEquals(imgPNG, model.returnImage("tiger-png"));
    assertEquals(imgBMP, model.returnImage("tiger-bmp"));
    assertEquals(imgPPM, model.returnImage("tiger-ppm"));

  }

  /**
   * Test sequence of save operations passed to the controller to manipulate a PPM image.
   *
   * @throws IOException if image doesn't exist.
   */
  @Test
  public void testMultipleSaveOnPPM() throws IOException {
    Image actualImg = new ImageImpl.ImageBuilder().loadFile(
            this.loadPPM("JD.ppm"));
    Image imgPNG = new ImageImpl.ImageBuilder().loadFile(
            this.loadImage("JD.png"));
    Image imgJPG = new ImageImpl.ImageBuilder().loadFile(
            this.loadImage("JD.jpg"));
    Image imgBMP = new ImageImpl.ImageBuilder().loadFile(
            this.loadImage("JD.bmp"));

    Readable rd = new InputStreamReader(new ByteArrayInputStream(("load JD.ppm JD" +
            "\nsave Dog.png JD" +
            "\nsave Dog.bmp JD" +
            "\nsave Dog.jpg JD" +
            "\nload Dog.png JD-png" +
            "\nload Dog.jpg JD-jpg" +
            "\nload Dog.bmp JD-bmp" +
            "\nquit").getBytes()));
    Appendable ap = new StringBuilder();
    ImageCollection model = new ImageList();
    ApplicationController controller = new ImageControllerExt(model, rd, ap);
    controller.control();
    assertEquals(actualImg, model.returnImage("JD"));
    assertEquals(imgPNG, model.returnImage("JD-png"));
    assertEquals(imgJPG, model.returnImage("JD-jpg"));
    assertEquals(imgBMP, model.returnImage("JD-bmp"));

  }

  /**
   * Test sequence of save operations passed to the controller to manipulate a PNG image.
   *
   * @throws IOException if image doesn't exist.
   */
  @Test
  public void testMultipleSaveOnPNG() throws IOException {
    Image actualImg = new ImageImpl.ImageBuilder().loadFile(
            this.loadImage("JAY.png"));
    Image imgPPM = new ImageImpl.ImageBuilder().loadFile(
            this.loadPPM("JAY.ppm"));
    Image imgJPG = new ImageImpl.ImageBuilder().loadFile(
            this.loadImage("JAY.jpg"));
    Image imgBMP = new ImageImpl.ImageBuilder().loadFile(
            this.loadImage("JAY.bmp"));

    Readable rd = new InputStreamReader(new ByteArrayInputStream(("load JAY.png JD" +
            "\nsave JAY.ppm JD" +
            "\nsave JAY.bmp JD" +
            "\nsave JAY.jpg JD" +
            "\nload JAY.ppm JD-ppm" +
            "\nload JAY.jpg JD-jpg" +
            "\nload JAY.bmp JD-bmp" +
            "\nquit").getBytes()));
    Appendable ap = new StringBuilder();
    ImageCollection model = new ImageList();
    ApplicationController controller = new ImageControllerExt(model, rd, ap);
    controller.control();
    assertEquals(actualImg, model.returnImage("JD"));
    assertEquals(imgPPM, model.returnImage("JD-ppm"));
    assertEquals(imgJPG, model.returnImage("JD-jpg"));
    assertEquals(imgBMP, model.returnImage("JD-bmp"));

  }

  /**
   * Test case to run the application with inputs from the script file.
   *
   * @throws IOException if valid file is not passed.
   */
  @Test
  public void testScript() throws IOException {
    Image img = new ImageImpl.ImageBuilder().loadFile(
            this.loadPPM("res/JD.ppm"));
    Image imgJPG = new ImageImpl.ImageBuilder().loadFile(
            this.loadImage("JD.jpg"));

    Command command = new ImageSharpen();
    Image imgBrighten = img.brighten(50);
    Image imgSharpen = ((ImageExt) imgJPG).runCommand(command);
    Readable rd = new InputStreamReader(new ByteArrayInputStream(
            ("run res/script.txt").getBytes()));
    Appendable ap = new StringBuilder();
    ImageCollection model = new ImageList();
    ApplicationController controller = new ImageControllerExt(model, rd, ap);
    controller.control();
    assertEquals(imgBrighten, model.returnImage("jay-bright"));
    assertEquals(imgSharpen, model.returnImage("jay-sharp"));
  }


  /**
   * Test case to run the application with error inputs from the script file.
   *
   * @throws IOException if valid file is not passed.
   */
  @Test(expected = IOException.class)
  public void testScriptError() throws IOException {
    Image img = new ImageImpl.ImageBuilder().loadFile(
            this.loadPPM("res/JD.ppm"));
    Image imgJPG = new ImageImpl.ImageBuilder().loadFile(
            this.loadImage("JD.jpg"));

    Command command = new ImageSharpen();
    Image imgBrighten = img.brighten(50);
    Image imgSharpen = ((ImageExt) imgJPG).runCommand(command);
    Readable rd = new InputStreamReader(new ByteArrayInputStream(
            ("run res/error_script.txt").getBytes()));
    Appendable ap = new StringBuilder();
    ImageCollection model = new ImageList();
    ApplicationController controller = new ImageControllerExt(model, rd, ap);
    controller.control();
    assertEquals(imgBrighten, model.returnImage("jay-bright"));
    assertEquals(imgSharpen, model.returnImage("jay-sharp"));
  }

}

