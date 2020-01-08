//Returns the URL to call the APIs
function getChannelApiUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/channel";
}

//Add a channel
function addChannel(event){
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
            getSuccessSnackbar("Channel Created.");
	   },
	   error: handleAjaxError
	});
	return false;
}

//GET Method: Retrieve all Channels
function getChannelList(){
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

//Display table of Channels
function displayChannelList(data){
	var $tbody = $('#channel-table').find('tbody');
	$tbody.empty();

	if(data.length == 0){
    	    var row = '<tr>'
            + '<td style="text-align:center; font-weight: bold; background-color:#ffebe8;" colspan="3">No Channels Created</td>'
            + '</tr>';
            $tbody.append(row);
    	}

	for(var i in data){
		var e = data[i];
		var row = '<tr>'
		+ '<td style="text-align:center; font-weight: bold;">' + e.id + '</td>'
		+ '<td>' + e.name + '</td>'
		+ '<td>' + e.invoiceType + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
}

//Initialization Code
function init(){
	$('#channel-form').submit(addChannel);
	$('#refresh-data').click(getChannelList);
}

$(document).ready(init);
$(document).ready(getChannelList);

