package zank.mods.fast_event;

import lombok.val;
import net.minecraftforge.eventbus.api.IEventListener;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author ZZZank
 */
public class EventListenerFactory {
    private static final boolean DO_CACHE = true;
    private static final BiFunction<Method, Function<Method, MethodHandle>, MethodHandle> FACTOR_CACHE;

    static {
        if (DO_CACHE) {
            FACTOR_CACHE = new ConcurrentHashMap<Method, MethodHandle>()::computeIfAbsent;
        } else {
            FACTOR_CACHE = (a, b) -> b.apply(a);
        }
    }

    public static IEventListener createRawListener(MethodHandles.Lookup lookup, Method method, Object instance) {
        // no caching is applied here because in EventBus scenario, caching will only be useful
        // when two instance-based listeners of the same class are registered, which is an
        // incredibly rare usage
        val isStatic = Modifier.isStatic(method.getModifiers());
        val listenerFactory = FACTOR_CACHE.apply(method, m -> createListenerFactory(lookup, m, isStatic, instance));

        try {
            return isStatic
                ? (IEventListener) listenerFactory.invokeExact()
                : (IEventListener) listenerFactory.invokeExact(instance);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private static MethodHandle createListenerFactory(
        MethodHandles.Lookup lookup,
        Method callback,
        boolean isStatic,
        Object instance
    ) {
        try {
            val handle = lookup.unreflect(callback);

            val factoryType = isStatic
                ? Constants.RETURNS_IT
                // implicit null check on "instance" via ".getClass()"
                : Constants.RETURNS_IT.insertParameterTypes(0, instance.getClass());

            val factoryHandle = LambdaMetafactory.metafactory(
                lookup,
                Constants.METHOD_NAME,
                factoryType,
                Constants.METHOD_TYPE,
                handle,
                MethodType.methodType(void.class, handle.type().parameterType(isStatic ? 0 : 1))
            ).getTarget();

            return isStatic
                ? factoryHandle
                : factoryHandle.asType(factoryType.changeParameterType(0, Object.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private interface Constants {
        Class<?> CLAZZ = IEventListener.class;
        Method METHOD = CLAZZ.getMethods()[0];
        String METHOD_NAME = METHOD.getName();
        MethodType METHOD_TYPE = MethodType.methodType(METHOD.getReturnType(), METHOD.getParameterTypes());
        MethodType RETURNS_IT = MethodType.methodType(CLAZZ);
    }
}