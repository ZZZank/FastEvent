package com.example.examplemod.mixin;

import com.example.examplemod.inerface_injection.InjectItemStack;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

/**
 * @author ZZZank
 */
@Mixin(ItemStack.class)
public abstract class MixinItemStack implements InjectItemStack {

    @Override
    public void exampleMod$doNothing() {
    }
}
