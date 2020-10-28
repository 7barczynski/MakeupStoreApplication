    /*
    * This function disable empty elements in a form to clear URL from unused parameters.
    * It is used on filter panel in shop view.
    */
    function disableEmptyInputs(form) {
        var elements = form.elements;
        disableEmptyElements(elements);
    }

    function disableEmptyElements(elements) {
        for (var i=0; i<elements.length; i++) {
            elements[i].disabled = elements[i].value == "" || elements[i].value == "-";
        }
    }

    /*
    * This function is used in locale switcher on the navigation bar.
    */
    function changeLocale() {
        newUrl = setOrAddLocaleParamInCurrentUrl();
        replaceLocationAndHistoryState(newUrl);
    }

    function setOrAddLocaleParamInCurrentUrl() {
        var localeSwitcherValue = document.getElementById("localeSwitcher").value;
        var searchParams = new URLSearchParams(location.search);

        searchParams.set("locale", localeSwitcherValue);

        return `${location.pathname}?${searchParams}`;
    }

    function replaceLocationAndHistoryState(url) {
        window.history.replaceState({}, '', url);
        location.replace(url);
    }

