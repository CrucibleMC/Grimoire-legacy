package io.github.crucible.grimoire.common.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import io.github.crucible.grimoire.common.GrimoireCore;
import io.github.crucible.grimoire.common.api.eventbus.CoreEventHandler;
import io.github.crucible.grimoire.common.api.grimmix.Grimmix;

class GrimoireAnnotationAnalyzer extends ClassVisitor {
    private final GrimmixCandidate grimmixCandidate = new GrimmixCandidate();
    private final EventHandlerCandidate handlerCandidate = new EventHandlerCandidate();

    public GrimoireAnnotationAnalyzer() {
        super(Opcodes.ASM4);
    }

    public GrimmixCandidate getGrimmixCandidate() {
        return this.grimmixCandidate;
    }

    public EventHandlerCandidate getHandlerCandidate() {
        return this.handlerCandidate;
    }

    public static GrimoireAnnotationAnalyzer examineClass(File classFile) {
        try {
            FileInputStream stream = new FileInputStream(classFile);
            GrimoireAnnotationAnalyzer result = examineClass(stream);
            stream.close();

            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static GrimoireAnnotationAnalyzer examineClass(ZipFile archive, ZipEntry entry) {
        try {
            InputStream stream = archive.getInputStream(entry);
            GrimoireAnnotationAnalyzer result = examineClass(stream);
            stream.close();

            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static GrimoireAnnotationAnalyzer examineClass(InputStream classStream) {
        try {
            ClassReader reader = new ClassReader(classStream);
            GrimoireAnnotationAnalyzer visitor = new GrimoireAnnotationAnalyzer();
            reader.accept(visitor, 0);

            return visitor;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if (getDescriptorForClass(Grimmix.class).equals(desc)) {
            this.grimmixCandidate.hasAnnotation = true;
        }

        if (getDescriptorForClass(CoreEventHandler.class).equals(desc)) {
            this.handlerCandidate.hasAnnotation = true;
        }

        return this.grimmixCandidate.hasAnnotation ? new DataReader(this) : null;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.grimmixCandidate.className = name != null ? name.replaceAll("/", ".") : "null";
        this.handlerCandidate.className = name != null ? name.replaceAll("/", ".") : "null";

        super.visit(version, access, name, signature, superName, interfaces);
    }

    private static class DataReader extends AnnotationVisitor {
        private final GrimoireAnnotationAnalyzer supervisitor;

        private DataReader(GrimoireAnnotationAnalyzer supervisitor) {
            super(Opcodes.ASM4);
            this.supervisitor = supervisitor;
        }

        /**
         * @see Grimmix#id()
         */
        @Override
        public void visit(String name, Object value) {
            if ("id".equals(name)) {
                this.supervisitor.grimmixCandidate.name = String.valueOf(value);
            }

            super.visit(name, value);
        }
    }

    public static String getDescriptorForClass(Class<?> classInQuestion) {
        return Type.getDescriptor(classInQuestion);
    }

    public static class EventHandlerCandidate {
        private String className = null;
        private boolean hasAnnotation = false;

        protected EventHandlerCandidate() {
            // NO-OP
        }

        public boolean validate() {
            if (this.hasAnnotation) {
                GrimoireCore.logger.info("Event handler candidate found: {}", this.className);
            }

            return this.hasAnnotation;
        }

        public String getClassName() {
            return this.className;
        }
    }

    public static class GrimmixCandidate {
        private String name = null;
        private String className = null;
        private boolean hasAnnotation = false;
        private List<String> configurationPaths = new ArrayList<>();

        protected GrimmixCandidate() {
            // NO-OP
        }

        public boolean validate() {
            if (this.hasAnnotation) {
                GrimoireCore.logger.info("Grimmix candidate found: {}", this.className);
            }

            return this.hasAnnotation;
        }

        public String getClassName() {
            return this.className;
        }

        public String getName() {
            return this.name;
        }

        public List<String> getConfigurationPaths() {
            return this.configurationPaths;
        }

        public void setConfigurationPaths(List<String> configurationPaths) {
            this.configurationPaths = configurationPaths;
        }
    }

}
