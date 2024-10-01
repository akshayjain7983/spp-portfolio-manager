package spp.portfolio.constituents.rules.simple;

/**
 * Interface for any rule to trace single security outpoint. Implementation via AOP.
 */
public interface SecurityOutpointTraceable
{
    String getOutpointRepresentation();
}
