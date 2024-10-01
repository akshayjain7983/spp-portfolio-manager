package spp.portfolio.constituents.rules.simple;

import java.util.Optional;

import io.github.funofprograming.context.ConcurrentApplicationContext;

public abstract class MinRunLockableFilter implements Filter, SecurityOutpointTraceable
{
    @Override
    public Optional<Security> execute(Optional<Security> security, ConcurrentApplicationContext context)
    {
	Boolean minRunLocked = security.flatMap(s->s.getAttributeValue("min_run_locked", Boolean.class)).orElse(Boolean.FALSE);
	Optional<Security> filteredSecurity = minRunLocked ? security : filterNonMinRunLockable(security, context); // no filtering needed if minRunLocked
	return filteredSecurity;
    }

    protected abstract Optional<Security> filterNonMinRunLockable(Optional<Security> security, ConcurrentApplicationContext context);
}
