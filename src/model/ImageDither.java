package model;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents an Image Dither Command that converts the given image into a B/W pixelated
 * image used in newspapers. This is done by adding error to the pixel values.
 */
public class ImageDither implements Command {
  @Override
  public Image executeMethod(Image image, int width, int height, int maxPixel) {
    image = image.greyScale("luma-component");
    List<List<ImageImpl.Pixel>> pixelMap = new ArrayList<>();
    pixelMap.addAll(image.getPixels());
    for (int col = 0; col < height; col++) {
      for (int row = 0; row < width; row++) {
        ImageImpl.Pixel pixel = pixelMap.get(col).get(row);
        double oldColor = pixel.red;
        double newColor = oldColor < 128 ? 0 : maxPixel;
        double error = oldColor - newColor;
        if (row + 1 < width) {
          pixelMap.get(col).set(row + 1,
                  setNeighbourPixels(row + 1, col, error * (0.4375), pixelMap));
        }
        if (row > 0 && col + 1 < height) {
          pixelMap.get(col + 1).set(row - 1,
                  setNeighbourPixels(row - 1, col + 1, error * (0.1875), pixelMap));
        }
        if (col + 1 < height) {
          pixelMap.get(col + 1).set(row,
                  setNeighbourPixels(row, col + 1, error * (0.3125), pixelMap));
        }
        if (row + 1 < width && col + 1 < height) {
          pixelMap.get(col + 1).set(row + 1,
                  setNeighbourPixels(row + 1, col + 1, error * (0.0625), pixelMap));
        }
        pixel = new ImageImpl.Pixel(newColor, newColor, newColor);
        pixelMap.get(col).set(row, pixel);
      }
    }
    return new ImageImplExt(width, height, maxPixel, pixelMap);
  }

  private ImageImpl.Pixel setNeighbourPixels(int row, int col, double error,
                                             List<List<ImageImpl.Pixel>> pixelMap) {
    ImageImpl.Pixel pixel = pixelMap.get(col).get(row);
    double newColor = pixel.red + error;
    if (newColor < 0) {
      newColor = 0;
    } else {
      newColor = Math.min(newColor, 255);
    }
    List<ImageImpl.Pixel> rowList = new ArrayList<>();
    rowList.addAll(pixelMap.get(col));
    pixel = new ImageImpl.Pixel(newColor, newColor, newColor);
    return pixel;
  }
}
