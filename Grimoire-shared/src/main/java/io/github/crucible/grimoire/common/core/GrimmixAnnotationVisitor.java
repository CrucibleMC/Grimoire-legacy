package io.github.crucible.grimoire.common.core;

import io.github.crucible.grimoire.common.GrimoireCore;
import io.github.crucible.grimoire.common.api.grimmix.Grimmix;
import org.objectweb.asm.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class GrimmixAnnotationVisitor extends ClassVisitor {
    private final GrimmixCandidate candidate = new GrimmixCandidate();

    public GrimmixAnnotationVisitor() {
        super(Opcodes.ASM4);
    }

    public static GrimmixCandidate examineClassForGrimmix(File classFile) {
        try {
            FileInputStream stream = new FileInputStream(classFile);
            GrimmixCandidate result = examineClassForGrimmix(stream);
            stream.close();

            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static GrimmixCandidate examineClassForGrimmix(ZipFile archive, ZipEntry entry) {
        try {
            InputStream stream = archive.getInputStream(entry);
            GrimmixCandidate result = examineClassForGrimmix(stream);
            stream.close();

            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static GrimmixCandidate examineClassForGrimmix(InputStream classStream) {
        try {
            ClassReader reader = new ClassReader(classStream);
            GrimmixAnnotationVisitor visitor = new GrimmixAnnotationVisitor();
            reader.accept(visitor, 0);

            return visitor.candidate;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if (getDescriptorForClass(Grimmix.class).equals(desc)) {
            this.candidate.hasAnnotation = true;
        }

        return new DataReader(this);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.candidate.className = name != null ? name.replaceAll("/", ".") : "null";

        super.visit(version, access, name, signature, superName, interfaces);
    }

    private static class DataReader extends AnnotationVisitor {
        private final GrimmixAnnotationVisitor supervisitor;

        private DataReader(GrimmixAnnotationVisitor supervisitor) {
            super(Opcodes.ASM4);
            this.supervisitor = supervisitor;
        }

        /**
         * @see Grimmix#modid()
         */
        @Override
        public void visit(String name, Object value) {
            if ("modid".equals(name)) {
                this.supervisitor.candidate.name = String.valueOf(value);
            }

            super.visit(name, value);
        }
    }

    public static String getDescriptorForClass(Class<?> classInQuestion) {
        return Type.getDescriptor(classInQuestion);
    }


    public static class GrimmixCandidate {
        protected String name = null;
        protected String className = null;
        protected boolean hasAnnotation = false;
        protected List<String> configurationPaths = new ArrayList<>();

        public GrimmixCandidate() {
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
