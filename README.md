    
    
    _________            ______________               
    __  ____/_______________  __ \__  /_____ _____  __
    _  /    _  __ \_  ___/_  /_/ /_  /_  __ `/_  / / /
    / /___  / /_/ /(__  )_  ____/_  / / /_/ /_  /_/ /
    \____/  \____//____/ /_/     /_/  \__,_/ _\__, /  
                                             /____/
            ASCII Game Engine for Scala3             
    

[![License](https://img.shields.io/badge/license-Apache%202-blue.svg)](https://raw.githubusercontent.com/apache/opennlp/master/LICENSE)
[![build](https://github.com/nivanov/cosplay/actions/workflows/build.yml/badge.svg)](https://github.com/nivanov/cosplay/actions/workflows/build.yml)
[![Maven Central](https://img.shields.io/maven-central/v/org.cosplayengine/cosplay.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22org.cosplayengine%22%20AND%20a:%22cosplay%22)

Check out [www.cosplayengine.com](https://cosplayengine.com) for the full documentation.

<table style="margin-top: 10px; border: none; border-collapse: collapse" cellspacing="0" cellpadding="0">
    <tbody>
        <tr style="border: none">
            <td style="border: none"><img width="128px" alt="" src="https://cosplayengine.com/images/cosplay_screenshot1.gif"></td>
            <td style="border: none"><img width="128px" alt="" src="https://cosplayengine.com/images/cosplay_screenshot2.gif"></td>
            <td style="border: none"><img width="128px" alt="" src="https://cosplayengine.com/images/cosplay_screenshot3.gif"></td>
            <td style="border: none"><img width="128px" alt="" src="https://cosplayengine.com/images/cosplay_screenshot4.gif"></td>
            <td style="border: none"><img width="128px" alt="" src="https://cosplayengine.com/images/cosplay_screenshot4-1.gif"></td>
        </tr>
        <tr style="border: none">
            <td style="border: none"><img width="128px" alt="" src="https://cosplayengine.com/images/cosplay_screenshot5.gif"></td>
            <td style="border: none"><img width="128px" alt="" src="https://cosplayengine.com/images/cosplay_screenshot6.gif"></td>
            <td style="border: none"><img width="128px" alt="" src="https://cosplayengine.com/images/cosplay_screenshot7.gif"></td>
            <td style="border: none"><img width="128px" alt="" src="https://cosplayengine.com/images/cosplay_screenshot8.gif"></td>
            <td style="border: none"><img width="128px" alt="" src="https://cosplayengine.com/images/cosplay_screenshot9.gif"></td>
        </tr>
    </tbody>
</table>

## What is CosPlay?
CosPlay is 2D ASCII game engine written in [Scala3](https://www.scala-lang.org/):
* Free, open source and royalty free
* First truly native ASCII/ANSI game engine
* Freedom of JVM runtime ecosystem 
* Native support for any ANSI terminal on macOS, Windows, Linux, Unix, or z/OS
* 8-bit and "True Color" 24-bit color modes
* Advanced sprite-based animation
* Camera focus tracking 
* Extensive 2D ASCII graphics support
* Powerful user-programmable shaders
* Built-in particle effects support
* Extensive imaging support including [REXPaint](https://www.gridsagegames.com/rexpaint) format
* Multi-channel audio & ASCII-video support
* 277 built-in FIGLet fonts with full rendering
* Built-in log viewer, debugger and terminal emulator
* CosPlay also comes with many examples for each functional area. The source code for these examples is shipped with the project (see org.cosplay.examples package) 

## Install and Use Cosplay
Prerequisites:
* Java 11+ ([download](https://www.java.com/en/download/))

Using SBT:
```scala
libraryDependencies += "org.cosplayengine" % "cosplay" % "0.9.3"
```
Using [Maven](https://mvnrepository.com/artifact/org.cosplayengine/cosplay):
```xml
<dependency>
  <groupId>org.cosplayengine</groupId>
  <artifactId>cosplay</artifactId>
  <version>0.9.3</version>
</dependency>
```

* libraryDependencies +=
    "org.cosplayengine" % "cosplay" % "0.9.2"

## One-time Git clone & build:
* $ git clone https://github.com/nivanov/cosplay.git
* $ cd cosplay
* $ sbt package
* CosPlay comes with support for both SBT and Maven builds so you can use both libraries of commands
* Use Cosplay to create new and fun games

* This is assuming you've already cloned the Cosplay repo on your machine
* For developers interested in contributing to the project, you can work within and create pull requests to the active UTIK branch
* For those just interested in using Cosply to create games can just download the most recent version

## Development 
Fork and Clone
* First fork the repository and clone to your own machine
* Submit pull requests to active branch UTIK to submit your possible changes
* Documentation and Information on possible changes can be found below within the discord 


## Questions?
* Join [discord](https://discord.gg/gDQuYJDM)
* [Documentation](https://cosplayengine.com), [examples](https://github.com/nivanov/cosplay/tree/master/modules/cosplay/src/main/scala/org/cosplay/examples) and built-in [games](https://github.com/nivanov/cosplay/tree/master/modules/cosplay/src/main/scala/org/cosplay/games)
* Post a question at [Stack Overflow](https://stackoverflow.com/questions/ask) using <code>cosplay</code> tag
* File a bug or improvement in [GitHub Issues](https://github.com/nivanov/cosplay/issues)
* Join project on [GitHub](https://github.com/nivanov/cosplay/issues)

## Copyright
Copyright (C) 2023 Rowan Games, Inc.

<img src="https://cosplayengine.com/images/cosplay-grey.gif" height="24px" alt="CosPlay Logo">


