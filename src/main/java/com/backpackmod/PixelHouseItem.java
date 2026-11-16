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
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Pixel House Item:
 * Crafted from logs, planks, stone bricks and an iron ingot.
 * When placed on the ground, it instantly constructs the ultimate multi-level
 * survival base complete with crop farm, animal barn, furnace workshop,
 * central staircase, furnished interior, and stone-trimmed gabled roof.
 */
public class PixelHouseItem extends Item {
    public PixelHouseItem(Properties properties) {
        super(properties.stacksTo(1).rarity(Rarity.EPIC));
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        Player player = ctx.getPlayer();
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        BlockPos origin = ctx.getClickedPos().above();
        buildSurvivalBase(level, origin);

        level.playSound(null, origin, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.BLOCKS, 1.0f, 1.0f);
        if (player != null) {
            player.sendSystemMessage(Component.literal("§2§l[Pixel House] §aتم بناء قاعدة النجاة الأسطورية (Survival Base) بالكامل!"));
            if (!player.isCreative()) {
                ctx.getItemInHand().shrink(1);
            }
        }

        return InteractionResult.SUCCESS;
    }

    private void buildSurvivalBase(Level level, BlockPos origin) {
        BlockState oakPlanks = Blocks.OAK_PLANKS.defaultBlockState();
        BlockState strippedLog = Blocks.STRIPPED_OAK_LOG.defaultBlockState();
        BlockState stoneBricks = Blocks.STONE_BRICKS.defaultBlockState();
        BlockState stoneStairs = Blocks.STONE_BRICK_STAIRS.defaultBlockState();
        BlockState oakStairs = Blocks.OAK_STAIRS.defaultBlockState();
        BlockState oakFence = Blocks.OAK_FENCE.defaultBlockState();
        BlockState glassPane = Blocks.GLASS_PANE.defaultBlockState();
        BlockState lantern = Blocks.LANTERN.defaultBlockState();
        BlockState chest = Blocks.CHEST.defaultBlockState();
        BlockState furnace = Blocks.FURNACE.defaultBlockState().setValue(FurnaceBlock.FACING, Direction.NORTH);
        BlockState bed = getBlockState("red_bed", getBlockState("white_bed", Blocks.OAK_PLANKS.defaultBlockState()));
        BlockState craftingTable = Blocks.CRAFTING_TABLE.defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockState dirtPath = Blocks.DIRT_PATH.defaultBlockState();
        BlockState farmland = Blocks.FARMLAND.defaultBlockState();
        BlockState wheat = Blocks.WHEAT.defaultBlockState().setValue(CropBlock.AGE, 7);
        BlockState hay = Blocks.HAY_BLOCK.defaultBlockState();

        // 1. Clear Space: X: -10 to 10, Z: -8 to 12, Y: 0 to 16
        for (int x = -10; x <= 10; x++) {
            for (int z = -8; z <= 12; z++) {
                for (int y = 0; y <= 16; y++) {
                    level.setBlockAndUpdate(origin.offset(x, y, z), air);
                }
                // Level ground
                level.setBlockAndUpdate(origin.offset(x, -1, z), Blocks.GRASS_BLOCK.defaultBlockState());
            }
        }

        // 2. Front Central Pathway (X = -1 to 1, Z = -8 to 0)
        for (int x = -1; x <= 1; x++) {
            for (int z = -8; z <= 0; z++) {
                level.setBlockAndUpdate(origin.offset(x, -1, z), dirtPath);
            }
        }

        // 3. Front-Right Enclosed Crop Farm (X = 3 to 8, Z = -7 to -1)
        for (int x = 3; x <= 8; x++) {
            for (int z = -7; z <= -1; z++) {
                boolean isBorder = (x == 3 || x == 8 || z == -7 || z == -1);
                if (isBorder) {
                    level.setBlockAndUpdate(origin.offset(x, 0, z), oakFence);
                } else if (x == 5 && z == -4) {
                    level.setBlockAndUpdate(origin.offset(x, -1, z), Blocks.WATER.defaultBlockState());
                    level.setBlockAndUpdate(origin.offset(x, 0, z), Blocks.OAK_SLAB.defaultBlockState());
                } else {
                    level.setBlockAndUpdate(origin.offset(x, -1, z), farmland);
                    level.setBlockAndUpdate(origin.offset(x, 0, z), wheat);
                }
            }
        }
        level.setBlockAndUpdate(origin.offset(3, 1, -7), lantern);
        level.setBlockAndUpdate(origin.offset(8, 1, -7), lantern);

        // 4. Front-Left Animal Barn & Stables (X = -8 to -3, Z = -7 to -1)
        for (int x = -8; x <= -3; x++) {
            for (int z = -7; z <= -1; z++) {
                boolean isBorder = (x == -8 || x == -3 || z == -7 || z == -1);
                if (isBorder) {
                    level.setBlockAndUpdate(origin.offset(x, 0, z), oakFence);
                }
            }
        }
        // Barn corner pillars & canopy roof
        level.setBlockAndUpdate(origin.offset(-8, 1, -7), strippedLog);
        level.setBlockAndUpdate(origin.offset(-8, 2, -7), strippedLog);
        level.setBlockAndUpdate(origin.offset(-3, 1, -7), strippedLog);
        level.setBlockAndUpdate(origin.offset(-3, 2, -7), strippedLog);
        for (int x = -8; x <= -3; x++) {
            for (int z = -7; z <= -1; z++) {
                level.setBlockAndUpdate(origin.offset(x, 3, z), Blocks.OAK_SLAB.defaultBlockState());
            }
        }
        level.setBlockAndUpdate(origin.offset(-6, 0, -4), hay);
        level.setBlockAndUpdate(origin.offset(-5, 0, -4), hay);

        // 5. Undercroft Furnace Workshop (Right side underneath terrace, X = 2 to 9, Z = 1 to 8)
        for (int x = 2; x <= 9; x++) {
            for (int z = 1; z <= 8; z++) {
                level.setBlockAndUpdate(origin.offset(x, -1, z), stoneBricks);
            }
        }
        // Bank of furnaces along the back wall of workshop (Z = 8, X = 3 to 8, Y = 0 to 1)
        for (int x = 3; x <= 8; x++) {
            for (int y = 0; y <= 1; y++) {
                level.setBlockAndUpdate(origin.offset(x, y, 8), furnace);
            }
        }
        level.setBlockAndUpdate(origin.offset(2, 0, 7), chest);
        level.setBlockAndUpdate(origin.offset(9, 0, 7), Blocks.BARREL.defaultBlockState());

        // 6. Central Grand Wooden Staircase (X = -1 to 1, Z = 1 to 5)
        for (int step = 0; step <= 3; step++) {
            int z = 1 + step;
            int y = step;
            for (int x = -1; x <= 1; x++) {
                for (int fy = 0; fy < y; fy++) {
                    level.setBlockAndUpdate(origin.offset(x, fy, z), oakPlanks);
                }
                level.setBlockAndUpdate(origin.offset(x, y, z), oakStairs.setValue(StairBlock.FACING, Direction.NORTH));
            }
        }

        // 7. Second Floor Veranda Deck / Terrace (Y = 4, X = -9 to 9, Z = 5 to 11)
        for (int x = -9; x <= 9; x++) {
            for (int z = 5; z <= 11; z++) {
                level.setBlockAndUpdate(origin.offset(x, 4, z), oakPlanks);
            }
        }
        // Terrace protective railings
        for (int x = -9; x <= 9; x++) {
            level.setBlockAndUpdate(origin.offset(x, 5, 5), oakFence);
            level.setBlockAndUpdate(origin.offset(x, 5, 11), oakFence);
        }
        for (int z = 5; z <= 11; z++) {
            level.setBlockAndUpdate(origin.offset(-9, 5, z), oakFence);
            level.setBlockAndUpdate(origin.offset(9, 5, z), oakFence);
        }
        // Open gap for central stairs entrance
        level.setBlockAndUpdate(origin.offset(-1, 5, 5), air);
        level.setBlockAndUpdate(origin.offset(0, 5, 5), air);
        level.setBlockAndUpdate(origin.offset(1, 5, 5), air);

        // Terrace decorative corner lanterns
        level.setBlockAndUpdate(origin.offset(-9, 6, 5), lantern);
        level.setBlockAndUpdate(origin.offset(9, 6, 5), lantern);
        level.setBlockAndUpdate(origin.offset(-2, 6, 5), lantern);
        level.setBlockAndUpdate(origin.offset(2, 6, 5), lantern);

        // 8. Main House Structure (X = -5 to 5, Z = 7 to 11, Y = 5 to 8)
        for (int x = -5; x <= 5; x++) {
            for (int z = 7; z <= 11; z++) {
                for (int y = 5; y <= 8; y++) {
                    boolean isWall = (x == -5 || x == 5 || z == 7 || z == 11);
                    if (isWall) {
                        boolean isLog = (Math.abs(x) == 5 || Math.abs(x) == 2);
                        level.setBlockAndUpdate(origin.offset(x, y, z), isLog ? strippedLog : oakPlanks);
                    } else {
                        level.setBlockAndUpdate(origin.offset(x, y, z), air);
                    }
                }
            }
        }

        // Windows & Doors
        level.setBlockAndUpdate(origin.offset(0, 5, 7), air); // Doorway
        level.setBlockAndUpdate(origin.offset(0, 6, 7), air);
        level.setBlockAndUpdate(origin.offset(-3, 6, 7), glassPane);
        level.setBlockAndUpdate(origin.offset(3, 6, 7), glassPane);

        // Furnish Interior
        level.setBlockAndUpdate(origin.offset(-4, 5, 10), bed);
        level.setBlockAndUpdate(origin.offset(-3, 5, 10), craftingTable);
        level.setBlockAndUpdate(origin.offset(4, 5, 10), chest);
        level.setBlockAndUpdate(origin.offset(0, 7, 9), lantern);

        // 9. Third Floor & Stone-Trimmed Gabled Roof (Y = 9 to 13)
        // Roof runs along X = -6 to 6, pitch ascends towards center (Z = 7 to 11)
        for (int x = -6; x <= 6; x++) {
            boolean isEdge = (Math.abs(x) == 6);
            BlockState stairMat = isEdge ? stoneStairs : oakStairs;
            BlockState plankMat = isEdge ? stoneBricks : oakPlanks;

            // Pitch 1 (Y = 9)
            level.setBlockAndUpdate(origin.offset(x, 9, 7), stairMat.setValue(StairBlock.FACING, Direction.SOUTH));
            level.setBlockAndUpdate(origin.offset(x, 9, 11), stairMat.setValue(StairBlock.FACING, Direction.NORTH));

            // Pitch 2 (Y = 10)
            level.setBlockAndUpdate(origin.offset(x, 10, 8), stairMat.setValue(StairBlock.FACING, Direction.SOUTH));
            level.setBlockAndUpdate(origin.offset(x, 10, 10), stairMat.setValue(StairBlock.FACING, Direction.NORTH));

            // Peak Ridge (Y = 11, Z = 9)
            level.setBlockAndUpdate(origin.offset(x, 11, 9), plankMat);
            level.setBlockAndUpdate(origin.offset(x, 12, 9), isEdge ? Blocks.STONE_BRICK_SLAB.defaultBlockState() : Blocks.OAK_SLAB.defaultBlockState());
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
