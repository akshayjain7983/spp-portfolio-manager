package spp.portfolio.constituents.spring;

import java.util.Collection;
import java.util.List;

import lombok.Data;

@Data
public class SppPortfolioConstituentsManagerConfiguration
{
    private Integer forecastpscoreHistoryDays = 30;
    private Collection<String> forecastPeriods = List.of("10d", "30d", "60d", "90d");
}
