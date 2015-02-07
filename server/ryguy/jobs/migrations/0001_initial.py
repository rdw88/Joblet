# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='Bid',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('bid_id', models.CharField(max_length=256)),
                ('listing_id', models.CharField(max_length=256)),
                ('amount', models.FloatField()),
                ('bidder_email', models.CharField(max_length=128)),
                ('bidder_device', models.CharField(max_length=256)),
                ('status', models.IntegerField()),
            ],
            options={
                'verbose_name': 'bid',
                'verbose_name_plural': 'bids',
            },
            bases=(models.Model,),
        ),
        migrations.CreateModel(
            name='Listing',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('job_title', models.CharField(max_length=256)),
                ('job_picture', models.TextField()),
                ('thumbnail', models.TextField()),
                ('starting_amount', models.FloatField()),
                ('current_bid', models.FloatField()),
                ('last_accepted_bid', models.CharField(default=None, max_length=256, null=True)),
                ('min_reputation', models.IntegerField()),
                ('job_description', models.TextField()),
                ('active_until', models.CharField(max_length=128)),
                ('profile_id', models.CharField(max_length=256)),
                ('listing_id', models.CharField(max_length=256)),
                ('time_created', models.CharField(max_length=64)),
                ('address', models.CharField(max_length=256)),
                ('city', models.CharField(max_length=128)),
                ('state', models.CharField(max_length=2)),
                ('lat', models.FloatField()),
                ('long', models.FloatField()),
                ('status', models.IntegerField()),
                ('tag', models.CharField(max_length=64)),
                ('owner_reputation', models.FloatField()),
                ('owner_name', models.CharField(max_length=256)),
                ('bids', models.TextField()),
            ],
            options={
                'verbose_name': 'listing',
                'verbose_name_plural': 'listings',
            },
            bases=(models.Model,),
        ),
        migrations.CreateModel(
            name='Profile',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('first_name', models.CharField(max_length=32)),
                ('last_name', models.CharField(max_length=32)),
                ('dob', models.DateField()),
                ('tags', models.TextField()),
                ('city_code', models.CharField(max_length=16)),
                ('profile_id', models.CharField(max_length=256)),
                ('date_created', models.DateField()),
                ('password', models.CharField(max_length=32)),
                ('email', models.CharField(max_length=128)),
                ('owned_listings', models.TextField()),
                ('device_id', models.CharField(max_length=256)),
                ('positive_reputation', models.IntegerField()),
                ('negative_reputation', models.IntegerField()),
                ('jobs_completed', models.IntegerField()),
                ('listings_completed', models.IntegerField()),
            ],
            options={
                'verbose_name': 'profile',
                'verbose_name_plural': 'profiles',
            },
            bases=(models.Model,),
        ),
    ]
