# CS 5010: Assignment 6
## Image Editing Application
*** 
This application follows a Model View Controller design pattern. The image acts as the Model in our design, and its interface provides some public methods to manipulate it. In the previous assignment, Pixel was a separate class, and its attributes needed public-getters to access individual channels. In the current implementation, Pixel is a static inner class, thus eliminating the need for public-getters and eliminating the possibility of unnecessary data access to the controller. We have implemented a new class in the Model to act as an image cache keeping track of image objects against the names provided in the user input. This cache gets cleared once the controller restarts. With this change, we have eliminated the need for the controller to store image data; instead, it will only need an image object pertinent to a particular operation. We have implemented a command design pattern to accommodate new image transformation operations. This implementation required a new model image extending the previous version.

The controller in our application prompts the user to enter a valid command to transform the image. Once the entered command is valid, the controller passes control to the Model to perform the required operation. The controller handles the loading and saving of images from the disk, loads the content into a string and passes it to the Model. It is the role of the Model to interpret the contents of the string to build a valid Image object. Similarly, the Model builds a string and passes it to the controller to store the image on the disk. With this change, we can read images of different file extensions and store images in different file extensions.

In this latest iteration of the image editing application, we have developed a GUI for interactive real-time manipulation of images. The View uses general command callbacks, where the action listeners of each event are present in the View itself. However, each action listener's functionality exists in a new interface Feature implemented by a GUIController. This GUIController takes an object of the existing implementation of the text-based controller to perform the actions on the image.

***
#### For now, we are expecting .ppm extension images.
```text
Expected inputs from user is as follows:
1. load file-path object-name(call the image with this name)
2. brighten value src-object-name dest-object-name
3. vertical-flip src-object-name dest-object-name
4. horizontal-flip src-object-name dest-object-name
5. greyscale value/red/green/blue/intensity/luma-component src-object-name dest-object-name
6. save file-path object-name
7. rgb-split src-object-name object-name-red object-name-green object-name-blue
8. rgb-combine dest-object-name src-name-red src-name-green src-name-blue
9. image-blur src-obj-name dest-obj-name
10. image-sharpen src-obj-name dest-obj-name
11. grey-scaled src-obj-name dest-obj-name
12. sepia src-obj-name dest-obj-name
13. dither src-obj-name dest-obj-name
14. run file-path
```
#### Run the application using the following command
```commandline
cd res/
# for running commands from a file
java -jar assignment6.jar -file script.txt

# for interactive command line execution
java -jar assignment6.jar -text script.txt

# for GUI image editing
java -jar assignment6.jar 
```
***
[License]()
Copyright © 2023 [Jayantha nanduri & Dheeraj Jonnalagadda Anjani]
Image provided in example is my own photograph, and I'm authorizing its use in the project.
Released under the [MIT License]().

