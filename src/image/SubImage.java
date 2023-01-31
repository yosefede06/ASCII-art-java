package image;

import java.awt.*;
import java.util.ArrayList;


/**
 * Bonus +5 points.
 * Class that implements image interface and will be used to iterate sub images instead of pixel by pixel.
 */
public class SubImage implements Image {

    private final int width;
    private final int height;
    private final Image img;
    private final int initCol;
    private final int initRow;


    /**
     * Class constructor. Inits instance of the class.
     * @param img Original image.
     * @param initRow Start row
     * @param initCol Start col.
     * @param width Image width.
     * @param height Image height.
     */
    public SubImage(Image img, int initRow, int initCol, int width, int height) {
        if(checkArgumentsInValid(img, initRow, initCol, width, height)) throw new IllegalArgumentException();
        this.width  = width;
        this.height = height;
        if(!(img instanceof SubImage)) {
            this.img = img;
            this.initCol = initCol;
            this.initRow = initRow;

        }
        else {
            var subImage = (SubImage) img;
            this.img = subImage.img;
            this.initCol =  subImage.initCol + initCol;
            this.initRow =  subImage.initRow + initRow;

        }
    }


    /**
     * Checks if arguments are invalid.
     * @param img Image.
     * @param img Original image.
     * @param initRow Start row
     * @param initCol Start col.
     * @param width Image width.
     * @param height Image height.
     * @return True if arguments are invalid, false otherwise.
     */
    private boolean checkArgumentsInValid(Image img, int initRow, int initCol, int width, int height) {
        return initRow + width > img.getWidth() ||
                initCol + height > img.getHeight() ||
                initRow < 0 || width <= 0 ||
                initCol < 0 || height <= 0;
    }

    /**
     * Overrides Pixel getter which will be used for our iterator.
     * @param x x input.
     * @param y y input.
     * @return Color pixel.
     */
    @Override
    public Color getPixel(int x, int y) {
        return this.img.getPixel(this.initRow + x, this.initCol + y);
    }

    /**
     * Getter for image width.
     * @return Image width.
     */
    @Override
    public int getWidth() {
        return width;
    }

    /**
     * Getter for image height.
     * @return Image height.
     */
    @Override
    public int getHeight() {
        return height;
    }
}
