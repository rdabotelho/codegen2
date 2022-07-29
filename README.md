# Codegen 2.0

## About

Codegen 2.0 is a command line interface (CLI) for generating code based on fully customized user templates.
Through its own DSL (domain-specific language) the codegen generates the artifacts specified by the user in the modeling files and templates files.

Unlike other code generators on the market, codegen 2.0 templates do not present "polluted code", as it separates the source file from the generation logic.

Here is an example of a java class template using the velocity engine:

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

Now see an example of the same template using codegen 2.0

```java
package com.m2r.codegen;

class Entity {

    private String name;

    public String getName() {
        return this.name;
    }

    public void setNam(String name) {
        this.name = name;
    }
}
```

As we can see, the template is much cleaner and this is only possible because codegen 2.0 separates the source file from the generation logic, through a definition file where the template is divided into generation blocks.
Thus, the code nature of the template is preserved, facilitating its maintenance.

Here's what the previous template definition file would look like

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

## Installation

To install codegen 2.0, you need to download the zip file (in the link of the latest release version of the project) and follow the steps below (for each OS).

### macOS / Linux

1. Extract the gencode zip file into the home directory `~/`.
2. Give execution permission to the `codegen` file.
```shell
chmod +x ~/codegen/codegen
```
3. Add gencode directory to the OS path.
```shell
export PATH=$PATH:~/codegen
```
>**Note:** It is important to include this script in an initialization file to keep the directory in the path.
4. Run the following command `codegen -v`, if everything has been done correctly, you will see the following output:
```shell
Codegen command line interface (CLI)
Version: 2.0.0
```

### Windows

1. Extract the gencode zip file into the home directory `~/`.
2. Add gencode directory to the OS path (Environment variable -> User variables -> Path).
3. Run the following command `codegen -v`, if everything has been done correctly, you will see the following output:
```shell
Codegen command line interface (CLI)
Version: 2.0.0
```

## Usage

To use codegen, we need to initialize it from within our project, using the following command:

```bash
codegen.sh init
```

If you prefer, we can clone the initial structure of a git repository, so we can reuse other templates already created.

```bash
codegen.sh init https://github.com/rdabotelho/mytemplates.git
```

After initialized, we can see, inside our project, the following structure created.

```bash
- .codegen.sh
  - modeling
  - templates
```
- **modeling:** Folder for models files (with own DSL).
- **templates:** Folder for the template files and definition files (with own DSL).

### Create a template
To create a template run the following command: `codegen create-template <FILE-NAME>`.

Example:
```bash
codegen.sh create-template entity.java
```

See that two files were created in the folder `.codegen/templates`.
- **entity.df:** Template definition (with generation logic).
- **entity.java:** Template (without generation logic).

>**Note:** Both files are created with sample code that generates a class in Java. To ignore this example code, just erase and implement your own code.

### Create a modeling file

To be able to generate code, in addition to the template files, we need to create the domain model file.

To create a modeling file, run the following command: `codegen create-model <FILE-NAME>`.

Example:
```bash
codegen.sh create-model entity.md
```

See that one file was created in the folder `.codegen/modeling`.
- **entity.md:** Modeling file (domain model).

```groovy
entity HelloWorld {
	String message
}
```

>**Note:** The modeling file is created with a sample code of a hello world entity. To ignore this example code, just erase and implement your own code.

### Code generation

Now, we can generate our code based on the template and modeling file created previously.

To do code generation, use the following command: `codegen generate <MODELING-FILE>`.

Example (if you haven't deleted the code generated in the previous examples):
```bash
codegen.sh generate entity.md
```

See that one file was created in the folder `src/main/java/com/m2r/example/entity`.
- **HelloWorld.java:** Java class generated through the codegen.

```java
package com.m2r.example.entity;

public class HelloWorld {
	private String message;
	public String getMessage() {
		return this.message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
```

### Codegen CLI commands

The following is a list of the commands available in the codegen CLI.

| Command             | Description                                             | Parameters                                                                       |
|---------------------|---------------------------------------------------------|----------------------------------------------------------------------------------|
| **init**            | Initialize a codegen project                            | - git url (optional)<br/>- git branch (optional)                                 |
| **create-template** | Create a new template file                              | - template file name                                                             |
| **create-model**    | Create a new modeling file                              | - model file name                                                                |
| **generate**        | Generate files based on templates                       | - model file name<br/>- force override (optional)                                |
| **shift**           | Shift blocks automatically in template definition files | - template definition file name<br/>- started line<br/>- total of lines to shift |

