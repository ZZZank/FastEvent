package zank.mods.fast_event.mixin;

import lombok.val;
import net.neoforged.bus.ConsumerEventHandler;
import net.neoforged.bus.SubscribeEventListener;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventListener;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.SubscribeEvent;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import zank.mods.fast_event.EventListenerFactory;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.function.Consumer;

/**
 * @author ZZZank
 */
@Mixin(value = SubscribeEventListener.class, remap = false)
public class MixinASMEventHandler {

    @Unique
    private static final MethodHandles.Lookup fastEvent$LOOKUP = MethodHandles.lookup();

    @Shadow
    @Final
    private SubscribeEvent subInfo;

    @Unique
    private Consumer<Event> fastEvent$handler;

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/neoforged/bus/EventListenerFactory;create(Ljava/lang/reflect/Method;Ljava/lang/Object;)Lnet/neoforged/bus/api/EventListener;"))
    private EventListener removeClassWrapperGen(Method method, Object target) {
        Consumer<Event> listener;
        try {
            // NeoForge allow using private/protected methods for event listener, so
            method.setAccessible(true);
            listener = EventListenerFactory.createRawListener(fastEvent$LOOKUP, method, target);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to create IEventListener", e);
        }
        fastEvent$handler = listener;
        return new ConsumerEventHandler(listener);
    }

    /**
     * @author ZZZank
     * @reason skip null check and a field access from ConsumerEventHandler to its internal consumer
     */
    @Overwrite
    public void invoke(Event event) {
        if (this.subInfo.receiveCanceled() || !((ICancellableEvent) event).isCanceled()) {
            this.fastEvent$handler.accept(event);
        }
    }
}
