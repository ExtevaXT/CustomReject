package su.external.customreject;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
public class ForgeEventHandler {
    public static void init() {
        final IEventBus bus = MinecraftForge.EVENT_BUS;
        bus.addListener(ForgeEventHandler::onPlayerLoggedIn);
    }
    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayer player = (ServerPlayer)event.getPlayer();
        if (player.isDeadOrDying()) {
            player.setBoundingBox(new AABB(0, 0, 0, 0, 0, 0));
            player.setPos(0, 0, 0);
            player.save(player.getPersistentData());
            CustomReject.LOGGER.info("Fixed player data");
        }
    }
}

