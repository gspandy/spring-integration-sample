package sanchez.sanchez.sergio.persistence.constraints.group;

import javax.validation.GroupSequence;
import javax.validation.groups.Default;

@GroupSequence({Default.class, Extended.class})
public interface ICommonSequence {

}
