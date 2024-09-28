{loadSecurities}
SELECT
:rebalanceDate as_of
, s.id 
, s.exchange_code
, e."name" exchange 
, s.security_name
, s.exchange_group 
, es."name" segment
, s.status 
, srd.face_value 
, srd.total_outstanding_shares 
, srd.listing_date 
, sic.level_0  
, sic.level_1
, sic.level_2
, sic.level_3
, sic.level_4
, sp."open" open_price
, sp.high high_price
, sp.low low_price
, sp."close" close_price
, sp.volume 
, sid.isin
, sid.ticker
FROM spp.securities s
INNER JOIN spp.exchange_segments es 
ON s.exchange_segment_id = es.id 
AND es.status = 'Active'
INNER JOIN spp.exchanges e 
ON es.exchange_id = e.id 
LEFT OUTER JOIN spp.security_reference_data srd  
ON s.id = srd.security_id 
AND srd.effective_date <= :rebalanceDate
AND srd.discontinued_date > :rebalanceDate
LEFT OUTER JOIN spp.security_industry_classification sic 
ON s.id = sic.security_id 
AND sic.effective_date <= :rebalanceDate
AND sic.discontinued_date > :rebalanceDate
LEFT OUTER JOIN spp.security_prices sp 
ON s.id = sp.security_id
AND sp."date" = :rebalanceDate
LEFT OUTER JOIN (
	SELECT * FROM crosstab('
		SELECT security_id::int4, identifier_key::varchar, identifier_value::varchar  
		FROM spp.security_identifiers si 
		WHERE effective_date <= '''||:rebalanceDate||'''
		AND discontinued_date > '''||:rebalanceDate||'''
		ORDER BY 1,2')
	AS (security_id int4, isin varchar, ticker varchar)
) sid
ON s.id = sid.security_id
WHERE 
s.status = 'Active'
AND
es."name" IN :segment
AND 
e."name" = :exchange

{loadForecastPScore}
SELECT * FROM crosstab('
			SELECT fsr.security_id, fsr.forecast_period, (''T-'' || CAST((RANK() OVER (PARTITION BY fsr.security_id, es."name" ORDER BY fps."date" DESC)) AS TEXT)) AS T, forecasted_p_score
			FROM spp.securities s 
			INNER JOIN spp.exchange_segments es 
			ON es.status = ''Active''
			AND s.exchange_segment_id = es.id 
			INNER JOIN spp.exchanges e 
			ON es.exchange_id = e.id 
			INNER JOIN spp.indices i 
			ON e.id = i.exchange_id
			AND i.status = ''Active''
			LEFT OUTER JOIN spp.forecast_security_returns fsr 
			ON fsr.security_id = s.id 
			AND fsr.is_active = TRUE 
			LEFT OUTER JOIN spp.forecast_index_returns fir 
			ON fir.index_id = i.id 
			AND fir.is_active = TRUE 
			LEFT OUTER JOIN spp.forecast_p_score fps 
			ON fsr.id = fps.forecast_security_returns_id 
			AND fir.id = fps.forecast_index_returns_id 
			WHERE e."name" = '''|| :exchange ||'''
			AND es."name" IN ('|| replace(replace(replace('''' || :segment || '''', ',',''','''), ')''', ''''), '''(', '''') ||')
			AND s.id IN ('|| replace(replace('' || :securityIds, ')', ''), '(', '') ||')
			AND fps."date" BETWEEN spp.previous_business_date(e."name", es."name", '''|| :rebalanceDate ||''', ('|| :fpsHistoryDays ||'-1)) AND '''|| :rebalanceDate ||'''
			AND fsr.forecast_period IN ('|| replace(replace(replace('''' || :forecastPeriod || '''', ',',''','''), ')''', ''''), '''(', '''') ||')
			ORDER BY fsr.security_id, fps."date" 
		'
		, 
		'
			SELECT ''T-'' || CAST(t AS TEXT) AS category FROM generate_series(1, '|| :fpsHistoryDays ||') t
		'
	)
	AS (security_id int4, forecast_period TEXT [SQL[?]])




