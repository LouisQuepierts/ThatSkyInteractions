package net.quepierts.thatskyinteractions;

import com.mojang.logging.LogUtils;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.quepierts.thatskyinteractions.proxy.ClientProxy;
import net.quepierts.thatskyinteractions.proxy.CommonProxy;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(ThatSkyInteractions.MODID)
public class ThatSkyInteractions {
    public static final String MODID = "thatskyinteractions";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final RandomSource RANDOM = RandomSource.create(42L);

    private static ThatSkyInteractions instance;

    private final CommonProxy proxy;


    public ThatSkyInteractions(IEventBus modBus, ModContainer modContainer) {
        instance = this;
        this.proxy = FMLEnvironment.dist.isClient() ? new ClientProxy(modBus, modContainer) : new CommonProxy(modBus, modContainer);
        //modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);

    }

    public static ThatSkyInteractions getInstance() {
        return instance;
    }

    public static ResourceLocation getLocation(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    public static ModelResourceLocation getModelLocation(String path) {
        return new ModelResourceLocation(getLocation(path), "vbo");
    }

    public static ModelResourceLocation getStandaloneModel(String path) {
        return ModelResourceLocation.standalone(getLocation(path));
    }

    public static ModelResourceLocation getModelLocation(String path, String variant) {
        return new ModelResourceLocation(getLocation(path), variant);
    }

    public CommonProxy getProxy() {
        return this.proxy;
    }

    @OnlyIn(Dist.CLIENT)
    public ClientProxy getClient() {
        return (ClientProxy) this.proxy;
    }

    public static String getInteractionTranslateKey(ResourceLocation interaction) {
        return "interaction." + interaction.getNamespace() + "." + interaction.getPath().replace('/', '.');
    }
}
