# Codegen 2.0

## What is the new codegen 2.0?

Codegen 2.0 is a command line interface (CLI) for generating code based on fully customized user templates.
Through its own DSL (domain-specific language) the codegen generates the artifacts specified by the user in the modeling files and templates files.

Unlike other code generators on the market, codegen 2.0 templates do not present "polluted code", as it separates the source file from the generation logic.

Here is an example of a java class template using the velocity library:

```groovy
package com.m2r.codegen;

class $domain.name {

#foreach( $attribute in $domain.attributes )
    private $attribute.type $attribute.name;
#end

#foreach( $attribute in $domain.attributes )
    public $attribute.type get${attribute.name.toPascalCase()}() {
        return $attribute.name;
    }

    public void set${attribute.name.toPascalCase()}($attribute.type id) {
        this.$attribute.name = $attribute.name;
    }

#end
}
```

Now see an example of the same template using codegen 2.0:

```java
package com.m2r.codegen;

class Entity {

    private String name;

    public String getName() {
        return $attribute.name;
    }

    public void setNam(String name) {
        this.name = name;
    }
}
```

As we can see, the template is much cleaner and this is only possible because codegen 2.0 separates the source file from the generation logic, through a definition file where the template is divided into generation blocks.
Thus, the code nature of the template is preserved, facilitating its maintenance.

Here's what the previous template definition file would look like:

```groovy
template {
	sourceFile: 'entity.java'
	targetFile: 'src/main/java/com/m2r/example/entity/${domain.name}.java'
	consider: 'entity'
	block(3, 3) {
		replace('Entity', domain.name)
	}
	block(5, 5) {
		iterate(domain.attributes, item) {
			replace('String', item.type)
			replace('name', item.name)
		}
	}
	block(7, 9) {
		iterate(domain.attributes, item) {
			replace('String', item.type)
			replace('name', item.name)
			replace('Name', item.name.pascalCase)
		}
	}
	block(11, 13) {
		iterate(domain.attributes, item) {
			replace('String', item.type)
			replace('name', item.name)
			replace('Name', item.name.pascalCase)
		}
	}
}

```

## How to install codegen 2.0?

## How to use codegen 2.0?

