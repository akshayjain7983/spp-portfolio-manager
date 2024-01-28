{loadSecurities}
SELECT 
:rebalanceDate as_of
, s.id 
, s.exchange_code
, s.exchange 
, s.security_name
, s.exchange_group 
, s.segment 
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
s.segment IN :segment
AND 
s.exchange IN :exchange
AND s.exchange_code = '500086'
