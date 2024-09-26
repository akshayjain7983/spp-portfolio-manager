package spp.portfolio.constituents.rules.simple;

import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.relaxationCondition;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import io.github.funofprograming.context.ConcurrentApplicationContext;
import lombok.Data;
import lombok.EqualsAndHashCode;
import spp.portfolio.constituents.rules.RelaxationCondition;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class RelaxableFilter extends MinRunLockableFilter
{
    protected Map<RelaxationCondition, Filter> relaxedFilters;
    
    protected Optional<Security> executeNonMinRunLockableFilter(Optional<Security> security, ConcurrentApplicationContext context)
    {
	Optional<Filter> relaxedFilter = getRelaxedFilter(context);
	Optional<Security> filteredSecurity = relaxedFilter.map(rf->rf.execute(security, context)).orElseGet(()->executeNormalFilter(security, context));
	return filteredSecurity;
    }
    
    protected abstract Optional<Security> executeNormalFilter(Optional<Security> security, ConcurrentApplicationContext context);

    protected Optional<Filter> getRelaxedFilter(ConcurrentApplicationContext context)
    {
	RelaxationCondition relaxationConditionApplied = context.fetch(relaxationCondition);
	
	if(Objects.isNull(relaxationConditionApplied))
	    return Optional.empty();
	
	return Optional.ofNullable(relaxedFilters)
			.map(rf->rf.get(relaxationConditionApplied));
    }
}
