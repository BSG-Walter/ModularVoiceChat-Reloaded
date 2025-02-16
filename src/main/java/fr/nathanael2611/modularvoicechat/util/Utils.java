package fr.nathanael2611.modularvoicechat.util;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Utils {
    public static short[] bytesToShorts(byte[] bytes) {
        if (bytes.length % 2 != 0) {
            throw new IllegalArgumentException("Input bytes need to be divisible by 2");
        }
        short[] data = new short[bytes.length / 2];
        for (int i = 0; i < bytes.length; i += 2) {
            data[i / 2] = bytesToShort(bytes[i], bytes[i + 1]);
        }
        return data;
    }

    public static byte[] shortsToBytes(short[] shorts) {
        byte[] data = new byte[shorts.length * 2];
        for (int i = 0; i < shorts.length; i++) {
            byte[] split = shortToBytes(shorts[i]);
            data[i * 2] = split[0];
            data[i * 2 + 1] = split[1];
        }
        return data;
    }

    public static short bytesToShort(byte b1, byte b2) {
        return (short) (((b2 & 0xFF) << 8) | (b1 & 0xFF));
    }

    public static byte[] shortToBytes(short s) {
        return new byte[]{(byte) (s & 0xFF), (byte) ((s >> 8) & 0xFF)};
    }

    public static short[] floatsToShorts(float[] floats) {
        short[] shorts = new short[floats.length];
        for (int i = 0; i < floats.length; i++) {
            shorts[i] = ((Float) floats[i]).shortValue();
        }
        return shorts;
    }

    public static float[] shortsToFloats(short[] shorts) {
        float[] floats = new float[shorts.length];
        for (int i = 0; i < shorts.length; i++) {
            floats[i] = ((Short) shorts[i]).floatValue();
        }
        return floats;
    }

    public static byte[] floatsToBytes(float[] floats) {
        byte[] bytes = new byte[floats.length * 2];
        for (int i = 0; i < floats.length; i++) {
            short x = ((Float) floats[i]).shortValue();
            bytes[i * 2] = (byte) (x & 0x00FF);
            bytes[i * 2 + 1] = (byte) ((x & 0xFF00) >> 8);
        }
        return bytes;
    }

    public static float[] bytesToFloats(byte[] bytes) {
        float[] floats = new float[bytes.length / 2];
        for (int i = 0; i < bytes.length / 2; i++) {
            if ((bytes[i * 2 + 1] & 0x80) != 0) {
                floats[i] = Short.MIN_VALUE + ((bytes[i * 2 + 1] & 0x7F) << 8) | (bytes[i * 2] & 0xFF);
            } else {
                floats[i] = ((bytes[i * 2 + 1] << 8) & 0xFF00) | (bytes[i * 2] & 0xFF);
            }
        }
        return floats;
    }

    @Nullable
    public static <T> T createSafe(Supplier<T> supplier, @Nullable Consumer<Throwable> onError) {
        AtomicReference<Throwable> exception = new AtomicReference<>();
        AtomicReference<T> obj = new AtomicReference<>();
        Thread t = new Thread(() -> {
            if (onError != null) {
                Thread.setDefaultUncaughtExceptionHandler((t1, e) -> {
                    exception.set(e);
                });
            }
            obj.set(supplier.get());
        }, "NativeInitializationThread");
        t.start();

        try {
            t.join(1000);
        } catch (InterruptedException e) {
            return null;
        }
        Throwable ex = exception.get();
        if (onError != null && ex != null) {
            onError.accept(ex);
        }
        return obj.get();
    }

    @Nullable
    public static <T> T createSafe(Supplier<T> supplier) {
        return createSafe(supplier, null);
    }


}
