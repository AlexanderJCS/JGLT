package jangl.graphics.textures;

import jangl.graphics.Bindable;
import jangl.graphics.shaders.AttribLocation;
import jangl.graphics.shaders.ShaderProgram;
import jangl.graphics.shaders.premade.TextureShaderFrag;
import jangl.graphics.shaders.premade.TextureShaderVert;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import static org.lwjgl.opengl.GL41.*;

/**
 * This class allows images to be drawn to the screen.
 * To use it, run Texture.bind() then render the TexturedModel you want to map it to.
 */
public class Texture implements AutoCloseable, Bindable {
    public final int width;
    public final int height;
    private final int id;
    private final ShaderProgram shaderProgram;
    private boolean useDefaultShader = true;

    public Texture(TextureBuilder builder) throws IllegalStateException {
        if (builder.getImageData() == null) {
            throw new IllegalStateException("The TextureBuilder does not have any image data");
        }

        this.width = builder.getWidth();
        this.height = builder.getHeight();

        this.shaderProgram = createShader();
        this.shaderProgram.getVertexShader().setObeyCamera(builder.isObeyingCamera());

        this.id = this.createImage(builder.getImageData(), this.width, this.height);
        this.setFilterMode(builder.getFilterMode());

        this.bind();
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, builder.getWrapMode());
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, builder.getWrapMode());
        this.unbind();
    }

    private static ShaderProgram createShader() {
        return new ShaderProgram(new TextureShaderVert(), new TextureShaderFrag(), TextureShaderVert.getAttribLocations());
    }

    /**
     * @return the ID of the created image
     */
    private int createImage(ByteBuffer imageData, int width, int height) {
        int imageID = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, imageID);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, imageData);
        glGenerateMipmap(GL_TEXTURE_2D);

        this.unbind();

        return imageID;
    }

    public void setFilterMode(int filterMode) {
        if (filterMode == GL_LINEAR) {
            filterMode = GL_LINEAR_MIPMAP_NEAREST;
        } else if (filterMode == GL_NEAREST) {
            filterMode = GL_NEAREST_MIPMAP_NEAREST;
        }

        this.bind();
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, filterMode);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, filterMode);
        this.unbind();
    }

    /**
     * When scaling, the texture is optimized for scaling higher-resolution images, but may make low-resolution images
     * such as pixel art blurry. On a higher resolution texture, it will make the image look smoother and more seamless.
     * <br>
     * Under the hood, this method changes the filter mode to GL_LINEAR.
     */
    public void setSmoothScaling() {
        this.setFilterMode(GL_LINEAR);
    }

    /**
     * When scaling, the texture is optimized for scaling pixelated images, such as pixel art. It will not make the
     * image blurry when scaling, but may make higher-resolution images look rough.
     * <br>
     * Under the hood, this method changes the filter mode to GL_NEAREST.
     */
    public void setPixelatedScaling() {
        this.setFilterMode(GL_NEAREST);
    }

    /**
     * Run this method before running TexturedModel.draw(). This will overlay the texture
     * on the TexturedModel.
     */
    @Override
    public void bind() {
        if (this.useDefaultShader) {
            this.shaderProgram.bind();
        }

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, this.id);
    }

    /**
     * Unbinds any existing bound texture
     */
    @Override
    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);

        if (this.useDefaultShader) {
            this.shaderProgram.unbind();
        }
    }

    /**
     * This method allows you to configure if the shader program attached to the texture (default shader) is used when
     * the texture is bound/unbound. Only set this to false if you plan on using your own custom shader in place of
     * the default.
     *
     * @param useDefaultShader True to use the default shader. False to use a custom shader.
     */
    public void useDefaultShader(boolean useDefaultShader) {
        this.useDefaultShader = useDefaultShader;
    }

    /**
     * @return the ShaderProgram that the Texture uses by default.
     */
    public ShaderProgram getShaderProgram() {
        return this.shaderProgram;
    }

    @Override
    public void close() {
        glDeleteTextures(this.id);
        this.shaderProgram.close();
    }
}
