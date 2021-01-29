#!/bin/bash

get_abs_filename() {
  echo "$(cd "$(dirname "$1")" && pwd)/$(basename "$1")"
}

dir_path=$(dirname $(get_abs_filename $0))

mongo --port $1 --eval "db.getSiblingDB('test').dropDatabase()" &>/dev/null
mongo --port $1 --eval "db.getSiblingDB('admin').shutdownServer()" &>/dev/null