package net.dvmn2.shakemod.client.mixin;

import net.dvmn2.shakemod.client.CameraShakeHandler;

import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class ShakeMixin {

    @Shadow
    public abstract float getYaw();

    @Shadow
    public abstract float getPitch();

    @Shadow
    protected abstract void setRotation(float yaw, float pitch);

    @Shadow
    public abstract Vec3d getCameraPos();

    @Shadow
    protected abstract void setPos(double x, double y, double z);

    @Shadow
    public abstract Vector3fc getHorizontalPlane();

    @Shadow
    public abstract Vector3fc getVerticalPlane();

    @Inject(method = "update", at = @At("TAIL"))
    private void applyShake(World area, Entity focusedEntity, boolean thirdPerson,
                            boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (!CameraShakeHandler.isShaking()) return;

        // тряска угла
        float yawOffset = CameraShakeHandler.getYawOffset();
        float pitchOffset = CameraShakeHandler.getPitchOffset();
        this.setRotation(this.getYaw() + yawOffset, this.getPitch() + pitchOffset);

        // тряска позиции — сдвигаем вдоль локальных осей камеры,
        // чтобы эффект выглядел как дрожание "в руках", а не сдвиг по мировым осям
        Vector3fc right = this.getHorizontalPlane();
        Vector3fc up = this.getVerticalPlane();
        float rightOffset = CameraShakeHandler.getRightOffset();
        float upOffset = CameraShakeHandler.getUpOffset();

        Vec3d pos = this.getCameraPos();
        double dx = right.x() * rightOffset + up.x() * upOffset;
        double dy = right.y() * rightOffset + up.y() * upOffset;
        double dz = right.z() * rightOffset + up.z() * upOffset;

        this.setPos(pos.x + dx, pos.y + dy, pos.z + dz);
    }
}