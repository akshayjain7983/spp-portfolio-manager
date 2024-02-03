package spp.portfolio.manager.utilities.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ReadOnlyJpaRepository<T, ID> extends JpaRepository<T, ID>
{
    default void flush()
    {
        throw new UnsupportedOperationException("This is a read only jpa repository");
    }

    default <S extends T> S saveAndFlush(S entity)
    {
        throw new UnsupportedOperationException("This is a read only jpa repository");
    }

    default <S extends T> List<S> saveAllAndFlush(Iterable<S> entities)
    {
        throw new UnsupportedOperationException("This is a read only jpa repository");
    }

    default void deleteAllInBatch(Iterable<T> entities)
    {
        throw new UnsupportedOperationException("This is a read only jpa repository");
    }

    default void deleteAllByIdInBatch(Iterable<ID> ids)
    {
        throw new UnsupportedOperationException("This is a read only jpa repository");
    }

    default void deleteAllInBatch()
    {
        throw new UnsupportedOperationException("This is a read only jpa repository");
    }
    
    default <S extends T> List<S> saveAll(Iterable<S> entities)
    {
        throw new UnsupportedOperationException("This is a read only jpa repository");
    }
    
    default <S extends T> S save(S entity)
    {
        throw new UnsupportedOperationException("This is a read only jpa repository");
    }

    default void deleteById(ID id)
    {
        throw new UnsupportedOperationException("This is a read only jpa repository");
    }
    
    default void delete(T entity)
    {
        throw new UnsupportedOperationException("This is a read only jpa repository");
    }

    default void deleteAllById(Iterable<? extends ID> ids)
    {
        throw new UnsupportedOperationException("This is a read only jpa repository");
    }
    
    default void deleteAll(Iterable<? extends T> entities)
    {
        throw new UnsupportedOperationException("This is a read only jpa repository");
    }

    default void deleteAll()
    {
        throw new UnsupportedOperationException("This is a read only jpa repository");
    }
    
}
