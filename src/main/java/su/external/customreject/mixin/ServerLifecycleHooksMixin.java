package su.external.customreject.mixin;

import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraftforge.network.ConnectionType;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import su.external.customreject.config.Config;

@Mixin(ServerLifecycleHooks.class)
public class ServerLifecycleHooksMixin {
    @Shadow
    private static void rejectConnection(final Connection manager, ConnectionType type, String message) {}

    @Inject(method = "handleServerLogin", at = @At("HEAD"), cancellable = true, remap = false)
    private static void vanilla(ClientIntentionPacket packet, Connection manager, CallbackInfoReturnable<Boolean> cir){
        if (packet.getIntention() == ConnectionProtocol.LOGIN) {
            final ConnectionType connectionType = ConnectionType.forVersionFlag(packet.getFMLVersion());
            if (connectionType == ConnectionType.VANILLA && !NetworkRegistry.acceptsVanillaClientConnections()) {
                rejectConnection(manager, connectionType, Config.SERVER.vanillaConnection.get());
                cir.setReturnValue(false);
            }
        }
    }
}

