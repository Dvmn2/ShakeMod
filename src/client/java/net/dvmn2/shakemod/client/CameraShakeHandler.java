package net.dvmn2.shakemod.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.random.Random;

public class CameraShakeHandler {

    private static final float POSITION_SCALE = 0.01f;

    private static final Random RANDOM = Random.create();

    private static float rotationIntensity = 0f;
    private static float positionIntensity = 0f;
    private static int ticksLeft = 0;
    private static int totalDuration = 0; // изначальная длительность — нужна для расчёта затухания

    public static void start(int intensity, int power, int duration) {
        rotationIntensity = intensity;
        positionIntensity = power * POSITION_SCALE;
        ticksLeft = duration;
        totalDuration = duration;
    }

    public static void tick(MinecraftClient client) {
        if (ticksLeft > 0) {
            ticksLeft--;
        }
    }

    private static float fadeFactor() {
        if (totalDuration <= 0) return 0f;
        float t = (float) ticksLeft / (float) totalDuration; // 1 -> 0
        return t * t; // квадратичное затухание — тряска "успокаивается" быстрее к концу
    }

    public static float getYawOffset() {
        return ticksLeft > 0 ? (RANDOM.nextFloat() - 0.5f) * rotationIntensity * fadeFactor() : 0f;
    }

    public static float getPitchOffset() {
        return ticksLeft > 0 ? (RANDOM.nextFloat() - 0.5f) * rotationIntensity * fadeFactor() : 0f;
    }

    public static float getRightOffset() {
        return ticksLeft > 0 ? (RANDOM.nextFloat() - 0.5f) * positionIntensity * fadeFactor() : 0f;
    }

    public static float getUpOffset() {
        return ticksLeft > 0 ? (RANDOM.nextFloat() - 0.5f) * positionIntensity * fadeFactor() : 0f;
    }

    public static boolean isShaking() {
        return ticksLeft > 0;
    }
}