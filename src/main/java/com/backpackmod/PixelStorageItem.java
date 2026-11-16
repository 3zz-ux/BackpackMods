package com.backpackmod;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.Half;

/**
 * Pixel Storage Item:
 * Crafted from 9 chests. When placed on the ground, it instantly constructs
 * the grand Gothic cathedral stone-brick storage vault matching the provided concept photo:
 * - Soaring stone-brick Gothic corbel arches (18 blocks tall)
 * - 4 large bays of stacked double chests (11 rows high) on both sides
 * - Illuminated center dividers with warm golden Ochre Froglights
 * - Pillar plinths with potted ferns and wall torches
 * - Regal central runner carpet (Dark Red with golden yellow border)
 * - Central dark-oak display tables with potted plants & lanterns (no armor stands per user request)
 * - Triple hanging glowstone & lantern chandeliers from iron chains
 * - Rear elevated mezzanine balcony with grand central staircase
 */
public class PixelStorageItem extends Item {
    public PixelStorageItem(Properties properties) {
        super(properties.stacksTo(1).rarity(Rarity.RARE));
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        Player player = ctx.getPlayer();
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        BlockPos origin = ctx.getClickedPos().above();
        buildStorageVault(level, origin);

        level.playSound(null, origin, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.BLOCKS, 1.0f, 1.0f);
        if (player != null) {
            player.sendSystemMessage(Component.literal("§6§l[Pixel Storage] §aتم بناء قاعة التخزين القوطية الكبرى بنجاح!"));
            if (!player.isCreative()) {
                ctx.getItemInHand().shrink(1);
            }
        }

        return InteractionResult.SUCCESS;
    }

    private void buildStorageVault(Level level, BlockPos origin) {
        int halfWidth = 6;  // X: -6 to +6 (13 blocks wide total)
        int length = 29;    // Z: 0 to 29 (30 blocks long)
        int height = 18;    // Y: 0 to 18 (19 blocks tall)

        BlockState stoneBricks = Blocks.STONE_BRICKS.defaultBlockState();
        BlockState chiseledBricks = Blocks.CHISELED_STONE_BRICKS.defaultBlockState();
        BlockState smoothStone = Blocks.SMOOTH_STONE.defaultBlockState();
        BlockState stoneStairs = Blocks.STONE_BRICK_STAIRS.defaultBlockState();
        BlockState darkOakPlanks = Blocks.DARK_OAK_PLANKS.defaultBlockState();
        BlockState darkOakStairs = Blocks.DARK_OAK_STAIRS.defaultBlockState();
        BlockState darkOakFence = Blocks.DARK_OAK_FENCE.defaultBlockState();
        BlockState glowstone = Blocks.GLOWSTONE.defaultBlockState();
        BlockState ochreFroglight = Blocks.OCHRE_FROGLIGHT.defaultBlockState();
        BlockState chain = getBlockState("chain", getBlockState("iron_chain", Blocks.IRON_BARS.defaultBlockState()));
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockState redCarpet = getBlockState("red_carpet", Blocks.STONE_BRICKS.defaultBlockState());
        BlockState yellowCarpet = getBlockState("yellow_carpet", Blocks.GOLD_BLOCK.defaultBlockState());
        BlockState potFern = Blocks.POTTED_FERN.defaultBlockState();
        BlockState hangingLantern = Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true);
        BlockState standingLantern = Blocks.LANTERN.defaultBlockState();
        BlockState goldBlock = Blocks.GOLD_BLOCK.defaultBlockState();
        BlockState diamondBlock = Blocks.DIAMOND_BLOCK.defaultBlockState();

        // 1. Clear Interior Space & Lay Floor
        for (int x = -halfWidth; x <= halfWidth; x++) {
            for (int z = 0; z <= length; z++) {
                // Clear interior
                for (int y = 0; y <= height; y++) {
                    level.setBlockAndUpdate(origin.offset(x, y, z), air);
                }
                // Floor base at y = -1
                if (Math.abs(x) == 2 && z % 6 == 0) {
                    level.setBlockAndUpdate(origin.offset(x, -1, z), chiseledBricks);
                } else if (Math.abs(x) == 2) {
                    level.setBlockAndUpdate(origin.offset(x, -1, z), smoothStone);
                } else {
                    level.setBlockAndUpdate(origin.offset(x, -1, z), stoneBricks);
                }
            }
        }

        // 2. Central Royal Runner Carpet with Golden Border (Z: 2 to 24)
        for (int z = 2; z <= 24; z++) {
            for (int x = -1; x <= 1; x++) {
                BlockPos p = origin.offset(x, 0, z);
                if (z == 2 || z == 24 || x == -1 || x == 1) {
                    level.setBlockAndUpdate(p, yellowCarpet);
                } else {
                    level.setBlockAndUpdate(p, redCarpet);
                }
            }
        }

        // 3. Structural Outer Walls (X = -6 and X = 6)
        for (int z = 0; z <= length; z++) {
            for (int y = 0; y <= 12; y++) {
                level.setBlockAndUpdate(origin.offset(-halfWidth, y, z), stoneBricks);
                level.setBlockAndUpdate(origin.offset(halfWidth, y, z), stoneBricks);
            }
        }

        // 4. Pillars & Chest Bays along Z = 0 to 24
        // Pillars are at Z = 0, 6, 12, 18, 24
        int[] pillarZ = { 0, 6, 12, 18, 24 };
        for (int pz : pillarZ) {
            for (int y = 0; y <= 11; y++) {
                BlockState pState = (y == 0 || y == 4 || y == 8 || y == 11) ? chiseledBricks : stoneBricks;
                level.setBlockAndUpdate(origin.offset(-5, y, pz), pState);
                level.setBlockAndUpdate(origin.offset(5, y, pz), pState);
            }

            // Pillar Plinths, Potted Ferns, and Wall Torches (facing aisle)
            if (pz > 0 && pz < 24) {
                // Left Pillar front accessories
                level.setBlockAndUpdate(origin.offset(-4, 0, pz),
                        stoneStairs.setValue(StairBlock.FACING, Direction.EAST));
                level.setBlockAndUpdate(origin.offset(-4, 1, pz), potFern);
                level.setBlockAndUpdate(origin.offset(-4, 3, pz),
                        Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.EAST));
                level.setBlockAndUpdate(origin.offset(-4, 7, pz), standingLantern);

                // Right Pillar front accessories
                level.setBlockAndUpdate(origin.offset(4, 0, pz),
                        stoneStairs.setValue(StairBlock.FACING, Direction.WEST));
                level.setBlockAndUpdate(origin.offset(4, 1, pz), potFern);
                level.setBlockAndUpdate(origin.offset(4, 3, pz),
                        Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.WEST));
                level.setBlockAndUpdate(origin.offset(4, 7, pz), standingLantern);
            }
        }

        // 4 Bays of Double Chests between Pillars
        int[] bayStarts = { 1, 7, 13, 19 };
        for (int bStart : bayStarts) {
            // bStart + 0 and bStart + 1 = Double Chest #1
            // bStart + 2             = Glowing Ochre Froglight center category column
            // bStart + 3 and bStart + 4 = Double Chest #2
            for (int y = 0; y <= 10; y++) {
                // West Wall (Left side, chests facing East into aisle)
                BlockPos leftChest1A = origin.offset(-5, y, bStart);
                BlockPos leftChest1B = origin.offset(-5, y, bStart + 1);
                level.setBlockAndUpdate(leftChest1A, Blocks.CHEST.defaultBlockState()
                        .setValue(ChestBlock.FACING, Direction.EAST)
                        .setValue(ChestBlock.TYPE, ChestType.LEFT));
                level.setBlockAndUpdate(leftChest1B, Blocks.CHEST.defaultBlockState()
                        .setValue(ChestBlock.FACING, Direction.EAST)
                        .setValue(ChestBlock.TYPE, ChestType.RIGHT));

                // Left center glowing marker column
                BlockPos leftDivider = origin.offset(-5, y, bStart + 2);
                level.setBlockAndUpdate(leftDivider, ochreFroglight);

                BlockPos leftChest2A = origin.offset(-5, y, bStart + 3);
                BlockPos leftChest2B = origin.offset(-5, y, bStart + 4);
                level.setBlockAndUpdate(leftChest2A, Blocks.CHEST.defaultBlockState()
                        .setValue(ChestBlock.FACING, Direction.EAST)
                        .setValue(ChestBlock.TYPE, ChestType.LEFT));
                level.setBlockAndUpdate(leftChest2B, Blocks.CHEST.defaultBlockState()
                        .setValue(ChestBlock.FACING, Direction.EAST)
                        .setValue(ChestBlock.TYPE, ChestType.RIGHT));

                // East Wall (Right side, chests facing West into aisle)
                BlockPos rightChest1A = origin.offset(5, y, bStart);
                BlockPos rightChest1B = origin.offset(5, y, bStart + 1);
                level.setBlockAndUpdate(rightChest1A, Blocks.CHEST.defaultBlockState()
                        .setValue(ChestBlock.FACING, Direction.WEST)
                        .setValue(ChestBlock.TYPE, ChestType.RIGHT));
                level.setBlockAndUpdate(rightChest1B, Blocks.CHEST.defaultBlockState()
                        .setValue(ChestBlock.FACING, Direction.WEST)
                        .setValue(ChestBlock.TYPE, ChestType.LEFT));

                // Right center glowing marker column
                BlockPos rightDivider = origin.offset(5, y, bStart + 2);
                level.setBlockAndUpdate(rightDivider, ochreFroglight);

                BlockPos rightChest2A = origin.offset(5, y, bStart + 3);
                BlockPos rightChest2B = origin.offset(5, y, bStart + 4);
                level.setBlockAndUpdate(rightChest2A, Blocks.CHEST.defaultBlockState()
                        .setValue(ChestBlock.FACING, Direction.WEST)
                        .setValue(ChestBlock.TYPE, ChestType.RIGHT));
                level.setBlockAndUpdate(rightChest2B, Blocks.CHEST.defaultBlockState()
                        .setValue(ChestBlock.FACING, Direction.WEST)
                        .setValue(ChestBlock.TYPE, ChestType.LEFT));
            }
        }

        // Horizontal dark oak beam above chests along walls
        for (int z = 0; z <= 24; z++) {
            level.setBlockAndUpdate(origin.offset(-5, 11, z), darkOakPlanks);
            level.setBlockAndUpdate(origin.offset(5, 11, z), darkOakPlanks);
        }

        // 5. Soaring Gothic Ribbed Corbel Arches & Cathedral Ceiling (Z = 0 to 24)
        for (int z = 0; z <= 24; z++) {
            boolean isArchRib = (z % 6 == 0);
            BlockState archBlock = isArchRib ? stoneBricks : darkOakPlanks;

            // Arch curve stepping inward towards apex
            level.setBlockAndUpdate(origin.offset(-5, 12, z), archBlock);
            level.setBlockAndUpdate(origin.offset(5, 12, z), archBlock);

            level.setBlockAndUpdate(origin.offset(-4, 13, z), archBlock);
            level.setBlockAndUpdate(origin.offset(4, 13, z), archBlock);

            level.setBlockAndUpdate(origin.offset(-3, 14, z), archBlock);
            level.setBlockAndUpdate(origin.offset(3, 14, z), archBlock);

            level.setBlockAndUpdate(origin.offset(-2, 15, z), archBlock);
            level.setBlockAndUpdate(origin.offset(2, 15, z), archBlock);

            level.setBlockAndUpdate(origin.offset(-1, 16, z), archBlock);
            level.setBlockAndUpdate(origin.offset(1, 16, z), archBlock);

            // Ceiling spine
            level.setBlockAndUpdate(origin.offset(0, 17, z), archBlock);
            level.setBlockAndUpdate(origin.offset(0, 18, z), stoneBricks);
        }

        // 6. Grand Hanging Glowstone Chandeliers with Chains
        int[] chandelierZ = { 6, 12, 18 };
        for (int cz : chandelierZ) {
            // Hanging iron chains from ceiling apex (Y = 17 down to 14)
            for (int cy = 14; cy <= 17; cy++) {
                level.setBlockAndUpdate(origin.offset(0, cy, cz), chain);
            }

            // Chandelier Core at Y = 13
            level.setBlockAndUpdate(origin.offset(0, 13, cz), glowstone);
            level.setBlockAndUpdate(origin.offset(1, 13, cz), darkOakFence);
            level.setBlockAndUpdate(origin.offset(-1, 13, cz), darkOakFence);
            level.setBlockAndUpdate(origin.offset(0, 13, cz + 1), darkOakFence);
            level.setBlockAndUpdate(origin.offset(0, 13, cz - 1), darkOakFence);

            // Hanging lanterns suspended below the chandelier arms at Y = 12
            level.setBlockAndUpdate(origin.offset(1, 12, cz), hangingLantern);
            level.setBlockAndUpdate(origin.offset(-1, 12, cz), hangingLantern);
            level.setBlockAndUpdate(origin.offset(0, 12, cz + 1), hangingLantern);
            level.setBlockAndUpdate(origin.offset(0, 12, cz - 1), hangingLantern);
        }

        // 7. Central Display Tables (NO ARMOR STANDS per user request)
        // Table 1 at Z = 9 to 10; Table 2 at Z = 15 to 16
        int[] tableZ = { 9, 15 };
        for (int tz : tableZ) {
            for (int dz = 0; dz <= 1; dz++) {
                for (int dx = -1; dx <= 1; dx++) {
                    BlockPos p = origin.offset(dx, 0, tz + dz);
                    level.setBlockAndUpdate(p, darkOakPlanks);

                    // Decorative planters & lanterns on top of tables
                    if (dx == 0 && dz == 0) {
                        level.setBlockAndUpdate(p.above(), potFern);
                    } else if (dx == 0 && dz == 1) {
                        level.setBlockAndUpdate(p.above(), standingLantern);
                    } else if (dx == -1) {
                        level.setBlockAndUpdate(p.above(), (tz == 9) ? goldBlock : diamondBlock);
                    } else if (dx == 1) {
                        level.setBlockAndUpdate(p.above(), potFern);
                    }
                }
            }
        }

        // 8. Elevated Mezzanine Balcony & Grand Staircase (Z = 25 to 29)
        // Central grand stone staircase leading up to Y = 4
        for (int step = 0; step <= 3; step++) {
            int sz = 25 + step;
            int sy = step;
            for (int sx = -2; sx <= 2; sx++) {
                for (int fy = 0; fy < sy; fy++) {
                    level.setBlockAndUpdate(origin.offset(sx, fy, sz), stoneBricks);
                }
                level.setBlockAndUpdate(origin.offset(sx, sy, sz),
                        stoneStairs.setValue(StairBlock.FACING, Direction.NORTH));
            }
        }

        // Balcony platform (Y = 4, Z = 28 to 29, X = -4 to 4)
        for (int bx = -4; bx <= 4; bx++) {
            for (int bz = 28; bz <= 29; bz++) {
                level.setBlockAndUpdate(origin.offset(bx, 4, bz), darkOakPlanks);
            }
        }

        // Balcony decorative railings overlooking the hall
        for (int bx = -4; bx <= 4; bx++) {
            level.setBlockAndUpdate(origin.offset(bx, 5, 28), darkOakFence);
        }
        // Balcony corner lanterns
        level.setBlockAndUpdate(origin.offset(-4, 6, 28), standingLantern);
        level.setBlockAndUpdate(origin.offset(4, 6, 28), standingLantern);

        // Rear archway under the balcony (Z = 29, X = -1 to 1, Y = 0 to 2)
        for (int rx = -1; rx <= 1; rx++) {
            for (int ry = 0; ry <= 2; ry++) {
                level.setBlockAndUpdate(origin.offset(rx, ry, 29), air);
            }
        }
        level.setBlockAndUpdate(origin.offset(0, 3, 29), hangingLantern);

        // Back wall (Z = 29, Y = 0 to 12)
        for (int bx = -halfWidth; bx <= halfWidth; bx++) {
            for (int by = 0; by <= 12; by++) {
                if (Math.abs(bx) > 1 || by > 2) {
                    level.setBlockAndUpdate(origin.offset(bx, by, 29), stoneBricks);
                }
            }
        }

        // 9. Front Entrance Grand Arch (Z = 0)
        for (int ex = -2; ex <= 2; ex++) {
            for (int ey = 0; ey <= 4; ey++) {
                level.setBlockAndUpdate(origin.offset(ex, ey, 0), air);
            }
        }
        level.setBlockAndUpdate(origin.offset(-2, 4, 0),
                stoneStairs.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.TOP));
        level.setBlockAndUpdate(origin.offset(2, 4, 0),
                stoneStairs.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.TOP));
    }

    private static BlockState getBlockState(String name, BlockState fallback) {
        try {
            Block b = BuiltInRegistries.BLOCK.getValue(Identifier.fromNamespaceAndPath("minecraft", name));
            if (b != null && b != Blocks.AIR) {
                return b.defaultBlockState();
            }
        } catch (Exception ignored) {}
        return fallback;
    }
}
