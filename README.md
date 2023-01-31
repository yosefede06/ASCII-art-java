# ASCII-art-java
> Software that converts image files to ASCII art

<img align="center" alt="HTML"  src="https://github.com/yosefede06/ASCII-art-java/blob/main/ascii-example.png" />


## Overview

The main idea is to convert an image file into ASCII art. This involves reading the image file, processing the image data and converting each pixel into an ASCII character based on its color intensity. The resulting ASCII image can be either printed to the console or saved as an HTML file.

## Technical Aspects

Image Representation: To represent an image, we need to read the image file and store its data in a 2-dimensional array. Each cell in the array represents a pixel in the image and holds the color information in the form of three basic colors - Red (R), Green (G), and Blue (B). These colors are determined by the intensity of illumination of the respective color, where the intensity scale is from 0-255. The image data can be represented as a Color object, with fields for R, G, and B.

ASCII Art Conversion: To convert the image into ASCII art, we need to map the color intensity of each pixel to an ASCII character. This can be done by dividing the intensity into a certain number of levels and assigning an ASCII character to each level. For example, a character with higher intensity can be mapped to a full block character (█), while a character with lower intensity can be mapped to a less dense character (.).

User Interface: A user interface can be built to provide an option for the user to select the image file and choose the desired output format. The output can either be printed to the console or saved as an HTML file, which will allow for the ASCII art to be viewed in a browser.


## Files description

1. BrightnessImgCharMatcher: Class that takes care of determining the brightness of ASCII
characters for a specific image.

2. Shell: Class that will perform all the interface operations and will be operated by the Driver class,
which means Driver main will be used in order to run the software.

3. SubImage: Class that implements image interface and serves as an iterator. It will be used to iterate
sub images instead of doing it pixel by pixel.

## Design

3 main decisions have been made for the implementation of the program:

1. BrightnessImgCharMatcher: The database used to save brightnesses was a simple array. I've decided to pick this
decision since it was cleaner to make all kind of changes s.t normalize, stretching... on the array. The way
I then mapped the brightness values to each respective character is by indexing the array (we never change the
order, therefore if char "a" was added at the first place with a brightness of 0.5 , it will stay the same when
we chose which brightness has the closest brightness value).

2. SubImage: In order to handle iteration on through subimages instead of pixels, I've build a new class called
SubImage which implements Image interface. It will be called by Image default method imageIntoSubImages which
allows to iterate through each subImage. This seems to be the most proper and cleaner solution, since is the job
of the subimage to handle jumps for mapping subimage purposes. FileImage will implement this method since it
implements the Image class, and as said before is default method.

3. Shell: In order to store the characters which will be used to build the ASCII image I've used a HashSet. This
database was the most intuitive from 2 reasons: Order was not important and we just wanted to have one
single unit of each character, without repetitions. Therefore, there is no need to check if a value already
appears in the hashSet when we add or remove a character.

## Time complexity

Worst time complexity: O(img.height * img.width * numCharsInRow)

Explanation: O(img.height * img.width) To build the brightnessImage 2D array we will need to iterate over
the original image (Even if we are building a smaller image we have to compute the average of the subimage
section and that requires to iterate through every pixel).
Setting the brightness of the ASCII characters requires O(charSet.length) steps, since all we are doing is
computing 0(1) operations on the brightnesses of each character.
The reason we get O(img.height * img.width * numCharsInRow) is at the moment we get to last step, when
we convert each subimage to a character with its corresponding brightness. Which we have to iterate through
O(img.height * img.width) assuming in worst case that subimage has a resolution of 1, and then iterate
through brightnesses characters O(numCharsInRow) to map on image the character which maps the closest
brightness to the one we performed for each pixel of brightnessImage array.

## Space complexity

Worst space complexity: O(img.height * img.width)

Explanation:  O(img.height * img.width) is the worst space complexity when subimages resolution equals
which we have to save a new space for brightnessImage that have the same size than the original.
O(numCharsInRow) is the space we've used for charBrightnessArray to keep track of brightnesses for each
character. Notice that, numCharsInRow <= img.width which means that in worst case we get:
O(img.height * img.width) + O(img.width) = O(img.height * img.width).

