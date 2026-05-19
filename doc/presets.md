# Setting up presets

Nitori can create and read a special text file located in `/etc/nitori/` which is called a preset file. Preset files are a convenient way to tell Nitori how to configure the CPU, battery charge limit and laptop screen brightness all at once.

To create a preset, run as root `nitori -pc <name>` or `nitori --create-preset <name>`, replacing "name" with the name you want the preset to have, for example `nitori -pc fullcpu`.

After creating the preset, the file will be located at `/etc/nitori/`, for example `/etc/nitori/fullcpu.nitori`. This file contains all settings as well as instructions. To enable and modify a setting, uncomment it by removing the "#" character.

A default preset file usually looks like this:

```
# Nitori preset file
# You can define a set of commands to run and configurations to set by calling this preset
# For example, you could make a power-saving preset which lowers the CPU clock speeds and laptop backlight brightness
# Lines that start with "#" are comments and so they are ignored, remove this character to enable a setting
# The following settings you can use are seen below

# Set the CPU's minimum and maximum clock speeds in megahertz (MHz)
#cpu_min=800
#cpu_max=3800

#Set the CPU's kernel governor
#cpu_gov=powersave

#Set the CPU's energy preference if supported
#cpu_energy_mode=balance_performance

#Reset the CPU's clock speeds to your hardware's limits (overrides cpu_min and cpu_max)
#cpu_reset=true

#Set the battery charge limit (in percentage %) if supported
#battery_limit=60

#Set the backlight brightness (in percentage %) if supported
#backlight_brightness=30
```
From here, you can uncomment the settings you want to use, removing the "#" character, and configure it. When you run this preset, it will set the configuration you applied here for you.

Once your preset is configured, you can run it with `nitori -p <name>` or `nitori --preset <name>`, for example `nitori -p fullcpu`.
