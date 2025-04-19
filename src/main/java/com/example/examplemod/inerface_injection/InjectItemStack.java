package com.example.examplemod.inerface_injection;

import net.minecraft.world.item.ItemStack;

/**
 * @author ZZZank
 */
public interface InjectItemStack {

    default ItemStack exampleMod$self() {
        return (ItemStack) this;
    }

    /**
     * if you're about to use interface injection to make method calling easier, make sure to
     * add a default implementation to used method, otherwise calling this method on a non-abstract
     * class will cause `symbol not found` error when compiling
     */
    default void exampleMod$doNothing() {
        throw new AssertionError("no implementation");
    }
}
