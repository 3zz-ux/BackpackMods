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
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;

/**
 * Pixel Portal Item:
 * Spawns a grand, majestic Japanese Pagoda Torii Nether Portal Shrine:
 * - Solid tiered platform of Deepslate Bricks and Polished Blackstone
 * - 5-wide front grand approach steps with stone brick stairs
 * - Grand portal frame of Obsidian and Crying Obsidian with active Nether Portal
 * - Flanking architectural Torii pillars with Chiseled Stone & Deepslate Walls
 * - Double-tiered sweeping Pagoda roofs with upturned oriental eaves & corner horns
 * - Hanging soul lanterns from ornamental chains for an ethereal glow
 */
public class PixelPortalItem extends Item {
    public PixelPortalItem(Properties properties) {
        super(properties.stacksTo(1).rarity(Rarity.EPIC));
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        Player player = ctx.getPlayer();
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        BlockPos origin = ctx.getClickedPos().above();
        buildGrandPagodaPortal(level, origin);

        level.playSound(null, origin, SoundEvents.PORTAL_TRIGGER, SoundSource.BLOCKS, 1.0f, 1.0f);
        level.playSound(null, origin, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.BLOCKS, 1.0f, 1.0f);
        if (player != null) {
            player.sendSystemMessage(Component.literal("§5§l[Pixel Portal] §aتم تشييد معبد الباغودا النذري الأسطوري بتصميم خرافي!"));
            if (!player.isCreative()) {
                ctx.getItemInHand().shrink(1);
            }
        }

        return InteractionResult.SUCCESS;
    }

    private void buildGrandPagodaPortal(Level level, BlockPos origin) {
        BlockState stoneBricks = Blocks.STONE_BRICKS.defaultBlockState();
        BlockState mossyBricks = Blocks.MOSSY_STONE_BRICKS.defaultBlockState();
        BlockState chiseledStone = Blocks.CHISELED_STONE_BRICKS.defaultBlockState();
        BlockState stoneWall = Blocks.STONE_BRICK_WALL.defaultBlockState();
        BlockState stoneStairs = Blocks.STONE_BRICK_STAIRS.defaultBlockState();

        BlockState deepslateBricks = Blocks.DEEPSLATE_BRICKS.defaultBlockState();
        BlockState deepslateStairs = Blocks.DEEPSLATE_BRICK_STAIRS.defaultBlockState();
        BlockState deepslateSlab = Blocks.DEEPSLATE_BRICK_SLAB.defaultBlockState();
        BlockState deepslateWall = Blocks.DEEPSLATE_BRICK_WALL.defaultBlockState();

        BlockState obsidian = Blocks.OBSIDIAN.defaultBlockState();
        BlockState cryingObsidian = Blocks.CRYING_OBSIDIAN.defaultBlockState();
        BlockState portal = Blocks.NETHER_PORTAL.defaultBlockState().setValue(NetherPortalBlock.AXIS, Direction.Axis.X);

        BlockState soulLantern = Blocks.SOUL_LANTERN.defaultBlockState();
        BlockState hangingSoulLantern = Blocks.SOUL_LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, true);
        BlockState lantern = Blocks.LANTERN.defaultBlockState();
        BlockState chain = getBlockState("iron_chain", getBlockState("chain", Blocks.IRON_BARS.defaultBlockState()));
        BlockState azalea = Blocks.FLOWERING_AZALEA_LEAVES.defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();

        // 1. Clear Space: X: -8 to 8, Z: -5 to 5, Y: 0 to 18
        for (int x = -8; x <= 8; x++) {
            for (int z = -5; z <= 5; z++) {
                for (int y = 0; y <= 18; y++) {
                    level.setBlockAndUpdate(origin.offset(x, y, z), air);
                }
            }
        }

        // 2. Solid Deep Foundation & Plinth (Y = -2 to 0, X = -6 to 6, Z = -3 to 3)
        for (int x = -6; x <= 6; x++) {
            for (int z = -3; z <= 3; z++) {
                for (int fy = -2; fy <= -1; fy++) {
                    level.setBlockAndUpdate(origin.offset(x, fy, z), deepslateBricks);
                }
                boolean border = (Math.abs(x) == 6 || Math.abs(z) == 3);
                level.setBlockAndUpdate(origin.offset(x, 0, z), border ? stoneBricks : deepslateBricks);
            }
        }

        // Front 5-wide stone approach steps (Z = -4 and -5, Y = 0)
        for (int sx = -2; sx <= 2; sx++) {
            level.setBlockAndUpdate(origin.offset(sx, 0, -4),
                    stoneStairs.setValue(StairBlock.FACING, Direction.NORTH).setValue(StairBlock.HALF, Half.BOTTOM));
        }

        // 4 Corner Pedestals with Soul Lanterns (X = ±5, Z = ±3, Y = 1 to 2)
        int[][] corners = { { -5, -3 }, { 5, -3 }, { -5, 3 }, { 5, 3 } };
        for (int[] c : corners) {
            int cx = c[0], cz = c[1];
            level.setBlockAndUpdate(origin.offset(cx, 1, cz), chiseledStone);
            level.setBlockAndUpdate(origin.offset(cx, 2, cz), stoneWall);
            level.setBlockAndUpdate(origin.offset(cx, 3, cz), soulLantern);
        }

        // Decorative azalea foliage around terrace edges
        level.setBlockAndUpdate(origin.offset(-6, 1, -2), azalea);
        level.setBlockAndUpdate(origin.offset(6, 1, -2), azalea);
        level.setBlockAndUpdate(origin.offset(-6, 1, 2), azalea);
        level.setBlockAndUpdate(origin.offset(6, 1, 2), azalea);

        // 3. Ornate Obsidian & Crying Obsidian Nether Portal Frame (Z = 0, Y = 1 to 7)
        // Bottom frame (Y = 1, X = -2 to 2)
        level.setBlockAndUpdate(origin.offset(-2, 1, 0), obsidian);
        level.setBlockAndUpdate(origin.offset(-1, 1, 0), cryingObsidian);
        level.setBlockAndUpdate(origin.offset(0, 1, 0), obsidian);
        level.setBlockAndUpdate(origin.offset(1, 1, 0), cryingObsidian);
        level.setBlockAndUpdate(origin.offset(2, 1, 0), obsidian);

        // Side frames (Y = 2 to 6)
        level.setBlockAndUpdate(origin.offset(-2, 2, 0), cryingObsidian);
        level.setBlockAndUpdate(origin.offset(-2, 3, 0), obsidian);
        level.setBlockAndUpdate(origin.offset(-2, 4, 0), cryingObsidian);
        level.setBlockAndUpdate(origin.offset(-2, 5, 0), obsidian);
        level.setBlockAndUpdate(origin.offset(-2, 6, 0), cryingObsidian);

        level.setBlockAndUpdate(origin.offset(2, 2, 0), obsidian);
        level.setBlockAndUpdate(origin.offset(2, 3, 0), cryingObsidian);
        level.setBlockAndUpdate(origin.offset(2, 4, 0), obsidian);
        level.setBlockAndUpdate(origin.offset(2, 5, 0), cryingObsidian);
        level.setBlockAndUpdate(origin.offset(2, 6, 0), obsidian);

        // Top frame (Y = 7, X = -2 to 2)
        level.setBlockAndUpdate(origin.offset(-2, 7, 0), obsidian);
        level.setBlockAndUpdate(origin.offset(-1, 7, 0), cryingObsidian);
        level.setBlockAndUpdate(origin.offset(0, 7, 0), obsidian);
        level.setBlockAndUpdate(origin.offset(1, 7, 0), cryingObsidian);
        level.setBlockAndUpdate(origin.offset(2, 7, 0), obsidian);

        // Active Glowing Nether Portal Blocks inside (X = -1 to 1, Y = 2 to 6)
        for (int x = -1; x <= 1; x++) {
            for (int y = 2; y <= 6; y++) {
                level.setBlockAndUpdate(origin.offset(x, y, 0), portal);
            }
        }

        // 4. Grand Flanking Torii Shrine Pillars (Left: X = -4, -3; Right: X = 3, 4, Y = 1 to 8)
        for (int y = 1; y <= 8; y++) {
            for (int z = -1; z <= 1; z++) {
                BlockState mat = (y == 1 || y == 8) ? chiseledStone : ((y % 3 == 0) ? mossyBricks : deepslateBricks);
                level.setBlockAndUpdate(origin.offset(-3, y, z), mat);
                level.setBlockAndUpdate(origin.offset(-4, y, z), mat);
                level.setBlockAndUpdate(origin.offset(3, y, z), mat);
                level.setBlockAndUpdate(origin.offset(4, y, z), mat);
            }
        }
        // Buttress walls on outside of pillars
        for (int y = 1; y <= 4; y++) {
            level.setBlockAndUpdate(origin.offset(-5, y, 0), deepslateWall);
            level.setBlockAndUpdate(origin.offset(5, y, 0), deepslateWall);
        }

        // Hanging Chains with Soul Lanterns beside the portal
        level.setBlockAndUpdate(origin.offset(-3, 6, -2), chain);
        level.setBlockAndUpdate(origin.offset(-3, 5, -2), hangingSoulLantern);
        level.setBlockAndUpdate(origin.offset(3, 6, -2), chain);
        level.setBlockAndUpdate(origin.offset(3, 5, -2), hangingSoulLantern);
        level.setBlockAndUpdate(origin.offset(-3, 6, 2), chain);
        level.setBlockAndUpdate(origin.offset(-3, 5, 2), hangingSoulLantern);
        level.setBlockAndUpdate(origin.offset(3, 6, 2), chain);
        level.setBlockAndUpdate(origin.offset(3, 5, 2), hangingSoulLantern);

        // 5. Grand Lower Sweeping Pagoda Roof (Y = 8 to 10)
        // Main eaves platform at Y = 8 (X = -6 to 6, Z = -2 to 2)
        for (int x = -6; x <= 6; x++) {
            for (int z = -2; z <= 2; z++) {
                if (Math.abs(x) >= 2 || Math.abs(z) >= 1) {
                    level.setBlockAndUpdate(origin.offset(x, 8, z), deepslateBricks);
                }
            }
        }

        // Curved Eave Trim with Deepslate Stairs at Y = 9:
        // North eave (Z = -2) slopes down North
        for (int x = -5; x <= 5; x++) {
            level.setBlockAndUpdate(origin.offset(x, 9, -2),
                    deepslateStairs.setValue(StairBlock.FACING, Direction.NORTH).setValue(StairBlock.HALF, Half.BOTTOM));
        }
        // South eave (Z = 2) slopes down South
        for (int x = -5; x <= 5; x++) {
            level.setBlockAndUpdate(origin.offset(x, 9, 2),
                    deepslateStairs.setValue(StairBlock.FACING, Direction.SOUTH).setValue(StairBlock.HALF, Half.BOTTOM));
        }
        // West eave (X = -6) slopes down West
        for (int z = -2; z <= 2; z++) {
            level.setBlockAndUpdate(origin.offset(-6, 9, z),
                    deepslateStairs.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.BOTTOM));
        }
        // East eave (X = 6) slopes down East
        for (int z = -2; z <= 2; z++) {
            level.setBlockAndUpdate(origin.offset(6, 9, z),
                    deepslateStairs.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.BOTTOM));
        }

        // Flared Upturned Oriental Horns at all 4 corners (Y = 10)
        level.setBlockAndUpdate(origin.offset(-7, 10, -3),
                deepslateStairs.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.BOTTOM));
        level.setBlockAndUpdate(origin.offset(7, 10, -3),
                deepslateStairs.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.BOTTOM));
        level.setBlockAndUpdate(origin.offset(-7, 10, 3),
                deepslateStairs.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.BOTTOM));
        level.setBlockAndUpdate(origin.offset(7, 10, 3),
                deepslateStairs.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.BOTTOM));

        // Corner Hanging Soul Lanterns under the horns
        level.setBlockAndUpdate(origin.offset(-7, 9, -3), hangingSoulLantern);
        level.setBlockAndUpdate(origin.offset(7, 9, -3), hangingSoulLantern);
        level.setBlockAndUpdate(origin.offset(-7, 9, 3), hangingSoulLantern);
        level.setBlockAndUpdate(origin.offset(7, 9, 3), hangingSoulLantern);

        // 6. Upper Shrine Pavilion (Y = 10 to 12)
        for (int y = 10; y <= 12; y++) {
            level.setBlockAndUpdate(origin.offset(-3, y, 0), chiseledStone);
            level.setBlockAndUpdate(origin.offset(-2, y, 0), stoneBricks);
            level.setBlockAndUpdate(origin.offset(0, y, 0), (y == 11) ? cryingObsidian : stoneBricks);
            level.setBlockAndUpdate(origin.offset(2, y, 0), stoneBricks);
            level.setBlockAndUpdate(origin.offset(3, y, 0), chiseledStone);
        }
        level.setBlockAndUpdate(origin.offset(0, 11, -1), lantern);
        level.setBlockAndUpdate(origin.offset(0, 11, 1), lantern);

        // 7. Grand Curved Upper Pagoda Roof (Y = 13 to 16)
        // Tier 1 Base (Y = 13, X = -5 to 5, Z = -2 to 2)
        for (int x = -5; x <= 5; x++) {
            for (int z = -2; z <= 2; z++) {
                level.setBlockAndUpdate(origin.offset(x, 13, z), deepslateBricks);
            }
        }
        // Tier 2 (Y = 14, X = -4 to 4, Z = -1 to 1)
        for (int x = -4; x <= 4; x++) {
            for (int z = -1; z <= 1; z++) {
                level.setBlockAndUpdate(origin.offset(x, 14, z), deepslateBricks);
            }
        }
        // Top Ridge (Y = 15, X = -3 to 3, Z = 0)
        for (int x = -3; x <= 3; x++) {
            level.setBlockAndUpdate(origin.offset(x, 15, 0), deepslateSlab);
        }

        // Top Horns (Y = 15 at X = ±4, Z = 0)
        level.setBlockAndUpdate(origin.offset(-4, 15, 0),
                deepslateStairs.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.BOTTOM));
        level.setBlockAndUpdate(origin.offset(4, 15, 0),
                deepslateStairs.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.BOTTOM));
        level.setBlockAndUpdate(origin.offset(0, 16, 0), soulLantern);
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
