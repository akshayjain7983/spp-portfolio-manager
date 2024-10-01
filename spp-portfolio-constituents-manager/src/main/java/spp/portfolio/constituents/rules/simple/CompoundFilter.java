package spp.portfolio.constituents.rules.simple;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.github.funofprograming.context.ConcurrentApplicationContext;
import lombok.Data;
import lombok.EqualsAndHashCode;
import spp.portfolio.constituents.rules.BooleanOperator;

@Data
@EqualsAndHashCode(callSuper=true)
public class CompoundFilter extends RelaxableFilter
{
    private BooleanOperator operator;
    private List<Filter> filters;
    
    @Override
    protected Optional<Security> filterNormal(Optional<Security> security, ConcurrentApplicationContext context)
    {
        Optional<Security> filteredSecurity = security;
        List<Boolean> results = new ArrayList<>();
        
        FILTER_LOOP: for(Filter f : filters)
        {
            filteredSecurity = f.execute(filteredSecurity, context);
            
            switch (operator)
            {
                case AND:
                    if(filteredSecurity.isEmpty()) //AND filter failed
                        break FILTER_LOOP;
                    break;
                    
                case OR:
                    if(filteredSecurity.isPresent()) //OR filter passed
                        break FILTER_LOOP;
                    break;
                    
                case XOR:
                    if(
                            (filteredSecurity.isPresent() && results.contains(Boolean.FALSE)) //current filter passed but some failure in previous ones so XOR filter pass
                            ||
                            (filteredSecurity.isEmpty() && results.contains(Boolean.TRUE)) //current filter failed but some success in previous ones so XOR filter pass
                            
                       )
                    {
                        filteredSecurity = security;
                        break FILTER_LOOP;
                    }
                    break;
                    
                case NOT:
                    if(filteredSecurity.isPresent())
                    {
                        filteredSecurity = Optional.empty(); //NOT filter failed
                        break FILTER_LOOP;
                    }        
                    break;
                    
                default:
                    throw new IllegalArgumentException("Unexpected value: " + operator);
            }
            
            filteredSecurity = security; //continue to check other filters with original security
            results.add(filteredSecurity.isPresent());
        }
        
        return filteredSecurity;
    }

    @Override
    public String getOutpointRepresentation()
    {
	return this.toString();
    }
}
