package spp.portfolio.constituents.expose;

import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.findPortfolioDefinitionConfiguration;
import static spp.portfolio.constituents.util.PortfolioConstituentsManagerConstants.isHolidayForAnyExchange;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import spp.portfolio.constituents.rebalance.PortfolioRebalanceCommand;
import spp.portfolio.constituents.rebalance.PortfolioRebalanceExecutor;
import spp.portfolio.constituents.rules.inmemory.dao.PortfolioRebalanceRepository;
import spp.portfolio.model.definition.PortfolioDefinition;
import spp.portfolio.model.definition.PortfolioDefinitionConfiguration;
import spp.portfolio.model.definition.configuration.rules.SecurityType;
import spp.portfolio.model.rebalance.PortfolioRebalance;
import spp.portfolio.model.rebalance.PortfolioRebalanceType;

@RestController
public class PortfolioConstituentsController implements PortfolioConstituentsManager
{
    @Autowired
    private PortfolioRebalanceExecutor portfolioRebalanceExecutor;
    
    @Autowired
    private PortfolioRebalanceRepository portfolioRebalanceRepository;
    
    @Override
    @PostMapping("/portfolio-constituents/rebalance/{portfolioDefinitionId}/{portfolioRebalanceType}")
    public String rebalance(
            @PathVariable Long portfolioDefinitionId
            , @PathVariable PortfolioRebalanceType portfolioRebalanceType
            , @RequestParam LocalDate fromDate
            , @RequestParam LocalDate toDate
            )
    {
        UUID runId = UUID.randomUUID();
        PortfolioRebalanceCommand portfolioRebalanceCommand = 
                PortfolioRebalanceCommand.builder()
                .runId(runId)
                .portfolioDefinitionId(portfolioDefinitionId)
                .portfolioRebalanceType(portfolioRebalanceType)
                .date(fromDate)
                .build();
        
        PortfolioDefinitionConfiguration portfolioDefinitionConfiguration = findPortfolioDefinitionConfiguration.apply(portfolioRebalanceCommand);
        Map<String, Collection<SecurityType>> exchangesWithSecurityTypes = portfolioDefinitionConfiguration.getConfiguration().getExchangesWithSecurityTypes();
        Map<String, Collection<String>> exchangesWithSecurityTypesStr = exchangesWithSecurityTypes.entrySet().stream().collect(Collectors.toMap(Entry::getKey, e->e.getValue().stream().map(SecurityType::name).collect(Collectors.toList())));
        Boolean isHolidayForAnyExchanges = isHolidayForAnyExchange.apply(exchangesWithSecurityTypesStr, portfolioRebalanceCommand.getDate());
        
        if(!isHolidayForAnyExchanges)
        {
            portfolioRebalanceExecutor.execute(portfolioRebalanceCommand);
            return runId.toString();
        }
        else
        {
            return "It's a holiday";
        }
    }

    @Override
    @GetMapping("/portfolio-constituents/{runId}")
    public Optional<PortfolioRebalance> getRebalance(@PathVariable UUID runId)
    {
        Optional<PortfolioRebalance> rebalance = portfolioRebalanceRepository.findByRunIdAndIsActive(runId, Boolean.TRUE);
        return sanitizeRebalance(rebalance);
    }
    
    @Override
    @GetMapping("/portfolio-constituents/{portfolioDefinitionId}/{portfolioRebalanceType}")
    public Optional<PortfolioRebalance> getRebalance(
            @PathVariable Long portfolioDefinitionId
            , @PathVariable PortfolioRebalanceType portfolioRebalanceType
            , @RequestParam LocalDate rebalanceDate)
    {
        Optional<PortfolioRebalance> rebalance = portfolioRebalanceRepository.findByPortfolioDefinitionAndDateAndRebalanceTypeAndIsActive(PortfolioDefinition.builder().id(portfolioDefinitionId).build(), rebalanceDate, portfolioRebalanceType, Boolean.TRUE); 
        return sanitizeRebalance(rebalance);
    }
    
    private Optional<PortfolioRebalance> sanitizeRebalance(Optional<PortfolioRebalance> rebalance)
    {
        return rebalance.map(r->{
            
            //need only portfolio definition id
            r.setPortfolioDefinition(PortfolioDefinition.builder().id(r.getPortfolioDefinition().getId()).build());
            
            //need only portfolio definition configuration id
            r.setPortfolioDefinitionConfiguration(PortfolioDefinitionConfiguration.builder().id(r.getPortfolioDefinitionConfiguration().getId()).build());
            
            return r;
        });
    }
    
}
