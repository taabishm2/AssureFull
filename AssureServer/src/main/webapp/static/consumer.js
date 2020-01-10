//Returns the URL to call the APIs
function getConsumerApiUrl(){
	var baseUrl = $("meta[name=baseUrl]").attr("content")
	return baseUrl + "/api/consumer";
}

//Add a consumer
function addConsumer(event){
	var $form = $("#consumer-form");
	var json = toJson($form);
	var url = getConsumerApiUrl();

	$.ajax({
	   url: url,
	   type: 'POST',
	   data: json,
	   headers: {
       	'Content-Type': 'application/json'
       },
	   success: function(response) {
	   		getConsumerList();
	   		$('#exampleModal').modal('toggle');
            getSuccessSnackbar("Consumer Created.");
	   },
	   error: handleAjaxError
	});
	return false;
}

//GET Method: Retrieve all Consumers
function getConsumerList(){
	var url = getConsumerApiUrl();
	$.ajax({
	   url: url,
	   type: 'GET',
	   success: function(data) {
	   		displayConsumerList(data);
	   },
	   error: handleAjaxError
	});
}

//Display table of Consumers
function displayConsumerList(data){
	var $tbody = $('#consumer-table').find('tbody');
	$tbody.empty();

	if(data.length == 0){
    	    var row = '<tr>'
            + '<td style="text-align:center; font-weight: bold; background-color:#ffebe8;" colspan="3">No Consumers Created</td>'
            + '</tr>';
            $tbody.append(row);
    	}

	for(var i in data){
		var e = data[i];
		var row = '<tr>'
		+ '<td style="text-align:center; font-weight: bold;">' + e.id + '</td>'
		+ '<td>' + e.name + '</td>'
		+ '<td>' + e.type + '</td>'
		+ '</tr>';
        $tbody.append(row);
	}
}

//Initialization Code
function init(){
	$('#consumer-form').submit(addConsumer);
	$('#refresh-data').click(getConsumerList);
}

$(document).ready(init);
$(document).ready(getConsumerList);

