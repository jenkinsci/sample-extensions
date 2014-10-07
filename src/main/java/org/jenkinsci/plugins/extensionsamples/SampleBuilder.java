/*
 * The MIT License
 *
 * Copyright (c) 2014 Red Hat, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jenkinsci.plugins.extensionsamples;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.FreeStyleProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;

import java.io.IOException;

import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * Example build step.
 */
public class SampleBuilder extends Builder {

    private final boolean failTheBuild;

    @DataBoundConstructor
    public SampleBuilder(boolean failTheBuild) {
        this.failTheBuild = failTheBuild;
    }

    /**
     * Expose config for jelly as <tt>it.failTheBuild</tt>, restricting access
     * because this is for view only and should not be considered an API.
     */
    @Restricted(NoExternalUse.class)
    public boolean getFailTheBuild() {
        return failTheBuild;
    }

    @Override
    public boolean perform(
            AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener
    ) throws InterruptedException, IOException {

        // Print string to build console output.
        listener.error("Sample error recorded");

        // Launch remote command on slave, redirect stdout to console output and wait for completion.
        launcher.launch().cmds("echo", "Sample command run from build step").stdout(listener).start().join();

        // Write string content to job workspace on slave.
        build.getWorkspace().child("sample.log").write("Sample contnet", "UTF-8");

        // Fail the build conditionally
        return !failTheBuild;
    }

    @Extension
    public static class Descriptor extends BuildStepDescriptor<Builder> {

        @Override
        public String getDisplayName() {
            return Messages.SampleBuilder_DisplayName();
        }

        /**
         * Restrict to FreeStyleProject only.
         */
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return jobType.isAssignableFrom(FreeStyleProject.class);
        }
    }
}
