# TrackerApp

Overview
The Delivery Tracker App keeps track of tracking numbers from UPS, USPS and Fedex. Users can create an account and see the packages they are actively tracking with the general status of the package displayed. "Update All" updates all the packages on their current status. Tapping on the package list item allows the User to see/add details pertaining to the delivery such as the description or nickname. Users can also choose to "Share with a Friend". Doing this drafts a text-message with the package information, ready to be sent to someone via the default messaging app. Users can add, update and delete tracking numbers associated with their accounts.

Technical Details
Delivery Tracker is an Android Application, built using Android Studios. To perform its tasks, it calls an API I built specically for it to communicate to a Dynamo Database. The DynamoDB stores the user account and tracking information. The API uses goShippo's tracking API in order to get the current status of a package.

Credits
Icons used are from flaticon.com and icons8.com
