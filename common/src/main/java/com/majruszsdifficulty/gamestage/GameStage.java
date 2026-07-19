package com.majruszsdifficulty.gamestage;

import com.majruszsdifficulty.internal.text.RegexString;
import com.majruszsdifficulty.internal.text.TextHelper;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;

public class GameStage {
    private static final Codec<ChatFormatting> FORMATTING_CODEC = Codec.STRING.xmap(ChatFormatting::valueOf, ChatFormatting::name);
    public static final Codec<GameStage> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("id").forGetter(stage -> stage.id),
            FORMATTING_CODEC.listOf().fieldOf("format").orElse(List.of()).forGetter(stage -> stage.format),
            Trigger.CODEC.fieldOf("triggers").orElse(new Trigger()).forGetter(stage -> stage.trigger),
            Message.CODEC.listOf().fieldOf("messages").orElse(List.of()).forGetter(stage -> stage.messages)
    ).apply(instance, (id, format, trigger, messages) -> {
        GameStage stage = new GameStage();
        stage.id = id;
        stage.format = new ArrayList<>(format);
        stage.trigger = trigger;
        stage.messages = new ArrayList<>(messages);
        return stage;
    }));
    public static final String NORMAL_ID = "normal";
    public static final String EXPERT_ID = "expert";
    public static final String MASTER_ID = "master";
    private String id = "";
    private List<ChatFormatting> format = new ArrayList<>();
    private Trigger trigger = new Trigger();
    private List<Message> messages = new ArrayList<>();
    int ordinal = 0;

    public static Builder named(String name) {
        return new Builder(name);
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof GameStage gameStage
                && this.id.equals(gameStage.id);
    }

    public boolean checkDimension(String dimensionId) {
        return this.trigger.dimensions.stream().anyMatch(string -> string.matches(dimensionId));
    }

    public boolean checkEntity(String entityId) {
        return this.trigger.entities.stream().anyMatch(string -> string.matches(entityId));
    }

    public boolean is(String name) {
        return this.id.equals(name);
    }

    public String getId() {
        return this.id;
    }

    public int getOrdinal() {
        return this.ordinal;
    }

    public MutableComponent getComponent() {
        return TextHelper.translatable("majruszsdifficulty.stages.%s".formatted(this.id.toLowerCase()))
                .withStyle(this.format.toArray(ChatFormatting[]::new));
    }

    public List<MutableComponent> getMessages() {
        return this.messages.stream()
                .map(message -> TextHelper.translatable(message.id).withStyle(message.format.toArray(ChatFormatting[]::new)))
                .toList();
    }

    GameStage copy(GameStage gameStage) {
        this.format = gameStage.format;
        this.trigger = gameStage.trigger;
        this.messages = gameStage.messages;

        return this;
    }

    public static class Builder {
        private final GameStage gameStage;

        public Builder(String name) {
            this.gameStage = new GameStage();
            this.gameStage.id = name;
        }

        public Builder format(ChatFormatting... format) {
            this.gameStage.format = List.of(format);

            return this;
        }

        public Builder triggersIn(String dimensionId) {
            this.gameStage.trigger.dimensions.add(new RegexString(dimensionId));

            return this;
        }

        public Builder triggersByKilling(String entityId) {
            this.gameStage.trigger.entities.add(new RegexString(entityId));

            return this;
        }

        public Builder message(String id, ChatFormatting... format) {
            Message message = new Message();
            message.id = id;
            message.format = List.of(format);
            this.gameStage.messages.add(message);

            return this;
        }

        public GameStage create() {
            return this.gameStage;
        }
    }

    private static class Trigger {
        private static final Codec<Trigger> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.listOf().fieldOf("dimensions").orElse(List.of()).forGetter(trigger -> RegexString.toString(trigger.dimensions)),
                Codec.STRING.listOf().fieldOf("entities").orElse(List.of()).forGetter(trigger -> RegexString.toString(trigger.entities))
        ).apply(instance, (dimensions, entities) -> {
            Trigger trigger = new Trigger();
            trigger.dimensions = RegexString.toRegex(dimensions);
            trigger.entities = RegexString.toRegex(entities);
            return trigger;
        }));
        public List<RegexString> dimensions = new ArrayList<>();
        public List<RegexString> entities = new ArrayList<>();
    }

    private static class Message {
        private static final Codec<Message> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("id").forGetter(message -> message.id),
                FORMATTING_CODEC.listOf().fieldOf("format").orElse(List.of()).forGetter(message -> message.format)
        ).apply(instance, (id, format) -> {
            Message message = new Message();
            message.id = id;
            message.format = new ArrayList<>(format);
            return message;
        }));
        public String id;
        public List<ChatFormatting> format = new ArrayList<>();
    }
}
