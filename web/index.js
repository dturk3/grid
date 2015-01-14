var geocoder = new google.maps.Geocoder();
var coords;
var map;

var computeCoords = function () {
    geocoder.geocode({
        'address': $('#planner-location').val()
    }, function (results, status) {
        if (status == google.maps.GeocoderStatus.OK) {
            if (results[0]) {
                var coords = results[0].geometry.location;
                window.name = coords;
                $('#coords').val(coords.k + ',' + coords.B);
                return coords;
            }
        }
    })
};

function initialize() {
    var chicago = new google.maps.LatLng(41.850033, -87.6500523);
    var mapOptions = {
        zoom: 2,
        center: chicago,
        mapTypeControl: false,
        streetViewControl: false,
        draggable: false,
        keyboardShortcuts: false,
        panControl: false,
        rotateControl: false,
        scaleControl: false,
        zoomControl: false,
        disableDoubleClickZoom: false,
        scrollwheel: false
    }
    map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);
    $("#map-canvas").css({
        width: '75px',
        height: '75px'
    });
    window.handle = localStorage.getItem('handle');
    if (window.handle != null) {
        $('#chhandle').val(window.handle);
    }
    geoLocate();
    console.log(map);

    google.maps.event.trigger(map, "resize");
    map.setZoom(15);
}

google.maps.event.addDomListener(window, 'load', initialize);


function createCORSRequest(method, url) {
    var xhr = new XMLHttpRequest();
    if ("withCredentials" in xhr) {
        xhr.open(method, url, true);
    } else if (typeof XDomainRequest != "undefined") {
        xhr = new XDomainRequest();
        xhr.open(method, url);

    } else {
        return null;

    }
    return xhr;
}

function refreshFeed() {
    var xhr = createCORSRequest('POST', '/feeds');

    xhr.onload = function () {
        var text = xhr.responseText;
        var feedObject = JSON.parse(text);
        feedObject.feed.reverse();
        $('#feed').html('');
        feedObject.feed.forEach(function (entry) {
            entry = JSON.parse(entry);
            var feedHtml = $('#feedtemplate').html();
            feedHtml = feedHtml.replace('{m}', entry.message);
            feedHtml = feedHtml.replace('{t}', entry.fuzzyTimestamp);
            feedHtml = feedHtml.replace('{p}', entry.publisher);
            $('#feed').append(feedHtml);
        });
    };

    xhr.onerror = function () {
        $('#feed').html('<div class="error">f*ck, error</div>');
    };

    xhr.send(JSON.stringify({
        lon: (Math.round(coords.k * 1000000) / 1000000),
        lat: (Math.round(coords.D * 1000000) / 1000000),
    }));
}

function sendMessage() {
    if ($('.entry').val().length <= 0 || $('.entry').val().length > 250) {

        return;
    }
    var xhr = createCORSRequest('POST', '/messages');

    xhr.send(JSON.stringify({
        lon: (Math.round(coords.k * 1000000) / 1000000),
        lat: (Math.round(coords.D * 1000000) / 1000000),
        message: $('.entry').val(),
        publisher: window.handle
    }));
    $('.entry').val('');
}

function geoLocate() {
    if ((navigator.geolocation)) {
        navigator.geolocation.getCurrentPosition(function (position) {
                var pos = new google.maps.LatLng(position.coords.latitude,
                    position.coords.longitude);
                geocoder.geocode({
                    'latLng': pos
                }, function (results, status) {
                    if (status == google.maps.GeocoderStatus.OK) {
                        if (results[1]) {
                            console.log(results[1]);
                            var component = results[1].address_components[0];
                            if (results[1].address_components.length > 1) {
                                component = results[1].address_components[1];
                            }
                            $('#location').html(component.long_name);
                        }
                    }
                });
                window.name = pos;
                coords = pos || computeCoords();
                console.log(coords);
                $('#coords').html(coords.k + ',' + coords.D);
                map.setCenter(new google.maps.LatLng(coords.k,
                    coords.D), 13);
                console.log($('.submitbtn'));
                $('.submitbtn').click(sendMessage);
                $('.entry').keyup(function (e) {
                    if (e.keyCode == 13) {
                        sendMessage();
                    }
                });

                $('#chhandle').popBox({
                    height: 25
                });


                if (window.handle == null) {
                    var xhr = createCORSRequest('POST', '/names');
                    xhr.send();
                    xhr.onload = function () {
                        var text = xhr.responseText;
                        var nameObject = JSON.parse(text);
                        window.handle = nameObject.name;
                        localStorage.setItem('handle', window.handle);
                        $('#chhandle').val(window.handle);
                    };
                }




                (function poll() {
                    setTimeout(function () {
                        refreshFeed();
                        poll();
                    }, 3000);
                })();

            },
            function () {});
    }
}