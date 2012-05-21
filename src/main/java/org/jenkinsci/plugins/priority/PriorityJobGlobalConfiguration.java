package org.jenkinsci.plugins.priority;

import hudson.Extension;
import jenkins.model.GlobalConfiguration;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

/**
 * @author londrusek
 */
@Extension
public class PriorityJobGlobalConfiguration extends GlobalConfiguration {

  private static boolean useJobPriority;

  @Override
  public boolean configure(StaplerRequest req, JSONObject json) throws hudson.model.Descriptor.FormException {
    json = json.getJSONObject("jobPriority");
    useJobPriority = json.getBoolean("useJobPriority");
    save();

    return true;
  }

  public boolean isUseJobPriority() {
    return useJobPriority;
  }
  
  public static boolean isUseJobPriorityEnabled(){
    return useJobPriority;
  }

}
