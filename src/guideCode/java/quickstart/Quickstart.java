package quickstart;

import jangl.JANGL;
import jangl.coords.PixelCoords;
import jangl.coords.ScreenCoords;
import jangl.graphics.shaders.ColorShader;
import jangl.io.Window;
import jangl.shapes.Rect;

public class Quickstart {
    public Quickstart() {
        // Input the width and height of your screen in pixels.
        JANGL.init(1600, 900);
    }

    public void run() {
        try (
                Rect rect = new Rect(
                    new ScreenCoords(0, 0),
                    PixelCoords.distXtoScreenDist(400),
                    PixelCoords.distYtoScreenDist(400)
                );

                ColorShader yellow = new ColorShader(1, 1, 0, 1)
        ) {
            while (Window.shouldRun()) {
                JANGL.update();
                Window.clear();

                rect.draw(yellow);
            }
        }

        Window.close();
    }

    public static void main(String[] args) {
        new Quickstart().run();
    }
}