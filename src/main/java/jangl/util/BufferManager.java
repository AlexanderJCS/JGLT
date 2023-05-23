package jangl.util;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Keeps four buffers for the entire program. This is important since if buffers just keep being created,
 * there will be a memory leak.
 */
public class BufferManager {
    public static final DoubleBuffer MOUSE_BUFFER_1 = BufferUtils.createDoubleBuffer(1);
    public static final DoubleBuffer MOUSE_BUFFER_2 = BufferUtils.createDoubleBuffer(1);
    /**
     * Max float buffer capacity
     */
    private static final int MAX_FB_CAPACITY = 4096;
    public static final FloatBuffer VBO_BUFFER = BufferUtils.createFloatBuffer(MAX_FB_CAPACITY);
    public static final FloatBuffer TEX_COORDS_BUFFER = BufferUtils.createFloatBuffer(MAX_FB_CAPACITY);
    /**
     * Max byte buffer capacity
     */
    private static final int MAX_BB_CAPACITY = 32000000;
    // This buffer needs to be larger since it's containing image data
    public static final ByteBuffer BYTE_BUFFER = BufferUtils.createByteBuffer(MAX_BB_CAPACITY);
    /**
     * Max int buffer capacity
     */
    private static final int MAX_IB_CAPACITY = 4096;
    public static final IntBuffer INDICES_BUFFER = BufferUtils.createIntBuffer(MAX_IB_CAPACITY);

    public static void setFloatBuffer(FloatBuffer fb, float[] newData) throws IllegalArgumentException {
        if (newData.length > MAX_FB_CAPACITY) {
            throw new IllegalArgumentException(
                    "newData is greater than length " + MAX_FB_CAPACITY +
                            ", which will cause a buffer overflow");
        }

        fb.clear();
        fb.limit(newData.length);
        fb.put(newData);
        fb.flip();
    }

    public static void setIntBuffer(IntBuffer ib, int[] newData) {
        if (newData.length > MAX_IB_CAPACITY) {
            throw new IllegalArgumentException(
                    "newData is greater than length " + MAX_IB_CAPACITY +
                            ", which will cause a buffer overflow");
        }

        ib.clear();
        ib.limit(newData.length);
        ib.put(newData);
        ib.flip();
    }
}