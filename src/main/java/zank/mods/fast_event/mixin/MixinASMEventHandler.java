package zank.mods.fast_event.mixin;

import net.minecraftforge.eventbus.ASMEventHandler;
import net.minecraftforge.eventbus.api.IEventListener;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zank.mods.fast_event.DummyHandler;
import zank.mods.fast_event.EventListenerFactory;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author ZZZank
 */
@Mixin(value = ASMEventHandler.class, remap = false)
public class MixinASMEventHandler {

    @Unique
    private static final MethodHandles.Lookup fastEvent$LOOKUP = MethodHandles.lookup();

    @Mutable
    @Shadow
    @Final
    private IEventListener handler;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void actualEventHandlerInit(Object target, Method method, boolean isGeneric, CallbackInfo ci) {
        this.handler = EventListenerFactory.createRawListener(fastEvent$LOOKUP, method, target);
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraftforge/eventbus/ASMEventHandler;createWrapper(Ljava/lang/reflect/Method;)Ljava/lang/Class;"))
    private Class<?> removeClassWrapperGen1(ASMEventHandler instance, Method callback) {
        return DummyHandler.class;
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/lang/Class;newInstance()Ljava/lang/Object;"))
    private Object removeClassWrapperGen2(Class<?> instance) {
        return DummyHandler.INSTANCE;
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/lang/Class;getConstructor([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;"))
    private Constructor<?> removeClassWrapperGen3(Class<?> instance, Class<?>[] parameterTypes) {
        return DummyHandler.CONSTRUCTOR_DUMMY;
    }

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/lang/reflect/Constructor;newInstance([Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object removeClassWrapperGen4(Constructor<?> instance, Object[] initargs) {
        return DummyHandler.INSTANCE;
    }
}
