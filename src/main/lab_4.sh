#!/usr/bin/env bash

# stats
ffprobe -show_format -pretty -print_format json ~/Downloads/films/vargtimmen_1968.avi

# cut
ffmpeg -i ~/Downloads/films/vargtimmen_1968.avi -ss 0 -t 10 -c copy cut.avi

# no video
ffmpeg -i cut.avi cut.mp3

# to image
ffmpeg -i cut.avi -r 1 -f image2 cut-%02d.png

# to gif
ffmpeg -i cut.avi -r 2 -f gif cut.gif

# concat
ffmpeg -f concat -i join.txt cut_double.avi

# resolution
ffmpeg -i cut.avi -s 640x480 -c:a copy cut_res.avi

# replace sound
ffmpeg -i cut.avi -i thermo.wav -c:v copy -map 0:v:0 -map 1:a:0 -shortest cut_sound.avi
