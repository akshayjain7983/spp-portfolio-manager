package spp.portfolio.model.definition.configuration.rules;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class MinRunRule implements PortfolioRule
{
    private Long minRunDays;
    private BigDecimal minRunLockMarketValueRatio;
}
