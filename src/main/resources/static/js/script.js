    /*
    * Used on filter form in shop view.
    */
    function disableEmptyInputs(form) {
        var elements = form.elements;
        disableEmptyElements(elements);
    }

    function disableEmptyElements(elements) {
        for (var i=0; i<elements.length; i++) {
            elements[i].disabled = elements[i].value === "" || elements[i].value === "-" || elements[i].value === "0";
        }
    }

    /*
    * Used on body in shop view.
    */
    function resetFilterFormOnUnload() {
        var form = document.getElementById("filter");
        form.reset();
    }

    /*
    * Used in shop's view script tag.
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

    /*
    * Used in locale switchers.
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
