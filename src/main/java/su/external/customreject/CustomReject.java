package su.external.customreject;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import su.external.customreject.config.Config;
import su.external.customreject.config.ServerConfig;

@Mod(CustomReject.MODID)
public class CustomReject {
    public static final String MODID = "customreject";
    public CustomReject() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        Config.init();
    }
    private void commonSetup(FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }
}
