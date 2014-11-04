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

	positive_reputation = models.IntegerField()
	negative_reputation = models.IntegerField()
	jobs_completed = models.IntegerField()
	listings_completed = models.IntegerField()


	class Meta:
		verbose_name = 'profile'
		verbose_name_plural = 'profiles'


class Listing(models.Model):
	job_title = models.CharField(max_length=256)
	job_picture = models.CharField(max_length=256)
	starting_amount = models.FloatField()
	current_bid = models.FloatField()
	min_reputation = models.IntegerField()
	job_location = models.CharField(max_length=256)
	job_description = models.TextField()
	active_time = models.IntegerField() # Stored in minutes.
	profile_id = models.CharField(max_length=256)
	listing_id = models.CharField(max_length=256)
	time_created = models.CharField(max_length=64)
	is_active = models.BooleanField(default=False) # Active means this listing is publicly available
	tags = models.TextField() # Stored as a JSON object

	class Meta:
		verbose_name = 'listing'
		verbose_name_plural = 'listings'