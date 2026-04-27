# Building Nitori from source

### Requirements
* Git
* OpenJDK 11 or newer
* [Yuuka](https://github.com/spacebanana420/yuuka)

You can use my build tool Yuuka to compile Nitori from source. You need to fetch the repository and then you can build it in various ways as seen below:

### Get the project
```
git clone https://github.com/spacebanana420/nitori.git
cd nitori
```

### Build from source
This command compiles the Nitori source code into the resulting .class files:
```
yuuka build
```
For the end-user that just wants to use my program, this method is impractical since the bytecode is not bundled. Instead you can do the 2 methods below.

### Build from source into a JAR (recommended)
This command compiles the Nitori source code and creates an executable JAR file that you can execute with `java -jar nitori.jar`:
```
yuuka package
```
Now you have a JAR file you can execute with `java -jar`.

### Build from source and install on your system (recommended)
This command compiles the Nitori source code, creates an executable JAR file and then installs it system-wide, so you can run the command `nitori` from anywhere. This action requires root permission:
```
yuuka install
```
After built, "nitori.jar" is moved to `/usr/local/bin/jars/` (by default) and a script named "nitori" is created at `/usr/local/bin/`. This allows you to run the nitori command from anywhere in your system, installing the program.
