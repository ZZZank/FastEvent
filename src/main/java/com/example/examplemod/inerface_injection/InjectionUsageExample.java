package com.example.examplemod.inerface_injection;

import lombok.val;
import net.minecraft.world.item.BoatItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @author ZZZank
 */
public class InjectionUsageExample {

    /**
     * use this syntax if `architectury.common.json` is not used
     */
    public static void doNothing(@NotNull ItemStack stack) {
        val inject = (InjectItemStack) (Object) stack;
        inject.exampleMod$doNothing();
    }

    /**
     * use this only when you're set up interface injection for your interface, which, in this case, is
     * {@link InjectItemStack}
     */
    public static void injectedDoNothing(@NotNull ItemStack stack) {
        stack.exampleMod$doNothing();
    }
}
