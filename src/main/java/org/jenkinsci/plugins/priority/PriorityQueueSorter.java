package org.jenkinsci.plugins.priority;

import static com.google.common.base.Preconditions.checkNotNull;
import hudson.Extension;
import hudson.model.Queue.BuildableItem;
import hudson.model.AbstractProject;
import hudson.model.queue.AbstractQueueSorterImpl;

import java.util.Comparator;
import java.util.List;

import com.google.common.collect.Ordering;

/**
 * @author Libor Ondrusek
 */
@Extension
public class PriorityQueueSorter extends AbstractQueueSorterImpl {

  private Comparator<BuildableItem> comparator;

  public PriorityQueueSorter() {
    this.comparator = new Comparator<BuildableItem>() {

      public int compare(BuildableItem o1, BuildableItem o2) {
        return Ordering.natural().compare(getJobPriority(o1), getJobPriority(o2));
      }

      public int getJobPriority(BuildableItem buildable) {
        AbstractProject<?, ?> project = (AbstractProject<?, ?>) buildable.task;
        PriorityConfigJobProperty property = project.getProperty(PriorityConfigJobProperty.class);
        if (property != null) {
          return property.getJobPriority();
        }
        else {
          return Constants.DEFAULT_PRIORITY;
        }
      }
    };

  }

  @Override
  public void sortBuildableItems(List<BuildableItem> buildables) {
    checkNotNull(buildables, "buildables must not be null");

    buildables = Ordering.from(comparator).immutableSortedCopy(buildables);
  }

}
