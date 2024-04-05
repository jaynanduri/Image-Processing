package model;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * This class represents the implementation of an Image (model). An Image will have width, height,
 * max value allowed for a pixel and a matrix of pixel(red, green, blue) objects.
 */
public class ImageImpl implements Image {
  protected final int maxPixel;
  protected final int height;
  protected final int width;
  protected final List<List<Pixel>> imagePixels;

  /**
   * Initialise the member variables of the Image model.
   *
   * @param width    width of image
   * @param height   height of image
   * @param maxPixel max allowed val for a pixel
   * @param pixels   a matrix of pixel objects
   */
  protected ImageImpl(int width, int height, int maxPixel, List<List<Pixel>> pixels) {
    this.width = width;
    this.height = height;
    this.maxPixel = maxPixel;
    this.imagePixels = pixels;
  }

  public List<List<Pixel>> getPixels() {
    return this.imagePixels;
  }

  private StringBuilder hashmapHelper() {
    StringBuilder builder = new StringBuilder();
    builder.append(this.width);
    builder.append(this.height);
    builder.append(this.maxPixel);
    for (int col = 0; col < this.height; col++) {
      List<Pixel> rowList = new ArrayList<>();
      rowList = this.imagePixels.get(col);
      for (int row = 0; row < this.width; row++) {
        Pixel pixels = rowList.get(row);
        builder.append(pixels.red);
        builder.append(pixels.green);
        builder.append(pixels.blue);
      }
    }
    return builder;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ImageImpl)) {
      return false;
    }
    ImageImpl img = (ImageImpl) obj;
    return this.height == img.height && this.width == img.width && this.maxPixel == img.maxPixel &&
           this.imagePixels.equals(img.imagePixels);
  }

  @Override
  public int hashCode() {
    return this.hashmapHelper().toString().hashCode();
  }

  private double capPixels(double channel, int maxValue) {
    if (channel > maxValue) {
      channel = maxValue;
    }
    if (channel < 0) {
      channel = 0;
    }
    return channel;
  }

  @Override
  public Image brighten(int quantity) {
    List<List<Pixel>> pixelMap = this.imagePixels;
    for (int col = 0; col < this.height; col++) {
      List<Pixel> rowList;
      rowList = pixelMap.get(col);
      for (int row = 0; row < this.width; row++) {
        Pixel pixel = rowList.get(row);
        double red = capPixels(pixel.red + quantity, this.maxPixel);
        double blue = capPixels(pixel.blue + quantity, this.maxPixel);
        double green = capPixels(pixel.green + quantity, this.maxPixel);
        pixel = new Pixel(red, green, blue);
        rowList.set(row, pixel);
      }
      pixelMap.set(col, rowList);
    }
    return this;
  }

  @Override
  public Image horizontalFlip() {
    List<List<Pixel>> pixelMap = this.imagePixels;
    for (int col = 0; col < this.height; col++) {
      List<Pixel> rowList = pixelMap.get(col);
      Collections.reverse(rowList);
      pixelMap.set(col, rowList);
    }
    return this;
  }

  @Override
  public Image verticalFlip() {
    Collections.reverse(this.imagePixels);
    return this;
  }

  private double greyScaleHelper(Pixel pixel, String component) {
    switch (component) {
      case "red": {
        return pixel.red;
      }
      case "green": {
        return pixel.green;
      }
      case "blue": {
        return pixel.blue;
      }
      case "value": {
        return pixel.val;
      }
      case "intensity": {
        return pixel.intensity;
      }
      case "luma": {
        return pixel.luma;
      }
      default: {
        throw new IllegalArgumentException("pass valid args\n valid args are: red-component, " +
                                           "green-component, blue-component, " +
                                           "intensity-component, value-component, luma-component");
      }
    }
  }

  @Override
  public Image greyScale(String component) {
    List<List<Pixel>> pixelMap = new ArrayList<>();
    pixelMap.addAll(this.imagePixels);
    for (int col = 0; col < this.height; col++) {
      List<Pixel> rowList = new ArrayList<>();
      rowList.addAll(pixelMap.get(col));
      for (int row = 0; row < this.width; row++) {
        Pixel pixels = rowList.get(row);
        double resultantPixel = this.greyScaleHelper(pixels, component.split("-")[0]);
        pixels = new Pixel(resultantPixel, resultantPixel, resultantPixel);
        rowList.set(row, pixels);
      }
      pixelMap.set(col, rowList);
    }
    return new ImageImplExt(this.width, this.height, this.maxPixel, pixelMap);
  }

  @Override
  public Map<String, Image> rgbSplit() {
    Map<String, Image> resultantChannels = new HashMap<>();
    resultantChannels.put("red", this.greyScale("red"));
    resultantChannels.put("green", this.greyScale("green"));
    resultantChannels.put("blue", this.greyScale("blue"));
    return resultantChannels;
  }

  @Override
  public Image rgbCombine(Image greenComp, Image blueComp) throws NullPointerException {
    List<List<Pixel>> pixelMap = new ArrayList<>();
    for (int col = 0; col < this.height; col++) {
      List<Pixel> rowList = new ArrayList<>();
      for (int row = 0; row < this.width; row++) {
        Map<String, Double> channelVal = this.rgbCombineHelper(this.imagePixels,
                greenComp.getPixels(), blueComp.getPixels(), col, row);
        Pixel pixels = new Pixel(channelVal.get("red"), channelVal.get("green"),
                channelVal.get("blue"));
        rowList.add(pixels);
      }
      pixelMap.add(rowList);
    }
    return new ImageImplExt(this.width, this.height, this.maxPixel, pixelMap);
  }

  @Override
  public StringBuilder encodeImage() {
    StringBuilder builder = new StringBuilder();
    builder.append(this.width).append(System.lineSeparator());
    builder.append(this.height).append(System.lineSeparator());
    builder.append(this.maxPixel).append(System.lineSeparator());
    for (int col = 0; col < this.height; col++) {
      List<Pixel> rowList = this.imagePixels.get(col);
      for (int row = 0; row < this.width; row++) {
        Pixel pixel = rowList.get(row);
        builder.append((int) pixel.red).append(System.lineSeparator());
        builder.append((int) pixel.green).append(System.lineSeparator());
        builder.append((int) pixel.blue).append(System.lineSeparator());
      }
    }
    return builder;
  }

  @Override
  public BufferedImage getBufferedImage() {
    StringBuilder builder = this.encodeImage();
    Scanner sc = new Scanner(builder.toString());
    int width = sc.nextInt();
    int height = sc.nextInt();
    int maxVal = sc.nextInt();
    BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        int r = sc.nextInt();
        int g = sc.nextInt();
        int b = sc.nextInt();
        int col = (r << 16) | (g << 8) | b;
        bufferedImage.setRGB(j, i, col);
      }
    }
    return bufferedImage;
  }

  private boolean sameDimensions(List<List<Pixel>> redMap,
                                 List<List<Pixel>> greenMap,
                                 List<List<Pixel>> blueMap) {
    return (redMap.size() == greenMap.size())
           && (greenMap.size() == blueMap.size())
           && (redMap.get(0).size() == greenMap.get(0).size())
           && (greenMap.get(0).size() == blueMap.get(0).size());
  }

  private boolean isGreyScale(List<List<Pixel>> map, int row, int col) {
    return (map.get(col).get(row).red == map.get(col).get(row).green) &&
           (map.get(col).get(row).green == map.get(col).get(row).blue);
  }

  private Map<String, Double> rgbCombineHelper(List<List<Pixel>> redMap,
                                               List<List<Pixel>> greenMap,
                                               List<List<Pixel>> blueMap, int col, int row) {
    Map<String, Double> channelVal = new HashMap<>();
    if (sameDimensions(redMap, greenMap, blueMap)
        && isGreyScale(redMap, row, col)
        && isGreyScale(greenMap, row, col)
        && isGreyScale(blueMap, row, col)) {
      channelVal.put("red", redMap.get(col).get(row).red);
      channelVal.put("green", greenMap.get(col).get(row).green);
      channelVal.put("blue", blueMap.get(col).get(row).blue);
    } else {
      throw new IllegalArgumentException("Pass grey-scale images only");
    }
    return channelVal;
  }

  /**
   * This static inner class represents a pixel in an image. A pixel will have 3 channels - red,
   * green and blue.
   */
  public static class Pixel {
    protected double red;
    protected double green;
    protected double blue;
    protected double val;
    protected double intensity;
    protected double luma;

    /**
     * Initialise the pixel object by passing values to the channel.
     *
     * @param r red channel of pixel
     * @param g green channel of pixel
     * @param b blue channel of pixel
     */
    protected Pixel(double r, double g, double b) {
      this.red = r;
      this.green = g;
      this.blue = b;
      this.val = Math.max(r, Math.max(g, b));
      this.intensity = (r + g + b) / 3.0;
      this.luma = (0.2126 * r) + (0.7152 * g) + (0.0722 * b);
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (!(obj instanceof Pixel)) {
        return false;
      }
      Pixel p = (Pixel) obj;
      return this.red == p.red && this.blue == p.blue && this.green == p.green;
    }

    @Override
    public int hashCode() {
      String s = "" + this.red + this.green + this.blue;
      return s.hashCode();
    }
  }

  /**
   * This inner class represents an image builder that constructs an image by parsing the content
   * of StringBuilder object.
   */
  public static class ImageBuilder {
    /**
     * Assign values to individual pixel and construct image from it.
     *
     * @param builder a StringBuilder object containing contents of an image read from a file.
     * @return a new Image object
     */
    public Image loadFile(StringBuilder builder) {
      if (builder != null) {
        Scanner sc = new Scanner(builder.toString());
        int width = sc.nextInt();
        int height = sc.nextInt();
        int maxVal = sc.nextInt();
        List<List<Pixel>> pixels = new ArrayList<>();
        for (int i = 0; i < height; i++) {
          List<Pixel> rowList = new ArrayList<>();
          for (int j = 0; j < width; j++) {
            int r = sc.nextInt();
            int g = sc.nextInt();
            int b = sc.nextInt();
            rowList.add(new Pixel(r, g, b));
          }
          pixels.add(rowList);
        }
        return new ImageImplExt(width, height, maxVal, pixels);
      } else {
        throw new IllegalArgumentException("Invalid Image passed in input.");
      }
    }
  }
}
