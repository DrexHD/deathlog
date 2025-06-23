package com.glisco.deathlog.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class CodecUtil {
    public static final MapCodec<List<String>> KEY_CODEC = new MapCodec<>() {
        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.empty();
        }

        @Override
        public <T> DataResult<List<String>> decode(DynamicOps<T> ops, MapLike<T> input) {
            return input.entries()
                .map(Pair::getFirst)
                .map(ops::getStringValue)
                .collect(
                    () -> new MutableObject<DataResult<ArrayList<String>>>(DataResult.success(new ArrayList<>())),
                    (acc, el) -> acc.getValue().flatMap(l -> el.map(l::add)),
                    (acc1, acc2) -> acc1.setValue(acc1.getValue().flatMap(l1 -> acc2.getValue().map(l2 -> {
                        l1.addAll(l2);
                        return l1;
                    })))
                )
                .getValue()
                .map(Function.identity());
        }

        @Override
        public <T> RecordBuilder<T> encode(List<String> input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            return prefix;
        }
    };
}
