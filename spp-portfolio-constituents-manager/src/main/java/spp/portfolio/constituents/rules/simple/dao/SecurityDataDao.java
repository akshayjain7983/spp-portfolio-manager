package spp.portfolio.constituents.rules.simple.dao;

import static spp.portfolio.manager.utilities.sql.TuplesResultSetExtractors.tupleAttributeMapper;
import static spp.portfolio.manager.utilities.sql.TuplesResultSetExtractors.tupleMapOfListMapperResultSetExtractor;
import static spp.portfolio.manager.utilities.sql.TuplesResultSetExtractors.tupleMapperListResultSetExtractor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import io.github.funofprograming.context.ApplicationContext;
import io.github.funofprograming.context.Key;
import io.github.funofprograming.context.KeyType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import spp.portfolio.constituents.rules.Attribute;
import spp.portfolio.constituents.rules.simple.Security;
import spp.portfolio.constituents.rules.simple.SecurityImpl;
import spp.portfolio.constituents.rules.simple.SecurityType;
import spp.portfolio.constituents.util.SqlFiles;
import spp.portfolio.manager.utilities.sql.SQLHelper;
import spp.portfolio.manager.utilities.sql.SqlQueryHolder;
import spp.portfolio.manager.utilities.sql.TupleMapper;
import spp.portfolio.manager.utilities.sql.TuplesResultSetExtractor;

@Repository
public class SecurityDataDao
{
    @Value("${spp.constituents-manager.forecastpscore.history.days:30}")
    private Integer forecastPScoreHistoryDays;
    
    @Value("${spp.constituents-manager.forecastPeriods.enabled:10d,30d,60d,90d}")
    private Collection<String> forecastPeriodsEnabled;
    
    @Autowired
    private EntityManager entityManager;
    
    public Collection<Security> loadSecurities(ApplicationContext daoContext)
    {
	Collection<Security> securities = loadSecuritiesRefDataPrices(daoContext);
        loadForecastPScore(securities, daoContext);
        return securities;
    }
    
    @SuppressWarnings("unchecked")
    private Collection<Security> loadSecuritiesRefDataPrices(ApplicationContext daoContext)
    {
        LocalDate rebalanceDate = daoContext.fetch(Key.of("rebalanceDate", LocalDate.class));
        Map<String, Collection<SecurityType>> exchangesWithSecurityTypes = daoContext.fetch(Key.of("exchangesWithSecurityTypes", KeyType.<Map<String, Collection<SecurityType>>>of(Map.class)));
        
        String sql = SqlQueryHolder.getSql(SqlFiles.CONSTITUENTS_SQL, "loadSecurities");
        
        TupleMapper<Map<Attribute<?>, Optional<Object>>> tupleAttributesMapper = tupleAttributeMapper(te->Attribute.ofName(te.getAlias(), te.getJavaType()));
        
        TupleMapper<Security> tupleSecurityMapper = 
                (tuple, rowNum)->
                {
                    Long id = SQLHelper.extractFromTuple(tuple, "id", Long.class);
                    String segment = SQLHelper.extractFromTuple(tuple, "segment", String.class);
                    SecurityType securityTypeFromDb = SecurityType.valueOf(segment);
                    Map<Attribute<?>, Optional<Object>> attributes = tupleAttributesMapper.mapTuple(tuple, rowNum);
                    return new SecurityImpl(id, securityTypeFromDb, attributes);
                };
                
        TuplesResultSetExtractor<List<Security>> securityExtractor = tupleMapperListResultSetExtractor(tupleSecurityMapper);
        
        final Collection<Security> securities = new ArrayList<>();
        
        for(String exchange: exchangesWithSecurityTypes.keySet())
        {
            Collection<SecurityType> securityTypes = exchangesWithSecurityTypes.get(exchange);
            
            Query jpaQuery = entityManager.createNativeQuery(sql, Tuple.class);
            SQLHelper.setObject(jpaQuery, "rebalanceDate", rebalanceDate);
            SQLHelper.setObject(jpaQuery, "segment", securityTypes.stream().map(SecurityType::name).collect(Collectors.toSet()));
            SQLHelper.setObject(jpaQuery, "exchange", exchange);
            List<Tuple> securitiesTuple = jpaQuery.getResultList();
            
            Optional.ofNullable(securitiesTuple)
            .filter(CollectionUtils::isNotEmpty)
            .map(securityExtractor::extractFromTuples)
            .filter(CollectionUtils::isNotEmpty)
            .ifPresent(securities::addAll);
        }
        
        return securities;
    }
    
    @SuppressWarnings("unchecked")
    private void loadForecastPScore(Collection<Security> securities, ApplicationContext daoContext) 
    {
	if(CollectionUtils.isEmpty(securities))
	    return;
	
	LocalDate rebalanceDate = daoContext.fetch(Key.of("rebalanceDate", LocalDate.class));
	Map<String, Collection<SecurityType>> exchangesWithSecurityTypes = daoContext.fetch(Key.of("exchangesWithSecurityTypes", KeyType.<Map<String, Collection<SecurityType>>>of(Map.class)));
	Collection<Long> securityIds = securities.stream().map(Security::getSecurityId).collect(Collectors.toList());
	TupleMapper<Tuple> tupleAttributesMapper = (tuple, rownNum)->tuple;
        TuplesResultSetExtractor<Map<Long, List<Tuple>>> forecastPScoreExtractor = 
        	tupleMapOfListMapperResultSetExtractor((tuple, rowNum)->SQLHelper.extractFromTuple(tuple, "security_id", Long.class), tupleAttributesMapper);
	
        for(String exchange: exchangesWithSecurityTypes.keySet())
        {
            Collection<SecurityType> securityTypes = exchangesWithSecurityTypes.get(exchange);
            String sql = SqlQueryHolder.getSql(SqlFiles.CONSTITUENTS_SQL, "loadForecastPScore");
            sql = SQLHelper.replaceSQLString(sql, loadForecastPScoreHistoryTColumnsWhere());
            Query jpaQuery = entityManager.createNativeQuery(sql, Tuple.class);
            SQLHelper.setObject(jpaQuery, "rebalanceDate", rebalanceDate);
            SQLHelper.setObject(jpaQuery, "securityIds", securityIds);
            SQLHelper.setObject(jpaQuery, "segment", securityTypes.stream().map(SecurityType::name).collect(Collectors.toSet()));
            SQLHelper.setObject(jpaQuery, "exchange", exchange);
            SQLHelper.setObject(jpaQuery, "fpsHistoryDays", forecastPScoreHistoryDays);
            SQLHelper.setObject(jpaQuery, "forecastPeriod", forecastPeriodsEnabled);
            List<Tuple> securitiesForecastPscoreTuples = jpaQuery.getResultList();
            Map<Long, List<Tuple>> forecastPScoresMap = 
        	    Optional.ofNullable(securitiesForecastPscoreTuples)
                    .filter(CollectionUtils::isNotEmpty)
                    .map(forecastPScoreExtractor::extractFromTuples)
                    .orElse(Collections.emptyMap());
            
            securities.parallelStream()
            .forEach(s->
            {
        	List<Tuple> forecastPScores = forecastPScoresMap.get(s.getSecurityId());
        	Optional.ofNullable(forecastPScores)
        	.orElse(Collections.emptyList())
        	.stream()
        	.forEach(fps->{
        	    fps.getElements()
        	    .stream()
        	    .filter(te->StringUtils.startsWithIgnoreCase(te.getAlias(), "forecast_p_score_T-"))
                    .filter(te->Number.class.isAssignableFrom(te.getJavaType()))
                    .forEach(te->{
                	Optional<BigDecimal> fpScore = Optional.ofNullable(fps.get(te)).map(fp->new BigDecimal(fp.toString()));
                	String fpAttributeName = fps.get("forecast_period") + "_" + te.getAlias();
                	s.setAttributeValue(fpAttributeName, fpScore);
                    });
        	});
            });
        }
    }
    
    private String loadForecastPScoreHistoryTColumnsWhere()
    {
	StringBuilder sb = new StringBuilder();
	for(int i=1;i<=forecastPScoreHistoryDays;i++)
	    sb.append(", \"forecast_p_score_T-").append(i).append("\" NUMERIC");
	
	return sb.toString();
    }
}
