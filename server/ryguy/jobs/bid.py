import requests
import json
import locale
from models import Listing, Profile, Bid
from error import ERROR_NO_SUCH_LISTING, ERROR_NO_SUCH_PROFILE
import notification

BID_STATUS_UNDETERMINED = 0
BID_STATUS_ACCEPTED = 1
BID_STATUS_DECLINED = 2

NUM_RECENT_BIDS = 2
NUM_RECENT_JOBS = 2

'''

When a user makes a bid to a listing, it gets sent here. This will notify
the owner of the listing and the owner can choose whether to accept or decline
the bid.

'''
def make_bid(args):
	listing_id = args['listing_id']
	bidder_email = args['bidder_email']
	bid_amount = args['bid_amount']

	listing = Listing.objects.filter(listing_id=listing_id)
	bidder_profile = Profile.objects.get(email=bidder_email)

	if len(listing) == 0:
		return False, ERROR_NO_SUCH_LISTING

	bids = Bid.objects.filter(listing_id=listing_id)
	bid_id = str(listing_id) + str(len(bids))

	bid = Bid(listing_id=listing_id, bid_id=bid_id, bidder_email=bidder_email, amount=bid_amount, status=0)
	bid.save()

	recent_bids = json.loads(str(bidder_profile.recent_bids))

	if len(recent_bids) == NUM_RECENT_BIDS:
		del recent_bids[0]

	recent_bids.append(listing_id)
	bidder_profile.__dict__['recent_bids'] = json.dumps(recent_bids)
	bidder_profile.save()

	listing = listing[0]
	listing_bids = json.loads(listing.bids)
	listing_bids.append(bid_id)
	listing.__dict__['bids'] = json.dumps(listing_bids)
	listing.save()

	owner = Profile.objects.get(profile_id=listing.profile_id)
	locale.setlocale(locale.LC_ALL, '')
	notification_title = 'Someone made a bid!'
	notification_description = '%s made a bid of %s!' % (bidder_email, locale.currency(float(bid_amount)))
	extras = {'bid_id' : bid_id}

	notification.create(notification_title, notification_description, owner.email, owner.password, extras)

	return True, None


def accept(args):
	bid = Bid.objects.get(bid_id=args['bid_id'])
	bid.__dict__['status'] = 1
	bid.save()
	bidder_profile = None

	try:
		bidder_profile = Profile.objects.get(email=bid.bidder_email)
	except Profile.DoesNotExist:
		return False, ERROR_NO_SUCH_PROFILE

	recent_jobs = json.loads(bidder_profile.recent_jobs)

	if len(recent_jobs) == NUM_RECENT_JOBS:
		del recent_jobs[0]

	recent_jobs.append(bid.listing_id)
	bidder_profile.__dict__['recent_jobs'] = json.dumps(recent_jobs)
	bidder_profile.save()

	listing = Listing.objects.get(listing_id=bid.listing_id)
	listing.__dict__['current_bid'] = bid.amount
	listing.__dict__['last_accepted_bid'] = bid.bid_id
	listing.save()

	locale.setlocale(locale.LC_ALL, '')
	notification_title = 'Bid Accepted!'
	notification_description = 'Your bid of %s for %s was accepted!' % (locale.currency(float(bid.amount)), listing.job_title)

	notification.create(notification_title, notification_description, bidder_profile.email, bidder_profile.password, None)

	return True, None


def decline(args):
	bid = Bid.objects.get(bid_id=args['bid_id'])
	bid.__dict__['status'] = 2
	bid.save()
	bidder_profile = None

	try:
		bidder_profile = Profile.objects.get(email=bid.bidder_email)
	except Profile.DoesNotExist:
		return False, ERROR_NO_SUCH_PROFILE

	listing = Listing.objects.get(listing_id=bid.listing_id)

	locale.setlocale(locale.LC_ALL, '')
	notification_title = 'Bid Declined'
	notification_description = 'Your bid of %s for %s was declined.' % (locale.currency(float(bid.amount)), listing.job_title)
	
	notification.create(notification_title, notification_description, bidder_profile.email, bidder_profile.password, None)

	return True, None


def get(args):
	bid_id = args['bid_id']
	bid = Bid.objects.get(bid_id=bid_id)
	return {'status' : bid.status, 'amount' : bid.amount, 'email' : bid.bidder_email, 'bid_id' : bid_id}, None