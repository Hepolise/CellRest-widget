#!/bin/bash
if [[ -z "$1" ]]; then
  echo "Usage: ${0##*/} 'commit's name'"
fi
git add .
git commit -m "$1"
git remote add origin https://github.com/Hepolise/TrafficWidget.git
git push -u origin master
