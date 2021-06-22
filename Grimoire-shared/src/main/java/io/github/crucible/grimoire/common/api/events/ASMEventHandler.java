/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package io.github.crucible.grimoire.common.api.events;

import com.google.common.collect.Maps;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;

import static org.objectweb.asm.Opcodes.*;

public class ASMEventHandler implements IEventListener {
    private static final String HANDLER_DESC = Type.getInternalName(IEventListener.class);
    private static final String HANDLER_FUNC_DESC = Type.getMethodDescriptor(IEventListener.class.getDeclaredMethods()[0]);
    private static final ASMClassLoader LOADER = new ASMClassLoader();
    private static final HashMap<Method, Class<?>> cache = Maps.newHashMap();
    private static int IDs = 0;
    private final IEventListener handler;
    //private final SubscribeEvent subInfo;
    private final EventPriority priority;
    private final boolean receiveCanceled;
    private String readable;
    private java.lang.reflect.Type filter = null;

    @Deprecated
    public ASMEventHandler(Object target, Method method) throws Exception {
        this(target, method, false);
    }

    public ASMEventHandler(Object target, Method method, boolean isGeneric) throws Exception {
        if (Modifier.isStatic(method.getModifiers()))
            handler = (IEventListener) createWrapper(method).newInstance();
        else
            handler = (IEventListener) createWrapper(method).getConstructor(Object.class).newInstance(target);
        priority = GrimoireEventBus.getPriority(method);
        receiveCanceled = GrimoireEventBus.receiveCanceled(method);
        readable = "ASM: " + target + " " + method.getName() + Type.getMethodDescriptor(method);
        if (isGeneric) {
            java.lang.reflect.Type type = method.getGenericParameterTypes()[0];
            if (type instanceof ParameterizedType) {
                filter = ((ParameterizedType) type).getActualTypeArguments()[0];
            }
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void invoke(GrimoireEvent event) {
        if (handler != null) {
            if (!event.isCancelable() || !event.isCanceled() || receiveCanceled) {
                if (filter == null || filter == ((IGenericEvent) event).getGenericType()) {
                    handler.invoke(event);
                }
            }
        }
    }

    public EventPriority getPriority() {
        return priority;
    }

    public Class<?> createWrapper(Method callback) {
        if (cache.containsKey(callback)) {
            return cache.get(callback);
        }

        ClassWriter cw = new ClassWriter(0);
        MethodVisitor mv;

        boolean isStatic = Modifier.isStatic(callback.getModifiers());
        String name = getUniqueName(callback);
        String desc = name.replace('.', '/');
        String instType = Type.getInternalName(callback.getDeclaringClass());
        String eventType = Type.getInternalName(callback.getParameterTypes()[0]);

        cw.visit(V1_6, ACC_PUBLIC | ACC_SUPER, desc, null, "java/lang/Object", new String[]{HANDLER_DESC});

        cw.visitSource(".dynamic", null);
        {
            if (!isStatic)
                cw.visitField(ACC_PUBLIC, "instance", "Ljava/lang/Object;", null, null).visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", isStatic ? "()V" : "(Ljava/lang/Object;)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            if (!isStatic) {
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitFieldInsn(PUTFIELD, desc, "instance", "Ljava/lang/Object;");
            }
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "invoke", HANDLER_FUNC_DESC, null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            if (!isStatic) {
                mv.visitFieldInsn(GETFIELD, desc, "instance", "Ljava/lang/Object;");
                mv.visitTypeInsn(CHECKCAST, instType);
            }
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, eventType);
            mv.visitMethodInsn(isStatic ? INVOKESTATIC : INVOKEVIRTUAL, instType, callback.getName(), Type.getMethodDescriptor(callback), false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        cw.visitEnd();
        Class<?> ret = LOADER.define(name, cw.toByteArray());
        cache.put(callback, ret);
        return ret;
    }

    private String getUniqueName(Method callback) {
        return String.format("%s_%d_%s_%s_%s", getClass().getName(), IDs++,
                callback.getDeclaringClass().getSimpleName(),
                callback.getName(),
                callback.getParameterTypes()[0].getSimpleName());
    }

    public String toString() {
        return readable;
    }

    private static class ASMClassLoader extends ClassLoader {
        private ASMClassLoader() {
            super(ASMClassLoader.class.getClassLoader());
        }

        public Class<?> define(String name, byte[] data) {
            return defineClass(name, data, 0, data.length);
        }
    }
}
