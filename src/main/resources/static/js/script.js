    /*
    * This function is used on filter panel in shop view.
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
    function changeLocale(switcherId) {
        newUrl = setOrAddLocaleParamInCurrentUrl(switcherId);
        replaceLocationAndHistoryState(newUrl);
    }

    function setOrAddLocaleParamInCurrentUrl(switcherId) {
        var localeSwitcherValue = document.getElementById(switcherId).value;
        var searchParams = new URLSearchParams(location.search);

        searchParams.set("locale", localeSwitcherValue);

        return `${location.pathname}?${searchParams}`;
    }

    function replaceLocationAndHistoryState(url) {
        window.history.replaceState({}, '', url);
        location.replace(url);
    }

    /*
    * This function is used only in shop view
    */
    function toggleFilterVisibilityOnWidthBreakpoint(mediaWidth) {
        var filter = document.getElementById("collapseFilter");
        if (mediaWidth.matches) {
            showIfHidden(filter);
        } else {
            hideIfPresent(filter);
        }
    }

    function showIfHidden(element) {
        if (isHidden(element)) {
            element.classList.add("show");
        }
    }

    function isHidden(element) {
        return !element.classList.contains("show");
     }

     function hideIfPresent(element) {
        if (!isHidden(element)) {
            element.classList.remove("show");
        }
     }
