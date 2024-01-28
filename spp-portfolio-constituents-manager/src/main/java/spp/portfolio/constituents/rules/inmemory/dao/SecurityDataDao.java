package spp.portfolio.constituents.rules.inmemory.dao;

import static spp.portfolio.manager.utilities.sql.TuplesResultSetExtractors.tupleAttributeMapper;
import static spp.portfolio.manager.utilities.sql.TuplesResultSetExtractors.tupleMapperListResultSetExtractor;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.github.funofprograming.context.ApplicationContext;
import io.github.funofprograming.context.Key;
import io.github.funofprograming.context.KeyType;
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
    public Collection<Security> loadSecurities(ApplicationContext daoContext)
    {
        LocalDate rebalanceDate = daoContext.fetch(Key.of("rebalanceDate", LocalDate.class));
        Collection<SecurityType> securityTypes = daoContext.fetch(Key.of("securityTypes", KeyType.<Collection<SecurityType>>of(Collection.class)));
        Collection<String> exchanges = daoContext.fetch(Key.of("exchanges", KeyType.<Collection<String>>of(Collection.class)));
        
        String sql = SqlQueryHolder.getSql(SqlFiles.CONSTITUENTS_SQL, "loadSecurities");
        Query jpaQuery = entityManager.createNativeQuery(sql, Tuple.class);
        SQLHelper.setObject(jpaQuery, "rebalanceDate", rebalanceDate);
        SQLHelper.setObject(jpaQuery, "segment", securityTypes.stream().map(SecurityType::getSymbol).collect(Collectors.toSet()));
        SQLHelper.setObject(jpaQuery, "exchange", exchanges);
        List<Tuple> securitiesTuple = jpaQuery.getResultList();
        if(CollectionUtils.isEmpty(securitiesTuple))
            return Collections.emptyList();
        
        TupleMapper<Map<Attribute<?>, Optional<Object>>> tupleAttributesMapper = tupleAttributeMapper(te->Attribute.ofName(te.getAlias(), te.getJavaType()));
        
        TupleMapper<Security> tupleSecurityMapper = 
                (tuple, rowNum)->
                {
                    Long id = SQLHelper.extractFromTuple(tuple, "id", Long.class);
                    String segment = SQLHelper.extractFromTuple(tuple, "segment", String.class);
                    SecurityType securityTypeFromDb = SecurityType.getFromSymbol(segment);
                    Map<Attribute<?>, Optional<Object>> attributes = tupleAttributesMapper.mapTuple(tuple, rowNum);
                    return new SecurityImpl(id, securityTypeFromDb, attributes);
                };
                
        TuplesResultSetExtractor<List<Security>> securityExtractor = tupleMapperListResultSetExtractor(tupleSecurityMapper);
        Collection<Security> securities = securityExtractor.extractFromTuples(securitiesTuple);
        return securities;
    }
}
