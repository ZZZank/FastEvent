package zank.mods.fast_event.transform;

import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.ITransformerVotingContext;
import cpw.mods.modlauncher.api.TransformerVoteResult;
import lombok.val;
import net.minecraftforge.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import zank.mods.fast_event.FastEventMod;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Set;
import java.util.stream.StreamSupport;

/**
 * @author ZZZank
 */
public class FastEventTransformer implements ITransformer<ClassNode> {
    /// @see EventBus
    private static final String TARGET_NAME = "net.minecraftforge.eventbus.EventBus";

    /// @see EventBus#register(Class, Object, Method)
    @Override
    public @NotNull ClassNode transform(@NotNull ClassNode input, @NotNull ITransformerVotingContext context) {
        FastEventMod.LOGGER.info("event listener transformation triggered, target: {}", context.getClassName());

        val methodNode = input.methods
            .stream()
            .filter(m -> "register".equals(m.name))
            .filter(m -> Modifier.isPrivate(m.access))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("`private ... register(...)` not found, what"));

        StreamSupport.stream(methodNode.instructions.spliterator(), false)
            .filter(inst -> inst instanceof TypeInsnNode)
            .map(inst -> (TypeInsnNode) inst)
            .filter(inst -> inst.getOpcode() == Opcodes.NEW
                && inst.desc.equals("net/minecraftforge/eventbus/ASMEventHandler"))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("`new ASMEventHandler` not found"))
            .desc = "zank/mods/fast_event/LambdaEventHandler";

        StreamSupport.stream(methodNode.instructions.spliterator(), false)
            .filter(inst -> inst instanceof MethodInsnNode)
            .map(inst -> (MethodInsnNode) inst)
            .filter(inst -> Opcodes.INVOKESPECIAL == inst.getOpcode()
                && "net/minecraftforge/eventbus/ASMEventHandler".equals(inst.owner)
                && "<init>".equals(inst.name))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("`ASMEventHandler.<init>(...)` not found"))
            .desc = "zank/mods/fast_event/LambdaEventHandler";

        methodNode.localVariables
            .stream()
            .filter(node -> node.desc.equals("Lnet/minecraftforge/eventbus/ASMEventHandler;"))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("`ASMEventHandler asm;` not found"))
            .desc = "Lzank/mods/fast_event/LambdaEventHandler;";

        return input;
    }

    @Override
    public @NotNull TransformerVoteResult castVote(@NotNull ITransformerVotingContext context) {
        return TransformerVoteResult.YES;
    }

    @Override
    public @NotNull Set<Target> targets() {
        return Collections.singleton(Target.targetPreClass(TARGET_NAME));
    }
}
