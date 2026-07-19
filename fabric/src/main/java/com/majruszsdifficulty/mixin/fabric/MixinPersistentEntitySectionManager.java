package com.majruszsdifficulty.mixin.fabric;

import cc.sighs.oelib.event.EventBus;
import com.majruszsdifficulty.events.ServerEntityJoinEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mirrors NeoForge's addEntity patch at the equivalent vanilla bytecode location: post immediately
 * before the first original add operation and return false when the event is cancelled.
 */
@Mixin(PersistentEntitySectionManager.class)
public abstract class MixinPersistentEntitySectionManager<T extends EntityAccess> {
    @Inject(
            method = "addEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/entity/PersistentEntitySectionManager;addEntityUuid(Lnet/minecraft/world/level/entity/EntityAccess;)Z"
            ),
            cancellable = true
    )
    private void majruszsdifficulty$onEntityJoin(T access, boolean loadedFromDisk,
                                                 CallbackInfoReturnable<Boolean> callback
    ) {
        if (access instanceof Entity entity && entity.level() instanceof ServerLevel
                && EventBus.post(new ServerEntityJoinEvent(entity, loadedFromDisk))) {
            callback.setReturnValue(false);
        }
    }
}
