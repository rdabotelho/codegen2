package com.m2r.codegencli.utils;

import org.eclipse.jgit.api.Git;
import java.io.File;
import java.util.Arrays;

public class TemplateRepo {

    public static void cloneBranch(String url, String branch, File destDir) {
        try {
            Git.cloneRepository()
                    .setURI(url)
                    .setDirectory(destDir)
                    .setBranchesToClone(Arrays.asList(branch))
                    .setBranch(branch)
                    .call();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
