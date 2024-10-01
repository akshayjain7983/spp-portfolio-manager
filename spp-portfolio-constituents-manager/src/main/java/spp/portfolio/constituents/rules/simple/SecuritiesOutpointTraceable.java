package spp.portfolio.constituents.rules.simple;

/**
 * Interface for any rule to trace collection of securities outpoint. Implementation via AOP.
 */
public interface SecuritiesOutpointTraceable
{
    String getOutpointRepresentation();
}
