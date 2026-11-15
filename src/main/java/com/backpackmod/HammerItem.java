package com.backpackmod;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;

/** Pickaxe-like unbreakable hammer. Tool/enchantable behaviour comes from Item.Properties#pickaxe + item tags. */
public class HammerItem extends Item {
    private final int size;
    public HammerItem(Properties p, int size) { super(p); this.size = size; }
    public int size() { return size; }

    /** Square of size x size blocks in the plane perpendicular to the struck face. */
    public static List<BlockPos> area(BlockPos c, Direction face, int size) {
        int r = (size - 1) / 2;
        List<BlockPos> out = new ArrayList<>();
        for (int a = -r; a <= r; a++)
            for (int b = -r; b <= r; b++)
                switch (face.getAxis()) {
                    case X -> out.add(c.offset(0, a, b));
                    case Y -> out.add(c.offset(a, 0, b));
                    case Z -> out.add(c.offset(a, b, 0));
                }
        return out;
    }
}
