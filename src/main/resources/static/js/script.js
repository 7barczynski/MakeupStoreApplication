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

    /*
    * This function is used in locale switcher on the navigation bar.
    */
    function changeLocale() {
        var localeSwitcherValue = document.getElementById("localeSwitcher").value;
        var searchParams = new URLSearchParams(location.search);

        searchParams.set("locale", localeSwitcherValue);

        window.history.replaceState({}, '', `${location.pathname}?${searchParams}`);
        location.replace(`${location.pathname}?${searchParams}`);
    }