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
        var switcherValue = document.getElementById(switcherId).value;
        var searchParams = getSearchParamsWith(location.search, "locale", switcherValue);
        replaceUrlWith(searchParams);
    }

    function getSearchParamsWith(searchString, paramName, paramValue) {
        var searchParams = new URLSearchParams(searchString);
        searchParams.set(paramName, paramValue);
        return searchParams;
    }

    function replaceUrlWith(searchParams) {
        url = `${location.pathname}?${searchParams}`;
        window.history.replaceState({}, '', url);
        location.replace(url);
    }

    /*
    * Used in shop's view sort select
    */
    function sendRequestForSortOption() {
        var sortValue = document.getElementById("sortOptionsList").value;
        var searchParams = getSearchParamsWithForSort(location.search, "sort", sortValue);
        assignUrlWith(searchParams);
    }

    function getSearchParamsWithForSort(searchString, paramName, paramValue) {
        if (isParamValueValid(paramValue)) {
            return getSearchParamsWith(searchString, paramName, paramValue);
        }
        return getSearchParamsWithout(searchString, paramName);
    }

    function isParamValueValid(paramValue) {
        return paramValue != "-";
    }

    function getSearchParamsWithout(searchString, paramName) {
        var searchParams = new URLSearchParams(searchString);
        searchParams.delete(paramName);
        return searchParams;
    }

    function assignUrlWith(searchParams) {
        url = `${location.pathname}?${searchParams}`;
        location.assign(url);
    }

    /*
    * Used in shop's view size select
    */
    function sendRequestForSizeOption() {
        var sizeValue = document.getElementById("sizeOptionsList").value;
        var searchParams = getSearchParamsWithForSize(location.search, "size", sizeValue, "page");
        assignUrlWith(searchParams);
    }

    function getSearchParamsWithForSize(searchString, paramName, paramValue, paramNameForDelete) {
        var searchParams = getSearchParamsWith(searchString, paramName, paramValue);
        searchParams.delete(paramNameForDelete);
        return searchParams;
    }