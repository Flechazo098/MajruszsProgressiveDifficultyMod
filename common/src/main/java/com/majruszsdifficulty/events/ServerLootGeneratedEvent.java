package com.majruszsdifficulty.events;

import cc.sighs.oelib.event.Event;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public final class ServerLootGeneratedEvent implements Event {
    public final ObjectArrayList<ItemStack> generatedLoot;
    public final ResourceLocation lootId;
    public final LootContext context;
    public final ServerLevel level;
    public final @Nullable BlockState blockState;
    public final @Nullable DamageSource damageSource;
    public final @Nullable Entity killer;
    public final @Nullable Entity entity;
    public final @Nullable Player lastDamagePlayer;
    public final @Nullable ItemStack tool;
    public final @Nullable Vec3 origin;

    public ServerLootGeneratedEvent(ObjectArrayList<ItemStack> loot, ResourceLocation id, LootContext context) {
        this.generatedLoot = loot;
        this.lootId = id;
        this.context = context;
        this.level = context.getLevel();
        this.blockState = this.get(LootContextParams.BLOCK_STATE);
        this.damageSource = this.get(LootContextParams.DAMAGE_SOURCE);
        this.killer = this.get(LootContextParams.ATTACKING_ENTITY);
        this.entity = this.get(LootContextParams.THIS_ENTITY);
        this.lastDamagePlayer = this.get(LootContextParams.LAST_DAMAGE_PLAYER);
        this.tool = this.get(LootContextParams.TOOL);
        this.origin = this.get(LootContextParams.ORIGIN);
    }

    public ServerLevel getLevel() {
        return this.level;
    }

    public ServerLevel getServerLevel() {
        return this.level;
    }

    public Vec3 getPosition() {
        return this.origin != null ? this.origin : Vec3.ZERO;
    }

    private <T> T get(LootContextParam<T> parameter) {
        return this.context.getParamOrNull(parameter);
    }
}
