package net.dvmn2.shakemod.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class ShakeModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        PayloadTypeRegistry.playS2C().register(net.dvmn2.bmcmod.client.shake.CameraShakePayload.ID, net.dvmn2.bmcmod.client.shake.CameraShakePayload.CODEC);

        ClientPlayNetworking.registerGlobalReceiver(net.dvmn2.bmcmod.client.shake.CameraShakePayload.ID, (payload, context) -> {
            context.client().execute(() ->
                    net.dvmn2.bmcmod.client.shake.CameraShakeHandler.start(payload.intensity(), payload.power(), payload.duration()));
        });
    }
}
