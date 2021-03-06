# Face Greases
Application for browsing make-up products. It presents, filters and sorts them.

You can view an already deployed application [here](https://face-greases.herokuapp.com).

Make-up data comes from free [Makeup API](http://makeup-api.herokuapp.com/).

### Setup
Application needs Java (recomended Java 11) and PostgreSQL installed to run.

Firstly, if you want to run the app locally you should change these values of database access properties in <i>application.properties</i> or create own environment variables on your system or IDE.  
``` properties
# postgresql
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USER}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
```

Then you can run application in two ways:
1. by running main method in MakeupStoreApplication.java in your IDE,
2. by running command 'bootRun' on a 'gradlew.bat' file on Windows or running './gradlew bootRun' on Linux and MacOS in application's main directory. 

### How to 
These are all views that make up the application:
- Home ([localhost:8080](http://localhost:8080/)) - home page with some information about project and contact links,
- Shop ([localhost:8080/shop](http://localhost:8080/shop)) - In this view you can filter and sort make-up products,
- Detailed product view ([localhost:8080/shop/{id}](http://localhost:8080/shop/1)) - this view presents details about one product.

You can also change the language of the application interface by choosing desired language from the list in the top right corner of the page.

If you encounter any problem I'm more than happy to contact you on this.

Thank you for your time to read this! Hope the code is clear and simple to you!

## Attribution
Icons made by [Freepik](https://www.flaticon.com/authors/freepik) and [Dave Gandy](https://www.flaticon.com/authors/dave-gandy) from [www.flaticon.com](https://www.flaticon.com/)

## License
[MIT](https://choosealicense.com/licenses/mit/)