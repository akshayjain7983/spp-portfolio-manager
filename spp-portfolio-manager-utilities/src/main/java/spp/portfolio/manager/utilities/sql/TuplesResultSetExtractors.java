package spp.portfolio.manager.utilities.sql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import jakarta.persistence.TupleElement;

public class TuplesResultSetExtractors
{

    public static <R> TuplesResultSetExtractor<R> tupleMapperResultSetExtractor(TupleMapper<R> tupleMapper)
    {
        return tuples ->
        {
            if (CollectionUtils.isEmpty(tuples))
                return Collections.emptyList();

            List<R> resultList = new ArrayList<>(tuples.size());

            for (int i = 0; i < tuples.size(); i++)
            {
                R val = tupleMapper.mapTuple(tuples.get(i), i);
                resultList.add(val);
            }

            return resultList;
        };
    }
    
    public static TupleMapper<Map<String, Object>> tupleAttributeMapper()
    {
        return
                (tuple, rowNum) ->
                {
                    Map<String, Object> attributes = new LinkedHashMap<>();
                    List<TupleElement<?>> columns = tuple.getElements();
                    for (TupleElement<?> col : columns)
                    {
                        Class<?> javaType = col.getJavaType();
                        String colName = col.getAlias();
                        Object val = SQLHelper.extractFromTuple(tuple, colName, javaType);
                        attributes.put(colName, val);
                    }
                    return attributes;
                };
    }

    public static TuplesResultSetExtractor<Map<String, Object>> tupleAttributeMapperResultSetExtractor()
    {
        return tupleMapperResultSetExtractor(tupleAttributeMapper());
    }
}
