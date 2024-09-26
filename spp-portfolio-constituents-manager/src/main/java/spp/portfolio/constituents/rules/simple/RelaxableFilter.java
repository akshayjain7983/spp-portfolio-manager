package spp.portfolio.constituents.rules.simple;

import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.relaxationCondition;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import io.github.funofprograming.context.ConcurrentApplicationContext;
import spp.portfolio.constituents.rules.RelaxationCondition;

public abstract class RelaxableFilter implements Filter
{
    protected Map<RelaxationCondition, Filter> relaxedFilters;
    
    @Override
    public Optional<Security> execute(Optional<Security> security, ConcurrentApplicationContext context)
    {
	Optional<Security> filteredSecurity = security;
	Filter relaxedFilter = getRelaxedFilter(context);
	
	if(Objects.nonNull(relaxedFilter))
	    filteredSecurity = relaxedFilter.execute(filteredSecurity, context);
	else 
	    filteredSecurity = executeFilter(filteredSecurity, context);
	
	return filteredSecurity;
    }
    
    protected abstract Optional<Security> executeFilter(Optional<Security> security, ConcurrentApplicationContext context);

    protected Filter getRelaxedFilter(ConcurrentApplicationContext context)
    {
	RelaxationCondition relaxationConditionApplied = context.fetch(relaxationCondition);
	
	if(Objects.isNull(relaxationConditionApplied))
	    return null;
	
	return Optional.ofNullable(relaxedFilters)
			.map(rf->rf.get(relaxationConditionApplied))
			.orElse(null);
    }
}
