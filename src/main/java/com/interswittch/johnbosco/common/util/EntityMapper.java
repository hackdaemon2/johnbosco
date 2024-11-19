package com.interswittch.johnbosco.common.util;

import java.util.function.BiFunction;
import java.util.function.Function;

public final class EntityMapper {

    private EntityMapper() {
        throw new IllegalStateException(EntityMapper.class.getName());
    }

    public static <D, E> E mapToEntity(D dto, Function<D, E> entitySupplier) {
        return entitySupplier.apply(dto);
    }

    public static <D, E, R> E mapToEntity(D dto, R relatedEntity, BiFunction<D, R, E> entitySupplier) {
        return entitySupplier.apply(dto, relatedEntity);
    }
}
