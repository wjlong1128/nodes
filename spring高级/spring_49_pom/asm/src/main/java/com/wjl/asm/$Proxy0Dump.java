package com.wjl.asm;

import org.springframework.asm.*;

public class $Proxy0Dump implements Opcodes {

    public static byte[] dump() throws Exception {

        ClassWriter classWriter = new ClassWriter(0);
        FieldVisitor fieldVisitor;
        RecordComponentVisitor recordComponentVisitor;
        MethodVisitor methodVisitor;
        AnnotationVisitor annotationVisitor0;

        classWriter.visit(V1_8, ACC_PUBLIC | ACC_SUPER, "asm/$Proxy0", null, "java/lang/reflect/Proxy", new String[]{"asm/Foo"});

        classWriter.visitSource("$Proxy0.java", null);

        {
            fieldVisitor = classWriter.visitField(ACC_STATIC, "foo", "Ljava/lang/reflect/Method;", null, null);
            fieldVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PROTECTED, "<init>", "(Ljava/lang/reflect/InvocationHandler;)V", null, null);
            methodVisitor.visitParameter("h", 0);
            methodVisitor.visitCode();
            Label label0 = new Label();	
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(16, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/reflect/Proxy", "<init>", "(Ljava/lang/reflect/InvocationHandler;)V", false);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLineNumber(17, label1);
            methodVisitor.visitInsn(RETURN);
            Label label2 = new Label();
            methodVisitor.visitLabel(label2);
            methodVisitor.visitLocalVariable("this", "Lasm/$Proxy0;", null, label0, label2, 0);
            methodVisitor.visitLocalVariable("h", "Ljava/lang/reflect/InvocationHandler;", null, label0, label2, 1);
            methodVisitor.visitMaxs(2, 2);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "foo", "()V", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            Label label1 = new Label();
            Label label2 = new Label();
            methodVisitor.visitTryCatchBlock(label0, label1, label2, "java/lang/Throwable");
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(32, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETFIELD, "asm/$Proxy0", "h", "Ljava/lang/reflect/InvocationHandler;");
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitFieldInsn(GETSTATIC, "asm/$Proxy0", "foo", "Ljava/lang/reflect/Method;");
            methodVisitor.visitInsn(ACONST_NULL);
            methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/lang/reflect/InvocationHandler", "invoke", "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;", true);
            methodVisitor.visitInsn(POP);
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLineNumber(35, label1);
            Label label3 = new Label();
            methodVisitor.visitJumpInsn(GOTO, label3);
            methodVisitor.visitLabel(label2);
            methodVisitor.visitLineNumber(33, label2);
            methodVisitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/Throwable"});
            methodVisitor.visitVarInsn(ASTORE, 1);
            Label label4 = new Label();
            methodVisitor.visitLabel(label4);
            methodVisitor.visitLineNumber(34, label4);
            methodVisitor.visitTypeInsn(NEW, "java/lang/RuntimeException");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/Throwable;)V", false);
            methodVisitor.visitInsn(ATHROW);
            methodVisitor.visitLabel(label3);
            methodVisitor.visitLineNumber(36, label3);
            methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            methodVisitor.visitInsn(RETURN);
            Label label5 = new Label();
            methodVisitor.visitLabel(label5);
            methodVisitor.visitLocalVariable("e", "Ljava/lang/Throwable;", null, label4, label3, 1);
            methodVisitor.visitLocalVariable("this", "Lasm/$Proxy0;", null, label0, label5, 0);
            methodVisitor.visitMaxs(4, 2);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            Label label1 = new Label();
            Label label2 = new Label();
            methodVisitor.visitTryCatchBlock(label0, label1, label2, "java/lang/NoSuchMethodException");
            methodVisitor.visitTryCatchBlock(label0, label1, label2, "java/lang/SecurityException");
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(23, label0);
            methodVisitor.visitLdcInsn(Type.getType("Lasm/Foo;"));
            methodVisitor.visitLdcInsn("foo");
            methodVisitor.visitInsn(ICONST_0);
            methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/Class");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Class", "getMethod", "(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", false);
            methodVisitor.visitFieldInsn(PUTSTATIC, "asm/$Proxy0", "foo", "Ljava/lang/reflect/Method;");
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLineNumber(26, label1);
            Label label3 = new Label();
            methodVisitor.visitJumpInsn(GOTO, label3);
            methodVisitor.visitLabel(label2);
            methodVisitor.visitLineNumber(24, label2);
            methodVisitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/Exception"});
            methodVisitor.visitVarInsn(ASTORE, 0);
            Label label4 = new Label();
            methodVisitor.visitLabel(label4);
            methodVisitor.visitLineNumber(25, label4);
            methodVisitor.visitTypeInsn(NEW, "java/lang/RuntimeException");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/RuntimeException", "<init>", "(Ljava/lang/Throwable;)V", false);
            methodVisitor.visitInsn(ATHROW);
            methodVisitor.visitLabel(label3);
            methodVisitor.visitLineNumber(27, label3);
            methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            methodVisitor.visitInsn(RETURN);
            methodVisitor.visitLocalVariable("e", "Ljava/lang/Exception;", null, label4, label3, 0);
            methodVisitor.visitMaxs(3, 1);
            methodVisitor.visitEnd();
        }
        classWriter.visitEnd();

        return classWriter.toByteArray();
    }
}
