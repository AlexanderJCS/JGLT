package jglt.coords;

import jglt.Consts;

/**
 * ScreenCoords is the coordinate system that LWJGL uses to render to the screen. All other
 * coordinate units (PixelCoords, GridCoords) are abstractions of this.
 */
public class ScreenCoords extends Coords {
    public ScreenCoords(float x, float y) {
        super(x, y);
    }

    public static float distXtoPixelCoords(float dist) {
        return dist * Consts.SCREEN_WIDTH / 2;
    }

    public static float distYtoPixelCoords(float dist) {
        return dist * Consts.SCREEN_HEIGHT / 2;
    }

    public PixelCoords toPixelCoords() {
        float pixCoordsX = (Consts.SCREEN_WIDTH) * (this.x + 1) / 2;
        float pixCoordsY = (Consts.SCREEN_HEIGHT) * (this.y + 1) / 2;

        return new PixelCoords(pixCoordsX, pixCoordsY);
    }
}
