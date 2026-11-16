package com.backpackmod;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Creative-only instant builder for the Grand Pixel Store at spawn.
 * Places a modern quartz & glass mega-store with interior market stalls
 * and a glowing rooftop sign.
 */
public class StoreSpawnerItem extends Item {
    public StoreSpawnerItem(Properties properties) {
        super(properties.stacksTo(1).rarity(Rarity.EPIC));
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        Player player = ctx.getPlayer();
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        if (player != null && !player.isCreative()) {
            player.sendSystemMessage(Component.literal("§c[Pixel Store] This spawner can only be used by server admins in Creative mode!"));
            return InteractionResult.FAIL;
        }

        BlockPos center = ctx.getClickedPos().above();
        buildStore(level, center);

        level.playSound(null, center, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.BLOCKS, 1.0f, 1.0f);
        if (player != null) {
            player.sendSystemMessage(Component.literal("§6§l[Pixel Store] §aThe Grand Pixel Store has been constructed successfully!"));
        }
        return InteractionResult.SUCCESS;
    }

    private void buildStore(Level level, BlockPos origin) {
        int radius = 9; // 19x19 footprint
        int height = 7;

        BlockState quartz = Blocks.SMOOTH_QUARTZ.defaultBlockState();
        BlockState blackstone = Blocks.POLISHED_BLACKSTONE.defaultBlockState();
        BlockState glass = Blocks.TINTED_GLASS.defaultBlockState();
        BlockState lantern = Blocks.SEA_LANTERN.defaultBlockState();
        BlockState gold = Blocks.GOLD_BLOCK.defaultBlockState();
        BlockState sprucePlanks = Blocks.SPRUCE_PLANKS.defaultBlockState();
        BlockState chest = Blocks.CHEST.defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();

        // 1. Clear interior area and lay checkered marble floor
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                BlockPos floorPos = origin.offset(dx, -1, dz);
                // Checkered floor pattern
                boolean check = ((dx + dz) % 2 == 0);
                level.setBlockAndUpdate(floorPos, check ? quartz : blackstone);

                // Clear interior space
                for (int dy = 0; dy <= height; dy++) {
                    level.setBlockAndUpdate(origin.offset(dx, dy, dz), air);
                }
            }
        }

        // 2. Build Walls and Panoramic Windows
        for (int dy = 0; dy < height; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    boolean isBorder = (Math.abs(dx) == radius || Math.abs(dz) == radius);
                    if (!isBorder) continue;

                    BlockPos p = origin.offset(dx, dy, dz);

                    // Corner pillars: Gold and Quartz
                    boolean isCorner = (Math.abs(dx) >= radius - 1 && Math.abs(dz) >= radius - 1);
                    if (isCorner) {
                        level.setBlockAndUpdate(p, (dy == 0 || dy == height - 1) ? gold : quartz);
                        continue;
                    }

                    // Front entrance door (dz == -radius, dx between -2 and 2)
                    if (dz == -radius && Math.abs(dx) <= 2) {
                        if (dy < 4) {
                            level.setBlockAndUpdate(p, air); // Entrance doorway
                        } else {
                            level.setBlockAndUpdate(p, (dy == 4) ? lantern : quartz);
                        }
                        continue;
                    }

                    // Windows vs solid columns
                    if (dy >= 1 && dy <= height - 2 && Math.abs(dx) % 3 != 0 && Math.abs(dz) % 3 != 0) {
                        level.setBlockAndUpdate(p, glass);
                    } else if (dy == 0 || dy == height - 1) {
                        level.setBlockAndUpdate(p, quartz);
                    } else {
                        level.setBlockAndUpdate(p, (dy % 2 == 0) ? lantern : quartz);
                    }
                }
            }
        }

        // 3. Ceiling with Neon Sea Lantern Grids
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                BlockPos roofPos = origin.offset(dx, height, dz);
                if (Math.abs(dx) % 4 == 0 && Math.abs(dz) % 4 == 0) {
                    level.setBlockAndUpdate(roofPos, lantern);
                } else {
                    level.setBlockAndUpdate(roofPos, quartz);
                }
            }
        }

        // 4. Interior Market Stalls with Spruce Counters & Chests
        int[][] stallOffsets = {
            {-4, -4}, {4, -4},
            {-4, 4},  {4, 4}
        };

        for (int[] offset : stallOffsets) {
            int sx = offset[0];
            int sz = offset[1];
            BlockPos stallCenter = origin.offset(sx, 0, sz);

            // Counter tables
            for (int cdx = -1; cdx <= 1; cdx++) {
                for (int cdz = -1; cdz <= 1; cdz++) {
                    if (cdx == 0 && cdz == 0) {
                        level.setBlockAndUpdate(stallCenter.offset(cdx, 0, cdz), chest);
                    } else {
                        level.setBlockAndUpdate(stallCenter.offset(cdx, 0, cdz), sprucePlanks);
                    }
                }
            }
            // Hanging lantern above stall
            level.setBlockAndUpdate(stallCenter.offset(0, height - 1, 0), lantern);
        }

        // 5. Grand Glowing Rooftop Sign: "PIXEL STORE" in Gold and Lanterns
        // Built on top of the front roof (dz = -radius + 1, dy = height + 1 to height + 4)
        buildRoofSign(level, origin.offset(0, height + 1, -radius + 1));
    }

    private void buildRoofSign(Level level, BlockPos signOrigin) {
        // Pixel Store billboard backdrop
        BlockState backBlock = Blocks.POLISHED_BLACKSTONE.defaultBlockState();
        BlockState goldBlock = Blocks.GOLD_BLOCK.defaultBlockState();
        BlockState lantern = Blocks.SEA_LANTERN.defaultBlockState();

        int signWidth = 17;
        int signHeight = 4;

        for (int dx = -signWidth / 2; dx <= signWidth / 2; dx++) {
            for (int dy = 0; dy < signHeight; dy++) {
                level.setBlockAndUpdate(signOrigin.offset(dx, dy, 0), backBlock);
            }
        }

        // Accent glowing neon borders around the billboard
        for (int dx = -signWidth / 2; dx <= signWidth / 2; dx++) {
            level.setBlockAndUpdate(signOrigin.offset(dx, signHeight, 0), lantern);
            level.setBlockAndUpdate(signOrigin.offset(dx, -1, 0), goldBlock);
        }
        for (int dy = 0; dy < signHeight; dy++) {
            level.setBlockAndUpdate(signOrigin.offset(-signWidth / 2, dy, 0), goldBlock);
            level.setBlockAndUpdate(signOrigin.offset(signWidth / 2, dy, 0), goldBlock);
        }

        // Center glowing golden beacon crest
        level.setBlockAndUpdate(signOrigin.offset(0, 1, 1), goldBlock);
        level.setBlockAndUpdate(signOrigin.offset(0, 2, 1), lantern);
    }
}
