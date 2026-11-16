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
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;

/**
 * Pixel House Item:
 * Crafted from logs, planks, stone bricks and an iron ingot.
 * Instantly constructs the exact Multi-Level Survival Base matching the reference photo:
 * - Solid stone foundation (Y = -4 to -1) ensuring it NEVER floats on any terrain
 * - Integrated central grand staircase connecting the garden path directly to the upper veranda
 * - Front-Right: Enclosed crop farm with irrigated mature wheat and potatoes
 * - Front-Left: Enclosed animal paddock with oak pergola canopy, hay bales, and live animals
 * - Undercroft Workshop (right side under terrace): Stone floor, bank of furnaces/smokers, chests & barrels
 * - Upper Terrace (Level 2): Wraps around the house with fenced sheep farm (right) and cow farm (left)
 * - Main 2-Story Survival House: Stripped oak framework, fully furnished interior, front balcony,
 *   and stone-trimmed gabled roof with central cross-gable.
 * - Live Sheep and Cows spawned directly in their dedicated farms!
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
            player.sendSystemMessage(Component.literal("§2§l[Pixel House] §aتم بناء قاعدة النجاة الأسطورية (Survival Base) بالكامل مع حظائر الأبقار والخراف والمزارع!"));
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
        BlockState cobblestone = Blocks.COBBLESTONE.defaultBlockState();
        BlockState stoneStairs = Blocks.STONE_BRICK_STAIRS.defaultBlockState();
        BlockState oakStairs = Blocks.OAK_STAIRS.defaultBlockState();
        BlockState oakFence = Blocks.OAK_FENCE.defaultBlockState();
        BlockState oakSlab = Blocks.OAK_SLAB.defaultBlockState();
        BlockState stoneSlab = Blocks.STONE_BRICK_SLAB.defaultBlockState();
        BlockState oakLeaves = Blocks.OAK_LEAVES.defaultBlockState();
        BlockState glassPane = Blocks.GLASS_PANE.defaultBlockState();
        BlockState lantern = Blocks.LANTERN.defaultBlockState();
        BlockState hangingLantern = Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true);
        BlockState chest = Blocks.CHEST.defaultBlockState();
        BlockState barrel = Blocks.BARREL.defaultBlockState();
        BlockState furnace = Blocks.FURNACE.defaultBlockState().setValue(FurnaceBlock.FACING, Direction.NORTH);
        BlockState blastFurnace = Blocks.BLAST_FURNACE.defaultBlockState().setValue(FurnaceBlock.FACING, Direction.NORTH);
        BlockState smoker = Blocks.SMOKER.defaultBlockState().setValue(FurnaceBlock.FACING, Direction.NORTH);
        BlockState craftingTable = Blocks.CRAFTING_TABLE.defaultBlockState();
        BlockState anvil = Blocks.ANVIL.defaultBlockState();
        BlockState smithingTable = Blocks.SMITHING_TABLE.defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockState dirtPath = Blocks.DIRT_PATH.defaultBlockState();
        BlockState farmland = Blocks.FARMLAND.defaultBlockState();
        BlockState wheat = Blocks.WHEAT.defaultBlockState().setValue(CropBlock.AGE, 7);
        BlockState potatoes = Blocks.POTATOES.defaultBlockState().setValue(CropBlock.AGE, 7);
        BlockState hay = Blocks.HAY_BLOCK.defaultBlockState();
        BlockState redCarpet = getBlockState("red_carpet", Blocks.STONE_BRICKS.defaultBlockState());
        BlockState oakDoor = Blocks.OAK_DOOR.defaultBlockState();
        BlockState bookshelves = Blocks.BOOKSHELF.defaultBlockState();
        BlockState potFern = Blocks.POTTED_FERN.defaultBlockState();

        int minX = -11, maxX = 11;
        int minZ = -10, maxZ = 15;

        // 1. DEEP SOLID FOUNDATION (Y = -4 to -1) - Eliminates floating house completely
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                // Clear upper space
                for (int y = 0; y <= 18; y++) {
                    level.setBlockAndUpdate(origin.offset(x, y, z), air);
                }
                // Solid deep stone layers
                for (int fy = -4; fy <= -2; fy++) {
                    level.setBlockAndUpdate(origin.offset(x, fy, z), (Math.abs(x * z) % 3 == 0) ? cobblestone : stoneBricks);
                }
                // Top foundation layer at Y = -1
                if (z >= 1 && z <= 14) {
                    level.setBlockAndUpdate(origin.offset(x, -1, z), stoneBricks);
                } else {
                    level.setBlockAndUpdate(origin.offset(x, -1, z), Blocks.GRASS_BLOCK.defaultBlockState());
                }
            }
        }

        // 2. Front Central Pathway (X = -1 to 1, Z = -10 to 0)
        for (int x = -1; x <= 1; x++) {
            for (int z = -10; z <= 0; z++) {
                level.setBlockAndUpdate(origin.offset(x, -1, z), dirtPath);
            }
        }

        // 3. Perimeter Leaves & Fence Border (Z = -10, X = -10 to 10)
        for (int x = -10; x <= 10; x++) {
            if (x < -1 || x > 1) {
                level.setBlockAndUpdate(origin.offset(x, 0, -10), oakLeaves);
                level.setBlockAndUpdate(origin.offset(x, 1, -10), oakFence);
            }
        }
        for (int z = -10; z <= 0; z++) {
            level.setBlockAndUpdate(origin.offset(-10, 0, z), oakLeaves);
            level.setBlockAndUpdate(origin.offset(10, 0, z), oakLeaves);
            level.setBlockAndUpdate(origin.offset(-10, 1, z), oakFence);
            level.setBlockAndUpdate(origin.offset(10, 1, z), oakFence);
        }
        // Front Gateway Posts with Lanterns
        level.setBlockAndUpdate(origin.offset(-2, 0, -10), strippedLog);
        level.setBlockAndUpdate(origin.offset(-2, 1, -10), strippedLog);
        level.setBlockAndUpdate(origin.offset(-2, 2, -10), lantern);

        level.setBlockAndUpdate(origin.offset(2, 0, -10), strippedLog);
        level.setBlockAndUpdate(origin.offset(2, 1, -10), strippedLog);
        level.setBlockAndUpdate(origin.offset(2, 2, -10), lantern);

        // 4. FRONT-RIGHT ENCLOSED CROP FARM (X = 3 to 9, Z = -9 to -1)
        for (int x = 3; x <= 9; x++) {
            for (int z = -9; z <= -1; z++) {
                boolean isBorder = (x == 3 || x == 9 || z == -9 || z == -1);
                if (isBorder) {
                    level.setBlockAndUpdate(origin.offset(x, 0, z), oakFence);
                } else if (x == 6 && z == -5) {
                    level.setBlockAndUpdate(origin.offset(x, -1, z), Blocks.WATER.defaultBlockState());
                    level.setBlockAndUpdate(origin.offset(x, 0, z), oakSlab);
                    level.setBlockAndUpdate(origin.offset(x, 1, z), lantern);
                } else {
                    level.setBlockAndUpdate(origin.offset(x, -1, z), farmland);
                    level.setBlockAndUpdate(origin.offset(x, 0, z), (x % 2 == 0) ? potatoes : wheat);
                }
            }
        }
        level.setBlockAndUpdate(origin.offset(3, 0, -9), strippedLog);
        level.setBlockAndUpdate(origin.offset(3, 1, -9), lantern);
        level.setBlockAndUpdate(origin.offset(9, 0, -9), strippedLog);
        level.setBlockAndUpdate(origin.offset(9, 1, -9), lantern);

        // 5. FRONT-LEFT ENCLOSED ANIMAL PADDOCK WITH PERGOLA CANOPY (X = -9 to -3, Z = -9 to -1)
        for (int x = -9; x <= -3; x++) {
            for (int z = -9; z <= -1; z++) {
                boolean isBorder = (x == -9 || x == -3 || z == -9 || z == -1);
                if (isBorder) {
                    level.setBlockAndUpdate(origin.offset(x, 0, z), oakFence);
                }
            }
        }
        // Corner Log Posts & Pergola Roof (X = -9 to -3, Z = -9 to -1)
        int[] paddockCornerX = { -9, -3 };
        int[] paddockCornerZ = { -9, -1 };
        for (int cx : paddockCornerX) {
            for (int cz : paddockCornerZ) {
                level.setBlockAndUpdate(origin.offset(cx, 0, cz), strippedLog);
                level.setBlockAndUpdate(origin.offset(cx, 1, cz), strippedLog);
                level.setBlockAndUpdate(origin.offset(cx, 2, cz), strippedLog);
            }
        }
        for (int x = -9; x <= -3; x++) {
            for (int z = -9; z <= -1; z++) {
                level.setBlockAndUpdate(origin.offset(x, 3, z), oakSlab);
            }
        }
        level.setBlockAndUpdate(origin.offset(-6, 0, -5), hay);
        level.setBlockAndUpdate(origin.offset(-5, 0, -5), hay);
        level.setBlockAndUpdate(origin.offset(-6, 2, -5), hangingLantern);

        // 6. UNDERCROFT WORKSHOP (Right side directly under terrace, X = 3 to 10, Z = 1 to 8)
        for (int x = 3; x <= 10; x++) {
            for (int z = 1; z <= 8; z++) {
                level.setBlockAndUpdate(origin.offset(x, -1, z), stoneBricks);
            }
        }
        // Bank of furnaces and crafting stations against back wall (Z = 8, X = 3 to 9)
        level.setBlockAndUpdate(origin.offset(3, 0, 8), furnace);
        level.setBlockAndUpdate(origin.offset(4, 0, 8), furnace);
        level.setBlockAndUpdate(origin.offset(5, 0, 8), blastFurnace);
        level.setBlockAndUpdate(origin.offset(6, 0, 8), smoker);
        level.setBlockAndUpdate(origin.offset(7, 0, 8), furnace);
        level.setBlockAndUpdate(origin.offset(8, 0, 8), furnace);
        level.setBlockAndUpdate(origin.offset(9, 0, 8), craftingTable);

        // Second row of furnaces
        level.setBlockAndUpdate(origin.offset(3, 1, 8), furnace);
        level.setBlockAndUpdate(origin.offset(4, 1, 8), furnace);
        level.setBlockAndUpdate(origin.offset(5, 1, 8), blastFurnace);
        level.setBlockAndUpdate(origin.offset(6, 1, 8), smoker);
        level.setBlockAndUpdate(origin.offset(7, 1, 8), furnace);
        level.setBlockAndUpdate(origin.offset(8, 1, 8), furnace);

        // Storage chests, barrels, anvil
        level.setBlockAndUpdate(origin.offset(3, 0, 7), chest);
        level.setBlockAndUpdate(origin.offset(3, 0, 6), chest);
        level.setBlockAndUpdate(origin.offset(10, 0, 7), barrel);
        level.setBlockAndUpdate(origin.offset(10, 0, 6), barrel);
        level.setBlockAndUpdate(origin.offset(9, 0, 5), anvil);
        level.setBlockAndUpdate(origin.offset(9, 0, 4), smithingTable);

        // Workshop pergola canopy roof overhang (Z = 1 to 4, Y = 3)
        for (int x = 3; x <= 10; x++) {
            for (int z = 1; z <= 4; z++) {
                level.setBlockAndUpdate(origin.offset(x, 3, z), oakSlab);
            }
        }
        level.setBlockAndUpdate(origin.offset(3, 2, 2), hangingLantern);
        level.setBlockAndUpdate(origin.offset(10, 2, 2), hangingLantern);

        // 7. INTEGRATED CENTRAL GRAND STAIRCASE (X = -1 to 1, Z = 0 to 5)
        // Stairs lead UP towards North (into the house)
        for (int step = 0; step <= 4; step++) {
            int z = step;
            int y = step;
            for (int x = -1; x <= 1; x++) {
                // Solid oak under stair steps
                for (int fy = 0; fy < y; fy++) {
                    level.setBlockAndUpdate(origin.offset(x, fy, z), oakPlanks);
                }
                // Step facing South (so player ascending from South walks up)
                level.setBlockAndUpdate(origin.offset(x, y, z), oakStairs.setValue(StairBlock.FACING, Direction.SOUTH));
            }
            // Side Banisters with stripped logs and lanterns
            level.setBlockAndUpdate(origin.offset(-2, y, z), strippedLog);
            level.setBlockAndUpdate(origin.offset(2, y, z), strippedLog);
        }
        level.setBlockAndUpdate(origin.offset(-2, 5, 4), lantern);
        level.setBlockAndUpdate(origin.offset(2, 5, 4), lantern);

        // 8. UPPER LEVEL TERRACE DECK (Y = 4, X = -10 to 10, Z = 5 to 14)
        for (int x = -10; x <= 10; x++) {
            for (int z = 5; z <= 14; z++) {
                level.setBlockAndUpdate(origin.offset(x, 4, z), oakPlanks);
            }
        }
        // Terrace protective oak fences
        for (int x = -10; x <= 10; x++) {
            if (x < -1 || x > 1) {
                level.setBlockAndUpdate(origin.offset(x, 5, 5), oakFence);
            }
            level.setBlockAndUpdate(origin.offset(x, 5, 14), oakFence);
        }
        for (int z = 5; z <= 14; z++) {
            level.setBlockAndUpdate(origin.offset(-10, 5, z), oakFence);
            level.setBlockAndUpdate(origin.offset(10, 5, z), oakFence);
        }
        // Terrace corner lantern posts
        level.setBlockAndUpdate(origin.offset(-10, 5, 5), strippedLog);
        level.setBlockAndUpdate(origin.offset(-10, 6, 5), lantern);
        level.setBlockAndUpdate(origin.offset(10, 5, 5), strippedLog);
        level.setBlockAndUpdate(origin.offset(10, 6, 5), lantern);

        // 9. UPPER LEFT DECK: COW FARM (X = -10 to -6, Z = 6 to 13)
        for (int x = -10; x <= -6; x++) {
            for (int z = 6; z <= 13; z++) {
                if (x == -6) {
                    level.setBlockAndUpdate(origin.offset(x, 5, z), oakFence);
                }
            }
        }
        // Pergola roof over cow farm
        for (int x = -10; x <= -6; x++) {
            for (int z = 6; z <= 13; z++) {
                level.setBlockAndUpdate(origin.offset(x, 8, z), oakSlab);
            }
        }
        level.setBlockAndUpdate(origin.offset(-8, 5, 8), hay);
        level.setBlockAndUpdate(origin.offset(-8, 5, 9), hay);
        level.setBlockAndUpdate(origin.offset(-8, 7, 8), hangingLantern);

        // 10. UPPER RIGHT DECK: SHEEP FARM (X = 6 to 10, Z = 6 to 13)
        for (int x = 6; x <= 10; x++) {
            for (int z = 6; z <= 13; z++) {
                if (x == 6) {
                    level.setBlockAndUpdate(origin.offset(x, 5, z), oakFence);
                }
            }
        }
        // Pergola roof over sheep farm
        for (int x = 6; x <= 10; x++) {
            for (int z = 6; z <= 13; z++) {
                level.setBlockAndUpdate(origin.offset(x, 8, z), oakSlab);
            }
        }
        level.setBlockAndUpdate(origin.offset(8, 5, 8), hay);
        level.setBlockAndUpdate(origin.offset(8, 5, 9), hay);
        level.setBlockAndUpdate(origin.offset(8, 7, 8), hangingLantern);

        // 11. MAIN TWO-STORY SURVIVAL HOUSE (X = -5 to 5, Z = 6 to 13)
        // Level 1 Walls (Y = 5 to 8)
        for (int x = -5; x <= 5; x++) {
            for (int z = 6; z <= 13; z++) {
                boolean isCorner = (Math.abs(x) == 5 && (z == 6 || z == 13)) || (x == 0 && z == 6);
                boolean isWall = (Math.abs(x) == 5 || z == 6 || z == 13);
                if (isWall) {
                    for (int y = 5; y <= 8; y++) {
                        level.setBlockAndUpdate(origin.offset(x, y, z), isCorner ? strippedLog : oakPlanks);
                    }
                }
            }
        }

        // Front Doorway at center (X = 0, Z = 6, Y = 5 to 6)
        level.setBlockAndUpdate(origin.offset(0, 5, 6), oakDoor
                .setValue(DoorBlock.FACING, Direction.SOUTH)
                .setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER));
        level.setBlockAndUpdate(origin.offset(0, 6, 6), oakDoor
                .setValue(DoorBlock.FACING, Direction.SOUTH)
                .setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER));
        level.setBlockAndUpdate(origin.offset(-1, 6, 6), lantern);
        level.setBlockAndUpdate(origin.offset(1, 6, 6), lantern);

        // Windows with Glass Panes on Front Wall
        level.setBlockAndUpdate(origin.offset(-3, 6, 6), glassPane);
        level.setBlockAndUpdate(origin.offset(-2, 6, 6), glassPane);
        level.setBlockAndUpdate(origin.offset(2, 6, 6), glassPane);
        level.setBlockAndUpdate(origin.offset(3, 6, 6), glassPane);

        // Second Floor Ceiling / Floor (Y = 9, X = -5 to 5, Z = 6 to 13)
        for (int x = -5; x <= 5; x++) {
            for (int z = 6; z <= 13; z++) {
                level.setBlockAndUpdate(origin.offset(x, 9, z), oakPlanks);
            }
        }

        // Second Story Attic Walls & Dormer (Y = 10 to 12)
        for (int x = -4; x <= 4; x++) {
            for (int z = 7; z <= 12; z++) {
                boolean isAtticWall = (Math.abs(x) == 4 || z == 7 || z == 12);
                if (isAtticWall) {
                    for (int y = 10; y <= 12; y++) {
                        level.setBlockAndUpdate(origin.offset(x, y, z), oakPlanks);
                    }
                }
            }
        }
        // Attic Dormer Windows & Balcony (Z = 7)
        level.setBlockAndUpdate(origin.offset(-2, 11, 7), glassPane);
        level.setBlockAndUpdate(origin.offset(2, 11, 7), glassPane);
        level.setBlockAndUpdate(origin.offset(0, 10, 7), oakDoor.setValue(DoorBlock.FACING, Direction.SOUTH).setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER));
        level.setBlockAndUpdate(origin.offset(0, 11, 7), oakDoor.setValue(DoorBlock.FACING, Direction.SOUTH).setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER));
        // Attic Front Balcony (Y = 9, Z = 6, X = -2 to 2)
        for (int x = -2; x <= 2; x++) {
            level.setBlockAndUpdate(origin.offset(x, 10, 6), oakFence);
        }

        // Interior Furnishing: Living Room & Bedroom
        // Ground Floor Interior (Y = 5)
        for (int z = 7; z <= 12; z++) {
            level.setBlockAndUpdate(origin.offset(0, 5, z), redCarpet);
        }
        level.setBlockAndUpdate(origin.offset(-4, 5, 8), bookshelves);
        level.setBlockAndUpdate(origin.offset(-4, 6, 8), bookshelves);
        level.setBlockAndUpdate(origin.offset(-4, 5, 9), bookshelves);
        level.setBlockAndUpdate(origin.offset(-4, 6, 9), bookshelves);
        level.setBlockAndUpdate(origin.offset(4, 5, 8), chest);
        level.setBlockAndUpdate(origin.offset(4, 5, 9), chest);
        level.setBlockAndUpdate(origin.offset(4, 5, 10), craftingTable);
        level.setBlockAndUpdate(origin.offset(-4, 5, 12), potFern);
        level.setBlockAndUpdate(origin.offset(4, 5, 12), potFern);
        level.setBlockAndUpdate(origin.offset(0, 8, 9), hangingLantern);

        // Internal Staircase to Second Floor (X = -3, Z = 10 to 12)
        for (int s = 0; s <= 3; s++) {
            level.setBlockAndUpdate(origin.offset(-3, 5 + s, 10 + s), oakStairs.setValue(StairBlock.FACING, Direction.NORTH));
        }

        // Attic Bedroom Furnishing (Y = 10)
        BlockState bed = getBlockState("red_bed", getBlockState("white_bed", Blocks.OAK_PLANKS.defaultBlockState()));
        level.setBlockAndUpdate(origin.offset(2, 10, 11), bed);
        level.setBlockAndUpdate(origin.offset(3, 10, 11), chest);
        level.setBlockAndUpdate(origin.offset(-2, 10, 11), bookshelves);
        level.setBlockAndUpdate(origin.offset(0, 12, 10), hangingLantern);

        // 12. MAJESTIC GABLED ROOF WITH STONE-BRICK TRIM & CROSS GABLE (Y = 12 to 16)
        for (int z = 5; z <= 14; z++) {
            boolean isEaveEdge = (z == 5 || z == 14);
            BlockState stairMat = isEaveEdge ? stoneStairs : oakStairs;
            BlockState slabMat = isEaveEdge ? stoneSlab : oakSlab;

            // Pitch 1 (Y = 12, X = ±6)
            level.setBlockAndUpdate(origin.offset(-6, 12, z), stairMat.setValue(StairBlock.FACING, Direction.EAST));
            level.setBlockAndUpdate(origin.offset(6, 12, z), stairMat.setValue(StairBlock.FACING, Direction.WEST));

            // Pitch 2 (Y = 13, X = ±5)
            level.setBlockAndUpdate(origin.offset(-5, 13, z), stairMat.setValue(StairBlock.FACING, Direction.EAST));
            level.setBlockAndUpdate(origin.offset(5, 13, z), stairMat.setValue(StairBlock.FACING, Direction.WEST));

            // Pitch 3 (Y = 14, X = ±4)
            level.setBlockAndUpdate(origin.offset(-4, 14, z), stairMat.setValue(StairBlock.FACING, Direction.EAST));
            level.setBlockAndUpdate(origin.offset(4, 14, z), stairMat.setValue(StairBlock.FACING, Direction.WEST));

            // Pitch 4 (Y = 15, X = ±3)
            level.setBlockAndUpdate(origin.offset(-3, 15, z), stairMat.setValue(StairBlock.FACING, Direction.EAST));
            level.setBlockAndUpdate(origin.offset(3, 15, z), stairMat.setValue(StairBlock.FACING, Direction.WEST));

            // Pitch 5 (Y = 16, X = ±2)
            level.setBlockAndUpdate(origin.offset(-2, 16, z), stairMat.setValue(StairBlock.FACING, Direction.EAST));
            level.setBlockAndUpdate(origin.offset(2, 16, z), stairMat.setValue(StairBlock.FACING, Direction.WEST));

            // Ridge Cap (Y = 16, X = -1 to 1)
            level.setBlockAndUpdate(origin.offset(-1, 16, z), slabMat);
            level.setBlockAndUpdate(origin.offset(0, 17, z), slabMat);
            level.setBlockAndUpdate(origin.offset(1, 16, z), slabMat);
        }

        // Close roof gables (Z = 6 and Z = 13, Y = 13 to 16)
        for (int gz : new int[]{ 6, 13 }) {
            for (int gx = -4; gx <= 4; gx++) {
                int maxGy = 16 - (Math.abs(gx) / 2);
                for (int gy = 13; gy <= maxGy; gy++) {
                    level.setBlockAndUpdate(origin.offset(gx, gy, gz), oakPlanks);
                }
            }
        }

        // 13. SPAWN LIVE ANIMALS IN THEIR PENS (Cows & Sheep)
        // Spawn 2 Cows in the upper cow farm (X = -8, Y = 5, Z = 8)
        spawnEntity(level, "cow", origin.offset(-8, 5, 8));
        spawnEntity(level, "cow", origin.offset(-8, 5, 11));

        // Spawn 2 Sheep in the upper sheep farm (X = 8, Y = 5, Z = 8)
        spawnEntity(level, "sheep", origin.offset(8, 5, 8));
        spawnEntity(level, "sheep", origin.offset(8, 5, 11));

        // Spawn 1 Cow / Horse in the lower front paddock (X = -6, Y = 0, Z = -4)
        spawnEntity(level, "cow", origin.offset(-6, 0, -4));
    }

    private static void spawnEntity(Level level, String entityId, BlockPos pos) {
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            var opt = BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.fromNamespaceAndPath("minecraft", entityId));
            opt.ifPresent(type -> {
                try {
                    for (var m : type.getClass().getMethods()) {
                        if (m.getName().equals("spawn") && m.getParameterCount() >= 2) {
                            Class<?>[] pTypes = m.getParameterTypes();
                            if (pTypes[0].isAssignableFrom(serverLevel.getClass()) && pTypes[1].isAssignableFrom(BlockPos.class)) {
                                Object[] args = new Object[m.getParameterCount()];
                                args[0] = serverLevel;
                                args[1] = pos;
                                if (args.length > 2 && pTypes[2].isEnum()) {
                                    args[2] = pTypes[2].getEnumConstants()[0];
                                }
                                m.invoke(type, args);
                                return;
                            }
                        }
                    }
                } catch (Exception ignored) {}
            });
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
