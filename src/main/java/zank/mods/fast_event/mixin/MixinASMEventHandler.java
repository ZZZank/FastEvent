package zank.mods.fast_event.mixin;

import net.minecraftforge.eventbus.ASMEventHandler;
import net.minecraftforge.eventbus.api.IEventListener;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zank.mods.fast_event.EventListenerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author ZZZank
 */
@Mixin(ASMEventHandler.class)
public class MixinASMEventHandler {

    @Mutable
    @Shadow
    @Final
    private IEventListener handler;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void actualEventHandlerInit(Object target, Method method, boolean isGeneric, CallbackInfo ci) {
        this.handler = EventListenerFactory.createRawListener(method, target);
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/eventbus/ASMEventHandler;createWrapper(Ljava/lang/reflect/Method;)Ljava/lang/Class;"))
    private Class<?> removeClassWrapperGen(ASMEventHandler instance, Method callback) {
        return null;
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/lang/Class;getConstructor([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;"))
    private Constructor<?> removeClassWrapperUsage1(Class<?> instance, Class<?>[] parameterTypes) {
        return null;
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/lang/reflect/Constructor;newInstance([Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object removeClassWrapperUsage2(Constructor<?> instance, Object[] initargs) {
        return null;
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/lang/Class;newInstance()Ljava/lang/Object;"))
    private Object removeClassWrapperUsage3(Class<?> instance) {
        return null;
    }
}
