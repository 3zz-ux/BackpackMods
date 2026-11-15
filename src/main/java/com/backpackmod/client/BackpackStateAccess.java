package com.backpackmod.client;

/** Duck interface mixed into AvatarRenderState: tier ordinal of the worn backpack, or -1. */
public interface BackpackStateAccess {
    int backpackmod$getTier();
    void backpackmod$setTier(int tier);
}
