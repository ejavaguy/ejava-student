package ejava.jpa.example.validation;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;

/**
 * This group specification defines a sequence of groups to validate 
 * against until one of them fails or they all pass. If one of the groups 
 * fail -- the follow-on groups do not get checked.
 */
@GroupSequence({Default.class, DBChecks.class, DataChecks.class})
public interface ValidationSequence {}
