package io.github.crucible.omniconfig.lib;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

import io.github.crucible.grimoire.common.GrimoireInternals;

public class Version {
    private static final char[] numbers = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };

    private final String version;
    private final int[] parsedVersion;

    public Version(String version) {
        this.version = Preconditions.checkNotNull(version);

        List<Integer> intList = new ArrayList<>();
        String anotherInt = null;

        for (char ch : version.toCharArray()) {
            if (this.isNumber(ch)) {
                if (anotherInt == null) {
                    anotherInt = String.valueOf(ch);
                } else {
                    anotherInt += ch;
                }
            } else {
                if (anotherInt != null) {
                    intList.add(Integer.parseInt(anotherInt));
                    anotherInt = null;
                } else {
                    continue;
                }
            }
        }

        if (intList.size() <= 0) {
            Throwables.propagate(new IllegalArgumentException("Invalid version argument specified: " + version));
        }

        this.parsedVersion = new int[intList.size()];
        for (int i = 0; i < intList.size(); i++) {
            this.parsedVersion[i] = intList.get(i);
        }
    }

    private final boolean isNumber(char ch) {
        for (char number : numbers) {
            if (number == ch)
                return true;
        }

        return false;
    }

    public boolean isNewerThan(String version) {
        Version another = new Version(version);
        int size = another.parsedVersion.length > this.parsedVersion.length ? this.parsedVersion.length : another.parsedVersion.length;

        for (int i = 0; i < size; i++) {
            int thisInt = this.parsedVersion[i];
            int anotherInt = another.parsedVersion[i];

            if (thisInt > anotherInt)
                return true;
            else if (thisInt < anotherInt)
                return false;
            else if (thisInt == anotherInt) {
                continue;
            }
        }

        return false;
    }

    public boolean isOlderThan(String version) {
        Version another = new Version(version);
        int size = another.parsedVersion.length > this.parsedVersion.length ? this.parsedVersion.length : another.parsedVersion.length;

        for (int i = 0; i < size; i++) {
            int thisInt = this.parsedVersion[i];
            int anotherInt = another.parsedVersion[i];

            if (thisInt < anotherInt)
                return true;
            else if (thisInt > anotherInt)
                return false;
            else if (thisInt == anotherInt) {
                continue;
            }
        }

        return false;
    }

    public boolean isEqual(String version) {
        return !this.isOlderThan(version) && !this.isNewerThan(version);
    }

    public boolean isNewerOrEqual(String version) {
        return this.isNewerThan(version) || this.isEqual(version);
    }

    public boolean isOlderOrEqual(String version) {
        return this.isOlderThan(version) || this.isEqual(version);
    }

    public String asString() {
        return this.version;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Version) {
            Version another = (Version) obj;
            return this.version.equals(another.version);
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return this.asString();
    }

}
