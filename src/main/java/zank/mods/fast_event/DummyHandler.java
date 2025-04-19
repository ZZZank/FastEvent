package zank.mods.fast_event;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventListener;

/**
 * @author ZZZank
 */
public class DummyHandler implements IEventListener {
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
