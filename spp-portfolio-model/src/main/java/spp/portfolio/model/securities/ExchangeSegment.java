package spp.portfolio.model.securities;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;

import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "exchange_segments")
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(of = {"id"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeSegment
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "exchange_id", nullable = false)
    @ManyToOne
    private Exchange exchange;
    private String name;
    @LastModifiedDate
    private Instant lastUpdatedTimestamp;
    private String description;
    private String status;
    private LocalTime openTime;
    private LocalTime closeTime;
    private ZoneId timezone;
}
