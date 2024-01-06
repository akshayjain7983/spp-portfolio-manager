package spp.portfolio.model.definition.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum Currency
{
    INR("INR", "Indian Rupee", "India");
    
    private final String code;
    private final String name;
    private final String region;
    
    public static Currency fromCode(String code)
    {
        switch (code)
        {
            case "INR": return INR;
            
            default:
                throw new IllegalArgumentException("Unexpected value: " + code);
        }
    }
}
