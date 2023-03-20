https://xd.adobe.com/view/38753b94-7296-413a-8282-7e05cb18a23a-6d26/grid
https://img.ly/

****M8**** All Settings
Kaveri
1-> Profile -> Get And Edit, Screen no 37-40
2-> Account -> Privacy, Change Number, Change Password, Blocked contacts, Delete my accounts, Screen
no 345
3-> Chat -> Screen no 319
4-> App General Setting -> Screen no 359
5-> Notification Setting -> Screen no 321
6-> About Us-> Screen no 360
7-> Legal-> Screen no 365
8-> Quite Mode-> Screen no 53
9-> Saved/Favourite -> Screen no 68
Localisation in Arabic only
//----------
@Aman Kumar Singh @Yogesh for M8 we need to work on below points
Profile:-Edit profile
Account setting:- In Privacy we need to do all points except (Story, Groups, Read Receipts, Last
seen)
App Setting:-Do all points except Rate App
Notification Setting:- Only do funtime part
About us
Legal
//-------------

1:- Thumbnail Image Property (Add new thumbnail UI and Funtionality for video)
2:- Sound Mixer Functionallity ( New Sound Mixer functiuonality add on)
3:- Add local music Functionality (Add new local music functionality user can pick music from local
storage and apply on video or image)
4:- Save As draft ui And functionality (Save as draft New UI and its funtionality)
5:- Filter name and its functionality
6:- Add download with watermark, block, comment on off funtionality
7:- user not able to post multiple image and multiple Video in funtime post  (They wants after
discussion user can create a post from multiple image or multiple video )
8:- Add Hastag funtionality on post screen
9:- When user can record 90 sec video and video length should be between 15 sec to 90 sec
Functionality.   (Add 90 sec funtionality user can record video 90 sec and add a validation to video
length shoulr be between 15 sec to 90 sec)
10:- By using this feature, User can change or modify your video into reverse order, flash order and
also in slow motion.

//----------------------------------------------------------------
https://api.postman.com/collections/10964628-49cbee9a-f1d4-4360-8fda-6d529567eae5?access_key=PMAT-01GTEAKGQ0WB67CD0DD3R7W71K

1:- Thumbnail Image Property (Add new thumbnail UI and Funtionality for video)
Note-> cover image key is not present in this api while creating reel
API -> auth_service/funtime_api/add_funtime
for point 1 key name => cover_image

4:- Save As draft ui And functionality (Save as draft New UI and its funtionality)
Note-> Provide API for saving as Draft
{{url}}/funtime_api/add_draft

5:- Filter name and its functionality

6:- comment on off funtionality for reels
Note-> Provide Api for comment on off
{{url}}/funtime_api/add_comment_disable

8:- Add Hastag funtionality on post screen
Note-> Provide API for search hashtag list
If already working in iOS then provide api