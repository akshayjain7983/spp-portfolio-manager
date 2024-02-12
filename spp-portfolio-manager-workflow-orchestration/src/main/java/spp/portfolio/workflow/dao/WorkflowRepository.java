package spp.portfolio.workflow.dao;

import java.time.ZonedDateTime;
import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;

import spp.portfolio.workflow.model.Workflow;
import spp.portfolio.workflow.model.WorkflowType;

public interface WorkflowRepository extends JpaRepository<Workflow<?, ?>, Long>
{
    Collection<Workflow<?, ?>> findByWorkflowType(WorkflowType workflowType);
    
    Collection<Workflow<?, ?>> findByWorkflowTypeAndFromDateAndToDate(WorkflowType workflowType, ZonedDateTime fromDate, ZonedDateTime toDate);
}
