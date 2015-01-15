#!/bin/sh
set -e

mvn exec:java -Dexec.mainClass="com.grid.server.ChatServer" -Dexec.classpathScope=runtime