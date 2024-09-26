package spp.portfolio.model.definition.configuration.rules;

import java.util.Map;

public abstract class RelaxableFilter implements Filter
{
    protected Map<RelaxationCondition, Filter> relaxedFilters;
}
