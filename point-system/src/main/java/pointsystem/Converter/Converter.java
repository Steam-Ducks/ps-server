package pointsystem.Converter;

import java.util.List;

public interface Converter<E, D> {
    D toDto(E entity);
    E toEntity(D dto);
    List<D> toDto(List<E> entities);
    List<E> toEntity(List<D> dtos);
}