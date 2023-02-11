package com.tm.playingcards.init;

import com.tm.playingcards.util.ArrayHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;

public class ModDataSerializers {
    public static final EntityDataSerializer<Byte[]> STACK = new EntityDataSerializer<>() {
        @Override
        public void write(FriendlyByteBuf buf, Byte[] value) {
            buf.writeByteArray(ArrayHelper.toPrimitive(value));
        }

        @Override
        public Byte[] read(FriendlyByteBuf buf) {
            return ArrayHelper.toObject(buf.readByteArray());
        }

        @Override
        public Byte[] copy(Byte[] value) {
            return ArrayHelper.clone(value);
        }
    };

    public static void register() {
        EntityDataSerializers.registerSerializer(STACK);
    }
}
