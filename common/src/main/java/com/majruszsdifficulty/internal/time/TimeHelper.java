package com.majruszsdifficulty.internal.time;

import cc.sighs.oelib.event.EventPriority;
import cc.sighs.oelib.event.EventSide;
import cc.sighs.oelib.event.Subscribe;
import cc.sighs.oelib.event.events.ClientTickEvent;
import cc.sighs.oelib.event.events.ServerTickEvent;
import com.majruszsdifficulty.internal.annotation.Dist;
import com.majruszsdifficulty.internal.annotation.OnlyIn;
import com.majruszsdifficulty.internal.collection.CollectionHelper;
import com.majruszsdifficulty.internal.platform.LogicalSafe;
import com.majruszsdifficulty.internal.platform.Side;
import com.majruszsdifficulty.internal.time.delays.Delay;
import com.majruszsdifficulty.internal.time.delays.IDelayedExecution;
import com.majruszsdifficulty.internal.time.delays.Slider;
import com.majruszsdifficulty.internal.time.delays.Until;
import net.minecraft.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class TimeHelper {
    private static final int TICKS_IN_SECOND = 20;
    private static final int TICKS_IN_MINUTE = TICKS_IN_SECOND * 60;
    private static final LogicalSafe<List<IDelayedExecution>> PENDING_EXECUTIONS = LogicalSafe.of(ArrayList::new);
    private static final LogicalSafe<List<IDelayedExecution>> EXECUTIONS = LogicalSafe.of(ArrayList::new);
    private static final LogicalSafe<Long> TICKS_COUNTER = LogicalSafe.of(() -> 1L);

    @Subscribe(priority = EventPriority.HIGHEST)
    private static void onServerTick(ServerTickEvent.Post event) {
        update();
    }

    @Subscribe(priority = EventPriority.HIGHEST, side = EventSide.CLIENT)
    private static void onClientTick(ClientTickEvent.Post event) {
        update();
    }

    public static Delay delay(int ticks, Consumer<Delay> callback) {
        return TimeHelper.run(new Delay(callback, ticks));
    }

    public static Delay delay(double seconds, Consumer<Delay> callback) {
        return TimeHelper.delay(TimeHelper.toTicks(seconds), callback);
    }

    public static Delay nextTick(Consumer<Delay> callback) {
        return TimeHelper.delay(1, callback);
    }

    public static Slider slider(int ticks, Consumer<Slider> callback) {
        return TimeHelper.run(new Slider(callback, ticks));
    }

    public static Slider slider(double seconds, Consumer<Slider> callback) {
        return TimeHelper.slider(TimeHelper.toTicks(seconds), callback);
    }

    public static Until until(Predicate<Until> predicate, Consumer<Until> callback) {
        return TimeHelper.run(new Until(callback, predicate));
    }

    public static <Type extends IDelayedExecution> Type run(Type exec) {
        PENDING_EXECUTIONS.run(list -> list.add(exec));

        return exec;
    }

    public static int toTicks(double seconds) {
        return (int) (seconds * TICKS_IN_SECOND);
    }

    public static double toSeconds(int ticks) {
        return (double) (ticks) / TICKS_IN_SECOND;
    }

    public static boolean haveTicksPassed(int ticks) {
        return TICKS_COUNTER.get() % ticks == 0;
    }

    public static boolean haveSecondsPassed(double seconds) {
        return TimeHelper.haveTicksPassed(TimeHelper.toTicks(seconds));
    }

    public static long getTicks() {
        return TICKS_COUNTER.get();
    }

    @OnlyIn(Dist.CLIENT)
    public static float getClientTime() {
        return Util.getMillis() / 1000.0f;
    }

    @OnlyIn(Dist.CLIENT)
    public static float getPartialTicks() {
        return Side.getMinecraft().getTimer().getGameTimeDeltaPartialTick(true);
    }

    private static void update() {
        TICKS_COUNTER.set(counter -> counter + 1);

        List<IDelayedExecution> copy = CollectionHelper.pop(PENDING_EXECUTIONS.get(), ArrayList::new);
        copy.forEach(IDelayedExecution::start);
        EXECUTIONS.run(executions -> {
            executions.addAll(copy);
            for (Iterator<IDelayedExecution> iterator = executions.iterator(); iterator.hasNext(); ) {
                IDelayedExecution exec = iterator.next();
                exec.tick();
                if (exec.isFinished()) {
                    exec.finish();
                    iterator.remove();
                }
            }
        });
    }
}
