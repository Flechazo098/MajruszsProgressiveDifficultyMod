package com.majruszsdifficulty.effects.bleeding;

import java.util.SplittableRandom;
import java.util.concurrent.ThreadLocalRandom;

record BloodSplat(long seed, long bornTick, float x, float y, float direction, float scale) {
    static BloodSplat create(long bornTick) {
        long seed = ThreadLocalRandom.current().nextLong();
        SplittableRandom random = new SplittableRandom(seed);
        float x;
        float y;
        float direction;

        if (random.nextDouble() < 0.72) {
            float margin = range(random, 0.025f, 0.16f);
            switch (random.nextInt(4)) {
                case 0 -> {
                    x = margin;
                    y = range(random, 0.05f, 0.95f);
                    direction = range(random, -0.85f, 0.85f);
                }
                case 1 -> {
                    x = 1.0f - margin;
                    y = range(random, 0.05f, 0.95f);
                    direction = (float)Math.PI + range(random, -0.85f, 0.85f);
                }
                case 2 -> {
                    x = range(random, 0.05f, 0.95f);
                    y = margin;
                    direction = (float)(Math.PI * 0.5) + range(random, -0.85f, 0.85f);
                }
                default -> {
                    x = range(random, 0.05f, 0.95f);
                    y = 1.0f - margin;
                    direction = (float)(-Math.PI * 0.5) + range(random, -0.85f, 0.85f);
                }
            }
        } else {
            x = range(random, 0.1f, 0.9f);
            y = range(random, 0.08f, 0.92f);
            direction = range(random, 0.0f, (float)(Math.PI * 2.0));
        }

        return new BloodSplat(seed, bornTick, x, y, direction, range(random, 0.82f, 1.28f));
    }

    private static float range(SplittableRandom random, float minimum, float maximum) {
        return (float)random.nextDouble(minimum, maximum);
    }
}
