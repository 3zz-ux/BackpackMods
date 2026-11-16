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
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;

/**
 * Pixel Portal Item:
 * Crafted from Obsidian and Crying Obsidian. When placed on the ground,
 * it instantly constructs the ancient Japanese / Oriental Pagoda Torii Gate
 * with an activated glowing Nether Portal matching the provided concept.
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
        buildPagodaPortal(level, origin);

        level.playSound(null, origin, SoundEvents.PORTAL_TRIGGER, SoundSource.BLOCKS, 1.0f, 1.0f);
        level.playSound(null, origin, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.BLOCKS, 1.0f, 1.0f);
        if (player != null) {
            player.sendSystemMessage(Component.literal("§5§l[Pixel Portal] §aتم استدعاء بوابة الباغودا الأسطورية بنجاح!"));
            if (!player.isCreative()) {
                ctx.getItemInHand().shrink(1);
            }
        }

        return InteractionResult.SUCCESS;
    }

    private void buildPagodaPortal(Level level, BlockPos origin) {
        BlockState stoneBricks = Blocks.STONE_BRICKS.defaultBlockState();
        BlockState mossyBricks = Blocks.MOSSY_STONE_BRICKS.defaultBlockState();
        BlockState crackedBricks = Blocks.CRACKED_STONE_BRICKS.defaultBlockState();
        BlockState chiseledBricks = Blocks.CHISELED_STONE_BRICKS.defaultBlockState();
        BlockState stoneBrickWall = Blocks.STONE_BRICK_WALL.defaultBlockState();
        BlockState deepslateStairs = Blocks.DEEPSLATE_BRICK_STAIRS.defaultBlockState();
        BlockState deepslateSlab = Blocks.DEEPSLATE_BRICK_SLAB.defaultBlockState();
        BlockState deepslateBricks = Blocks.DEEPSLATE_BRICKS.defaultBlockState();
        BlockState obsidian = Blocks.OBSIDIAN.defaultBlockState();
        BlockState portal = Blocks.NETHER_PORTAL.defaultBlockState().setValue(NetherPortalBlock.AXIS, Direction.Axis.X);
        BlockState azalea = Blocks.FLOWERING_AZALEA_LEAVES.defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();

        // 1. Clear Space: X: -6 to 6, Z: -4 to 4, Y: 0 to 16
        for (int x = -7; x <= 7; x++) {
            for (int z = -4; z <= 4; z++) {
                for (int y = 0; y <= 16; y++) {
                    level.setBlockAndUpdate(origin.offset(x, y, z), air);
                }
            }
        }

        // 2. Foundation & Front Stone Steps (Y = 0)
        for (int x = -5; x <= 5; x++) {
            for (int z = -2; z <= 2; z++) {
                boolean moss = (Math.abs(x * z) % 3 == 0);
                level.setBlockAndUpdate(origin.offset(x, 0, z), moss ? mossyBricks : stoneBricks);
            }
        }
        // Front steps
        for (int sx = -3; sx <= 3; sx++) {
            level.setBlockAndUpdate(origin.offset(sx, 0, -3), Blocks.STONE_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH));
            level.setBlockAndUpdate(origin.offset(sx, 1, -2), Blocks.STONE_BRICK_STAIRS.defaultBlockState().setValue(StairBlock.FACING, Direction.NORTH));
        }

        // Bushes around base
        level.setBlockAndUpdate(origin.offset(-5, 1, -2), azalea);
        level.setBlockAndUpdate(origin.offset(5, 1, -2), azalea);
        level.setBlockAndUpdate(origin.offset(-5, 1, 2), azalea);
        level.setBlockAndUpdate(origin.offset(5, 1, 2), azalea);

        // 3. Central Obsidian Nether Portal Frame & Portal Blocks (Z = 0)
        // Bottom frame (Y = 1, X = -2 to 2)
        for (int x = -2; x <= 2; x++) level.setBlockAndUpdate(origin.offset(x, 1, 0), obsidian);
        // Top frame (Y = 7, X = -2 to 2)
        for (int x = -2; x <= 2; x++) level.setBlockAndUpdate(origin.offset(x, 7, 0), obsidian);
        // Side frames (X = -2 and X = 2, Y = 2 to 6)
        for (int y = 2; y <= 6; y++) {
            level.setBlockAndUpdate(origin.offset(-2, y, 0), obsidian);
            level.setBlockAndUpdate(origin.offset(2, y, 0), obsidian);
        }
        // Active glowing portal blocks inside (X = -1 to 1, Y = 2 to 6)
        for (int x = -1; x <= 1; x++) {
            for (int y = 2; y <= 6; y++) {
                level.setBlockAndUpdate(origin.offset(x, y, 0), portal);
            }
        }

        // 4. Grand Flanking Stone Pillars (Left: X=-4, -3; Right: X=3, 4)
        for (int y = 1; y <= 8; y++) {
            for (int z = -1; z <= 1; z++) {
                BlockState mat = (y % 4 == 0) ? chiseledBricks : (y % 3 == 0 ? mossyBricks : stoneBricks);
                level.setBlockAndUpdate(origin.offset(-3, y, z), mat);
                level.setBlockAndUpdate(origin.offset(-4, y, z), mat);
                level.setBlockAndUpdate(origin.offset(3, y, z), mat);
                level.setBlockAndUpdate(origin.offset(4, y, z), mat);
            }
        }
        // Outer pillar buttress walls
        for (int y = 1; y <= 4; y++) {
            level.setBlockAndUpdate(origin.offset(-5, y, 0), stoneBrickWall);
            level.setBlockAndUpdate(origin.offset(5, y, 0), stoneBrickWall);
        }

        // 5. Middle Pagoda Eaves (Lower Sweeping Roof: Y = 8 to 9)
        for (int x = -6; x <= 6; x++) {
            for (int z = -2; z <= 2; z++) {
                if (Math.abs(x) >= 2) {
                    level.setBlockAndUpdate(origin.offset(x, 8, z), deepslateBricks);
                }
            }
        }
        // Flared upturned pagoda corners (oriental curved tips)
        level.setBlockAndUpdate(origin.offset(-6, 9, -2), deepslateStairs.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.BOTTOM));
        level.setBlockAndUpdate(origin.offset(6, 9, -2), deepslateStairs.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.BOTTOM));
        level.setBlockAndUpdate(origin.offset(-6, 9, 2), deepslateStairs.setValue(StairBlock.FACING, Direction.EAST).setValue(StairBlock.HALF, Half.BOTTOM));
        level.setBlockAndUpdate(origin.offset(6, 9, 2), deepslateStairs.setValue(StairBlock.FACING, Direction.WEST).setValue(StairBlock.HALF, Half.BOTTOM));

        // 6. Upper Pavilion Shrine (Y = 9 to 11)
        for (int y = 9; y <= 11; y++) {
            level.setBlockAndUpdate(origin.offset(-3, y, 0), chiseledBricks);
            level.setBlockAndUpdate(origin.offset(-2, y, 0), stoneBricks);
            level.setBlockAndUpdate(origin.offset(2, y, 0), stoneBricks);
            level.setBlockAndUpdate(origin.offset(3, y, 0), chiseledBricks);
        }

        // 7. Grand Curved Upper Pagoda Roof (Y = 12 to 14)
        for (int x = -5; x <= 5; x++) {
            for (int z = -2; z <= 2; z++) {
                level.setBlockAndUpdate(origin.offset(x, 12, z), deepslateBricks);
            }
        }
        // Roof Tier 2
        for (int x = -4; x <= 4; x++) {
            for (int z = -1; z <= 1; z++) {
                level.setBlockAndUpdate(origin.offset(x, 13, z), deepslateBricks);
            }
        }
        // Top Ridge beam (Y = 14)
        for (int x = -3; x <= 3; x++) {
            level.setBlockAndUpdate(origin.offset(x, 14, 0), deepslateSlab);
        }
        // Sweeping top pagoda horns / corners
        level.setBlockAndUpdate(origin.offset(-5, 13, -2), deepslateStairs.setValue(StairBlock.FACING, Direction.EAST));
        level.setBlockAndUpdate(origin.offset(5, 13, -2), deepslateStairs.setValue(StairBlock.FACING, Direction.WEST));
        level.setBlockAndUpdate(origin.offset(-5, 13, 2), deepslateStairs.setValue(StairBlock.FACING, Direction.EAST));
        level.setBlockAndUpdate(origin.offset(5, 13, 2), deepslateStairs.setValue(StairBlock.FACING, Direction.WEST));
    }
}
