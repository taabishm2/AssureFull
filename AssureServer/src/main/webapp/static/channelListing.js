function getChannelListingUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/channelListing";
}

function getChannelNamesUrl(){
    var baseUrl = $("meta[name=baseUrl]").attr("content")
    return baseUrl + "/api/channel";
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
    console.log("Processing Data");
    var $form = $("#channelListing-form");
	var channelJson = toJson($form);
	console.log("CHANNEL-JSON: ",channelJson);

	var obj = JSON.parse(channelJson);
	channelId = obj["channelId"];

	var file = $('#channelListingFile')[0].files[0];
	console.log("FIRST FILE: ",file);
	readFileData(file, readFileDataCallback);
}

function readFileDataCallback(results){
	fileData = results.data;
	console.log("FILEDATA: ",fileData);
	uploadRows();
}

function uploadRows(){
	var url = getChannelListingUrl()+"/list/"+channelId;

    console.log("URL: ",url)
    var formList = [];
    var processCount;
	for(processCount=0; processCount<fileData.length; processCount++){
	    console.log("FILE ROW: ",fileData[processCount]);
	    formList.push(fileData[processCount]);
	}

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
	        getChannelListingList();
	   		getSuccessSnackbar("Channel Listings Created.");
	   },
	   error: function(response){
	   		//row.error=response.responseText
	   		//errorData.push(row);
	   		//uploadRows();
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
		+ '<td style="text-align:center; font-weight: bold;">' + e.channelId + '</td>'
		+ '<td>' + e.clientId + '</td>'
		+ '<td>' + e.channelSkuId + '</td>'
		+ '<td>'  + e.clientSkuId + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
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

function populateDropdown(){
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

//INITIALIZATION CODE
function init(){

	$('#refresh-data').click(getChannelListingList);
	$('#upload-data').click(displayUploadData);
	$('#add-channelListing').click(processData);
	$('#download-errors').click(downloadErrors);
    $('#channelListingFile').on('change', updateFileName)

}

$(document).ready(init);
$(document).ready(getChannelListingList);
$(document).ready(populateDropdown);