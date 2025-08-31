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
* Linux-based systems are the only systems that work
* Java 11 or newer

Nitori is only tested on x86_64 CPUs, but it might also work on other CPU architectures. Battery support is only tested with lithium-ion batteries.

## Download

You can download Nitori from the [releases page](https://github.com/spacebanana420/nitori/releases).

You can run `java -jar nitori.jar` and open the help screen to see what you can do.

## Build from source (using [Yuuka](https://github.com/spacebanana420/yuuka))

### Build a JAR
```
yuuka package
```

### Install on your system (requires root)
```
yuuka install
```
