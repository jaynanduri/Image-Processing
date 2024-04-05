package model;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a color transform operation that can be applied on an image.
 */
public class ColorTransform implements Command {
  private List<List<Double>> colorMat = new ArrayList<>();

  /**
   * Initialise the matrix to transform the image.
   *
   * @param mat matrix for color transform operation
   */
  public ColorTransform(List<List<Double>> mat) {
    colorMat = mat;
  }

  @Override
  public Image executeMethod(Image image, int width, int height, int maxPixel) {
    StringBuilder builder = new StringBuilder();
    builder.append(width).append(System.lineSeparator());
    builder.append(height).append(System.lineSeparator());
    builder.append(maxPixel).append(System.lineSeparator());
    for (int col = 0; col < height; col++) {
      for (int row = 0; row < width; row++) {
        ImageImpl.Pixel pix = image.getPixels().get(col).get(row);
        builder.append(convertRGB(colorMat.get(0), pix)).append(System.lineSeparator());
        builder.append(convertRGB(colorMat.get(1), pix)).append(System.lineSeparator());
        builder.append(convertRGB(colorMat.get(2), pix)).append(System.lineSeparator());
      }
    }
    return new ImageImpl.ImageBuilder().loadFile(builder);
  }

  private int convertRGB(List<Double> pixMultiplier, ImageImpl.Pixel pixel) {
    int result = (int) ((pixMultiplier.get(0) * pixel.red) + (pixMultiplier.get(1) * pixel.green)
                        + (pixMultiplier.get(2) * pixel.blue));
    if (result < 0) {
      return 0;
    } else {
      return Math.min(result, 255);
    }
  }
}
