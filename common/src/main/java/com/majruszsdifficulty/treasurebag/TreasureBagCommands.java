package com.majruszsdifficulty.treasurebag;

import com.majruszsdifficulty.internal.command.Command;
import com.majruszsdifficulty.internal.command.CommandData;
import com.majruszsdifficulty.internal.command.IParameter;
import com.majruszsdifficulty.internal.text.TextHelper;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class TreasureBagCommands {
    private static final IParameter<List<? extends Entity>> ENTITIES = Command.entities();

    public static void register() {
        Command.create()
                .literal("treasurebag")
                .hasPermission(4)
                .literal("reset")
                .execute(TreasureBagCommands::reset)
                .parameter(ENTITIES)
                .execute(TreasureBagCommands::reset)
                .register();

        Command.create()
                .literal("treasurebag")
                .hasPermission(4)
                .literal("unlockall")
                .execute(TreasureBagCommands::unlockAll)
                .parameter(ENTITIES)
                .execute(TreasureBagCommands::unlockAll)
                .register();
    }

    private static int reset(CommandData data) throws CommandSyntaxException {
        List<? extends Entity> entities = data.getOptional(ENTITIES).orElseGet(() -> List.of(data.getCaller()));
        for (Entity entity : entities) {
            if (entity instanceof Player player) {
                TreasureBagHelper.clearProgress(player);
                data.source.sendSuccess(() -> TextHelper.translatable("commands.treasurebag.reset", player.getName()), true);
            }
        }

        return entities.isEmpty() ? -1 : 0;
    }

    private static int unlockAll(CommandData data) throws CommandSyntaxException {
        List<? extends Entity> entities = data.getOptional(ENTITIES).orElseGet(() -> List.of(data.getCaller()));
        for (Entity entity : entities) {
            if (entity instanceof Player player) {
                TreasureBagHelper.unlockAll(player);
                data.source.sendSuccess(() -> TextHelper.translatable("commands.treasurebag.unlockall", player.getName()), true);
            }
        }

        return entities.isEmpty() ? -1 : 0;
    }
}
