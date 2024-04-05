package model;

/**
 * This class represents an extension of Image model to support additional functionality apart from
 * the basic commands in Image interface.
 */
public interface ImageExt extends Image {
  /**
   * Takes the command object and runs it on itself to create a new transformed image.
   *
   * @param command a Command object
   * @return transformed image
   */
  Image runCommand(Command command);
}
