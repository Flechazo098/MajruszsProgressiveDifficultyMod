package com.majruszsdifficulty;

import cc.sighs.oelib.config.ui.screen.ConfigScreen;
import cc.sighs.oelib.event.EventAutoRegistration;
import cc.sighs.oelib.event.EventBus;
import cc.sighs.oelib.network.api.NetworkManager;
import com.majruszsdifficulty.events.*;
import com.majruszsdifficulty.features.SpawnBlocker;
import com.majruszsdifficulty.platform.NeoForgeEntityDataPlatform;
import com.majruszsdifficulty.platform.ProgressiveLootModifier;
import com.majruszsdifficulty.registration.ClientRegistration;
import com.majruszsdifficulty.registration.CommonRegistration;
import com.majruszsdifficulty.registry.ModItems;
import com.majruszsdifficulty.registry.ModPotions;
import com.mojang.serialization.MapCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityTeleportEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

@Mod(MajruszsDifficulty.MOD_ID)
public class Initializer {
    private static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MajruszsDifficulty.MOD_ID);

    static {
        LOOT_MODIFIERS.register("progressive", () -> ProgressiveLootModifier.CODEC);
    }

    public Initializer(IEventBus bus, ModContainer container) {
        LOOT_MODIFIERS.register(bus);
        NetworkManager.registerPacketScanPackage("com.majruszsdifficulty.network");
        NeoForgeEntityDataPlatform.ATTACHMENTS.register(bus);
        CommonRegistration.register();
        EventAutoRegistration.registerBasePackage("com.majruszsdifficulty");
        EventAutoRegistration.registerAllListeners();
        NeoForge.EVENT_BUS.addListener(Initializer::registerBrewingRecipes);
        NeoForge.EVENT_BUS.addListener(Initializer::afterEntityTick);
        NeoForge.EVENT_BUS.addListener(Initializer::onLivingDeath);
        NeoForge.EVENT_BUS.addListener(Initializer::onMobSpawnPositionCheck);
        NeoForge.EVENT_BUS.addListener(Initializer::onPlayerLoggedIn);
        NeoForge.EVENT_BUS.addListener(Initializer::onServerStarted);
        NeoForge.EVENT_BUS.addListener(Initializer::onIncomingDamage);
        NeoForge.EVENT_BUS.addListener(Initializer::onPlayerChangedDimension);
        NeoForge.EVENT_BUS.addListener(Initializer::onLivingDamaged);
        NeoForge.EVENT_BUS.addListener(Initializer::onEffectApplicable);
        NeoForge.EVENT_BUS.addListener(Initializer::onEquipmentChanged);
        NeoForge.EVENT_BUS.addListener(Initializer::onItemUseFinished);
        NeoForge.EVENT_BUS.addListener(Initializer::onChorusFruitTeleport);
        NeoForge.EVENT_BUS.addListener(Initializer::onEntityJoinLevel);
        NeoForge.EVENT_BUS.addListener(Initializer::onExplosionStart);
        NeoForge.EVENT_BUS.addListener(Initializer::onExplosionDetonate);
        NeoForge.EVENT_BUS.addListener(Initializer::onExperienceChange);
        NeoForge.EVENT_BUS.addListener(Initializer::onItemFished);
        NeoForge.EVENT_BUS.addListener(Initializer::onEndermanAnger);
        NeoForge.EVENT_BUS.addListener(Initializer::onBreakSpeed);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            container.registerExtensionPoint(
                    IConfigScreenFactory.class,
                    (current, parent) -> new ConfigScreen(parent, MajruszsDifficulty.MOD_ID)
            );
            ClientRegistration.register();
            NeoForge.EVENT_BUS.addListener(ClientInitializer::onItemTooltip);
            NeoForge.EVENT_BUS.addListener(ClientInitializer::onScreenMousePressed);
            NeoForge.EVENT_BUS.addListener(ClientInitializer::onRenderHand);
            bus.addListener(ClientInitializer::setup);
            bus.addListener(ClientInitializer::registerParticles);
            bus.addListener(ClientInitializer::registerReloadListeners);
            bus.addListener(ClientInitializer::registerItemColors);
            bus.addListener(ClientInitializer::registerGuiLayers);
        }
    }

    private static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        PlayerBreakSpeedEvent common = new PlayerBreakSpeedEvent(event.getEntity(), event.getState(), event.getPosition().orElse(null), event.getOriginalSpeed());
        EventBus.post(common);
        event.setNewSpeed(common.speed);
    }

    private static void onEndermanAnger(EnderManAngerEvent event) {
        event.setCanceled(EventBus.post(new ServerEndermanAngerEvent(event.getEntity(), event.getPlayer())));
    }

    private static void onExperienceChange(PlayerXpEvent.XpChange event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ServerPlayerExperienceChangeEvent common = new ServerPlayerExperienceChangeEvent(player, event.getAmount());
            event.setCanceled(EventBus.post(common));
            event.setAmount(common.amount);
        }
    }

    private static void onItemFished(ItemFishedEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            EventBus.post(new ServerItemFishedEvent(player, event.getHookEntity(), player.getMainHandItem(), event.getDrops()));
        }
    }

    private static void onExplosionStart(ExplosionEvent.Start event) {
        if (event.getLevel() instanceof ServerLevel level) {
            event.setCanceled(EventBus.post(new ServerExplosionStartEvent(level, event.getExplosion())));
        }
    }

    private static void onExplosionDetonate(ExplosionEvent.Detonate event) {
        if (event.getLevel() instanceof ServerLevel level) {
            EventBus.post(new ServerExplosionDetonateEvent(
                    level, event.getExplosion(), event.getAffectedBlocks(), event.getAffectedEntities()
            ));
        }
    }

    private static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel() instanceof ServerLevel) {
            event.setCanceled(EventBus.post(new ServerEntityJoinEvent(event.getEntity(), event.loadedFromDisk())));
        }
    }

    private static void onMobSpawnPositionCheck(MobSpawnEvent.PositionCheck event) {
        if (event.getSpawnType() == MobSpawnType.NATURAL && SpawnBlocker.isForbidden(event.getEntity())) {
            event.setResult(MobSpawnEvent.PositionCheck.Result.FAIL);
        }
    }

    private static void onChorusFruitTeleport(EntityTeleportEvent.ChorusFruit event) {
        event.setCanceled(EventBus.post(new ServerChorusFruitTeleportEvent(event.getEntityLiving())));
    }

    private static void onItemUseFinished(LivingEntityUseItemEvent.Finish event) {
        if (event.getEntity().level() instanceof ServerLevel) {
            EventBus.post(new ServerItemUseFinishedEvent(event.getEntity(), event.getItem()));
        }
    }

    private static void onEquipmentChanged(LivingEquipmentChangeEvent event) {
        EventBus.post(new ServerEquipmentChangedEvent(
                event.getEntity(), event.getSlot(), event.getFrom(), event.getTo()
        ));
    }

    private static void onEffectApplicable(MobEffectEvent.Applicable event) {
        if (event.getEntity().level() instanceof ServerLevel
                && EventBus.post(new ServerMobEffectApplicableEvent(event.getEffectInstance(), event.getEntity()))) {
            event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
        }
    }

    private static void onLivingDamaged(LivingDamageEvent.Post event) {
        if (event.getEntity().level() instanceof ServerLevel) {
            EventBus.post(new ServerLivingEntityDamagedEvent(
                    event.getSource(), event.getEntity(), event.getNewDamage()
            ));
        }
    }

    private static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player
                && player.getServer() != null
                && player.getServer().getLevel(event.getTo()) instanceof ServerLevel current) {
            EventBus.post(new ServerPlayerChangedDimensionEvent(player, current));
        }
    }

    private static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity().level() instanceof ServerLevel)) {
            return;
        }
        ServerLivingEntityIncomingDamageEvent commonEvent = new ServerLivingEntityIncomingDamageEvent(
                event.getSource(), event.getEntity(), event.getAmount()
        );
        EventBus.post(commonEvent);
        event.setAmount(commonEvent.damage);
        event.setCanceled(commonEvent.isDamageCancelled());
        if (commonEvent.attacker instanceof Player player) {
            if (commonEvent.spawnCriticalParticles) {
                player.crit(commonEvent.target);
            }
            if (commonEvent.spawnMagicParticles) {
                player.magicCrit(commonEvent.target);
            }
        }
    }

    private static void onServerStarted(net.neoforged.neoforge.event.server.ServerStartedEvent event) {
        EventBus.post(new ServerStartedEvent(event.getServer()));
    }

    private static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            EventBus.post(new ServerPlayerJoinedEvent(player));
        }
    }

    private static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity().level() instanceof ServerLevel) {
            event.setCanceled(EventBus.post(
                    new ServerLivingEntityDeathEvent(event.getSource(), event.getEntity())
            ));
        }
    }

    private static void afterEntityTick(EntityTickEvent.Post event) {
        if (event.getEntity() instanceof LivingEntity livingEntity && livingEntity.level() instanceof ServerLevel) {
            EventBus.post(new ServerLivingEntityTickEvent(livingEntity));
        }
    }

    private static void registerBrewingRecipes(RegisterBrewingRecipesEvent event) {
        var builder = event.getBuilder();
        builder.addMix(Potions.WATER, ModItems.CERBERUS_FANG_ITEM.get(), Potions.MUNDANE);
        builder.addMix(Potions.AWKWARD, ModItems.CERBERUS_FANG_ITEM.get(), MajruszsDifficulty.potionHolder(ModPotions.WITHER_POTION));
        builder.addMix(MajruszsDifficulty.potionHolder(ModPotions.WITHER_POTION), Items.REDSTONE, MajruszsDifficulty.potionHolder(ModPotions.WITHER_LONG_POTION));
        builder.addMix(MajruszsDifficulty.potionHolder(ModPotions.WITHER_POTION), Items.GLOWSTONE_DUST, MajruszsDifficulty.potionHolder(ModPotions.WITHER_STRONG_POTION));
    }

}
