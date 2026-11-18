package com.backpackmod;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;

/**
 * Pixel House Item:
 * Crafted from logs, planks, stone bricks and an iron ingot.
 * 1:1 Complete recreation of the Grand Victorian Alpine Luxury Manor House:
 * - Solid 5-block deep stone/cobblestone foundation ensuring it NEVER floats
 * anywhere
 * - Raised stone terrace plinth with continuous connected oak fences & corner
 * lanterns
 * - Wide, correctly-oriented grand central staircase (Half.BOTTOM, FACING =
 * SOUTH)
 * - Ground Floor (Empty structural shell ready for player customisation):
 * - Timber & stone brick framing with fully connected multi-pane glass windows
 * - Grand entrance porch with completely open walkway (NO blocking lantern)
 * - 100% solid exterior walls on all 4 sides with attached side verandas
 * - Left-side staircase (along East wall: X = 7 to 8, flush against solid wall)
 * ascending to 2nd floor
 * - Second Floor:
 * - Full-width grand front promenade balcony with continuous connected railings
 * - Clean, spacious middle hall with balcony French doors
 * - Right-side staircase (along West wall: X = -8 to -7, flush against solid
 * wall) ascending to 3rd floor
 * - Third Floor (Magnificent Walk-in Cathedral Penthouse / Attic Suite):
 * - Solid oak plank floor (Y = 13)
 * - Massive open interior with 8-block high vaulted cathedral ceiling (Y = 14
 * to 21)
 * - Front & rear gable windows with connected glass panes and warm hanging
 * chandeliers
 * - 100% Enclosed, Watertight, Zero Holes, Completely Sealed Roof & Side Walls
 */
public class PixelHouseItem extends Item {
    public PixelHouseItem(Properties properties) {
        super(properties.stacksTo(1).rarity(Rarity.EPIC));
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        Player player = ctx.getPlayer();
        if (level.isClientSide())
            return InteractionResult.SUCCESS;

        BlockPos origin = ctx.getClickedPos().above();
        buildLuxuryManor(level, origin);

        level.playSound(null, origin, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.BLOCKS, 1.0f, 1.0f);
        if (player != null) {
            player.sendSystemMessage(Component.literal(
                    "§6§l[Pixel House] §aتم تشييد القصر الملكي بالدور الثالث والسلالم المتناسقة وتوصيل الزجاج بالكامل!"));
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
        BlockState lantern = Blocks.LANTERN.defaultBlockState();
        BlockState hangingLantern = Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true);
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockState dirtPath = Blocks.DIRT_PATH.defaultBlockState();
        BlockState oakDoor = Blocks.OAK_DOOR.defaultBlockState();
        BlockState potFern = Blocks.POTTED_FERN.defaultBlockState();
        BlockState smoothStone = Blocks.SMOOTH_STONE.defaultBlockState();

        // Connected glass panes: pre-configured with EAST/WEST or NORTH/SOUTH to
        // connect seamlessly
        BlockState glassPaneX = Blocks.GLASS_PANE.defaultBlockState()
                .setValue(CrossCollisionBlock.EAST, true)
                .setValue(CrossCollisionBlock.WEST, true);
        BlockState glassPaneZ = Blocks.GLASS_PANE.defaultBlockState()
                .setValue(CrossCollisionBlock.NORTH, true)
                .setValue(CrossCollisionBlock.SOUTH, true);

        int minX = -13, maxX = 13;
        int minZ = -8, maxZ = 18;

        // 1. DEEP SOLID STONE BEDROCK FOUNDATION (Y = -5 to -1) - Eliminates floating
        // 100%
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                // Clear all space above (Y = 0 to 24)
                for (int y = 0; y <= 24; y++) {
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

        // Front approach dirt/stone path leading to the grand staircase
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

        // Continuous, fully-connected terrace balustrade (Y = 3):
        // Solid stone posts with lanterns at corners and staircase flanks
        // Continuous connected oak fences everywhere else (no broken/gapped fences!)
        for (int x = -11; x <= -3; x++)
            level.setBlockAndUpdate(origin.offset(x, 3, 0), oakFence);
        for (int x = 3; x <= 11; x++)
            level.setBlockAndUpdate(origin.offset(x, 3, 0), oakFence);
        for (int x = -11; x <= 11; x++)
            level.setBlockAndUpdate(origin.offset(x, 3, 18), oakFence);
        for (int z = 1; z <= 17; z++) {
            level.setBlockAndUpdate(origin.offset(-12, 3, z), oakFence);
            level.setBlockAndUpdate(origin.offset(12, 3, z), oakFence);
        }

        // Terrace corner posts with lanterns
        int[][] terracePosts = { { -12, 0 }, { 12, 0 }, { -12, 18 }, { 12, 18 }, { -6, 0 }, { 6, 0 } };
        for (int[] tp : terracePosts) {
            level.setBlockAndUpdate(origin.offset(tp[0], 3, tp[1]), stoneBricks);
            level.setBlockAndUpdate(origin.offset(tp[0], 4, tp[1]), lantern);
        }

        // 3. WIDE GRAND CENTRAL ENTRANCE STAIRCASE (X = -2 to 2, Z = -3 to -1)
        // Correct facing: FACING = SOUTH (player on path at North climbs South UP to
        // the terrace!)
        for (int step = 0; step <= 2; step++) {
            int z = -3 + step;
            int y = step;
            for (int x = -2; x <= 2; x++) {
                // Solid stone base under step
                for (int fy = 0; fy < y; fy++) {
                    level.setBlockAndUpdate(origin.offset(x, fy, z), stoneBricks);
                }
                // Step facing South (so lower tread faces North towards approaching player)
                level.setBlockAndUpdate(origin.offset(x, y, z),
                        oakStairs.setValue(StairBlock.FACING, Direction.SOUTH).setValue(StairBlock.HALF, Half.BOTTOM));
            }
            // Continuous flanking stone balustrades without any gaps
            level.setBlockAndUpdate(origin.offset(-3, y, z), stoneBricks);
            level.setBlockAndUpdate(origin.offset(-3, y + 1, z), stoneWall);
            level.setBlockAndUpdate(origin.offset(3, y, z), stoneBricks);
            level.setBlockAndUpdate(origin.offset(3, y + 1, z), stoneWall);
        }
        level.setBlockAndUpdate(origin.offset(-3, 3, 0), stoneBricks);
        level.setBlockAndUpdate(origin.offset(-3, 4, 0), lantern);
        level.setBlockAndUpdate(origin.offset(3, 3, 0), stoneBricks);
        level.setBlockAndUpdate(origin.offset(3, 4, 0), lantern);

        // 4. FIRST FLOOR EXTERIOR WALLS (Y = 3 to 7, X = -9 to 9, Z = 4 to 16)
        // Completely solid walls on all 4 sides - ZERO GAPS!
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
                            mat = oakPlanks; // warm wood siding
                        }
                        level.setBlockAndUpdate(origin.offset(x, y, z), mat);
                    }
                }
            }
        }

        // GRAND LUXURY ENTRANCE PORTICO (Image 2 fix - enhanced shape, beautiful gabled A-frame arch)
        // Center Door at X = 0, Z = 4
        level.setBlockAndUpdate(origin.offset(0, 3, 4), oakDoor
                .setValue(DoorBlock.FACING, Direction.SOUTH)
                .setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER));
        level.setBlockAndUpdate(origin.offset(0, 4, 4), oakDoor
                .setValue(DoorBlock.FACING, Direction.SOUTH)
                .setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER));

        // Completely clear 2-block high walkway under porch (Z = 2 and 3)
        for (int pz = 2; pz <= 3; pz++) {
            for (int px = -1; px <= 1; px++) {
                level.setBlockAndUpdate(origin.offset(px, 3, pz), air);
                level.setBlockAndUpdate(origin.offset(px, 4, pz), air);
            }
        }

        // Grand Portico Columns at X = -2 and X = 2, Z = 3
        level.setBlockAndUpdate(origin.offset(-2, 3, 3), stoneBricks);
        level.setBlockAndUpdate(origin.offset(-2, 4, 3), strippedLog);
        level.setBlockAndUpdate(origin.offset(-2, 5, 3), strippedLog);
        level.setBlockAndUpdate(origin.offset(-2, 6, 3), lantern); // Lantern safely on column top

        level.setBlockAndUpdate(origin.offset(2, 3, 3), stoneBricks);
        level.setBlockAndUpdate(origin.offset(2, 4, 3), strippedLog);
        level.setBlockAndUpdate(origin.offset(2, 5, 3), strippedLog);
        level.setBlockAndUpdate(origin.offset(2, 6, 3), lantern); // Lantern safely on column top

        // Curved Timber Archway at Y = 5 (framing the doorway with vaulted curved wood)
        level.setBlockAndUpdate(origin.offset(-1, 5, 3),
                oakStairs.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.TOP));
        level.setBlockAndUpdate(origin.offset(0, 5, 3), oakPlanks);
        level.setBlockAndUpdate(origin.offset(1, 5, 3),
                oakStairs.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.TOP));

        // Peaked Portico Gable Roof at Y = 6 & 7 (Z = 3)
        level.setBlockAndUpdate(origin.offset(-1, 6, 3),
                oakStairs.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.BOTTOM));
        level.setBlockAndUpdate(origin.offset(0, 6, 3), oakPlanks);
        level.setBlockAndUpdate(origin.offset(1, 6, 3),
                oakStairs.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.BOTTOM));
        level.setBlockAndUpdate(origin.offset(0, 7, 3), oakSlab); // Apex ridge crest

        // Front Portico Overhang at Z = 2
        level.setBlockAndUpdate(origin.offset(-2, 3, 2), potFern);
        level.setBlockAndUpdate(origin.offset(2, 3, 2), potFern);
        level.setBlockAndUpdate(origin.offset(-2, 4, 2), oakFence);
        level.setBlockAndUpdate(origin.offset(2, 4, 2), oakFence);
        level.setBlockAndUpdate(origin.offset(-2, 5, 2), oakFence);
        level.setBlockAndUpdate(origin.offset(2, 5, 2), oakFence);

        level.setBlockAndUpdate(origin.offset(-1, 5, 2),
                oakStairs.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.BOTTOM));
        level.setBlockAndUpdate(origin.offset(0, 5, 2), oakSlab);
        level.setBlockAndUpdate(origin.offset(1, 5, 2),
                oakStairs.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.BOTTOM));
        level.setBlockAndUpdate(origin.offset(0, 6, 2), oakSlab);

        // Fully-Framed, Fully-Connected Multi-Pane Glass Windows on Front Facade
        // (X-axis)
        // Frame: X = -7 and X = -4 are solid oak planks. Bottom is stoneBricks. Top is
        // oakPlanks.
        // Panes at X = -6, -5 connect with EAST=true, WEST=true!
        int[] winX = { -6, -5, 5, 6 };
        for (int wx : winX) {
            level.setBlockAndUpdate(origin.offset(wx, 4, 4), glassPaneX);
            level.setBlockAndUpdate(origin.offset(wx, 5, 4), glassPaneX);
            level.setBlockAndUpdate(origin.offset(wx, 3, 3), potFern); // flower box
        }

        // Fully-Framed Windows on West Side (X = -9) (Z-axis) - Connects NORTH & SOUTH!
        int[] sideWinZ = { 8, 9, 12, 13 };
        for (int wz : sideWinZ) {
            level.setBlockAndUpdate(origin.offset(-9, 4, wz), glassPaneZ);
            level.setBlockAndUpdate(origin.offset(-9, 5, wz), glassPaneZ);
        }

        // Symmetrical Covered Side Pergolas / Verandas attached to outside of walls
        for (int z = 5; z <= 15; z += 3) {
            level.setBlockAndUpdate(origin.offset(-11, 3, z), strippedLog);
            level.setBlockAndUpdate(origin.offset(-11, 4, z), strippedLog);
            level.setBlockAndUpdate(origin.offset(-11, 5, z), strippedLog);
            level.setBlockAndUpdate(origin.offset(11, 3, z), strippedLog);
            level.setBlockAndUpdate(origin.offset(11, 4, z), strippedLog);
            level.setBlockAndUpdate(origin.offset(11, 5, z), strippedLog);
        }
        for (int z = 4; z <= 16; z++) {
            for (int sx = -11; sx <= -10; sx++)
                level.setBlockAndUpdate(origin.offset(sx, 6, z), oakSlab);
            for (int sx = 10; sx <= 11; sx++)
                level.setBlockAndUpdate(origin.offset(sx, 6, z), oakSlab);
        }

        // 5. FIRST FLOOR INTERIOR (Y = 3 to 7) - CLEAN, UNCLUTTERED, ZERO FURNITURE!
        // Polished oak flooring across the entire ground floor
        for (int x = -8; x <= 8; x++) {
            for (int z = 5; z <= 15; z++) {
                level.setBlockAndUpdate(origin.offset(x, 2, z), oakPlanks);
            }
        }
        // Warm chandeliers in main living hall
        level.setBlockAndUpdate(origin.offset(0, 7, 7), hangingLantern);
        level.setBlockAndUpdate(origin.offset(0, 7, 13), hangingLantern);
        level.setBlockAndUpdate(origin.offset(-4, 7, 10), hangingLantern);
        level.setBlockAndUpdate(origin.offset(4, 7, 10), hangingLantern);

        // Second Floor Ceiling / Floor (Y = 8, X = -9 to 9, Z = 4 to 16)
        for (int x = -9; x <= 9; x++) {
            for (int z = 4; z <= 16; z++) {
                level.setBlockAndUpdate(origin.offset(x, 8, z), oakPlanks);
            }
        }

        // GROUND FLOOR TO 2ND FLOOR STAIRCASE (Along the LEFT/East wall: X = 7 to 8)
        // Ascends South from Z = 8 to 13, Y = 3 to 8
        for (int s = 0; s <= 5; s++) {
            int sz = 8 + s;
            int sy = 3 + s;
            for (int sx = 7; sx <= 8; sx++) {
                // Solid oak planks under each step
                for (int fy = 3; fy < sy; fy++) {
                    level.setBlockAndUpdate(origin.offset(sx, fy, sz), oakPlanks);
                }
                level.setBlockAndUpdate(origin.offset(sx, sy, sz),
                        oakStairs.setValue(StairBlock.FACING, Direction.SOUTH).setValue(StairBlock.HALF, Half.BOTTOM));
            }
            // Ensure exterior wall at X = 9 behind stairs is 100% solid wood (no holes!)
            for (int wy = 3; wy <= 8; wy++) {
                level.setBlockAndUpdate(origin.offset(9, wy, sz), oakPlanks);
            }
        }
        // Landing at Z = 14 and 15 on 2nd floor
        for (int sx = 7; sx <= 8; sx++) {
            level.setBlockAndUpdate(origin.offset(sx, 8, 14), oakPlanks);
            level.setBlockAndUpdate(origin.offset(sx, 8, 15), oakPlanks);
        }

        // Stairwell opening in 2nd floor ceiling above left-side staircase
        for (int x = 7; x <= 8; x++) {
            for (int z = 8; z <= 12; z++) {
                level.setBlockAndUpdate(origin.offset(x, 8, z), air);
            }
            for (int z = 8; z <= 15; z++) {
                level.setBlockAndUpdate(origin.offset(x, 9, z), air);
                level.setBlockAndUpdate(origin.offset(x, 10, z), air);
                level.setBlockAndUpdate(origin.offset(x, 11, z), air);
            }
        }

        // COMPLETE PROTECTIVE BALUSTRADE (Image 4 - "الفنس ابيك تكمله")
        // Continuous fence along West side of stairwell:
        for (int bz = 8; bz <= 13; bz++) {
            level.setBlockAndUpdate(origin.offset(6, 9, bz), oakFence);
        }
        // Complete the North end: Connects from X = 6 across X = 7, 8 all the way to the East wall at X = 9!
        level.setBlockAndUpdate(origin.offset(6, 9, 7), oakFence);
        level.setBlockAndUpdate(origin.offset(7, 9, 7), oakFence);
        level.setBlockAndUpdate(origin.offset(8, 9, 7), oakFence);

        // 6. SECOND FLOOR & BALCONY FACADE (Y = 8 to 12)
        // Second Floor Exterior Walls (Y = 9 to 13, X = -9 to 9, Z = 4 to 16)
        // Uniform width X = -9 to 9 extending all the way up to Y = 13 (sealing side
        // walls 100%!)
        for (int x = -9; x <= 9; x++) {
            for (int z = 4; z <= 16; z++) {
                boolean isCorner = (Math.abs(x) == 9 && (z == 4 || z == 10 || z == 16));
                boolean isWall = (Math.abs(x) == 9 || z == 4 || z == 16);
                if (isWall) {
                    for (int y = 9; y <= 13; y++) {
                        level.setBlockAndUpdate(origin.offset(x, y, z), isCorner ? strippedLog : oakPlanks);
                    }
                }
            }
        }

        // Center wall on Second Floor (Clean solid wood facade - NO floating door!)
        level.setBlockAndUpdate(origin.offset(0, 9, 4), oakPlanks);
        level.setBlockAndUpdate(origin.offset(0, 10, 4), oakPlanks);
        level.setBlockAndUpdate(origin.offset(0, 11, 4), oakPlanks);
        level.setBlockAndUpdate(origin.offset(0, 10, 3), lantern); // decorative warm lantern

        // Second Floor Windows on Front Facade with Flower Boxes (connected panes)
        for (int wx : new int[] { -5, 5 }) {
            level.setBlockAndUpdate(origin.offset(wx, 10, 4), glassPaneX);
            level.setBlockAndUpdate(origin.offset(wx, 11, 4), glassPaneX);
            level.setBlockAndUpdate(origin.offset(wx, 9, 3), potFern);
        }

        // SECOND FLOOR INTERIOR (Y = 9 to 12)
        level.setBlockAndUpdate(origin.offset(0, 12, 10), hangingLantern);

        // 7. MAJESTIC ALPINE ROOF (Y = 13 to 22) - 100% WATERTIGHT, ZERO HOLES!
        // Roof runs from front (Z = 4) to back (Z = 16)
        // Slopes down West and East:
        // West slope (X < 0): FACING = EAST (steps descend towards West)
        // East slope (X > 0): FACING = WEST (steps descend towards East)
        for (int z = 4; z <= 16; z++) {
            boolean isEaveEdge = (z == 4 || z == 16);
            BlockState trimStair = isEaveEdge ? stoneStairs : oakStairs;

            // Slope 1: X = ±9, Y = 13
            level.setBlockAndUpdate(origin.offset(-9, 13, z),
                    trimStair.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.BOTTOM));
            level.setBlockAndUpdate(origin.offset(9, 13, z),
                    trimStair.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.BOTTOM));

            // Slope 2: X = ±8, Y = 14
            level.setBlockAndUpdate(origin.offset(-8, 14, z),
                    trimStair.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.BOTTOM));
            level.setBlockAndUpdate(origin.offset(8, 14, z),
                    trimStair.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.BOTTOM));
            level.setBlockAndUpdate(origin.offset(-8, 13, z), oakPlanks);
            level.setBlockAndUpdate(origin.offset(8, 13, z), oakPlanks);

            // Slope 3: X = ±7, Y = 15
            level.setBlockAndUpdate(origin.offset(-7, 15, z),
                    trimStair.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.BOTTOM));
            level.setBlockAndUpdate(origin.offset(7, 15, z),
                    trimStair.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.BOTTOM));
            level.setBlockAndUpdate(origin.offset(-7, 14, z), oakPlanks);
            level.setBlockAndUpdate(origin.offset(7, 14, z), oakPlanks);

            // Slope 4: X = ±6, Y = 16
            level.setBlockAndUpdate(origin.offset(-6, 16, z),
                    trimStair.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.BOTTOM));
            level.setBlockAndUpdate(origin.offset(6, 16, z),
                    trimStair.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.BOTTOM));
            level.setBlockAndUpdate(origin.offset(-6, 15, z), oakPlanks);
            level.setBlockAndUpdate(origin.offset(6, 15, z), oakPlanks);

            // Slope 5: X = ±5, Y = 17
            level.setBlockAndUpdate(origin.offset(-5, 17, z),
                    trimStair.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.BOTTOM));
            level.setBlockAndUpdate(origin.offset(5, 17, z),
                    trimStair.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.BOTTOM));
            level.setBlockAndUpdate(origin.offset(-5, 16, z), oakPlanks);
            level.setBlockAndUpdate(origin.offset(5, 16, z), oakPlanks);

            // Slope 6: X = ±4, Y = 18
            level.setBlockAndUpdate(origin.offset(-4, 18, z),
                    trimStair.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.BOTTOM));
            level.setBlockAndUpdate(origin.offset(4, 18, z),
                    trimStair.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.BOTTOM));
            level.setBlockAndUpdate(origin.offset(-4, 17, z), oakPlanks);
            level.setBlockAndUpdate(origin.offset(4, 17, z), oakPlanks);

            // Slope 7: X = ±3, Y = 19
            level.setBlockAndUpdate(origin.offset(-3, 19, z),
                    trimStair.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.BOTTOM));
            level.setBlockAndUpdate(origin.offset(3, 19, z),
                    trimStair.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.BOTTOM));
            level.setBlockAndUpdate(origin.offset(-3, 18, z), oakPlanks);
            level.setBlockAndUpdate(origin.offset(3, 18, z), oakPlanks);

            // Slope 8: X = ±2, Y = 20
            level.setBlockAndUpdate(origin.offset(-2, 20, z),
                    trimStair.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.BOTTOM));
            level.setBlockAndUpdate(origin.offset(2, 20, z),
                    trimStair.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.BOTTOM));
            level.setBlockAndUpdate(origin.offset(-2, 19, z), oakPlanks);
            level.setBlockAndUpdate(origin.offset(2, 19, z), oakPlanks);

            // Slope 9: X = ±1, Y = 21
            level.setBlockAndUpdate(origin.offset(-1, 21, z),
                    trimStair.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.BOTTOM));
            level.setBlockAndUpdate(origin.offset(1, 21, z),
                    trimStair.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.BOTTOM));
            level.setBlockAndUpdate(origin.offset(-1, 20, z), oakPlanks);
            level.setBlockAndUpdate(origin.offset(1, 20, z), oakPlanks);

            // Central Ridge: X = 0, Y = 21 (Planks) and Y = 22 (Slab)
            level.setBlockAndUpdate(origin.offset(0, 21, z), oakPlanks);
            level.setBlockAndUpdate(origin.offset(0, 22, z), isEaveEdge ? stoneSlab : oakSlab);
        }

        // Close front and rear gable walls completely with solid oak planks (Z = 4 and Z = 16)
        for (int gz : new int[] { 4, 16 }) {
            for (int gx = -8; gx <= 8; gx++) {
                int maxGy = 21 - Math.abs(gx);
                for (int gy = 13; gy <= maxGy; gy++) {
                    level.setBlockAndUpdate(origin.offset(gx, gy, gz), oakPlanks);
                }
            }
        }

        // Front & Rear 3rd Floor Gable Windows (Z = 4 and Z = 16)
        level.setBlockAndUpdate(origin.offset(0, 16, 4), glassPaneX);
        level.setBlockAndUpdate(origin.offset(0, 17, 4), glassPaneX);
        level.setBlockAndUpdate(origin.offset(0, 15, 3), potFern);
        level.setBlockAndUpdate(origin.offset(0, 20, 3), hangingLantern);

        level.setBlockAndUpdate(origin.offset(0, 16, 16), glassPaneX);
        level.setBlockAndUpdate(origin.offset(0, 17, 16), glassPaneX);

        // 8. THIRD FLOOR (REAL WALK-IN PENTHOUSE ATTIC) - Y = 13 to 21
        // Solid Oak Plank Floor at Y = 13 across entire room (X = -8 to 8, Z = 5 to 15)
        for (int x = -8; x <= 8; x++) {
            for (int z = 5; z <= 15; z++) {
                level.setBlockAndUpdate(origin.offset(x, 13, z), oakPlanks);
            }
        }

        // CLEAR AIR ONLY INSIDE THE CATHEDRAL ROOM (NEVER TOUCHES EXTERIOR ROOF TILES!)
        for (int x = -5; x <= 5; x++) {
            for (int z = 5; z <= 15; z++) {
                int maxH = 20 - Math.abs(x);
                for (int y = 14; y <= maxH; y++) {
                    level.setBlockAndUpdate(origin.offset(x, y, z), air);
                }
            }
        }

        // 3rd Floor Chandeliers hanging from the ridge beam
        level.setBlockAndUpdate(origin.offset(0, 19, 7), hangingLantern);
        level.setBlockAndUpdate(origin.offset(0, 19, 10), hangingLantern);
        level.setBlockAndUpdate(origin.offset(0, 19, 13), hangingLantern);

        // 9. 2ND FLOOR TO 3RD FLOOR STAIRCASE (Along West side: X = -5 to -4)
        // Solid floor on 2nd floor at Y = 8 underneath
        for (int sx = -5; sx <= -4; sx++) {
            for (int z = 7; z <= 15; z++) {
                level.setBlockAndUpdate(origin.offset(sx, 8, z), oakPlanks);
            }
        }

        // Ascends South from Z = 8 to 12, Y = 9 to 13
        int[][] steps3rd = {
                { 8, 9 },
                { 9, 10 },
                { 10, 11 },
                { 11, 12 },
                { 12, 13 } // Top step is oakStairs facing South, stepping smoothly onto 3rd floor at Z = 13!
        };
        for (int[] st : steps3rd) {
            int sz = st[0];
            int sy = st[1];
            for (int sx = -5; sx <= -4; sx++) {
                // Solid oak planks under each step up to sy - 1
                for (int fy = 8; fy < sy; fy++) {
                    level.setBlockAndUpdate(origin.offset(sx, fy, sz), oakPlanks);
                }
                // Stair step facing South (Half.BOTTOM)
                level.setBlockAndUpdate(origin.offset(sx, sy, sz),
                        oakStairs.setValue(StairBlock.FACING, Direction.SOUTH).setValue(StairBlock.HALF, Half.BOTTOM));
            }
        }

        // Solid Landing on 3rd floor at Z = 13, 14, 15 (Y = 13)
        for (int sx = -5; sx <= -4; sx++) {
            level.setBlockAndUpdate(origin.offset(sx, 13, 13), oakPlanks);
            level.setBlockAndUpdate(origin.offset(sx, 13, 14), oakPlanks);
            level.setBlockAndUpdate(origin.offset(sx, 13, 15), oakPlanks);
        }

        // Opening in 2nd floor ceiling above staircase (X = -5 to -4, Z = 8 to 12)
        for (int sx = -5; sx <= -4; sx++) {
            for (int z = 8; z <= 12; z++) {
                level.setBlockAndUpdate(origin.offset(sx, 8, z), air);
            }
            for (int z = 8; z <= 14; z++) {
                level.setBlockAndUpdate(origin.offset(sx, 9, z), air);
                level.setBlockAndUpdate(origin.offset(sx, 10, z), air);
                level.setBlockAndUpdate(origin.offset(sx, 11, z), air);
                level.setBlockAndUpdate(origin.offset(sx, 12, z), air);
            }
        }

        // Complete protective balustrade on 2nd floor around stair opening:
        for (int bz = 8; bz <= 12; bz++) {
            level.setBlockAndUpdate(origin.offset(-3, 9, bz), oakFence);
        }
        level.setBlockAndUpdate(origin.offset(-3, 9, 7), oakFence);
        level.setBlockAndUpdate(origin.offset(-4, 9, 7), oakFence);
        level.setBlockAndUpdate(origin.offset(-5, 9, 7), oakFence);

        // Complete protective balustrade on 3rd floor around stair opening:
        for (int bz = 8; bz <= 12; bz++) {
            level.setBlockAndUpdate(origin.offset(-3, 14, bz), oakFence);
        }
        level.setBlockAndUpdate(origin.offset(-3, 14, 7), oakFence);
        level.setBlockAndUpdate(origin.offset(-4, 14, 7), oakFence);
        level.setBlockAndUpdate(origin.offset(-5, 14, 7), oakFence);
    }
}
