package org.jenkinsci.plugins.priority;

import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

/**
 * @author londrusek
 */
public class PriorityJobGlobalConfiguration extends GlobalConfiguration {

  private boolean useJobPriority;

  public boolean getUseJobPriority() {
    return useJobPriority;
  }

  @Override
  public boolean configure(StaplerRequest req, JSONObject json) throws hudson.model.Descriptor.FormException {
    return super.configure(req, json);
  }
}
