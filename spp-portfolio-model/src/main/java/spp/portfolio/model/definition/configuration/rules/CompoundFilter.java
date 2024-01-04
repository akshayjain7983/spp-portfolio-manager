package spp.portfolio.model.definition.configuration.rules;

import java.util.List;

import lombok.Data;

@Data
public class CompoundFilter implements Filter
{
    private BooleanOperator operator;
    private List<Filter> filters;
}
