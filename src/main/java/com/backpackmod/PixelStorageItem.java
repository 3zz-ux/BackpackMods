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
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;

/**
 * Pixel Storage Item:
 * Crafted from 9 chests. Instantly constructs the grand Gothic cathedral stone-brick storage vault:
 * - Completely enclosed weather-proof exterior with fully covered stone-trimmed roof and no gaps
 * - Grand Gothic entrance porch/canopy ("الدمّة") with double dark-oak doors, archway, and lanterns
 * - Completely sealed rear wall with glowing stained-glass decorative cross window
 * - 4 grand bays of double chests (11 rows high) with glowing Ochre Froglight center category columns
 * - Cathedral corbel arches, chandeliers, runner carpet, and display counters
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
            player.sendSystemMessage(Component.literal("§6§l[Pixel Storage] §aتم بناء قاعة التخزين القوطية الملكية بالكامل مع الدمّة والسقف المتناسق!"));
            if (!player.isCreative()) {
                ctx.getItemInHand().shrink(1);
            }
        }

        return InteractionResult.SUCCESS;
    }

    private void buildStorageVault(Level level, BlockPos origin) {
        int halfWidth = 6;  // X: -6 to +6 (13 blocks wide)
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
        BlockState darkOakDoor = Blocks.DARK_OAK_DOOR.defaultBlockState();
        BlockState ironBars = Blocks.IRON_BARS.defaultBlockState();
        BlockState tintedGlass = getBlockState("tinted_glass", Blocks.GLASS.defaultBlockState());

        // 1. Solid Foundation (Y = -3 to -1) & Clear Interior (Y = 0 to 18)
        for (int x = -halfWidth - 1; x <= halfWidth + 1; x++) {
            for (int z = -3; z <= length + 1; z++) {
                // Solid stone foundation below ground
                for (int fy = -3; fy <= -1; fy++) {
                    level.setBlockAndUpdate(origin.offset(x, fy, z), stoneBricks);
                }
                // Clear interior
                for (int y = 0; y <= height + 1; y++) {
                    level.setBlockAndUpdate(origin.offset(x, y, z), air);
                }
                // Main floor pattern at Y = -1
                if (z >= 0 && z <= length) {
                    if (Math.abs(x) == 2 && z % 6 == 0) {
                        level.setBlockAndUpdate(origin.offset(x, -1, z), chiseledBricks);
                    } else if (Math.abs(x) == 2) {
                        level.setBlockAndUpdate(origin.offset(x, -1, z), smoothStone);
                    }
                }
            }
        }

        // 2. Central Runner Carpet (Z = 2 to 24)
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

        // 3. Side Walls (X = -6 and X = 6) - Fully sealed up to roof line
        for (int z = 0; z <= length; z++) {
            for (int y = 0; y <= 13; y++) {
                level.setBlockAndUpdate(origin.offset(-halfWidth, y, z), stoneBricks);
                level.setBlockAndUpdate(origin.offset(halfWidth, y, z), stoneBricks);
            }
        }

        // 4. Pillars & Chest Bays along Z = 0 to 24
        int[] pillarZ = { 0, 6, 12, 18, 24 };
        for (int pz : pillarZ) {
            for (int y = 0; y <= 12; y++) {
                BlockState pState = (y == 0 || y == 4 || y == 8 || y == 12) ? chiseledBricks : stoneBricks;
                level.setBlockAndUpdate(origin.offset(-5, y, pz), pState);
                level.setBlockAndUpdate(origin.offset(5, y, pz), pState);
            }

            // Pillar Plinths, Potted Ferns, and Torches
            if (pz > 0 && pz < 24) {
                level.setBlockAndUpdate(origin.offset(-4, 0, pz),
                        stoneStairs.setValue(StairBlock.FACING, Direction.EAST));
                level.setBlockAndUpdate(origin.offset(-4, 1, pz), potFern);
                level.setBlockAndUpdate(origin.offset(-4, 3, pz),
                        Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.EAST));
                level.setBlockAndUpdate(origin.offset(-4, 7, pz), standingLantern);

                level.setBlockAndUpdate(origin.offset(4, 0, pz),
                        stoneStairs.setValue(StairBlock.FACING, Direction.WEST));
                level.setBlockAndUpdate(origin.offset(4, 1, pz), potFern);
                level.setBlockAndUpdate(origin.offset(4, 3, pz),
                        Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, Direction.WEST));
                level.setBlockAndUpdate(origin.offset(4, 7, pz), standingLantern);
            }
        }

        // 4 Bays of Double Chests with Glowing Ochre Froglight Dividers
        int[] bayStarts = { 1, 7, 13, 19 };
        for (int bStart : bayStarts) {
            for (int y = 0; y <= 10; y++) {
                // West Wall (Left side, chests facing East into aisle)
                level.setBlockAndUpdate(origin.offset(-5, y, bStart), Blocks.CHEST.defaultBlockState()
                        .setValue(ChestBlock.FACING, Direction.EAST).setValue(ChestBlock.TYPE, ChestType.LEFT));
                level.setBlockAndUpdate(origin.offset(-5, y, bStart + 1), Blocks.CHEST.defaultBlockState()
                        .setValue(ChestBlock.FACING, Direction.EAST).setValue(ChestBlock.TYPE, ChestType.RIGHT));

                level.setBlockAndUpdate(origin.offset(-5, y, bStart + 2), ochreFroglight);

                level.setBlockAndUpdate(origin.offset(-5, y, bStart + 3), Blocks.CHEST.defaultBlockState()
                        .setValue(ChestBlock.FACING, Direction.EAST).setValue(ChestBlock.TYPE, ChestType.LEFT));
                level.setBlockAndUpdate(origin.offset(-5, y, bStart + 4), Blocks.CHEST.defaultBlockState()
                        .setValue(ChestBlock.FACING, Direction.EAST).setValue(ChestBlock.TYPE, ChestType.RIGHT));

                // East Wall (Right side, chests facing West into aisle)
                level.setBlockAndUpdate(origin.offset(5, y, bStart), Blocks.CHEST.defaultBlockState()
                        .setValue(ChestBlock.FACING, Direction.WEST).setValue(ChestBlock.TYPE, ChestType.RIGHT));
                level.setBlockAndUpdate(origin.offset(5, y, bStart + 1), Blocks.CHEST.defaultBlockState()
                        .setValue(ChestBlock.FACING, Direction.WEST).setValue(ChestBlock.TYPE, ChestType.LEFT));

                level.setBlockAndUpdate(origin.offset(5, y, bStart + 2), ochreFroglight);

                level.setBlockAndUpdate(origin.offset(5, y, bStart + 3), Blocks.CHEST.defaultBlockState()
                        .setValue(ChestBlock.FACING, Direction.WEST).setValue(ChestBlock.TYPE, ChestType.RIGHT));
                level.setBlockAndUpdate(origin.offset(5, y, bStart + 4), Blocks.CHEST.defaultBlockState()
                        .setValue(ChestBlock.FACING, Direction.WEST).setValue(ChestBlock.TYPE, ChestType.LEFT));
            }
        }

        // Horizontal dark oak beam above chests along walls
        for (int z = 0; z <= 24; z++) {
            level.setBlockAndUpdate(origin.offset(-5, 11, z), darkOakPlanks);
            level.setBlockAndUpdate(origin.offset(5, 11, z), darkOakPlanks);
        }

        // 5. Gothic Ribbed Corbel Arches & Fully Sealed Exterior Roof (Z = -2 to 30)
        for (int z = -2; z <= 30; z++) {
            boolean isArchRib = (z % 6 == 0) || (z == -2) || (z == 0) || (z == length);
            BlockState ribMat = isArchRib ? stoneBricks : darkOakPlanks;

            // Fill side gap so no open space between side walls and roof
            level.setBlockAndUpdate(origin.offset(-6, 13, z), stoneBricks);
            level.setBlockAndUpdate(origin.offset(6, 13, z), stoneBricks);

            // Stepped roof layers
            level.setBlockAndUpdate(origin.offset(-5, 13, z), ribMat);
            level.setBlockAndUpdate(origin.offset(5, 13, z), ribMat);

            level.setBlockAndUpdate(origin.offset(-4, 14, z), ribMat);
            level.setBlockAndUpdate(origin.offset(4, 14, z), ribMat);

            level.setBlockAndUpdate(origin.offset(-3, 15, z), ribMat);
            level.setBlockAndUpdate(origin.offset(3, 15, z), ribMat);

            level.setBlockAndUpdate(origin.offset(-2, 16, z), ribMat);
            level.setBlockAndUpdate(origin.offset(2, 16, z), ribMat);

            level.setBlockAndUpdate(origin.offset(-1, 17, z), ribMat);
            level.setBlockAndUpdate(origin.offset(1, 17, z), ribMat);

            // Roof Apex Spine
            level.setBlockAndUpdate(origin.offset(0, 18, z), ribMat);
            level.setBlockAndUpdate(origin.offset(0, 19, z), isArchRib ? stoneBricks : darkOakPlanks);
        }

        // 6. Grand Hanging Glowstone Chandeliers with Chains
        int[] chandelierZ = { 6, 12, 18 };
        for (int cz : chandelierZ) {
            for (int cy = 14; cy <= 18; cy++) {
                level.setBlockAndUpdate(origin.offset(0, cy, cz), chain);
            }
            level.setBlockAndUpdate(origin.offset(0, 13, cz), glowstone);
            level.setBlockAndUpdate(origin.offset(1, 13, cz), darkOakFence);
            level.setBlockAndUpdate(origin.offset(-1, 13, cz), darkOakFence);
            level.setBlockAndUpdate(origin.offset(0, 13, cz + 1), darkOakFence);
            level.setBlockAndUpdate(origin.offset(0, 13, cz - 1), darkOakFence);

            level.setBlockAndUpdate(origin.offset(1, 12, cz), hangingLantern);
            level.setBlockAndUpdate(origin.offset(-1, 12, cz), hangingLantern);
            level.setBlockAndUpdate(origin.offset(0, 12, cz + 1), hangingLantern);
            level.setBlockAndUpdate(origin.offset(0, 12, cz - 1), hangingLantern);
        }

        // 7. Central Display Tables (NO ARMOR STANDS per user request)
        int[] tableZ = { 9, 15 };
        for (int tz : tableZ) {
            for (int dz = 0; dz <= 1; dz++) {
                for (int dx = -1; dx <= 1; dx++) {
                    BlockPos p = origin.offset(dx, 0, tz + dz);
                    level.setBlockAndUpdate(p, darkOakPlanks);
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
        for (int bx = -4; bx <= 4; bx++) {
            for (int bz = 28; bz <= 29; bz++) {
                level.setBlockAndUpdate(origin.offset(bx, 4, bz), darkOakPlanks);
            }
        }
        for (int bx = -4; bx <= 4; bx++) {
            level.setBlockAndUpdate(origin.offset(bx, 5, 28), darkOakFence);
        }
        level.setBlockAndUpdate(origin.offset(-4, 6, 28), standingLantern);
        level.setBlockAndUpdate(origin.offset(4, 6, 28), standingLantern);

        // 9. COMPLETELY SEALED REAR WALL (Z = 30) - Full Gothic Gable with Stained Glass Cross
        for (int bx = -halfWidth; bx <= halfWidth; bx++) {
            for (int by = 0; by <= 18; by++) {
                // Gable slope limits
                int maxGableY = 18 - Math.abs(bx);
                if (by <= maxGableY) {
                    level.setBlockAndUpdate(origin.offset(bx, by, 30), stoneBricks);
                }
            }
        }
        // Glowing decorative stained-glass cross window on the back wall above balcony
        for (int wy = 7; wy <= 13; wy++) {
            level.setBlockAndUpdate(origin.offset(0, wy, 30), tintedGlass);
            level.setBlockAndUpdate(origin.offset(0, wy, 31), ochreFroglight); // back-lighting
        }
        for (int wx = -2; wx <= 2; wx++) {
            level.setBlockAndUpdate(origin.offset(wx, 11, 30), tintedGlass);
            level.setBlockAndUpdate(origin.offset(wx, 11, 31), ochreFroglight);
        }

        // 10. FRONT ENTRANCE FACADE & GRAND PORCH ("الدمّة") (Z = -2 to 0)
        // Complete front wall closing the gable at Z = 0
        for (int bx = -halfWidth; bx <= halfWidth; bx++) {
            for (int by = 0; by <= 18; by++) {
                int maxGableY = 18 - Math.abs(bx);
                if (by <= maxGableY) {
                    level.setBlockAndUpdate(origin.offset(bx, by, 0), stoneBricks);
                }
            }
        }

        // Clear entrance portal opening at Z = 0
        for (int ex = -1; ex <= 0; ex++) {
            for (int ey = 0; ey <= 2; ey++) {
                level.setBlockAndUpdate(origin.offset(ex, ey, 0), air);
            }
        }
        // Double Dark Oak Doors at Z = 0
        level.setBlockAndUpdate(origin.offset(-1, 0, 0), darkOakDoor
                .setValue(DoorBlock.FACING, Direction.SOUTH)
                .setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER)
                .setValue(DoorBlock.HINGE, DoorHingeSide.RIGHT));
        level.setBlockAndUpdate(origin.offset(-1, 1, 0), darkOakDoor
                .setValue(DoorBlock.FACING, Direction.SOUTH)
                .setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER)
                .setValue(DoorBlock.HINGE, DoorHingeSide.RIGHT));

        level.setBlockAndUpdate(origin.offset(0, 0, 0), darkOakDoor
                .setValue(DoorBlock.FACING, Direction.SOUTH)
                .setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER)
                .setValue(DoorBlock.HINGE, DoorHingeSide.LEFT));
        level.setBlockAndUpdate(origin.offset(0, 1, 0), darkOakDoor
                .setValue(DoorBlock.FACING, Direction.SOUTH)
                .setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER)
                .setValue(DoorBlock.HINGE, DoorHingeSide.LEFT));

        // Decorative Gothic transom window above door (Y = 3 to 5)
        level.setBlockAndUpdate(origin.offset(-1, 3, 0), ironBars);
        level.setBlockAndUpdate(origin.offset(0, 3, 0), ironBars);
        level.setBlockAndUpdate(origin.offset(-1, 4, 0), stoneStairs.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.TOP));
        level.setBlockAndUpdate(origin.offset(0, 4, 0), stoneStairs.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.TOP));

        // Grand Entrance Porch Canopy ("الدمّة") extending forward to Z = -1 and -2
        // Support pillars at X = -3 and X = 2 (Z = -2)
        for (int py = 0; py <= 4; py++) {
            level.setBlockAndUpdate(origin.offset(-3, py, -2), (py == 0 || py == 4) ? chiseledBricks : stoneBricks);
            level.setBlockAndUpdate(origin.offset(2, py, -2), (py == 0 || py == 4) ? chiseledBricks : stoneBricks);
        }
        // Lanterns on entrance pillars
        level.setBlockAndUpdate(origin.offset(-3, 5, -2), standingLantern);
        level.setBlockAndUpdate(origin.offset(2, 5, -2), standingLantern);

        // Porch Arch & Canopy Roof (Z = -2 to -1)
        for (int pz = -2; pz <= -1; pz++) {
            for (int px = -3; px <= 2; px++) {
                level.setBlockAndUpdate(origin.offset(px, 5, pz), darkOakPlanks);
            }
            level.setBlockAndUpdate(origin.offset(-3, 6, pz), darkOakStairs.setValue(StairBlock.FACING, Direction.EAST));
            level.setBlockAndUpdate(origin.offset(-2, 6, pz), stoneBricks);
            level.setBlockAndUpdate(origin.offset(-1, 6, pz), stoneStairs.setValue(StairBlock.FACING, Direction.EAST));
            level.setBlockAndUpdate(origin.offset(0, 6, pz), stoneStairs.setValue(StairBlock.FACING, Direction.WEST));
            level.setBlockAndUpdate(origin.offset(1, 6, pz), stoneBricks);
            level.setBlockAndUpdate(origin.offset(2, 6, pz), darkOakStairs.setValue(StairBlock.FACING, Direction.WEST));
        }
        // Hanging Lantern in center of porch canopy
        level.setBlockAndUpdate(origin.offset(-1, 4, -1), hangingLantern);
        level.setBlockAndUpdate(origin.offset(0, 4, -1), hangingLantern);

        // Entrance stone steps leading up to door (Z = -3 to -1)
        for (int sx = -2; sx <= 1; sx++) {
            level.setBlockAndUpdate(origin.offset(sx, -1, -1), smoothStone);
            level.setBlockAndUpdate(origin.offset(sx, -1, -2), smoothStone);
            level.setBlockAndUpdate(origin.offset(sx, -1, -3),
                    stoneStairs.setValue(StairBlock.FACING, Direction.NORTH));
        }
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
