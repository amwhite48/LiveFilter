Original App Design Project 
===

# Live Photo Filters

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
This app allows the user to apply a filter to the camera while taking a photo and then adjust that filter. The user can then save the filter they created for later use.


### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Photography, social
- **Mobile:** Allows user to adjust filters on a photo as they take a picture, as well as browse other filters and apply them as they use their phone camera. This app gives users the ability to edit and adjust their photos before they take them and save the filter they created to use later.
- **Story:** Users can adjust the appearance of their photos before they take them, and then save this adjustment to apply it again when they want a similar effect. Users can also browse new effects to try out on their photos. Gives user more power to design the effects they apply and the flexibility to try them out with the camera rather than editing effects like this afterwards.
- **Market:** Any users who take photos and want to customize them can use this app. 
- **Habit:** If a user wanted to take a photo with a particular effect they can't find elsewhere, they can design it and continue using it when they want. Users can adjust the camera as they like and also discover new filters to use.
- **Scope:** The first version of the app allows the users to apply preselected filters. The next iteration would allow them to design their filters in the app while they're using the camera. Another iteration allows sharing and saving these filters. A possible future iteration could include applying filters to photos from the camera roll.

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* Take a picture in the app
* Apply a filter to the camera in real time
* Adjust the filter to the camera in real time
* Save the picture taken to the camera roll
* A user can sign in, log in, and log out of the app
* Be able to share user-generated filters
* User can view a feed of filters generated by other users

**Optional Nice-to-have Stories**

* Be able to save other users' generated filters
* Share pictures taken in app to other apps
* Log in with Facebook
* Apply filters to photos in camera roll
* Allow photo overlays and other additions to filters

### 2. Screen Archetypes

* Login screen
   * user can login to their account
* Signup screen
   * user can create a new account
* Feed
   * view filters generated by self and other users
   * save filters to own library
   * select filter to take a picture with camera and corresponding filter
* Camera
  * user can take a picture and save it to their camera roll
  * user can adjust the filter on the camera in real time
  * user can save filter after taking a picture
* Your filters
    * user can view filters they've saved 

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Feed of filters
* Camera
* Your filters

**Flow Navigation** (Screen to Screen)

* Login screen
   * Home (feed)
   * Signup
* Signup
   * Home (feed)
* Home (feed)
    * Camera
    * Saved filters
* Saved filters
    * Camera
    * Feed

## Wireframes
[Add picture of your hand sketched wireframes in this section]
<img src="YOUR_WIREFRAME_IMAGE_URL" width=600>

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 
[This section will be completed in Unit 9]
### Models
[Add table of models]
### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]

References:
https://stackoverflow.com/questions/33892915/how-to-add-real-time-filtering-effects-in-camera-2-api-in-android
https://developer.android.com/reference/android/hardware/camera2/CaptureRequest.html#CONTROL_EFFECT_MODE
