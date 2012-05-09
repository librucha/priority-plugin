package org.jenkinsci.plugins.priority;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Job;
import hudson.views.ListViewColumnDescriptor;
import hudson.views.ListViewColumn;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * @author Libor Ondrusek
 */
public class PriorityJobColumn extends ListViewColumn {

  @DataBoundConstructor
  public PriorityJobColumn() {
  }

  public String getPriority(final Job<?, ?> job) {
    final PriorityConfigJobProperty jobProperty = job.getProperty(PriorityConfigJobProperty.class);
    if (jobProperty != null) {
      return Integer.toString(jobProperty.getJobPriority());
    }
    else {
      return Integer.toString(Constants.DEFAULT_PRIORITY); // default
    }
  }

  @Override
  public Descriptor<ListViewColumn> getDescriptor() {
    return (DescriptorImpl) super.getDescriptor();
  }

  @Extension
  public static class DescriptorImpl extends ListViewColumnDescriptor {

    @Override
    public String getDisplayName() {
      return "Priority value";
    }

    @Override
    public boolean shownByDefault() {
      return true;
    }
  }
}
