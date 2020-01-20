function getChannelListingUrl() {
    var baseUrl = $("meta[name=baseUrl]").attr("content")
    return baseUrl + "/api/channelListing";
}

function getChannelNamesUrl() {
    var baseUrl = $("meta[name=baseUrl]").attr("content")
    return baseUrl + "/api/channel";
}

function getClientNamesUrl() {
    var baseUrl = $("meta[name=baseUrl]").attr("content")
    return baseUrl + "/api/consumer/clients";
}

function getChannelListingList() {
    var url = getChannelListingUrl();
    $.ajax({
        url: url,
        type: 'GET',
        success: function(data) {
            displayChannelListingList(data);
        },
        error: handleAjaxError
    });
}

// FILE UPLOAD METHODS
var fileData = [];
var errorData = [];
var processCount = 0;
var channelId;

var addListRequestBody = [];

function processData() {
    var file = $('#channelListingFile')[0].files[0];
    readFileData(file, readFileDataCallback);
    return false;
}

function readFileDataCallback(results) {
    fileData = results.data;
    uploadRows();
}

function uploadRows() {
    var formList = [];
    var processCount;
    for (processCount = 0; processCount < fileData.length; processCount++) {
        formList.push(fileData[processCount]);
    }

    validateCsv(formList);
}

function validateCsv(formList) {
    var $form = $("#channelListing-form");
    var channelJson = toJson($form);

    var obj = JSON.parse(channelJson);
    channelId = obj["channelId"];
    clientId = obj["clientId"];

    var url = getChannelListingUrl() + "/validate/" + channelId + "/" + clientId;

    console.log(json);
    var json = JSON.stringify(formList);

    //Make ajax call
    $.ajax({
        url: url,
        type: 'POST',
        data: json,
        headers: {
            'Content-Type': 'application/json'
        },
        success: function(response) {
            uploadChannelListing(formList, channelId, clientId);
        },
        error: function(response) {
            sessionStorage.setItem("ChannelListingCSVError", JSON.stringify(JSON.parse(response.responseText)['message']));
            errorButtonActivate();
            alert("Errors in CSV");
        }
    });
    return false;
}

function errorButtonActivate() {
    document.getElementById("download-errors").style.visibility = "visible";
    document.getElementById("download-errors").disabled = false;
}

function displayProductCsvErrors() {
    document.getElementById('errorDetails').innerHTML = sessionStorage.getItem("ChannelListingCSVError");
    document.getElementById("errorDetails").style.display = "block";
    return false;
}

function resetModal() {
    sessionStorage.setItem("ProductCSVError", '');
    document.getElementById("errorDetails").style.display = "none";
    document.getElementById("download-errors").disabled = true;
    document.getElementById("download-errors").style.visibility = "hidden";
}

function uploadChannelListing(formList, channelId, clientId) {
    var url = getChannelListingUrl() + "/list/" + channelId + "/" + clientId;
    var json = JSON.stringify(formList);
    console.log(json);

    $.ajax({
        url: url,
        type: 'POST',
        data: json,
        headers: {
            'Content-Type': 'application/json'
        },
        success: function(response) {
            $('#exampleModal').modal('toggle');
            getSuccessSnackbar("Channel Listings Created.");
        },
        error: function(response) {
            alert(response.responseText);
        }
    });
}

function downloadErrors() {
    writeFileData(errorData);
}

//UI DISPLAY METHODS
function displayChannelListingList(data) {
    var $tbody = $('#channelListing-table').find('tbody');
    $tbody.empty();

    if (data.length == 0) {
        var row = '<tr>' +
            '<td style="text-align:center; font-weight: bold; background-color:#ffebe8;" colspan="5">No Channel Listings Created</td>' +
            '</tr>';
        $tbody.append(row);
    }

    for (var i in data) {
        var e = data[i];
        var row = '<tr>' +
            '<td style="text-align:center; font-weight: bold;">' + e.id + '</td>' +
            '<td style="text-align:center; font-weight: bold;">' + e.channelName + '</td>' +
            '<td style="text-align:center;">' + e.clientName + '</td>' +
            '<td>' + e.channelSkuId + '</td>' +
            '<td>' + e.clientSkuId + '</td>' +
            '</tr>';
        $tbody.append(row);
    }
}

function updateFileName() {
    var $file = $('#channelListingFile');
    var fileName = $file.val();
    $('#channelListingFileName').html(fileName);
}

function populateDropdown(){
    populateChannelDropdown();
    populateClientDropdown();
    return false;
}

function loadChannelNames(data) {
    var $channelDropdown = $('#channelId');
    var $channelSearchDropdown = $('#channelIdSearch');
    for (var channel in data) {
        var channelId = data[channel];
        var option = '<option value=' + data[channel].id + '>' +
            data[channel].name +
            '</option>';
        if (data[channel].name != "INTERNAL") {
            $channelDropdown.append(option);
            $channelSearchDropdown.append(option);
        }
    }
}

function loadClientNames(data) {
    var clientDropdown = $('#clientId');
    var clientSearchDropdown = $('#clientIdSearch');
    for (var client in data) {
        var clientId = data[client];
        var option = '<option value=' + data[client].id + '>' +
            data[client].name +
            '</option>';
        clientDropdown.append(option);
        clientSearchDropdown.append(option)
    }
}

function populateChannelDropdown() {
    var channelUrl = getChannelNamesUrl();
    $.ajax({
        url: channelUrl,
        type: 'GET',
        success: function(data) {
            loadChannelNames(data);
        },
        error: handleAjaxError
    });
}

function populateClientDropdown() {
    var channelUrl = getClientNamesUrl();
    $.ajax({
        url: channelUrl,
        type: 'GET',
        success: function(data) {
            loadClientNames(data);
        },
        error: handleAjaxError
    });
}

function getSearchChannelListingList() {
    var json = JSON.parse(toJson($("#search-param-form")));

    json = JSON.stringify(json);

    var url = getChannelListingUrl() + '/search';

    $.ajax({
        url: url,
        type: 'POST',
        data: json,
        headers: {
            'Content-Type': 'application/json'
        },
        success: function(response) {
            displayChannelListingList(response);
        },
        error: handleAjaxError
    });
    return false;
}

function displayChannelListingList(data){
	var $tbody = $('#channelListing-table').find('tbody');
	$tbody.empty();

    if(data.length == 0){
	    var row = '<tr>'
        + '<td style="text-align:center; font-weight: bold; background-color:#ffebe8;" colspan="5">No Listing Entries</td>'
        + '</tr>';
        $tbody.append(row);
    }

	for(var i in data){
		var e = data[i];

		var row = '<tr>'
		+ '<td style="text-align:center; font-weight: bold;">' + e.id + '</td>'
		+ '<td>' + e.channelName + '</td>'
		+ '<td>' + e.clientName + '</td>'
		+ '<td>' + e.channelSkuId + '</td>'
		+ '<td>' + e.clientSkuId + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}

		document.getElementById("channelListing-table").style.visibility = "visible";
    	document.getElementById("refresh-data").disabled = false;
    	getSuccessSnackbar("Refreshed");
    	return false;
}

function init() {

    $('#refresh-data').click(getSearchChannelListingList);
    $('#channelListing-form').submit(processData);
    $('#search-param-form').submit(getSearchChannelListingList);
    $('#download-errors').click(displayProductCsvErrors);
    $('#channelListingFile').on('change', updateFileName);
    document.getElementById("refresh-data").disabled = true;
    document.getElementById("download-errors").disabled = true;
}

$(document).ready(init);
$(document).ready(populateDropdown);