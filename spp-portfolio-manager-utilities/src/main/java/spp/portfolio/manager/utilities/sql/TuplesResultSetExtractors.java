package spp.portfolio.manager.utilities.sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.collections4.CollectionUtils;

import jakarta.persistence.Tuple;
import jakarta.persistence.TupleElement;

public class TuplesResultSetExtractors
{
    public static <T, R extends Collection<T>> TuplesResultSetExtractor<R> tupleCollectionMapperResultSetExtractor(TupleMapper<T> tupleMapper, Supplier<R> collectionSupplier)
    {
        return tuples ->
        {
            R collection = collectionSupplier.get();
            
            if (!CollectionUtils.isEmpty(tuples)) 
            {
                Iterator<Tuple> tuplesIterator = tuples.iterator();
                int rowNum = 0;
                while(tuplesIterator.hasNext())
                {
                    T val = tupleMapper.mapTuple(tuplesIterator.next(), rowNum);
                    collection.add(val);
                    rowNum++;
                }
            }

            return collection;
        };
    }
    
    public static <K, V, R extends Map<K, V>> TuplesResultSetExtractor<R> tupleMapMapperResultSetExtractor(TupleMapper<K> tupleKeyMapper, TupleMapper<V> tupleValueMapper, Supplier<R> mapSupplier)
    {
        return tuples ->
        {
            R map = mapSupplier.get();
            
            if (!CollectionUtils.isEmpty(tuples)) 
            {
                Iterator<Tuple> tuplesIterator = tuples.iterator();
                int rowNum = 0;
                while(tuplesIterator.hasNext())
                {
                    K key = tupleKeyMapper.mapTuple(tuplesIterator.next(), rowNum);
                    V value = tupleValueMapper.mapTuple(tuplesIterator.next(), rowNum);
                    map.put(key, value);
                    rowNum++;
                }
            }

            return map;
        };
    }
    
    public static <T> TuplesResultSetExtractor<List<T>> tupleMapperListResultSetExtractor(TupleMapper<T> tupleMapper)
    {
        return tupleCollectionMapperResultSetExtractor(tupleMapper, ArrayList::new);
    }
    
    public static <T> TuplesResultSetExtractor<Set<T>> tupleMapperSetResultSetExtractor(TupleMapper<T> tupleMapper)
    {
        return tupleCollectionMapperResultSetExtractor(tupleMapper, LinkedHashSet::new);
    }
    
    public static <K, V> TuplesResultSetExtractor<Map<K, V>> tupleMapperMapResultSetExtractor(TupleMapper<K> tupleKeyMapper, TupleMapper<V> tupleValueMapper)
    {
        return tupleMapMapperResultSetExtractor(tupleKeyMapper, tupleValueMapper, LinkedHashMap::new);
    }
    
    public static <K, V> TupleMapper<Map<K, Optional<Object>>> tupleAttributeMapper(Function<TupleElement<?>, K> tupleMapKeyMapper)
    {
        return
                (tuple, rowNum) ->
                {
                    Map<K, Optional<Object>> attributes = new LinkedHashMap<>();
                    
                    if(Objects.nonNull(tuple))
                    {
                        List<TupleElement<?>> columns = tuple.getElements();
                        for (TupleElement<?> col : columns)
                        {
                            Class<?> javaType = col.getJavaType();
                            String colName = col.getAlias();
                            Object val = SQLHelper.extractFromTuple(tuple, colName, javaType);
                            K key = tupleMapKeyMapper.apply(col);
                            attributes.put(key, Optional.ofNullable(val));
                        }
                    }
                    
                    return attributes;
                };
    }
}
