package spp.portfolio.model.definition.configuration.rules;

import java.util.Collection;

import lombok.Data;

@Data
public class SortingRanker implements Ranker
{
    private Collection<SortingRankerAttribute> sortingRankerAttributes;
}
