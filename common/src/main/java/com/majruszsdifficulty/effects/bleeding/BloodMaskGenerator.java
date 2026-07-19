package com.majruszsdifficulty.effects.bleeding;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

final class BloodMaskGenerator {
    private final int width;
    private final int height;
    private final float[] coverage;
    private final float[] surfaceHeight;
    private final float[] freshness;
    private final float[] age;

    static NativeImage generate(int width, int height, long snapshotTick, int lifetime, List<BloodSplat> splats) {
        BloodMaskGenerator generator = new BloodMaskGenerator(width, height);
        for (BloodSplat splat : splats) {
            float age = Mth.clamp((float)(snapshotTick - splat.bornTick()) / lifetime, 0.0f, 1.0f);
            if (age < 1.0f) {
                generator.drawSplat(splat, age);
            }
        }
        generator.smoothSurfaceHeight();
        return generator.createImage();
    }

    private BloodMaskGenerator(int width, int height) {
        this.width = width;
        this.height = height;
        int size = width * height;
        this.coverage = new float[size];
        this.surfaceHeight = new float[size];
        this.freshness = new float[size];
        this.age = new float[size];
        java.util.Arrays.fill(this.age, 1.0f);
    }

    private void drawSplat(BloodSplat splat, float splatAge) {
        SplittableRandom random = new SplittableRandom(splat.seed());
        float minimumDimension = this.height;
        float centerX = splat.x() * this.width;
        float centerY = splat.y() * this.height;
        float baseFreshness = range(random, 0.78f, 0.98f);
        float baseRadius = minimumDimension * range(random, 0.034f, 0.056f) * splat.scale();

        stampEllipse(
                centerX, centerY,
                baseRadius * range(random, 1.38f, 1.92f),
                baseRadius * range(random, 0.62f, 0.92f),
                splat.direction() + range(random, -0.2f, 0.2f),
                splatAge, baseFreshness, random.nextLong()
        );

        List<Droplet> droplets = new ArrayList<>(96);
        generateCoronaDroplets(
                random, droplets, centerX, centerY, splat.direction(), baseRadius,
                splatAge, baseFreshness
        );
        generateBallisticDroplets(
                random, droplets, centerX, centerY, splat.direction(), baseRadius,
                splatAge, baseFreshness
        );
        generateMistDroplets(
                random, droplets, centerX, centerY, splat.direction(), splat.scale(),
                splatAge, baseFreshness
        );
    }

    /**
     * Produces the close satellite ring of an impact. This is an anisotropic Thomas-like cluster:
     * samples are concentrated close to the impact while remaining detached from the main patch.
     */
    private void generateCoronaDroplets(
            SplittableRandom random,
            List<Droplet> droplets,
            float centerX,
            float centerY,
            float direction,
            float baseRadius,
            float splatAge,
            float splatFreshness
    ) {
        int targetCount = random.nextInt(14, 24);
        int accepted = 0;
        for (int attempt = 0; attempt < targetCount * 7 && accepted < targetCount; ++attempt) {
            float angle = range(random, 0.0f, Mth.TWO_PI);
            float radial = Math.min(1.0f, (float)Math.sqrt(-2.0 * Math.log(Math.max(1.0e-7, 1.0 - random.nextDouble()))) / 2.75f);
            float distance = baseRadius * Mth.lerp(radial, 2.3f, 5.1f);
            float directionalScale = 1.0f + 0.28f * Math.abs((float)Math.cos(angle - direction));
            float x = centerX + (float)Math.cos(angle) * distance * directionalScale;
            float y = centerY + (float)Math.sin(angle) * distance;
            float radius = baseRadius * Mth.lerp(radial, range(random, 0.16f, 0.31f), range(random, 0.065f, 0.15f));
            if (!isInside(x, y, radius) || !isSeparated(droplets, x, y, radius, 0.82f)) {
                continue;
            }

            float radialDirection = (float)Math.atan2(y - centerY, x - centerX);
            float stretch = range(random, 1.0f, 1.5f);
            stampEllipse(
                    x, y,
                    radius * stretch,
                    radius,
                    radialDirection + range(random, -0.22f, 0.22f),
                    splatAge,
                    splatFreshness * range(random, 0.88f, 1.0f),
                    random.nextLong()
            );
            droplets.add(new Droplet(x, y, radius));
            ++accepted;
        }
    }

    /**
     * Deposits independently flying droplets. Distance follows a long-tailed Gamma distribution;
     * angular velocity is concentrated around the impact direction with a smaller broad component.
     * A fast landing may be elongated, but it never has a geometric connection to the impact patch.
     */
    private void generateBallisticDroplets(
            SplittableRandom random,
            List<Droplet> droplets,
            float centerX,
            float centerY,
            float direction,
            float baseRadius,
            float splatAge,
            float splatFreshness
    ) {
        int targetCount = random.nextInt(28, 45);
        int accepted = 0;
        for (int attempt = 0; attempt < targetCount * 8 && accepted < targetCount; ++attempt) {
            float angle = random.nextDouble() < 0.78
                    ? direction + sampleNormal(random) * 0.52f
                    : direction + range(random, -2.35f, 2.35f);
            float directionX = (float)Math.cos(angle);
            float directionY = (float)Math.sin(angle);
            float availableTravel = distanceToBounds(centerX, centerY, directionX, directionY, 2.0f);
            if (availableTravel < baseRadius * 2.2f) {
                continue;
            }

            float gamma = sampleGammaShapeTwo(random);
            float travelFraction = 0.06f + 0.88f * gamma / (gamma + 2.45f);
            float travel = Math.max(baseRadius * range(random, 2.35f, 3.0f), availableTravel * travelFraction);
            travel = Math.min(travel, availableTravel * 0.96f);
            float lateral = sampleNormal(random) * baseRadius * Mth.lerp(travelFraction, 0.12f, 0.72f);
            float x = centerX + directionX * travel - directionY * lateral;
            float y = centerY + directionY * travel + directionX * lateral;
            float sizeBias = (float)Math.pow(random.nextDouble(), 2.15);
            float radius = baseRadius
                    * Mth.lerp(sizeBias, 0.055f, 0.27f)
                    * Mth.lerp(travelFraction, 1.0f, 0.56f);
            radius = Math.max(0.55f, radius);
            if (!isInside(x, y, radius) || !isSeparated(droplets, x, y, radius, 0.68f)) {
                continue;
            }

            stampBallisticDroplet(
                    random, x, y, angle, radius, travelFraction,
                    splatAge, splatFreshness * range(random, 0.82f, 1.0f)
            );
            droplets.add(new Droplet(x, y, radius));
            if (radius > 1.15f && random.nextDouble() < 0.2) {
                generateSecondaryDrops(
                        random, x, y, angle, radius, splatAge, splatFreshness * 0.9f
                );
            }
            ++accepted;
        }
    }

    /**
     * Adds fine aerosol-like points over the full available flight cone. Rejection sampling gives
     * them a blue-noise minimum spacing, preventing both a regular grid and accidental solid clumps.
     */
    private void generateMistDroplets(
            SplittableRandom random,
            List<Droplet> droplets,
            float centerX,
            float centerY,
            float direction,
            float scale,
            float splatAge,
            float splatFreshness
    ) {
        int targetCount = random.nextInt(16, 29);
        int accepted = 0;
        List<Droplet> mistDroplets = new ArrayList<>(targetCount);
        for (int attempt = 0; attempt < targetCount * 9 && accepted < targetCount; ++attempt) {
            float angle = direction + sampleNormal(random) * 0.94f;
            if (random.nextDouble() < 0.18) {
                angle = range(random, 0.0f, Mth.TWO_PI);
            }
            float directionX = (float)Math.cos(angle);
            float directionY = (float)Math.sin(angle);
            float availableTravel = distanceToBounds(centerX, centerY, directionX, directionY, 1.0f);
            if (availableTravel < this.height * 0.08f) {
                continue;
            }

            float travelFraction = Mth.lerp((float)Math.pow(random.nextDouble(), 0.62), 0.18f, 0.96f);
            float x = centerX + directionX * availableTravel * travelFraction;
            float y = centerY + directionY * availableTravel * travelFraction;
            float radius = this.height * range(random, 0.0012f, 0.0032f) * scale;
            if (!isInside(x, y, radius)
                    || !isSeparated(droplets, x, y, radius, 0.26f)
                    || !isSeparated(mistDroplets, x, y, radius, 1.45f)) {
                continue;
            }

            stampEllipse(
                    x, y,
                    radius * range(random, 1.0f, 1.42f),
                    radius,
                    angle,
                    splatAge,
                    splatFreshness * range(random, 0.78f, 0.94f),
                    random.nextLong()
            );
            mistDroplets.add(new Droplet(x, y, radius));
            ++accepted;
        }
    }

    private void stampBallisticDroplet(
            SplittableRandom random,
            float x,
            float y,
            float direction,
            float radius,
            float speed,
            float splatAge,
            float splatFreshness
    ) {
        float directionX = (float)Math.cos(direction);
        float directionY = (float)Math.sin(direction);
        if (radius > 0.8f && random.nextDouble() < Mth.lerp(speed, 0.06f, 0.2f)) {
            float length = radius * Mth.lerp(speed, range(random, 2.0f, 2.8f), range(random, 4.4f, 6.4f));
            float halfLength = length * 0.5f;
            stampCapsule(
                    x - directionX * halfLength,
                    y - directionY * halfLength,
                    x + directionX * halfLength,
                    y + directionY * halfLength,
                    radius * range(random, 0.55f, 0.72f),
                    radius * range(random, 0.38f, 0.58f),
                    splatAge,
                    splatFreshness,
                    random.nextLong()
            );
            return;
        }

        float stretch = 1.0f + speed * range(random, 0.15f, 0.9f);
        stampEllipse(
                x, y,
                radius * stretch,
                radius,
                direction + range(random, -0.14f, 0.14f),
                splatAge,
                splatFreshness,
                random.nextLong()
        );
    }

    private void generateSecondaryDrops(
            SplittableRandom random,
            float x,
            float y,
            float direction,
            float radius,
            float splatAge,
            float splatFreshness
    ) {
        int count = random.nextInt(2, 5);
        for (int index = 0; index < count; ++index) {
            float angle = direction + sampleNormal(random) * 0.72f;
            float distance = radius * range(random, 2.0f, 5.8f);
            float microRadius = radius * range(random, 0.16f, 0.34f);
            float microX = x + (float)Math.cos(angle) * distance;
            float microY = y + (float)Math.sin(angle) * distance;
            if (!isInside(microX, microY, microRadius)) {
                continue;
            }
            stampEllipse(
                    microX, microY,
                    microRadius * range(random, 1.0f, 1.4f),
                    microRadius,
                    angle,
                    splatAge,
                    splatFreshness,
                    random.nextLong()
            );
        }
    }

    private boolean isInside(float x, float y, float radius) {
        return x >= radius && x < this.width - radius && y >= radius && y < this.height - radius;
    }

    private static boolean isSeparated(List<Droplet> droplets, float x, float y, float radius, float spacingScale) {
        for (Droplet droplet : droplets) {
            float minimumDistance = Math.max(1.75f, (radius + droplet.radius()) * spacingScale);
            float deltaX = x - droplet.x();
            float deltaY = y - droplet.y();
            if (deltaX * deltaX + deltaY * deltaY < minimumDistance * minimumDistance) {
                return false;
            }
        }
        return true;
    }

    private float distanceToBounds(float x, float y, float directionX, float directionY, float margin) {
        float distance = Float.POSITIVE_INFINITY;
        if (directionX > 0.0001f) {
            distance = Math.min(distance, (this.width - margin - x) / directionX);
        } else if (directionX < -0.0001f) {
            distance = Math.min(distance, (margin - x) / directionX);
        }
        if (directionY > 0.0001f) {
            distance = Math.min(distance, (this.height - margin - y) / directionY);
        } else if (directionY < -0.0001f) {
            distance = Math.min(distance, (margin - y) / directionY);
        }
        return Math.max(0.0f, distance);
    }

    private static float sampleNormal(SplittableRandom random) {
        double first = Math.max(1.0e-7, random.nextDouble());
        double second = random.nextDouble();
        return (float)(Math.sqrt(-2.0 * Math.log(first)) * Math.cos(Mth.TWO_PI * second));
    }

    private static float sampleGammaShapeTwo(SplittableRandom random) {
        double first = Math.max(1.0e-7, random.nextDouble());
        double second = Math.max(1.0e-7, random.nextDouble());
        return (float)-Math.log(first * second);
    }

    private record Droplet(float x, float y, float radius) {}

    private void stampEllipse(
            float centerX,
            float centerY,
            float radiusX,
            float radiusY,
            float rotation,
            float splatAge,
            float splatFreshness,
            long seed
    ) {
        float extent = Math.max(radiusX, radiusY) * 1.2f + 2.0f;
        int minimumX = Math.max(0, Mth.floor(centerX - extent));
        int maximumX = Math.min(this.width - 1, Mth.ceil(centerX + extent));
        int minimumY = Math.max(0, Mth.floor(centerY - extent));
        int maximumY = Math.min(this.height - 1, Mth.ceil(centerY + extent));
        float cosine = (float)Math.cos(rotation);
        float sine = (float)Math.sin(rotation);
        float phase1 = unitHash(seed) * (float)(Math.PI * 2.0);
        float phase2 = unitHash(seed ^ 0x9E3779B97F4A7C15L) * (float)(Math.PI * 2.0);
        float antiAlias = 1.35f;

        for (int y = minimumY; y <= maximumY; ++y) {
            for (int x = minimumX; x <= maximumX; ++x) {
                float offsetX = x + 0.5f - centerX;
                float offsetY = y + 0.5f - centerY;
                float localX = cosine * offsetX + sine * offsetY;
                float localY = -sine * offsetX + cosine * offsetY;
                float normalizedX = localX / radiusX;
                float normalizedY = localY / radiusY;
                float angle = (float)Math.atan2(normalizedY, normalizedX);
                float boundary = 1.0f
                        + 0.105f * (float)Math.sin(angle * 3.0f + phase1)
                        + 0.065f * (float)Math.sin(angle * 5.0f + phase2)
                        + 0.035f * (float)Math.sin(angle * 9.0f + phase1 - phase2);
                float radialDistance = (float)Math.sqrt(normalizedX * normalizedX + normalizedY * normalizedY);
                float signedDistance = (boundary - radialDistance) * Math.min(radiusX, radiusY);
                float alpha = smoothstep(-antiAlias, antiAlias, signedDistance);
                if (alpha <= 0.0f) {
                    continue;
                }

                float detail = valueNoise(x, y, seed, 11);
                paint(
                        x,
                        y,
                        alpha,
                        0.82f + detail * 0.1f,
                        splatAge,
                        splatFreshness * (0.96f + detail * 0.04f)
                );
            }
        }
    }

    private void stampCapsule(
            float startX,
            float startY,
            float endX,
            float endY,
            float radiusStart,
            float radiusEnd,
            float splatAge,
            float splatFreshness,
            long seed
    ) {
        float maximumRadius = Math.max(radiusStart, radiusEnd) + 2.0f;
        int minimumX = Math.max(0, Mth.floor(Math.min(startX, endX) - maximumRadius));
        int maximumX = Math.min(this.width - 1, Mth.ceil(Math.max(startX, endX) + maximumRadius));
        int minimumY = Math.max(0, Mth.floor(Math.min(startY, endY) - maximumRadius));
        int maximumY = Math.min(this.height - 1, Mth.ceil(Math.max(startY, endY) + maximumRadius));
        float segmentX = endX - startX;
        float segmentY = endY - startY;
        float segmentLengthSquared = segmentX * segmentX + segmentY * segmentY;

        if (segmentLengthSquared < 0.0001f) {
            return;
        }

        for (int y = minimumY; y <= maximumY; ++y) {
            for (int x = minimumX; x <= maximumX; ++x) {
                float pointX = x + 0.5f - startX;
                float pointY = y + 0.5f - startY;
                float progress = Mth.clamp((pointX * segmentX + pointY * segmentY) / segmentLengthSquared, 0.0f, 1.0f);
                float nearestX = startX + segmentX * progress;
                float nearestY = startY + segmentY * progress;
                float deltaX = x + 0.5f - nearestX;
                float deltaY = y + 0.5f - nearestY;
                float radius = Mth.lerp(progress, radiusStart, radiusEnd);
                float distance = (float)Math.sqrt(deltaX * deltaX + deltaY * deltaY);
                float alpha = smoothstep(-1.15f, 1.15f, radius - distance);
                if (alpha <= 0.0f) {
                    continue;
                }

                float detail = valueNoise(x, y, seed, 9);
                paint(
                        x,
                        y,
                        alpha,
                        0.76f + detail * 0.1f,
                        splatAge,
                        splatFreshness * (0.96f + detail * 0.04f)
                );
            }
        }
    }

    private void paint(int x, int y, float alpha, float height, float splatAge, float splatFreshness) {
        int index = x + y * this.width;
        this.coverage[index] = Math.max(this.coverage[index], alpha);
        this.surfaceHeight[index] = Math.max(this.surfaceHeight[index], height * alpha);
        this.freshness[index] = Math.max(this.freshness[index], splatFreshness);
        this.age[index] = Math.min(this.age[index], splatAge);
    }

    private void smoothSurfaceHeight() {
        float[] horizontal = new float[this.surfaceHeight.length];
        float[] vertical = new float[this.surfaceHeight.length];
        for (int iteration = 0; iteration < 2; ++iteration) {
            for (int y = 0; y < this.height; ++y) {
                int row = y * this.width;
                for (int x = 0; x < this.width; ++x) {
                    float left = this.surfaceHeight[row + Math.max(0, x - 1)];
                    float center = this.surfaceHeight[row + x];
                    float right = this.surfaceHeight[row + Math.min(this.width - 1, x + 1)];
                    horizontal[row + x] = (left + center * 2.0f + right) * 0.25f;
                }
            }

            for (int y = 0; y < this.height; ++y) {
                int previousRow = Math.max(0, y - 1) * this.width;
                int row = y * this.width;
                int nextRow = Math.min(this.height - 1, y + 1) * this.width;
                for (int x = 0; x < this.width; ++x) {
                    float blurred = (horizontal[previousRow + x] + horizontal[row + x] * 2.0f + horizontal[nextRow + x]) * 0.25f;
                    vertical[row + x] = blurred * this.coverage[row + x];
                }
            }

            System.arraycopy(vertical, 0, this.surfaceHeight, 0, this.surfaceHeight.length);
        }
    }

    private NativeImage createImage() {
        NativeImage image = new NativeImage(this.width, this.height, false);
        for (int y = 0; y < this.height; ++y) {
            for (int x = 0; x < this.width; ++x) {
                int index = x + y * this.width;
                int red = toByte(this.coverage[index]);
                int green = toByte(this.surfaceHeight[index]);
                int blue = toByte(this.freshness[index]);
                int alpha = toByte(this.age[index]);
                image.setPixelRGBA(x, y, FastColor.ABGR32.color(alpha, blue, green, red));
            }
        }
        return image;
    }

    private static int toByte(float value) {
        return Mth.clamp(Math.round(value * 255.0f), 0, 255);
    }

    private static float smoothstep(float minimum, float maximum, float value) {
        float normalized = Mth.clamp((value - minimum) / (maximum - minimum), 0.0f, 1.0f);
        return normalized * normalized * (3.0f - 2.0f * normalized);
    }

    private static float triangular(SplittableRandom random) {
        return (float)(random.nextDouble() + random.nextDouble() - 1.0);
    }

    private static float range(SplittableRandom random, float minimum, float maximum) {
        return (float)random.nextDouble(minimum, maximum);
    }

    private static float unitHash(long value) {
        value ^= value >>> 33;
        value *= 0xff51afd7ed558ccdL;
        value ^= value >>> 33;
        value *= 0xc4ceb9fe1a85ec53L;
        value ^= value >>> 33;
        return (float)(value & 0xFFFFFFL) / 0x1000000;
    }

    private static float pixelHash(int x, int y, long seed) {
        long value = seed ^ (long)x * 0x9E3779B185EBCA87L ^ (long)y * 0xC2B2AE3D27D4EB4FL;
        return unitHash(value);
    }

    private static float valueNoise(int x, int y, long seed, int scale) {
        int cellX = Math.floorDiv(x, scale);
        int cellY = Math.floorDiv(y, scale);
        float localX = (float)Math.floorMod(x, scale) / scale;
        float localY = (float)Math.floorMod(y, scale) / scale;
        localX = localX * localX * (3.0f - 2.0f * localX);
        localY = localY * localY * (3.0f - 2.0f * localY);
        float top = Mth.lerp(localX, pixelHash(cellX, cellY, seed), pixelHash(cellX + 1, cellY, seed));
        float bottom = Mth.lerp(localX, pixelHash(cellX, cellY + 1, seed), pixelHash(cellX + 1, cellY + 1, seed));
        return Mth.lerp(localY, top, bottom);
    }
}
