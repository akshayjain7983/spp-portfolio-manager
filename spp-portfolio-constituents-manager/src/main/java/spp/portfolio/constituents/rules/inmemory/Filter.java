package spp.portfolio.constituents.rules.inmemory;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import io.github.funofprograming.context.ConcurrentApplicationContext;
import spp.portfolio.constituents.rules.Security;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({
    @Type(name = "CompoundFilter", value = CompoundFilter.class),
    @Type(name = "ExpressionFilter", value = ExpressionFilter.class)
})
public interface Filter
{
    Optional<Security> execute(Optional<Security> security, ConcurrentApplicationContext context);
}
