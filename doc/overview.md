# Nitori source overview

Nitori separates code between source files based on the category of task they do as well as the task itself. The design idea sums up to separating high-level orchestration code from low-level implementation.

## Package summary

* `nitori`: Contains main.java, for the program's starting point, and tasks.java, for orchestrating the program's features
* `nitori.cli`: Command-line parsing and help screen
* `nitori.features`: The implementation of different features, such as CPU control or process monitoring
* `nitori.io`: File operations and standard output printing
* `nitori.preset`: Logic related to Nitori presets and the parsing of preset files
