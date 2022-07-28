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
                if (!a.isBasicType()) {
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
        for (Domain d : domains) {
            for (DomainAttribute a : d.getAttributes()) {
                if (a.isComposition()) {
                    Domain part = a.getTypeDomain();
                    part.setCompositionOwner(d);
                }
                if (a.hasTypeDomain()) {
                    for (DomainAttribute aa : a.getTypeDomain().getAttributes()) {
                        if (aa.hasTypeDomain() && aa.getTypeDomain().getName().toString().equals(d.getName().toString())) {
                            if (a.isMain() && aa.isMain()) {
                                aa.setMain(false);
                                aa.setMappedBy(a.getName().toCamelCase());
                            }
                            else if (!a.isMain() && !aa.isMain()) {
                                a.setMain(true);
                                aa.setMappedBy(a.getName().toCamelCase());
                            }
                            else if (a.isMain()) {
                                aa.setMappedBy(a.getName().toCamelCase());
                            }
                            else {
                                a.setMappedBy(aa.getName().toCamelCase());
                            }
                            break;
                        }
                    }
                }
            }
        }
    }



}
