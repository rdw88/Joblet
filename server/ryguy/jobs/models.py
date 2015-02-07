from django.db import models

class Profile(models.Model):
	first_name = models.CharField(max_length=32)
	last_name = models.CharField(max_length=32)
	age = models.IntegerField(default=0)
	tags = models.TextField() # Stored as a JSON object
	bio = models.TextField(default='')
	city_code = models.CharField(max_length=256)
	profile_id = models.CharField(max_length=256)
	date_created = models.DateField()
	password = models.CharField(max_length=32)
	email = models.CharField(max_length=128)
	owned_listings = models.TextField(default='[]') # JSON array of listing ids
	device_id = models.CharField(max_length=256)
	profile_picture = models.TextField(default='profile/pictures/default_profile_picture.png')
	profile_picture_thumbnail = models.TextField(default='profile/pictures/default_profile_picture.png')

	positive_reputation = models.IntegerField(default=0)
	negative_reputation = models.IntegerField(default=0)
	jobs_completed = models.IntegerField(default=0)
	listings_completed = models.IntegerField(default=0)
	recent_jobs = models.TextField(default='[]') # Two most recent jobs currently
	recent_bids = models.TextField(default='[]') # Two most recent bids currently

	class Meta:
		verbose_name = 'profile'
		verbose_name_plural = 'profiles'


class Listing(models.Model):
	job_title = models.CharField(max_length=256)
	job_picture = models.TextField() # JSON object to support multiple pictures
	thumbnail = models.TextField()
	starting_amount = models.FloatField()
	current_bid = models.FloatField()
	last_accepted_bid = models.CharField(max_length=256, null=True, default=None) # bid_id of last accepted bid for this listing.
	min_reputation = models.IntegerField()
	job_description = models.TextField()
	active_until = models.CharField(max_length=128) # Date and time it closes
	profile_id = models.CharField(max_length=256)
	listing_id = models.CharField(max_length=256)
	time_created = models.CharField(max_length=64)

	address = models.CharField(max_length=256)
	city = models.CharField(max_length=128)
	state = models.CharField(max_length=2)
	lat = models.FloatField()
	long = models.FloatField()
	
	status = models.IntegerField() # 0 is active, 1 is inactive, 2 is pending, 3 is completed
	tag = models.CharField(max_length=64)
	owner_reputation = models.FloatField() # store as percent positive reputation?
	owner_name = models.CharField(max_length=256)
	bids = models.TextField() # array of bid ids

	class Meta:
		verbose_name = 'listing'
		verbose_name_plural = 'listings'


class Bid(models.Model):
	bid_id = models.CharField(max_length=256)
	listing_id = models.CharField(max_length=256)
	amount = models.FloatField()
	bidder_email = models.CharField(max_length=128)
	status = models.IntegerField()   # 0 for in progress, 1 for accepted, 2 for declined

	class Meta:
		verbose_name = 'bid'
		verbose_name_plural = 'bids'