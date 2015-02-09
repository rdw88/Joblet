# Script that downloads any song just by inputting the artist and song title.

import requests
import sys
from bs4 import BeautifulSoup
from subprocess import call
import os

def get_download_url(soup):
	results = soup.findAll('ol', { 'class' : 'item-section'})[0]
	results_list = results.findAll('li')
	
	if len(results_list[0].findAll('div', {'class' : 'yt-lockup-thumbnail'})) == 0: # If there are spelling mistakes in query
		results_list = results_list[1]
	else:
		results_list = results_list[0]

	for result in results_list:
		link = result.find('h3', { 'class' : 'yt-lockup-title' }).find('a')
		title = unicode(link.string)

		if 'music video' not in title.lower() and 'full album' not in title.lower() and 'album' not in title.lower():
			return str(link.get('href'))

def download(artist, song):
	search = artist + '+' + song
	query = 'http://www.youtube.com/results?search_query=%s' % search

	response = requests.get(query)
	soup = BeautifulSoup(response.text)

	call(['sudo', 'youtube-dl', 'http://www.youtube.com%s' % get_download_url(soup).encode('ascii'), '-o', '/usr/local/apache2/htdocs/django/ryguy/static/music/%s.mp3' % song.replace('+', ' ').encode('ascii'), '-x'])