package com.m2r.codegen.parser.modeling;

import java.util.ArrayList;
import java.util.List;

public class DomainList {

    private StringWrapper projectName;
    private List<Domain> domains = new ArrayList<>();
    private StringWrapper basePackage;

    public StringWrapper getProjectName() {
        return projectName;
    }

    public void setProjectName(StringWrapper projectName) {
        this.projectName = projectName;
    }

    public List<Domain> getDomains() {
        return domains;
    }

    public void setDomains(List<Domain> domains) {
        this.domains = domains;
    }

    public Domain getDomainByName(String name) {
        for (Domain d : domains) {
            if (name.equals(d.getName().toString())) {
                return d;
            }
        }
        return null;
    }

    public StringWrapper getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(StringWrapper basePackage) {
        this.basePackage = basePackage;
    }

    public void finallyProcess() {
        for (Domain d : domains) {
            for (DomainAttribute a : d.getAttributes()) {
                if (!a.isBasic()) {
                    String domainName = null;
                    if (a.isList()) {
                        domainName = a.getItemType().toString();
                    }
                    else {
                        domainName = a.getType().toString();
                    }
                    Domain typeDomain = getDomainByName(domainName);
                    if (typeDomain == null) {
                        throw new RuntimeException("Domain " + domainName + " not declared!");
                    }
                    a.setTypeDomain(typeDomain);
                }
            }
        }
    }



}
