package image;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;

/**
 * A package-private class of the package image.
 * @author Dan Nirel
 */
class ImageIterableProperty<T> implements Iterable<T> {
    private final Image img;
    private final BiFunction<Integer, Integer, T> propertySupplier;
    private final int pixelJumpX;
    private final int pixelJumpY;

    public ImageIterableProperty(
            Image img,
            BiFunction<Integer, Integer, T> propertySupplier, int pixelJumpX, int pixelJumpY) {
        this.img = img;
        this.propertySupplier = propertySupplier;
        this.pixelJumpX = pixelJumpX;
        this.pixelJumpY = pixelJumpY;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            int x = 0, y = 0;

            @Override
            public boolean hasNext() {
                return y < img.getHeight();
            }

            @Override
            public T next() {
                if (!hasNext())
                    throw new NoSuchElementException();
                var next = propertySupplier.apply(x, y);
                x += pixelJumpX;
                if (x >= img.getWidth()) {
                    x = 0;
                    y += pixelJumpY;
                }
                return next;
            }
        };
    }
}
