package com.backpackmod;

import java.util.*;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

/** Hammer AOE, Tree Capitator and Vein Miner. Extra blocks are broken without costing tool durability. */
public final class BreakActions {
    private static final TagKey<Block> ORES = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("c", "ores"));

    /** PlayerBlockBreakEvents.BEFORE: return false to cancel the vanilla break (we handled it). */
    public static boolean before(Level level, Player player, BlockPos pos, BlockState state, BlockEntity be) {
        if (!(level instanceof ServerLevel sl) || !(player instanceof ServerPlayer sp)) return true;
        ItemStack tool = sp.getMainHandItem();

        if (sp.isShiftKeyDown() && canBreak(sl, sp, pos, state, tool)) {
            if (state.is(BlockTags.LOGS) && tool.is(ItemTags.AXES)) {       // Tree Capitator
                chain(sl, sp, pos, tool, s -> s.is(BlockTags.LOGS), 128);
                return false;
            }
            if (state.is(ORES) && tool.is(ItemTags.PICKAXES)) {             // Vein Miner (pickaxe or hammer)
                Block ore = state.getBlock();
                chain(sl, sp, pos, tool, s -> s.is(ore), 64);
                return false;
            }
        }

        if (tool.getItem() instanceof HammerItem h && h.size() > 1) {       // Hammer AOE
            Direction face = face(sp, pos);
            for (BlockPos p : HammerItem.area(pos, face, h.size())) {
                if (p.equals(pos)) continue;
                BlockState s = sl.getBlockState(p);
                if (tool.getDestroySpeed(s) > 1.0f && canBreak(sl, sp, p, s, tool)) breakNoDamage(sl, sp, p, tool);
            }
        }
        return true; // origin block breaks normally (Fortune / Silk Touch apply via the tool)
    }

    private static Direction face(ServerPlayer p, BlockPos pos) {
        HitResult hr = p.pick(p.blockInteractionRange(), 1.0f, false);
        if (hr instanceof BlockHitResult b && b.getBlockPos().equals(pos)) return b.getDirection();
        return Direction.orderedByNearest(p)[0];
    }

    private static boolean canBreak(ServerLevel l, ServerPlayer p, BlockPos pos, BlockState s, ItemStack tool) {
        if (s.isAir() || s.getDestroySpeed(l, pos) < 0 || !l.mayInteract(p, pos)) return false;
        return !s.requiresCorrectToolForDrops() || tool.isCorrectToolForDrops(s);
    }

    private static void chain(ServerLevel l, ServerPlayer p, BlockPos origin, ItemStack tool, Predicate<BlockState> match, int max) {
        Set<BlockPos> seen = new HashSet<>(List.of(origin));
        ArrayDeque<BlockPos> queue = new ArrayDeque<>(List.of(origin));
        List<BlockPos> found = new ArrayList<>();
        while (!queue.isEmpty() && found.size() < max) {
            BlockPos c = queue.poll();
            found.add(c);
            for (int dx = -1; dx <= 1; dx++) for (int dy = -1; dy <= 1; dy++) for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dy == 0 && dz == 0) continue;
                BlockPos n = c.offset(dx, dy, dz);
                if (!seen.add(n)) continue;
                BlockState s = l.getBlockState(n);
                if (match.test(s) && canBreak(l, p, n, s, tool)) queue.add(n);
            }
        }
        for (BlockPos f : found) breakNoDamage(l, p, f, tool);
    }

    /** Drops resolve through the held tool, so Fortune and Silk Touch apply to every block. */
    private static void breakNoDamage(ServerLevel l, ServerPlayer p, BlockPos pos, ItemStack tool) {
        BlockState s = l.getBlockState(pos);
        if (s.isAir()) return;
        BlockEntity be = l.getBlockEntity(pos);
        if (!p.isCreative()) Block.dropResources(s, l, pos, be, p, tool); // also spawns ore XP
        l.destroyBlock(pos, false, p);
    }
    private BreakActions() {}
}
