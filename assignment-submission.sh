#!/usr/bin/env bash

MOVIE_FUN_APP_URL=moviefun-grouchy-kangaroo.apps.evans.pal.pivotal.io

cd ~/workspace/assignment-submission

./gradlew replatformingRemovingPersistenceToFileSystem -PmovieFunUrl=https://${MOVIE_FUN_APP_URL}