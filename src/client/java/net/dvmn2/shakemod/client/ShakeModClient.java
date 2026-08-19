package net.dvmn2.shakemod.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class ShakeModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        PayloadTypeRegistry.playS2C().register(CameraShakePayload.ID, CameraShakePayload.CODEC);

        ClientPlayNetworking.registerGlobalReceiver(CameraShakePayload.ID, (payload, context) -> {
            context.client().execute(() ->
                    CameraShakeHandler.start(payload.intensity(), payload.power(), payload.duration()));
        });

        ClientTickEvents.END_CLIENT_TICK.register(CameraShakeHandler::tick);
    }
}
