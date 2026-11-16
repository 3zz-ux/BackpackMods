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
 * 1:1 Recreation of the Grand Victorian Alpine Luxury Manor House from the new reference photo:
 * - Solid 5-block deep stone/cobblestone foundation ensuring it NEVER floats
 * - Raised stone terrace plinth with stone brick walls, balustrades & lanterns
 * - Wide, perfectly-oriented 5-block grand central staircase (Half.BOTTOM normal steps)
 * - Ground Floor:
 *   - Timber & stone brick framing with large multi-pane windows and flower boxes
 *   - Protruding timber entrance porch with lanterns
 *   - Symmetrical covered side verandas
 *   - Grand interior living hall (5 blocks high ceiling): hearth/kitchen, sofa, library bookshelves, red carpet
 *   - Wide, easy-to-climb internal staircase with an expansive open ceiling stairwell (full headroom)
 * - Second Floor:
 *   - Full-width grand front balcony/promenade with oak fences & lanterns
 *   - Luxury master suite: King bed, wardrobes, bookcases, study desk, front balcony doors
 * - Soaring Triple-Gabled Alpine Roof:
 *   - Tall Gothic central dormer gable with stone brick trim and apex lantern
 *   - Symmetrical left and right dormer gables
 *   - Fully sealed, watertight, zero holes to the sky, deep overhangs with inverted eaves
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
        buildLuxuryManor(level, origin);

        level.playSound(null, origin, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.BLOCKS, 1.0f, 1.0f);
        if (player != null) {
            player.sendSystemMessage(Component.literal("§6§l[Pixel House] §aتم تشييد القصر الريفي الملكي (Luxury Manor) بالكامل وبأدق تفاصيل الصورة!"));
            if (!player.isCreative()) {
                ctx.getItemInHand().shrink(1);
            }
        }

        return InteractionResult.SUCCESS;
    }

    private void buildLuxuryManor(Level level, BlockPos origin) {
        BlockState stoneBricks = Blocks.STONE_BRICKS.defaultBlockState();
        BlockState cobblestone = Blocks.COBBLESTONE.defaultBlockState();
        BlockState stoneStairs = Blocks.STONE_BRICK_STAIRS.defaultBlockState();
        BlockState stoneSlab = Blocks.STONE_BRICK_SLAB.defaultBlockState();
        BlockState stoneWall = Blocks.STONE_BRICK_WALL.defaultBlockState();
        BlockState oakPlanks = Blocks.OAK_PLANKS.defaultBlockState();
        BlockState strippedLog = Blocks.STRIPPED_OAK_LOG.defaultBlockState();
        BlockState oakStairs = Blocks.OAK_STAIRS.defaultBlockState();
        BlockState oakSlab = Blocks.OAK_SLAB.defaultBlockState();
        BlockState oakFence = Blocks.OAK_FENCE.defaultBlockState();
        BlockState glassPane = Blocks.GLASS_PANE.defaultBlockState();
        BlockState lantern = Blocks.LANTERN.defaultBlockState();
        BlockState hangingLantern = Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true);
        BlockState chest = Blocks.CHEST.defaultBlockState();
        BlockState barrel = Blocks.BARREL.defaultBlockState();
        BlockState furnace = Blocks.FURNACE.defaultBlockState().setValue(FurnaceBlock.FACING, Direction.NORTH);
        BlockState smoker = Blocks.SMOKER.defaultBlockState().setValue(FurnaceBlock.FACING, Direction.NORTH);
        BlockState craftingTable = Blocks.CRAFTING_TABLE.defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockState dirtPath = Blocks.DIRT_PATH.defaultBlockState();
        BlockState redCarpet = getBlockState("red_carpet", Blocks.STONE_BRICKS.defaultBlockState());
        BlockState oakDoor = Blocks.OAK_DOOR.defaultBlockState();
        BlockState bookshelves = Blocks.BOOKSHELF.defaultBlockState();
        BlockState potFern = Blocks.POTTED_FERN.defaultBlockState();
        BlockState smoothStone = Blocks.SMOOTH_STONE.defaultBlockState();

        int minX = -13, maxX = 13;
        int minZ = -8, maxZ = 18;

        // 1. DEEP SOLID STONE BEDROCK FOUNDATION (Y = -5 to -1) - Eliminates floating 100%
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                // Clear all space above
                for (int y = 0; y <= 22; y++) {
                    level.setBlockAndUpdate(origin.offset(x, y, z), air);
                }
                // Solid deep stone layers
                for (int fy = -5; fy <= -2; fy++) {
                    level.setBlockAndUpdate(origin.offset(x, fy, z), ((x + z) % 2 == 0) ? stoneBricks : cobblestone);
                }
                // Surface ground at Y = -1
                if (z >= 0 && z <= maxZ) {
                    level.setBlockAndUpdate(origin.offset(x, -1, z), stoneBricks);
                } else {
                    level.setBlockAndUpdate(origin.offset(x, -1, z), Blocks.GRASS_BLOCK.defaultBlockState());
                }
            }
        }

        // Front gravel / dirt path leading to the grand staircase
        for (int z = -8; z <= -4; z++) {
            for (int x = -2; x <= 2; x++) {
                level.setBlockAndUpdate(origin.offset(x, -1, z), (Math.abs(x) == 2) ? smoothStone : dirtPath);
            }
        }

        // 2. RAISED STONE TERRACE PLINTH (Y = 0 to 2, X = -12 to 12, Z = 0 to 18)
        for (int x = -12; x <= 12; x++) {
            for (int z = 0; z <= 18; z++) {
                // Solid stone terrace base
                level.setBlockAndUpdate(origin.offset(x, 0, z), stoneBricks);
                level.setBlockAndUpdate(origin.offset(x, 1, z), ((x + z) % 2 == 0) ? cobblestone : stoneBricks);
                // Terrace floor at Y = 2
                boolean isEdge = (Math.abs(x) == 12 || z == 0 || z == 18);
                level.setBlockAndUpdate(origin.offset(x, 2, z), isEdge ? smoothStone : oakPlanks);
            }
        }

        // Terrace Balustrade (Y = 3): Stone Brick Walls, Posts & Lanterns
        for (int x = -12; x <= 12; x++) {
            if (x < -2 || x > 2) {
                level.setBlockAndUpdate(origin.offset(x, 3, 0), ((x % 3 == 0) ? stoneBricks : oakFence));
            }
            level.setBlockAndUpdate(origin.offset(x, 3, 18), ((x % 3 == 0) ? stoneBricks : oakFence));
        }
        for (int z = 0; z <= 18; z++) {
            level.setBlockAndUpdate(origin.offset(-12, 3, z), ((z % 3 == 0) ? stoneBricks : oakFence));
            level.setBlockAndUpdate(origin.offset(12, 3, z), ((z % 3 == 0) ? stoneBricks : oakFence));
        }
        // Lanterns on terrace balustrade stone posts
        int[] postX = { -12, -6, -3, 3, 6, 12 };
        for (int px : postX) {
            level.setBlockAndUpdate(origin.offset(px, 3, 0), stoneBricks);
            level.setBlockAndUpdate(origin.offset(px, 4, 0), lantern);
            level.setBlockAndUpdate(origin.offset(px, 3, 18), stoneBricks);
            level.setBlockAndUpdate(origin.offset(px, 4, 18), lantern);
        }

        // 3. WIDE GRAND CENTRAL ENTRANCE STAIRCASE (X = -2 to 2, Z = -3 to 0)
        // Perfectly oriented right-side-up steps (Half.BOTTOM) ascending smoothly into the terrace!
        for (int step = 0; step <= 2; step++) {
            int z = -3 + step;
            int y = step;
            for (int x = -2; x <= 2; x++) {
                // Solid stone base under each step
                for (int fy = 0; fy < y; fy++) {
                    level.setBlockAndUpdate(origin.offset(x, fy, z), stoneBricks);
                }
                // Step facing South (so player ascends walking North up to the house)
                level.setBlockAndUpdate(origin.offset(x, y, z),
                        oakStairs.setValue(StairBlock.FACING, Direction.SOUTH).setValue(StairBlock.HALF, Half.BOTTOM));
            }
            // Flanking stone brick stepped balustrades
            level.setBlockAndUpdate(origin.offset(-3, y, z), stoneBricks);
            level.setBlockAndUpdate(origin.offset(-3, y + 1, z), stoneWall);
            level.setBlockAndUpdate(origin.offset(3, y, z), stoneBricks);
            level.setBlockAndUpdate(origin.offset(3, y + 1, z), stoneWall);
        }
        level.setBlockAndUpdate(origin.offset(-3, 3, 0), lantern);
        level.setBlockAndUpdate(origin.offset(3, 3, 0), lantern);

        // 4. FIRST FLOOR EXTERIOR WALLS (Y = 3 to 7, X = -9 to 9, Z = 4 to 16)
        for (int x = -9; x <= 9; x++) {
            for (int z = 4; z <= 16; z++) {
                boolean isCorner = (Math.abs(x) == 9 && (z == 4 || z == 10 || z == 16)) || (Math.abs(x) == 3 && z == 4);
                boolean isWall = (Math.abs(x) == 9 || z == 4 || z == 16);
                if (isWall) {
                    for (int y = 3; y <= 7; y++) {
                        BlockState mat;
                        if (y == 3) {
                            mat = stoneBricks; // solid stone foundation row
                        } else if (isCorner) {
                            mat = strippedLog; // vertical timber pillar
                        } else {
                            mat = oakPlanks;   // warm wood siding
                        }
                        level.setBlockAndUpdate(origin.offset(x, y, z), mat);
                    }
                }
            }
        }

        // Grand Protruding Front Porch & Entrance (X = -1 to 1, Z = 4, Y = 3 to 5)
        level.setBlockAndUpdate(origin.offset(0, 3, 4), oakDoor
                .setValue(DoorBlock.FACING, Direction.SOUTH)
                .setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER));
        level.setBlockAndUpdate(origin.offset(0, 4, 4), oakDoor
                .setValue(DoorBlock.FACING, Direction.SOUTH)
                .setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER));
        level.setBlockAndUpdate(origin.offset(-1, 4, 3), lantern);
        level.setBlockAndUpdate(origin.offset(1, 4, 3), lantern);

        // Porch Overhang Canopy at Z = 3
        for (int px = -2; px <= 2; px++) {
            level.setBlockAndUpdate(origin.offset(px, 5, 3), oakSlab);
        }
        level.setBlockAndUpdate(origin.offset(-2, 3, 3), strippedLog);
        level.setBlockAndUpdate(origin.offset(2, 3, 3), strippedLog);

        // Large Multi-Pane Windows on Front Facade with Flower Boxes
        int[] winX = { -6, -5, 5, 6 };
        for (int wx : winX) {
            level.setBlockAndUpdate(origin.offset(wx, 4, 4), glassPane);
            level.setBlockAndUpdate(origin.offset(wx, 5, 4), glassPane);
            level.setBlockAndUpdate(origin.offset(wx, 3, 3), potFern); // flower box
        }

        // Symmetrical Covered Side Pergolas / Verandas (Left & Right)
        for (int z = 5; z <= 15; z += 3) {
            level.setBlockAndUpdate(origin.offset(-11, 3, z), strippedLog);
            level.setBlockAndUpdate(origin.offset(-11, 4, z), strippedLog);
            level.setBlockAndUpdate(origin.offset(-11, 5, z), strippedLog);
            level.setBlockAndUpdate(origin.offset(11, 3, z), strippedLog);
            level.setBlockAndUpdate(origin.offset(11, 4, z), strippedLog);
            level.setBlockAndUpdate(origin.offset(11, 5, z), strippedLog);
        }
        for (int z = 4; z <= 16; z++) {
            for (int sx = -11; sx <= -9; sx++) level.setBlockAndUpdate(origin.offset(sx, 6, z), oakSlab);
            for (int sx = 9; sx <= 11; sx++) level.setBlockAndUpdate(origin.offset(sx, 6, z), oakSlab);
        }

        // 5. FIRST FLOOR LUXURY INTERIOR (Y = 3 to 7)
        // Red carpet runner down the grand central foyer
        for (int z = 5; z <= 14; z++) {
            level.setBlockAndUpdate(origin.offset(0, 3, z), redCarpet);
        }
        // Right Wing: Kitchen & Fireplace Hearth
        level.setBlockAndUpdate(origin.offset(8, 3, 8), stoneBricks);
        level.setBlockAndUpdate(origin.offset(8, 3, 9), furnace);
        level.setBlockAndUpdate(origin.offset(8, 3, 10), smoker);
        level.setBlockAndUpdate(origin.offset(8, 3, 11), craftingTable);
        level.setBlockAndUpdate(origin.offset(8, 3, 12), chest);
        level.setBlockAndUpdate(origin.offset(8, 3, 13), barrel);
        level.setBlockAndUpdate(origin.offset(8, 4, 9), stoneBricks);
        level.setBlockAndUpdate(origin.offset(8, 4, 10), stoneBricks);

        // Left Wing: Library & Living Room Lounge
        for (int bz = 8; bz <= 12; bz++) {
            level.setBlockAndUpdate(origin.offset(-8, 3, bz), bookshelves);
            level.setBlockAndUpdate(origin.offset(-8, 4, bz), bookshelves);
        }
        level.setBlockAndUpdate(origin.offset(-6, 3, 9), oakStairs.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.BOTTOM));
        level.setBlockAndUpdate(origin.offset(-6, 3, 10), oakStairs.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.BOTTOM));
        level.setBlockAndUpdate(origin.offset(-5, 3, 9), oakSlab);
        level.setBlockAndUpdate(origin.offset(-5, 4, 9), potFern);

        // Warm chandeliers in main living hall
        level.setBlockAndUpdate(origin.offset(0, 7, 7), hangingLantern);
        level.setBlockAndUpdate(origin.offset(0, 7, 12), hangingLantern);

        // Second Floor Ceiling / Floor (Y = 8, X = -9 to 9, Z = 4 to 16)
        for (int x = -9; x <= 9; x++) {
            for (int z = 4; z <= 16; z++) {
                level.setBlockAndUpdate(origin.offset(x, 8, z), oakPlanks);
            }
        }

        // WIDE OPEN STAIRWELL (X = -7 to -5, Z = 11 to 15, Y = 8 to 10) - Full Headroom!
        for (int x = -7; x <= -5; x++) {
            for (int z = 11; z <= 15; z++) {
                level.setBlockAndUpdate(origin.offset(x, 8, z), air);
                level.setBlockAndUpdate(origin.offset(x, 9, z), air);
                level.setBlockAndUpdate(origin.offset(x, 10, z), air);
            }
        }

        // REAL FUNCTIONAL INTERNAL STAIRCASE (X = -6 to -5, Z = 11 to 15, ascending to Y = 8)
        // Perfectly oriented right-side-up steps (Half.BOTTOM) so player ascends smoothly!
        for (int s = 0; s <= 4; s++) {
            int sz = 11 + s;
            int sy = 3 + s;
            for (int sx = -6; sx <= -5; sx++) {
                for (int fy = 3; fy < sy; fy++) {
                    level.setBlockAndUpdate(origin.offset(sx, fy, sz), oakPlanks);
                }
                level.setBlockAndUpdate(origin.offset(sx, sy, sz),
                        oakStairs.setValue(StairBlock.FACING, Direction.SOUTH).setValue(StairBlock.HALF, Half.BOTTOM));
            }
        }

        // 6. SECOND FLOOR & PROMENADE BALCONY (Y = 8 to 12)
        // Full-width front promenade balcony (Z = 4, X = -8 to 8, Y = 9)
        for (int x = -8; x <= 8; x++) {
            level.setBlockAndUpdate(origin.offset(x, 9, 4), oakFence);
        }
        level.setBlockAndUpdate(origin.offset(-8, 9, 4), strippedLog);
        level.setBlockAndUpdate(origin.offset(-8, 10, 4), lantern);
        level.setBlockAndUpdate(origin.offset(8, 9, 4), strippedLog);
        level.setBlockAndUpdate(origin.offset(8, 10, 4), lantern);

        // Second Floor Exterior Walls (Y = 9 to 12, X = -8 to 8, Z = 5 to 15)
        for (int x = -8; x <= 8; x++) {
            for (int z = 5; z <= 15; z++) {
                boolean isCorner = (Math.abs(x) == 8 && (z == 5 || z == 15)) || (Math.abs(x) == 3 && z == 5);
                boolean isWall = (Math.abs(x) == 8 || z == 5 || z == 15);
                if (isWall) {
                    for (int y = 9; y <= 12; y++) {
                        level.setBlockAndUpdate(origin.offset(x, y, z), isCorner ? strippedLog : oakPlanks);
                    }
                }
            }
        }

        // Balcony French Doors at center (X = 0, Z = 5, Y = 9 to 10)
        level.setBlockAndUpdate(origin.offset(0, 9, 5), oakDoor
                .setValue(DoorBlock.FACING, Direction.SOUTH)
                .setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER));
        level.setBlockAndUpdate(origin.offset(0, 10, 5), oakDoor
                .setValue(DoorBlock.FACING, Direction.SOUTH)
                .setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER));

        // Second Floor Windows on Front Facade with Flower Boxes
        for (int wx : new int[]{ -5, 5 }) {
            level.setBlockAndUpdate(origin.offset(wx, 10, 5), glassPane);
            level.setBlockAndUpdate(origin.offset(wx, 9, 4), potFern);
        }

        // Master Bedroom Interior (Y = 9)
        BlockState bed = getBlockState("red_bed", getBlockState("white_bed", Blocks.OAK_PLANKS.defaultBlockState()));
        level.setBlockAndUpdate(origin.offset(2, 9, 13), bed);
        level.setBlockAndUpdate(origin.offset(3, 9, 13), bed);
        level.setBlockAndUpdate(origin.offset(4, 9, 13), barrel);
        level.setBlockAndUpdate(origin.offset(4, 10, 13), lantern);
        level.setBlockAndUpdate(origin.offset(1, 9, 13), barrel);
        level.setBlockAndUpdate(origin.offset(1, 10, 13), potFern);

        // Master wardrobes & desk
        level.setBlockAndUpdate(origin.offset(7, 9, 10), chest);
        level.setBlockAndUpdate(origin.offset(7, 9, 9), chest);
        level.setBlockAndUpdate(origin.offset(7, 10, 10), chest);
        level.setBlockAndUpdate(origin.offset(7, 10, 9), chest);
        level.setBlockAndUpdate(origin.offset(-3, 9, 9), bookshelves);
        level.setBlockAndUpdate(origin.offset(-3, 10, 9), bookshelves);
        level.setBlockAndUpdate(origin.offset(0, 12, 10), hangingLantern);

        // Attic Ceiling / Floor (Y = 13, X = -8 to 8, Z = 5 to 15)
        for (int x = -8; x <= 8; x++) {
            for (int z = 5; z <= 15; z++) {
                level.setBlockAndUpdate(origin.offset(x, 13, z), oakPlanks);
            }
        }

        // 7. MAJESTIC TRIPLE-GABLED ALPINE ROOF (Y = 13 to 20) - ZERO HOLES TO THE SKY!
        // Main Roof slopes down towards East and West (X = ±9 to 0, Z = 4 to 16)
        for (int z = 4; z <= 16; z++) {
            boolean isEaveEdge = (z == 4 || z == 16);
            BlockState stairMat = isEaveEdge ? stoneStairs : oakStairs;
            BlockState slabMat = isEaveEdge ? stoneSlab : oakSlab;

            // Pitch 1 (Y = 13, X = ±9)
            level.setBlockAndUpdate(origin.offset(-9, 13, z), stairMat.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.BOTTOM));
            level.setBlockAndUpdate(origin.offset(9, 13, z), stairMat.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.BOTTOM));

            // Pitch 2 (Y = 14, X = ±8)
            level.setBlockAndUpdate(origin.offset(-8, 14, z), stairMat.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.BOTTOM));
            level.setBlockAndUpdate(origin.offset(8, 14, z), stairMat.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.BOTTOM));

            // Pitch 3 (Y = 15, X = ±7)
            level.setBlockAndUpdate(origin.offset(-7, 15, z), stairMat.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.BOTTOM));
            level.setBlockAndUpdate(origin.offset(7, 15, z), stairMat.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.BOTTOM));

            // Pitch 4 (Y = 16, X = ±6)
            level.setBlockAndUpdate(origin.offset(-6, 16, z), stairMat.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.BOTTOM));
            level.setBlockAndUpdate(origin.offset(6, 16, z), stairMat.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.BOTTOM));

            // Pitch 5 (Y = 17, X = ±5)
            level.setBlockAndUpdate(origin.offset(-5, 17, z), stairMat.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.BOTTOM));
            level.setBlockAndUpdate(origin.offset(5, 17, z), stairMat.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.BOTTOM));

            // Pitch 6 (Y = 18, X = ±4)
            level.setBlockAndUpdate(origin.offset(-4, 18, z), stairMat.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.BOTTOM));
            level.setBlockAndUpdate(origin.offset(4, 18, z), stairMat.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.BOTTOM));

            // Pitch 7 (Y = 19, X = ±3)
            level.setBlockAndUpdate(origin.offset(-3, 19, z), stairMat.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.BOTTOM));
            level.setBlockAndUpdate(origin.offset(3, 19, z), stairMat.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.BOTTOM));

            // Ridge Cap (Y = 19 to 20, X = -2 to 2) - Completely seals ceiling watertight!
            for (int rx = -2; rx <= 2; rx++) {
                level.setBlockAndUpdate(origin.offset(rx, 19, z), oakPlanks);
                level.setBlockAndUpdate(origin.offset(rx, 20, z), slabMat);
            }
        }

        // Close front and rear roof gables completely with oak planks (Z = 4 and Z = 16)
        for (int gz : new int[]{ 4, 16 }) {
            for (int gx = -8; gx <= 8; gx++) {
                int maxGy = 19 - Math.abs(gx);
                for (int gy = 13; gy <= maxGy; gy++) {
                    level.setBlockAndUpdate(origin.offset(gx, gy, gz), oakPlanks);
                }
            }
        }

        // 8. TALL GOTHIC CENTRAL DORMER GABLE (X = -3 to 3, Z = 4 to 8, Y = 14 to 20)
        for (int dz = 4; dz <= 8; dz++) {
            boolean isFrontTrim = (dz == 4);
            BlockState dStair = isFrontTrim ? stoneStairs : oakStairs;
            BlockState dSlab = isFrontTrim ? stoneSlab : oakSlab;

            level.setBlockAndUpdate(origin.offset(-3, 15, dz), dStair.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.BOTTOM));
            level.setBlockAndUpdate(origin.offset(3, 15, dz), dStair.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.BOTTOM));

            level.setBlockAndUpdate(origin.offset(-2, 16, dz), dStair.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.BOTTOM));
            level.setBlockAndUpdate(origin.offset(2, 16, dz), dStair.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.BOTTOM));

            level.setBlockAndUpdate(origin.offset(-1, 17, dz), dStair.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.BOTTOM));
            level.setBlockAndUpdate(origin.offset(1, 17, dz), dStair.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.BOTTOM));

            level.setBlockAndUpdate(origin.offset(0, 18, dz), oakPlanks);
            level.setBlockAndUpdate(origin.offset(0, 19, dz), dSlab);
        }
        // Central dormer window & peak lantern
        level.setBlockAndUpdate(origin.offset(0, 15, 4), glassPane);
        level.setBlockAndUpdate(origin.offset(0, 16, 4), glassPane);
        level.setBlockAndUpdate(origin.offset(0, 14, 3), potFern);
        level.setBlockAndUpdate(origin.offset(0, 18, 3), hangingLantern);

        // 9. SYMMETRICAL LEFT & RIGHT DORMER GABLES (Left: X = -7 to -4; Right: X = 4 to 7, Z = 4 to 7)
        int[][] sideDormers = { { -7, -4, -6, -5 }, { 4, 7, 5, 6 } };
        for (int[] sd : sideDormers) {
            int leftX = sd[0], rightX = sd[1], midX1 = sd[2], midX2 = sd[3];
            for (int dz = 4; dz <= 7; dz++) {
                boolean isEdge = (dz == 4);
                BlockState sStair = isEdge ? stoneStairs : oakStairs;

                level.setBlockAndUpdate(origin.offset(leftX, 15, dz), sStair.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.BOTTOM));
                level.setBlockAndUpdate(origin.offset(rightX, 15, dz), sStair.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.BOTTOM));

                level.setBlockAndUpdate(origin.offset(midX1, 16, dz), oakPlanks);
                level.setBlockAndUpdate(origin.offset(midX2, 16, dz), oakPlanks);
                level.setBlockAndUpdate(origin.offset(midX1, 17, dz), stoneSlab);
                level.setBlockAndUpdate(origin.offset(midX2, 17, dz), stoneSlab);
            }
            level.setBlockAndUpdate(origin.offset(midX1, 15, 4), glassPane);
            level.setBlockAndUpdate(origin.offset(midX2, 15, 4), glassPane);
            level.setBlockAndUpdate(origin.offset(midX1, 14, 3), potFern);
            level.setBlockAndUpdate(origin.offset(midX1, 17, 3), hangingLantern);
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
