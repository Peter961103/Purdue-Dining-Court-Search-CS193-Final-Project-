# Purdue Dining Court search

## Project Goal
Checking multiple Purdue dining court websites every day to see what food is being served can be inconvenient.
This project aims to make it easier by fetching daily menu data from all campus dining courts, caching it locally, and allowing users to instantly search for specific food items with keywords.

## MVP Scope 
The Minimum Viable Product includes:
- **API Data Fetching:** Automatically pulls the daily menu JSON payload from Purdue dining endpoints using Java's `HttpClient`.
- **Local Caching:** Stores raw menu data locally to support offline testing and avoid redundant network requests.
- **Data Parsing:** Deserializes JSON responses into structured Java objects.
- **Keyword Search Engine:** Scans all loaded dining court items based on user input and outputs matching dishes and their respective locations.

## Future Plans (Version 2.0+)
*These features are scheduled for future versions:*
- **Personalized Recommendations:** Suggest dining courts based on user preferences and past favorite foods.
- **Automated Notifications:** Alert users when specific favorite dishes are being served.
- **Dietary & Allergy Filtering:** Filter menus for vegetarian, vegan, gluten-free, and allergen restrictions.

## Tech Stack
- **Language:** Java 
- **Networking:** `java.net.http.HttpClient`
- **JSON Parsing:** Gson or Jackson
- **Data Storage:** Local JSON file caching 
- **Version Control:** Git & GitHub
