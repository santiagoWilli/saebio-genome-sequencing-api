#!/bin/bash

get_abs_filename() {
  echo "$(cd "$(dirname "$1")" && pwd)/$(basename "$1")"
}

dir_path=$(dirname $(get_abs_filename $0))

mvn -f $dir_path/../pom.xml exec:java -Dexec.args="--port $1 --db-port $2 --database test" > $dir_path/../log/app.txt 2> $dir_path/../log/error.txt &
echo $! > $dir_path/.app_pid