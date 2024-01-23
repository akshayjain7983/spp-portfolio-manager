package spp.portfolio.constituents.rules.inmemory.dao;

import static spp.portfolio.manager.utilities.sql.TuplesResultSetExtractors.tupleAttributeMapper;
import static spp.portfolio.manager.utilities.sql.TuplesResultSetExtractors.tupleMapperListResultSetExtractor;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.apache.commons.collections4.CollectionUtils;

import io.github.funofprograming.context.ApplicationContextKey;
import io.github.funofprograming.context.ConcurrentApplicationContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import spp.portfolio.constituents.rules.Security;
import spp.portfolio.constituents.rules.SecurityType;
import spp.portfolio.constituents.rules.inmemory.Attribute;
import spp.portfolio.constituents.rules.inmemory.SecurityImpl;
import spp.portfolio.constituents.util.SqlFiles;
import spp.portfolio.manager.utilities.sql.SQLHelper;
import spp.portfolio.manager.utilities.sql.SqlQueryHolder;
import spp.portfolio.manager.utilities.sql.TupleMapper;
import spp.portfolio.manager.utilities.sql.TuplesResultSetExtractor;

@Repository
public class SecurityDataDao
{
    @Autowired
    private EntityManager entityManager;
    
    @SuppressWarnings("unchecked")
    public Collection<Security> loadSecurities(ConcurrentApplicationContext context)
    {
        LocalDate rebalanceDate = context.fetch(ApplicationContextKey.of("rebalanceDate", LocalDate.class));
        SecurityType securityType = context.fetch(ApplicationContextKey.of("securityType", SecurityType.class));
        String exchangeCode = context.fetch(ApplicationContextKey.of("exchangeCode", String.class));
        
        String sql = SqlQueryHolder.getSql(SqlFiles.CONSTITUENTS_SQL, "loadSecurities");
        Query jpaQuery = entityManager.createNativeQuery(sql, Tuple.class);
        SQLHelper.setObject(jpaQuery, "rebalanceDate", rebalanceDate);
        SQLHelper.setObject(jpaQuery, "segment", securityType.getSymbol());
        SQLHelper.setObject(jpaQuery, "exchangeCode", exchangeCode);
        List<Tuple> securitiesTuple = jpaQuery.getResultList();
        if(CollectionUtils.isEmpty(securitiesTuple))
            return Collections.emptyList();
        
        TupleMapper<Map<Attribute<?>, Object>> tupleAttributesMapper = tupleAttributeMapper(te->Attribute.ofName(te.getAlias(), te.getJavaType()));
        
        TupleMapper<Security> tupleSecurityMapper = 
                (tuple, rowNum)->
                {
                    Long id = SQLHelper.extractFromTuple(tuple, "id", Long.class);
                    String segment = SQLHelper.extractFromTuple(tuple, "segment", String.class);
                    SecurityType securityTypeFromDb = SecurityType.getFromSymbol(segment);
                    Map<Attribute<?>, Object> attributes = tupleAttributesMapper.mapTuple(tuple, rowNum);
                    return new SecurityImpl(id, securityTypeFromDb, attributes);
                };
                
        TuplesResultSetExtractor<List<Security>> securityExtractor = tupleMapperListResultSetExtractor(tupleSecurityMapper);
        Collection<Security> securities = securityExtractor.extractFromTuples(securitiesTuple);
        return securities;
    }
}
