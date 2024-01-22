package spp.portfolio.manager.utilities.sql;

import java.util.List;

import jakarta.persistence.Tuple;

@FunctionalInterface
public interface TuplesResultSetExtractor<R>
{
    List<R> extractFromTuples(List<Tuple> tuples);
}
