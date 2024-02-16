package spp.portfolio.model.marketdata;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;

import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(catalog = "spp", schema = "spp", name = "holidays")
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(of = {"id"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Holiday implements Comparable<Holiday>
{
    public static final Comparator<Holiday> HOLIDAY_ASC_COMPARATOR = Comparator.comparing(Holiday::getDate, Comparator.naturalOrder());
    public static final Comparator<Holiday> HOLIDAY_DESC_COMPARATOR = Comparator.comparing(Holiday::getDate, Comparator.reverseOrder());
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate date;
    private String exchange;
    private String segment;
    private String type;
    private String reason;
    @LastModifiedBy
    private Instant lastUpdatedTimestamp;
    
    @Override
    public int compareTo(Holiday other)
    {
        return HOLIDAY_ASC_COMPARATOR.compare(this, other);
    }
}
