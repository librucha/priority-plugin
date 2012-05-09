package org.jenkinsci.plugins.priority;

import hudson.model.Queue.Executable;
import hudson.model.Queue.Task;
import hudson.model.Executor;

import java.io.IOException;

/**
 * @author londrusek
 */
public class PriorityJobExecutor extends Executor {

  private final Executor pausedExecutor;
  private final Task task;

  public PriorityJobExecutor(Executor pausedExecutor, Task task) {
    super(pausedExecutor.getOwner(), pausedExecutor.getOwner().getNumExecutors() + 1);
    this.task = task;
    this.pausedExecutor = pausedExecutor;
  }

  @Override
  public void run() {
    try {
      Executable exe = task.createExecutable();
      exe.run();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    finally {
      pausedExecutor.pauseOrCont();
    }
  }
}
