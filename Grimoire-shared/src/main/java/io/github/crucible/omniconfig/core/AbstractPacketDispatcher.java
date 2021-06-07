package io.github.crucible.omniconfig.core;

import io.github.crucible.omniconfig.lib.Finalized;
import io.github.crucible.omniconfig.wrappers.OmniconfigWrapper;

public abstract class AbstractPacketDispatcher<T, E> {

    public abstract AbstractBufferIO<T> getBufferIO(T buffer);

    public abstract void syncToAll(OmniconfigWrapper wrapper);

    public abstract void syncToPlayer(OmniconfigWrapper wrapper, E player);

    public abstract void syncAllToPlayer(E player);



    public static abstract class AbstractBufferIO<T> {
        protected final T buffer;

        protected AbstractBufferIO(T buffer) {
            this.buffer = buffer;
        }

        public abstract void writeString(String string, int charLimit);
        public abstract void writeLong(long value);
        public abstract void writeInt(int value);
        public abstract void writeDouble(double value);
        public abstract void writeFloat(float value);

        public abstract String readString(int charLimit);
        public abstract long readLong();
        public abstract int readInt();
        public abstract double readDouble();
        public abstract float readFloat();
    }

}
