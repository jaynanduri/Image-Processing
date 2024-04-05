package controller;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import model.ImageCollection;
import model.ImageList;
import model.ReadOnlyModel;
import model.ReadOnlyModelImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

/**
 * This class represents test class for the Image GUI controller. It tests the functionality of the
 * controller for GUI.
 */
public class GuiControllerTest {
  private ImageCollection model;
  private ReadOnlyModel roi;
  private Features controller;
  private ApplicationController commandController;

  @Before
  public void setup() {
    model = new ImageList();
    roi = new ReadOnlyModelImpl(model);
    Readable rd = new InputStreamReader(System.in);
    Appendable ap = System.out;
    commandController = new ImageControllerExt(model, rd, ap);
    controller = new GuiController(commandController, null, model);
  }

  @Test
  public void testLoadImage() {
    controller.loadImageFile("res/JD.ppm");
    Scanner sc = new Scanner("load res/JD.ppm view-image");
    commandController.processCommand(sc.next(), sc, model);
    assertEquals(model.returnImage("view-image"), model.returnImage("view-image"));
  }

  @Test
  public void testSaveImage() throws IOException {
    controller.loadImageFile("res/JD.ppm");
    controller.saveImage("res/gui-JD.ppm");
    Scanner sc = new Scanner("save res/cl-JD.ppm view-image");
    commandController.processCommand(sc.next(), sc, model);
    byte[] f1 = Files.readAllBytes(Path.of("res/gui-JD.ppm"));
    byte[] f2 = Files.readAllBytes(Path.of("res/cl-JD.ppm"));
    assertArrayEquals(f2, f1);
  }

  @Test
  public void testHFlip() {
    controller.loadImageFile("res/JD.ppm");
    controller.hFlipImage();
    Scanner sc = new Scanner("horizontal-flip view-image view-image");
    commandController.processCommand(sc.next(), sc, model);
    assertEquals(model.returnImage("view-image"), model.returnImage("view-image"));
  }

  @Test
  public void testVFlip() {
    controller.loadImageFile("res/JD.ppm");
    controller.vFlipImage();
    Scanner sc = new Scanner("vertical-flip view-image view-image");
    commandController.processCommand(sc.next(), sc, model);
    assertEquals(model.returnImage("view-image"), model.returnImage("view-image"));
  }

  @Test
  public void testBrightenImage() {
    controller.loadImageFile("res/JD.ppm");
    controller.brightenImage("10");
    Scanner sc = new Scanner("greyscale 10 view-image view-image");
    commandController.processCommand(sc.next(), sc, model);
    assertEquals(model.returnImage("view-image"), model.returnImage("view-image"));
  }

  @Test
  public void testImageBlur() {
    controller.loadImageFile("res/JD.ppm");
    controller.imageBlur();
    Scanner sc = new Scanner("image-blur view-image view-image");
    commandController.processCommand(sc.next(), sc, model);
    assertEquals(model.returnImage("view-image"), model.returnImage("view-image"));
  }

  @Test
  public void testImageSharpen() {
    controller.loadImageFile("res/JD.ppm");
    controller.imageSharpen();
    Scanner sc = new Scanner("image-sharpen view-image view-image");
    commandController.processCommand(sc.next(), sc, model);
    assertEquals(model.returnImage("view-image"), model.returnImage("view-image"));
  }

  @Test
  public void testGreyTransform() {
    controller.loadImageFile("res/JD.ppm");
    controller.greyTransform();
    Scanner sc = new Scanner("grey-scaled view-image view-image");
    commandController.processCommand(sc.next(), sc, model);
    assertEquals(model.returnImage("view-image"), model.returnImage("view-image"));
  }

  @Test
  public void testSepiaTransform() {
    controller.loadImageFile("res/JD.ppm");
    controller.sepiaTransform();
    Scanner sc = new Scanner("sepia view-image view-image");
    commandController.processCommand(sc.next(), sc, model);
    assertEquals(model.returnImage("view-image"), model.returnImage("view-image"));
  }

  @Test
  public void testDither() {
    controller.loadImageFile("res/JD.ppm");
    controller.dither();
    Scanner sc = new Scanner("dither view-image view-image");
    commandController.processCommand(sc.next(), sc, model);
    assertEquals(model.returnImage("view-image"), model.returnImage("view-image"));
  }

  @Test
  public void testGreyScale() {
    controller.loadImageFile("res/JD.ppm");
    controller.greyscale("red-component");
    Scanner sc = new Scanner("greyscale red-component view-image view-image");
    commandController.processCommand(sc.next(), sc, model);
    assertEquals(model.returnImage("view-image"), model.returnImage("view-image"));
  }

  @Test
  public void testRGBSplit() {
    controller.loadImageFile("res/JD.ppm");
    List<String> filePaths = new ArrayList<>();
    filePaths.add("res/script-images/JD-grey-red1.jpg");
    filePaths.add("res/script-images/JD-grey-green1.jpg");
    filePaths.add("res/script-images/JD-grey-blue2.jpg");
    controller.rgbSplit(filePaths);
    Scanner sc = new Scanner("rgb-split view-image view-red view-green view-blue");
    commandController.processCommand(sc.next(), sc, model);
    assertEquals(model.returnImage("view-red"), model.returnImage("view-red"));
    assertEquals(model.returnImage("view-green"), model.returnImage("view-green"));
    assertEquals(model.returnImage("view-blue"), model.returnImage("view-blue"));
  }

  @Test
  public void testRGBCombine() {
    List<String> filePaths = new ArrayList<>();
    filePaths.add("res/script-images/JD-grey-red.jpg");
    filePaths.add("res/script-images/JD-grey-green.jpg");
    filePaths.add("res/script-images/JD-grey-blue.jpg");
    controller.rgbCombine(filePaths);
    Scanner sc = new Scanner("rgb-combine view-image view-red view-green view-blue");
    commandController.processCommand(sc.next(), sc, model);
    assertEquals(model.returnImage("view-image"), model.returnImage("view-image"));
  }

  @Test
  public void testRoModel() {
    controller.loadImageFile("res/JD.ppm");
    assertEquals(roi.getBufferedImg("view-image"),
            model.returnImage("view-image").getBufferedImage());
  }

}