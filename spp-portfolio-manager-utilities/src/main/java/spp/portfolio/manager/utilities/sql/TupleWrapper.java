package spp.portfolio.manager.utilities.sql;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.function.Function;

import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "of")
public class TupleWrapper
{
    private final Tuple tuple;

    private <R> R ifNotNull(String name, Function<Tuple, R> valueExtractor)
    {
        if (tuple.get(name) == null)
            return null;
        else 
            return valueExtractor.apply(tuple);
    }
    
    public Double getDouble(String name)
    {
        return ifNotNull(name, tuple->Double.valueOf(tuple.get(name).toString()));
    }

    public Integer getInt(String name)
    {
        return ifNotNull(name, tuple->Integer.valueOf(tuple.get(name).toString()));
    }

    public Long getLong(String name)
    {
        return ifNotNull(name, tuple->Long.valueOf(tuple.get(name).toString()));
    }

    public Boolean getBoolean(String name)
    {
        return ifNotNull(name, tuple->Boolean.valueOf(tuple.get(name).toString()));
    }

    public String getString(String name)
    {
        return ifNotNull(name, tuple->tuple.get(name).toString());
    }

    public BigDecimal getBigDecimal(String name)
    {
        return ifNotNull(name, tuple->new BigDecimal(tuple.get(name).toString()));
    }

    public Date getDate(String name)
    {
        return ifNotNull(name, tuple->tuple.get(name, Date.class));
    }

    public Float getFloat(String name)
    {
        return ifNotNull(name, tuple->Float.valueOf(tuple.get(name).toString()));
    }

    public Byte getByte(String name)
    {
        return ifNotNull(name, tuple->Byte.valueOf(tuple.get(name).toString()));
    }

    public Short getShort(String name)
    {
        return ifNotNull(name, tuple->Short.valueOf(tuple.get(name).toString()));
    }

    public Timestamp getTimestamp(String name)
    {
        return ifNotNull(name, tuple->tuple.get(name, Timestamp.class));
    }

    public Time getTime(String name)
    {
        return ifNotNull(name, tuple->tuple.get(name, Time.class));
    }
    
    public Object getObject(String name)
    {
        return ifNotNull(name, tuple->tuple.get(name, Object.class));
    }
}
