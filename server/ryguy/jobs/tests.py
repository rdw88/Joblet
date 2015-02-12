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
		'age':'21', 'tags':'Programming', 'city_code':'San Francisco, CA', 'profile_id':'349384', 
		'date_created': '1-18-2015', 'request':'create', 'bio': 'This is my random bio.'}

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
	def __init__(self, arg):
		super(ListingTests, self).__init__(arg)
		self.test_profile = {'email':'ryan@gmail.com', 'first_name':'Ryan', 'last_name':'Wise', 'password':'password', 
					'age': 21, 'tags':'Programming', 'city_code':'San Francisco, CA', 'profile_id':'349384', 
					'date_created': '1-18-2015', 'request':'create', 'bio' : 'this is my random bio.'}

		self.test_listing = {'password':'password',
		'job_title' : 'random job', 'starting_amount' : 50, 'current_bid': 50, 'min_reputation':50,
		'active_until':'1-23-2015 5:20PM', 'tag' : 'Programming', 'address' : '1234 h ln', 'city':'cityname',
		'state':'CA', 'latitude' : 45, 'longitude': 55, 'job_description' : 'Random job description'}

	def test_listing_creation(self):
		profile.create(self.test_profile)
		prof_id = Profile.objects.get(email=self.test_profile['email']).profile_id
		self.test_listing['profile_id'] = prof_id
		response, error = listing.create(self.test_listing)	

		self.assertEqual(error, None)

		self.listing_id = response['listing_id']


	def test_get_listing(self):
		self.test_listing_creation()
		response, error = listing.get({'listing_id' : self.listing_id})
		self.assertEqual(error, None)
		
		for key in response:
			if key in self.test_listing:
				self.assertEqual(self.test_listing[key], response[key])