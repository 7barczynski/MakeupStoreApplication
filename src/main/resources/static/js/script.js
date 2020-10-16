    /*
    * This onload function checks current language and sets proper selection in the "language switcher" <select> tag.
    * It uses variable "currentLanguage" and "defaultLanguage" which get current and default language code from model attribute
    * and was initialized in corresponding html file.
    *
    * To prevent possible bugs - if "currentLanguage" variable is null then page redirects to default language.
    */
    window.onload = function() {
        var options = document.getElementById("languageSwitcher").options;
        var optionToSelect = options.namedItem(currentLanguage);
        if (optionToSelect != null) {
            optionToSelect.selected = true;
        } else {
            console.log("Cannot find option in the languageSwitcher with the id = [" + currentLanguage + "]. Ignoring problem and setting default language.");
            options.namedItem(defaultLanguage).selected = true;
            changeLanguage();
        }
    };

    /*
    * This function changes language by adding to url propper parameter and by this calling Spring's SessionLocaleResolver.
    * It is called whenever client changes option in "language switcher" <select> tag
    */
    function changeLanguage() {
        var LOCALE = "locale=";
        var selectedValue = document.getElementById("languageSwitcher").value;
        var url =  document.URL;

        // if there is already "locale=**" in url replace the part after "="
        if (url.includes("locale=")) {
            replacedUrl = url.replace(/locale=[^&#]+/, LOCALE + selectedValue);
            location.replace(replacedUrl);
        // if there isn't "locale=**" in url but it has other parameters (?)
        } else if (url.includes("?")) {
            urlFragment = ""
            if (url.includes("#")) {
                urlFragment = url.substring(url.indexOf("#"), url.length);
                url = url.substring(0, url.indexOf("#"));
            }
            location.replace(url + "&" + LOCALE + selectedValue + urlFragment);
        // if there is no query parameters but a fragment (#)
        } else if (url.includes("#")) {
            urlFragment = url.substring(url.indexOf("#"), url.length);
            urlWithoutFragment = url.substring(0, url.indexOf("#"));
            location.replace(urlWithoutFragment + "?" + LOCALE + selectedValue + urlFragment);
        // if the url hasn't got any parameters
        } else location.replace(url + "?" + LOCALE + selectedValue);
    }

    /*
    * This function disable empty inputs in a form to clear URL from unused parameters.
    * It is used on filter panel in shop view.
    */
    function disableEmptyInputs(form) {
      var controls = form.elements;
      for (var i=0; i<controls.length; i++) {
        controls[i].disabled = controls[i].value == "" || controls[i].value == "-";
      }
    }