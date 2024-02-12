package spp.portfolio.workflow.model;

import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(catalog = "spp", schema = "spp", name = "workflow_contexts")
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(of = {"workflow"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowContext
{
    @Id
    @OneToOne(fetch = FetchType.LAZY)
    private Workflow<?, ?> workflow;
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> context; 
}
