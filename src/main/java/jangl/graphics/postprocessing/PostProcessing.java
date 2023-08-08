package jangl.graphics.postprocessing;

import jangl.graphics.shaders.ShaderProgram;
import jangl.graphics.shaders.VertexShader;
import jangl.graphics.shaders.premade.TextureShaderFrag;
import jangl.graphics.shaders.premade.TextureShaderVert;

import java.util.ArrayList;
import java.util.List;

public class PostProcessing implements AutoCloseable {
    private final List<PipelineItem> pipeline;

    public PostProcessing() {
        this.pipeline = new ArrayList<>();

        VertexShader vertShader = new TextureShaderVert();
        vertShader.setObeyCamera(false);

        this.addToPipeline(
                new PipelineItem(
                        new ShaderProgram(vertShader, new TextureShaderFrag())
                )
        );
    }

    /**
     * Adds a new pipeline item to the end of the pipeline.
     * @param item The pipeline item to add.
     */
    public void addToPipeline(PipelineItem item) {
        this.pipeline.add(item);
    }

    /**
     * @return A shallow copy of the pipeline. You can (and are encouraged to) modify this if needed. Before deleting a
     *         PipelineItem, remember to close it and its ShaderProgram before de-referencing it.
     */
    public List<PipelineItem> getPipeline() {
        return this.pipeline.subList(1, this.pipeline.size());
    }

    /**
     * Run this before rendering the first frame of the pipeline. This should be done automatically within JANGL.update()
     */
    public void start() {
        this.pipeline.get(0).bind();
    }

    /**
     * Run this at the end of the frame. This should be done automatically within JANGL.update().
     */
    public void end() {
        for (int i = 1; i < this.pipeline.size(); i++) {
            PipelineItem item = this.pipeline.get(i);
            item.bind();

            // I have no idea why it needs to be i - 1 but when I say item.draw() it just does not work in the
            // slightest. So just don't modify this line
            this.pipeline.get(i - 1).draw();
        }

        this.pipeline.get(0).unbind();  // it doesn't matter which one to call to unbind
        this.pipeline.get(this.pipeline.size() - 1).draw();
    }

    /**
     * Closes all resources generated by the PostProcessing class. Note that this does not close any pipeline items
     * added via addToPipeline().
     */
    @Override
    public void close() {
        // Close the default pipeline item
        this.pipeline.get(0).getShaderProgram().close();
        this.pipeline.get(0).close();
    }
}
