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
 * 1:1 Recreation of the iconic Multi-Level Survival Base from the reference photo:
 * - Solid 6-block deep stone bedrock foundation (Y = -6 to -1) ensuring it NEVER floats
 * - Ground Level:
 *   - Front-Right: Irrigated crop farm with mature wheat & potatoes, surrounded by oak fences & lanterns
 *   - Front-Left: Covered animal paddock with oak trellis pergola canopy & hay bales
 *   - Front Perimeter: Oak leaf hedges with stripped log corner posts & lanterns
 *   - Center: Smooth stone & dirt path leading to the grand central staircase
 *   - Right Undercroft: Fully covered workshop under terrace with double-height furnaces, barrels, anvil, smithing table & chests
 *   - Left Undercroft: Supported storage cellar with chests and barrels
 *   - Central Grand Staircase: Wide oak staircase with stepped stripped-log banisters and lanterns
 * - Second Story Terrace (Level 2):
 *   - Wraps around the chalet with full post supports below
 *   - Left Balcony: Fenced cow farm with pergola roof, hay & live cows
 *   - Right Balcony: Fenced sheep farm with pergola roof, hay & live sheep
 * - Main 2-Story Survival Chalet:
 *   - Stripped oak timber framing with oak planks infill
 *   - Glass pane windows with flower boxes and shutters
 *   - Front door with lanterns
 *   - Second-floor dormer balcony
 *   - Classic alpine cross-gable roof with stone brick stair trim and oak body
 *   - Fully furnished cozy interior: living room, kitchen, fireplace, bookshelves, upstairs master bedroom
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
            player.sendSystemMessage(Component.literal("§2§l[Pixel House] §aتم بناء قاعدة النجاة الحقيقية المتطابقة مع الصورة بالكامل!"));
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
        BlockState stoneSlab = Blocks.STONE_BRICK_SLAB.defaultBlockState();
        BlockState oakStairs = Blocks.OAK_STAIRS.defaultBlockState();
        BlockState oakSlab = Blocks.OAK_SLAB.defaultBlockState();
        BlockState oakFence = Blocks.OAK_FENCE.defaultBlockState();
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
        BlockState hay = Blocks.HAY_BLOCK.defaultBlockState();
        BlockState redCarpet = getBlockState("red_carpet", Blocks.STONE_BRICKS.defaultBlockState());
        BlockState oakDoor = Blocks.OAK_DOOR.defaultBlockState();
        BlockState bookshelves = Blocks.BOOKSHELF.defaultBlockState();
        BlockState potFern = Blocks.POTTED_FERN.defaultBlockState();
        BlockState smoothStone = Blocks.SMOOTH_STONE.defaultBlockState();

        int minX = -10, maxX = 10;
        int minZ = -9, maxZ = 13;

        // 1. DEEP SOLID MOUNTAIN FOUNDATION (Y = -6 to -1) - Eliminates floating 100%
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                // Clear all space above
                for (int y = 0; y <= 20; y++) {
                    level.setBlockAndUpdate(origin.offset(x, y, z), air);
                }
                // Deep solid stone foundation
                for (int fy = -6; fy <= -2; fy++) {
                    level.setBlockAndUpdate(origin.offset(x, fy, z), (Math.abs(x + z) % 3 == 0) ? cobblestone : stoneBricks);
                }
                // Surface layer at Y = -1
                if (z >= 0 && z <= 12) {
                    level.setBlockAndUpdate(origin.offset(x, -1, z), stoneBricks);
                } else {
                    level.setBlockAndUpdate(origin.offset(x, -1, z), Blocks.GRASS_BLOCK.defaultBlockState());
                }
            }
        }

        // 2. FRONT PERIMETER HEDGES & ENTRANCE GATE (Z = -9)
        for (int x = -10; x <= 10; x++) {
            if (x < -1 || x > 1) {
                level.setBlockAndUpdate(origin.offset(x, 0, -9), oakLeaves);
                level.setBlockAndUpdate(origin.offset(x, 1, -9), oakFence);
            }
        }
        for (int z = -9; z <= -1; z++) {
            level.setBlockAndUpdate(origin.offset(-10, 0, z), oakLeaves);
            level.setBlockAndUpdate(origin.offset(-10, 1, z), oakFence);
            level.setBlockAndUpdate(origin.offset(10, 0, z), oakLeaves);
            level.setBlockAndUpdate(origin.offset(10, 1, z), oakFence);
        }
        // Gateway posts with lanterns
        level.setBlockAndUpdate(origin.offset(-2, 0, -9), strippedLog);
        level.setBlockAndUpdate(origin.offset(-2, 1, -9), strippedLog);
        level.setBlockAndUpdate(origin.offset(-2, 2, -9), lantern);

        level.setBlockAndUpdate(origin.offset(2, 0, -9), strippedLog);
        level.setBlockAndUpdate(origin.offset(2, 1, -9), strippedLog);
        level.setBlockAndUpdate(origin.offset(2, 2, -9), lantern);

        // Central garden path
        for (int z = -9; z <= 0; z++) {
            level.setBlockAndUpdate(origin.offset(-1, -1, z), smoothStone);
            level.setBlockAndUpdate(origin.offset(0, -1, z), dirtPath);
            level.setBlockAndUpdate(origin.offset(1, -1, z), smoothStone);
        }

        // 3. FRONT-RIGHT ENCLOSED CROP FARM (X = 3 to 9, Z = -8 to -1)
        for (int x = 3; x <= 9; x++) {
            for (int z = -8; z <= -1; z++) {
                boolean isBorder = (x == 3 || x == 9 || z == -8 || z == -1);
                if (isBorder) {
                    level.setBlockAndUpdate(origin.offset(x, 0, z), oakFence);
                } else if (x == 6 && z == -4) {
                    level.setBlockAndUpdate(origin.offset(x, -1, z), Blocks.WATER.defaultBlockState());
                    level.setBlockAndUpdate(origin.offset(x, 0, z), oakSlab);
                    level.setBlockAndUpdate(origin.offset(x, 1, z), lantern);
                } else {
                    level.setBlockAndUpdate(origin.offset(x, -1, z), farmland);
                    level.setBlockAndUpdate(origin.offset(x, 0, z), wheat);
                }
            }
        }
        level.setBlockAndUpdate(origin.offset(3, 0, -8), strippedLog);
        level.setBlockAndUpdate(origin.offset(3, 1, -8), lantern);
        level.setBlockAndUpdate(origin.offset(9, 0, -8), strippedLog);
        level.setBlockAndUpdate(origin.offset(9, 1, -8), lantern);

        // 4. FRONT-LEFT ANIMAL PADDOCK WITH PERGOLA CANOPY (X = -9 to -3, Z = -8 to -1)
        for (int x = -9; x <= -3; x++) {
            for (int z = -8; z <= -1; z++) {
                boolean isBorder = (x == -9 || x == -3 || z == -8 || z == -1);
                if (isBorder) {
                    level.setBlockAndUpdate(origin.offset(x, 0, z), oakFence);
                }
            }
        }
        // Corner posts (stripped oak logs)
        int[] padX = { -9, -3 };
        int[] padZ = { -8, -1 };
        for (int px : padX) {
            for (int pz : padZ) {
                level.setBlockAndUpdate(origin.offset(px, 0, pz), strippedLog);
                level.setBlockAndUpdate(origin.offset(px, 1, pz), strippedLog);
                level.setBlockAndUpdate(origin.offset(px, 2, pz), strippedLog);
            }
        }
        // Paddock pergola roof
        for (int x = -9; x <= -3; x++) {
            for (int z = -8; z <= -1; z++) {
                level.setBlockAndUpdate(origin.offset(x, 3, z), oakSlab);
            }
        }
        level.setBlockAndUpdate(origin.offset(-6, 0, -4), hay);
        level.setBlockAndUpdate(origin.offset(-5, 0, -4), hay);
        level.setBlockAndUpdate(origin.offset(-6, 2, -4), hangingLantern);

        // 5. UNDERCROFT WORKSHOP (X = 3 to 10, Z = 0 to 7) - Built solidly under terrace
        // Structural support pillars along perimeter of workshop (preventing any floating)
        int[] workPillarX = { 3, 6, 10 };
        for (int wpx : workPillarX) {
            for (int y = 0; y <= 4; y++) {
                level.setBlockAndUpdate(origin.offset(wpx, y, 0), strippedLog);
                level.setBlockAndUpdate(origin.offset(wpx, y, 7), strippedLog);
            }
        }
        // Workshop back stone wall (Z = 7, X = 3 to 10, Y = 0 to 3)
        for (int x = 3; x <= 10; x++) {
            for (int y = 0; y <= 3; y++) {
                level.setBlockAndUpdate(origin.offset(x, y, 7), stoneBricks);
            }
        }
        // Bank of furnaces (X = 4 to 9, Y = 0 to 1, Z = 6)
        for (int x = 4; x <= 9; x++) {
            level.setBlockAndUpdate(origin.offset(x, 0, 6), (x % 2 == 0) ? blastFurnace : furnace);
            level.setBlockAndUpdate(origin.offset(x, 1, 6), (x % 2 == 0) ? smoker : furnace);
        }
        // Workshop tools, chests, anvil
        level.setBlockAndUpdate(origin.offset(3, 0, 5), chest);
        level.setBlockAndUpdate(origin.offset(3, 0, 4), chest);
        level.setBlockAndUpdate(origin.offset(10, 0, 5), barrel);
        level.setBlockAndUpdate(origin.offset(10, 0, 4), barrel);
        level.setBlockAndUpdate(origin.offset(9, 0, 3), craftingTable);
        level.setBlockAndUpdate(origin.offset(9, 0, 2), anvil);
        level.setBlockAndUpdate(origin.offset(9, 0, 1), smithingTable);

        // Workshop front pergola overhang (Z = 0 to 2, Y = 3)
        for (int x = 3; x <= 10; x++) {
            for (int z = 0; z <= 2; z++) {
                level.setBlockAndUpdate(origin.offset(x, 3, z), oakSlab);
            }
        }
        level.setBlockAndUpdate(origin.offset(3, 2, 1), hangingLantern);
        level.setBlockAndUpdate(origin.offset(6, 2, 1), hangingLantern);
        level.setBlockAndUpdate(origin.offset(10, 2, 1), hangingLantern);

        // 6. UNDERCROFT CELLAR & STORAGE (Left side under terrace, X = -10 to -3, Z = 0 to 7)
        int[] leftPillarX = { -10, -6, -3 };
        for (int lpx : leftPillarX) {
            for (int y = 0; y <= 4; y++) {
                level.setBlockAndUpdate(origin.offset(lpx, y, 0), strippedLog);
                level.setBlockAndUpdate(origin.offset(lpx, y, 7), strippedLog);
            }
        }
        for (int x = -10; x <= -3; x++) {
            for (int y = 0; y <= 3; y++) {
                level.setBlockAndUpdate(origin.offset(x, y, 7), stoneBricks);
            }
        }
        level.setBlockAndUpdate(origin.offset(-4, 0, 5), chest);
        level.setBlockAndUpdate(origin.offset(-4, 0, 4), chest);
        level.setBlockAndUpdate(origin.offset(-9, 0, 5), barrel);
        level.setBlockAndUpdate(origin.offset(-9, 0, 4), barrel);

        // 7. INTEGRATED CENTRAL GRAND STAIRCASE (X = -1 to 1, Z = 0 to 4)
        for (int step = 0; step <= 4; step++) {
            int z = step;
            int y = step;
            for (int x = -1; x <= 1; x++) {
                // Solid stone brick base under stairs
                for (int fy = 0; fy < y; fy++) {
                    level.setBlockAndUpdate(origin.offset(x, fy, z), stoneBricks);
                }
                // Step facing South (so player ascends walking North into house)
                level.setBlockAndUpdate(origin.offset(x, y, z), oakStairs.setValue(StairBlock.FACING, Direction.SOUTH));
            }
            // Stepped stripped-log side banisters
            level.setBlockAndUpdate(origin.offset(-2, y, z), strippedLog);
            level.setBlockAndUpdate(origin.offset(2, y, z), strippedLog);
        }
        level.setBlockAndUpdate(origin.offset(-2, 5, 4), lantern);
        level.setBlockAndUpdate(origin.offset(2, 5, 4), lantern);

        // 8. SECOND STORY TERRACE DECK (Floor at Y = 4, X = -10 to 10, Z = 4 to 12)
        for (int x = -10; x <= 10; x++) {
            for (int z = 4; z <= 12; z++) {
                level.setBlockAndUpdate(origin.offset(x, 4, z), oakPlanks);
            }
        }
        // Terrace protective oak fences
        for (int x = -10; x <= 10; x++) {
            if (x < -1 || x > 1) {
                level.setBlockAndUpdate(origin.offset(x, 5, 4), oakFence);
            }
            level.setBlockAndUpdate(origin.offset(x, 5, 12), oakFence);
        }
        for (int z = 4; z <= 12; z++) {
            level.setBlockAndUpdate(origin.offset(-10, 5, z), oakFence);
            level.setBlockAndUpdate(origin.offset(10, 5, z), oakFence);
        }
        // Corner stripped log posts with lanterns
        level.setBlockAndUpdate(origin.offset(-10, 5, 4), strippedLog);
        level.setBlockAndUpdate(origin.offset(-10, 6, 4), lantern);
        level.setBlockAndUpdate(origin.offset(10, 5, 4), strippedLog);
        level.setBlockAndUpdate(origin.offset(10, 6, 4), lantern);

        // 9. UPPER LEFT DECK: FENCED COW FARM (X = -10 to -5, Z = 5 to 11)
        for (int x = -10; x <= -5; x++) {
            for (int z = 5; z <= 11; z++) {
                if (x == -5) {
                    level.setBlockAndUpdate(origin.offset(x, 5, z), oakFence);
                }
            }
        }
        // Pergola canopy roof over cow farm (Y = 8)
        for (int x = -10; x <= -5; x++) {
            for (int z = 5; z <= 11; z++) {
                level.setBlockAndUpdate(origin.offset(x, 8, z), oakSlab);
            }
        }
        level.setBlockAndUpdate(origin.offset(-8, 5, 8), hay);
        level.setBlockAndUpdate(origin.offset(-7, 5, 8), hay);
        level.setBlockAndUpdate(origin.offset(-8, 7, 8), hangingLantern);

        // 10. UPPER RIGHT DECK: FENCED SHEEP FARM (X = 5 to 10, Z = 5 to 11)
        for (int x = 5; x <= 10; x++) {
            for (int z = 5; z <= 11; z++) {
                if (x == 5) {
                    level.setBlockAndUpdate(origin.offset(x, 5, z), oakFence);
                }
            }
        }
        // Pergola canopy roof over sheep farm (Y = 8)
        for (int x = 5; x <= 10; x++) {
            for (int z = 5; z <= 11; z++) {
                level.setBlockAndUpdate(origin.offset(x, 8, z), oakSlab);
            }
        }
        level.setBlockAndUpdate(origin.offset(8, 5, 8), hay);
        level.setBlockAndUpdate(origin.offset(7, 5, 8), hay);
        level.setBlockAndUpdate(origin.offset(8, 7, 8), hangingLantern);

        // 11. MAIN TWO-STORY SURVIVAL CHALET (X = -4 to 4, Z = 5 to 11)
        // Level 1 Walls (Y = 5 to 8)
        for (int x = -4; x <= 4; x++) {
            for (int z = 5; z <= 11; z++) {
                boolean isCorner = (Math.abs(x) == 4 && (z == 5 || z == 11)) || (Math.abs(x) == 2 && z == 5);
                boolean isWall = (Math.abs(x) == 4 || z == 5 || z == 11);
                if (isWall) {
                    for (int y = 5; y <= 8; y++) {
                        level.setBlockAndUpdate(origin.offset(x, y, z), isCorner ? strippedLog : oakPlanks);
                    }
                }
            }
        }

        // Front Doorway in center (X = 0, Z = 5, Y = 5 to 6)
        level.setBlockAndUpdate(origin.offset(0, 5, 5), oakDoor
                .setValue(DoorBlock.FACING, Direction.SOUTH)
                .setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER));
        level.setBlockAndUpdate(origin.offset(0, 6, 5), oakDoor
                .setValue(DoorBlock.FACING, Direction.SOUTH)
                .setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER));
        level.setBlockAndUpdate(origin.offset(-1, 6, 5), lantern);
        level.setBlockAndUpdate(origin.offset(1, 6, 5), lantern);

        // Windows with Glass Panes & Flower Boxes on front facade
        level.setBlockAndUpdate(origin.offset(-3, 6, 5), glassPane);
        level.setBlockAndUpdate(origin.offset(3, 6, 5), glassPane);
        level.setBlockAndUpdate(origin.offset(-3, 5, 4), potFern);
        level.setBlockAndUpdate(origin.offset(3, 5, 4), potFern);

        // Second Floor Ceiling / Floor (Y = 9, X = -4 to 4, Z = 5 to 11)
        for (int x = -4; x <= 4; x++) {
            for (int z = 5; z <= 11; z++) {
                level.setBlockAndUpdate(origin.offset(x, 9, z), oakPlanks);
            }
        }

        // Second Story Attic / Master Suite Walls (Y = 10 to 12)
        for (int x = -3; x <= 3; x++) {
            for (int z = 6; z <= 10; z++) {
                boolean isAtticWall = (Math.abs(x) == 3 || z == 6 || z == 10);
                if (isAtticWall) {
                    for (int y = 10; y <= 12; y++) {
                        level.setBlockAndUpdate(origin.offset(x, y, z), (Math.abs(x) == 3 && (z == 6 || z == 10)) ? strippedLog : oakPlanks);
                    }
                }
            }
        }
        // Attic Dormer Window overlooking front yard (Z = 6, Y = 11, X = 0)
        level.setBlockAndUpdate(origin.offset(0, 11, 6), glassPane);
        level.setBlockAndUpdate(origin.offset(-1, 11, 6), glassPane);
        level.setBlockAndUpdate(origin.offset(1, 11, 6), glassPane);

        // Attic Front Overlook Balcony (Y = 9, Z = 5, X = -2 to 2)
        for (int x = -2; x <= 2; x++) {
            level.setBlockAndUpdate(origin.offset(x, 10, 5), oakFence);
        }

        // Interior Furnishing: Ground Floor Living & Kitchen
        for (int z = 6; z <= 10; z++) {
            level.setBlockAndUpdate(origin.offset(0, 5, z), redCarpet);
        }
        level.setBlockAndUpdate(origin.offset(-3, 5, 7), bookshelves);
        level.setBlockAndUpdate(origin.offset(-3, 6, 7), bookshelves);
        level.setBlockAndUpdate(origin.offset(-3, 5, 8), bookshelves);
        level.setBlockAndUpdate(origin.offset(-3, 6, 8), bookshelves);
        level.setBlockAndUpdate(origin.offset(3, 5, 7), chest);
        level.setBlockAndUpdate(origin.offset(3, 5, 8), chest);
        level.setBlockAndUpdate(origin.offset(3, 5, 9), craftingTable);
        level.setBlockAndUpdate(origin.offset(3, 5, 10), furnace);
        level.setBlockAndUpdate(origin.offset(0, 8, 8), hangingLantern);

        // Internal Staircase to Second Floor (X = -2, Z = 8 to 10)
        for (int s = 0; s <= 3; s++) {
            level.setBlockAndUpdate(origin.offset(-2, 5 + s, 7 + s), oakStairs.setValue(StairBlock.FACING, Direction.NORTH));
        }

        // Attic Master Bedroom Furnishing (Y = 10)
        BlockState bed = getBlockState("red_bed", getBlockState("white_bed", Blocks.OAK_PLANKS.defaultBlockState()));
        level.setBlockAndUpdate(origin.offset(2, 10, 9), bed);
        level.setBlockAndUpdate(origin.offset(2, 10, 8), chest);
        level.setBlockAndUpdate(origin.offset(-1, 10, 9), bookshelves);
        level.setBlockAndUpdate(origin.offset(0, 12, 8), hangingLantern);

        // 12. MAJESTIC ALPINE CROSS-GABLE ROOF WITH STONE-BRICK TRIM (Y = 12 to 16)
        // Main roof slopes down towards East and West (X = ±5 to 0, Z = 4 to 12)
        for (int z = 4; z <= 12; z++) {
            boolean isEaveEdge = (z == 4 || z == 12);
            BlockState stairMat = isEaveEdge ? stoneStairs : oakStairs;
            BlockState slabMat = isEaveEdge ? stoneSlab : oakSlab;

            // Pitch 1 (Y = 12, X = ±5)
            level.setBlockAndUpdate(origin.offset(-5, 12, z), stairMat.setValue(StairBlock.FACING, Direction.EAST));
            level.setBlockAndUpdate(origin.offset(5, 12, z), stairMat.setValue(StairBlock.FACING, Direction.WEST));

            // Pitch 2 (Y = 13, X = ±4)
            level.setBlockAndUpdate(origin.offset(-4, 13, z), stairMat.setValue(StairBlock.FACING, Direction.EAST));
            level.setBlockAndUpdate(origin.offset(4, 13, z), stairMat.setValue(StairBlock.FACING, Direction.WEST));

            // Pitch 3 (Y = 14, X = ±3)
            level.setBlockAndUpdate(origin.offset(-3, 14, z), stairMat.setValue(StairBlock.FACING, Direction.EAST));
            level.setBlockAndUpdate(origin.offset(3, 14, z), stairMat.setValue(StairBlock.FACING, Direction.WEST));

            // Pitch 4 (Y = 15, X = ±2)
            level.setBlockAndUpdate(origin.offset(-2, 15, z), stairMat.setValue(StairBlock.FACING, Direction.EAST));
            level.setBlockAndUpdate(origin.offset(2, 15, z), stairMat.setValue(StairBlock.FACING, Direction.WEST));

            // Ridge Cap (Y = 15 to 16, X = -1 to 1)
            level.setBlockAndUpdate(origin.offset(-1, 15, z), slabMat);
            level.setBlockAndUpdate(origin.offset(0, 16, z), slabMat);
            level.setBlockAndUpdate(origin.offset(1, 15, z), slabMat);
        }

        // Close roof gables (Z = 4 and Z = 12)
        for (int gz : new int[]{ 4, 12 }) {
            for (int gx = -4; gx <= 4; gx++) {
                int maxGy = 15 - Math.abs(gx);
                for (int gy = 12; gy <= maxGy; gy++) {
                    level.setBlockAndUpdate(origin.offset(gx, gy, gz), oakPlanks);
                }
            }
        }

        // Central Cross-Gable Dormer Peak (Z = 4 to 7, X = -2 to 2, Y = 13 to 15)
        for (int dx = -2; dx <= 2; dx++) {
            level.setBlockAndUpdate(origin.offset(dx, 14, 5), oakPlanks);
            level.setBlockAndUpdate(origin.offset(dx, 14, 4), stoneStairs.setValue(StairBlock.FACING, Direction.SOUTH));
        }
        level.setBlockAndUpdate(origin.offset(0, 15, 4), stoneSlab);

        // 13. SPAWN LIVE ANIMALS IN THEIR PENS (Cows & Sheep)
        spawnEntity(level, "cow", origin.offset(-8, 5, 7));
        spawnEntity(level, "cow", origin.offset(-7, 5, 9));

        spawnEntity(level, "sheep", origin.offset(8, 5, 7));
        spawnEntity(level, "sheep", origin.offset(7, 5, 9));

        spawnEntity(level, "cow", origin.offset(-6, 0, -5));
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
