#!/bin/bash

get_abs_filename() {
  echo "$(cd "$(dirname "$1")" && pwd)/$(basename "$1")"
}

dir_path=$(dirname $(get_abs_filename $0))

kill -9 `cat $dir_path/.app_pid 2>/dev/null` 2>/dev/null && rm -f $dir_path/.app_pid