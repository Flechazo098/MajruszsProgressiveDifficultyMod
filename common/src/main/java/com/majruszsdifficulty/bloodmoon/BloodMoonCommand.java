package com.majruszsdifficulty.bloodmoon;

import com.majruszsdifficulty.internal.command.Command;
import com.majruszsdifficulty.internal.command.CommandData;
import com.majruszsdifficulty.internal.text.TextHelper;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class BloodMoonCommand {
    public static void register() {
        Command.create()
                .literal("bloodmoon")
                .hasPermission(4)
                .literal("start")
                .execute(BloodMoonCommand::start)
                .register();

        Command.create()
                .literal("bloodmoon")
                .hasPermission(4)
                .literal("stop")
                .execute(BloodMoonCommand::stop)
                .register();
    }

    private static int start(CommandData data) throws CommandSyntaxException {
        if (BloodMoonHelper.start()) {
            return 0;
        }

        data.source.sendFailure(TextHelper.translatable("commands.blood_moon.cannot_start"));
        return -1;
    }

    private static int stop(CommandData data) throws CommandSyntaxException {
        if (BloodMoonHelper.stop()) {
            return 0;
        }

        data.source.sendFailure(TextHelper.translatable("commands.blood_moon.not_started"));
        return -1;
    }
}
