from django.shortcuts import render
from django.http import HttpResponse
import profile as mod_profile
import listing
import json

'''

The main view function that handles all incoming requests
regarding user profiles.

- GET responds with a user's profile
- POST receives information for creating, editing, or deleting a user's profile.

A user's profile is used to create a new job instance to look for people
in your area to do a particular job. Your profile also holds data about
previous jobs you have offered or done through the service. It can also
contain data in what your interests are or professions.

A user profile contains the following information:

	- Name (First, Last)
	- City
	- Professions / Description of your skills.
	- Age (Must be at least 16?)

We want to limit personal information for users to prevent discrimination.

'''

# TODO: Security warning: Do NOT use reflection straight from a POST or GET request, 
# we need to check to make sure what was sent to the server is a legitimate command.

def profile(request):
	if request.method == 'GET':
		data = request.GET.copy()

		result, err_code = mod_profile.get(data['profile_id'])

		if not err_code: # result will be a dictionary to send back to client.
			return HttpResponse(json.dumps(result), content_type='application/json')
		else:
			return HttpResponse(json.dumps({ 'error': err_code }), content_type='application/json')

	elif request.method == 'POST':
		data = request.POST.copy()
		
		operation = data['request'] # operation will be an exact string representation of the associated function name in profile.py
		del data['request']

		result, err_code = getattr(mod_profile, operation)(data)

		if not err_code:
			return HttpResponse(json.dumps({'success' : '1'}), content_type='application/json')
		else:
			return HttpResponse(json.dumps({ 'error' : err_code }), content_type='application/json')


'''

The main view function that handles all incoming requests regarding listings.

We want the following functionality with listings:

	- Create Listings
	- Fetch/Search Listings (with sorting)
	- Update Listings from user interaction
	- Edit Listing settings
	- Delete Listings

Listings, like profiles will have unique listing_ids

'''

def listings(request):
	if request.method == 'GET':
		data = request.GET.copy()

		result, err_code = listing.get(data['listing_id'])

		if not err_code:
			return HttpResponse(json.dumps(result), content_type='application/json')
		else:
			return HttpResponse(json.dumps({ 'error' : err_code }), content_type='application/json')

	elif request.method = 'POST':
		data = request.POST.copy()
		
		operation = data['request'] # operation will be an exact string representation of the associated function name in profile.py
		del data['request']

		result, err_code = getattr(listing, operation)(data)

		if not err_code:
			return HttpResponse(json.dumps({'success' : '1'}), content_type='application/json')
		else:
			return HttpResponse(json.dumps({ 'error' : err_code }), content_type='application/json')
