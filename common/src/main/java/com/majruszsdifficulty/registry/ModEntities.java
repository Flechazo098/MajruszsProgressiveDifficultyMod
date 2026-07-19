package com.majruszsdifficulty.registry;

import cc.sighs.oelib.registry.DeferredRegister;
import cc.sighs.oelib.registry.RegisterSupplier;
import com.majruszsdifficulty.MajruszsDifficulty;
import com.majruszsdifficulty.entity.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;

public final class ModEntities {
    private static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create(Registries.ENTITY_TYPE, MajruszsDifficulty.MOD_ID);

    public static final RegisterSupplier<EntityType<Cerberus>> CERBERUS_ENTITY = REGISTER.register("cerberus", Cerberus::createEntityType);
    public static final RegisterSupplier<EntityType<Creeperling>> CREEPERLING_ENTITY = REGISTER.register("creeperling", Creeperling::createEntityType);
    public static final RegisterSupplier<EntityType<CursedArmor>> CURSED_ARMOR_ENTITY = REGISTER.register("cursed_armor", CursedArmor::createEntityType);
    public static final RegisterSupplier<EntityType<Giant>> GIANT_ENTITY = REGISTER.register("giant", Giant::createEntityType);
    public static final RegisterSupplier<EntityType<Illusioner>> ILLUSIONER_ENTITY = REGISTER.register("illusioner", Illusioner::createEntityType);
    public static final RegisterSupplier<EntityType<Tank>> TANK_ENTITY = REGISTER.register("tank", Tank::createEntityType);

    public static void register() {
        REGISTER.register();
    }

    private ModEntities() {
    }
}
