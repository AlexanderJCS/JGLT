package jangl.graphics;

import jangl.color.Color;
import jangl.util.ArrayUtils;
import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL46.*;

import java.awt.image.BufferedImage;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;

/**
 * Allows you to set individual pixel values of the texture.
 */
public class MutableTexture extends Texture {
    private final ByteBuffer pixelBuffer;

    public MutableTexture(Color color, int width, int height, int filterMode) {
        super(
                ArrayUtils.repeatSequence(ArrayUtils.intsToBytes(color.get255RGBA()), width * height),
                width, height, filterMode
        );

        this.pixelBuffer = BufferUtils.createByteBuffer(16);
    }

    @Deprecated
    public MutableTexture(float red, float green, float blue, float alpha, int width, int height, int filterMode) {
        super(
                ArrayUtils.repeatSequence(new byte[]{(byte) (red * 255), (byte) (green * 255), (byte) (blue * 255), (byte) (alpha * 255)},
                        width * height),
                width, height, filterMode
        );

        this.pixelBuffer = BufferUtils.createByteBuffer(16);
    }

    @Deprecated
    public MutableTexture(float red, float green, float blue, float alpha, int width, int height) {
        this(red, green, blue, alpha, width, height, GL_NEAREST);
    }

    public MutableTexture(String filepath, int filterMode) throws UncheckedIOException {
        super(filepath, filterMode);
        this.pixelBuffer = BufferUtils.createByteBuffer(16);
    }

    public MutableTexture(String filepath) throws UncheckedIOException {
        super(filepath);
        this.pixelBuffer = BufferUtils.createByteBuffer(16);
    }

    public MutableTexture(String filepath, int x, int y, int width, int height, int filterMode) throws IndexOutOfBoundsException, UncheckedIOException {
        super(filepath, x, y, width, height, filterMode);
        this.pixelBuffer = BufferUtils.createByteBuffer(16);
    }

    public MutableTexture(String filepath, int x, int y, int width, int height) throws IndexOutOfBoundsException, UncheckedIOException {
        super(filepath, x, y, width, height);
        this.pixelBuffer = BufferUtils.createByteBuffer(16);
    }

    public MutableTexture(BufferedImage bufferedImage, int x, int y, int width, int height, int filterMode) throws IndexOutOfBoundsException, UncheckedIOException {
        super(bufferedImage, x, y, width, height, filterMode);
        this.pixelBuffer = BufferUtils.createByteBuffer(16);
    }

    @Deprecated
    public void setPixelAt(int x, int y, float[] rgba) throws IllegalArgumentException {
        if (rgba.length != 4) {
            throw new IllegalArgumentException("RGBA array length != 4");
        }

        this.setPixelAt(x, y, rgba[0], rgba[1], rgba[2], rgba[3]);
    }

    @Deprecated
    public void setPixelAt(int x, int y, float red, float green, float blue, float alpha) {
        this.pixelBuffer.put((byte) (red * 255));
        this.pixelBuffer.put((byte) (green * 255));
        this.pixelBuffer.put((byte) (blue * 255));
        this.pixelBuffer.put((byte) (alpha * 255));

        this.pixelBuffer.flip();

        this.bind();

        glTexSubImage2D(
                GL_TEXTURE_2D,
                0, x, y,
                1, 1,
                GL_RGBA, GL_UNSIGNED_BYTE,
                this.pixelBuffer
        );

        Texture.unbind();
    }

    public void setPixelAt(int x, int y, Color color) {
        this.pixelBuffer.put(ArrayUtils.intsToBytes(color.get255RGBA()));
        this.pixelBuffer.flip();

        this.bind();
        glTexSubImage2D(
                GL_TEXTURE_2D,
                0, x, y,
                1, 1,
                GL_RGBA, GL_UNSIGNED_BYTE,
                this.pixelBuffer
        );

        Texture.unbind();
    }

    @Deprecated
    public void fillImage(float red, float green, float blue, float alpha) {
        ByteBuffer imageBuffer = BufferUtils.createByteBuffer(this.width * this.height * 4);

        for (int i = 0; i < this.width * this.height; i++) {
            imageBuffer.put((byte) (red * 255));
            imageBuffer.put((byte) (green * 255));
            imageBuffer.put((byte) (blue * 255));
            imageBuffer.put((byte) (alpha * 255));
        }

        imageBuffer.flip();
        this.bind();

        glTexSubImage2D(
                GL_TEXTURE_2D,
                0, 0, 0,
                this.width, this.height,
                GL_RGBA, GL_UNSIGNED_BYTE,
                imageBuffer
        );

        Texture.unbind();
    }

    public void fillImage(Color color) {
        ByteBuffer imageBuffer = BufferUtils.createByteBuffer(this.width * this.height * 4);

        for (int i = 0; i < this.width * this.height; i++) {
            imageBuffer.put(ArrayUtils.intsToBytes(color.get255RGBA()));
        }

        imageBuffer.flip();
        this.bind();

        glTexSubImage2D(
                GL_TEXTURE_2D,
                0, 0, 0,
                this.width, this.height,
                GL_RGBA, GL_UNSIGNED_BYTE,
                imageBuffer
        );

        Texture.unbind();
    }
}