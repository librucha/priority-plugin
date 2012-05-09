package org.jenkinsci.plugins.priority;

import hudson.Extension;
import hudson.model.JobProperty;
import hudson.model.JobPropertyDescriptor;
import hudson.model.AbstractProject;
import hudson.util.FormValidation;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

/**
 * @author Libor Ondrusek
 */
public class PriorityConfigJobProperty extends JobProperty<AbstractProject<?, ?>> {

  private int jobPriority = Constants.DEFAULT_PRIORITY;

  @DataBoundConstructor
  public PriorityConfigJobProperty(Integer jobPriority) {
    if (jobPriority != null) {
      this.jobPriority = jobPriority;
    }
  }

  public int getJobPriority() {
    return jobPriority;
  }

  @Override
  public JobPropertyDescriptor getDescriptor() {
    return (DescriptorImpl) super.getDescriptor();
  }

  @Extension
  public static final class DescriptorImpl extends JobPropertyDescriptor {

    @Override
    public String getDisplayName() {
      return "Job priority";
    }

    /**
     * Default priority for each job.
     */
    int getDefaultPriority() {
      return Constants.DEFAULT_PRIORITY;
    }

    public FormValidation doCheckJobPriority(@QueryParameter Integer value) {
      if (value == null) {
        return FormValidation.ok();
      }
      else if (value >= Constants.MIN_PRIORITY && value <= Constants.MAX_PRIORITY) {
        return FormValidation.ok();
      }
      return FormValidation.error("Value %d is not number between or equal %d and %d", value, Constants.MIN_PRIORITY, Constants.MAX_PRIORITY);
    }
  }
}
