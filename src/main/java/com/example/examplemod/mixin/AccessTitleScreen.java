package com.example.examplemod.mixin;

import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.PanoramaRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author ZZZank
 */
@Mixin(TitleScreen.class)
public interface AccessTitleScreen {

    @Accessor("panorama")
    PanoramaRenderer exampleMod$panorama();
}
