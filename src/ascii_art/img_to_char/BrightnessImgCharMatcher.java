package ascii_art.img_to_char;

import image.Image;
import java.awt.*;

/**
 * Class that takes care of determining the brightness of ASCII characters for a specific image.
 */
public class BrightnessImgCharMatcher {
    private static final int RESOLUTION = 16;
    private static final double MIN_BRIGHTNESS = 0;
    private static final double MAX_BRIGHTNESS = 1;
    private static final double MAXIMAL_RGB = 255;
    private static final double RGB_TO_GRAYSCALE_RED = 0.2126;
    private static final double RGB_TO_GRAYSCALE_GREEN = 0.7152;
    private static final double RGB_TO_GRAYSCALE_BLUE = 0.0722;

    /**
     * Original image.
     */
    private final Image img;
    /**
     * Font style.
     */
    private final String font;

    /**
     * Class constructor. Inits instance of the class.
     * @param img Original image.
     * @param font Font style.
     */
    public BrightnessImgCharMatcher(Image img, String font) {
        this.img = img;
        this.font = font;
    }

    /**
     * Converts the image to a binary Image of true or false representing black and white pixels respectively and
     * returns the number of true / white pixels on the binary image.
     *
     * @param c ASCII char.
     * @param font font.
     * @return Number of white / true pixels.
     */
    private int whitePixelsForChar(char c, String font) {
        int whiteCounter = 0;
        boolean[][] binaryImage = CharRenderer.getImg(c, BrightnessImgCharMatcher.RESOLUTION, font);
        for(int i = 0; i < binaryImage.length; i++) {
            for(int j = 0; j < binaryImage[0].length; j++) {
                if(binaryImage[i][j]) {
                    whiteCounter++;
                }
            }
        }
        return whiteCounter;
    }

    /**
     * Normalizes the images upon the range [0,1] to represent brightness value.
     * @param number brightness value not normalized.
     * @return brightness value normalized.
     */
    private double normalize(int number) {
        return number / Math.pow(RESOLUTION, 2);
    }

    /**
     * Performs the linear stretching on the character.
     * @param charBrightness Current character brightness.
     * @param minBrightness Min image brightness.
     * @param maxBrightness Max image brightness.
     * @return Stretched brightness.
     */
    private double linearStretching(double charBrightness, double minBrightness, double maxBrightness) {
        return (charBrightness - minBrightness) / (maxBrightness - minBrightness);
    }

    /**
     * Calculates the maximum brightness of the ASCII image, which represents the character with the highest brightness
     * @param charArrBrightness Array representing the brightness of all characters of the ASCII image.
     * @return maximum brightness
     */
    private double maxBrightness(double[] charArrBrightness) {
        double maxCharBrightness = MIN_BRIGHTNESS;
        for(double charBrightness : charArrBrightness) {
            if(charBrightness >= maxCharBrightness) {
                maxCharBrightness = charBrightness;
            }
        }
        return maxCharBrightness;
    }

    /**
     * Calculates the minimum brightness of the ASCII image, which represents the character with the lowest brightness
     * @param charArrBrightness Array representing the brightness of each character of the ASCII image.
     * @return minimum brightness.
     */
    private double minBrightness(double[] charArrBrightness) {
        double minCharBrightness = MAX_BRIGHTNESS;
        for(double charBrightness : charArrBrightness) {
            if(charBrightness <= minCharBrightness) {
                minCharBrightness = charBrightness;
            }
        }
        return minCharBrightness;
    }

    /**
     * Method that performs the conversion of the image into characters.
     * @param numCharsInRow Number of characters will we draw in each row of the ASCII image.
     * @param charSet The set of characters with which we would like to draw our picture
     * (we will use only those that are suitable and not necessarily all).
     * @return ASCII image.
     */
    public char[][] chooseChars(int numCharsInRow, Character[] charSet) {
        int rows = (img.getHeight() / img.getWidth()) * numCharsInRow ;
        double[][] brightnessImage = new double[rows][numCharsInRow];
        imageToBrightness(img.getWidth() / numCharsInRow, brightnessImage);
        double[] charBrightnessArray = new double[charSet.length];
        // Computes brightness, normalize it.
        for(int i = 0; i < charSet.length; i++) {
            charBrightnessArray[i] = normalize(whitePixelsForChar(charSet[i], font));
        }
        // Computes maximum and minimum brightness of the whole image.
        double minBrightness = minBrightness(charBrightnessArray);
        double maxBrightness = maxBrightness(charBrightnessArray);
        // Linear stretch brightness of each char.
        for(int i = 0; i < charSet.length; i++) {
            charBrightnessArray[i] = linearStretching(charBrightnessArray[i], minBrightness, maxBrightness);
        }
        return matchBrightnessWithChar(brightnessImage, charSet, charBrightnessArray);
    }

    /**
     * Computes the resulting ASCII image of characters by matching the intensities of the original
     * image with a character in charSet which intensity character is the closest.
     * @param brightnessImage Image of brightnesses.
     * @param charSet Array of all possible characters that can appear on our final image.
     * @param charBrightnessArray Array which represents the mapping of the brightnesses for each of the characters.
     * @return char[][] Array which is the ASCII image.
     */
    private char[][] matchBrightnessWithChar(double[][] brightnessImage,
                                             Character[] charSet,
                                             double[] charBrightnessArray) {
        char[][] finalBrightnessImage = new char[brightnessImage.length][brightnessImage[0].length];
        for(int i = 0; i < brightnessImage.length; i++) {
            for(int j = 0; j < brightnessImage[0].length; j++) {
                // Set the initial current char and difference brightness values.
                char currBrightnessChar = charSet[0];
                double currDifferenceBrightness = brightnessDistance(brightnessImage[i][j], charBrightnessArray[0]);
                for(int k = 0; k < charSet.length; k++) {
                    double tempDifferenceBrightness = brightnessDistance(brightnessImage[i][j], charBrightnessArray[k]);
                    if(tempDifferenceBrightness < currDifferenceBrightness) {
                        currDifferenceBrightness = tempDifferenceBrightness;
                        currBrightnessChar = charSet[k];
                    }
                }
                // Plugin final character for each sub image section.
                finalBrightnessImage[i][j] = currBrightnessChar;
            }
        }
        return finalBrightnessImage;
    }

    /**
     * Calculates the difference value between two brightnesses.
     * @param brightnessValue1 Brightness value.
     * @param brightnessValue2 Another brightness value.
     * @return Difference brightness value.
     */
    private double brightnessDistance(double brightnessValue1, double brightnessValue2) {
        return Math.abs(brightnessValue1 - brightnessValue2);
    }

    /**
     * Performs the conversion of each subImage to a brightness value.
     * @param pixels Total number of pixels of each subImage.
     * @param brightnessImage 2D dimensional array representing the brightness resulted image.
     */
    private void imageToBrightness(int pixels, double[][] brightnessImage) {
        int count = 0;
        int cols = brightnessImage[0].length;
        for(Image subImage: img.imageIntoSubImages(pixels)) {
            brightnessImage[count / cols][count % cols] = getAverageBrightness(subImage);
            count++;
        }
    }

    /**
     * Method that performs the sub image brightness by converting the RGB pixels to grayscale, and then calculating
     * the average respect all the pixels.
     * @param img Sub image.
     * @return Brightness value.
     */
    private double getAverageBrightness(Image img) {
        int totalPixels = 0;
        double grayPixel = 0;
        for(Color pixel: img.pixels()) {
            double redSignal = pixel.getRed() * RGB_TO_GRAYSCALE_RED;
            double greenSignal = pixel.getGreen() * RGB_TO_GRAYSCALE_GREEN;
            double blueSignal = pixel.getBlue() * RGB_TO_GRAYSCALE_BLUE;
            grayPixel += redSignal + greenSignal + blueSignal;
            totalPixels++;
        }
        return (grayPixel / totalPixels) / MAXIMAL_RGB;
    }
}
