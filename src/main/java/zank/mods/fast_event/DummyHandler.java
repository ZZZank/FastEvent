package zank.mods.fast_event;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventListener;

import java.lang.reflect.Constructor;

/**
 * @author ZZZank
 */
public class DummyHandler implements IEventListener {
    public static final DummyHandler INSTANCE = new DummyHandler();
    public static final Constructor<?> CONSTRUCTOR_DUMMY = DummyHandler.class.getConstructors()[0];

    public DummyHandler() {
        // simulate static listener
    }

    public DummyHandler(Object ignored) {
        // simulate instance event listener
    }

    @Override
    public void invoke(Event event) {
    }
}
