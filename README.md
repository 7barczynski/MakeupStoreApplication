# Face Greases
Makeup store web application

This is simple application that presents, filters and sorts makeup products.

Makeup products data comes from free [Makeup API](http://makeup-api.herokuapp.com/).

Można uruchomić aplikację na 2 sposoby:
1. Odpalając w IDE główną metodę klasy MakeupStoreApplication.java 
2. W konsoli ("Command Propt") dotrzeć do pierwszego, głównego folderu aplikacji ("MakeupStoreApplication") i uruchomić zadanie 'bootRun' na pliku gradlew.bat (u mnie na Windows'ie wygląda to dosłownie: E:\Workspace\InteliJ IDEA\workspace\MakeupStoreApplication>gradlew.bat bootRun). Niestety nie jestem w tych procesach jakoś bardzo doświadczony. Gdy próbowałem odpalić serwer na innej maszynie to konsola poinformowała mnie o braku zainstalowanej javy, więc pewnie jest konieczna również u Ciebie...


Gdy po uruchomieniu w przeglądarce wpsize się URL [localhost:8080](http://localhost:8080/) serwer powinien zwrócić widok strony głównej.
Na tą chwilę dostępne są 4 widoki: Home, About, Shop i single-product.
- About ("/about") - jest na razie niedokończony (ten plik README pełni jedną z jego funkcji). Znajdują się tu linki do mojego GitHub'a, LinkedIn'a (w niedalekiej przyszłości...) oraz ogólna informacja o aplikacji.
- Home ("/") - strona główna na razie nie ma żadnych funkcji oprócz przekierowania zdjęciami do sklepu.
- Shop ("/shop") - ten widok przedstawia produkty ściągnięte z zewnętrznego API wraz z filtrem, którego można użyć do następnych wyszukań. Gdy produktów jest więcej to poniżej listy będzie znajdowała się paginacja. Poniżej przedstawię porady/tłumaczenie jak korzystać z tego widoku najwygodniej.
- single-product ("/{id}") - widok przedstawia wybrany pojedynczy produkt. Przechodzi się do niego po kliknięciu w nazwę produktu w sklepie. Można wtedy zobaczyć dodatkowo opis produktu oraz dostępne kolory (jeżeli są).

Aplikacja jest "wielojęzykowa". W każdym widoku oprócz About, w prawym górnym rogu znajduje się lista języków. Po zmianie języka strona się "odświeża" i wszystkie słowa oprócz nazw i opisów przedmiotów ściągniętych z API są tłumaczone na wybrany język. 

Porady dla korzystania z "wyszukiwarki" produktów (Shop):
- Gdy wejdzie się w niego poprzez czysty link (tzn."localhost:8080/shop") to API zwraca wszystkie produkty jakie ma w bazie. Niestety trwa to zdecydowanie dłużej od wyszukiwań bardziej doprecyzowanych, dlatego zalecam korzystać z filtra, bądź gotowych kategorii w rozwijalnej zakładce SHOP w pasku menu (pasek znajduje się pod logiem).
- Filtr działa dopiero gdy się kliknie przycisk "Filter" poniżej całego panelu. Wtedy następuje kolejne zapytanie do serwera. 
- Niestety, widok listy produktów w sklepie i zdjęcie pojedynczego produktu w widoku single-product są zabugowane. Ściągane z API obrazki produktów makijażowych są różnych rozmiarów i dopracowanie tego, by nie burzyło to zaplanowanego porządku wymaga ode mnie jeszcze trochę nauki i pracy.
- Breadcrumby są na razie "czystą estetyką". Również muszę poświęcić na nie trochę czasu. 

## License
[MIT](https://choosealicense.com/licenses/mit/)