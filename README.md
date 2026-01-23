## Nitori
Nitori is a CLI tool for controlling and monitoring the system's CPU and hardware on Linux-based operating systems.

### Supported features
* **CPU**: Montior hardware specifications and current configuration, set clock speeds, set kernel governor
* **Battery**: Set battery charge limit and monitor specifications, manufacturer, power usage and charge capabilities
* **Backlight**: Set and view the screen backlight brightness for built-in laptop screens
* **Suspension**: Suspend the system to RAM, freeze userspace or hibernate to disk
* **Memory**: Monitor the system memory and swap and now much is free, available, used and cached
* **Process**: List, count and find system processes and kernel threads

## Requirements
* Linux-based operating system
* Java 11 or newer

Nitori is only tested on x86_64 CPUs, but it might also work on other CPU architectures. Battery support is made with laptop lithium-ion batteries in mind.

## Download

You can download Nitori from the [releases page](https://github.com/spacebanana420/nitori/releases).

You can run `java -jar nitori.jar` and open the help screen to see what you can do.

### Install on your system (using [Yuuka](https://github.com/spacebanana420/yuuka))
```
yuuka install nitori.jar
```

## Build from source (using [Yuuka](https://github.com/spacebanana420/yuuka))
You can use my build tool Yuuka to compile Nitori from source. You need to fetch the repository, and then you can build it in various ways as seen below:

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

### Build from source into a JAR
This command compiles the Nitori source code and creates an executable JAR file that you can execute with `java -jar nitori.jar`:
```
yuuka package
```

### Build from source and install on your system
This command compiles the Nitori source code, creates an executable JAR file and then installs it system-wide, so you can run the command `nitori` from anywhere. This action requires root permission:
```
yuuka install
```
After built, "nitori.jar" is moved to `/usr/local/bin/jars/` (by default) and a script named "nitori" is created at `/usr/local/bin/`. This allows you to run the nitori command from anywhere in your system, installing the program.
