# Face Greases
Makeup store web application

This is simple application that presents and filters makeup products.

Makeup products data comes from free [Makeup API](http://makeup-api.herokuapp.com/).

### Setup
You can run application in two ways:
1. by running main method in MakeupStoreApplication.java in your IDE,
2. by reaching in the Command Prompt main directory of this application ("MakeupStoreAppliaction") and running task 'bootRun' on a 'gradlew.bat' file.

Application needs Java to run. I use Java 11 and this is the version I recommend.   
Also you have to be online to use application because almost every request to the server downloads data from external API.

### How to 
Application uses embeded Tomcat and its port is 8080. At the moment there are available four views:
- Home ([localhost:8080](http://localhost:8080/)) - home page,
- About ([localhost:8080/about](http://localhost:8080/about)) - there are some links to my social media,
- Shop ([localhost:8080/shop](http://localhost:8080/shop)) - this view presents makeup products downloaded from external API and a filter which helps to make serching more specific. When there are more than 12 (default value) products to show there should be pagination on the bottom of the list,
- Single product ([localhost:8080/shop/{id}](http://localhost:8080/shop/1)) - this view presents details about one product. You can reach this view by clicking an image of the desired product on a shop view.

You can also change the language of the application interface by choosing desired language from the list in the top right corner of the page. At the moment there are two languages - english and polish.

Little advice on searching in the shop view:
- "pure", parameterless request (http://localhost:8080/shop) downloads all products from the external API. This last longer than more specific requests and thats why I recommend to use filter and categories from dropdown list on the main menu.

If you encounter any problem I'm more than happy to contact you on this.

Thank you for your time to read this! Hope the code is satisfying!
## License
[MIT](https://choosealicense.com/licenses/mit/)