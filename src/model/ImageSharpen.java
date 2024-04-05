package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This clas represents Image sharpening operation to be performed on the image. This operation
 * will have 5x5 kernel to be applied on the image object.
 */
public class ImageSharpen implements Command {
  private final List<List<Double>> sharpMat = new ArrayList<>();

  /**
   * Initialise the kernel for the applying the operation on the image.
   */
  public ImageSharpen() {
    sharpMat.add(new ArrayList<>(Arrays.asList(-1.0 / 8, -1.0 / 8, -1.0 / 8, -1.0 / 8, -1.0 / 8)));
    sharpMat.add(new ArrayList<>(Arrays.asList(-1.0 / 8, 1.0 / 4, 1.0 / 4, 1.0 / 4, -1.0 / 8)));
    sharpMat.add(new ArrayList<>(Arrays.asList(-1.0 / 8, 1.0 / 4, 1.0, 1.0 / 4, -1.0 / 8)));
    sharpMat.add(new ArrayList<>(Arrays.asList(-1.0 / 8, 1.0 / 4, 1.0 / 4, 1.0 / 4, -1.0 / 8)));
    sharpMat.add(new ArrayList<>(Arrays.asList(-1.0 / 8, -1.0 / 8, -1.0 / 8, -1.0 / 8, -1.0 / 8)));
  }

  @Override
  public Image executeMethod(Image image, int width, int height, int maxPixel) {
    StringBuilder builder = new StringBuilder();
    builder.append(width).append(System.lineSeparator());
    builder.append(height).append(System.lineSeparator());
    builder.append(maxPixel).append(System.lineSeparator());
    for (int col = 0; col < height; col++) {
      for (int row = 0; row < width; row++) {
        Map<String, Integer> sharpPixels = applyKernel(image.getPixels(),
                sharpMat.stream().flatMap(List::stream).collect(Collectors.toList()), row, col);
        builder.append(sharpPixels.get("red")).append(System.lineSeparator());
        builder.append(sharpPixels.get("green")).append(System.lineSeparator());
        builder.append(sharpPixels.get("blue")).append(System.lineSeparator());
      }
    }
    return new ImageImpl.ImageBuilder().loadFile(builder);
  }

  private Map<String, Integer> applyKernel(List<List<ImageImpl.Pixel>> pixels,
                                           List<Double> kernel, int row, int col) {
    Map<String, Integer> sharpPixels = new HashMap<>();
    sharpPixels.put("red", vectorMultiply(pixels, kernel, row, col, "red"));
    sharpPixels.put("green", vectorMultiply(pixels, kernel, row, col, "green"));
    sharpPixels.put("blue", vectorMultiply(pixels, kernel, row, col, "blue"));
    return sharpPixels;
  }

  private int vectorMultiply(List<List<ImageImpl.Pixel>> pixels, List<Double> kernel,
                             int row, int col, String color) {
    List<Double> pixelWindow = new ArrayList<>();
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
