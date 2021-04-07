# Local Delivery app with realtime location tracking

I wanted to emulate something what Swiggy / Zomato / GrubHub would do, so I made this project with 3 sub apps for the User, Shop and the Delivery Executive such that all basic functionalities are covered for any Delivery app startup

The name TUYU and Water360 was my hypothetical startup's initial name, so it would be present throughout the project, you can Ignore and refactor as you would like
Google Maps API key and Firebase API key has to be replaced by the forker (please do not use my api keys if your use is professional and profit oriented)

The code is open source and can be used freely

Tech stack Used: Google Android SDK, Firebase, Google maps API

This project was SOLELY made By Abbilashh Senthilkumar @abilashsenth / @thenextbiggeek


# User app (TUYU)

User app has following features:
- Use Geofencing and list shops within given mile radius
- Display the products by the shops in listed order
- maintain proper user profile with necessary information
- send user location data for easy delivery
- clean user interface
- ability to give ratings for the product and delivery personnel
- payment protocol (currently its a swipe to pay on cash system)

# Shop app (TUYU Main)

Shop app has following features:
- find the delivery partners (logged in) realtime in maps
- get the list of orders placed for this shop
- login page
- ability to assign orders to specific delivery partners
- Much more flexibility
- Look at customer ratings for the product as well as delivery partner
- history of their previous completed orders

# Delivery Partner app (TUYU Prtner)

Delivery partner app has following features:
- Log in capability
- get the list of orders assigned to this particular delivery partner
- share realtime location data to the shop
- share realtime location data to the customer once the shop assigns an order
- ability to find customer location only after order is assigned for navigation in google maps
- flexibility to log out
- history of their previous completed orders
- rate the customer experience

UI is still not perfect for a delivery app, but its minimal effort, since the entire system is built from scratch and is working perfectly
