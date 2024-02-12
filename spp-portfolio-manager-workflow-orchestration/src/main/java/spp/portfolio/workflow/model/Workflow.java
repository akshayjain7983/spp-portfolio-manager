package spp.portfolio.workflow.model;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Collection;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import spp.portfolio.model.definition.PortfolioDefinition;

@Data
@Entity
@Table(catalog = "spp", schema = "spp", name = "workflows")
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(of = {"id"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Workflow<W, T>
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private PortfolioDefinition portfolioDefinition;
    private WorkflowType workflowType;
    private ZonedDateTime fromDate;
    private ZonedDateTime toDate;
    @JdbcTypeCode(SqlTypes.JSON)
    private W workflowRequest;
    private WorkflowStatus status;
    @CreatedDate
    private Instant createdTimestamp;
    private String createdBy;
    @LastModifiedDate
    private Instant lastUpdatedTimestamp;
    private String lastUpdatedBy;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "workflow")
    private Collection<WorkflowTask<T, W>> workflowTasks;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "workflow")
    private WorkflowContext workfowContext;
}
