from django.shortcuts import render
from django.http import HttpResponse
from django.core.servers.basehttp import FileWrapper
from django.utils.encoding import smart_str
from subprocess import call
from music import download
import os
import json
import socket

def music(request):
	if request.method == 'GET':
		return render(request, 'music.html')
	elif request.method == 'POST':
		data = request.POST
		song = data['song'].replace(' ', '+')
		artist = data['artist'].replace(' ', '+')

		download(artist, song)

		file_name = '%s.m4a' % song.replace('+', ' ')
		file_path = '/mp3s/%s' % file_name
		response = HttpResponse(json.dumps({'url' : file_path}), content_type='application/json')
		return response