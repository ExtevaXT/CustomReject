package su.external.customreject.config;

import net.minecraftforge.common.ForgeConfigSpec;
import su.external.customreject.CustomReject;

import java.util.function.Function;

public class ServerConfig {
    // General
    public final ForgeConfigSpec.ConfigValue<String> vanillaConnection;
    ServerConfig(ForgeConfigSpec.Builder innerBuilder) {
        Function<String, ForgeConfigSpec.Builder> builder = name -> innerBuilder.translation(CustomReject.MODID + ".config.server." + name);

        innerBuilder.push("general");
        vanillaConnection = builder.apply("vanillaConnection").comment("Reject message for vanilla connection").define("vanillaConnection", "You need Forge 1.18.2 to join this server");
    }
}
