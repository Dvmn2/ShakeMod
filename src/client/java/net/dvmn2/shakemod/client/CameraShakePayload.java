package net.dvmn2.shakemod.client;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record CameraShakePayload(int intensity, int power, int duration) implements CustomPayload {
    public static final Id<CameraShakePayload> ID =
            new Id<>(Identifier.of("shakemod", "shake"));

    public static final PacketCodec<PacketByteBuf, CameraShakePayload> CODEC = PacketCodec.of(
            (payload, buf) -> {
                buf.writeInt(payload.intensity());
                buf.writeInt(payload.power());
                buf.writeInt(payload.duration());
            },
            buf -> new CameraShakePayload(buf.readInt(), buf.readInt(), buf.readInt())
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}