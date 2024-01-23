package spp.portfolio.constituents.util;

import java.util.Arrays;

import spp.portfolio.manager.utilities.sql.SqlQueryHolder;

public class SqlFiles
{
    public static final String CONSTITUENTS_SQL = "sql/constituents.sql";
    
    public static void load() 
    {
        Arrays.asList(
                CONSTITUENTS_SQL
                )
        .forEach(SqlQueryHolder::loadQueries);
    }
}
