package zank.mods.fast_event.transform;

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.IncompatibleEnvironmentException;
import org.jetbrains.annotations.NotNull;
import zank.mods.fast_event.FastEventMod;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author ZZZank
 */
public class FastEventTransformerService implements ITransformationService {
    @Override
    public @NotNull String name() {
        return FastEventMod.MOD_ID;
    }

    @Override
    public void initialize(@NotNull IEnvironment environment) {
    }

    @Override
    public void beginScanning(@NotNull IEnvironment environment) {
    }

    @Override
    public void onLoad(@NotNull IEnvironment env, @NotNull Set<String> otherServices)
        throws IncompatibleEnvironmentException {
    }

    @Override
    public @NotNull List<ITransformer> transformers() {
        return Collections.singletonList(new FastEventTransformer());
    }
}
