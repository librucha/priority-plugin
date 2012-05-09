package org.jenkinsci.plugins.priority;

import static java.lang.String.format;
import hudson.Extension;
import hudson.model.Action;
import hudson.model.Queue;
import hudson.model.Queue.Executable;
import hudson.model.Queue.QueueDecisionHandler;
import hudson.model.Queue.Task;
import hudson.model.Computer;
import hudson.model.Executor;
import hudson.model.Job;
import hudson.model.Node;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import jenkins.model.Jenkins;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;

/**
 * @author londrusek
 */
@Extension
public class PriorityJobQueueDecisionHandler extends QueueDecisionHandler {

  private final Logger LOGGER = Logger.getLogger(getClass().getSimpleName());

  @Override
  public boolean shouldSchedule(Task task, List<Action> actions) {

    // if are some idle executor, then schedule normally
    if (!getAvailableExecutors().isEmpty()) {
      return true;
    }

    Map<Task, Executor> runningLowestPriorityTasks = Maps.newHashMapWithExpectedSize(0);
    int thisTaskPriority = getPriority(task);
    LOGGER.info(format("This task %s has priority: %d", task.getDisplayName(), thisTaskPriority));

    for (Entry<Executor, Task> entry : getRunningTasks().entrySet()) {
      int runningTaskPriority = getPriority(entry.getValue());

      if (thisTaskPriority > runningTaskPriority) {
        runningLowestPriorityTasks.put(entry.getValue(), entry.getKey());
      }
    }

    // check again for available executors
    if (getAvailableExecutors().isEmpty()) {
      Comparator<Queue.Task> taskPriorityComparator = new Comparator<Queue.Task>() {
        @Override
        public int compare(Task t1, Task t2) {
          return new Integer(getPriority(t1)).compareTo(getPriority(t2));
        }
      };
      Task lowestProrityTask = Ordering.<Task> from(taskPriorityComparator).nullsLast().min(runningLowestPriorityTasks.keySet());
      Executor lowestPriorityExecutor = runningLowestPriorityTasks.get(lowestProrityTask);
      lowestPriorityExecutor.pauseOrCont();
      Executor newExecutor = new PriorityJobExecutor(lowestPriorityExecutor, task);
      newExecutor.start();

      return false; // this Job is doing outside queue
    }

    return true;
  }

  public Map<Executor, Task> getRunningTasks() {
    Map<Executor, Task> tasks = Maps.newHashMap();
    for (hudson.model.Computer comp : jenkins.model.Jenkins.getInstance().getComputers()) {
      for (Executor executor : comp.getExecutors()) {
        Executable currentExecutable = executor.getCurrentExecutable();
        if (currentExecutable != null) {
          tasks.put(executor, currentExecutable.getParent().getOwnerTask());
        }
      }
    }
    return tasks;
  }

  private List<Executor> getAvailableExecutors() {
    return Lists.newArrayList(Iterables.filter(Jenkins.getInstance().getQueue().getParked().keySet(), new Predicate<Executor>() {
      @Override
      public boolean apply(Executor executor) {
        return executor.getOwner().getNode().getMode().equals(Node.Mode.NORMAL);
      }
    }));
  }

  private int getPriority(Task task) {
    PriorityConfigJobProperty priorityProperty = ((Job<?, ?>) task).getProperty(PriorityConfigJobProperty.class);
    return (priorityProperty != null ? priorityProperty.getJobPriority() : Constants.DEFAULT_PRIORITY);
  }

  private Executor createNewExecutor(Computer owner) {
    Executor executor = new Executor(owner, owner.getNumExecutors() + 1);
    owner.getExecutors().add(executor);

    return executor;
  }

}
