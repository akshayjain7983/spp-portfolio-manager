package spp.portfolio.model.definition.configuration;

import java.time.temporal.ChronoUnit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioRebalanceFrequency
{
    private FrequencyType frequencyType;
    private ChronoUnit frequencyUnit;
    private Integer frequencyValue;
}
