# Setting up presets

Nitori can create and read a special text file located in `/etc/nitori/` which is called a preset file. Preset files are a convenient way to tell Nitori how to configure the CPU, battery charge limit and laptop screen brightness all at once.

To create a preset, run as root `nitori -pc <name>` or `nitori --create-preset <name>`, replacing "name" with the name you want the preset to have, for example `nitori -pc fullcpu`.

After creating the preset, the file will be located at `/etc/nitori/`, for example `/etc/nitori/fullcpu.nitori`. This file contains all settings as well as instructions. To enable and modify a setting, uncomment it by removing the "#" character.

Once your preset is configured, you can run it with `nitori -p <name>` or `nitori --preset <name>`, for example `nitori -p fullcpu`.
