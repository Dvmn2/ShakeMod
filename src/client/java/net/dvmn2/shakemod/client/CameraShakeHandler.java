package net.dvmn2.shakemod.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.random.Random;

/**
 * Класс отвечает за состояние тряски камеры на стороне клиента.
 * Хранит текущие параметры эффекта (амплитуда поворота/смещения, оставшееся
 * и общее время действия) и на каждый тик пересчитывает величину затухания.
 * <p>
 * Логика полностью статическая, так как тряска камеры — это глобальный
 * эффект клиента, а не привязанный к конкретной сущности объект. Само
 * применение смещения к камере происходит не здесь, а в
 * {@link net.dvmn2.shakemod.client.mixin.ShakeMixin} — этот класс только
 * хранит состояние и отдаёт готовые значения смещений по запросу.
 */
public class CameraShakeHandler {

    /**
     * Множитель для перевода "сырых" единиц позиции (из пакета/команды) в блоки мира.
     */
    private static final float POSITION_SCALE = 0.01f;

    /**
     * Общий генератор случайных чисел для расчёта дрожания.
     */
    private static final Random RANDOM = Random.create();

    /**
     * Максимальная амплитуда поворота камеры (в градусах) на текущий момент тряски.
     */
    private static float rotation = 0f;

    /**
     * Максимальная амплитуда смещения камеры (в блоках, уже с учётом POSITION_SCALE).
     */
    private static float position = 0f;

    /**
     * Сколько тиков осталось до окончания тряски.
     */
    private static int ticksLeft = 0;

    /**
     * Изначальная длительность — нужна для расчёта коэффициента затухания.
     */
    private static int totalDuration = 0;

    /**
     * Запускает (или перезапускает — если тряска уже шла, новая перекроет старую)
     * эффект тряски камеры.
     *
     * @param angle_delta    максимальный угол отклонения камеры (градусы)
     * @param position_delta максимальное смещение камеры (в "сырых" единицах, см. POSITION_SCALE)
     * @param duration       длительность эффекта в тиках
     */
    public static void start(int angle_delta, int position_delta, int duration) {
        rotation = angle_delta;
        position = position_delta * POSITION_SCALE;
        ticksLeft = duration;
        totalDuration = duration;
    }

    /**
     * Вызывается каждый клиентский тик (см. {@link ShakeModClient}), просто
     * уменьшает оставшееся время тряски. Само дрожание камеры считается
     * "лениво", по запросу, через getXxxOffset()-методы ниже.
     */
    public static void tick(MinecraftClient client) {
        if (ticksLeft > 0) {
            ticksLeft--;
        }
    }

    /**
     * Коэффициент затухания тряски: 1 в начале эффекта, 0 в самом конце.
     * Используется квадратичная кривая (t^2), а не линейная — тряска
     * "успокаивается" быстрее к концу, что визуально выглядит естественнее.
     */
    private static float fadeFactor() {
        if (totalDuration <= 0) return 0f;
        float t = (float) ticksLeft / (float) totalDuration; // 1 -> 0
        return t * t; // квадратичное затухание
    }

    /**
     * Случайное смещение по рысканию (yaw) камеры на текущий момент вызова.
     */
    public static float getYawOffset() {
        return ticksLeft > 0 ? (RANDOM.nextFloat() - 0.5f) * rotation * fadeFactor() : 0f;
    }

    /**
     * Случайное смещение по тангажу (pitch) камеры на текущий момент вызова.
     */
    public static float getPitchOffset() {
        return ticksLeft > 0 ? (RANDOM.nextFloat() - 0.5f) * rotation * fadeFactor() : 0f;
    }

    /**
     * Случайное смещение камеры вдоль её локальной горизонтальной оси ("вправо/влево").
     */
    public static float getRightOffset() {
        return ticksLeft > 0 ? (RANDOM.nextFloat() - 0.5f) * position * fadeFactor() : 0f;
    }

    /**
     * Случайное смещение камеры вдоль её локальной вертикальной оси ("вверх/вниз").
     */
    public static float getUpOffset() {
        return ticksLeft > 0 ? (RANDOM.nextFloat() - 0.5f) * position * fadeFactor() : 0f;
    }

    /**
     * true, если тряска сейчас активна (остались тики).
     */
    public static boolean isShaking() {
        return ticksLeft > 0;
    }
}