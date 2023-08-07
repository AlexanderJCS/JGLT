package texturedemo;

import jangl.JANGL;
import jangl.coords.WorldCoords;
import jangl.graphics.textures.Texture;
import jangl.graphics.textures.TextureBuilder;
import jangl.io.Window;
import jangl.shapes.Circle;
import jangl.shapes.Rect;
import jangl.shapes.Triangle;

public class TextureDemo implements AutoCloseable {
    private final Triangle rect1;
    private final Rect rect2;
    private final Texture texture;

    public TextureDemo() {
        this.rect1 = new Triangle(new WorldCoords(0.5f, 0.5f), new WorldCoords(0, 0), new WorldCoords(1, 0));
        this.rect2 = new Rect(new WorldCoords(0.7f, 0.5f), 0.5f, 0.5f);

        this.rect1.setTexRepeatX(2);
        this.rect1.setTexRepeatY(2);

        this.texture = new Texture(
                new TextureBuilder().setImagePath("src/demo/demoResources/textureDemo/image.png")
        );
    }

    public void draw() {
        Window.clear();

        this.rect1.draw(this.texture);
        this.rect2.draw(this.texture);
    }

    public void run() {
        while (Window.shouldRun()) {
            this.draw();

            JANGL.update();
        }
    }

    @Override
    public void close() {
        this.rect1.close();
        this.rect2.close();
        this.texture.close();
    }

    public static void main(String[] args) {
        JANGL.init(1600, 900);

        TextureDemo textureDemo = new TextureDemo();
        textureDemo.run();
        textureDemo.close();

        Window.close();
    }
}
