{loadSecurities}
SELECT 
s.id 
, s.exchange_code exchangeCode
, s.exchange 
, s.security_name securityName
, s.exchange_group exchangeGroup 
, s.segment 
, s.status 
, srd.face_value faceValue 
, srd.total_outstanding_shares totalOutstandingShares 
, srd.listing_date listingDate 
, sic.level_0 level0  
, sic.level_1 level1
, sic.level_2 level2
, sic.level_3 level3
, sic.level_4 level4
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
LEFT OUTER JOIN (
	SELECT * FROM crosstab('
		SELECT security_id::int4, identifier_key::varchar, identifier_value::varchar  FROM spp.security_identifiers si 
		WHERE security_id = 4075
		AND effective_date <= '''||:rebalanceDate||'''
		AND discontinued_date > '''||:rebalanceDate||'''
		ORDER BY 1,2')
	AS (security_id int4, isin varchar, ticker varchar)
) sid
ON s.id = sid.security_id
WHERE 
s.status = 'Active'
AND 
CASE WHEN :exchangeCode IS NULL THEN 1=1
ELSE s.exchange_code = :exchangeCode
END 