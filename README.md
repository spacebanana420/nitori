## Nitori
Nitori is a CLI program that can control and monitor the system's hardware on Linux-based operating systems:
```sh
space@Wakasagi ~> doas nitori -cmax 2300
Setting maximum clock speed 2300MHz for all cores
```
You can find more examples on how to use Nitori [here](doc/examples.md).


### Supported features
* **CPU**: Montior hardware specifications and current configuration, set clock speeds, set kernel governor
* **Battery**: Set battery charge limit and monitor specifications, manufacturer, power usage and charge capabilities
* **Backlight**: Set and view the screen backlight brightness for built-in laptop screens
* **Suspension**: Suspend the system to RAM, freeze userspace or hibernate to disk
* **Memory**: Monitor the system RAM and swap and now much is free, available, used and cached
* **GPU memory**: Monitor the VRAM and GTT of one or multiple connected graphics chips
* **Process**: List, count and find system processes and kernel threads
* **Temperature**: Monitor the temperature of known hardware sensors

## Requirements
* Linux-based operating system
* Java 11 or newer

Nitori is only tested on x86_64 CPUs, but it should also work on other CPU architectures. Battery support and features might vary with different models. Nitori is currently Linux-only because it interacts with features specifically from the Linux kernel.

## Download

You can download Nitori from the [releases page](https://github.com/spacebanana420/nitori/releases).

You can run `java -jar nitori.jar` and open the help screen to see what you can do.

### Install on your system (using [Yuuka](https://github.com/spacebanana420/yuuka))
```
yuuka install nitori.jar
```

## Documentation

* [Nitori Examples](doc/examples.md)
* [Build Nitori from source](doc/build.md)
* [Setting up presets](doc/presets.md)
* [Source code overview](doc/overview.md)
