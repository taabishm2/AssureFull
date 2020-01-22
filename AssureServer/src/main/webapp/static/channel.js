function getChannelApiUrl() {
    var baseUrl = $("meta[name=baseUrl]").attr("content")
    return baseUrl + "/api/channel";
}

function addChannel(event) {
    var $form = $("#channel-form");
    var json = toJson($form);
    var url = getChannelApiUrl();

    $.ajax({
        url: url,
        type: 'POST',
        data: json,
        headers: {
            'Content-Type': 'application/json'
        },
        success: function(response) {
            getChannelList();
            $('#exampleModal').modal('toggle');
            getSuccessSnackbar("Success");
        },
        error: handleAjaxError
    });
    return false;
}

function getChannelList() {
    var url = getChannelApiUrl();
    $.ajax({
        url: url,
        type: 'GET',
        success: function(data) {
            displayChannelList(data);
        },
        error: handleAjaxError
    });
}

function displayChannelList(data) {
    var $tbody = $('#channel-table').find('tbody');
    $tbody.empty();

    if (data.length == 0) {
        var row = '<tr>' +
            '<td style="text-align:center; font-weight: bold; background-color:#ffebe8;" colspan="3">No Channels Created</td>' +
            '</tr>';
        $tbody.append(row);
    }

    for (var i in data) {
        var e = data[i];
        var row = '<tr>' +
            '<td style="text-align:center; font-weight: bold;">' + e.id + '</td>' +
            '<td>' + e.name + '</td>' +
            '<td>' + e.invoiceType + '</td>' +
            '</tr>';
        $tbody.append(row);
    }
}

function init() {
    $('#channel-form').submit(addChannel);
    $('#refresh-data').click(getChannelList);
}

$(document).ready(init);
$(document).ready(getChannelList);