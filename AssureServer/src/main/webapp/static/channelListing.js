function getChannelListingUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/channelListing";
}

function getChannelNamesUrl(){
    var baseUrl = $("meta[name=baseUrl]").attr("content")
    return baseUrl + "/api/channel";
}

function getClientNamesUrl(){
    var baseUrl = $("meta[name=baseUrl]").attr("content")
    return baseUrl + "/api/consumer/clients";
}

function getChannelListingList(){
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

function processData(){
	var file = $('#channelListingFile')[0].files[0];
	readFileData(file, readFileDataCallback);
	return false;
}

function readFileDataCallback(results){
	fileData = results.data;
	uploadRows();
}

function uploadRows(){
    var formList = [];
    var processCount;
	for(processCount=0; processCount<fileData.length; processCount++){
	    formList.push(fileData[processCount]);
	}

	validateCsv(formList);
}

function validateCsv(formList){
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
    	   error: function(response){
    	        errorButtonActivate(JSON.parse(response.responseText)['message']);
    	   		alert("Errors in CSV");
    	   }
    	});
    	return false;
}

function uploadChannelListing(formList, channelId, clientId){
    var url = getChannelListingUrl() + "/list/" + channelId + "/" + clientId;
	var json = JSON.stringify(formList);
    console.log(json);

	//Make ajax call
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
	   error: function(response){
	   		alert(response.responseText);
	   }
	});
}

function downloadErrors(){
	writeFileData(errorData);
}

//UI DISPLAY METHODS
function displayChannelListingList(data){
	var $tbody = $('#channelListing-table').find('tbody');
	$tbody.empty();

	if(data.length == 0){
    	    var row = '<tr>'
            + '<td style="text-align:center; font-weight: bold; background-color:#ffebe8;" colspan="5">No Channel Listings Created</td>'
            + '</tr>';
            $tbody.append(row);
    	}

	for(var i in data){
		var e = data[i];
	var row = '<tr>'
		+ '<td style="text-align:center; font-weight: bold;">' + e.id + '</td>'
		+ '<td style="text-align:center; font-weight: bold;">' + e.channelName + '</td>'
		+ '<td style="text-align:center;">' + e.clientId + '</td>'
		+ '<td>' + e.channelSkuId + '</td>'
		+ '<td>'  + e.clientSkuId + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
}

function errorButtonActivate(link){
    document.getElementById("download-errors").disabled = false;
    document.getElementById('error-file-link').href = link;
}

function resetUploadDialog(){
	//Reset file name
	var $file = $('#channelListingFile');
	$file.val('');
	$('#channelListingFileName').html("Choose File");
	//Reset various counts
	processCount = 0;
	fileData = [];
	errorData = [];
}

function updateFileName(){
	var $file = $('#channelListingFile');
	var fileName = $file.val();
	$('#channelListingFileName').html(fileName);
}

function displayUploadData(){
 	resetUploadDialog(); 	
	$('#upload-channelListing-modal').modal('toggle');
}

function loadChannelNames(data){
	var $channelDropdown = $('#channelId');
	for(var channel in data){
		var channelId = data[channel];
		var option = '<option value='+data[channel].id+'>'
		+ data[channel].name
		+ '</option>';
        $channelDropdown.append(option);
	}
}

function loadClientNames(data){
	var clientDropdown = $('#clientId');
	for(var client in data){
		var clientId = data[client];
		var option = '<option value='+data[client].id+'>'
		+ data[client].name
		+ '</option>';
        clientDropdown.append(option);
	}
}

function populateDropdown(){
    populateChannelDropdown();
    populateClientDropdown();
}

function populateChannelDropdown(){
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

function populateClientDropdown(){
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

//INITIALIZATION CODE
function init(){

	$('#refresh-data').click(getChannelListingList);
	$('#channelListing-form').submit(processData);
    $('#channelListingFile').on('change', updateFileName);
    document.getElementById("download-errors").disabled = true;
}

$(document).ready(init);
$(document).ready(getChannelListingList);
$(document).ready(populateDropdown);