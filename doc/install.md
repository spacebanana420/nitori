# Installing Nitori

Nitori is a program built in Java and distributed as a JAR file. This file requires the Java Runtime in order to be executed. If you are looking for building Nitori from source, see [this page instead](build.md).

## Requirements

* Linux-based operating system
* Java 11 or newer

Nitori currently only works on a Linux-based system because it makes deep use of the pseudo-files that the Linux kernel creates. Java 11 or newer is required to run Nitori, you can satisfy this dependency by installing OpenJDK or JRE.

## Installing Java

Installing Java on Linux is very easy, and your OS, regardless which it is, will have it in its repository. JDK is larger but it is only necessary if you intend on *building* Nitori or any other Java program from source. JRE is smaller since it only contains what is necessary for *running* Nitori and Java programs.

The examples below need to be run as root:

### Arch/Artix
```sh
pacman -S jre-openjdk
```
or
```sh
pacman -S jdk-openjdk
```

### Debian/Devuan
```sh
apt install default-jre
```
or
```sh
apt install default-jdk
```

## Downloading Nitori

You can get the latest version of Nitori [here](https://github.com/spacebanana420/nitori/releases). Download the JAR and run it with `java -jar nitori.jar`.

## Installing Nitori on your system

Since Nitori is a CLI program, you want to eventually run it from anywhere just by typing the `nitori` command. In order to do this, you have to install Nitori on your system.

### Installing Nitori automatically (using Yuuka)

To install Nitori automatically, you need my [Yuuka](https://github.com/spacebanana420/yuuka) build tool. Download the latest version of Yuuka from its releases, place the JAR at the same place where Nitori's JAR is, and run:

```sh
java -jar yuuka.jar install nitori.jar
```

If you already have Yuuka installed on your system, you can run instead:

```sh
yuuka install nitori.jar
```

Both commands require root.


### Installing Nitori manually

If you do not want to download or install my build tool, you have to do manually what my program does under the hood to install Nitori.

Create the following script:

```sh
touch nitori
chmod +x nitori
```

Open the file with a text editor, and paste the following text:

```sh
#!/bin/sh
java -jar /usr/local/bin/jars/nitori.jar "$@"
```

Now run as root:

```sh
mkdir /usr/local/bin/jars/
mv nitori.jar /usr/local/bin/jars/
mv nitori /usr/local/bin
```
