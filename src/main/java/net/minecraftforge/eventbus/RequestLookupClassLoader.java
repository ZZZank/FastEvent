package net.minecraftforge.eventbus;

import lombok.val;
import org.objectweb.asm.*;

import java.lang.invoke.MethodHandles;

/**
 * @author ZZZank
 */
final class RequestLookupClassLoader extends ClassLoader {

    RequestLookupClassLoader() {
        super(Thread.currentThread().getContextClassLoader());
    }

    /// can only be called once (which is, by FastEvent), because defining
    /// the same class twice is not allowed
    public MethodHandles.Lookup request() throws NoSuchFieldException, IllegalAccessException {
        val data = dump();
        return (MethodHandles.Lookup) defineClass("zank.mods.fast_event.GiveMeLookup", data, 0, data.length)
            .getField("LOOKUP")
            .get(null);
    }

    /// ```java
    /// public interface GiveMeLookup {
    ///     MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    /// }
    /// ```
    /// generated: 2025/06/18
    private static byte[] dump() {

        ClassWriter classWriter = new ClassWriter(0);
        FieldVisitor fieldVisitor;
        RecordComponentVisitor recordComponentVisitor;
        MethodVisitor methodVisitor;
        AnnotationVisitor annotationVisitor0;

        classWriter.visit(
            Opcodes.V1_8,
            Opcodes.ACC_PUBLIC | Opcodes.ACC_ABSTRACT | Opcodes.ACC_INTERFACE,
            "zank/mods/fast_event/GiveMeLookup",
            null,
            "java/lang/Object",
            null
        );

        classWriter.visitSource("GiveMeLookup.java", null);

        classWriter.visitInnerClass(
            "java/lang/invoke/MethodHandles$Lookup",
            "java/lang/invoke/MethodHandles",
            "Lookup",
            Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL | Opcodes.ACC_STATIC
        );

        {
            fieldVisitor = classWriter.visitField(
                Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL | Opcodes.ACC_STATIC,
                "LOOKUP",
                "Ljava/lang/invoke/MethodHandles$Lookup;",
                null,
                null
            );
            fieldVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(Opcodes.ACC_STATIC, "<clinit>", "()V", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(9, label0);
            methodVisitor.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "java/lang/invoke/MethodHandles",
                "lookup",
                "()Ljava/lang/invoke/MethodHandles$Lookup;",
                false
            );
            methodVisitor.visitFieldInsn(
                Opcodes.PUTSTATIC,
                "zank/mods/fast_event/GiveMeLookup",
                "LOOKUP",
                "Ljava/lang/invoke/MethodHandles$Lookup;"
            );
            methodVisitor.visitInsn(Opcodes.RETURN);
            methodVisitor.visitMaxs(1, 0);
            methodVisitor.visitEnd();
        }
        classWriter.visitEnd();

        return classWriter.toByteArray();
    }
}
