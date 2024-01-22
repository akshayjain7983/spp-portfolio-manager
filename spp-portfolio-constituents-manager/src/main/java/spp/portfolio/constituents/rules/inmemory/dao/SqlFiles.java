package spp.portfolio.constituents.rules.inmemory.dao;

import java.util.Arrays;

import spp.portfolio.manager.utilities.sql.SqlQueryHolder;

public class SqlFiles
{
    public static final String CONSTITUENTS_SQL = "sql/constituents.sql";
    
    static 
    {
        Arrays.asList(
                CONSTITUENTS_SQL
                )
        .forEach(SqlQueryHolder::loadQueries);
    }
}
