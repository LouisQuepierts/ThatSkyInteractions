package net.quepierts.thatskyinteractions.client.shader;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.preprocessor.GlslPreprocessor;
import com.mojang.blaze3d.shaders.Program;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.extern.log4j.Log4j2;
import net.minecraft.FileUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Log4j2
public class MultiBranchProgram extends Program {
    private static final int MAX_LOG_LENGTH = 32768;
    private final Type type;
    private final String name;

    private final int[] ids;

    protected MultiBranchProgram(Type type, int[] ids, String name) {
        super(type, ids[0], name);
        this.type = type;
        this.ids = ids;
        this.name = name;
    }

    @Override
    public void close() {

        int[] ints = this.ids;
        for (int i = 0; i < ints.length; i++) {
            int id = ints[i];
            if (id != -1) {
                RenderSystem.assertOnRenderThread();
                GlStateManager.glDeleteShader(id);
                ints[i] = -1;
            }
        }


        this.type.getPrograms().remove(this.name);
    }

    public static MultiBranchProgram getOrCreate(final ResourceProvider resourceProvider, Program.Type programType, String name) throws IOException {
        Program program = programType.getPrograms().get(name);

        if (program instanceof MultiBranchProgram multiBranchProgram) {
            return multiBranchProgram;
        }

        ResourceLocation loc = ResourceLocation.parse(name);
        String var10000 = loc.getPath();
        String s = "shaders/core/" + var10000 + programType.getExtension();
        ResourceLocation resourcelocation = ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), s);
        Resource resource = resourceProvider.getResourceOrThrow(resourcelocation);

        try (InputStream inputstream = resource.open()) {
            final String s1 = FileUtil.getFullResourcePath(s);


        } catch (Throwable var13) {
            throw var13;
        }

        return null;
    }

    private static void compile(Type type, String name, InputStream shaderData, String sourceName) throws IOException {
        RenderSystem.assertOnRenderThread();

        String source = IOUtils.toString(shaderData, StandardCharsets.UTF_8);

        // int i = compileShaderInternal(type, name, shaderData, sourceName, preprocessor);
        Program program = new MultiBranchProgram(type, new int[]{0}, name);
        type.getPrograms().put(name, program);
    }
}
