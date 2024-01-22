package spp.portfolio.constituents.rules.inmemory;

import jakarta.persistence.Tuple;
import lombok.Data;
import spp.portfolio.constituents.rules.Security;
import spp.portfolio.constituents.rules.SecurityType;
import spp.portfolio.manager.utilities.sql.SQLHelper;

@Data
public class SecurityImpl implements Security
{
    private final Long securityId;
    private final SecurityType type;
    private final Tuple tuple;

    @Override
    public <T> T getAttributeValue(String attributeKey, Class<T> attributeType)
    {
        return SQLHelper.extractFromTuple(tuple, attributeKey, attributeType);
    }
}
