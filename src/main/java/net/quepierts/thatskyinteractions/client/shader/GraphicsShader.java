package net.quepierts.thatskyinteractions.client.shader;

import com.mojang.blaze3d.shaders.Program;
import com.mojang.blaze3d.shaders.Shader;

public class GraphicsShader implements Shader, AutoCloseable {
    @Override
    public int getId() {
        return 0;
    }

    @Override
    public void markDirty() {

    }

    @Override
    public Program getVertexProgram() {
        return null;
    }

    @Override
    public Program getFragmentProgram() {
        return null;
    }

    @Override
    public void attachToProgram() {

    }

    @Override
    public void close() throws Exception {

    }
}
