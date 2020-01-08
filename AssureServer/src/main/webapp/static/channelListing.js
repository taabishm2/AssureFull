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

//Add a binSku
function getChannelListingList(event){
	var $form = $("#channelListing-form");
	var json = toJson($form);
	var url = getChannelListingUrl();

	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	   		getBinSkuList();
	   		getSuccessSnackbar("Channel Product Listing Added.");
	   },
	   error: handleAjaxError
	});
	return false;
}


//UI DISPLAY METHODS
function displayChannelListingList(data){
	var $tbody = $('#channelListing-table').find('tbody');
	$tbody.empty();

	if(data.length == 0){
    	    var row = '<tr>'
            + '<td style="text-align:center; font-weight: bold; background-color:#ffebe8;" colspan="4">No Channel Listings Created</td>'
            + '</tr>';
            $tbody.append(row);
    	}

	for(var i in data){
		var e = data[i];
		var row = '<tr>'
		+ '<td style="text-align:center; font-weight: bold;">' + e.id + '</td>'
		+ '<td style="text-align:center; font-weight: bold;">' + e.channelId + '</td>'
		+ '<td>' + e.globalSkuId + '</td>'
		+ '<td>' + e.channelSkuId + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
}


function loadChannelNames(data){
	var $clientDropdown = $('#channelId');
	for(var channel in data){
		var channelId = data[channel];
		var option = '<option value='+data[channel].id+'>'
		+ data[channel].name
		+ '</option>';
        $clientDropdown.append(option);
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
    $('#channelListing-form').submit(addChannelListing);
	$('#refresh-data').click(getChannelListingList);
}

$(document).ready(init);
$(document).ready(getChannelListingList);
$(document).ready(populateDropdown);
