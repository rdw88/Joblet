from django.db import models

class Profile(models.Model):
	first_name = models.CharField(max_length=32)
	last_name = models.CharField(max_length=32)
	dob = models.DateField()
	tags = models.TextField() # Stored as a JSON object
	city_code = models.CharField(max_length=16)
	profile_id = models.CharField(max_length=256)
	date_created = models.DateField()
	password = models.CharField(max_length=32)
	email = models.CharField(max_length=128)
	owned_listings = models.TextField() # JSON array of listing ids

	positive_reputation = models.IntegerField()
	negative_reputation = models.IntegerField()
	jobs_completed = models.IntegerField()
	listings_completed = models.IntegerField()

	class Meta:
		verbose_name = 'profile'
		verbose_name_plural = 'profiles'


class Listing(models.Model):
	job_title = models.CharField(max_length=256)
	job_picture = models.TextField() # JSON object to support multiple pictures
	thumbnail = models.TextField()
	starting_amount = models.FloatField()
	current_bid = models.FloatField()
	min_reputation = models.IntegerField()
	job_location = models.CharField(max_length=256)
	job_description = models.TextField()
	active_until = models.CharField(max_length=128) # Date and time it closes
	profile_id = models.CharField(max_length=256)
	listing_id = models.CharField(max_length=256)
	time_created = models.CharField(max_length=64)
	is_active = models.BooleanField(default=False) # Active means this listing is publicly available
	tag = models.CharField(max_length=64)
	owner_reputation = models.FloatField() # store as percent positive reputation?
	owner_name = models.CharField(max_length=256)

	class Meta:
		verbose_name = 'listing'
		verbose_name_plural = 'listings'