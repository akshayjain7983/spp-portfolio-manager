package spp.portfolio.model.json;

import java.math.BigDecimal;

import org.springframework.boot.jackson.JsonMixin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import spp.portfolio.model.definition.configuration.Currency;
import spp.portfolio.model.definition.configuration.PortfolioConfiguration;

@JsonMixin(PortfolioConfiguration.class)
public abstract class PortfolioConfigurationMixIn
{
    @JsonCreator
    public PortfolioConfigurationMixIn(
            @JsonProperty("currency") Currency currency
            , @JsonProperty("portfolioAmountLimit") BigDecimal portfolioAmountLimit
            , @JsonProperty("portfolioInvestmentAmountLimitUpdated") Boolean portfolioInvestmentAmountLimitUpdated
            ){}
}
