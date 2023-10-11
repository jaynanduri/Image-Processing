# Instructions to execute operations on image
```text
1. load: Load an image from the specified path and refer it to henceforth in the program by the given image name.
2. save: Save the image with the given name to the specified path which should include the name of the file.
3. greyscale: Create a greyscale image with a component of the image with the given name, and refer to it henceforth in the program by the given destination name. Similar commands for green, blue, value, luma, intensity components should be supported.
4. horizontal-flip: Flip an image horizontally to create a new image, referred to henceforth by the given destination name.
5. vertical-flip: Flip an image vertically to create a new image, referred to henceforth by the given destination name.
6. brighten: brighten the image by the given increment to create a new image, referred to henceforth by the given destination name. The increment may be positive (brightening) or negative (darkening).
7. rgb-split: split the given image into three greyscale images containing its red, green and blue components respectively.
8. rgb-combine: Combine the three greyscale images into a single image that gets its red, green and blue components from the three images respectively.
9. run: Load and run the script commands in the specified file.
10. image-blur: Apply gaussian blur operation on the src-object and call it dest-obj name.
11. image-sharpen: Apply sharpen operation on the src-object and call it dest-obj name.
12. grey-scaled: Apply greyscale transformation operation on the src-object and call it dest-obj name.
13. sepia: Apply sepia transformation operation on the src-object and call it dest-obj name.
14. dither: Apply dither operation on the src-object and call it dest-obj name.
```
## Sample commands
```text
# must start with load operation, otherwise there will be no object to apply operations
# load operation from file JD.ppm and call it jay
load JD.ppm jay

# apply image blur on jay and call it jay-blur
image-blur jay jay-blur

# save jay-blur to JD-blur.png
save JD-blur.png jay-blur

# apply image sharpen on jay and call it jay-blur
image-sharpen jay jay-sharp

# apply greyscale transformation on jay and call it jay-blur
grey-scaled jay jay-grey

# apply sepia on jay and call it jay-blur
sepia jay jay-sepia

# apply dither on jay and call it jay-blur
dither jay jay-dither

#brighten koala by adding 10  
brighten 10 koala koala-brighter 

#flip koala vertically
vertical-flip koala koala-vertical

#flip the vertically flipped koala horizontally
horizontal-flip koala-vertical koala-vertical-horizontal

#create a greyscale using only the value component, as an image koala-greyscale
greyscale value-component koala koala-greyscale

#save koala-brighter
save images/koala-brighter.ppm koala-brighter

#save koala-greyscale
save images/koala-gs.ppm koala-greyscale

#overwrite koala with another file
load images/upper.ppm koala

#give the koala a red tint
rgb-split koala koala-red koala-green koala-blue

#brighten just the red image
brighten 50 koala-red koala-red

#combine them back, but by using the brightened red we get a red tint
rgb-combine koala-red-tint koala-red koala-green koala-blue
save images/koala-red-tint.ppm koala-red-tint

# run commands from a scrip file
run script.txt
```
# Steps for running GUI Application
```text
There are different buttons in the image editing application.
1. Perform load to display the image.
2. Perform any operation on this image and the displayed image will be transformed accordingly.
3. Hit save to save the image displayed on the screen to the specified path on disk.
4. Perform load again to get a new image in the window.
5. To perform RGB-combine and RGB-split operations, the user will be prompted to enter 3 paths 
for red, green and blue greyscale images.
```