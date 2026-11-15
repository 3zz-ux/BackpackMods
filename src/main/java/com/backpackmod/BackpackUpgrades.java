package com.backpackmod;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/** Custom data component holding the special upgrades installed in a backpack. */
public record BackpackUpgrades(boolean magnet, boolean autoFeed) {
    public static final BackpackUpgrades NONE = new BackpackUpgrades(false, false);
    public static final Codec<BackpackUpgrades> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.BOOL.optionalFieldOf("magnet", false).forGetter(BackpackUpgrades::magnet),
            Codec.BOOL.optionalFieldOf("auto_feed", false).forGetter(BackpackUpgrades::autoFeed)
    ).apply(i, BackpackUpgrades::new));
    public static final StreamCodec<ByteBuf, BackpackUpgrades> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, BackpackUpgrades::magnet,
            ByteBufCodecs.BOOL, BackpackUpgrades::autoFeed,
            BackpackUpgrades::new);

    public BackpackUpgrades withMagnet() { return new BackpackUpgrades(true, autoFeed); }
    public BackpackUpgrades withAutoFeed() { return new BackpackUpgrades(magnet, true); }
}
