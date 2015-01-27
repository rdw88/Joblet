from django.test import TestCase, Client
from models import Profile, Listing
import profile
import listing
import error
import time

class ProfileTests(TestCase):
	def __init__(self, arg):
		super(ProfileTests, self).__init__(arg)
		self.data = {'email':'ryan@gmail.com', 'first_name':'Ryan', 'last_name':'Wise', 'password':'password', 
		'dob':'1-8-1994', 'tags':'Programming', 'city_code':'San Francisco, CA', 'profile_id':'349384', 
		'date_created': '1-18-2015', 'request':'create'}

	def test_profile_creation(self):
		res, err = profile.create(self.data)
		self.assertEqual(res, True)

		res, err = profile.create(self.data)		
		self.assertEqual(err, error.ERROR_EMAIL_IN_USE)

	def test_profile_login(self):
		data = self.data
		profile.create(data)
		res, err = profile.login(data)
		self.assertEqual(res, True)

		data['password'] = '1234'
		res, err = profile.login(data)
		self.assertEqual(err, error.ERROR_INCORRECT_PASSWORD)

		data['email'] = '29389@gmail.com'
		res, err = profile.login(data)
		self.assertEqual(err, error.ERROR_NO_SUCH_PROFILE)


class ListingTests(TestCase):
	def test_large_creation(self):
		prof = {'email':'ryan@gmail.com', 'first_name':'Ryan', 'last_name':'Wise', 'password':'password', 
		'dob':'1-8-1994', 'tags':'Programming', 'city_code':'San Francisco, CA', 'profile_id':'349384', 
		'date_created': '1-18-2015', 'request':'create'}

		profile.create(prof)
		prof_id = Profile.objects.get(email='ryan@gmail.com').profile_id

		l = {'profile_id' : prof_id, 'password':'password',
		'job_title' : '23', 'starting_amount' : '50', 'current_bid': '50', 'min_reputation':'50',
		'active_until':'1-23-2015 5:20PM', 'tag' : 'Programming', 'address' : '1234 h ln', 'city':'cityname',
		'state':'CA', 'latitude' : '45', 'longitude': '55'}

		now = time.time() * 1000.0

		for i in range(10000):
			lis, err = listing.create(l)

			if i % 1000 == 0:
				print i

		print (time.time() * 1000.0) - now

		all_listings = Listing.objects.all()
		for i in range(10000):
			at = str(all_listings[i].active_until)

			if at == '1-22-2015':
				print 'k'

		print (time.time() * 1000.0) - now


