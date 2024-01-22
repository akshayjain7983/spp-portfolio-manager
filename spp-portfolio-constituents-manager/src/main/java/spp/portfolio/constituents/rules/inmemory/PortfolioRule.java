package spp.portfolio.constituents.rules.inmemory;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import io.github.funofprograming.context.ConcurrentApplicationContext;
import spp.portfolio.constituents.rules.Security;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "type")
@JsonSubTypes({
    @Type(name = "SourceDataRule", value = SourceDataRule.class),
    @Type(name = "ParentPortfolioRule", value = ParentPortfolioRule.class),
    @Type(name = "FiltersRule", value = FiltersRule.class)
})
public interface PortfolioRule
{
    boolean doExecute(ConcurrentApplicationContext context);
    
    Collection<Security> execute(Collection<Security> securities, ConcurrentApplicationContext context);
}
