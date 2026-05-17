# Nitori Examples

Below are a few examples on how to use Nitori and how the program is useful for daily hardware monitoring, configuration and administration. These are examples ran on my hardware:

### Viewing CPU specifications and configuration

```sh
space@Wakasagi ~> nitori -ci
[CPU Specifications]
 * Minimum supported clock speed: 419.421 MHz
 * Maximum supported clock speed: 5137.904 MHz
 * Base clock speed: N/A
 * Number of threads: 16
 * Available governors: performance powersave
 * Available energy preferences: default performance balance_performance balance_power power
 * Turbo enabled: No

 * Current minimum clock speed: 419.421 MHz
 * Current maximum clock speed: 2300.0 MHz
 * Current governor: powersave
 * Current energy mode: balance_performance
```

### Setting CPU minimum and maximum clock speeds
```sh
space@Wakasagi ~> nitori -cmin 900 -cmax 2300
Setting minimum clock speed 900MHz for all cores
Setting maximum clock speed 2300MHz for all cores
```

### Counting and finding processes
```sh
space@Wakasagi ~> nitori -proc
Total number of running system processes: 375
Userspace processes: 102
Kernel processes: 273
```

```sh
space@Wakasagi ~> nitori -procf java
Found the following processes including java in the path:

Process ID 4566
  * Name: java
  * Command: java -jar /usr/local/bin/jars/parasol.jar Music
  * Memory usage (MB): 87.844
  * Swap usage (MB): 0.0

Process ID 16426
  * Name: java
  * Command: java -jar /usr/local/bin/jars/nitori.jar -procf java
  * Memory usage (MB): 68.96
  * Swap usage (MB): 0.0
```

### Viewing battery information

```sh
space@Wakasagi ~> nitori -bi
[Battery Specifications]
 * Technology: Li-ion
 * Manufacturer: OEM
 * Model: standard
 * Energy capacity: 6.4Ah
 * Original energy capacity: 6.4Ah
 * Battery health: 100%
 * Current charge: 3.84Ah
 * Current charge percentage: 60%
 * Charge cycle count: 0
 * Power usage: N/A
```
