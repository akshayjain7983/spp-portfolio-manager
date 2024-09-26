package spp.portfolio.model.definition.configuration.rules;

import java.util.Map;

public abstract class RelaxableFilter extends MinRunLockableFilter
{
    protected Map<RelaxationCondition, Filter> relaxedFilters;
}
