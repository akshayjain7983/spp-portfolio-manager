package spp.portfolio.constituents.util;

import java.util.Map;

import io.github.funofprograming.context.ApplicationContextKey;
import io.github.funofprograming.context.KeyType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PortfolioConstituentsManagerConstants
{
    public static final ApplicationContextKey<Map<Long, String>> securityOutpointMapKey = ApplicationContextKey.of("securityOutpointMapKey", KeyType.<Map<Long, String>>of(Map.class));
}
