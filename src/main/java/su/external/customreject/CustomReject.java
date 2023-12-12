package su.external.customreject;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import su.external.customreject.config.Config;

@Mod(CustomReject.MODID)
public class CustomReject {
    public static final String MODID = "customreject";
    public static final Logger LOGGER = LogUtils.getLogger();
    public CustomReject() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
        Config.init();
        ForgeEventHandler.init();
    }
    private void commonSetup(FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }
}
