package spp.portfolio.constituents.rules.inmemory.dao;

import static spp.portfolio.manager.utilities.sql.TuplesResultSetExtractors.tupleAttributeMapper;
import static spp.portfolio.manager.utilities.sql.TuplesResultSetExtractors.tupleMapperResultSetExtractor;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import io.github.funofprograming.context.ApplicationContextKey;
import io.github.funofprograming.context.ConcurrentApplicationContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import spp.portfolio.constituents.rules.Security;
import spp.portfolio.constituents.rules.SecurityType;
import spp.portfolio.constituents.rules.inmemory.SecurityImpl;
import spp.portfolio.manager.utilities.sql.SQLHelper;
import spp.portfolio.manager.utilities.sql.SqlQueryHolder;
import spp.portfolio.manager.utilities.sql.TupleMapper;
import spp.portfolio.manager.utilities.sql.TuplesResultSetExtractor;

@Repository
public class SecurityDataDao
{
    @Autowired
    private EntityManager entityManager;

    public Collection<Security> loadSecurities(ConcurrentApplicationContext context)
    {
        LocalDate rebalanceDate = context.fetch(ApplicationContextKey.of("rebalanceDate", LocalDate.class));
        String exchangeCode = context.fetch(ApplicationContextKey.of("exchangeCode", String.class));
        
        String sql = SqlQueryHolder.getSql(SqlFiles.CONSTITUENTS_SQL, "loadSecurities");
        Query q = entityManager.createNativeQuery(sql, Tuple.class);
        SQLHelper.setObject(q, "rebalanceDate", rebalanceDate);
        SQLHelper.setObject(q, "exchangeCode", exchangeCode);
        List<Tuple> securitiesTuple = q.getResultList();
        if(CollectionUtils.isEmpty(securitiesTuple))
            return Collections.emptyList();
        
//        TupleMapper<Map<String, Object>> tupleAttributeMapper = tupleAttributeMapper();
        TupleMapper<Security> tupleSecurityMapper = 
                (tuple, rowNum)->
                {
                    Long id = SQLHelper.extractFromTuple(tuple, "id", Long.class);
                    String segment = SQLHelper.extractFromTuple(tuple, "segment", String.class);
                    SecurityType securityType = SecurityType.getFromSymbol(segment);
//                    Map<String, Object> attributes = tupleAttributeMapper.mapTuple(tuple, rowNum);
                    
                    return new SecurityImpl(id, securityType, tuple);
                };
                
        TuplesResultSetExtractor<Security> securityExtractor = tupleMapperResultSetExtractor(tupleSecurityMapper);
        List<Security> securities = securityExtractor.extractFromTuples(securitiesTuple);
        return securities;
    }
}
