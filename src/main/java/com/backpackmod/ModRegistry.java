package com.backpackmod;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Function;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.RecipeSerializer;

public final class ModRegistry {
    public static final EnumMap<BackpackTier, Item> BACKPACKS = new EnumMap<>(BackpackTier.class);
    public static final List<Item> HAMMERS = new ArrayList<>();
    public static DataComponentType<BackpackUpgrades> UPGRADES;
    public static AttachmentType<ItemStack> EQUIPPED;
    public static MenuType<BackpackMenu> BACKPACK_MENU;
    public static RecipeSerializer<UpgradeRecipe> UPGRADE_RECIPE;
    public static AttachmentType<Boolean> XP_MARK;
    public static Item GOLD_COIN;
    public static Item PIXEL_STORAGE;
    public static Item PIXEL_PORTAL;
    public static Item PIXEL_HOUSE;

    private static Item item(String name, Function<Item.Properties, Item> factory, Item.Properties props) {
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, BackpackMod.id(name));
        return Registry.register(BuiltInRegistries.ITEM, key, factory.apply(props.setId(key)));
    }

    private record Hammer(String name, ToolMaterial material, int size) {}

    public static void init() {
        UPGRADES = Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, BackpackMod.id("upgrades"),
                DataComponentType.<BackpackUpgrades>builder()
                        .persistent(BackpackUpgrades.CODEC)
                        .networkSynchronized(BackpackUpgrades.STREAM_CODEC).build());

        // Equipped backpack: persistent, synced to everyone tracking the player (for the 3D back model),
        // copied on respawn (we drop + clear it ourselves on death when keepInventory is off).
        EQUIPPED = AttachmentRegistry.create(BackpackMod.id("equipped_backpack"), b -> b
                .persistent(ItemStack.OPTIONAL_CODEC)
                .syncWith(ItemStack.OPTIONAL_STREAM_CODEC, AttachmentSyncPredicate.all())
                .copyOnDeath());

        XP_MARK = AttachmentRegistry.create(BackpackMod.id("xp_x6"), b -> b.persistent(Codec.BOOL));

        for (BackpackTier t : BackpackTier.values()) {
            Item.Properties p = new Item.Properties().stacksTo(1)
                    .component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
                    .component(UPGRADES, BackpackUpgrades.DEFAULT);
            if (t.fireproof) p.fireResistant();
            BACKPACKS.put(t, item(t.id + "_backpack", props -> new BackpackItem(props, t), p));
        }

        for (Hammer h : List.of(
                new Hammer("wooden", ToolMaterial.WOOD, 3), new Hammer("stone", ToolMaterial.STONE, 3),
                new Hammer("iron", ToolMaterial.IRON, 3), new Hammer("golden", ToolMaterial.GOLD, 3),
                new Hammer("diamond", ToolMaterial.DIAMOND, 5), new Hammer("netherite", ToolMaterial.NETHERITE, 7))) {
            Item.Properties p = new Item.Properties()
                    .pickaxe(h.material(), 1.0f, -2.8f)              // tool component, enchantability, attributes
                    .component(DataComponents.UNBREAKABLE, Unit.INSTANCE); // truly unbreakable
            HAMMERS.add(item(h.name() + "_hammer", props -> new HammerItem(props, h.size()), p));
        }

        GOLD_COIN = item("gold_coin", Item::new, new Item.Properties());
        PIXEL_STORAGE = item("pixel_storage", PixelStorageItem::new, new Item.Properties());
        PIXEL_PORTAL = item("pixel_portal", PixelPortalItem::new, new Item.Properties());
        PIXEL_HOUSE = item("pixel_house", PixelHouseItem::new, new Item.Properties());

        BACKPACK_MENU = Registry.register(BuiltInRegistries.MENU, BackpackMod.id("backpack"),
                new ExtendedMenuType<BackpackMenu, Integer>(BackpackMenu::new, ByteBufCodecs.VAR_INT));
        UPGRADE_RECIPE = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, BackpackMod.id("backpack_upgrade"),
                new RecipeSerializer<UpgradeRecipe>(MapCodec.unit(UpgradeRecipe::new), StreamCodec.unit(new UpgradeRecipe())));
    }
    private ModRegistry() {}
}
