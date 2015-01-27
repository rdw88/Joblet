import requests
import json
from models import Listing, Profile, Bid
from error import ERROR_NO_SUCH_LISTING
from helper import send_push_notification

BID_STATUS_UNDETERMINED = 0
BID_STATUS_ACCEPTED = 1
BID_STATUS_DECLINED = 2

PUSH_NOTIFICATION_NEW_BID = 0
PUSH_NOTIFICATION_BID_RESPONSE = 1
PUSH_NOTIFICATION_BID_FINALIZED = 2

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

	bid = Bid(listing_id=listing_id, bid_id=bid_id, bidder_email=bidder_email, amount=bid_amount, bidder_device=bidder_profile.device_id, status=0)
	bid.save()

	listing = listing[0]
	listing_bids = json.loads(listing.bids)
	listing_bids.append(bid_id)
	listing.__dict__['bids'] = json.dumps(listing_bids)
	listing.save()

	owner = Profile.objects.get(profile_id=listing.profile_id)
	owner_device = str(owner.device_id)

	data = {'type' : PUSH_NOTIFICATION_NEW_BID, 'bidder_email' : bidder_email, 'bid_amount' : bid_amount, 'bid_id' : bid_id}
	send_push_notification(owner_device, data)

	return True, None


def accept(args):
	bid = Bid.objects.get(bid_id=args['bid_id'])
	bid.__dict__['status'] = 1
	bid.save()

	listing = Listing.objects.get(listing_id=bid.listing_id)
	listing.__dict__['current_bid'] = bid.amount
	listing.__dict__['last_accepted_bid'] = bid.bid_id
	listing.save()

	data = {'type' : PUSH_NOTIFICATION_BID_RESPONSE, 'listing_id' : bid.listing_id, 'status' : BID_STATUS_ACCEPTED, 'amount' : bid.amount}
	send_push_notification(bid.bidder_device, data)

	return True, None


def decline(args):
	bid = Bid.objects.get(bid_id=args['bid_id'])
	bid.__dict__['status'] = 2
	bid.save()

	data = {'type' : PUSH_NOTIFICATION_BID_RESPONSE, 'listing_id' : bid.listing_id, 'status' : BID_STATUS_DECLINED, 'amount' : bid.amount}
	send_push_notification(bid.bidder_device, data)

	return True, None


def get(args):
	bid_id = args['bid_id']
	bid = Bid.objects.get(bid_id=bid_id)
	return {'status' : bid.status, 'amount' : bid.amount, 'email' : bid.bidder_email, 'bid_id' : bid_id}, None