#!/bin/bash

get_abs_filename() {
  echo "$(cd "$(dirname "$1")" && pwd)/$(basename "$1")"
}

dir_path=$(dirname $(get_abs_filename $0))

mongod --noauth --dbpath $dir_path/../db --port $1 &>$dir_path/../log/db.txt &
mongo --port $1 --eval "db.getSiblingDB('test').dropDatabase()" &>/dev/null