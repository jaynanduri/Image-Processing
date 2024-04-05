package model;

import org.junit.Before;
import org.junit.Test;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;


import static org.junit.Assert.assertEquals;

/**
 * This class represents test cases for model.
 */
public class ImageImplExtTest {
  private Image img;

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

  private void applyKernel(List<List<ImageImpl.Pixel>> pixels,
                           List<Double> kernel) {
    for (int col = 0; col < pixels.size(); col++) {
      List<ImageImpl.Pixel> rowList = pixels.get(col);
      for (int row = 0; row < pixels.get(0).size(); row++) {
        ImageImpl.Pixel p = rowList.get(row);
        p.red = vectorMultiply(pixels, kernel, row, col, "red");
        p.green = vectorMultiply(pixels, kernel, row, col, "green");
        p.blue = vectorMultiply(pixels, kernel, row, col, "blue");
        rowList.set(row, p);
      }
      pixels.set(col, rowList);
    }
  }

  /**
   * initialise image by reading it from file.
   *
   * @throws IOException if image doesn't exist.
   */
  @Before
  public void setup() throws IOException {
    this.img = new ImageImpl.ImageBuilder().loadFile(this.loadImage("jay.jpg"));
  }

  /**
   * Test functionality of image blur operation.
   */
  @Test
  public void testImageBlur() {
    List<List<ImageImpl.Pixel>> actualValue = img.getPixels();
    Command imgBlur = new ImageBlur();
    Image img2 = ((ImageImplExt) img).runCommand(imgBlur);
    List<List<Double>> blurMat = new ArrayList<>();
    blurMat.add(new ArrayList<>(Arrays.asList(1.0 / 16, 1.0 / 8, 1.0 / 16)));
    blurMat.add(new ArrayList<>(Arrays.asList(1.0 / 8, 1.0 / 4, 1.0 / 8)));
    blurMat.add(new ArrayList<>(Arrays.asList(1.0 / 16, 1.0 / 8, 1.0 / 16)));
    applyKernel(actualValue, blurMat.stream().flatMap(List::stream).collect(Collectors.toList()));
    for (int col = 0; col < actualValue.size(); col++) {
      for (int row = 0; row < actualValue.get(0).size(); row++) {
        assertEquals(actualValue.get(col).get(row), img2.getPixels().get(col).get(row));
      }
    }
  }

  /**
   * Test functionality of image sharpen.
   */
  @Test
  public void testImageSharpen() {
    List<List<ImageImpl.Pixel>> actualValue = img.getPixels();
    Command command = new ImageSharpen();
    Image img2 = ((ImageImplExt) img).runCommand(command);
    List<List<Double>> sharpMat = new ArrayList<>();
    sharpMat.add(new ArrayList<>(Arrays.asList(-1.0 / 8, -1.0 / 8, -1.0 / 8, -1.0 / 8, -1.0 / 8)));
    sharpMat.add(new ArrayList<>(Arrays.asList(-1.0 / 8, 1.0 / 4, 1.0 / 4, 1.0 / 4, -1.0 / 8)));
    sharpMat.add(new ArrayList<>(Arrays.asList(-1.0 / 8, 1.0 / 4, 1.0, 1.0 / 4, -1.0 / 8)));
    sharpMat.add(new ArrayList<>(Arrays.asList(-1.0 / 8, 1.0 / 4, 1.0 / 4, 1.0 / 4, -1.0 / 8)));
    sharpMat.add(new ArrayList<>(Arrays.asList(-1.0 / 8, -1.0 / 8, -1.0 / 8, -1.0 / 8, -1.0 / 8)));
    applyKernel(actualValue, sharpMat.stream().flatMap(List::stream).collect(Collectors.toList()));
    for (int col = 0; col < actualValue.size(); col++) {
      for (int row = 0; row < actualValue.get(0).size(); row++) {
        assertEquals(actualValue.get(col).get(row), img2.getPixels().get(col).get(row));
      }
    }
  }

  /**
   * test image grey scale operation to be performed on image.
   */
  @Test
  public void testImageGreyScale() {
    List<List<ImageImpl.Pixel>> actualValue = img.getPixels();
    Command command = new ColorTransform(new ArrayList<>());
    Image img2 = ((ImageImplExt) img).runCommand(command);
    for (int col = 0; col < actualValue.size(); col++) {
      for (int row = 0; row < actualValue.get(0).size(); row++) {
        assertEquals(img.greyScale("luma-component").getPixels().get(col).get(row),
                img2.getPixels().get(col).get(row));
      }
    }
  }

  /**
   * testing image sepia operation.
   */
  @Test
  public void testImageSepia() {
    List<List<ImageImpl.Pixel>> actualValue = img.getPixels();
    List<List<Double>> sepiaMat = new ArrayList<>();
    sepiaMat.add(new ArrayList<>(Arrays.asList(0.393, 0.769, 0.189)));
    sepiaMat.add(new ArrayList<>(Arrays.asList(0.349, 0.686, 0.168)));
    sepiaMat.add(new ArrayList<>(Arrays.asList(0.272, 0.534, 0.131)));
    Command command = new ColorTransform(new ArrayList<>());
    Image img2 = ((ImageImplExt) img).runCommand(command);
    applySepia(actualValue, sepiaMat);
    for (int col = 0; col < actualValue.size(); col++) {
      for (int row = 0; row < actualValue.get(0).size(); row++) {
        assertEquals(actualValue.get(col).get(row), img2.getPixels().get(col).get(row));
      }
    }
  }

  /**
   * testing image dither operation.
   */
  @Test
  public void testImageDither() {
    List<List<ImageImpl.Pixel>> actualValue = img.getPixels();
    Command command = new ImageDither();
    Image img2 = ((ImageImplExt) img).runCommand(command);
    applyDither(actualValue);
    for (int col = 0; col < actualValue.size(); col++) {
      for (int row = 0; row < actualValue.get(0).size(); row++) {
        assertEquals(actualValue.get(col).get(row), img2.getPixels().get(col).get(row));
      }
    }
  }

  private int convertRGBToSepia(List<Double> pixMultiplier, ImageImpl.Pixel pixel) {
    int result = (int) ((pixMultiplier.get(0) * pixel.red) + (pixMultiplier.get(1) * pixel.green)
                        + (pixMultiplier.get(2) * pixel.blue));
    if (result < 0) {
      return 0;
    } else {
      return Math.min(result, 255);
    }
  }

  private void applySepia(List<List<ImageImpl.Pixel>> pixels, List<List<Double>> sepiaMat) {
    for (int col = 0; col < pixels.size(); col++) {
      List<ImageImpl.Pixel> rowList = pixels.get(col);
      for (int row = 0; row < pixels.get(0).size(); row++) {
        ImageImpl.Pixel pix = rowList.get(row);
        pix.red = convertRGBToSepia(sepiaMat.get(0), pix);
        pix.green = convertRGBToSepia(sepiaMat.get(1), pix);
        pix.blue = convertRGBToSepia(sepiaMat.get(2), pix);
        rowList.set(row, pix);
      }
      pixels.set(col, rowList);
    }
  }

  private void setNeighbourPixels(int row, int col, double error,
                                  List<List<ImageImpl.Pixel>> pixelMap) {
    if (row >= 0 && col >= 0 && row < pixelMap.get(0).size() && col < pixelMap.size()) {
      ImageImpl.Pixel pixel = pixelMap.get(col).get(row);
      double newColor = pixel.luma + error;
      if (newColor < 0) {
        newColor = 0;
      }
      if (newColor > 255) {
        newColor = 255;
      }
      List<ImageImpl.Pixel> rowList = pixelMap.get(col);
      pixel = new ImageImpl.Pixel(newColor, newColor, newColor);
      rowList.set(row, pixel);
      pixelMap.set(col, rowList);
    }
  }

  private void applyDither(List<List<ImageImpl.Pixel>> pixelMap) {
    for (int col = 0; col < pixelMap.size(); col++) {
      List<ImageImpl.Pixel> rowList = pixelMap.get(col);
      for (int row = 0; row < pixelMap.get(0).size(); row++) {
        ImageImpl.Pixel pixel = rowList.get(row);
        double oldColor = pixel.luma;
        double newColor = Math.round(oldColor / 255.0) * 255;
        double error = oldColor - newColor;
        pixel = new ImageImpl.Pixel(newColor, newColor, newColor);
        rowList.set(row, pixel);
        setNeighbourPixels(row + 1, col, error * (7.0 / 16), pixelMap);
        setNeighbourPixels(row - 1, col + 1, error * (3.0 / 16), pixelMap);
        setNeighbourPixels(row, col + 1, error * (5.0 / 16), pixelMap);
        setNeighbourPixels(row + 1, col + 1, error * (1.0 / 16), pixelMap);
      }
      pixelMap.set(col, rowList);
    }
  }

  private int vectorMultiply(List<List<ImageImpl.Pixel>> pixels, List<Double> kernel,
                             int row, int col, String color) {
    List<Double> pixelWindow = new ArrayList<>();
    if (kernel.size() == 9) {
      if (Objects.equals(color, "red")) {
        for (int i = col - 1; i <= col + 1; i++) {
          for (int j = row - 1; j <= row + 1; j++) {
            if (i >= 0 && i < pixels.size() && j >= 0 && j < pixels.get(0).size()) {
              pixelWindow.add(pixels.get(i).get(j).red);
            } else {
              pixelWindow.add(0.0);
            }
          }
        }
      } else if (Objects.equals(color, "green")) {
        for (int i = col - 1; i <= col + 1; i++) {
          for (int j = row - 1; j <= row + 1; j++) {
            if (i >= 0 && i < pixels.size() && j >= 0 && j < pixels.get(0).size()) {
              pixelWindow.add(pixels.get(i).get(j).green);
            } else {
              pixelWindow.add(0.0);
            }
          }
        }
      } else {
        for (int i = col - 1; i <= col + 1; i++) {
          for (int j = row - 1; j <= row + 1; j++) {
            if (i >= 0 && i < pixels.size() && j >= 0 && j < pixels.get(0).size()) {
              pixelWindow.add(pixels.get(i).get(j).blue);
            } else {
              pixelWindow.add(0.0);
            }
          }
        }
      }
      double pixelSum =
              IntStream.range(0, kernel.size())
                      .mapToDouble(i -> pixelWindow.get(i) * kernel.get(i))
                      .sum();
      if (pixelSum < 0) {
        pixelSum = 0;
      }
      if (pixelSum > 255) {
        pixelSum = 255;
      }
      return (int) pixelSum;
    } else {
      if (Objects.equals(color, "red")) {
        for (int i = col - 2; i <= col + 2; i++) {
          for (int j = row - 2; j <= row + 2; j++) {
            if (i >= 0 && i < pixels.size() && j >= 0 && j < pixels.get(0).size()) {
              pixelWindow.add(pixels.get(i).get(j).red);
            } else {
              pixelWindow.add(0.0);
            }
          }
        }
      } else if (Objects.equals(color, "green")) {
        for (int i = col - 2; i <= col + 2; i++) {
          for (int j = row - 2; j <= row + 2; j++) {
            if (i >= 0 && i < pixels.size() && j >= 0 && j < pixels.get(0).size()) {
              pixelWindow.add(pixels.get(i).get(j).green);
            } else {
              pixelWindow.add(0.0);
            }
          }
        }
      } else {
        for (int i = col - 2; i <= col + 2; i++) {
          for (int j = row - 2; j <= row + 2; j++) {
            if (i >= 0 && i < pixels.size() && j >= 0 && j < pixels.get(0).size()) {
              pixelWindow.add(pixels.get(i).get(j).blue);
            } else {
              pixelWindow.add(0.0);
            }
          }
        }
      }
      double pixelSum =
              IntStream.range(0, kernel.size())
                      .mapToDouble(i -> pixelWindow.get(i) * kernel.get(i))
                      .sum();
      if (pixelSum < 0) {
        pixelSum = 0;
      }
      if (pixelSum > 255) {
        pixelSum = 255;
      }
      return (int) pixelSum;
    }
  }
}