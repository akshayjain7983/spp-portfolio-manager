package spp.portfolio.model.definition.configuration.rules;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CompoundFilter extends RelaxableFilter
{
    private BooleanOperator operator;
    private List<Filter> filters;
}
