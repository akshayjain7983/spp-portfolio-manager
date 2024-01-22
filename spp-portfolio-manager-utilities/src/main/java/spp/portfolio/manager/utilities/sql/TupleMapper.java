package spp.portfolio.manager.utilities.sql;

import jakarta.persistence.Tuple;

@FunctionalInterface
public interface TupleMapper<R>
{
    R mapTuple(Tuple tuple, int rowNum);
}
