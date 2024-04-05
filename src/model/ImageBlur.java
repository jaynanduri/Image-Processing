package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * This class represents an Image Blur Command that applies gaussian blur operation on the image.
 * Image blur will have 3x3 kernel to be applied on the image.
 */
public class ImageBlur implements Command {
  private final List<Double> blurMat = new ArrayList<>();

  /**
   * Initialize the values of the kernel.
   */
  public ImageBlur() {
    blurMat.add(0.0625);
    blurMat.add(0.125);
    blurMat.add(0.0625);
    blurMat.add(0.125);
    blurMat.add(0.25);
    blurMat.add(0.125);
    blurMat.add(0.0625);
    blurMat.add(0.125);
    blurMat.add(0.0625);
  }

  @Override
  public Image executeMethod(Image image, int width, int height, int maxPixel) {
    StringBuilder builder = new StringBuilder();
    builder.append(width).append(System.lineSeparator());
    builder.append(height).append(System.lineSeparator());
    builder.append(maxPixel).append(System.lineSeparator());
    for (int col = 0; col < height; col++) {
      for (int row = 0; row < width; row++) {
        Map<String, Integer> blurPixels = applyKernel(image.getPixels(), blurMat, row, col);
        builder.append(blurPixels.get("red")).append(System.lineSeparator());
        builder.append(blurPixels.get("green")).append(System.lineSeparator());
        builder.append(blurPixels.get("blue")).append(System.lineSeparator());
      }
    }
    return new ImageImpl.ImageBuilder().loadFile(builder);
  }

  private Map<String, Integer> applyKernel(List<List<ImageImpl.Pixel>> pixels,
                                           List<Double> kernel, int row, int col) {
    Map<String, Integer> blurPixels = new HashMap<>();
    blurPixels.put("red", vectorMultiply(pixels, kernel, row, col, "red"));
    blurPixels.put("green", vectorMultiply(pixels, kernel, row, col, "green"));
    blurPixels.put("blue", vectorMultiply(pixels, kernel, row, col, "blue"));
    return blurPixels;
  }

  private int vectorMultiply(List<List<ImageImpl.Pixel>> pixels, List<Double> kernel,
                             int row, int col, String color) {
    List<Double> pixelWindow = new ArrayList<>();
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
  }
}
