package com.majruszsdifficulty.internal.command;

import cc.sighs.oelib.registry.extra.CommandRegister;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class CommandBuilder {
    final List<List<Command.IModification>> modifications;
    final List<ArgumentBuilder<CommandSourceStack, ?>> arguments;

    public CommandBuilder copy() {
        CommandBuilder copy = new CommandBuilder();
        this.modifications.forEach(modification -> copy.modifications.add(new ArrayList<>(modification)));
        copy.arguments.addAll(this.arguments);
        return copy;
    }

    public CommandBuilder add(Predicate<CommandSourceStack> predicate) {
        return this.add((Command.IModification) (builder -> builder.getLastArgument().requires(predicate)));
    }

    public CommandBuilder addArgument(Supplier<ArgumentBuilder<CommandSourceStack, ?>> argument) {
        return this.add((Command.IModification) (builder -> builder.addArgument(argument.get())));
    }

    public CommandBuilder literal(String... names) {
        List<Command.IModification> alternatives = new ArrayList<>();
        for (String name : names) {
            alternatives.add(builder -> builder.addArgument(Commands.literal(name)));
        }
        return this.add(alternatives);
    }

    public CommandBuilder parameter(IParameter<?> parameter) {
        return parameter.apply(this);
    }

    public CommandBuilder hasPermission(int requiredLevel) {
        return this.add((Predicate<CommandSourceStack>) (stack -> stack.hasPermission(requiredLevel)));
    }

    public CommandBuilder isPlayer() {
        return this.add(CommandSourceStack::isPlayer);
    }

    public CommandBuilder execute(Command.IExecutable executable) {
        return this.add((Command.IModification) (builder -> builder.getLastArgument().executes(context -> executable.execute(new CommandData(context)))));
    }

    public void register() {
        CommandRegister.registerServer((dispatcher, context, environment) -> this.register(dispatcher));
    }

    CommandBuilder() {
        this.modifications = new ArrayList<>();
        this.arguments = new ArrayList<>();
    }

    void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        for (List<Integer> permutation : this.generatePermutations()) {
            this.clearArguments();
            for (int index = 0; index < permutation.size(); ++index) {
                this.modifications.get(index).get(permutation.get(index)).apply(this);
            }
            this.mergeArguments();
            try {
                dispatcher.register((LiteralArgumentBuilder<CommandSourceStack>) this.getFirstArgument());
            } catch (Exception exception) {
                throw new IllegalArgumentException("First argument of any command must be a literal", exception);
            }
        }
    }

    void addArgument(ArgumentBuilder<CommandSourceStack, ?> argument) {
        this.arguments.add(argument);
    }

    void clearArguments() {
        this.arguments.clear();
    }

    boolean emptyArguments() {
        return this.arguments.isEmpty();
    }

    ArgumentBuilder<CommandSourceStack, ?> getFirstArgument() {
        if (this.emptyArguments()) {
            throw new IllegalArgumentException();
        }
        return this.arguments.get(0);
    }

    ArgumentBuilder<CommandSourceStack, ?> getLastArgument() {
        if (this.emptyArguments()) {
            throw new IllegalArgumentException();
        }
        return this.arguments.getLast();
    }

    void mergeArguments() {
        for (int index = this.arguments.size(); index >= 2; --index) {
            this.arguments.get(index - 2).then(this.arguments.get(index - 1));
        }
    }

    List<List<Integer>> generatePermutations() {
        List<List<Integer>> permutations = new ArrayList<>();
        permutations.add(new ArrayList<>());
        for (List<Command.IModification> alternatives : this.modifications) {
            List<List<Integer>> next = new ArrayList<>();
            for (int index = 0; index < alternatives.size(); ++index) {
                for (List<Integer> permutation : permutations) {
                    List<Integer> copy = new ArrayList<>(permutation);
                    copy.add(index);
                    next.add(copy);
                }
            }
            permutations = next;
        }
        return permutations;
    }

    CommandBuilder add(Command.IModification modification) {
        return this.add(List.of(modification));
    }

    CommandBuilder add(List<Command.IModification> alternatives) {
        this.modifications.add(alternatives);
        return this;
    }
}
