package sanchez.sanchez.sergio.integration.aggregation;

import java.util.Date;

import org.springframework.integration.aggregator.MessageGroupProcessor;
import org.springframework.integration.store.MessageGroup;
import org.springframework.messaging.Message;

import sanchez.sanchez.sergio.integration.constants.IntegrationConstants;
import sanchez.sanchez.sergio.persistence.entity.IterationEntity;
import sanchez.sanchez.sergio.persistence.entity.TaskEntity;

public class IterationGroupProcessor implements MessageGroupProcessor {

	@Override
	public Object processMessageGroup(MessageGroup messageGroup) {
		Date iterationStart = (Date)messageGroup.getOne().getHeaders().get(IntegrationConstants.ITERATION_START_HEADER);
        Date iterationFinish = new Date();
        Long duration = Math.abs((iterationStart.getTime() - iterationFinish.getTime()) / 1000);
        IterationEntity iterationEntity = new IterationEntity(iterationStart, iterationFinish, duration);
        iterationEntity.setTotalTasks(messageGroup.getMessages().size());
        for(Message<?> message: messageGroup.getMessages()){
            TaskEntity task = (TaskEntity)message.getPayload();
            if(task.isSuccess())
                iterationEntity.setTotalComments(iterationEntity.getTotalComments() + task.getComments().size());
            else
                iterationEntity.setTotalFailedTasks(iterationEntity.getTotalFailedTasks() + 1);
            iterationEntity.addTask(task);
            
        }
        return iterationEntity;
	}

}
